/*
 * Copyright 2020 The CloudEvents Authors
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

import io.cloudevents.Attributes;
import io.cloudevents.SpecVersion;
import io.cloudevents.impl.AttributesInternal;
import io.cloudevents.lang.Nullable;
import io.cloudevents.message.BinaryMessageAttributesVisitor;
import io.cloudevents.message.MessageVisitException;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * The event attributes implementation for v0.3
 *
 * @author fabiojose
 * @author slinkydeveloper
 */
public final class AttributesImpl implements AttributesInternal {

    private final String id;
    private final URI source;
    private final String type;
    private final String datacontenttype;
    private final URI schemaurl;
    private final String subject;
    private final ZonedDateTime time;

    public AttributesImpl(String id, URI source, String type,
                          ZonedDateTime time, URI schemaurl,
                          String datacontenttype, String subject) {
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
    public void visitAttributes(BinaryMessageAttributesVisitor visitor) throws MessageVisitException {
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
    public Attributes toV03() {
        return this;
    }

    @Override
    public Attributes toV1() {
        return new io.cloudevents.v1.AttributesImpl(
            this.id,
            this.source,
            this.type,
            this.datacontenttype,
            this.schemaurl,
            this.subject,
            this.time
        );
    }

    @Override
    public String toString() {
        return "Attributes V0.3 [id=" + id + ", source=" + source
            + ", specversion=" + SpecVersion.V03 + ", type=" + type
            + ", time=" + time + ", schemaurl=" + schemaurl
            + ", datacontenttype=" + datacontenttype + ", subject="
            + subject + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributesImpl that = (AttributesImpl) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(source, that.source) &&
            Objects.equals(type, that.type) &&
            Objects.equals(datacontenttype, that.datacontenttype) &&
            Objects.equals(schemaurl, that.schemaurl) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, source, type, datacontenttype, schemaurl, subject, time);
    }

}
