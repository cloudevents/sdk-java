/**
 * Copyright 2018 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.beans;

import io.cloudevents.CloudEvent;
import io.cloudevents.cdi.EventTypeQualifier;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import java.net.URI;
import java.util.UUID;

public class Router {

    @Inject
    private Event<CloudEvent<AttributesImpl, MyCustomEvent>> cloudEvent;

    public void routeMe() throws Exception {

        CloudEvent<AttributesImpl, MyCustomEvent> event = 
        	CloudEventBuilder.<MyCustomEvent>builder()
                .withType("Cloud.Storage.Item.Created")
                .withSource(new URI("/trigger"))
                .withId(UUID.randomUUID().toString())
                .build();

        cloudEvent.select(
                new EventTypeQualifier("Cloud.Storage.Item.Created"))
                .fire(event);
    }
}
