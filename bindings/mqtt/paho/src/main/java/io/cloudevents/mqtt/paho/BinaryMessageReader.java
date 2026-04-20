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

import io.cloudevents.SpecVersion;
import io.cloudevents.mqtt.core.BaseMqttBinaryMessageReader;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

final class BinaryMessageReader extends BaseMqttBinaryMessageReader {

    private final List<UserProperty> userProperties;

    BinaryMessageReader(final SpecVersion version, final String contentType, MqttMessage message) {
        super(version, contentType, message.getPayload());

        // Sanity Check
        if (message.getProperties().getUserProperties() != null) {
            userProperties = message.getProperties().getUserProperties();
        } else {
            userProperties = Collections.emptyList();
        }
    }

    @Override
    protected void forEachUserProperty(BiConsumer<String, Object> fn) {

        userProperties.forEach(up -> {

            final String key = up.getKey();
            final String val = up.getValue();

            if (key != null && val != null) {
                fn.accept(key, val);
            }
        });

    }
}
