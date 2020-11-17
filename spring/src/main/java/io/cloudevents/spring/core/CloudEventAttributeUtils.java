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

package io.cloudevents.spring.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.cloudevents.CloudEventAttributes;
import io.cloudevents.SpecVersion;

import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/**
 * Miscellaneous utility methods to assist with Cloud Event attributes. Primarily intended
 * for the internal use within Spring-based frameworks or integrations.
 *
 * @author Oleg Zhurakousky
 * @author Dave Syer
 * @since 2.0
 */
public final class CloudEventAttributeUtils {

	private CloudEventAttributeUtils() {
	}

	/**
	 * String value of 'application/cloudevents' mime type.
	 */
	public static String APPLICATION_CLOUDEVENTS_VALUE = "application/cloudevents";

	/**
	 * {@link MimeType} instance representing 'application/cloudevents' mime type.
	 */
	public static MimeType APPLICATION_CLOUDEVENTS = MimeTypeUtils.parseMimeType(APPLICATION_CLOUDEVENTS_VALUE);

	/**
	 * Prefix for attributes.
	 */
	public static String DEFAULT_ATTR_PREFIX = "ce_";

	/**
	 * AMQP attributes prefix.
	 */
	public static String AMQP_ATTR_PREFIX = "cloudEvents:";

	/**
	 * Prefix for attributes.
	 */
	public static String HTTP_ATTR_PREFIX = "ce-";

	/**
	 * Value for 'data' attribute.
	 */
	public static String DATA = "data";

	/**
	 * Make a mutable copy of the input (or just return the input if it is already
	 * mutable).
	 * @param attributes input CloudEventAttributes
	 * @return a mutable instance with the same attributes
	 */
	public static MutableCloudEventAttributes toMutable(CloudEventAttributes attributes) {
		if (attributes instanceof MutableCloudEventAttributes) {
			return (MutableCloudEventAttributes) attributes;
		}
		HashMap<String, Object> headers = new HashMap<>();
		for (String name : attributes.getAttributeNames()) {
			headers.put(name, attributes.getAttribute(name));
		}
		return CloudEventAttributeUtils.toAttributes(headers);
	}

	/**
	 * Will wrap the provided map of headers as {@link MutableCloudEventAttributes}.
	 * @param headers map representing headers
	 * @return instance of {@link MutableCloudEventAttributes}
	 */
	public static MutableCloudEventAttributes toAttributes(Map<String, Object> headers) {
		Map<String, Object> attributes = extractAttributes(headers);
		return new MutableCloudEventAttributes(attributes);
	}

	private static String determinePrefixToUse(Map<String, Object> messageHeaders) {
		Set<String> keys = messageHeaders.keySet();
		if (keys.contains(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + MutableCloudEventAttributes.ID)) {
			return CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX;
		}
		else if (keys.contains(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + MutableCloudEventAttributes.ID)) {
			return CloudEventAttributeUtils.HTTP_ATTR_PREFIX;
		}
		else if (keys.contains(CloudEventAttributeUtils.AMQP_ATTR_PREFIX + MutableCloudEventAttributes.ID)) {
			return CloudEventAttributeUtils.AMQP_ATTR_PREFIX;
		}
		else if (keys.contains("user-agent")) {
			return CloudEventAttributeUtils.HTTP_ATTR_PREFIX;
		}
		return "";
	}

	private static Map<String, Object> extractAttributes(Map<String, Object> headers) {
		String prefix = determinePrefixToUse(headers);
		SpecVersion specVersion = extractSpecVersion(headers, prefix);
		Map<String, Object> result = new HashMap<>();
		for (String name : specVersion.getAllAttributes()) {
			if (headers.containsKey(prefix + name)) {
				result.put(name, headers.get(prefix + name));
			}
		}
		result.put(MutableCloudEventAttributes.SPECVERSION, specVersion);
		if (headers.containsKey(prefix + CloudEventAttributeUtils.DATA)) {
			result.put(CloudEventAttributeUtils.DATA, headers.get(prefix + CloudEventAttributeUtils.DATA));
		}
		return result;
	}

	private static SpecVersion extractSpecVersion(Map<String, Object> headers, String prefix) {
		String key = prefix + MutableCloudEventAttributes.SPECVERSION;
		if (headers.containsKey(key)) {
			Object object = headers.get(key);
			if (object instanceof SpecVersion) {
				return (SpecVersion) object;
			}
			if (object != null) {
				return SpecVersion.parse(object.toString());
			}
		}
		return SpecVersion.V1;
	}

}
