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

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * Internal utility class for copying <code>CloudEvent</code> context to a map (message
 * headers).
 * 
 * @author Dave Syer
 *
 */
class MessageBuilderMessageWriter
		implements CloudEventWriter<Message<byte[]>>, MessageWriter<MessageBuilderMessageWriter, Message<byte[]>> {

	private Map<String, Object> headers = new HashMap<>();

	public MessageBuilderMessageWriter(Map<String, Object> headers) {
		this.headers.putAll(headers);
	}

	public MessageBuilderMessageWriter() {
	}

	@Override
	public Message<byte[]> setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
		headers.put(CloudEventsHeaders.CONTENT_TYPE, format.serializedContentType());
		return MessageBuilder.withPayload(value).copyHeaders(headers).build();
	}

	@Override
	public Message<byte[]> end(CloudEventData value) throws CloudEventRWException {
		return MessageBuilder.withPayload(value == null ? new byte[0] : value.toBytes()).copyHeaders(headers).build();
	}

	@Override
	public Message<byte[]> end() {
		return MessageBuilder.withPayload(new byte[0]).copyHeaders(headers).build();
	}

	@Override
	public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
		headers.put(CloudEventsHeaders.CE_PREFIX + name, value);
		return this;
	}

	@Override
	public MessageBuilderMessageWriter create(SpecVersion version) {
		headers.put(CloudEventsHeaders.SPEC_VERSION, version.toString());
		return this;
	}

}