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

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;

import java.util.ArrayList;
import java.util.List;

class V5MessageWriter<R> implements MessageWriter<CloudEventWriter<MqttMessage>, MqttMessage>, CloudEventWriter<MqttMessage> {

    private final List<UserProperty> userProperties;
    private final MqttMessage message;

    V5MessageWriter() {
        userProperties = new ArrayList<>(10);
        message = new MqttMessage();
        message.setProperties(new MqttProperties());
    }

    // -- Implementation Overrides

    @Override
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
        final UserProperty up = new UserProperty(name, value);
        userProperties.add(up);

        return this;
    }

    @Override
    public MqttMessage end(CloudEventData data) throws CloudEventRWException {
        message.setPayload(data.toBytes());
        return end();
    }

    @Override
    public MqttMessage end() throws CloudEventRWException {
        if (userProperties.size() != 0) {
            message.getProperties().setUserProperties(userProperties);
        }
        return message;
    }

    @Override
    public CloudEventWriter<MqttMessage> create(SpecVersion version) throws CloudEventRWException {
        userProperties.add(new UserProperty("specversion", version.toString()));
        return this;
    }

    @Override
    public MqttMessage setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        message.getProperties().setContentType(format.serializedContentType());
        message.setPayload(value);
        return end();
    }
}
