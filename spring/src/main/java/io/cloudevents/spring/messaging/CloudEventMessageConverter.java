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

import java.nio.charset.Charset;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.StructuredMessageWriter;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;
import io.cloudevents.core.message.impl.BaseStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.rw.CloudEventRWException;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;

/**
 * A {@link MessageConverter} that can translate to and from a {@link Message
 * Message&lt;byte[]>} or {@link Message Message&lt;String>} and a {@link CloudEvent}. The
 * {@link CloudEventContext} is canonicalized, with key names given a "ce-" prefix in the
 * {@link MessageHeaders}.
 * 
 * @author Dave Syer
 *
 */
public class CloudEventMessageConverter implements MessageConverter {

	@Override
	public Object fromMessage(Message<?> message, Class<?> targetClass) {
		if (CloudEvent.class.isAssignableFrom(targetClass)) {
			return createMessageReader(message).toEvent();
		}
		return null;
	}

	@Override
	public Message<?> toMessage(Object payload, MessageHeaders headers) {
		if (payload instanceof CloudEvent) {
			CloudEvent event = (CloudEvent) payload;
			CloudEventData data = event.getData();
			byte[] bytes = data == null ? new byte[0] : data.toBytes();
			return MessageBuilder.withPayload(bytes).copyHeaders(headers)
					.copyHeaders(CloudEventHeaderUtils.toMap(event)).build();
		}
		return null;
	}

	private MessageReader createMessageReader(Message<?> message) {
		Supplier<String> contentTypeHeaderReader = () -> contentType(message);
		Function<EventFormat, MessageReader> structuredMessageFactory = format -> structuredMessageReader(message,
				format);
		Supplier<String> specVersionHeaderReader = () -> version(message);
		Function<SpecVersion, MessageReader> binaryMessageFactory = version -> binaryMessageReader(message, version);
		return MessageUtils.parseStructuredOrBinaryMessage(contentTypeHeaderReader, structuredMessageFactory,
				specVersionHeaderReader, binaryMessageFactory);
	}

	private String version(Message<?> message) {
		if (message.getHeaders().containsKey(CloudEventsHeaders.SPEC_VERSION)) {
			return message.getHeaders().get(CloudEventsHeaders.SPEC_VERSION).toString();
		}
		return null;
	}

	private MessageReader binaryMessageReader(Message<?> message, SpecVersion version) {
		return new BaseGenericBinaryMessageReaderImpl<String, Object>(version,
				BytesCloudEventData.wrap(getBinaryData(message))) {

			@Override
			protected boolean isContentTypeHeader(String key) {
				return MessageHeaders.CONTENT_TYPE.equals(key) || CloudEventsHeaders.CONTENT_TYPE.equals(key);
			}

			@Override
			protected boolean isCloudEventsHeader(String key) {
				return key.startsWith(CloudEventsHeaders.CE_PREFIX);
			}

			@Override
			protected String toCloudEventsKey(String key) {
				return isCloudEventsHeader(key) ? key.substring(CloudEventsHeaders.CE_PREFIX.length()) : key;
			}

			@Override
			protected void forEachHeader(BiConsumer<String, Object> fn) {
				message.getHeaders().forEach((k, v) -> fn.accept(k, v));
			}

			@Override
			protected String toCloudEventsValue(Object value) {
				return value.toString();
			}
		};
	}

	private MessageReader structuredMessageReader(Message<?> message, EventFormat format) {
		return new BaseStructuredMessageReader() {

			@Override
			public <T> T read(StructuredMessageWriter<T> visitor) throws CloudEventRWException, IllegalStateException {
				return visitor.setEvent(format, getBinaryData(message));
			}
		};
	}

	private String contentType(Message<?> message) {
		if (message.getHeaders().containsKey(MessageHeaders.CONTENT_TYPE)) {
			return message.getHeaders().get(MessageHeaders.CONTENT_TYPE).toString();
		}
		if (message.getHeaders().containsKey(CloudEventsHeaders.CONTENT_TYPE)) {
			return message.getHeaders().get(CloudEventsHeaders.CONTENT_TYPE).toString();
		}
		return null;
	}

	private byte[] getBinaryData(Message<?> message) {
		Object payload = message.getPayload();
		if (payload instanceof byte[]) {
			return (byte[]) payload;
		}
		else if (payload instanceof String) {
			return ((String) payload).getBytes(Charset.defaultCharset());
		}
		return null;
	}

}
