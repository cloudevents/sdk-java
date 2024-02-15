package io.cloudevents.core.test;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.validator.CloudEventValidator;

public class CloudEventCustomValidator implements CloudEventValidator {

    @Override
    public void validate(CloudEvent cloudEvent) {
        String namespace = null;
        if ((namespace = (String) cloudEvent.getExtension("namespace")) != null &&
            !namespace.equals("sales")){
            throw new IllegalStateException("Expecting sales in namespace extension");
        }
    }

}
