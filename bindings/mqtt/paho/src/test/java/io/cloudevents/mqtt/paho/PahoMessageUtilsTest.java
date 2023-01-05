package io.cloudevents.mqtt.paho;

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PahoMessageUtilsTest {

    @Test
    void verifyPropertyList() {

        List<UserProperty> props = new ArrayList<>(5);

        // Ensure Works with null List
        Assertions.assertNull(PahoMessageUtils.getUserProperty(props, "id"));

        // Ensure works with empty list.
        Assertions.assertNull(PahoMessageUtils.getUserProperty(props, "id"));

        // Create some props
        props = new ArrayList<>(5);
        props.add(new UserProperty("id", "aaa-bbb-ccc"));
        props.add(new UserProperty("specversion", "v1.0"));

        // Ensure Presence
        Assertions.assertEquals("aaa-bbb-ccc", PahoMessageUtils.getUserProperty(props, "id"));

        // Ensure Absence
        Assertions.assertNull(PahoMessageUtils.getUserProperty(props, "scoobydoo"));

    }

    @Test
    void verifyMessageProperties() {

        MqttMessage msg = new MqttMessage();

        // Verify message with no props
        Assertions.assertNull(PahoMessageUtils.getUserProperty(msg, "id"));

        // Create some props
        List<UserProperty> props = null;
        props = new ArrayList<>(5);
        props.add(new UserProperty("id", "aaa-bbb-ccc"));
        props.add(new UserProperty("specversion", "v1.0"));

        msg.setProperties(new MqttProperties());
        msg.getProperties().setUserProperties(props);

        // Ensure Presence
        Assertions.assertEquals("aaa-bbb-ccc", PahoMessageUtils.getUserProperty(msg, "id"));

        // Ensure Absence
        Assertions.assertNull(PahoMessageUtils.getUserProperty(msg, "scoobydoo"));

    }
}
