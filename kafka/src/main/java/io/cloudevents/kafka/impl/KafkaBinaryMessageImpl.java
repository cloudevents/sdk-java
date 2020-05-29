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

package io.cloudevents.kafka.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageImpl;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.BiConsumer;

public class KafkaBinaryMessageImpl extends BaseGenericBinaryMessageImpl<String, byte[]> {

    private final Headers headers;

    public KafkaBinaryMessageImpl(SpecVersion version, Headers headers, byte[] payload) {
        super(version, payload);

        Objects.requireNonNull(headers);
        this.headers = headers;
    }

    @Override
    protected boolean isContentTypeHeader(String key) {
        return key.equals(KafkaHeaders.CONTENT_TYPE);
    }

    @Override
    protected boolean isCloudEventsHeader(String key) {
        return key.length() > 3 && key.substring(0, KafkaHeaders.CE_PREFIX.length()).startsWith(KafkaHeaders.CE_PREFIX);
    }

    @Override
    protected String toCloudEventsKey(String key) {
        return key.substring(KafkaHeaders.CE_PREFIX.length()).toLowerCase();
    }

    @Override
    protected void forEachHeader(BiConsumer<String, byte[]> fn) {
        this.headers.forEach(h -> fn.accept(h.key(), h.value()));
    }

    @Override
    protected String toCloudEventsValue(byte[] value) {
        return new String(value, StandardCharsets.UTF_8);
    }
}
