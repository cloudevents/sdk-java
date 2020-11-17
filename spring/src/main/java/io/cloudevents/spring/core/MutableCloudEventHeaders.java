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
package io.cloudevents.spring.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.cloudevents.CloudEventAttributes;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;

import org.springframework.util.StringUtils;

/**
 * @author Dave Syer
 *
 */
class MutableCloudEventHeaders {

	private Map<String, Object> map = new HashMap<>();

	MutableCloudEventHeaders(Map<String, Object> headers) {
		map.putAll(headers);
		safeString(headers, CloudEventHeaderUtils.ID);
		safeTime(headers, CloudEventHeaderUtils.TIME);
		safeUri(headers, CloudEventHeaderUtils.SOURCE);
		safeUri(headers, CloudEventHeaderUtils.DATASCHEMA);
		safeUri(headers, CloudEventHeaderUtils.SCHEMAURL);
		map.put(CloudEventHeaderUtils.SPECVERSION, getSpecVersion());
	}

	private void safeString(Map<String, Object> headers, String key) {
		Object value = headers.get(key);
		if (value != null && !(value instanceof String)) {
			map.put(key, value.toString());
		}
	}

	private void safeTime(Map<String, Object> headers, String key) {
		Object value = headers.get(key);
		if (value instanceof String) {
			map.put(key, OffsetTime.parse((String) value));
		}
	}

	private void safeUri(Map<String, Object> headers, String key) {
		Object value = headers.get(key);
		if (value instanceof String) {
			map.put(key, URI.create((String) value));
		}
	}

	private SpecVersion getSpecVersion() {
		SpecVersion specVersion = (SpecVersion) this.getAttribute(CloudEventHeaderUtils.SPECVERSION);
		return specVersion == null ? SpecVersion.V1 : specVersion;
	}

	// Visible for testing
	Object getAttribute(String attributeName) {
		return map.get(attributeName);
	}

	/**
	 * Determines if this instance of {@link CloudEventAttributes} represents valid Cloud
	 * Event. This implies that it contains all 4 required attributes (id, source, type
	 * and specversion)
	 * @return true if this instance represents a valid Cloud Event
	 */
	public boolean isValidCloudEvent() {
		return StringUtils.hasText((String) getAttribute(CloudEventHeaderUtils.ID))
				&& StringUtils.hasText((String) getAttribute(CloudEventHeaderUtils.SOURCE))
				&& StringUtils.hasText((String) getAttribute(CloudEventHeaderUtils.TYPE));
	}

	private Set<String> getAttributeNames() {
		return getSpecVersion().getAllAttributes().stream().filter(s -> getAttribute(s) != null)
				.collect(Collectors.toSet());
	}

	public CloudEventBuilder getBuilder() {
		CloudEventBuilder builder = CloudEventBuilder.fromSpecVersion(this.getSpecVersion());
		Set<String> names = this.getAttributeNames();
		for (String name : names) {
			Object value = this.getAttribute(name);
			if (value instanceof String) {
				builder.withAttribute(name, (String) value);
			}
			else if (value instanceof URI) {
				builder.withAttribute(name, (URI) value);
			}
			else if (value instanceof OffsetDateTime) {
				builder.withAttribute(name, (OffsetDateTime) value);
			}
		}
		for (String name : this.map.keySet()) {
			if (names.contains(name)) {
				continue;
			}
			Object value = this.map.get(name);
			if (value instanceof String) {
				builder.withExtension(name, (String) value);
			}
			else if (value instanceof Number) {
				builder.withExtension(name, (Number) value);
			}
			else if (value instanceof Boolean) {
				builder.withExtension(name, (Boolean) value);
			}
		}
		return builder;
	}

}
