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
