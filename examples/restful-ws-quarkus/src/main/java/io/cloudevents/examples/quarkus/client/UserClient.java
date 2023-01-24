package io.cloudevents.examples.quarkus.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/users")
@RegisterRestClient
public interface UserClient {

    // This will emit binary encoded events.
    // To use structured JSON encoding use @Consumes(JsonFormat.CONTENT_TYPE).
    @POST
    void emitBinary(CloudEvent event);

    @POST
    @Consumes(JsonFormat.CONTENT_TYPE)
    void emitStructured(CloudEvent event);
}
