package io.cloudevents.examples.quarkus.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.examples.quarkus.model.User;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @Inject
    ObjectMapper mapper;

    @Context
    UriInfo uriInfo;

    private Map<String, User> users = new HashMap<>();

    @GET
    @Path("/{username}")
    public User get(@PathParam("username") String username) {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        throw new NotFoundException();
    }

    @GET
    public Map<String, User> list() {
        return users;
    }

    @POST
    public Response create(CloudEvent event) {
        if (event == null || event.getData() == null) {
            throw new BadRequestException("Invalid data received. Null or empty event");
        }
        User user = PojoCloudEventDataMapper
            .from(mapper, User.class)
            .map(event.getData())
            .getValue();
        if (users.containsKey(user.getUsername())) {
            throw new BadRequestException("Username already exists: " + user.getUsername());
        }
        LOGGER.info("Received User: {}", user);
        users.put(user.getUsername(), user);
        return Response
            .created(uriInfo.getAbsolutePathBuilder().build(event.getId()))
            .build();
    }
}
