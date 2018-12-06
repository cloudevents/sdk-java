/**
 * Copyright 2018 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents;

import io.cloudevents.extensions.DistributedTracingExtension;
import org.junit.Test;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventBuilderTest {

    @Test
    public void testBuilderWithData() {

        // given
        final Map<String, String> keyValueStore = new HashMap<>();
        keyValueStore.put("key1", "value1");
        keyValueStore.put("key2", "value2");
        final String id = UUID.randomUUID().toString();
        final URI src = URI.create("/trigger");
        final String type = "My.Cloud.Event.Type";
        final ZonedDateTime eventTime = ZonedDateTime.now();
        final String contentType = "application/json";
        final URI schemaUri = URI.create("http://cloudevents.io/schema");

        // when
        final CloudEvent<Map<String, String>> simpleKeyValueEvent = new CloudEventBuilder()
                .data(keyValueStore)
                .contentType(contentType)
                .type(type)
                .schemaURL(schemaUri)
                .time(eventTime)
                .id(id)
                .source(src)
                .build();

        // than
        simpleKeyValueEvent.getData().ifPresent(data -> {
            assertThat(data).isNotNull();
            assertThat(data).containsKeys("key1", "key2");
            assertThat(data).containsValues("value1", "value2");
        });

        assertThat(simpleKeyValueEvent.getContentType().get()).isEqualTo(contentType);
        assertThat(simpleKeyValueEvent.getTime().get()).isEqualTo(eventTime);
        assertThat(simpleKeyValueEvent.getId()).isEqualTo(id);
        assertThat(simpleKeyValueEvent.getSchemaURL().get()).isEqualTo(schemaUri);
        assertThat(simpleKeyValueEvent.getType()).isEqualTo(type);
        assertThat(simpleKeyValueEvent.getSource()).isEqualTo(src);
        assertThat(simpleKeyValueEvent.getSepcVersion()).isEqualTo(SpecVersion.DEFAULT.toString());
    }

    @Test
    public void testBuilderWithoutData() {

        // given
        final String id = UUID.randomUUID().toString();
        final URI src = URI.create("/trigger");
        final String type = "My.Cloud.Event.Type";

        // when
        final CloudEvent<Map<String, String>> simpleKeyValueEvent = new CloudEventBuilder()
                .type(type)
                .id(id)
                .source(src)
                .build();
        // than
        assertThat(simpleKeyValueEvent.getData().isPresent()).isFalse();
        assertThat(simpleKeyValueEvent.getTime().isPresent()).isFalse();
        assertThat(simpleKeyValueEvent.getId()).isEqualTo(id);
        assertThat(simpleKeyValueEvent.getType()).isEqualTo(type);
        assertThat(simpleKeyValueEvent.getSource()).isEqualTo(src);
        assertThat(simpleKeyValueEvent.getSepcVersion()).isEqualTo(SpecVersion.DEFAULT.toString());
    }

    @Test
    public void testBuilderWithoutDataAndUrn() {

        // given
        final String id = UUID.randomUUID().toString();
        final URI src = URI.create("urn:event:from:myapi/resourse/123");
        final String type = "some.Cloud.Event.Type";

        // when
        final CloudEvent<Map<String, String>> simpleKeyValueEvent = new CloudEventBuilder()
                .type(type)
                .id(id)
                .source(src)
                .build();
        // than
        assertThat(simpleKeyValueEvent.getSource()).isEqualTo(src);
    }

    @Test
    public void test01BuilderWithoutDataAndUrn() {

        // given
        final String id = UUID.randomUUID().toString();
        final URI src = URI.create("urn:event:from:myapi/resourse/123");
        final String type = "some.Cloud.Event.Type";

        // when
        final CloudEvent<Map<String, String>> simpleKeyValueEvent = new CloudEventBuilder()
                .specVersion("0.1")
                .type(type)
                .id(id)
                .source(src)
                .build();
        // than
        assertThat(simpleKeyValueEvent.getSource()).isEqualTo(src);
        assertThat(simpleKeyValueEvent.getSepcVersion()).isEqualTo(SpecVersion.V_01.toString());

    }

    @Test
    public void testBuilderWithoutDataAndURISchema() {

        // given
        final String id = UUID.randomUUID().toString();
        final URI src = URI.create("urn:event:from:myapi/resourse/123");
        final String type = "some.Cloud.Event.Type";
        final URI schema = URI.create("urn:oasis:names:specification:docbook:dtd:xml:4.1.2");

        // when
        final CloudEvent<Map<String, String>> simpleKeyValueEvent = new CloudEventBuilder()
                .type(type)
                .id(id)
                .source(src)
                .schemaURL(schema)
                .build();
        // than
        assertThat(simpleKeyValueEvent.getSchemaURL().get()).isEqualTo(schema);
    }

    @Test
    public void testBuilderWithoutDataAndMailto() {

        // given
        final String id = UUID.randomUUID().toString();
        final URI src = URI.create("mailto:cncf-wg-serverless@lists.cncf.io");
        final String type = "My.Cloud.Event.Type";

        // when
        final CloudEvent<Map<String, String>> simpleKeyValueEvent = new CloudEventBuilder()
                .type(type)
                .id(id)
                .source(src)
                .build();
        // than
        assertThat(simpleKeyValueEvent.getSource()).isEqualTo(src);
    }

    @Test
    public void testBuilderWithoutDataAndDistributedTracingExtension() {

        // given
        final String id = UUID.randomUUID().toString();
        final URI src = URI.create("mailto:cncf-wg-serverless@lists.cncf.io");
        final String type = "My.Cloud.Event.Type";
        final DistributedTracingExtension dte = new DistributedTracingExtension();
        dte.setTraceparent("00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01");
        dte.setTracestate("congo=BleGNlZWRzIHRohbCBwbGVhc3VyZS4");

        // when
        final CloudEvent<Map<String, String>> simpleKeyValueEvent = new CloudEventBuilder()
                .type(type)
                .id(id)
                .source(src)
                .extension(dte)
                .build();
        // than
        assertThat(simpleKeyValueEvent.getSource()).isEqualTo(src);
        assertThat(simpleKeyValueEvent.getExtensions().get()).contains(dte);

        Extension receivedDte = simpleKeyValueEvent.getExtensions().get().get(0);
        assertThat(receivedDte).extracting("traceparent", "tracestate")
                .contains("00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01", "congo=BleGNlZWRzIHRohbCBwbGVhc3VyZS4");
    }

}
