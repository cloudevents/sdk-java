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
package io.cloudevents.v1;

import io.cloudevents.Attributes;
import io.cloudevents.SpecVersion;
import io.cloudevents.impl.AttributesInternal;
import io.cloudevents.message.BinaryMessageAttributesVisitor;
import io.cloudevents.message.MessageVisitException;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author fabiojose
 * @author slinkydeveloper
 * @version 1.0
 */
public final class AttributesImpl implements AttributesInternal {

    private final String id;
    private final URI source;
    private final String type;
    private final String datacontenttype;
    private final URI dataschema;
    private final String subject;
    private final ZonedDateTime time;

    public AttributesImpl(String id, URI source,
                          String type, String datacontenttype,
                          URI dataschema, String subject, ZonedDateTime time) {

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

    public ZonedDateTime getTime() {
        return time;
    }

    @Override
    public Attributes toV03() {
        return new io.cloudevents.v03.AttributesImpl(
            this.id,
            this.source,
            this.type,
            this.time,
            this.dataschema,
            this.datacontenttype,
            this.subject
        );
    }

    @Override
    public Attributes toV1() {
        return this;
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
        if (this.dataschema != null) {
            visitor.setAttribute(
                ContextAttributes.DATASCHEMA.name().toLowerCase(),
                this.dataschema
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
    public String toString() {
        return "Attibutes V1.0 [id=" + id + ", source=" + source
            + ", type=" + type
            + ", datacontenttype=" + datacontenttype + ", dataschema="
            + dataschema + ", subject=" + subject
            + ", time=" + time + "]";
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
            Objects.equals(dataschema, that.dataschema) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, source, type, datacontenttype, dataschema, subject, time);
    }
}
