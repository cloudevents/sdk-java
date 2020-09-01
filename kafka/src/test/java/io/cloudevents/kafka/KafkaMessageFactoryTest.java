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

package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.types.Time;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.cloudevents.core.test.Data.*;
import static io.cloudevents.kafka.KafkaUtils.header;
import static io.cloudevents.kafka.KafkaUtils.kafkaHeaders;
import static org.assertj.core.api.Assertions.assertThat;

public class KafkaMessageFactoryTest {

    @ParameterizedTest()
    @MethodSource("binaryTestArguments")
    public void readBinary(Headers headers, byte[] body, CloudEvent event) {
        MessageReader message = KafkaMessageFactory.createReader(headers, body);

        assertThat(message.getEncoding())
            .isEqualTo(Encoding.BINARY);
        assertThat(message.toEvent())
            .isEqualTo(event);
    }

    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void readStructured(CloudEvent event) {
        byte[] serializedEvent = CSVFormat.INSTANCE.serialize(event);

        MessageReader message = KafkaMessageFactory.createReader(
            new RecordHeaders().add("content-type", (CSVFormat.INSTANCE.serializedContentType() + "; charset=utf8").getBytes()),
            serializedEvent
        );

        assertThat(message.getEncoding())
            .isEqualTo(Encoding.STRUCTURED);
        assertThat(message.toEvent())
            .isEqualTo(event);
    }

    public static Stream<Arguments> binaryTestArguments() {
        return Stream.of(
            // V03
            Arguments.of(
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V03.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("ignored", "ignored")
                ),
                null,
                V03_MIN
            ),
            Arguments.of(
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V03.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("ce_schemaurl", DATASCHEMA.toString()),
                    header("content-type", DATACONTENTTYPE_JSON),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.writeTime(TIME)),
                    header("ignored", "ignored")
                ),
                DATA_JSON_SERIALIZED,
                V03_WITH_JSON_DATA
            ),
            Arguments.of(
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V03.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("ce_schemaurl", DATASCHEMA.toString()),
                    header("content-type", DATACONTENTTYPE_JSON),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.writeTime(TIME)),
                    header("ce_astring", "aaa"),
                    header("ce_aboolean", "true"),
                    header("ce_anumber", "10"),
                    header("ignored", "ignored")
                ),
                DATA_JSON_SERIALIZED,
                V03_WITH_JSON_DATA_WITH_EXT_STRING
            ),
            Arguments.of(
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V03.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("content-type", DATACONTENTTYPE_XML),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.writeTime(TIME)),
                    header("ignored", "ignored")
                ),
                DATA_XML_SERIALIZED,
                V03_WITH_XML_DATA
            ),
            Arguments.of(
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V03.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("content-type", DATACONTENTTYPE_TEXT),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.writeTime(TIME)),
                    header("ignored", "ignored")
                ),
                DATA_TEXT_SERIALIZED,
                V03_WITH_TEXT_DATA
            ),
            // V1
            Arguments.of(
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V1.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("ignored", "ignored")
                ),
                null,
                V1_MIN
            ),
            Arguments.of(
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V1.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("ce_dataschema", DATASCHEMA.toString()),
                    header("content-type", DATACONTENTTYPE_JSON),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.writeTime(TIME)),
                    header("ignored", "ignored")
                ),
                DATA_JSON_SERIALIZED,
                V1_WITH_JSON_DATA
            ),
            Arguments.of(
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V1.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("ce_dataschema", DATASCHEMA.toString()),
                    header("content-type", DATACONTENTTYPE_JSON),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.writeTime(TIME)),
                    header("ce_astring", "aaa"),
                    header("ce_aboolean", "true"),
                    header("ce_anumber", "10"),
                    header("ignored", "ignored")
                ),
                DATA_JSON_SERIALIZED,
                V1_WITH_JSON_DATA_WITH_EXT_STRING
            ),
            Arguments.of(
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V1.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("content-type", DATACONTENTTYPE_XML),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.writeTime(TIME)),
                    header("ignored", "ignored")
                ),
                DATA_XML_SERIALIZED,
                V1_WITH_XML_DATA
            ),
            Arguments.of(
                kafkaHeaders(
                    header("ce_specversion", SpecVersion.V1.toString()),
                    header("ce_id", ID),
                    header("ce_type", TYPE),
                    header("ce_source", SOURCE.toString()),
                    header("content-type", DATACONTENTTYPE_TEXT),
                    header("ce_subject", SUBJECT),
                    header("ce_time", Time.writeTime(TIME)),
                    header("ignored", "ignored")
                ),
                DATA_TEXT_SERIALIZED,
                V1_WITH_TEXT_DATA
            )
        );
    }

}
