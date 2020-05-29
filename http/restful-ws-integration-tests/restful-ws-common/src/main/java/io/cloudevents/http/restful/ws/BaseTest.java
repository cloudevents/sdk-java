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
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseTest {

    protected abstract WebTarget getWebTarget();

    @Test
    void getMinEvent() {
        Response res = getWebTarget().path("getMinEvent").request().buildGet().invoke();

        assertThat(res.getHeaderString("ce-specversion"))
            .isEqualTo("1.0");

        CloudEvent outEvent = res.readEntity(CloudEvent.class);
        assertThat(outEvent)
            .isEqualTo(Data.V1_MIN);
    }

    @Test
    void getStructuredEvent() {
        Response res = getWebTarget().path("getStructuredEvent").request().buildGet().invoke();

        CloudEvent outEvent = res.readEntity(CloudEvent.class);
        assertThat(outEvent)
            .isEqualTo(Data.V1_MIN);
        assertThat(res.getHeaderString(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(CSVFormat.INSTANCE.serializedContentType());
    }

    @Test
    void getEvent() {
        Response res = getWebTarget().path("getEvent").request().buildGet().invoke();

        CloudEvent outEvent = res.readEntity(CloudEvent.class);
        assertThat(outEvent)
            .isEqualTo(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING);
    }

    @Test
    void postEventWithoutBody() {
        Response res = getWebTarget()
            .path("postEventWithoutBody")
            .request()
            .buildPost(Entity.entity(Data.V1_MIN, CloudEventsProvider.CLOUDEVENT_TYPE))
            .invoke();

        assertThat(res.getStatus())
            .isEqualTo(200);
    }

    @Test
    void postEventStructured() {
        Response res = getWebTarget()
            .path("postEventWithoutBody")
            .request()
            .buildPost(Entity.entity(Data.V1_MIN, "application/cloudevents+csv"))
            .invoke();

        assertThat(res.getStatus())
            .isEqualTo(200);
    }

    @Test
    void postEvent() {
        Response res = getWebTarget()
            .path("postEvent")
            .request()
            .buildPost(Entity.entity(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING, CloudEventsProvider.CLOUDEVENT_TYPE))
            .invoke();

        assertThat(res.getStatus())
            .isEqualTo(200);
    }
}
