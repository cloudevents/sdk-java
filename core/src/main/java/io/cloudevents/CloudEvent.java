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
package io.cloudevents;

import io.cloudevents.lang.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

/**
 * An abstract event envelope
 *
 * @author fabiojose
 * @author slinkydeveloper
 */
@ParametersAreNonnullByDefault
public interface CloudEvent extends CloudEventVisitable, CloudEventAttributes {

    /**
     * The event data
     */
    @Nullable
    byte[] getData();

    /**
     * The event extensions
     * <p>
     * Extensions values could be String/Number/Boolean
     */
    Map<String, Object> getExtensions();

    // --- Default implementations for CloudEventVisitable ---
    // Be aware that this implementation assumes the event is SpecVersion v1.
    // If you need to handle other versions, please implement this method by yourself

    @Override
    default <V extends CloudEventVisitor<R>, R> R visit(CloudEventVisitorFactory<V, R> visitorFactory) throws RuntimeException {
        CloudEventVisitor<R> visitor = visitorFactory.create(this.getSpecVersion());
        this.visitAttributes(visitor);
        this.visitExtensions(visitor);

        if (this.getData() != null) {
            visitor.setBody(this.getData());
        }

        return visitor.end();
    }

    @Override
    default void visitAttributes(CloudEventAttributesVisitor visitor) throws RuntimeException {
        visitor.setAttribute("id", this.getId());
        visitor.setAttribute("source", this.getSource());
        visitor.setAttribute("type", this.getType());
        if (this.getDataContentType() != null) {
            visitor.setAttribute("datacontenttype", this.getDataContentType());
        }
        if (this.getDataSchema() != null) {
            visitor.setAttribute("dataschema", this.getDataSchema());
        }
        if (this.getSubject() != null) {
            visitor.setAttribute("subject", this.getSubject());
        }
        if (this.getTime() != null) {
            visitor.setAttribute("time", this.getTime());
        }
    }

    @Override
    default void visitExtensions(CloudEventExtensionsVisitor visitor) throws RuntimeException {
        for (Map.Entry<String, Object> entry : this.getExtensions().entrySet()) {
            if (entry.getValue() instanceof String) {
                visitor.setExtension(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Number) {
                visitor.setExtension(entry.getKey(), (Number) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                visitor.setExtension(entry.getKey(), (Boolean) entry.getValue());
            } else {
                // This should never happen because we build that map only through our builders
                throw new IllegalStateException("Illegal value inside extensions map: " + entry);
            }
        }
        ;
    }
}
