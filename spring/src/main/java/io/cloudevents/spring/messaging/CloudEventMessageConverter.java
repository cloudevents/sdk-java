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

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;

import java.nio.charset.StandardCharsets;

/**
 * A {@link MessageConverter} that can translate to and from a {@link Message
 * Message&lt;byte[]>} or {@link Message Message&lt;String>} and a {@link CloudEvent}. The
 * {@link CloudEventContext} is canonicalized, with key names given a {@code ce-} prefix in the
 * {@link MessageHeaders}.
 *
 * @author Dave Syer
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
		if (payload instanceof CloudEvent event) {
			return CloudEventUtils.toReader(event).read(new MessageBuilderMessageWriter(headers));
		}
		return null;
	}

	private MessageReader createMessageReader(Message<?> message) {
		return MessageUtils.parseStructuredOrBinaryMessage( //
				() -> contentType(message.getHeaders()), //
				format -> structuredMessageReader(message, format), //
				() -> version(message.getHeaders()), //
				version -> binaryMessageReader(message, version) //
		);
	}

	private String version(MessageHeaders message) {
		if (message.containsKey(CloudEventsHeaders.SPEC_VERSION)) {
			return message.get(CloudEventsHeaders.SPEC_VERSION).toString();
		}
		return null;
	}

	private MessageReader binaryMessageReader(Message<?> message, SpecVersion version) {
		return new MessageBinaryMessageReader(version, message.getHeaders(), getBinaryData(message));
	}

	private MessageReader structuredMessageReader(Message<?> message, EventFormat format) {
		return new GenericStructuredMessageReader(format, getBinaryData(message));
	}

	private String contentType(MessageHeaders message) {
		if (message.containsKey(MessageHeaders.CONTENT_TYPE)) {
			return message.get(MessageHeaders.CONTENT_TYPE).toString();
		}
		if (message.containsKey(CloudEventsHeaders.CONTENT_TYPE)) {
			return message.get(CloudEventsHeaders.CONTENT_TYPE).toString();
		}
		return null;
	}

	private byte[] getBinaryData(Message<?> message) {
		Object payload = message.getPayload();
		if (payload instanceof byte[] bytes) {
			return bytes;
		}
		else if (payload instanceof String string) {
			return string.getBytes(StandardCharsets.UTF_8);
		}
		return null;
	}

}
