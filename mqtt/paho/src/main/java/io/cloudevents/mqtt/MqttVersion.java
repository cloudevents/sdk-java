package io.cloudevents.mqtt;

import java.util.List;

import org.eclipse.paho.mqttv5.common.packet.UserProperty;

import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.v1.CloudEventV1;

/**
 * Enumeration of the various MQTT versions specified in the <a href="https://github.com/cloudevents/spec/blob/master/mqtt-protocol-binding.md#3-mqtt-publish-message-mapping">MQTT protocol binding</a>
 * specification.
 */
public enum MqttVersion {
    MQTTV3 {
        @Override
        MessageReader createReader(final String contentType, final List<UserProperty> userProperties, final byte[] payload) {

            // The spec mandates the JSON event format for MQTTv3.
            // Regardless of the content-type, this method will always succeed in creating a MessageReader.
            // However, when the event payload is not a JSON formatted payload, an attempt by a client to use the created
            // reader to read a CloudEvent will result in an EventDeserializationException exception thrown during deserialization.
            final EventFormat jsonFormat = EventFormatProvider.getInstance().resolveFormat("application/cloudevents+json");
            return new GenericStructuredMessageReader(jsonFormat, payload);
        }
    },
    MQTTV5 {
        @Override
        MessageReader createReader(final String contentType, final List<UserProperty> userProperties, final byte[] payload) {
            return MessageUtils.parseStructuredOrBinaryMessage(
                    () -> contentType, 
                    format -> new GenericStructuredMessageReader(format, payload),
                    () -> MqttUtils.getUserPropertyValue(userProperties, CloudEventV1.SPECVERSION),
                    sv -> new PahoMqttBinaryMessageReader(sv, userProperties, contentType, payload));
        }
    };

    /**
     * Creates a new {@link MessageReader} instance that can translate an MQTT message to a CloudEvent.
     * 
     * @param contentType       The content type.
     * @param userProperties    A list of user properties for MQTTv5 or {@code null} for MQTTv3.
     * @param payload           The payload of the MQTT message.
     * @return                  An instance of a MessageReader.
     */
    abstract MessageReader createReader(final String contentType, final List<UserProperty> userProperties, final byte[] payload);
}

