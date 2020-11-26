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

package io.cloudevents.http.vertx;

import static io.cloudevents.core.test.Data.DATACONTENTTYPE_JSON;
import static io.cloudevents.core.test.Data.DATACONTENTTYPE_TEXT;
import static io.cloudevents.core.test.Data.DATACONTENTTYPE_XML;
import static io.cloudevents.core.test.Data.DATASCHEMA;
import static io.cloudevents.core.test.Data.DATA_JSON_SERIALIZED;
import static io.cloudevents.core.test.Data.DATA_TEXT_SERIALIZED;
import static io.cloudevents.core.test.Data.DATA_XML_SERIALIZED;
import static io.cloudevents.core.test.Data.ID;
import static io.cloudevents.core.test.Data.SOURCE;
import static io.cloudevents.core.test.Data.SUBJECT;
import static io.cloudevents.core.test.Data.TIME;
import static io.cloudevents.core.test.Data.TYPE;
import static io.cloudevents.core.test.Data.V03_MIN;
import static io.cloudevents.core.test.Data.V03_WITH_JSON_DATA;
import static io.cloudevents.core.test.Data.V03_WITH_JSON_DATA_WITH_EXT_STRING;
import static io.cloudevents.core.test.Data.V03_WITH_TEXT_DATA;
import static io.cloudevents.core.test.Data.V03_WITH_XML_DATA;
import static io.cloudevents.core.test.Data.V1_MIN;
import static io.cloudevents.core.test.Data.V1_WITH_JSON_DATA;
import static io.cloudevents.core.test.Data.V1_WITH_JSON_DATA_WITH_EXT_STRING;
import static io.cloudevents.core.test.Data.V1_WITH_TEXT_DATA;
import static io.cloudevents.core.test.Data.V1_WITH_XML_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.types.Time;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(VertxExtension.class)
public class VertxMessageFactoryTest {

    @Test
    public void structuredMessageWithoutBody() {
        MultiMap headers = MultiMap
            .caseInsensitiveMultiMap()
            .add("content-type", CSVFormat.INSTANCE.serializedContentType() + "; charset=utf8");

        assertThatCode(() -> VertxMessageFactory.createReader(headers, null))
            .isInstanceOf(CloudEventRWException.class)
            .hasCauseInstanceOf(IllegalStateException.class)
            .hasMessageContaining("null body");
    }

    @ParameterizedTest
    @MethodSource("binaryTestArguments")
    public void readBinary(MultiMap headers, Buffer body, CloudEvent event) {
        MessageReader message = VertxMessageFactory.createReader(headers, body);

        assertThat(message.getEncoding())
            .isEqualTo(Encoding.BINARY);
        assertThat(message.toEvent())
            .isEqualTo(event);
    }

    @ParameterizedTest
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void readStructured(CloudEvent event) {
        byte[] serializedEvent = CSVFormat.INSTANCE.serialize(event);

        MessageReader message = VertxMessageFactory.createReader(
            MultiMap.caseInsensitiveMultiMap().add("content-type", CSVFormat.INSTANCE.serializedContentType() + "; charset=utf8"),
            Buffer.buffer(serializedEvent)
        );

        assertThat(message.getEncoding())
            .isEqualTo(Encoding.STRUCTURED);
        assertThat(message.toEvent())
            .isEqualTo(event);
    }

    @Test
    @Timeout(timeUnit = TimeUnit.SECONDS, value = 2)
    public void shouldCatchAllExceptions(final Vertx vertx, final VertxTestContext context) throws InterruptedException {

        final int port = 4200;

        final CountDownLatch cd = new CountDownLatch(1);

        vertx.createHttpServer()
            .exceptionHandler(context::failNow)
            .requestHandler(r -> VertxMessageFactory.createReader(r)
                .onFailure(cause -> context.completeNow())
                .onSuccess(reader -> context.failNow("Expected failed future"))
            )
            .listen(port, context.succeeding(s -> cd.countDown()));

        cd.await(2, TimeUnit.SECONDS);

        WebClient.create(vertx)
            .post(port, "127.0.0.1", "")
            .putHeader("Content-Type", "application/cloudevents+json; charset=UTF-8")
            .putHeader("ce-specversion", "9000.1")
            .putHeader("ce-type", "type")
            .putHeader("ce-source", "/mysource")
            .send()
            .onFailure(context::failNow)
            .onSuccess(r -> {
            });
    }

    public static Stream<Arguments> binaryTestArguments() {
        return Stream.of(
            // V03
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ignored", "ignored"),
                null,
                V03_MIN
            ),
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ce-schemaurl", DATASCHEMA.toString())
                    .add("content-type", DATACONTENTTYPE_JSON)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME))
                    .add("ignored", "ignored"),
                Buffer.buffer(DATA_JSON_SERIALIZED),
                V03_WITH_JSON_DATA
            ),
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
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
                    .add("ce-anumber", "10")
                    .add("ignored", "ignored"),
                Buffer.buffer(DATA_JSON_SERIALIZED),
                V03_WITH_JSON_DATA_WITH_EXT_STRING
            ),
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_XML)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME))
                    .add("ignored", "ignored"),
                Buffer.buffer(DATA_XML_SERIALIZED),
                V03_WITH_XML_DATA
            ),
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_TEXT)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME))
                    .add("ignored", "ignored"),
                Buffer.buffer(DATA_TEXT_SERIALIZED),
                V03_WITH_TEXT_DATA
            ),
            // V1
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ignored", "ignored"),
                null,
                V1_MIN
            ),
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ce-dataschema", DATASCHEMA.toString())
                    .add("content-type", DATACONTENTTYPE_JSON)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME))
                    .add("ignored", "ignored"),
                Buffer.buffer(DATA_JSON_SERIALIZED),
                V1_WITH_JSON_DATA
            ),
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
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
                    .add("ce-anumber", "10")
                    .add("ignored", "ignored"),
                Buffer.buffer(DATA_JSON_SERIALIZED),
                V1_WITH_JSON_DATA_WITH_EXT_STRING
            ),
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_XML)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME))
                    .add("ignored", "ignored"),
                Buffer.buffer(DATA_XML_SERIALIZED),
                V1_WITH_XML_DATA
            ),
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_TEXT)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.writeTime(TIME))
                    .add("ignored", "ignored"),
                Buffer.buffer(DATA_TEXT_SERIALIZED),
                V1_WITH_TEXT_DATA
            ),
            // Headers case insensitivity
            Arguments.of(
                MultiMap.caseInsensitiveMultiMap()
                    .add("Ce-sPecversion", SpecVersion.V03.toString())
                    .add("cE-id", ID)
                    .add("CE-Type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ignored", "ignored")
                    .add("ab", "should-not-break-anything"),
                null,
                V03_MIN
            )
        );
    }

}
