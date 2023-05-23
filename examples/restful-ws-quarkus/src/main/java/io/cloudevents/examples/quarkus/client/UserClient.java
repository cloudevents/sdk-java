package io.cloudevents.examples.quarkus.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

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
