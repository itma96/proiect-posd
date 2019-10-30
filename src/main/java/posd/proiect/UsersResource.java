package posd.proiect;

import java.util.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "users")
@Path("/users")
public class UsersResource
{
    protected static Map<Integer, User> users = new HashMap<>();

    @GET
    @Produces("application/json")
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>(UsersResource.users.values());
        return users;
    }

    static
    {
        User user1 = new User();
        user1.setId(1);
        user1.setFirstName("John");
        user1.setLastName("Wick");
        user1.setPassword("1234");
        user1.setRoles(Arrays.asList(new Role[] {Role.ADMIN}));

        User user2 = new User();
        user2.setId(2);
        user2.setFirstName("Harry");
        user2.setLastName("Potter");
        user2.setPassword("1234");
        user2.setRoles(Arrays.asList(new Role[] {Role.READONLY}));


        users.put(user1.getId(), user1);
        users.put(user2.getId(), user2);
    }
}
