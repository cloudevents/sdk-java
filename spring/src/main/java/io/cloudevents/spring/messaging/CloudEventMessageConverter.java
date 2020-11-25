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

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.core.CloudEventHeaderUtils;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;

/**
 * A {@link MessageConverter} that can translate to and from a {@link Message
 * Message&lt;byte[]>} and a {@link CloudEvent}. The {@link CloudEventContext} is
 * canonicalized, with key names given a "ce_" prefix in the {@link MessageHeaders}.
 * 
 * @author Dave Syer
 *
 */
public class CloudEventMessageConverter implements MessageConverter {

	@Override
	public Object fromMessage(Message<?> message, Class<?> targetClass) {
		byte[] payload = getBinaryData(message);
		if (CloudEvent.class.isAssignableFrom(targetClass)) {
			CloudEventBuilder builder = CloudEventHeaderUtils.fromMap(message.getHeaders());
			if (payload != null) {
				return builder.withData(payload).build();
			}
			return builder.build();
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

	@Override
	public Message<?> toMessage(Object payload, MessageHeaders headers) {
		if (payload instanceof CloudEvent) {
			CloudEvent event = (CloudEvent) payload;
			CloudEventData data = event.getData();
			byte[] bytes = data == null ? new byte[0] : data.toBytes();
			return MessageBuilder.withPayload(bytes).copyHeaders(headers)
					.copyHeaders(CloudEventHeaderUtils.toMap(event, CloudEventHeaderUtils.DEFAULT_ATTR_PREFIX)).build();
		}
		return null;
	}

}
