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
