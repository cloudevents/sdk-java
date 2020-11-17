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

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventAttributes;
import io.cloudevents.CloudEventContext;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;

import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

/**
 * Miscellaneous utility methods to assist with Cloud Event attributes. Primarily intended
 * for the internal use within Spring-based frameworks or integrations.
 *
 * @author Oleg Zhurakousky
 * @author Dave Syer
 * @since 2.0
 */
public final class CloudEventHeaderUtils {

	private CloudEventHeaderUtils() {
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
	 * Value for 'id' attribute.
	 */
	public static String ID = "id";

	/**
	 * Value for 'source' attribute.
	 */
	public static String SOURCE = "source";

	/**
	 * Value for 'specversion' attribute.
	 */
	public static String SPECVERSION = "specversion";

	/**
	 * Value for 'type' attribute.
	 */
	public static String TYPE = "type";

	/**
	 * Value for 'datacontenttype' attribute.
	 */
	public static String DATACONTENTTYPE = "datacontenttype";

	/**
	 * Value for 'dataschema' attribute.
	 */
	public static String DATASCHEMA = "dataschema";

	/**
	 * V03 name for 'dataschema' attribute.
	 */
	public static final String SCHEMAURL = "schemaurl";

	/**
	 * Value for 'subject' attribute.
	 */
	public static String SUBJECT = "subject";

	/**
	 * Value for 'time' attribute.
	 */
	public static String TIME = "time";

	/**
	 * Will wrap the provided map of headers as {@link MutableCloudEventHeaders}.
	 * @param headers map representing headers
	 * @return instance of {@link CloudEventAttributes}
	 */
	public static CloudEventBuilder fromMap(Map<String, Object> headers) {
		MutableCloudEventHeaders reader = new MutableCloudEventHeaders(toCanonical(headers));
		return reader.getBuilder();
	}

	/**
	 * Will convert these attributes to {@link Map} of headers where each attribute will
	 * be prefixed with the value of 'prefixToUse'.
	 * @param prefixToUse prefix to be used on attributes
	 * @return map of headers.
	 */
	public static Map<String, Object> toMap(CloudEventContext attributes, String prefixToUse) {
		Map<String, Object> result = new HashMap<>();
		if (!StringUtils.hasText(prefixToUse)) {
			prefixToUse = "";
		}
		for (String key : attributes.getAttributeNames()) {
			Object value = attributes.getAttribute(key);
			if (value != null && !(value instanceof String)) {
				value = value.toString();
			}
			if (value != null) {
				result.put(prefixToUse + key, value);
			}
		}
		for (String key : attributes.getExtensionNames()) {
			Object value = attributes.getExtension(key);
			if (value != null && !(value instanceof String)) {
				value = value.toString();
			}
			if (value != null) {
				result.put(prefixToUse + key, value);
			}
		}
		result.put(prefixToUse + CloudEventHeaderUtils.SPECVERSION, attributes.getSpecVersion().toString());
		return result;
	}

	private static String determinePrefixToUse(Map<String, Object> messageHeaders) {
		Set<String> keys = messageHeaders.keySet();
		if (keys.contains(CloudEventHeaderUtils.DEFAULT_ATTR_PREFIX + CloudEventHeaderUtils.ID)) {
			return CloudEventHeaderUtils.DEFAULT_ATTR_PREFIX;
		}
		else if (keys.contains(CloudEventHeaderUtils.HTTP_ATTR_PREFIX + CloudEventHeaderUtils.ID)) {
			return CloudEventHeaderUtils.HTTP_ATTR_PREFIX;
		}
		else if (keys.contains(CloudEventHeaderUtils.AMQP_ATTR_PREFIX + CloudEventHeaderUtils.ID)) {
			return CloudEventHeaderUtils.AMQP_ATTR_PREFIX;
		}
		else if (keys.contains("user-agent")) {
			return CloudEventHeaderUtils.HTTP_ATTR_PREFIX;
		}
		return "";
	}

	/**
	 * Extract canonical version of map with CloudEvent keys.
	 * @param headers a map of headers
	 * @return a canonical form of the same map with {@link CloudEvent} attribute names
	 */
	public static Map<String, Object> toCanonical(Map<String, Object> headers) {
		String prefix = determinePrefixToUse(headers);
		SpecVersion specVersion = extractSpecVersion(headers, prefix);
		Map<String, Object> result = new HashMap<>();
		for (String name : headers.keySet()) {
			if (name.startsWith(prefix) && name.length() > prefix.length()) {
				result.put(name.substring(prefix.length()), headers.get(name));
			}
		}
		result.put(CloudEventHeaderUtils.SPECVERSION, specVersion);
		if (headers.containsKey(prefix + CloudEventHeaderUtils.DATA)) {
			result.put(CloudEventHeaderUtils.DATA, headers.get(prefix + CloudEventHeaderUtils.DATA));
		}
		return result;
	}

	private static SpecVersion extractSpecVersion(Map<String, Object> headers, String prefix) {
		String key = prefix + CloudEventHeaderUtils.SPECVERSION;
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

	public static boolean isValidCloudEvent(Map<String, Object> attributes) {
		return new MutableCloudEventHeaders(attributes).isValidCloudEvent();
	}

}
