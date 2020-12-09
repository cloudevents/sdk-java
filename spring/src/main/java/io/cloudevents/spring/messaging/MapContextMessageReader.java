/*
 * Copyright 2019-2019 the original author or authors.
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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;

import static io.cloudevents.spring.messaging.CloudEventsHeaders.CE_PREFIX;
import static org.springframework.messaging.MessageHeaders.CONTENT_TYPE;

/**
 * Utility for converting maps (message headers) to `CloudEvent` contexts.
 * 
 * @author Dave Syer
 *
 */
class MapContextMessageReader extends BaseGenericBinaryMessageReaderImpl<String, Object> {

	private final Consumer<BiConsumer<String, Object>> forEachHeader;

	public MapContextMessageReader(SpecVersion version, Consumer<BiConsumer<String, Object>> forEachHeader,
			CloudEventData data) {
		super(version, data);
		this.forEachHeader = forEachHeader;
	}

	public MapContextMessageReader(SpecVersion version, Consumer<BiConsumer<String, Object>> forEachHeader) {
		this(version, forEachHeader, null);
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
		forEachHeader.accept(fn);
	}

	@Override
	protected String toCloudEventsValue(Object value) {
		return value.toString();
	}

}