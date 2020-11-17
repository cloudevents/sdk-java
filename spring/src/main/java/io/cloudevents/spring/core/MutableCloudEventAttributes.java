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

import java.io.Serializable;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import io.cloudevents.CloudEventAttributes;
import io.cloudevents.SpecVersion;

import org.springframework.util.StringUtils;

/**
 * Utility class to assist with accessing and setting Cloud Events attributes from headers
 * in messages and HTTP exchanges.
 *
 * It is primarily used within various Spring frameworks.
 *
 * @author Oleg Zhurakousky
 * @author Dave Syer
 * @since 2.0
 */
public class MutableCloudEventAttributes implements CloudEventAttributes, Serializable {

	private static final long serialVersionUID = 5393610770855366497L;

	private Map<String, Object> map = new HashMap<>();

	boolean isV03 = false;

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

	MutableCloudEventAttributes(Map<String, Object> headers) {
		map.putAll(headers);
		safe(headers, MutableCloudEventAttributes.SOURCE);
		safe(headers, MutableCloudEventAttributes.DATASCHEMA);
		this.isV03 = this.getSpecVersion().equals(SpecVersion.V03);
	}

	private void safe(Map<String, Object> headers, String key) {
		Object value = headers.get(key);
		if (value != null) {
			map.put(key, value.toString());
		}
	}

	@Override
	public SpecVersion getSpecVersion() {
		SpecVersion specVersion = (SpecVersion) this.getAttribute(MutableCloudEventAttributes.SPECVERSION);
		return specVersion == null ? SpecVersion.V1 : specVersion;
	}

	public MutableCloudEventAttributes setId(String id) {
		this.setAttribute(MutableCloudEventAttributes.ID, id);
		return this;
	}

	@Override
	public String getId() {
		Object id = this.getAttribute(MutableCloudEventAttributes.ID);
		return id == null ? null : id.toString();
	}

	public MutableCloudEventAttributes setType(String type) {
		this.setAttribute(MutableCloudEventAttributes.TYPE, type);
		return this;
	}

	@Override
	public String getType() {
		return (String) this.getAttribute(MutableCloudEventAttributes.TYPE);
	}

	public MutableCloudEventAttributes setSource(URI source) {
		this.setAttribute(MutableCloudEventAttributes.SOURCE, source.toString());
		return this;
	}

	@Override
	public URI getSource() {
		Object value = this.getAttribute(MutableCloudEventAttributes.SOURCE);
		return value == null ? null : URI.create((String) value);
	}

	public MutableCloudEventAttributes setDataContentType(String datacontenttype) {
		this.setAttribute(MutableCloudEventAttributes.DATACONTENTTYPE, datacontenttype);
		return this;
	}

	@Override
	public String getDataContentType() {
		return (String) this.getAttribute(MutableCloudEventAttributes.DATACONTENTTYPE);
	}

	public MutableCloudEventAttributes setDataSchema(URI dataschema) {
		this.setAttribute(MutableCloudEventAttributes.DATASCHEMA, dataschema.toString());
		return this;
	}

	@Override
	public URI getDataSchema() {
		Object value = this.getAttribute(MutableCloudEventAttributes.DATASCHEMA);
		if (value == null && this.getSpecVersion() == SpecVersion.V03) {
			value = this.getAttribute(MutableCloudEventAttributes.SCHEMAURL);
		}
		return value == null ? null : URI.create((String) value);
	}

	public MutableCloudEventAttributes setSubject(String subject) {
		this.setAttribute(MutableCloudEventAttributes.SUBJECT, subject);
		return this;
	}

	@Override
	public String getSubject() {
		return (String) this.getAttribute(MutableCloudEventAttributes.SUBJECT);
	}

	public MutableCloudEventAttributes setTime(String time) {
		this.setAttribute(MutableCloudEventAttributes.TIME, time);
		return this;
	}

	@Override
	public OffsetDateTime getTime() {
		String time = (String) this.getAttribute(MutableCloudEventAttributes.TIME);
		return OffsetDateTime.parse(time);
	}

	/**
	 * Will delegate to the underlying {@link Map} returning the value for the requested
	 * attribute or null.
	 */
	@Override
	public Object getAttribute(String attributeName) {
		if (isV03 && MutableCloudEventAttributes.SCHEMAURL.equals(attributeName)
				&& map.containsKey(MutableCloudEventAttributes.DATASCHEMA)) {
			return map.get(MutableCloudEventAttributes.DATASCHEMA);
		}
		return map.get(attributeName);
	}

	/**
	 * Determines if this instance of {@link CloudEventAttributes} represents valid Cloud
	 * Event. This implies that it contains all 4 required attributes (id, source, type
	 * and specversion)
	 * @return true if this instance represents a valid Cloud Event
	 */
	public boolean isValidCloudEvent() {
		return StringUtils.hasText(this.getId()) && this.getSource() != null
				&& StringUtils.hasText(this.getSource().toString()) && this.getSpecVersion() != null
				&& StringUtils.hasText(this.getType());
	}

	/**
	 * Will convert these attributes to {@link Map} of headers where each attribute will
	 * be prefixed with the value of 'prefixToUse'.
	 * @param prefixToUse prefix to be used on attributes
	 * @return map of headers.
	 */
	public Map<String, Object> toMap(String prefixToUse) {
		Map<String, Object> result = new HashMap<>();
		if (!StringUtils.hasText(prefixToUse)) {
			prefixToUse = "";
		}
		for (String key : this.getAttributeNames()) {
			Object value = this.getAttribute(key);
			if (value != null) {
				result.put(prefixToUse + key, value);
			}
		}
		result.put(prefixToUse + "specversion", this.getSpecVersion().toString());
		return result;
	}

	public MutableCloudEventAttributes setAttribute(String attrName, Object attrValue) {
		map.put(attrName, attrValue);
		return this;
	}

	@Override
	public String toString() {
		return map.toString();
	}

}
