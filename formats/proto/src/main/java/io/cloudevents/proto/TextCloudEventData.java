package io.cloudevents.proto;

import io.cloudevents.CloudEventData;

/**
 * String/Text {@link CloudEventData}
 */
public interface TextCloudEventData extends CloudEventData {

    /**
     * Obtain the {@link String} representation.
     *
     * @return Textual representation
     */
    String getText();

}
