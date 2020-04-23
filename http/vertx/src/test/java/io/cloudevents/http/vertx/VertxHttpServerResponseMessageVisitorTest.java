package io.cloudevents.http.vertx;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.mock.CSVFormat;
import io.cloudevents.types.Time;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.cloudevents.test.Data.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class VertxHttpServerResponseMessageVisitorTest {

    @ParameterizedTest
    @MethodSource("io.cloudevents.test.Data#allEventsWithoutExtensions")
    void testReplyWithStructured(Vertx vertx, VertxTestContext testContext, CloudEvent event) {
        Checkpoint checkpoint = testContext.checkpoint(2);

        HttpServer server = vertx
            .createHttpServer()
            .requestHandler(httpServerRequest -> {
                try {
                    event.asStructuredMessage(CSVFormat.INSTANCE).visit(
                        VertxHttpServerResponseMessageVisitor.create(httpServerRequest.response())
                    );
                    checkpoint.flag();
                } catch (Throwable e) {
                    testContext.failNow(e);
                }
            })
            .listen(0);

        HttpClient client = vertx.createHttpClient();

        client.get(server.actualPort(), "localhost", "/", res -> {
            res.bodyHandler(buf -> {
                testContext.verify(() -> {
                    assertThat(res.statusCode())
                        .isEqualTo(200);
                    assertThat(res.getHeader("content-type"))
                        .isEqualTo(CSVFormat.INSTANCE.serializedContentType());
                    assertThat(buf)
                        .isEqualTo(CSVFormat.INSTANCE.serialize(event));

                    checkpoint.flag();
                });
            });
        }).end();
    }

    @ParameterizedTest
    @MethodSource("binaryTestArguments")
    void testReplyWithBinary(Vertx vertx, VertxTestContext testContext, CloudEvent event, MultiMap headers, Buffer body) {
        Checkpoint checkpoint = testContext.checkpoint(2);

        HttpServer server = vertx
            .createHttpServer()
            .requestHandler(httpServerRequest -> {
                try {
                    event.asBinaryMessage().visit(
                        VertxHttpServerResponseMessageVisitor.create(httpServerRequest.response())
                    );
                    checkpoint.flag();
                } catch (Throwable e) {
                    testContext.failNow(e);
                }
            })
            .listen(0);

        HttpClient client = vertx.createHttpClient();

        client.get(server.actualPort(), "localhost", "/", res -> {
            res.bodyHandler(buf -> {
                testContext.verify(() -> {
                    assertThat(res.statusCode())
                        .isEqualTo(200);

                    headers.forEach(e -> {
                        assertThat(res.getHeader(e.getKey())).isEqualTo(e.getValue());
                    });

                    if (body != null) {
                        assertThat(buf).isEqualTo(body);
                    }

                    checkpoint.flag();
                });
            });
        }).end();
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
