package posd.proiect;

import org.springframework.stereotype.Component;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

@Component
public class JerseyConfig extends ResourceConfig
{
    public JerseyConfig()
    {
        register(MultiPartFeature.class);
        register(UsersResource.class);
        register(RolesResource.class);
        register(ServiceEndpoint.class);
    }
}
