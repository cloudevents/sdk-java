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

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;

import java.util.List;
import java.util.Optional;

/**
 * General Utility functions
 */
final class PahoMessageUtils {

    /**
     * Prevent Instantiation
     */
    private PahoMessageUtils() {
    }

    /**
     * Get the value of a specific user property from a message.
     *
     * @param msg  The MQTT Message
     * @param name The property to retrieve.
     * @return property value or NULL if not set.
     */
    static String getUserProperty(final MqttMessage msg, final String name) {

        final MqttProperties mProps = msg.getProperties();

        return (mProps == null) ? null : getUserProperty(mProps.getUserProperties(), name);

    }

    /**
     * Get the value of a specific user property from a message.
     *
     * @param props The List of MQTT Message properties
     * @param name  The property to retrieve.
     * @return property value or NULL if not set.
     */
    public static String getUserProperty(final List<UserProperty> props, final String name) {

        if (props == null) {
            return null;
        } else {

            Optional<UserProperty> up = props.stream().filter(p -> p.getKey().equals(name)).findFirst();

            return up.map(UserProperty::getValue).orElse(null);
        }
    }
}
