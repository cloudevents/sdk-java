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
package io.cloudevents.v03;

import io.cloudevents.CloudEventAttributesVisitor;
import io.cloudevents.CloudEventVisitException;
import io.cloudevents.SpecVersion;
import io.cloudevents.impl.BaseCloudEvent;
import io.cloudevents.lang.Nullable;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * CloudEvent implementation for v0.3
 *
 * @author fabiojose
 * @author slinkydeveloper
 */
public final class CloudEventV03 extends BaseCloudEvent {

    private final String id;
    private final URI source;
    private final String type;
    private final String datacontenttype;
    private final URI schemaurl;
    private final String subject;
    private final ZonedDateTime time;

    public CloudEventV03(String id, URI source, String type,
                         ZonedDateTime time, URI schemaurl,
                         String datacontenttype, String subject,
                         byte[] data, Map<String, Object> extensions) {
        super(data, extensions);

        this.id = id;
        this.source = source;
        this.type = type;

        this.time = time;
        this.schemaurl = schemaurl;
        this.datacontenttype = datacontenttype;
        this.subject = subject;
    }

    public SpecVersion getSpecVersion() {
        return SpecVersion.V03;
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

    public String getDataContentType() {
        return datacontenttype;
    }

    public URI getDataSchema() {
        return schemaurl;
    }

    @Nullable
    public URI getSchemaUrl() {
        return schemaurl;
    }

    public String getSubject() {
        return subject;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    @Override
    public void visitAttributes(CloudEventAttributesVisitor visitor) throws CloudEventVisitException {
        visitor.setAttribute(
            ContextAttributes.ID.name().toLowerCase(),
            this.id
        );
        visitor.setAttribute(
            ContextAttributes.SOURCE.name().toLowerCase(),
            this.source
        );
        visitor.setAttribute(
            ContextAttributes.TYPE.name().toLowerCase(),
            this.type
        );
        if (this.datacontenttype != null) {
            visitor.setAttribute(
                ContextAttributes.DATACONTENTTYPE.name().toLowerCase(),
                this.datacontenttype
            );
        }
        if (this.schemaurl != null) {
            visitor.setAttribute(
                ContextAttributes.SCHEMAURL.name().toLowerCase(),
                this.schemaurl
            );
        }
        if (this.subject != null) {
            visitor.setAttribute(
                ContextAttributes.SUBJECT.name().toLowerCase(),
                this.subject
            );
        }
        if (this.time != null) {
            visitor.setAttribute(
                ContextAttributes.TIME.name().toLowerCase(),
                this.time
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloudEventV03 that = (CloudEventV03) o;
        return Objects.equals(getId(), that.getId()) &&
            Objects.equals(getSource(), that.getSource()) &&
            Objects.equals(getType(), that.getType()) &&
            Objects.equals(datacontenttype, that.datacontenttype) &&
            Objects.equals(schemaurl, that.schemaurl) &&
            Objects.equals(getSubject(), that.getSubject()) &&
            Objects.equals(getTime(), that.getTime()) &&
            Arrays.equals(getData(), that.getData()) &&
            Objects.equals(getExtensions(), that.getExtensions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSource(), getType(), datacontenttype, schemaurl, getSubject(), getTime(), Arrays.hashCode(getData()), getExtensions());
    }

    @Override
    public String toString() {
        return "CloudEvent{" +
            "id='" + id + '\'' +
            ", source=" + source +
            ", type='" + type + '\'' +
            ", datacontenttype='" + datacontenttype + '\'' +
            ", schemaurl=" + schemaurl +
            ", subject='" + subject + '\'' +
            ", time=" + time +
            ", data=" + Arrays.toString(getData()) +
            ", extensions" + getExtensions() +
            '}';
    }
}
