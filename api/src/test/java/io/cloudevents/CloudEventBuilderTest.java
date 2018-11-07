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

import org.junit.Test;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventBuilderTest {

    @Test
    public void testBuilderWithData() {

        // given
        final Map<String, String> keyValueStore = Map.of("key1", "value1", "key2", "val2");
        final String eventId = UUID.randomUUID().toString();
        final URI src = URI.create("/trigger");
        final String eventType = "My.Cloud.Event.Type";
        final String eventTypeVersion = "2.0";
        final ZonedDateTime eventTime = ZonedDateTime.now();
        final String contentType = "application/json";
        final URI schemaUri = URI.create("http://cloudevents.io/schema");
        final Map<String, String> extensionData = Map.of("foo", "bar");

        // when
        final CloudEvent<Map<String, String>> simpleKeyValueEvent = new CloudEventBuilder()
                .data(keyValueStore)
                .contentType(contentType)
                .eventType(eventType)
                .schemaURL(schemaUri)
                .eventTypeVersion(eventTypeVersion)
                .eventTime(eventTime)
                .extensions(extensionData)
                .eventID(eventId)
                .source(src)
                .build();

        // than
        simpleKeyValueEvent.getData().ifPresent(data -> {
            assertThat(data).isNotNull();
            assertThat(data).containsKeys("key1", "key2");
            assertThat(data).containsValues("value1", "val2");
        });

        assertThat(simpleKeyValueEvent.getContentType().get()).isEqualTo(contentType);
        assertThat(simpleKeyValueEvent.getEventTime().get()).isEqualTo(eventTime);
        assertThat(simpleKeyValueEvent.getEventID()).isEqualTo(eventId);
        assertThat(simpleKeyValueEvent.getSchemaURL().get()).isEqualTo(schemaUri);
        assertThat(simpleKeyValueEvent.getEventType()).isEqualTo(eventType);
        assertThat(simpleKeyValueEvent.getSource()).isEqualTo(src);
        assertThat(simpleKeyValueEvent.getExtensions().get()).isEqualTo(extensionData);
        assertThat(simpleKeyValueEvent.getCloudEventsVersion()).isEqualTo("0.1");
        assertThat(simpleKeyValueEvent.getEventTypeVersion().get()).isEqualTo("2.0");
    }

    @Test
    public void testBuilderWithoutData() {

        // given
        final String eventId = UUID.randomUUID().toString();
        final URI src = URI.create("/trigger");
        final String eventType = "My.Cloud.Event.Type";

        // when
        final CloudEvent<Map<String, String>> simpleKeyValueEvent = new CloudEventBuilder()
                .eventType(eventType)
                .eventID(eventId)
                .source(src)
                .build();
        // than
        assertThat(simpleKeyValueEvent.getData().isPresent()).isFalse();
        assertThat(simpleKeyValueEvent.getEventTime().isPresent()).isFalse();
        assertThat(simpleKeyValueEvent.getEventID()).isEqualTo(eventId);
        assertThat(simpleKeyValueEvent.getEventType()).isEqualTo(eventType);
        assertThat(simpleKeyValueEvent.getSource()).isEqualTo(src);
        assertThat(simpleKeyValueEvent.getCloudEventsVersion()).isEqualTo("0.1");
    }
}
