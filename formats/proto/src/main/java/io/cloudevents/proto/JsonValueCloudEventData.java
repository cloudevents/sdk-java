package io.cloudevents.proto;

import io.cloudevents.CloudEventData;

import javax.json.JsonValue;

/**
 * {@link CloudEventData} representing a {@link JsonValue}
 */
public interface JsonValueCloudEventData extends TextCloudEventData {

    /**
     * Obtain the {@link JsonValue} representation
     *
     * @return The {@link JsonValue}
     */
    JsonValue getJsonValue();

}
