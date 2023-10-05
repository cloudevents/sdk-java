package io.cloudevents.core.validator;

import io.cloudevents.CloudEvent;

/**
 * Interface which defines validation for CloudEvents attributes and extensions.
 */
public interface CloudEventValidator {

    /**
     * Validate the attributes of a CloudEvent.
     *
     * @param cloudEvent the CloudEvent to validate
     */
    void validate(CloudEvent cloudEvent);
}
