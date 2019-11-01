package posd.proiect;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.List;

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
    @Path("/buckets")
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

    @GET
    @Path("/bucket")
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

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@QueryParam("bucket") String bucketName, @QueryParam("file") String fileName) {
        byte[] data = storageService.getFile(bucketName, fileName);
        if (data == null) {
            return Response.status(404).build();
        }
        return Response.ok(data, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .build();
    }
}
