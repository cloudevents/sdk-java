package io.cloudevents.http.restful.ws;

import io.cloudevents.CloudEvent;
import io.cloudevents.mock.CSVFormat;
import io.cloudevents.test.Data;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseTest {

    @Test
    void getMinEvent(WebTarget target) {
        Response res = target.path("getMinEvent").request().buildGet().invoke();

        assertThat(res.getHeaderString("ce-specversion"))
            .isEqualTo("1.0");

        CloudEvent outEvent = res.readEntity(CloudEvent.class);
        assertThat(outEvent)
            .isEqualTo(Data.V1_MIN);
    }

    @Test
    void getStructuredEvent(WebTarget target) {
        Response res = target.path("getStructuredEvent").request().buildGet().invoke();

        CloudEvent outEvent = res.readEntity(CloudEvent.class);
        assertThat(outEvent)
            .isEqualTo(Data.V1_MIN);
        assertThat(res.getHeaderString(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(CSVFormat.INSTANCE.serializedContentType());
    }

    @Test
    void getEvent(WebTarget target) {
        Response res = target.path("getEvent").request().buildGet().invoke();

        CloudEvent outEvent = res.readEntity(CloudEvent.class);
        assertThat(outEvent)
            .isEqualTo(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING);
    }

    @Test
    void postEventWithoutBody(WebTarget target) {
        Response res = target
            .path("postEventWithoutBody")
            .request()
            .buildPost(Entity.entity(Data.V1_MIN, CloudEventsProvider.CLOUDEVENT_TYPE))
            .invoke();

        assertThat(res.getStatus())
            .isEqualTo(200);
    }

    @Test
    void postEventStructured(WebTarget target) {
        Response res = target
            .path("postEventWithoutBody")
            .request()
            .buildPost(Entity.entity(Data.V1_MIN, "application/cloudevents+csv"))
            .invoke();

        assertThat(res.getStatus())
            .isEqualTo(200);
    }

    @Test
    void postEvent(WebTarget target) {
        Response res = target
            .path("postEvent")
            .request()
            .buildPost(Entity.entity(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING, CloudEventsProvider.CLOUDEVENT_TYPE))
            .invoke();

        assertThat(res.getStatus())
            .isEqualTo(200);
    }
}
