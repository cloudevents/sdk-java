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

import io.cloudevents.impl.DefaultCloudEventImpl;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder class to create a Java Object representing a CloudEvent implementation
 * @param <T> type of the data field
 */
public class  CloudEventBuilder<T> {

    private final String cloudEventsVersion = "0.1";
    private Map<?,?> extensions = new LinkedHashMap();
    private String contentType;
    private String eventType;
    private URI source;
    private String eventID;
    private String eventTypeVersion;
    private ZonedDateTime eventTime;
    private URI schemaURL;
    private T data;

    /**
     * Type of occurrence which has happened. Often this property is used for routing, observability, policy enforcement, etc.
     */
    public CloudEventBuilder<T> eventType(final String eventType) {
        this.eventType = eventType;
        return this;
    }

    /**
     * The version of the eventType. This enables the interpretation of data by eventual consumers, requires the consumer to be knowledgeable about the producer.
     */
    public CloudEventBuilder<T> eventTypeVersion(final String eventTypeVersion) {
        this.eventTypeVersion = eventTypeVersion;
        return this;
    }

    /**
     * This describes the event producer. Often this will include information such as the type of the event source, the organization publishing the event, and some unique identifiers.
     * The exact syntax and semantics behind the data encoded in the URI is event producer defined.
     */
    public CloudEventBuilder<T> source(final URI source) {
        this.source = source;
        return this;
    }


    /**
     * ID of the event. The semantics of this string are explicitly undefined to ease the implementation of producers. Enables deduplication.
     */
    public CloudEventBuilder<T> eventID(final String eventID) {
        this.eventID = eventID;
        return this;
    }

    /**
     * Timestamp of when the event happened.
     */
    public CloudEventBuilder<T> eventTime(final ZonedDateTime eventTime) {
        this.eventTime = eventTime;
        return this;
    }

    /**
     * A link to the schema that the data attribute adheres to.
     */
    public CloudEventBuilder<T> schemaURL(final URI schemaURL) {
        this.schemaURL = schemaURL;
        return this;
    }

    /**
     * Describe the data encoding format
     */
    public CloudEventBuilder<T> contentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * This is for additional metadata and this does not have a mandated structure. This enables a place for custom
     * fields a producer or middleware might want to include and provides a place to test metadata before adding them
     * to the CloudEvents specification.
     */
    public CloudEventBuilder<T> extensions(final Map extensions) {
        this.extensions = extensions;
        return this;
    }

    /**
     * The event payload. The payload depends on the eventType, schemaURL and eventTypeVersion, the payload is encoded into a media format which is specified by the contentType attribute (e.g. application/json).
     */
    public CloudEventBuilder<T> data(final T data) {
        this.data = data;
        return this;
    }

    /**
     * Constructs a new {@link CloudEvent} with the previously-set configuration.
     */
    public CloudEvent<T> build() {

        if (eventType == null || cloudEventsVersion == null || source == null || eventID == null) {
            throw new IllegalArgumentException("please provide all required fields");
        }

        return new DefaultCloudEventImpl<T>(eventType, cloudEventsVersion, source, eventID, eventTypeVersion, eventTime, schemaURL, contentType, extensions, data);
    }
}