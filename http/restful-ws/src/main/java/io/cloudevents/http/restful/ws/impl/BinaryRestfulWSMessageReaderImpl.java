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

package io.cloudevents.http.restful.ws.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Objects;
import java.util.function.BiConsumer;

import static io.cloudevents.http.restful.ws.impl.CloudEventsHeaders.CE_PREFIX;

public final class BinaryRestfulWSMessageReaderImpl extends BaseGenericBinaryMessageReaderImpl<String, String> {

    private final MultivaluedMap<String, String> headers;

    public BinaryRestfulWSMessageReaderImpl(SpecVersion version, MultivaluedMap<String, String> headers, byte[] body) {
        super(version, body != null && body.length > 0 ? BytesCloudEventData.wrap(body) : null);

        Objects.requireNonNull(headers);
        this.headers = headers;
    }

    @Override
    protected boolean isContentTypeHeader(String key) {
        return key.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE);
    }

    @Override
    protected boolean isCloudEventsHeader(String key) {
        return key.length() > 3 && key.substring(0, CE_PREFIX.length()).toLowerCase().startsWith(CE_PREFIX);
    }

    @Override
    protected String toCloudEventsKey(String key) {
        return key.substring(CE_PREFIX.length()).toLowerCase();
    }

    @Override
    protected void forEachHeader(BiConsumer<String, String> fn) {
        this.headers.forEach((k, v) -> fn.accept(k, v.get(0)));
    }

    @Override
    protected String toCloudEventsValue(String value) {
        return value;
    }
}
