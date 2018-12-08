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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder class to create a Java Object representing a CloudEvent implementation
 * @param <T> type of the data field
 */
public class CloudEventBuilder<T> {

    private String specversion;
    private String contentType;
    private String type;
    private URI source;
    private String id;
    private ZonedDateTime time;
    private URI schemaURL;
    private T data;
    private final List<Extension> extensions = new ArrayList<>();

    /**
     * The version of the CloudEvents specification which the event uses.
     */
    public CloudEventBuilder<T> specVersion(final String specVersion) {
        this.specversion = specVersion;
        return this;
    }

    /**
     * Type of occurrence which has happened. Often this property is used for routing, observability, policy enforcement, etc.
     */
    public CloudEventBuilder<T> type(final String type) {
        this.type = type;
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
    public CloudEventBuilder<T> id(final String id) {
        this.id = id;
        return this;
    }

    /**
     * Timestamp of when the event happened.
     */
    public CloudEventBuilder<T> time(final ZonedDateTime time) {
        this.time = time;
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
     * The event payload. The payload depends on the type and schemaURL, the payload is encoded into a media format which is specified by the contenttype attribute (e.g. application/json).
     */
    public CloudEventBuilder<T> data(final T data) {
        this.data = data;
        return this;
    }

    public CloudEventBuilder<T> extension(final Extension extension) {
        this.extensions.add(extension);
        return this;
    }

    /**
     * Constructs a new {@link CloudEvent} with the previously-set configuration.
     */
    public CloudEvent<T> build() {

        // forcing latest (default) version
        if (specversion == null) {
            specversion = SpecVersion.DEFAULT.toString();
        }

        if (type == null || source == null || id == null) {
            throw new IllegalArgumentException("please provide all required fields");
        }

        return new DefaultCloudEventImpl<T>(type, specversion, source, id, time, schemaURL, contentType, data, extensions);
    }
}
