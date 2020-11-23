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

package io.cloudevents.core.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.rw.*;

public class CloudEventReaderAdapter implements CloudEventReader, CloudEventContextReader {

    private final CloudEvent event;

    private final CloudEventContextReader context;

    public CloudEventReaderAdapter(CloudEvent event) {
        this.event = event;
        this.context = new CloudEventContextReaderAdapter(event);
    }

    @Override
    public <V extends CloudEventWriter<R>, R> R read(CloudEventWriterFactory<V, R> writerFactory, CloudEventDataMapper<? extends CloudEventData> mapper) throws RuntimeException {
        CloudEventWriter<R> visitor = writerFactory.create(event.getSpecVersion());
        this.readAttributes(visitor);
        this.readExtensions(visitor);

        if (event.getData() != null) {
            return visitor.end(mapper.map(event.getData()));
        }

        return visitor.end();
    }

    @Override
    public void readAttributes(CloudEventAttributesWriter writer) throws RuntimeException {
        context.readAttributes(writer);
    }

    @Override
    public void readExtensions(CloudEventExtensionsWriter writer) throws RuntimeException {
        context.readExtensions(writer);
    }

}
