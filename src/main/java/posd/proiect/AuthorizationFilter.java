package posd.proiect;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

import java.util.*;
import java.lang.reflect.Method;

@Provider
@JWTTokenNeeded
@Priority(Priorities.AUTHENTICATION)
public class AuthorizationFilter implements ContainerRequestFilter {
    private static final String SECRET = "secret";
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED).build();
    private static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN).build();
    private static final Response SERVER_ERROR = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        User user = null;

        // Get the HTTP Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(AUTHORIZATION_PROPERTY);
        if (authorizationHeader == null) {
            requestContext.abortWith(ACCESS_DENIED);
            return;
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();

        // If no token provided, refuse request
        if (token == null) {
            requestContext.abortWith(ACCESS_DENIED);
            return;
        }

        // Validate the token
        try {
            Key key = new SecretKeySpec(SECRET.getBytes(), 0, SECRET.getBytes().length, "DES");
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            String subject = claims.getBody().getSubject();

            // Verify if claimed user exists
            for (Integer id : UsersResource.users.keySet()) {
                if (UsersResource.users.get(id).getUserName().equals(subject)) {
                    user = UsersResource.users.get(id);
                    break;
                }
            }

            if (user == null) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }

            // Verify if token expired
            Date expiration = claims.getBody().getExpiration();
            if (new Date().compareTo(expiration) > 0) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }

        } catch (JwtException e) {
            e.printStackTrace();
            requestContext.abortWith(ACCESS_DENIED);
            return;
        }

        Method method = resourceInfo.getResourceMethod();

        // Access allowed for all
        if (!method.isAnnotationPresent(PermitAll.class)) {

            // Access denied for all
            if (method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(ACCESS_FORBIDDEN);
                return;
            }

            // Verify user role
            if (method.isAnnotationPresent(Roles.class)) {
                List<Role> rolesAllowed = Arrays.asList(method.getAnnotation(Roles.class).value());

                boolean found = false;
                for (Role role : rolesAllowed) {
                    if (user.getRoles().contains(role)) {
                        found = true;
                        break;
                    }
                }

                // User does not have the appropriate role
                if (!found) {
                    requestContext.abortWith(ACCESS_FORBIDDEN);
                    return;
                }

                // Verify user permissions
                if (method.isAnnotationPresent(Permissions.class)) {
                    List<Permission> permissionsAllowed =
                            Arrays.asList(method.getAnnotation(Permissions.class).value());

                    for (Permission permission : permissionsAllowed) {
                        found = false;
                        for (Role role : user.getRoles()) {
                            if (RolesResource.roles.get(role).contains(permission)) {
                                found = true;
                                break;
                            }
                        }

                        // User does not have the appropriate permissions
                        if (!found) {
                            requestContext.abortWith(ACCESS_FORBIDDEN);
                            return;
                        }
                    }
                }
            }
        }
    }
}