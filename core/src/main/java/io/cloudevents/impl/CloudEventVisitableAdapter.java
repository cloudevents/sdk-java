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

package io.cloudevents.impl;

import io.cloudevents.*;

import java.util.Map;

public class CloudEventVisitableAdapter implements CloudEventVisitable {

    private CloudEvent event;

    CloudEventVisitableAdapter(CloudEvent event) {
        this.event = event;
    }

    @Override
    public <V extends CloudEventVisitor<R>, R> R visit(CloudEventVisitorFactory<V, R> visitorFactory) throws RuntimeException {
        CloudEventVisitor<R> visitor = visitorFactory.create(event.getSpecVersion());
        this.visitAttributes(visitor);
        this.visitExtensions(visitor);

        if (event.getData() != null) {
            visitor.setBody(event.getData());
        }

        return visitor.end();
    }

    @Override
    public void visitAttributes(CloudEventAttributesVisitor visitor) throws RuntimeException {
        visitor.setAttribute("id", event.getId());
        visitor.setAttribute("source", event.getSource());
        visitor.setAttribute("type", event.getType());
        if (event.getDataContentType() != null) {
            visitor.setAttribute("datacontenttype", event.getDataContentType());
        }
        if (event.getDataSchema() != null) {
            visitor.setAttribute("dataschema", event.getDataSchema());
        }
        if (event.getSubject() != null) {
            visitor.setAttribute("subject", event.getSubject());
        }
        if (event.getTime() != null) {
            visitor.setAttribute("time", event.getTime());
        }
    }

    @Override
    public void visitExtensions(CloudEventExtensionsVisitor visitor) throws RuntimeException {
        for (Map.Entry<String, Object> entry : event.getExtensions().entrySet()) {
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
