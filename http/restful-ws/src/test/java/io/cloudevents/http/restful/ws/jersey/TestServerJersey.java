/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.http.restful.ws.jersey;

import com.github.hanleyt.JerseyExtension;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.http.restful.ws.CloudEventsProvider;
import io.cloudevents.http.restful.ws.TestResource;
import io.cloudevents.mock.CSVFormat;
import io.cloudevents.test.Data;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class TestServerJersey {

    @BeforeAll
    public static void beforeAll() {
        EventFormatProvider.getInstance().registerFormat(CSVFormat.INSTANCE);
    }

    @RegisterExtension
    JerseyExtension jerseyExtension = new JerseyExtension(this::configureJersey, this::configureJerseyClient);

    private Application configureJersey() {
        return new ResourceConfig(TestResource.class)
            .register(CloudEventsProvider.class);
    }


    private ClientConfig configureJerseyClient(ExtensionContext extensionContext, ClientConfig clientConfig) {
        clientConfig.register(CloudEventsProvider.class);
        return clientConfig;
    }

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
