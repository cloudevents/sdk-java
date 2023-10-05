package io.cloudevents.core.v1;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.validator.CloudEventValidator;

public class CustomCloudEventValidatorImpl implements CloudEventValidator {
    @Override
    public void validate(CloudEvent cloudEvent) {

        if (cloudEvent.getExtension("namespace") == null){
            throw new IllegalStateException("Extension 'namespace' cannot be null");
        }

    }
}
