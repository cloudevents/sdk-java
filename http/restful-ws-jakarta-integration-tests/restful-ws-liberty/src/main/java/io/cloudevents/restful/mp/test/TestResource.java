package io.cloudevents.restful.mp.test;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.test.Data;
import io.cloudevents.http.restful.ws.StructuredEncoding;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/")
public class TestResource{

    @PostConstruct
    void init() {
        EventFormatProvider.getInstance().registerFormat(CSVFormat.INSTANCE);
    }

    @GET
    @Path("getMinEvent")
    public CloudEvent getMinEvent() {
        return Data.V1_MIN;
    }

    @GET
    @Path("getStructuredEvent")
    @StructuredEncoding("application/cloudevents+csv")
    public CloudEvent getStructuredEvent() {
        return Data.V1_MIN;
    }

    @GET
    @Path("getEvent")
    public CloudEvent getEvent() {
        return Data.V1_WITH_JSON_DATA_WITH_EXT_STRING;
    }

    @POST
    @Path("postEventWithoutBody")
    public Response postEventWithoutBody(CloudEvent inputEvent) {
        if (inputEvent.equals(Data.V1_MIN)) {
            return Response.ok().build();
        }
        return Response.serverError().build();
    }

    @POST
    @Path("postEvent")
    public Response postEvent(CloudEvent inputEvent) {
        if (inputEvent.equals(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING)) {
            return Response.ok().build();
        }
        return Response.serverError().build();
    }
}
