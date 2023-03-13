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

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.mqtt.core.MqttUtils;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * A {@link MessageWriter} that writes an CloudEvent to a V3 MQTT Message.
 * <p>
 * Note: This only supports Structured messages in JSON format as defined
 * by the MQTT CloudEvent binding specification.
 */
class V3MessageWriter implements MessageWriter<CloudEventWriter<MqttMessage>, MqttMessage> {

    private final MqttMessage message;

    V3MessageWriter() {
        message = new MqttMessage();
    }

    /**
     * Ensure the supplied content type is appropriate for V3 messages
     * as-per binding specification.
     * <p>
     * Raises exception if not valid.
     *
     * @param contentType
     */
    private void ensureValidContent(String contentType) {

        if (!MqttUtils.getDefaultContentType().equals(contentType)) {

            throw CloudEventRWException.newOther("MQTT V3 Does not support contentType: " + contentType);

        }
    }

    @Override
    public MqttMessage writeStructured(CloudEvent event, String format) {

        final EventFormat eventFormat = EventFormatProvider.getInstance().resolveFormat(format);

        // Sanity Check
        if (eventFormat == null) {

        }

        return writeStructured(event, eventFormat);
    }

    @Override
    public MqttMessage writeStructured(CloudEvent event, EventFormat format) {
        // Ensure format is valid
        ensureValidContent(format.serializedContentType());
        // Populate the structured format.
        message.setPayload(format.serialize(event));
        // Done.
        return message;
    }

    @Override
    public MqttMessage writeBinary(CloudEvent event) {
        // This operation is not allowed.
        // This should fail
        throw CloudEventRWException.newOther("MQTT V3 Does not support CloudEvent Binary mode");
    }

    @Override
    public CloudEventWriter<MqttMessage> create(SpecVersion version) throws CloudEventRWException {
        return null;
    }

    @Override
    public MqttMessage setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        ensureValidContent(format.serializedContentType());
        message.setPayload(value);
        return message;
    }
}
