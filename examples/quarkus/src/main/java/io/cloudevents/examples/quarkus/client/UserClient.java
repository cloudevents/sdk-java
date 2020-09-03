package io.cloudevents.examples.quarkus.client;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.cloudevents.CloudEvent;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/users")
@RegisterRestClient
public interface UserClient {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    void emit(CloudEvent event);
}
