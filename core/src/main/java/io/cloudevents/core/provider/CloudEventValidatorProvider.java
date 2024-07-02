/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.cloudevents.core.provider;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.validator.CloudEventValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

/**
 * CloudEventValidatorProvider is a singleton class which loads and access CE Validator service providers on behalf of service clients.
 */
public class CloudEventValidatorProvider {

    private static final CloudEventValidatorProvider cloudEventValidatorProvider = new CloudEventValidatorProvider();

    private final Collection<CloudEventValidator> validators;

    private CloudEventValidatorProvider() {
        final ServiceLoader<CloudEventValidator> loader = ServiceLoader.load(CloudEventValidator.class);
        this.validators = new ArrayList<>(2);
        for (CloudEventValidator cloudEventValidator : loader) {
            validators.add(cloudEventValidator);
        }
    }

    public static CloudEventValidatorProvider getInstance() {
        return cloudEventValidatorProvider;
    }

    /**
     * iterates through available Cloudevent validators.
     *
     * @param cloudEvent event to validate.
     */
    public void validate(CloudEvent cloudEvent) {
        for (final CloudEventValidator validator : validators) {
            validator.validate(cloudEvent);
        }
    }
}
