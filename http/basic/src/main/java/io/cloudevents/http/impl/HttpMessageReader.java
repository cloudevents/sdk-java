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
 */

package io.cloudevents.http.impl;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.cloudevents.http.impl.CloudEventsHeaders.CE_PREFIX;
import static io.cloudevents.http.impl.CloudEventsHeaders.CONTENT_TYPE;

public class HttpMessageReader extends BaseGenericBinaryMessageReaderImpl<String, String> {

    private final Consumer<BiConsumer<String, String>> forEachHeader;

    public HttpMessageReader(SpecVersion version, Consumer<BiConsumer<String, String>> forEachHeader, CloudEventData body) {
        super(version, body);
        this.forEachHeader = forEachHeader;
    }

    public HttpMessageReader(SpecVersion version, Consumer<BiConsumer<String, String>> forEachHeader, byte[] body) {
        this(version, forEachHeader, body != null && body.length > 0 ? BytesCloudEventData.wrap(body) : null);
    }

    @Override
    protected boolean isContentTypeHeader(String key) {
        return CONTENT_TYPE.equalsIgnoreCase(key);
    }

    @Override
    protected boolean isCloudEventsHeader(String key) {
        return key != null && key.length() > 3 && key.substring(0, CE_PREFIX.length()).toLowerCase().startsWith(CE_PREFIX);
    }

    @Override
    protected String toCloudEventsKey(String key) {
        return key.substring(CE_PREFIX.length()).toLowerCase();
    }

    @Override
    protected void forEachHeader(BiConsumer<String, String> fn) {
        forEachHeader.accept(fn);
    }

    @Override
    protected String toCloudEventsValue(String value) {
        return value;
    }
}
