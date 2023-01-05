package io.cloudevents.mqtt.hivemq;

import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import io.cloudevents.SpecVersion;
import io.cloudevents.mqtt.core.BaseMqttBinaryMessageReader;

import java.util.function.BiConsumer;

final class BinaryMessageReader extends BaseMqttBinaryMessageReader {

    Mqtt5Publish message;

    BinaryMessageReader(final SpecVersion version, final String contentType, Mqtt5Publish message) {
        super(version, contentType, message.getPayloadAsBytes());

        this.message = message;
    }

    @Override
    protected void forEachUserProperty(BiConsumer<String, Object> fn) {

        message.getUserProperties().asList().forEach(up -> {

            final String key = up.getName().toString();
            final String val = up.getValue().toString();

            if (key != null && val != null) {
                fn.accept(key, val);
            }
        });

    }
}
