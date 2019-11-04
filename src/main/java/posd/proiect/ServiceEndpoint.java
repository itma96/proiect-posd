package posd.proiect;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

@Component
@Path("/service")
public class ServiceEndpoint {

    private static final String SECRET = "secret";
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final Response NOT_FOUND = Response.status(Response.Status.NOT_FOUND).build();
    private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED).build();
    private static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN).build();
    private static final Response SERVER_ERROR = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    @Context
    private UriInfo uriInfo;

    @Autowired
    private GoogleStorageService storageService;

    @GET
    @Path("/test")
    public String test() {
        return storageService.test();
    }

    @POST
    @Path("/auth")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password) {

        // Authenticate the user using the credentials provided
        try {
            authenticate(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return ACCESS_DENIED;
        }

        // Issue a token for the user
        String token = issueToken(username);

        // Return the token on the response
        return Response.ok().header(AUTHORIZATION_PROPERTY, "Bearer " + token).build();
    }

    private void authenticate(String username, String password) throws Exception {
        boolean userFound = false;
        for (User user : UsersResource.users.values()) {
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                userFound = true;
            }
        }
        if (!userFound) {
            throw new SecurityException("Invalid user/password");
        }
    }

    private String issueToken(String login) {
        Key key = new SecretKeySpec(SECRET.getBytes(), 0, SECRET.getBytes().length, "DES");
        String jwtToken = Jwts.builder()
                .setSubject(login)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date()).setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        return jwtToken;

    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @GET
    @Path("/buckets/all")
    @Produces("application/json")
    @JWTTokenNeeded
    @Roles({Role.ADMIN, Role.READWRITE, Role.READONLY})
    @Permissions(Permission.VIEW)
    public Response listBuckets() {
        List<Bucket> bucketList = storageService.getAllBuckets();
        if (bucketList == null) {
            return NOT_FOUND;
        }
        return Response.status(200).entity(bucketList).build();
    }

    /*
        @GET
        @Path("/buckets")
        @Produces("application/json")
        public Response getBucket(@QueryParam("name") String name) {
            Bucket bucket = storageService.getBucket(name);
            if(bucket == null) {
                return Response.status(404). build();
            }
            return Response
                    .status(200)
                    .entity(bucket)
                    .build();
        }
    */
    @GET
    @Path("/buckets/new")
    @Produces("application/json")
    @JWTTokenNeeded
    @Roles({Role.ADMIN, Role.READWRITE})
    @Permissions(Permission.CREATE)
    public Response createBucket(@QueryParam("name") String name) {
        Bucket newBucket = storageService.createBucket(name);
        if (newBucket == null) {
            return SERVER_ERROR;
        }
        return Response.status(200).entity(newBucket).build();
    }

    @GET
    @Path("/buckets/delete")
    @Produces("application/json")
    @JWTTokenNeeded
    @Roles({Role.ADMIN})
    @Permissions(Permission.DELETE)
    public Response deleteBucket(@QueryParam("name") String name) {
        storageService.deleteBucket(name);
        return Response.status(200).build();
    }

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @JWTTokenNeeded
    @Roles({Role.ADMIN, Role.READWRITE, Role.READONLY})
    @Permissions(Permission.DOWNLOAD)
    public Response download(@QueryParam("bucket") String bucketName, @QueryParam("file") String fileName) {
        byte[] data = storageService.downloadFile(bucketName, fileName);
        if (data == null) {
            return NOT_FOUND;
        }
        return Response.ok(data, MediaType.APPLICATION_OCTET_STREAM_TYPE).header("Content-Disposition", "attachment; "
                + "filename=\"" + fileName + "\"").build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @JWTTokenNeeded
    @Roles({Role.ADMIN, Role.READWRITE})
    @Permissions(Permission.UPLOAD)
    public Response uploadImage(@QueryParam("bucket") String bucketName,
                                @FormDataParam("file") InputStream uploadedInputStream,
                                @FormDataParam("file") FormDataContentDisposition fileDetails) {
        byte[] bytes = null;

        try {
            bytes = IOUtils.toByteArray(uploadedInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return SERVER_ERROR;
        }

        Blob blob = storageService.uploadFile(bucketName, fileDetails.getFileName(), bytes);
        if (blob == null) {
            return SERVER_ERROR;
        }

        return Response.status(200).entity(blob).build();
    }
}
