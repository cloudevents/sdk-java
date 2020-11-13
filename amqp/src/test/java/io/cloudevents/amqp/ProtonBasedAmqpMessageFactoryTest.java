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

package io.cloudevents.amqp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.test.Data;
import io.cloudevents.types.Time;

/**
 * Tests verifying the behavior of the {@code ProtonBasedAmqpMessageFactory}.
 */
public class ProtonBasedAmqpMessageFactoryTest {

    private static final String PREFIX_TEMPLATE = "cloudEvents:%s";
    private static final String DATACONTENTTYPE_NULL = null;
    private static final byte[] DATAPAYLOAD_NULL = null;

    @ParameterizedTest()
    @MethodSource("binaryTestArguments")
    public void readBinary(final Map<String, Object> props, final String contentType, final byte[] body,
            final CloudEvent event) {
        final MessageReader amqpReader = ProtonBasedAmqpMessageFactory.createReader(contentType, new ApplicationProperties(props), body);
        assertThat(amqpReader.getEncoding()).isEqualTo(Encoding.BINARY);
        assertThat(amqpReader.toEvent()).isEqualTo(event);
    }

    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void readStructured(final CloudEvent event) {
        final String contentType = CSVFormat.INSTANCE.serializedContentType() + "; charset=utf8";
        final byte[] contentPayload = CSVFormat.INSTANCE.serialize(event);

        final MessageReader amqpReader = ProtonBasedAmqpMessageFactory.createReader(contentType, null, contentPayload);
        assertThat(amqpReader.getEncoding()).isEqualTo(Encoding.STRUCTURED);
        assertThat(amqpReader.toEvent()).isEqualTo(event);
    }

    private static Stream<Arguments> binaryTestArguments() {

        return Stream.of(
                // V03
                Arguments.of(
                        properties(
                                property("specversion", SpecVersion.V03.toString()),
                                property("id", Data.ID),
                                property("type", Data.TYPE),
                                property("source", Data.SOURCE),
                                property("ignored", "ignore")
                        ),
                        DATACONTENTTYPE_NULL,
                        DATAPAYLOAD_NULL,
                        Data.V03_MIN
                ),
                Arguments.of(
                        properties(
                                property("specversion", SpecVersion.V03.toString()),
                                property("id", Data.ID),
                                property("type", Data.TYPE),
                                property("source", Data.SOURCE.toString()),
                                property("schemaurl", Data.DATASCHEMA.toString()),
                                property("subject", Data.SUBJECT),
                                property("time", Time.writeTime(Data.TIME)),
                                property("ignored", "ignore")
                        ),
                        Data.DATACONTENTTYPE_JSON,
                        Data.DATA_JSON_SERIALIZED,
                        Data.V03_WITH_JSON_DATA
                ),
                Arguments.of(
                        properties(
                            property("specversion", SpecVersion.V03.toString()),
                            property("id", Data.ID),
                            property("type", Data.TYPE),
                            property("source", Data.SOURCE.toString()),
                            property("schemaurl", Data.DATASCHEMA.toString()),
                            property("subject", Data.SUBJECT),
                            property("time", Time.writeTime(Data.TIME)),
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
                            property("specversion", SpecVersion.V03.toString()),
                            property("id", Data.ID),
                            property("type", Data.TYPE),
                            property("source", Data.SOURCE.toString()),
                            property("subject", Data.SUBJECT),
                            property("time", Time.writeTime(Data.TIME)),
                            property("ignored", "ignored")
                        ),
                        Data.DATACONTENTTYPE_XML,
                        Data.DATA_XML_SERIALIZED,
                        Data.V03_WITH_XML_DATA
                    ),
                Arguments.of(
                        properties(
                            property("specversion", SpecVersion.V03.toString()),
                            property("id", Data.ID),
                            property("type", Data.TYPE),
                            property("source", Data.SOURCE.toString()),
                            property("subject", Data.SUBJECT),
                            property("time", Time.writeTime(Data.TIME)),
                            property("ignored", "ignored")
                        ),
                        Data.DATACONTENTTYPE_TEXT,
                        Data.DATA_TEXT_SERIALIZED,
                        Data.V03_WITH_TEXT_DATA
                    ),
                // V1
                Arguments.of(
                    properties(
                        property("specversion", SpecVersion.V1.toString()),
                        property("id", Data.ID),
                        property("type", Data.TYPE),
                        property("source", Data.SOURCE.toString()),
                        property("ignored", "ignored")
                    ),
                    DATACONTENTTYPE_NULL,
                    DATAPAYLOAD_NULL,
                    Data.V1_MIN
                ),
                Arguments.of(
                        properties(
                            property("specversion", SpecVersion.V1.toString()),
                            property("id", Data.ID),
                            property("type", Data.TYPE),
                            property("source", Data.SOURCE.toString()),
                            property("dataschema", Data.DATASCHEMA.toString()),
                            property("subject", Data.SUBJECT),
                            property("time", Time.writeTime(Data.TIME)),
                            property("ignored", "ignored")
                        ),
                        Data.DATACONTENTTYPE_JSON,
                        Data.DATA_JSON_SERIALIZED,
                        Data.V1_WITH_JSON_DATA
                    ),
                Arguments.of(
                        properties(
                            property("specversion", SpecVersion.V1.toString()),
                            property("id", Data.ID),
                            property("type", Data.TYPE),
                            property("source", Data.SOURCE.toString()),
                            property("dataschema", Data.DATASCHEMA.toString()),
                            property("subject", Data.SUBJECT),
                            property("time", Time.writeTime(Data.TIME)),
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
                            property("specversion", SpecVersion.V1.toString()),
                            property("id", Data.ID),
                            property("type", Data.TYPE),
                            property("source", Data.SOURCE.toString()),
                            property("subject", Data.SUBJECT),
                            property("time", Time.writeTime(Data.TIME)),
                            property("ignored", "ignored")
                        ),
                        Data.DATACONTENTTYPE_XML,
                        Data.DATA_XML_SERIALIZED,
                        Data.V1_WITH_XML_DATA
                    ),
                Arguments.of(
                        properties(
                            property("specversion", SpecVersion.V1.toString()),
                            property("id", Data.ID),
                            property("type", Data.TYPE),
                            property("source", Data.SOURCE.toString()),
                            property("subject", Data.SUBJECT),
                            property("time", Time.writeTime(Data.TIME)),
                            property("ignored", "ignored")
                        ),
                        Data.DATACONTENTTYPE_TEXT,
                        Data.DATA_TEXT_SERIALIZED,
                        Data.V1_WITH_TEXT_DATA
                    )
        );
    }

    private static final SimpleEntry<String, Object> property(final String name, final Object value) {
        return name.equalsIgnoreCase("ignored") ?
                new SimpleEntry<>(name, value) :
                new SimpleEntry<>(String.format(PREFIX_TEMPLATE, name), value);
    }

    @SafeVarargs
    private static final Map<String, Object> properties(final SimpleEntry<String, Object>... entries) {
        return Stream.of(entries)
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    }
}
