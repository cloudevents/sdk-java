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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.cloudevents.impl.DefaultCloudEventImpl;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * An abstract event envelope, representing the 0.2 version of the <a href="https://github.com/cloudevents/spec/blob/master/spec.md">CNCF CloudEvent spec</a>.
 *
 */
@JsonDeserialize(as = DefaultCloudEventImpl.class)
public interface CloudEvent<T> {

    /**
     * Type of occurrence which has happened. Often this property is used for routing, observability, policy enforcement, etc.
     */
    String getType();

    /**
     * The version of the CloudEvents specification which the event uses. This enables the interpretation of the context.
     */
    String getSpecVersion();

    /**
     * This describes the event producer. Often this will include information such as the type of the event source, the organization publishing the event, and some unique identifiers.
     * The exact syntax and semantics behind the data encoded in the URI is event producer defined.
     */
    URI getSource();

    /**
     * ID of the event. The semantics of this string are explicitly undefined to ease the implementation of producers. Enables deduplication.
     */
    String getId();

    /**
     * Timestamp of when the event happened.
     */
    Optional<ZonedDateTime> getTime();

    /**
     * A link to the schema that the data attribute adheres to.
     */
    Optional<URI> getSchemaURL();

    /**
     * Describe the data encoding format
     */
    Optional<String> getContentType();

    /**
     * The event payload. The payload depends on the eventType, schemaURL and eventTypeVersion, the payload is encoded into a media format which is specified by the contentType attribute (e.g. application/json).
     */
    Optional<T> getData();

    /**
     *
     */
    Optional<List<Extension>> getExtensions();
}