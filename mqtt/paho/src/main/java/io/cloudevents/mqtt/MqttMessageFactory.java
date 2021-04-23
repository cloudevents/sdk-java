package io.cloudevents.mqtt;

import java.util.List;
import java.util.Objects;

import org.eclipse.paho.mqttv5.common.packet.UserProperty;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventRWException;

/**
 * A factory class providing convenience methods for creating {@link MessageReader} and {@link MessageWriter} instances based on the Paho MQTT library.
 * <p>
 * The MQTT protocol binding spec mandates that MQTTv3 messages be translated to Structured CloudEvent messages while MQTTv3 messages can be translated
 * either to Structured or Binary content modes depending on the payload format of the message.
 */
public class MqttMessageFactory {

    private MqttMessageFactory() {
        // prevent instantiation
    }

    /**
     * Creates a {@link MessageReader} instance for the given MQTTv5 message.
     * <p>
     * The MessageReader is capable of translating the message to either a Structured or Binary CloudEvent.
     * 
     * @param message                An MQTTv5 message.
     * @return                       A new MessageReader instance capable of reading the given message to a (Structured/Binary) CloudEvent.
     * @throws CloudEventRWException if something goes wrong while resolving the {@link SpecVersion} or if the message has an unknown encoding.
     */
    public static MessageReader createReader(final org.eclipse.paho.mqttv5.common.MqttMessage message) {
        return createReader(MqttVersion.MQTTV5, message.getProperties().getContentType(), message.getProperties().getUserProperties(), message.getPayload());
    }

    /**
     * Creates a {@link MessageReader} instance for the given MQTTv3 message.
     * <p>
     * The MessageReader is capable of translating the message to a Structured CloudEvent only.
     * 
     * @param message   An MQTTv3 message.
     * @return          A new MessageReader instance capable of reading the given message to a Structured CloudEvent.
     * @throws CloudEventRWException if something goes wrong while resolving the {@link SpecVersion} or if the message has an unknown encoding.
     */
    public static MessageReader createReader(final org.eclipse.paho.client.mqttv3.MqttMessage message) {
        return createReader(MqttVersion.MQTTV3, null, null, message.getPayload());
    }

    /**
     * Creates a {@link MessageReader} capable of translating an MQTT message to a CloudEvent.
     * <p>
     * The MessageReader is constructed as follows:
     * <ul>
     * <li>If the mqttVersion corresponds to the MQTT version 3 protocol, a MessageReader capable of reading
     * <b>only a Structured</b> CloudEvent is returned.</li>
     * <li>If the mqttVersion corresponds to the MQTT version 5 protocol, a MessageReader capable of reading
     * <b>both a Structured and Binary</b> CloudEvent is returned.</li>
     * </ul>
     *
     * @param  mqttVersion      Either {@link MqttVersion#MQTTV3} or {@link MqttVersion#MQTTV5} to create a message reader based on
     *                          an MQTT protocol version.
     * @param  contentType      The content type of the message payload.
     * @param  userProperties   In the case of {@link MqttVersion#MQTTV5}, the list of User Properties for an MQTT PUBLISH packet.
     * @param  payload          The payload of the MQTT message. The payload should be encoded according to the specified content type property.
     * @return                  A new message reader instance.
     * @throws CloudEventRWException if something goes wrong while resolving the {@link SpecVersion} or if the message has unknown encoding.
     * @throws NullPointerException if the mqttVersion is {@code null}.
     */
    public static MessageReader createReader(final MqttVersion mqttVersion, final String contentType,
            final List<UserProperty> userProperties, final byte[] payload) throws CloudEventRWException {

        Objects.requireNonNull(mqttVersion, "MQTT Version must not be null");
        return mqttVersion.createReader(contentType, userProperties, payload);
    }
}
