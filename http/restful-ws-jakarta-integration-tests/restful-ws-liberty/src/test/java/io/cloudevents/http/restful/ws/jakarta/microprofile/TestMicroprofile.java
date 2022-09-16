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
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;


/**
 * Arquilian does not support assertj, so test cases have been ported to Junit to work with arquilian
 */
@RunWith(Arquillian.class)
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

        Assert.assertEquals("1.0",res.getHeaderString("ce-specversion"));
        Assert.assertEquals(Data.V1_MIN,res.readEntity(CloudEvent.class));

        res.close();
    }

    @Test
    @RunAsClient
    public void getStructuredEvent() {
        Response res = getWebTarget().path("getStructuredEvent").request().buildGet().invoke();

        Assert.assertEquals(Data.V1_MIN,res.readEntity(CloudEvent.class));
        Assert.assertEquals(CSVFormat.INSTANCE.serializedContentType(),res.getHeaderString(HttpHeaders.CONTENT_TYPE));

        res.close();
    }

    @Test
    @RunAsClient
    public void testGetEvent() throws Exception {
        Response response = getWebTarget().path("getEvent").request().buildGet().invoke();

        Assert.assertEquals("Valid response code", 200, response.getStatus());
        Assert.assertEquals("should match", Data.V1_WITH_JSON_DATA_WITH_EXT_STRING, response.readEntity(CloudEvent.class));

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

        Assert.assertEquals(200,res.getStatus());
    }

    @Test
    @RunAsClient
    public void postEventStructured() {
        Response res = getWebTarget()
            .path("postEventWithoutBody")
            .request()
            .buildPost(Entity.entity(Data.V1_MIN, "application/cloudevents+csv"))
            .invoke();

        Assert.assertEquals(200,res.getStatus());
    }

    @Test
    @RunAsClient
    public void postEvent() {
        Response res = getWebTarget()
            .path("postEvent")
            .request()
            .buildPost(Entity.entity(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING, CloudEventsProvider.CLOUDEVENT_TYPE))
            .invoke();

        Assert.assertEquals(200,res.getStatus());
    }
}
