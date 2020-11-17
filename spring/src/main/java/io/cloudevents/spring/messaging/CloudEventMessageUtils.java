/*
 * Copyright 2020-Present The CloudEvents Authors
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

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import io.cloudevents.CloudEventAttributes;
import io.cloudevents.spring.core.CloudEventAttributeUtils;
import io.cloudevents.spring.core.CloudEventAttributesProvider;
import io.cloudevents.spring.core.MutableCloudEventAttributes;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.ContentTypeResolver;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

/**
 * Miscellaneous utility methods to assist with representing Cloud Event as Spring
 * {@link Message} <br>
 * Primarily intended for the internal use within Spring-based frameworks and
 * integrations;
 *
 * @author Oleg Zhurakousky
 * @author Dave Syer
 * @since 2.0
 */
public final class CloudEventMessageUtils {

	private static final ContentTypeResolver contentTypeResolver = new DefaultContentTypeResolver();

	private CloudEventMessageUtils() {

	}

	@SuppressWarnings("unchecked")
	public static Message<?> toBinary(Message<?> inputMessage, MessageConverter messageConverter) {
		Map<String, Object> headers = inputMessage.getHeaders();
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(headers);

		// first check the obvious and see if content-type is `cloudevents`
		if (!attributes.isValidCloudEvent() && headers.containsKey(MessageHeaders.CONTENT_TYPE)) {
			MimeType contentType = contentTypeResolver.resolve(inputMessage.getHeaders());
			if (contentType.getType().equals(CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS.getType()) && contentType
					.getSubtype().startsWith(CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS.getSubtype())) {

				String dataContentType = StringUtils.hasText(attributes.getDataContentType())
						? attributes.getDataContentType() : MimeTypeUtils.APPLICATION_JSON_VALUE;

				String suffix = contentType.getSubtypeSuffix();
				MimeType cloudEventDeserializationContentType = MimeTypeUtils
						.parseMimeType(contentType.getType() + "/" + suffix);
				Message<?> cloudEventMessage = MessageBuilder.fromMessage(inputMessage)
						.setHeader(MessageHeaders.CONTENT_TYPE, cloudEventDeserializationContentType)
						.setHeader(MutableCloudEventAttributes.DATACONTENTTYPE, dataContentType).build();
				Map<String, Object> structuredCloudEvent = (Map<String, Object>) messageConverter
						.fromMessage(cloudEventMessage, Map.class);
				Message<?> binaryCeMessage = buildBinaryMessageFromStructuredMap(structuredCloudEvent,
						inputMessage.getHeaders());
				return binaryCeMessage;
			}
		}
		else if (StringUtils.hasText(attributes.getDataContentType())) {
			return MessageBuilder.fromMessage(inputMessage)
					.setHeader(MessageHeaders.CONTENT_TYPE, attributes.getDataContentType()).build();
		}
		return inputMessage;
	}

	/**
	 * Utility method to assist with creating output attributes. <br>
	 * Typically user by {@link Consumer}. Unlike {@link Function} where framework(s)
	 * internally do that once the function is executed and output is produced, Consumer
	 * does not produce any output, so from the framework perspective it is the end of the
	 * line. However, such Consumer may want to send new Cloud Event (e.g., via HTTP or
	 * some messaging template) and thus still requires generation of output attributes.
	 * @param message instance of input {@link Message}.
	 * @param provider instance of CloudEventAttributesProvider.
	 * @return an instance of {@link CloudEventAttributes} as
	 * {@link MutableCloudEventAttributes}
	 */
	public static MutableCloudEventAttributes getOutputAttributes(Message<?> message,
			CloudEventAttributesProvider provider) {
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(message.getHeaders())
				.setId(message.getHeaders().getId().toString())
				.setType(message.getPayload().getClass().getName().getClass().getName());
		return CloudEventAttributeUtils.toMutable(provider.getOutputAttributes(attributes));
	}

	private static Message<?> buildBinaryMessageFromStructuredMap(Map<String, Object> structuredCloudEvent,
			MessageHeaders originalHeaders) {
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(structuredCloudEvent);
		Object payload = attributes.getAttribute(CloudEventAttributeUtils.DATA);
		if (payload == null) {
			payload = Collections.emptyMap();
		}
		return MessageBuilder.withPayload(payload)
				.copyHeaders(attributes.toMap(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX))
				.copyHeaders(originalHeaders)
				.setHeader(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + MutableCloudEventAttributes.ID,
						attributes.getId())
				.build();
	}

}
