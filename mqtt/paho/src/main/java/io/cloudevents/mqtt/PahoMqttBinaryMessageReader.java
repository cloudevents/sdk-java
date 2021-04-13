package io.cloudevents.mqtt;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.eclipse.paho.mqttv5.common.packet.UserProperty;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;

/**
 * An MQTT implementation of the {@link io.cloudevents.core.message.MessageReader} interface based on the Eclipse Paho MQTT client library.
 * <p>
 * This reader reads sections of an MQTT message to construct a CloudEvent representation by doing the following:
 * <ul>
 *    <li> If the content-type property is set, the value of the property is represented as a cloud event datacontenttype attribute.
 *    <li> If the (mandatory) user properties contains cloud event attributes (and possibly extensions),
 *         this reader will represent each attribute as a cloud event attribute and each.
 * </ul>
 */
public class PahoMqttBinaryMessageReader extends BaseGenericBinaryMessageReaderImpl<String, Object> {

    private final String contentType;
    private final List<UserProperty> userProperties;

    /**
     * Creates an instance of an MQTTv5 message reader.
     * 
     * @param version           The version of the cloud event message.
     * @param userProperties    The user properties of the MQTTv5 message that contains
     *                          the cloud event metadata (i.e attributes and extensions).
     * @param contentType       The content-type property of the message or {@code null} if the content-type is unknown.
     * @param body              The message payload or {@code null} if the message does not contain any payload.
     * 
     * @throws NullPointerException if the user properties is {@code null}.
     */
    public PahoMqttBinaryMessageReader(final SpecVersion version, final List<UserProperty> userProperties, final String contentType, final byte[] body) {
        super(version, body != null && body.length > 0 ? BytesCloudEventData.wrap(body) : null);
        this.contentType = contentType;
        this.userProperties = Objects.requireNonNull(userProperties);
    }

    @Override
    protected boolean isContentTypeHeader(final String key) {
        return key.equals("Content Type");
    }

    @Override
    protected boolean isCloudEventsHeader(final String key) {
        // MQTT binding does not mandate a prefix for attribute names
        return key != null;
    }

    @Override
    protected String toCloudEventsKey(final String key) {
        return key;
    }

    @Override
    protected void forEachHeader(final BiConsumer<String, Object> fn) {
        if (contentType != null) {
            fn.accept("Content Type", contentType);
        }
        userProperties.forEach(property -> {
            fn.accept(property.getKey(), property.getValue());
        });
    }

    @Override
    protected String toCloudEventsValue(final Object value) {
        return value.toString();
    }
}
