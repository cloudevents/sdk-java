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
package io.cloudevents.core.v03;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;

import static io.cloudevents.core.test.Data.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * @author fabiojose
 */
public class CloudEventBuilderTest {

    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#v03Events")
    void testCopyWithBuilder(CloudEvent event) {
        assertThat(CloudEventBuilder.v03(event).build()).isEqualTo(event);
    }

    @Test
    void testToV1() {
        CloudEvent input = CloudEventBuilder.v03()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .withExtension("astring", "aaa")
            .withExtension("aboolean", "true")
            .withExtension("anumber", "10")
            .build();

        CloudEvent expected = CloudEventBuilder.v1()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .withExtension("astring", "aaa")
            .withExtension("aboolean", "true")
            .withExtension("anumber", "10")
            .build();

        CloudEvent actual = CloudEventBuilder.v1(input).build();

        assertThat(expected.getSpecVersion())
            .isEqualTo(SpecVersion.V1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testCopyExtensions() {
        io.cloudevents.core.v03.CloudEventBuilder templateBuilder = CloudEventBuilder.v03()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .withExtension("astring", "aaa")
            .withExtension("aboolean", "true")
            .withExtension("anumber", "10");

        CloudEvent event = templateBuilder.build();
        CloudEvent cloned = new io.cloudevents.core.v03.CloudEventBuilder(event).build();

        assertThat(cloned).isEqualTo(event);
    }

    @Test
    void testNewBuilder() {
        io.cloudevents.core.v03.CloudEventBuilder templateBuilder = CloudEventBuilder.v03()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .withExtension("astring", "aaa")
            .withExtension("aboolean", "true")
            .withExtension("anumber", "10");

        CloudEvent event = templateBuilder.build();
        CloudEvent cloned = templateBuilder.newBuilder().build();

        assertThat(cloned).isEqualTo(event);
    }

    @Test
    void testDataContentEncoding() {
        CloudEvent event = CloudEventBuilder.v03()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .withExtension("astring", "aaa")
            .withExtension("aboolean", "true")
            .withExtension("anumber", "10")
            .build();

        assertThat(event.getAttribute("datacontentencoding"))
            .isNull();
    }

    @Test
    void testMissingId() {
        assertThatCode(() -> CloudEventBuilder
            .v03()
            .withSource(URI.create("http://localhost"))
            .withType("aaa")
            .build()
        ).hasMessageContaining("Attribute 'id' cannot be null");
    }

    @Test
    void testMissingSource() {
        assertThatCode(() -> CloudEventBuilder
            .v03()
            .withId("000")
            .withType("aaa")
            .build()
        ).hasMessageContaining("Attribute 'source' cannot be null");
    }

    @Test
    void testMissingType() {
        assertThatCode(() -> CloudEventBuilder
            .v03()
            .withId("000")
            .withSource(URI.create("http://localhost"))
            .build()
        ).hasMessageContaining("Attribute 'type' cannot be null");
    }

}
