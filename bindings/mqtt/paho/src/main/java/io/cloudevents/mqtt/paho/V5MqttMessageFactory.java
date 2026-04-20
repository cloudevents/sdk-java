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
package io.cloudevents.mqtt.paho;

import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.rw.CloudEventWriter;
import org.eclipse.paho.mqttv5.common.MqttMessage;

/**
 * MQTT V5 factory to :
 * - Obtain a {@link MessageReader} to read CloudEvents from MQTT messages.
 * - Create a {@link MessageWriter} enabling CloudEVents to be written to an MQTT message.
 */

public final class V5MqttMessageFactory {

    /**
     * Prevent instantiation.
     */
    private V5MqttMessageFactory() {

    }

    /**
     * Create a {@link MessageReader} to read MQTT Messages as CloudEVents
     *
     * @param mqttMessage An MQTT Message.
     * @return {@link MessageReader}
     */
    public static MessageReader createReader(MqttMessage mqttMessage) {

        final String contentType = mqttMessage.getProperties().getContentType();

        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> contentType,
            format -> new GenericStructuredMessageReader(format, mqttMessage.getPayload()),
            () -> PahoMessageUtils.getUserProperty(mqttMessage, CloudEventV1.SPECVERSION),
            sv -> new BinaryMessageReader(sv, contentType, mqttMessage)
        );
    }

    /**
     * Creates a {@link MessageWriter} capable of translating both a structured and binary CloudEvent
     * to an MQTT {@link  MqttMessage}
     *
     * @return A {@link MessageWriter} to write a {@link io.cloudevents.CloudEvent} to MQTT using structured or binary encoding.
     */
    public static MessageWriter<CloudEventWriter<MqttMessage>, MqttMessage> createWriter() {
        return new V5MessageWriter<>();
    }

}
