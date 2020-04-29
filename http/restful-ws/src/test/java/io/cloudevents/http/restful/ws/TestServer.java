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

package io.cloudevents.http.restful.ws;

import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.mock.CSVFormat;
import io.cloudevents.test.Data;
import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

public class TestServer {

    private VertxResteasyDeployment resteasyDeployment;
    private WebTarget target;

    @BeforeEach
    public void before() throws Exception {
        EventFormatProvider.getInstance().registerFormat(CSVFormat.INSTANCE);
        this.resteasyDeployment = VertxContainer.start();
        this.resteasyDeployment.getProviderFactory().register(CloudEventsProvider.class);
        this.resteasyDeployment.getRegistry().addPerRequestResource(TestResource.class);

        this.target = ClientBuilder
            .newClient()
            .register(CloudEventsProvider.class)
            .target(generateURL("/"));
    }

    @AfterEach
    public void after() throws Exception {
        this.resteasyDeployment.stop();
    }

    @Test
    void getMinEvent() {
        Response res = target.path("getMinEvent").request().buildGet().invoke();

        assertThat(res.getHeaderString("ce-specversion"))
            .isEqualTo("1.0");

        CloudEvent outEvent = res.readEntity(CloudEvent.class);
        assertThat(outEvent)
            .isEqualTo(Data.V1_MIN);
    }

    @Test
    void getStructuredEvent() {
        Response res = target.path("getStructuredEvent").request().buildGet().invoke();

        CloudEvent outEvent = res.readEntity(CloudEvent.class);
        assertThat(outEvent)
            .isEqualTo(Data.V1_MIN);
        assertThat(res.getMediaType().getType())
            .isEqualTo(CSVFormat.INSTANCE.serializedContentType());
    }

    @Test
    void getEvent() {
        Response res = target.path("getEvent").request().buildGet().invoke();

        CloudEvent outEvent = res.readEntity(CloudEvent.class);
        assertThat(outEvent)
            .isEqualTo(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING);
    }

    @Test
    void postEventWithoutBody() {
        Response res = target
            .path("postEventWithoutBody")
            .request()
            .buildPost(Entity.entity(Data.V1_MIN, "application/cloudevents"))
            .invoke();

        assertThat(res.getStatus())
            .isEqualTo(200);
    }

    @Test
    void postEvent() {
        Response res = target
            .path("postEvent")
            .request()
            .buildPost(Entity.entity(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING, "application/cloudevents"))
            .invoke();

        assertThat(res.getStatus())
            .isEqualTo(200);
    }


}
