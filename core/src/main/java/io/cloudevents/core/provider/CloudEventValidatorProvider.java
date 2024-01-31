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

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * CloudEventValidatorProvider is a singleton class which loads and access CE Validator service providers on behalf of service clients.
 */
public class CloudEventValidatorProvider {

    private static CloudEventValidatorProvider cloudEventValidatorProvider;

    private ServiceLoader<CloudEventValidator> loader;

    private CloudEventValidatorProvider(){
        loader = ServiceLoader.load(CloudEventValidator.class);
    }

    public static synchronized CloudEventValidatorProvider getInstance(){
        if(cloudEventValidatorProvider == null){
            cloudEventValidatorProvider = new CloudEventValidatorProvider();
        }
        return cloudEventValidatorProvider;
    }

    /**
     * iterates through available Cloudevent validators.
     * @param cloudEvent
     */
    public void validate(CloudEvent cloudEvent){
        try{
            //
            Iterator<CloudEventValidator> validatorIterator = loader.iterator();
            while (validatorIterator.hasNext()){
                CloudEventValidator validator = validatorIterator.next();
                validator.validate(cloudEvent);

            }
        } catch (ServiceConfigurationError serviceError) {

            serviceError.printStackTrace();
        }

    }





}
