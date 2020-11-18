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
package io.cloudevents.core.v1;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.impl.BaseCloudEvent;
import io.cloudevents.rw.CloudEventAttributesWriter;
import io.cloudevents.rw.CloudEventRWException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * CloudEvent implementation for v1.0
 *
 * @author fabiojose
 * @author slinkydeveloper
 */
public final class CloudEventV1 extends BaseCloudEvent {

    /**
     * The name of the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#id">id</a> attribute
     */
    public final static String ID = "id";

    /**
     * The name of the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#source">source</a> attribute
     */
    public final static String SOURCE = "source";

    /**
     * The name of the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#specversion">specversion</a> attribute
     */
    public final static String SPECVERSION = "specversion";

    /**
     * The name of the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#type">type</a> attribute
     */
    public final static String TYPE = "type";

    /**
     * The name of the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#time">time</a> attribute
     */
    public final static String TIME = "time";

    /**
     * The name of the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#dataschema">dataschema</a> attribute
     */
    public final static String DATASCHEMA = "dataschema";

    /**
     * The name of the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#datacontenttype">datacontenttype</a> attribute
     */
    public final static String DATACONTENTTYPE = "datacontenttype";

    /**
     * The name of the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#subject">subject</a> attribute
     */
    public final static String SUBJECT = "subject";

    private final String id;
    private final URI source;
    private final String type;
    private final String datacontenttype;
    private final URI dataschema;
    private final String subject;
    private final OffsetDateTime time;

    public CloudEventV1(String id, URI source,
                        String type, String datacontenttype,
                        URI dataschema, String subject, OffsetDateTime time,
                        CloudEventData data, Map<String, Object> extensions) {
        super(data, extensions);

        this.id = id;
        this.source = source;
        this.type = type;
        this.datacontenttype = datacontenttype;
        this.dataschema = dataschema;
        this.subject = subject;
        this.time = time;
    }

    public SpecVersion getSpecVersion() {
        return SpecVersion.V1;
    }

    public String getId() {
        return id;
    }

    public URI getSource() {
        return source;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getDataContentType() {
        return datacontenttype;
    }

    @Override
    public URI getDataSchema() {
        return dataschema;
    }

    public String getSubject() {
        return subject;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    @Override
    public Object getAttribute(String attributeName) {
        switch (attributeName) {
            case SPECVERSION:
                return getSpecVersion();
            case ID:
                return this.id;
            case SOURCE:
                return this.source;
            case TYPE:
                return this.type;
            case DATACONTENTTYPE:
                return this.datacontenttype;
            case DATASCHEMA:
                return this.dataschema;
            case SUBJECT:
                return this.subject;
            case TIME:
                return this.time;
        }
        throw new IllegalArgumentException("Spec version v1 doesn't have attribute named " + attributeName);
    }

    @Override
    public void readAttributes(CloudEventAttributesWriter writer) throws CloudEventRWException {
        writer.withAttribute(
            ID,
            this.id
        );
        writer.withAttribute(
            SOURCE,
            this.source
        );
        writer.withAttribute(
            TYPE,
            this.type
        );
        if (this.datacontenttype != null) {
            writer.withAttribute(
                DATACONTENTTYPE,
                this.datacontenttype
            );
        }
        if (this.dataschema != null) {
            writer.withAttribute(
                DATASCHEMA,
                this.dataschema
            );
        }
        if (this.subject != null) {
            writer.withAttribute(
                SUBJECT,
                this.subject
            );
        }
        if (this.time != null) {
            writer.withAttribute(
                TIME,
                this.time
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloudEventV1 that = (CloudEventV1) o;
        return Objects.equals(getId(), that.getId()) &&
            Objects.equals(getSource(), that.getSource()) &&
            Objects.equals(getType(), that.getType()) &&
            Objects.equals(datacontenttype, that.getDataContentType()) &&
            Objects.equals(dataschema, that.getDataSchema()) &&
            Objects.equals(getSubject(), that.getSubject()) &&
            Objects.equals(getTime(), that.getTime()) &&
            Objects.equals(getData(), that.getData()) &&
            Objects.equals(this.extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSource(), getType(), datacontenttype, dataschema, getSubject(), getTime(), getData(), this.extensions);
    }

    @Override
    public String toString() {
        return "CloudEvent{" +
            "id='" + id + '\'' +
            ", source=" + source +
            ", type='" + type + '\'' +
            ", datacontenttype='" + datacontenttype + '\'' +
            ", dataschema=" + dataschema +
            ", subject='" + subject + '\'' +
            ", time=" + time +
            ", data=" + getData() +
            ", extensions=" + this.extensions +
            '}';
    }
}
