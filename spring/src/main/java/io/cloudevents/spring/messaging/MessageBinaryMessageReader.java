/*
 * Copyright 2020-Present The CloudEvents Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.spring.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.messaging.MessageHeaders;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;

import static io.cloudevents.spring.messaging.CloudEventsHeaders.CE_PREFIX;
import static io.cloudevents.spring.messaging.CloudEventsHeaders.CONTENT_TYPE;

/**
 * Utility for converting maps (message headers) to `CloudEvent` contexts.
 *
 * @author Dave Syer
 *
 */
class MessageBinaryMessageReader extends BaseGenericBinaryMessageReaderImpl<String, Object> {

	private final Map<String, Object> headers = new HashMap<>();

	public MessageBinaryMessageReader(SpecVersion version, Map<String, Object> headers, byte[] payload) {
		super(version, payload == null ? null : BytesCloudEventData.wrap(payload));
		this.headers.putAll(headers);
        if (headers.containsKey(MessageHeaders.CONTENT_TYPE) && !headers.containsKey(CONTENT_TYPE)) {
            this.headers.put(CONTENT_TYPE, headers.get(MessageHeaders.CONTENT_TYPE));
        }
	}

	public MessageBinaryMessageReader(SpecVersion version, Map<String, Object> headers) {
		this(version, headers, null);
	}

	@Override
	protected boolean isContentTypeHeader(String key) {
		return CONTENT_TYPE.equalsIgnoreCase(key);
	}

	@Override
	protected boolean isCloudEventsHeader(String key) {
		return key != null && key.length() > 3
				&& key.substring(0, CE_PREFIX.length()).toLowerCase().startsWith(CE_PREFIX);
	}

	@Override
	protected String toCloudEventsKey(String key) {
		return key.substring(CE_PREFIX.length()).toLowerCase();
	}

	@Override
	protected void forEachHeader(BiConsumer<String, Object> fn) {
		headers.forEach((k, v) -> fn.accept(k, v));
	}

	@Override
	protected String toCloudEventsValue(Object value) {
		if (value instanceof byte[]) {
			return new String((byte[])value);
		}
		return value.toString();
	}

}
