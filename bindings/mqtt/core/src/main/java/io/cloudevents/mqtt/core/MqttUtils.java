package io.cloudevents.mqtt.core;

import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;

/**
 * General MQTT Utilities and Helpers
 */
public class MqttUtils {

    private MqttUtils() {}

    private  static final String DEFAULT_FORMAT = "application/cloudevents+json";

    /**
     * Obtain the {@link EventFormat} to use when working with MQTT V3
     * messages.
     *
     * @return An event format.
     */
    public static EventFormat getDefaultEventFormat () {

        return EventFormatProvider.getInstance().resolveFormat(DEFAULT_FORMAT);

    }

    /**
     * Get the default content type to assume for MQTT messages.
     * @return A Content-Type
     */
    public static final String getDefaultContentType() {
        return DEFAULT_FORMAT;
    }

}
