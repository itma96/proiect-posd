package posd.proiect;

import java.util.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "roles")
@Path("/roles")
public class RolesResource
{
    protected static Map<Role, List<Permission>> roles = new HashMap<>();

    @GET
    @Produces("application/json")
    public Map<Role, List<Permission>> getRoles() {
        return roles;
    }

    static
    {
        roles.put(Role.ADMIN, Arrays.asList(new Permission[] {Permission.VIEW, Permission.CREATE, Permission.DELETE, Permission.UPLOAD, Permission.DOWNLOAD}));
        roles.put(Role.READWRITE, Arrays.asList(new Permission[] {Permission.VIEW, Permission.CREATE, Permission.UPLOAD, Permission.DOWNLOAD}));
        roles.put(Role.READONLY, Arrays.asList(new Permission[] {Permission.VIEW, Permission.DOWNLOAD}));
    }
}
