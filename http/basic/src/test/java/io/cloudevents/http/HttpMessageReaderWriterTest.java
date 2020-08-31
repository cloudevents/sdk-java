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
 */

package io.cloudevents.http;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.http.HttpMessageFactory;
import io.cloudevents.http.impl.HttpMessageWriter;
import io.cloudevents.types.Time;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static io.cloudevents.core.test.Data.*;
import static org.assertj.core.api.Assertions.assertThat;

public class HttpMessageReaderWriterTest {

    @ParameterizedTest
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    void testWriteStructured(CloudEvent event) {

        final AtomicReference<byte[]> body = new AtomicReference<>();
        final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        GenericStructuredMessageReader.from(event, CSVFormat.INSTANCE).visit(
            HttpMessageFactory.createWriter(headers::put, body::set)
        );
        assertThat(headers.get("content-type"))
            .isEqualTo(CSVFormat.INSTANCE.serializedContentType());
        assertThat(body.get())
            .isEqualTo(CSVFormat.INSTANCE.serialize(event));

    }

    @ParameterizedTest
    @MethodSource("binaryTestArguments")
    void testWriteBinary(CloudEvent event, Map<String,String> expectedHeaders, byte[] expectedBody) {

        final AtomicReference<byte[]> body = new AtomicReference<>();
        final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        new HttpMessageWriter(headers::put, body::set).writeBinary(event);

        expectedHeaders.forEach((k, v) -> {
            assertThat(headers.get(k)).isEqualTo(v);
        });
        if (expectedBody != null) {
            assertThat(body.get()).isEqualTo(expectedBody);
        }
    }

    @ParameterizedTest
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void testReadStructured(CloudEvent event) {
        byte[] serializedEvent = CSVFormat.INSTANCE.serialize(event);

        Map<String,String> headers = new HashMap<String,String>() {{
            put("content-type", CSVFormat.INSTANCE.serializedContentType() + "; charset=utf8");
        }};

        MessageReader message = HttpMessageFactory.createReader(
            headers,
            serializedEvent
        );

        assertThat(message.getEncoding())
            .isEqualTo(Encoding.STRUCTURED);
        assertThat(message.toEvent())
            .isEqualTo(event);
    }

    @ParameterizedTest
    @MethodSource("binaryTestArguments")
    public void testReadBinary(CloudEvent expectedEvent, Map<String,String> headers, byte[] body) {
        MessageReader message = HttpMessageFactory.createReader(headers::forEach, body);

        assertThat(message.getEncoding())
            .isEqualTo(Encoding.BINARY);
        assertThat(message.toEvent())
            .isEqualTo(expectedEvent);
    }

    public static Stream<Arguments> binaryTestArguments() {
        return Stream.of(
            // V03
            Arguments.of(
                V03_MIN,
                createHeaders()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString()),
                null
            ),
            Arguments.of(
                V03_WITH_JSON_DATA,
                createHeaders()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ce-schemaurl", DATASCHEMA.toString())
                    .add("content-type", DATACONTENTTYPE_JSON)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME)),
                DATA_JSON_SERIALIZED
            ),
            Arguments.of(
                V03_WITH_JSON_DATA_WITH_EXT_STRING,
                createHeaders()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ce-schemaurl", DATASCHEMA.toString())
                    .add("content-type", DATACONTENTTYPE_JSON)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME))
                    .add("ce-astring", "aaa")
                    .add("ce-aboolean", "true")
                    .add("ce-anumber", "10"),
                DATA_JSON_SERIALIZED
            ),
            Arguments.of(
                V03_WITH_XML_DATA,
                createHeaders()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_XML)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME)),
                DATA_XML_SERIALIZED
            ),
            Arguments.of(
                V03_WITH_TEXT_DATA,
                createHeaders()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_TEXT)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME)),
                DATA_TEXT_SERIALIZED
            ),
            // V1
            Arguments.of(
                V1_MIN,
                createHeaders()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString()),
                null
            ),
            Arguments.of(
                V1_WITH_JSON_DATA,
                createHeaders()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ce-dataschema", DATASCHEMA.toString())
                    .add("content-type", DATACONTENTTYPE_JSON)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME)),
                DATA_JSON_SERIALIZED
            ),
            Arguments.of(
                V1_WITH_JSON_DATA_WITH_EXT_STRING,
                createHeaders()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ce-dataschema", DATASCHEMA.toString())
                    .add("content-type", DATACONTENTTYPE_JSON)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME))
                    .add("ce-astring", "aaa")
                    .add("ce-aboolean", "true")
                    .add("ce-anumber", "10"),
                DATA_JSON_SERIALIZED
            ),
            Arguments.of(
                V1_WITH_XML_DATA,
                createHeaders()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_XML)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME)),
                DATA_XML_SERIALIZED
            ),
            Arguments.of(
                V1_WITH_TEXT_DATA,
                createHeaders()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_TEXT)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME)),
                DATA_TEXT_SERIALIZED
            )
        );
    }

    private static Headers createHeaders() {
        return new Headers();
    }

    private static class Headers extends HashMap<String, String> {
        public Headers add(String k, String v) {
            this.put(k, v);
            return this;
        }
    }
}
