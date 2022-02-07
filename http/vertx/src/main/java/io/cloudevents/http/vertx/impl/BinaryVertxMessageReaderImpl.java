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

package io.cloudevents.http.vertx.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.impl.StringUtils;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;

import java.util.Objects;
import java.util.function.BiConsumer;

public class BinaryVertxMessageReaderImpl extends BaseGenericBinaryMessageReaderImpl<String, String> {

    private final MultiMap headers;

    public BinaryVertxMessageReaderImpl(SpecVersion version, MultiMap headers, Buffer body) {
        super(version, body != null && body.length() > 0 ? BytesCloudEventData.wrap(body.getBytes()) : null);

        Objects.requireNonNull(headers);
        this.headers = headers;
    }

    @Override
    protected boolean isContentTypeHeader(String key) {
        return key.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE.toString());
    }

    @Override
    protected boolean isCloudEventsHeader(String key) {
        return key.length() > CloudEventsHeaders.CE_PREFIX.length() && StringUtils.startsWithIgnoreCase(key, CloudEventsHeaders.CE_PREFIX);
    }

    @Override
    protected String toCloudEventsKey(String key) {
        return key.substring(CloudEventsHeaders.CE_PREFIX.length()).toLowerCase();
    }

    @Override
    protected void forEachHeader(BiConsumer<String, String> fn) {
        this.headers.forEach(e -> fn.accept(e.getKey(), e.getValue()));
    }

    @Override
    protected String toCloudEventsValue(String value) {
        return value;
    }
}
