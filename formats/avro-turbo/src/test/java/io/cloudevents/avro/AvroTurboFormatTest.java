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
package io.cloudevents.avro;

import io.cloudevents.CloudEvent;
import io.cloudevents.avroturbo.AvroTurboFormat;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AvroTurboFormatTest {

    private final EventFormat format = EventFormatProvider.getInstance().resolveFormat(AvroTurboFormat.AVRO_TURBO_CONTENT_TYPE);

    // TODO - add test cases for
    // - null data
    // - non-bytes data
    // - extension that is bytes
    // - invalid extension type
    @Test
    void format() {
        assertNotNull(format);
        assertEquals(Collections.singleton("application/cloudevents+avroturbo"), format.deserializableContentTypes());

        CloudEvent event = CloudEventBuilder.v1()
                // mandatory
                .withId("")
                .withSource(URI.create(""))
                .withType("")
                // optional
                .withTime(Instant.EPOCH.atOffset(ZoneOffset.UTC))
                .withSubject("")
                .withDataSchema(URI.create(""))
                // extension
                // support boolean, int, string, bytes
                .withExtension("boolean", false)
                .withExtension("int", 0)
                .withExtension("string", "")
                // omitting bytes, because it is not supported be CloudEvent.equals
                .withData("", BytesCloudEventData.wrap(new byte[0]))
                .build();

        byte[] serialized = format.serialize(event);

        assertNotNull(serialized);

        CloudEvent deserialized = format.deserialize(serialized);

        assertEquals(event, deserialized);

    }
}
