package io.cloudevents.http.restful.ws.jakarta.microprofile;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.test.Data;
import io.cloudevents.http.restful.ws.CloudEventsProvider;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ArquillianExtension.class)
public class TestMicroprofile {

    private static final String WARNAME = "microprofile-test.war";
    private Client client = ClientBuilder.newClient();

    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        System.out.println(WARNAME);
        WebArchive archive = ShrinkWrap.create(WebArchive.class, WARNAME).addPackages(true,"io.cloudevents");
        return archive;
    }

    @ArquillianResource
    private URL baseURL;

    private WebTarget webTarget;

    public WebTarget getWebTarget() {
        if(webTarget == null){
            webTarget = client.target(baseURL.toString());
            webTarget.register(CloudEventsProvider.class);
        }
        return webTarget;
    }

    @Test
    @RunAsClient
    public void getMinEvent() {
        Response res = getWebTarget().path("getMinEvent").request().buildGet().invoke();

        assertThat(res.getHeaderString("ce-specversion")).isEqualTo("1.0");
        assertThat(res.readEntity(CloudEvent.class)).isEqualTo(Data.V1_MIN);

        res.close();
    }

    @Test
    @RunAsClient
    public void getStructuredEvent() {
        Response res = getWebTarget().path("getStructuredEvent").request().buildGet().invoke();

        assertThat(res.readEntity(CloudEvent.class)).isEqualTo(Data.V1_MIN);
        assertThat(res.getHeaderString(HttpHeaders.CONTENT_TYPE)).isEqualTo(CSVFormat.INSTANCE.serializedContentType());

        res.close();
    }

    @Test
    @RunAsClient
    public void testGetEvent() throws Exception {
        Response response = getWebTarget().path("getEvent").request().buildGet().invoke();

        assertThat(response.getStatus()).as("Valid response code").isEqualTo(200);
        assertThat(response.readEntity(CloudEvent.class)).as("should match").isEqualTo(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING);

        response.close();
    }

    @Test
    @RunAsClient
    public void postEventWithoutBody() {
        Response res = getWebTarget()
            .path("postEventWithoutBody")
            .request()
            .buildPost(Entity.entity(Data.V1_MIN, CloudEventsProvider.CLOUDEVENT_TYPE))
            .invoke();

        assertThat(res.getStatus()).isEqualTo(200);
    }

    @Test
    @RunAsClient
    public void postEventStructured() {
        Response res = getWebTarget()
            .path("postEventWithoutBody")
            .request()
            .buildPost(Entity.entity(Data.V1_MIN, "application/cloudevents+csv"))
            .invoke();

        assertThat(res.getStatus()).isEqualTo(200);
    }

    @Test
    @RunAsClient
    public void postEvent() {
        Response res = getWebTarget()
            .path("postEvent")
            .request()
            .buildPost(Entity.entity(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING, CloudEventsProvider.CLOUDEVENT_TYPE))
            .invoke();

        assertThat(res.getStatus()).isEqualTo(200);
    }
}
