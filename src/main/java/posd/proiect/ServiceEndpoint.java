package posd.proiect;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public List<Bucket> listBuckets() {
        return storageService.getAllBuckets();
    }
}
