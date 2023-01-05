package io.cloudevents.mqtt.hivemq;

import com.hivemq.client.mqtt.datatypes.MqttUtf8String;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3PublishBuilder;
import com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperty;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.mqtt.core.MqttUtils;

import java.util.List;
import java.util.Optional;

/**
 * A factory to obtain:
 * - {@link MessageReader} instances to read CloudEvents from MQTT messages.
 * - {@link MessageWriter} instances to write CloudEvents into MQTT messages.
 *
 */
public class MqttMessageFactory {

    // Prevent Instantiation.
    private MqttMessageFactory() {
    }

    /**
     * Create a {@link MessageReader} for an MQTT V3 message.
     * <p>
     * As-Per MQTT Binding specification this only supports
     * a structured JSON Format message.
     *
     * @param message An MQTT V3 message.
     * @return MessageReader.
     */
    public static MessageReader createReader(Mqtt3Publish message) {
        return new GenericStructuredMessageReader(MqttUtils.getDefaultEventFormat(), message.getPayloadAsBytes());
    }

    /**
     * Create a {@link MessageReader} for an MQTT V5 message
     *
     * @param message An MQTT V5 message.
     * @return A message reader.
     */
    public static MessageReader createReader(Mqtt5Publish message) {

        Optional<MqttUtf8String> cType = message.getContentType();

        String contentType = cType.isPresent() ? cType.get().toString() : null;

        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> contentType,
            format -> new GenericStructuredMessageReader(format, message.getPayloadAsBytes()),
            () -> getSpecVersion(message),
            sv -> new BinaryMessageReader(sv, contentType, message)
        );
    }


    /**
     * Create a {@link MessageWriter} for an MQTT V5 Message.
     *
     * @param builder {@link Mqtt5PublishBuilder.Complete}
     * @return A message writer.
     */
    public static MessageWriter createWriter(Mqtt5PublishBuilder.Complete builder) {
        return new V5MessageWriter(builder);
    }

    /**
     * Create a {@link MessageWriter} for an MQTT V3 Message.
     *
     * Only supports structured messages.
     *
     * @param builder {@link Mqtt3PublishBuilder.Complete}
     * @return A message writer.
     */
    public static MessageWriter createWriter(Mqtt3PublishBuilder.Complete builder) {
        return new V3MessageWriter(builder);
    }


    // -- Private functions

    /**
     * Find the value of the CloudEvent 'specversion' in the MQTT V5 User Properties.
     * @param message An MQTT message.
     * @return spec version attribute content.
     */
    private static String getSpecVersion(Mqtt5Publish message) {

        List<Mqtt5UserProperty> props = (List<Mqtt5UserProperty>) message.getUserProperties().asList();

        Optional<Mqtt5UserProperty> up = props.stream().filter(p -> p.getName().toString().equals(CloudEventV1.SPECVERSION)).findFirst();

        return (up.isPresent()) ? up.get().getValue().toString() : null;

    }

}
