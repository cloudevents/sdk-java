package io.cloudevents.examples.microprofile;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@RequestScoped
@Path("events")
public class CloudEventsJaxrsService {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CloudEvent retrieveEvent(){
        System.out.println("Received request for an event");
        return CloudEventBuilder.v1()
                .withData("{\"message\":\"Welcome to this Cloudevents + Microprofile example\"}".getBytes(StandardCharsets.UTF_8))
                .withDataContentType("application/json")
                .withId("hello")
                .withType("example.http")
                .withSource(URI.create("http://localhost"))
                .build();
    }

    //Example Curl - curl -v http://localhost:9080/cloudevents-restful-ws-microprofile-example/events -H "Ce-Specversion: 1.0" -H "Ce-Type: User" -H "Ce-Source: io.cloudevents.examples/user" -H "Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f78" -H "Content-Type: application/json" -H "Ce-Subject: SUBJ-0001" -d "hello"

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postEvent(CloudEvent event){
        System.out.println("Received request providing a event with body "+ new String(event.getData().toBytes(), StandardCharsets.UTF_8));
        System.out.println(event);
        return Response.noContent().build();
    }

    //Example Curl - curl -v http://localhost:9080/cloudevents-restful-ws-microprofile-example/events/echo -H "Ce-Specversion: 1.0" -H "Ce-Type: User" -H "Ce-Source: io.cloudevents.examples/user" -H "Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f78" -H "Content-Type: application/json" -H "Ce-Subject: SUBJ-0001" -d "hello"

    @POST
    @Path("/echo")
    @Consumes(MediaType.APPLICATION_JSON)
    public CloudEvent echo(CloudEvent event){
        return CloudEventBuilder.v1()
                .withData("application/json", ("{\"echo\": \"" + new String(event.getData().toBytes(),StandardCharsets.UTF_8) + "\"}").getBytes(StandardCharsets.UTF_8))
                .withId("echo")
                .withType("echo.http")
                .withSource(URI.create("http://localhost"))
                .build();
    }


}
