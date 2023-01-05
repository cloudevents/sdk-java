package io.cloudevents.mqtt.paho;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.test.Data;
import io.cloudevents.core.v03.CloudEventV03;
import io.cloudevents.types.Time;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class V5MessageFactoryTest {

    private static final String DATACONTENTTYPE_NULL = null;
    private static final byte[] DATAPAYLOAD_NULL = null;

    private static Stream<Arguments> binaryTestArguments() {

        return Stream.of(
            // V03
            Arguments.of(
                properties(
                    property(CloudEventV03.SPECVERSION, SpecVersion.V03.toString()),
                    property(CloudEventV03.ID, Data.ID),
                    property(CloudEventV03.TYPE, Data.TYPE),
                    property(CloudEventV03.SOURCE, Data.SOURCE.toString())
                ),
                DATACONTENTTYPE_NULL,
                DATAPAYLOAD_NULL,
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
                    property(CloudEventV03.TIME, Time.writeTime(Data.TIME))
                ),
                Data.DATACONTENTTYPE_JSON,
                Data.DATA_JSON_SERIALIZED,
                Data.V03_WITH_JSON_DATA
            )
        );
    }

    private static UserProperty property(String key, String val) {
        return new UserProperty(key, val);
    }

    private static List<UserProperty> properties(final UserProperty... props) {
        return Stream.of(props).collect(Collectors.toList());
    }

    @Test
    public void testWriteBinary() {

        final MqttMessage message = V5MqttMessageFactory.createWriter().writeBinary(Data.V1_MIN);
        Assertions.assertNotNull(message);
    }

    // Test Data

    @Test
    public void testWriteStructured() {
        final MqttMessage message = V5MqttMessageFactory.createWriter().writeStructured(Data.V1_MIN, CSVFormat.INSTANCE);
        Assertions.assertNotNull(message);
    }

    @ParameterizedTest()
    @MethodSource("binaryTestArguments")
    public void testReadBinary(List<UserProperty> userProps, String contentType, byte[] data, CloudEvent ce) {
        MqttMessage msg = new MqttMessage();

        // Populate Properties
        MqttProperties props = new MqttProperties();
        props.setUserProperties(userProps);
        msg.setProperties(props);

        // Populate payload & contentType
        if (data != null) {
            msg.setPayload(data);
        }

        if (contentType != null) {
            msg.getProperties().setContentType(contentType);
        }

        MessageReader reader = V5MqttMessageFactory.createReader(msg);

        Assertions.assertNotNull(reader);
        assertThat(reader.getEncoding()).isEqualTo(Encoding.BINARY);

        CloudEvent newCe = reader.toEvent();

        assertThat(newCe).isEqualTo(ce);

    }

    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void testReadStructured(CloudEvent ce) {


        final String contentType = CSVFormat.INSTANCE.serializedContentType() + "; charset=utf8";
        final byte[] contentPayload = CSVFormat.INSTANCE.serialize(ce);

        // Build the MQTT Message

        MqttMessage m = new MqttMessage();

        MqttProperties props = new MqttProperties();
        props.setContentType(contentType);
        m.setProperties(props);
        m.setPayload(contentPayload);

        // Get a reader
        MessageReader reader = V5MqttMessageFactory.createReader(m);
        Assertions.assertNotNull(reader);
        assertThat(reader.getEncoding()).isEqualTo(Encoding.STRUCTURED);

        // Re-Hydrate the CloudEvent
        CloudEvent newCE = reader.toEvent();
        Assertions.assertNotNull(newCE);

        // And hopefully they match
        assertThat(newCE).isEqualTo(ce);

    }
}
