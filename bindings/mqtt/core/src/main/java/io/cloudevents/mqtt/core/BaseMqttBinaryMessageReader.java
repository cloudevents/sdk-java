package io.cloudevents.mqtt.core;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;
import io.cloudevents.core.v1.CloudEventV1;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enable the hydration of a CloudEvent in binary mode from an MQTT message.
 * <p>
 * This abstract class provides common behavior across different MQTT
 * client implementations.
 */
public abstract class BaseMqttBinaryMessageReader extends BaseGenericBinaryMessageReaderImpl<String, Object> {

    /**
     * CloudEvent attribute names must match this pattern.
     */
    private static final Pattern CE_ATTR_NAME_REGEX = Pattern.compile("^[a-z\\d]+$");
    private final String contentType;

    /**
     * Initialise the binary message reader.
     * @param version The CloudEvent message version.
     * @param contentType The assigned media content type.
     * @param payload The raw data payload from the MQTT message.
     */
    protected BaseMqttBinaryMessageReader(final SpecVersion version, final String contentType, final byte[] payload) {
        super(version, payload != null && payload.length > 0 ? BytesCloudEventData.wrap(payload) : null);
        this.contentType = contentType;
    }

    // --- Overrides

    @Override
    protected boolean isContentTypeHeader(String key) {
        return false; // The content type is not defined in a user-property
    }

    @Override
    protected boolean isCloudEventsHeader(String key) {

        // The binding specification does not require name prefixing,
        // as such any user-property is a potential CE Context Attribute.
        //
        // If the name complies with CE convention then we'll assume
        // it's a context attribute.
        //
        Matcher m = CE_ATTR_NAME_REGEX.matcher(key);
        return m.matches();
    }

    @Override
    protected String toCloudEventsKey(String key) {
        return key; // No special prefixing occurs in the MQTT binding spec.
    }


    @Override
    protected void forEachHeader(BiConsumer<String, Object> fn) {

        // If there is a content-type then we need set it.
        // Inspired by AMQP/Proton code :-)

        if (contentType != null) {
            fn.accept(CloudEventV1.DATACONTENTTYPE, contentType);
        }

        // Now process each MQTT User Property.
        forEachUserProperty(fn);

    }

    @Override
    protected String toCloudEventsValue(Object value) {
        return value.toString();
    }

    /**
     * Visit each MQTT user-property and invoke the supplied function.
     * @param fn The function to invoke for each MQTT User property.
     */
    protected abstract void forEachUserProperty(BiConsumer<String, Object> fn);
}
