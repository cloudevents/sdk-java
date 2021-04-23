package io.cloudevents.mqtt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.util.Arrays;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.test.Data;
import io.cloudevents.core.v03.CloudEventV03;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.types.Time;

/**
 * Tests verifying the behavior of <em>MqttMessageFactory</em>.
 */
public class PahoMqttMessageFactoryTest {

    private static final String DATACONTENTTYPE_NULL = null;
    /*
     * The Paho MQTT client library throws a NullpointerException when
     * the message payload is null. As a workaround, we pass in an empty message payload
     * instead of null.
     */
    private static final byte[] DATAPAYLOAD_EMPTY = "".getBytes();

    /**
     * Verifies that an MQTTv5 message can be successfully read as a CloudEvent in Binary mode.
     * 
     * @param event     The expected CloudEvent message.
     * @param message   The MQTTv5 message to be read as a CloudEvent.
     */
    @ParameterizedTest()
    @MethodSource("binaryTestArguments")
    public void testReadBinaryMQTTv5(final CloudEvent event, final MqttMessage message) {     
        // GIVEN an MQTTv5 message passed as argument

        // WHEN a message reader is created for the MQTT message
        final MessageReader mqttReader = MqttMessageFactory.createReader(message);

        assertThat(mqttReader.getEncoding()).isEqualTo(Encoding.BINARY);
        assertThat(mqttReader.toEvent()).isEqualTo(event);
    }

    /**
     * Verifies that an MQTTv5 message can be successfully read as a CloudEvent in Structured mode.
     * 
     * @param event   The expected CloudEvent message.
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void readStructuredMQTTv5(final CloudEvent event) {
        // GIVEN an MQTTv5 message containing a CSV formatted payload.
        final CSVFormat format = new CSVFormat();
        final String contentType = format.serializedContentType() + "; charset=UTF-8";
        final byte[] payload =  format.serialize(event);

        // WHEN a message reader is created for the MQTT message
        final MessageReader mqttReader = MqttMessageFactory.createReader(MqttVersion.MQTTV5, contentType, null, payload);

        // THEN the reader's encoding mode for a CSV format is STRUCTURED
        assertThat(mqttReader.getEncoding()).isEqualTo(Encoding.STRUCTURED);
        assertThat(mqttReader.toEvent()).isEqualTo(event);
    }

    /**
     * 
     * @param event   The expected CloudEvent message.
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void readStructuredMQTTv3(final CloudEvent event) {
        // GIVEN an MQTTv3 message containing a JSON formatted event payload
        final JsonFormat eventFormat = new JsonFormat();
        final byte[] payload =  eventFormat.serialize(event);
        final org.eclipse.paho.client.mqttv3.MqttMessage message = new org.eclipse.paho.client.mqttv3.MqttMessage(payload);

        // WHEN a message reader is created for the MQTT message
        final MessageReader mqttReader = MqttMessageFactory.createReader(message);

        // THEN  (1) the reader's encoding mode is STRUCTURED
        assertThat(mqttReader.getEncoding()).isEqualTo(Encoding.STRUCTURED); // content-mode is always structured
        //       (2) and can translate the mqtt message to a structured CloudEvent
        assertThat(mqttReader.toEvent(inputData -> BytesCloudEventData.wrap(inputData.toBytes()))).isEqualTo(event);
    }

    /**
     * Verifies that an attempt to read a CloudEvent message using an MQTTv3 MessageReader 
     * created with a CSV event format and payload fails with an <em>EventDeserializationException</em> exception.
     */
    @Test
    public void testReadingCloudEventFailsDeserializationExceptionMqttV3MessageReader() {
        // GIVEN an MQTTv3 message reader created with a CSV formatted payload
        final String contentType = "application/cloudevents+csv";
        final byte[] csvPayload = "a,b,c,d".getBytes();
        final MessageReader mqttV3Reader = MqttMessageFactory.createReader(MqttVersion.MQTTV3, contentType, null, csvPayload);
        assertThat(mqttV3Reader.getEncoding()).isEqualTo(Encoding.STRUCTURED);

        // WHEN the reader reads a CloudEvent message
        assertThrows(EventDeserializationException.class, () -> {
            // THEN the reader fails with an EventDeserializationException exception.
            mqttV3Reader.toEvent();
        });
    }

    //------------------------<  private methods  >---
    
    private static Stream<Arguments> binaryTestArguments() {
        // V03
        return Stream.of(
                Arguments.of(
                        Data.V03_MIN,
                         message(
                             DATAPAYLOAD_EMPTY,
                             properties(
                                 DATACONTENTTYPE_NULL,
                                 new UserProperty(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                                 new UserProperty(CloudEventV03.ID, Data.ID),
                                 new UserProperty(CloudEventV03.TYPE, Data.TYPE),
                                 new UserProperty(CloudEventV03.SOURCE, Data.SOURCE.toString())                             
                             )
                         )
                ),
                Arguments.of(
                        Data.V03_WITH_JSON_DATA,
                         message(
                             Data.DATA_JSON_SERIALIZED,
                             properties(
                                 Data.DATACONTENTTYPE_JSON,
                                 new UserProperty(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                                 new UserProperty(CloudEventV03.ID, Data.ID),
                                 new UserProperty(CloudEventV03.TYPE, Data.TYPE),
                                 new UserProperty(CloudEventV03.SOURCE, Data.SOURCE.toString()),
                                 new UserProperty(CloudEventV03.SCHEMAURL, Data.DATASCHEMA.toString()),
                                 new UserProperty(CloudEventV03.SUBJECT, Data.SUBJECT),
                                 new UserProperty(CloudEventV03.TIME, Time.writeTime(Data.TIME))
                             )
                         )
                ),
                Arguments.of(
                        Data.V03_WITH_JSON_DATA_WITH_EXT_STRING,
                         message(
                             Data.DATA_JSON_SERIALIZED,
                             properties(
                                 Data.DATACONTENTTYPE_JSON,
                                 new UserProperty(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                                 new UserProperty(CloudEventV03.ID, Data.ID),
                                 new UserProperty(CloudEventV03.TYPE, Data.TYPE),
                                 new UserProperty(CloudEventV03.SOURCE, Data.SOURCE.toString()),
                                 new UserProperty(CloudEventV03.SCHEMAURL, Data.DATASCHEMA.toString()),
                                 new UserProperty(CloudEventV03.SUBJECT, Data.SUBJECT),
                                 new UserProperty(CloudEventV03.TIME, Time.writeTime(Data.TIME)),
                                 new UserProperty("astring", "aaa"),
                                 new UserProperty("aboolean", "true"),
                                 new UserProperty("anumber", "10")
                             )
                         )
                ),
                Arguments.of(
                        Data.V03_WITH_XML_DATA,
                         message(
                             Data.DATA_XML_SERIALIZED,
                             properties(
                                 Data.DATACONTENTTYPE_XML,
                                 new UserProperty(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                                 new UserProperty(CloudEventV03.ID, Data.ID),
                                 new UserProperty(CloudEventV03.TYPE, Data.TYPE),
                                 new UserProperty(CloudEventV03.SOURCE, Data.SOURCE.toString()),
                                 new UserProperty(CloudEventV03.SUBJECT, Data.SUBJECT),
                                 new UserProperty(CloudEventV03.TIME, Time.writeTime(Data.TIME))
                             )
                         )
                ),
                Arguments.of(
                        Data.V03_WITH_TEXT_DATA,
                         message(
                             Data.DATA_TEXT_SERIALIZED,
                             properties(
                                 Data.DATACONTENTTYPE_TEXT,
                                 new UserProperty(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                                 new UserProperty(CloudEventV03.ID, Data.ID),
                                 new UserProperty(CloudEventV03.TYPE, Data.TYPE),
                                 new UserProperty(CloudEventV03.SOURCE, Data.SOURCE.toString()),
                                 new UserProperty(CloudEventV03.SUBJECT, Data.SUBJECT),
                                 new UserProperty(CloudEventV03.TIME, Time.writeTime(Data.TIME))
                             )
                         )
                ),
                // V1
                Arguments.of(
                       Data.V1_MIN,
                        message(
                            DATAPAYLOAD_EMPTY,
                            properties(
                                DATACONTENTTYPE_NULL,
                                new UserProperty(CloudEventV1.SPECVERSION, SpecVersion.V1.toString()),
                                new UserProperty(CloudEventV1.ID, Data.ID),
                                new UserProperty(CloudEventV1.TYPE, Data.TYPE),
                                new UserProperty(CloudEventV1.SOURCE, Data.SOURCE.toString())
                            )
                        )
                ),
                Arguments.of(
                        Data.V1_WITH_JSON_DATA,
                        message(
                            Data.DATA_JSON_SERIALIZED,
                            properties(
                                Data.DATACONTENTTYPE_JSON,
                                new UserProperty(CloudEventV1.SPECVERSION, SpecVersion.V1.toString()),
                                new UserProperty(CloudEventV1.ID, Data.ID),
                                new UserProperty(CloudEventV1.TYPE, Data.TYPE),
                                new UserProperty(CloudEventV1.SOURCE, Data.SOURCE.toString()),
                                new UserProperty(CloudEventV1.DATASCHEMA, Data.DATASCHEMA.toString()),
                                new UserProperty(CloudEventV1.SUBJECT, Data.SUBJECT),
                                new UserProperty(CloudEventV1.TIME, Time.writeTime(Data.TIME))
                            )
                        )
                ),
                Arguments.of(
                        Data.V1_WITH_JSON_DATA_WITH_EXT_STRING,
                        message(
                            Data.DATA_JSON_SERIALIZED,
                            properties(
                                Data.DATACONTENTTYPE_JSON,
                                new UserProperty(CloudEventV1.SPECVERSION, SpecVersion.V1.toString()),
                                new UserProperty(CloudEventV1.ID, Data.ID),
                                new UserProperty(CloudEventV1.TYPE, Data.TYPE),
                                new UserProperty(CloudEventV1.SOURCE, Data.SOURCE.toString()),
                                new UserProperty(CloudEventV1.SUBJECT, Data.SUBJECT),
                                new UserProperty(CloudEventV1.DATASCHEMA, Data.DATASCHEMA.toString()),
                                new UserProperty(CloudEventV1.TIME, Time.writeTime(Data.TIME)),
                                new UserProperty("astring", "aaa"),
                                new UserProperty("aboolean", "true"),
                                new UserProperty("anumber", "10")
                            )
                        )
                ),
                Arguments.of(
                        Data.V1_WITH_XML_DATA,
                        message(
                            Data.DATA_XML_SERIALIZED,
                            properties(
                                Data.DATACONTENTTYPE_XML,
                                new UserProperty(CloudEventV1.SPECVERSION, SpecVersion.V1.toString()),
                                new UserProperty(CloudEventV1.ID, Data.ID),
                                new UserProperty(CloudEventV1.TYPE, Data.TYPE),
                                new UserProperty(CloudEventV1.SOURCE, Data.SOURCE.toString()),
                                new UserProperty(CloudEventV1.SUBJECT, Data.SUBJECT),
                                new UserProperty(CloudEventV1.TIME, Time.writeTime(Data.TIME))
                            )
                        )
                ),
                Arguments.of(
                        Data.V1_WITH_TEXT_DATA,
                        message(
                            Data.DATA_TEXT_SERIALIZED,
                            properties(
                                Data.DATACONTENTTYPE_TEXT,
                                new UserProperty(CloudEventV1.SPECVERSION, SpecVersion.V1.toString()),
                                new UserProperty(CloudEventV1.ID, Data.ID),
                                new UserProperty(CloudEventV1.TYPE, Data.TYPE),
                                new UserProperty(CloudEventV1.SOURCE, Data.SOURCE.toString()),
                                new UserProperty(CloudEventV1.SUBJECT, Data.SUBJECT),
                                new UserProperty(CloudEventV1.TIME, Time.writeTime(Data.TIME))
                            )
                        )
                )
        );
        
        
    }

    private static final MqttMessage message(final byte[] payload, final MqttProperties properties) {
        final MqttMessage message = new MqttMessage(payload);
        message.setProperties(properties);
        return message;
    }

    private static final MqttProperties properties(final String contentType, final UserProperty... userProperties) {
        final MqttProperties properties = new MqttProperties();
        properties.setContentType(contentType);
        properties.setUserProperties(
                    Arrays.asList(userProperties)
                    .stream()
                    .map(property -> (UserProperty) property)
                    .collect(Collectors.toList())
                );
        return properties;

    }

}
