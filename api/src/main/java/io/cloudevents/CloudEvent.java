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
package io.cloudevents;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * An abstract event envelope, representing the 0.1 version of the <a href="https://github.com/cloudevents/spec/blob/master/spec.md">CNCF CloudEvent spec</a>.
 *
 */
public interface CloudEvent<T> {

    // required
    String EVENT_TYPE_KEY = "ce-eventType";
    String CLOUD_EVENTS_VERSION_KEY = "ce-cloudEventsVersion";
    String SOURCE_KEY = "ce-source";
    String EVENT_ID_KEY = "ce-eventID";

    // none-required
    String EVENT_TYPE_VERSION_KEY = "ce-eventTypeVersion";
    String EVENT_TIME_KEY = "ce-eventTime";
    String SCHEMA_URL_KEY = "ce-schemaURL";
    String HEADER_PREFIX = "ce-x-";

    /**
     * Type of occurrence which has happened. Often this property is used for routing, observability, policy enforcement, etc.
     */
    String getEventType();

    /**
     * The version of the CloudEvents specification which the event uses. This enables the interpretation of the context.
     */
    String getCloudEventsVersion();

    /**
     * This describes the event producer. Often this will include information such as the type of the event source, the organization publishing the event, and some unique identifiers.
     * The exact syntax and semantics behind the data encoded in the URI is event producer defined.
     */
    URI getSource();

    /**
     * ID of the event. The semantics of this string are explicitly undefined to ease the implementation of producers. Enables deduplication.
     */
    String getEventID();

    /**
     * The version of the eventType. This enables the interpretation of data by eventual consumers, requires the consumer to be knowledgeable about the producer.
     */
    Optional<String> getEventTypeVersion();
    /**
     * Timestamp of when the event happened.
     */
    Optional<ZonedDateTime> getEventTime();

    /**
     * A link to the schema that the data attribute adheres to.
     */
    Optional<URI> getSchemaURL();

    /**
     * Describe the data encoding format
     */
    Optional<String> getContentType();

    /**
     * This is for additional metadata and this does not have a mandated structure. This enables a place for custom fields a producer or middleware might want to include and provides a place to test metadata before adding them to the CloudEvents specification.
     */
    Optional<Map> getExtensions();

    /**
     * The event payload. The payload depends on the eventType, schemaURL and eventTypeVersion, the payload is encoded into a media format which is specified by the contentType attribute (e.g. application/json).
     */
    Optional<T> getData();
}