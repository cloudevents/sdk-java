
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

package io.cloudevents.http.restful.ws.resteasy;

import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.http.restful.ws.CloudEventsProvider;
import io.cloudevents.http.restful.ws.TestResource;
import io.cloudevents.mock.CSVFormat;
import io.cloudevents.test.Data;
import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class TestResteasy {

    private static VertxResteasyDeployment resteasyDeployment;
    private static WebTarget target;

    @BeforeAll
    public static void beforeClass() throws Exception {
        EventFormatProvider.getInstance().registerFormat(CSVFormat.INSTANCE);

        String base = TestPortProvider.generateBaseUrl();
        TestResteasy.resteasyDeployment = VertxContainer.start(base);
        TestResteasy.resteasyDeployment.getProviderFactory().register(CloudEventsProvider.class);
        TestResteasy.resteasyDeployment.getRegistry().addPerRequestResource(TestResource.class);

        TestResteasy.target = ClientBuilder.newClient().register(CloudEventsProvider.class).target(base);
    }

    @AfterAll
    public static void after() throws Exception {
        TestResteasy.resteasyDeployment.stop();
    }

    @Test
    void getMinEvent() {
        Response res = target.path("getMinEvent").request().buildGet().invoke();

        CloudEvent outEvent = res.readEntity(CloudEvent.class);
        assertThat(outEvent)
            .isEqualTo(Data.V1_MIN);
    }

    @Test
    @Disabled("This test doesn't work on Resteasy")
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
            .buildPost(Entity.entity(Data.V1_MIN, MediaType.WILDCARD))
            .invoke();

        assertThat(res.getStatus())
            .isEqualTo(200);
    }

    @Test
    void postEvent() {
        Response res = target
            .path("postEvent")
            .request()
            .buildPost(Entity.entity(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING, MediaType.WILDCARD))
            .invoke();

        assertThat(res.getStatus())
            .isEqualTo(200);
    }


}
