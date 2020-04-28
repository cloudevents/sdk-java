package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.kafka.impl.KafkaHeaders;
import io.cloudevents.mock.CSVFormat;
import io.cloudevents.types.Time;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.cloudevents.kafka.KafkaUtils.header;
import static io.cloudevents.kafka.KafkaUtils.kafkaHeaders;
import static io.cloudevents.test.Data.*;
import static org.assertj.core.api.Assertions.assertThat;

public class KafkaProducerMessageVisitorTest {

    @ParameterizedTest
    @MethodSource("io.cloudevents.test.Data#allEventsWithoutExtensions")
    void testRequestWithStructured(CloudEvent event) {
        String expectedContentType = CSVFormat.INSTANCE.serializedContentType();
        byte[] expectedBuffer = CSVFormat.INSTANCE.serialize(event);

        String topic = "test";
        Integer partition = 10;
        Long timestamp = System.currentTimeMillis();
        String key = "aaa";

        ProducerRecord<String, byte[]> producerRecord = event
            .asStructuredMessage(CSVFormat.INSTANCE)
            .visit(KafkaProducerMessageVisitor.create(topic, partition, timestamp, key));

        assertThat(producerRecord.topic())
            .isEqualTo(topic);
        assertThat(producerRecord.partition())
            .isEqualTo(partition);
        assertThat(producerRecord.timestamp())
            .isEqualTo(timestamp);
        assertThat(producerRecord.key())
            .isEqualTo(key);
        assertThat(producerRecord.headers())
            .containsExactly(new RecordHeader(KafkaHeaders.CONTENT_TYPE, expectedContentType.getBytes()));
        assertThat(producerRecord.value())
            .isEqualTo(expectedBuffer);
    }

    @ParameterizedTest
    @MethodSource("binaryTestArguments")
    void testRequestWithBinary(CloudEvent event, Headers expectedHeaders, byte[] expectedBody) {
        String topic = "test";
        Integer partition = 10;
        Long timestamp = System.currentTimeMillis();
        String key = "aaa";

        ProducerRecord<String, byte[]> producerRecord = event
            .asBinaryMessage()
            .visit(KafkaProducerMessageVisitor.create(topic, partition, timestamp, key));

        assertThat(producerRecord.topic())
            .isEqualTo(topic);
        assertThat(producerRecord.partition())
            .isEqualTo(partition);
        assertThat(producerRecord.timestamp())
            .isEqualTo(timestamp);
        assertThat(producerRecord.key())
            .isEqualTo(key);
        assertThat(producerRecord.headers())
            .containsExactlyInAnyOrder(expectedHeaders.toArray());
        assertThat(producerRecord.value())
            .isEqualTo(expectedBody);
    }

    public static Stream<Arguments> binaryTestArguments() {
        return Stream.of(
            // V03
            Arguments.of(
                V03_MIN,
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V03.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString())
                ),
                null
            ),
            Arguments.of(
                V03_WITH_JSON_DATA,
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V03.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("ce_schemaurl", DATASCHEMA.toString()),
                    header("content-type", DATACONTENTTYPE_JSON),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.RFC3339_DATE_FORMAT.format(TIME))
                ),
                DATA_JSON_SERIALIZED
            ),
            Arguments.of(
                V03_WITH_JSON_DATA_WITH_EXT_STRING,
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V03.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("ce_schemaurl", DATASCHEMA.toString()),
                    header("content-type", DATACONTENTTYPE_JSON),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.RFC3339_DATE_FORMAT.format(TIME)),
                    header("ce_astring", "aaa"),
                    header("ce_aboolean", "true"),
                    header("ce_anumber", "10")
                ),
                DATA_JSON_SERIALIZED
            ),
            Arguments.of(
                V03_WITH_XML_DATA,
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V03.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("content-type", DATACONTENTTYPE_XML),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.RFC3339_DATE_FORMAT.format(TIME))
                ),
                DATA_XML_SERIALIZED
            ),
            Arguments.of(
                V03_WITH_TEXT_DATA,
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V03.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("content-type", DATACONTENTTYPE_TEXT),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.RFC3339_DATE_FORMAT.format(TIME))
                ),
                DATA_TEXT_SERIALIZED
            ),
            // V1
            Arguments.of(
                V1_MIN,
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V1.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString())
                ),
                null
            ),
            Arguments.of(
                V1_WITH_JSON_DATA,
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V1.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("ce_dataschema", DATASCHEMA.toString()),
                    header("content-type", DATACONTENTTYPE_JSON),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.RFC3339_DATE_FORMAT.format(TIME))
                ),
                DATA_JSON_SERIALIZED
            ),
            Arguments.of(
                V1_WITH_JSON_DATA_WITH_EXT_STRING,
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V1.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("ce_dataschema", DATASCHEMA.toString()),
                    header("content-type", DATACONTENTTYPE_JSON),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.RFC3339_DATE_FORMAT.format(TIME)),
                    header("ce_astring", "aaa"),
                    header("ce_aboolean", "true"),
                    header("ce_anumber", "10")
                ),
                DATA_JSON_SERIALIZED
            ),
            Arguments.of(
                V1_WITH_XML_DATA,
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V1.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("content-type", DATACONTENTTYPE_XML),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.RFC3339_DATE_FORMAT.format(TIME))
                ),
                DATA_XML_SERIALIZED
            ),
            Arguments.of(
                V1_WITH_TEXT_DATA,
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V1.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("content-type", DATACONTENTTYPE_TEXT),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.RFC3339_DATE_FORMAT.format(TIME))
                ),
                DATA_TEXT_SERIALIZED
            )
        );
    }

}
