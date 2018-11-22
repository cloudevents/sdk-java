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
package io.cloudevents.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.cloudevents.CloudEvent;

import java.io.Serializable;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Default Java implementation of the CloudEvent API.
 *
 * @param <T> generic type of the underlying data field.
 */
public class DefaultCloudEventImpl<T> implements CloudEvent<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private String cloudEventsVersion = "0.1";
    private Map extensions = null;
    private String eventType = null;
    private URI source = null;
    private String eventID = null;
    private String eventTypeVersion = null;
    private ZonedDateTime eventTime = null;
    private URI schemaURL = null;
    private String contentType = null;
    private T data = null;

    public DefaultCloudEventImpl(final String eventType, final String cloudEventsVersion, final URI source, final String eventID, final String eventTypeVersion, final ZonedDateTime eventTime, final URI schemaURL, final String contentType, final Map extensions, final T data) {
        this.cloudEventsVersion = cloudEventsVersion;
        this.extensions = extensions;
        this.eventType = eventType;
        this.source = source;
        this.eventID = eventID;
        this.eventTypeVersion = eventTypeVersion;
        this.eventTime = eventTime;
        this.schemaURL = schemaURL;
        this.contentType = contentType;
        this.data = data;
    }

    DefaultCloudEventImpl() {
        // no-op
    }

    @Override
    public String getCloudEventsVersion() {
        return cloudEventsVersion;
    }

    @Override
    public Optional<Map> getExtensions() {
        return Optional.ofNullable(extensions);
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public URI getSource() {
        return source;
    }

    @Override
    public String getEventID() {
        return eventID;
    }

    @Override
    public Optional<String> getEventTypeVersion() {
        return Optional.ofNullable(eventTypeVersion);
    }

    @Override
    public Optional<ZonedDateTime> getEventTime() {
        return Optional.ofNullable(eventTime);
    }

    @Override
    public Optional<URI> getSchemaURL() {
        return Optional.ofNullable(schemaURL);
    }

    @Override
    public Optional<String> getContentType() {
        return Optional.ofNullable(contentType);
    }

    @Override
    public Optional<T> getData() {
        return Optional.ofNullable(data);
    }


    // protected setters, used for (JSON) deserialization

    void setCloudEventsVersion(String cloudEventsVersion) {
        this.cloudEventsVersion = cloudEventsVersion;
    }

    void setExtensions(Map extensions) {
        this.extensions = extensions;
    }

    void setEventType(String eventType) {
        this.eventType = eventType;
    }

    void setSource(URI source) {
        this.source = source;
    }

    void setEventID(String eventID) {
        this.eventID = eventID;
    }

    void setEventTypeVersion(String eventTypeVersion) {
        this.eventTypeVersion = eventTypeVersion;
    }

    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    void setEventTime(ZonedDateTime eventTime) {
        this.eventTime = eventTime;
    }

    void setSchemaURL(URI schemaURL) {
        this.schemaURL = schemaURL;
    }

    void setContentType(String contentType) {
        this.contentType = contentType;
    }

    void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DefaultCloudEventImpl{" +
                "cloudEventsVersion='" + cloudEventsVersion + '\'' +
                ", extensions=" + extensions +
                ", eventType='" + eventType + '\'' +
                ", source=" + source +
                ", eventID='" + eventID + '\'' +
                ", eventTypeVersion='" + eventTypeVersion + '\'' +
                ", eventTime=" + eventTime +
                ", schemaURL=" + schemaURL +
                ", contentType='" + contentType + '\'' +
                ", data=" + data +
                '}';
    }
}
