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
