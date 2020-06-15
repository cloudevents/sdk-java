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

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.types.Time;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.cloudevents.core.test.Data.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class VertxHttpServerResponseMessageWriterTest {

    @ParameterizedTest
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    void testReplyWithStructured(CloudEvent event, Vertx vertx, VertxTestContext testContext) {
        Checkpoint checkpoint = testContext.checkpoint(2);

        vertx
            .createHttpServer()
            .requestHandler(httpServerRequest -> {
                try {
                    GenericStructuredMessageReader.from(event, CSVFormat.INSTANCE).visit(
                        VertxHttpServerResponseMessageWriter.create(httpServerRequest.response())
                    );
                    checkpoint.flag();
                } catch (Throwable e) {
                    testContext.failNow(e);
                }
            })
            .listen(9000, testContext.succeeding(server -> {
                HttpClient client = vertx.createHttpClient();
                client
                    .get(server.actualPort(), "localhost", "/")
                    .handler(res -> {
                        res.bodyHandler(buf -> {
                            testContext.verify(() -> {
                                assertThat(res.statusCode())
                                    .isEqualTo(200);
                                assertThat(res.getHeader("content-type"))
                                    .isEqualTo(CSVFormat.INSTANCE.serializedContentType());
                                assertThat(buf.getBytes())
                                    .isEqualTo(CSVFormat.INSTANCE.serialize(event));

                                checkpoint.flag();
                            });
                        });
                    })
                    .end();
            }));
    }

    @ParameterizedTest
    @MethodSource("binaryTestArguments")
    void testReplyWithBinary(CloudEvent event, MultiMap headers, Buffer body, Vertx vertx, VertxTestContext testContext) {
        Checkpoint checkpoint = testContext.checkpoint(2);

        vertx
            .createHttpServer()
            .requestHandler(httpServerRequest -> {
                try {
                    VertxHttpServerResponseMessageWriter
                        .create(httpServerRequest.response())
                        .writeBinary(event);
                    checkpoint.flag();
                } catch (Throwable e) {
                    testContext.failNow(e);
                }
            })
            .listen(9000, testContext.succeeding(server -> {
                HttpClient client = vertx.createHttpClient();
                client
                    .get(server.actualPort(), "localhost", "/")
                    .handler(res -> {
                        res.bodyHandler(buf -> {
                            testContext.verify(() -> {
                                assertThat(res.statusCode())
                                    .isEqualTo(200);
                                headers.forEach(e -> {
                                    assertThat(res.getHeader(e.getKey())).isEqualTo(e.getValue());
                                });
                                if (body != null) {
                                    assertThat(buf.getBytes())
                                        .isEqualTo(body.getBytes());
                                }
                            });
                            checkpoint.flag();
                        });
                    })
                    .end();
            }));
    }

    public static Stream<Arguments> binaryTestArguments() {
        return Stream.of(
            // V03
            Arguments.of(
                V03_MIN,
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString()),
                null
            ),
            Arguments.of(
                V03_WITH_JSON_DATA,
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ce-schemaurl", DATASCHEMA.toString())
                    .add("content-type", DATACONTENTTYPE_JSON)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.RFC3339_DATE_FORMAT.format(TIME)),
                Buffer.buffer(DATA_JSON_SERIALIZED)
            ),
            Arguments.of(
                V03_WITH_JSON_DATA_WITH_EXT_STRING,
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ce-schemaurl", DATASCHEMA.toString())
                    .add("content-type", DATACONTENTTYPE_JSON)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.RFC3339_DATE_FORMAT.format(TIME))
                    .add("ce-astring", "aaa")
                    .add("ce-aboolean", "true")
                    .add("ce-anumber", "10"),
                Buffer.buffer(DATA_JSON_SERIALIZED)
            ),
            Arguments.of(
                V03_WITH_XML_DATA,
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_XML)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.RFC3339_DATE_FORMAT.format(TIME)),
                Buffer.buffer(DATA_XML_SERIALIZED)
            ),
            Arguments.of(
                V03_WITH_TEXT_DATA,
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V03.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_TEXT)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.RFC3339_DATE_FORMAT.format(TIME)),
                Buffer.buffer(DATA_TEXT_SERIALIZED)
            ),
            // V1
            Arguments.of(
                V1_MIN,
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString()),
                null
            ),
            Arguments.of(
                V1_WITH_JSON_DATA,
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ce-dataschema", DATASCHEMA.toString())
                    .add("content-type", DATACONTENTTYPE_JSON)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.RFC3339_DATE_FORMAT.format(TIME)),
                Buffer.buffer(DATA_JSON_SERIALIZED)
            ),
            Arguments.of(
                V1_WITH_JSON_DATA_WITH_EXT_STRING,
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("ce-dataschema", DATASCHEMA.toString())
                    .add("content-type", DATACONTENTTYPE_JSON)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.RFC3339_DATE_FORMAT.format(TIME))
                    .add("ce-astring", "aaa")
                    .add("ce-aboolean", "true")
                    .add("ce-anumber", "10"),
                Buffer.buffer(DATA_JSON_SERIALIZED)
            ),
            Arguments.of(
                V1_WITH_XML_DATA,
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_XML)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.RFC3339_DATE_FORMAT.format(TIME)),
                Buffer.buffer(DATA_XML_SERIALIZED)
            ),
            Arguments.of(
                V1_WITH_TEXT_DATA,
                MultiMap.caseInsensitiveMultiMap()
                    .add("ce-specversion", SpecVersion.V1.toString())
                    .add("ce-id", ID)
                    .add("ce-type", TYPE)
                    .add("ce-source", SOURCE.toString())
                    .add("content-type", DATACONTENTTYPE_TEXT)
                    .add("ce-subject", SUBJECT)
                    .add("ce-time", Time.RFC3339_DATE_FORMAT.format(TIME)),
                Buffer.buffer(DATA_TEXT_SERIALIZED)
            )
        );
    }

}
