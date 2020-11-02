package io.cloudevents.rw;

import io.cloudevents.CloudEventData;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface to convert a {@link CloudEventData} instance to another one.
 */
@FunctionalInterface
@ParametersAreNonnullByDefault
public interface CloudEventDataMapper {

    /**
     * Map {@code data} to another {@link CloudEventData} instance.
     *
     * @param data the input data
     * @return The new data
     * @throws CloudEventRWException is anything goes wrong while mapping the input data
     */
    CloudEventData map(CloudEventData data) throws CloudEventRWException;

}
