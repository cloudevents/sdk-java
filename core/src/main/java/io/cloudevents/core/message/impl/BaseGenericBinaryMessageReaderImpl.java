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

package io.cloudevents.core.message.impl;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import io.cloudevents.rw.CloudEventWriterFactory;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * This class implements a Binary {@link io.cloudevents.core.message.MessageReader},
 * providing common logic to most protocol bindings which supports both Binary and Structured mode.
 * <p>
 * Content-type is handled separately using a key not prefixed with CloudEvents header prefix.
 *
 * @param <HK> Header key type
 * @param <HV> Header value type
 */
public abstract class BaseGenericBinaryMessageReaderImpl<HK, HV> extends BaseBinaryMessageReader {

    private final SpecVersion version;
    private final CloudEventData body;

    protected BaseGenericBinaryMessageReaderImpl(SpecVersion version, CloudEventData body) {
        Objects.requireNonNull(version);
        this.version = version;
        this.body = body;
    }

    @Override
    public <T extends CloudEventWriter<V>, V> V read(CloudEventWriterFactory<T, V> writerFactory, CloudEventDataMapper<? extends CloudEventData> mapper) throws CloudEventRWException, IllegalStateException {
        CloudEventWriter<V> visitor = writerFactory.create(this.version);

        // Grab from headers the attributes and extensions
        // This implementation avoids to use visitAttributes and visitExtensions
        // in order to complete the visit in one loop
        this.forEachHeader((key, value) -> {
            if (isContentTypeHeader(key)) {
                visitor.withAttribute("datacontenttype", toCloudEventsValue(value));
            } else if (isCloudEventsHeader(key)) {
                String name = toCloudEventsKey(key);
                if (name.equals("specversion")) {
                    return;
                }
                if (this.version.getAllAttributes().contains(name)) {
                    visitor.withAttribute(name, toCloudEventsValue(value));
                } else {
                    visitor.withExtension(name, toCloudEventsValue(value));
                }
            }
        });

        // Set the payload
        if (this.body != null) {
            return visitor.end(mapper.map(this.body));
        }

        return visitor.end();
    }

    protected abstract boolean isContentTypeHeader(HK key);

    protected abstract boolean isCloudEventsHeader(HK key);

    protected abstract String toCloudEventsKey(HK key);

    protected abstract void forEachHeader(BiConsumer<HK, HV> fn);

    protected abstract String toCloudEventsValue(HV value);

}
