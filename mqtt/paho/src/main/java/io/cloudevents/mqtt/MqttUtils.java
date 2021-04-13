package io.cloudevents.mqtt;

import java.util.List;
import java.util.Objects;

import org.eclipse.paho.mqttv5.common.packet.UserProperty;

public class MqttUtils {

    /**
     * Gets the value of the user property matching the given property name.
     *
     * @param props The list of user properties.
     * @param name  The name of the user property to retrieve the value for.
     * @return      The value of the user property matching the given name.
     */
    public static String getUserPropertyValue(final List<UserProperty> props, final String name) {
        if (props == null) {
            return null;
        } else {
            final UserProperty prop = props.stream()
            .filter(property -> Objects.equals(property.getKey(), name))
            .findFirst().orElse(null);
            return prop != null ? prop.getValue() : null;
        }
    }
}
