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

package io.cloudevents.rocketmq;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.test.Data;
import io.cloudevents.core.v03.CloudEventV03;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.types.Time;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests verifying the behavior of the {@code RocketmqMessageFactory}.
 */
public class RocketmqMessageFactoryTest {
    private static final String PREFIX_TEMPLATE = RocketmqConstants.CE_PREFIX + "%s";

    private static final String DATA_CONTENT_TYPE_NULL = null;
    private static final byte[] DATA_PAYLOAD_NULL = null;

    @ParameterizedTest()
    @MethodSource("binaryTestArguments")
    public void readBinary(final Map<String, String> props, final String contentType, final byte[] body, final CloudEvent event) {
        final MessageReader reader = RocketMqMessageFactory.createReader(contentType, props, body);
        assertThat(reader.getEncoding()).isEqualTo(Encoding.BINARY);
        assertThat(reader.toEvent()).isEqualTo(event);
    }

    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void readStructured(final CloudEvent event) {
        final String contentType = CSVFormat.INSTANCE.serializedContentType() + "; charset=utf8";
        final byte[] contentPayload = CSVFormat.INSTANCE.serialize(event);

        final MessageReader reader = RocketMqMessageFactory.createReader(contentType, null, contentPayload);
        assertThat(reader.getEncoding()).isEqualTo(Encoding.STRUCTURED);
        assertThat(reader.toEvent()).isEqualTo(event);
    }

    private static Stream<Arguments> binaryTestArguments() {

        return Stream.of(
            // V03
            Arguments.of(
                properties(
                    property(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                    property(CloudEventV03.ID, Data.ID),
                    property(CloudEventV03.TYPE, Data.TYPE),
                    property(CloudEventV03.SOURCE, Data.SOURCE.toString()),
                    property("ignored", "ignore")
                ),
                DATA_CONTENT_TYPE_NULL,
                DATA_PAYLOAD_NULL,
                Data.V03_MIN
            ),
            Arguments.of(
                properties(
                    property(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                    property(CloudEventV03.ID, Data.ID),
                    property(CloudEventV03.TYPE, Data.TYPE),
                    property(CloudEventV03.SOURCE, Data.SOURCE.toString()),
                    property(CloudEventV03.SCHEMAURL, Data.DATASCHEMA.toString()),
                    property(CloudEventV03.SUBJECT, Data.SUBJECT),
                    property(CloudEventV03.TIME, Time.writeTime(Data.TIME)),
                    property("ignored", "ignore")
                ),
                Data.DATACONTENTTYPE_JSON,
                Data.DATA_JSON_SERIALIZED,
                Data.V03_WITH_JSON_DATA
            ),
            Arguments.of(
                properties(
                    property(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                    property(CloudEventV03.ID, Data.ID),
                    property(CloudEventV03.TYPE, Data.TYPE),
                    property(CloudEventV03.SOURCE, Data.SOURCE.toString()),
                    property(CloudEventV03.SCHEMAURL, Data.DATASCHEMA.toString()),
                    property(CloudEventV03.SUBJECT, Data.SUBJECT),
                    property(CloudEventV03.TIME, Time.writeTime(Data.TIME)),
                    property("astring", "aaa"),
                    property("aboolean", "true"),
                    property("anumber", "10"),
                    property("ignored", "ignored")
                ),
                Data.DATACONTENTTYPE_JSON,
                Data.DATA_JSON_SERIALIZED,
                Data.V03_WITH_JSON_DATA_WITH_EXT_STRING
            ),
            Arguments.of(
                properties(
                    property(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                    property(CloudEventV03.ID, Data.ID),
                    property(CloudEventV03.TYPE, Data.TYPE),
                    property(CloudEventV03.SOURCE, Data.SOURCE.toString()),
                    property(CloudEventV03.SUBJECT, Data.SUBJECT),
                    property(CloudEventV03.TIME, Time.writeTime(Data.TIME)),
                    property("ignored", "ignored")
                ),
                Data.DATACONTENTTYPE_XML,
                Data.DATA_XML_SERIALIZED,
                Data.V03_WITH_XML_DATA
            ),
            Arguments.of(
                properties(
                    property(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                    property(CloudEventV03.ID, Data.ID),
                    property(CloudEventV03.TYPE, Data.TYPE),
                    property(CloudEventV03.SOURCE, Data.SOURCE.toString()),
                    property(CloudEventV03.SUBJECT, Data.SUBJECT),
                    property(CloudEventV03.TIME, Time.writeTime(Data.TIME)),
                    property("ignored", "ignored")
                ),
                Data.DATACONTENTTYPE_TEXT,
                Data.DATA_TEXT_SERIALIZED,
                Data.V03_WITH_TEXT_DATA
            ),
            // V1
            Arguments.of(
                properties(
                    property(CloudEventV1.SPECVERSION, SpecVersion.V1.toString()),
                    property(CloudEventV1.ID, Data.ID),
                    property(CloudEventV1.TYPE, Data.TYPE),
                    property(CloudEventV1.SOURCE, Data.SOURCE.toString()),
                    property("ignored", "ignored")
                ),
                DATA_CONTENT_TYPE_NULL,
                DATA_PAYLOAD_NULL,
                Data.V1_MIN
            ),
            Arguments.of(
                properties(
                    property(CloudEventV1.SPECVERSION, SpecVersion.V1.toString()),
                    property(CloudEventV1.ID, Data.ID),
                    property(CloudEventV1.TYPE, Data.TYPE),
                    property(CloudEventV1.SOURCE, Data.SOURCE.toString()),
                    property(CloudEventV1.DATASCHEMA, Data.DATASCHEMA.toString()),
                    property(CloudEventV1.SUBJECT, Data.SUBJECT),
                    property(CloudEventV1.TIME, Time.writeTime(Data.TIME)),
                    property("ignored", "ignored")
                ),
                Data.DATACONTENTTYPE_JSON,
                Data.DATA_JSON_SERIALIZED,
                Data.V1_WITH_JSON_DATA
            ),
            Arguments.of(
                properties(
                    property(CloudEventV1.SPECVERSION, SpecVersion.V1.toString()),
                    property(CloudEventV1.ID, Data.ID),
                    property(CloudEventV1.TYPE, Data.TYPE),
                    property(CloudEventV1.SOURCE, Data.SOURCE.toString()),
                    property(CloudEventV1.DATASCHEMA, Data.DATASCHEMA.toString()),
                    property(CloudEventV1.SUBJECT, Data.SUBJECT),
                    property(CloudEventV1.TIME, Time.writeTime(Data.TIME)),
                    property("astring", "aaa"),
                    property("aboolean", "true"),
                    property("anumber", "10"),
                    property("ignored", "ignored")
                ),
                Data.DATACONTENTTYPE_JSON,
                Data.DATA_JSON_SERIALIZED,
                Data.V1_WITH_JSON_DATA_WITH_EXT_STRING
            ),
            Arguments.of(
                properties(
                    property(CloudEventV1.SPECVERSION, SpecVersion.V1.toString()),
                    property(CloudEventV1.ID, Data.ID),
                    property(CloudEventV1.TYPE, Data.TYPE),
                    property(CloudEventV1.SOURCE, Data.SOURCE.toString()),
                    property(CloudEventV1.SUBJECT, Data.SUBJECT),
                    property(CloudEventV1.TIME, Time.writeTime(Data.TIME)),
                    property("ignored", "ignored")
                ),
                Data.DATACONTENTTYPE_XML,
                Data.DATA_XML_SERIALIZED,
                Data.V1_WITH_XML_DATA
            ),
            Arguments.of(
                properties(
                    property(CloudEventV1.SPECVERSION, SpecVersion.V1.toString()),
                    property(CloudEventV1.ID, Data.ID),
                    property(CloudEventV1.TYPE, Data.TYPE),
                    property(CloudEventV1.SOURCE, Data.SOURCE.toString()),
                    property(CloudEventV1.SUBJECT, Data.SUBJECT),
                    property(CloudEventV1.TIME, Time.writeTime(Data.TIME)),
                    property("ignored", "ignored")
                ),
                Data.DATACONTENTTYPE_TEXT,
                Data.DATA_TEXT_SERIALIZED,
                Data.V1_WITH_TEXT_DATA
            )
        );
    }

    private static AbstractMap.SimpleEntry<String, String> property(final String name, final String value) {
        return name.equalsIgnoreCase("ignored") ?
            new AbstractMap.SimpleEntry<>(name, value) :
            new AbstractMap.SimpleEntry<>(String.format(PREFIX_TEMPLATE, name), value);
    }

    @SafeVarargs
    private static Map<String, String> properties(final AbstractMap.SimpleEntry<String, String>... entries) {
        return Stream.of(entries)
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

    }
}
