package io.cloudevents.mqtt.paho;

import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.mqtt.core.MqttUtils;
import io.cloudevents.rw.CloudEventWriter;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MQTT V3 factory to :
 * - Obtain a {@link MessageReader} to read CloudEvents from MQTT messages.
 * - Create a {@link MessageWriter} enabling CloudEVents to be written to an MQTT message.
 * <p>
 * NOTE: The V3 binding only supports structured messages using a JSON Format.
 */

public final class V3MqttMessageFactory {

    /**
     * Prevent instantiation.
     */
    private V3MqttMessageFactory() {

    }

    /**
     * Create a {@link MessageReader} to read a V3 MQTT Messages as a CloudEVents
     *
     * @param mqttMessage An MQTT Message.
     * @return {@link MessageReader}
     */
    public static MessageReader createReader(MqttMessage mqttMessage) {
        return new GenericStructuredMessageReader(MqttUtils.getDefaultEventFormat(), mqttMessage.getPayload());
    }

    /**
     * Creates a {@link MessageWriter} to write a CloudEvent to an MQTT {@link  MqttMessage}.
     * <p>
     * NOTE: This implementation *only* supports JSON structured format as-per the MQTT binding specification.
     *
     * @return A {@link MessageWriter} to write a {@link io.cloudevents.CloudEvent} to MQTT.
     */
    public static MessageWriter<CloudEventWriter<MqttMessage>, MqttMessage> createWriter() {
        return new V3MessageWriter();
    }

}
