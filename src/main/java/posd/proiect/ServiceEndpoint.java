package posd.proiect;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

@Component
@Path("/service")
public class ServiceEndpoint {

    @Autowired
    private GoogleStorageService storageService;

    @GET
    @Path("/test")
    public String test() {
        return storageService.test();
    }

    @GET
    @Path("/buckets/all")
    @Produces("application/json")
    public Response listBuckets() {
        List<Bucket> bucketList = storageService.getAllBuckets();
        if (bucketList == null) {
            return Response.status(404).build();
        }
        return Response
                .status(200)
                .entity(bucketList)
                .build();
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
    public Response createBucket(@QueryParam("name") String name) {
        Bucket newBucket = storageService.createBucket(name);
        if (newBucket == null) {
            return Response.status(404).build();
        }
        return Response
                .status(200)
                .entity(newBucket)
                .build();
    }

    @GET
    @Path("/buckets/delete")
    @Produces("application/json")
    public Response deleteBucket(@QueryParam("name") String name) {
        storageService.deleteBucket(name);

        return Response.status(200).build();
    }

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@QueryParam("bucket") String bucketName, @QueryParam("file") String fileName) {
        byte[] data = storageService.downloadFile(bucketName, fileName);
        if (data == null) {
            return Response.status(404).build();
        }
        return Response.ok(data, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadImage(
            @QueryParam("bucket") String bucketName,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {
        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(uploadedInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bytes == null) {
            return Response.status(404).build();
        }
        Blob blob = storageService.uploadFile(bucketName, fileDetails.getFileName(), bytes);
        if (blob == null) {
            return Response.status(404).build();
        }

        return Response
                .status(200)
                .entity(blob)
                .build();
    }
}
