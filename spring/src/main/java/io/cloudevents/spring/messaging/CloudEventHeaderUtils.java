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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;

import org.springframework.messaging.MessageHeaders;

import static io.cloudevents.spring.messaging.CloudEventsHeaders.CE_PREFIX;
import static org.springframework.messaging.MessageHeaders.CONTENT_TYPE;

/**
 * Utility class for copying message headers to and from {@link CloudEventContext}.
 * 
 * @author Dave Syer
 *
 */
public class CloudEventHeaderUtils {

	/**
	 * Helper method for converting {@link MessageHeaders} to a {@link CloudEvent}. The
	 * input headers must represent a valid event in "binary" form, i.e. it must have
	 * headers "ce-id", "ce-specversion" etc.
	 * @param headers the input request headers
	 * @return a {@link CloudEventBuilder} that can be used to create a new
	 * {@link CloudEvent}
	 * 
	 */
	public static CloudEventBuilder fromMap(Map<String, ?> headers) {
		Object value = headers.get(CloudEventsHeaders.SPEC_VERSION);
		SpecVersion version = value == null ? SpecVersion.V1 : SpecVersion.parse(value.toString());
		return CloudEventBuilder
				.fromContext(CloudEventUtils.toEvent(new MapContextMessageReader(version, headers::forEach)));
	}

	/**
	 * Helper method for extracting {@link MessageHeaders} from a {@link CloudEvent}.
	 * @param event the input {@link CloudEvent}
	 * @return the response headers represented by the event
	 */
	public static Map<String, ?> toMap(CloudEvent event) {
		Map<String, Object> headers = new HashMap<>();
		CloudEventUtils.toContextReader(event).readContext(new MapWriter(headers));
		// Probably this should be done in CloudEventContextReaderAdapter
		headers.put(CE_PREFIX + "specversion", event.getSpecVersion().toString());
		return headers;
	}

	private static class MapContextMessageReader extends BaseGenericBinaryMessageReaderImpl<String, Object> {

		private final Consumer<BiConsumer<String, Object>> forEachHeader;

		public MapContextMessageReader(SpecVersion version, Consumer<BiConsumer<String, Object>> forEachHeader) {
			super(version, null);
			this.forEachHeader = forEachHeader;
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

	private static class MapWriter implements CloudEventContextWriter {

		private final Map<String, Object> map;

		public MapWriter(Map<String, Object> map) {
			this.map = map;
		}

		@Override
		public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
			map.put(CE_PREFIX + name, value);
			return this;
		}

	}

}
