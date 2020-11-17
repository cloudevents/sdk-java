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
package io.cloudevents.spring.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.cloudevents.CloudEventContext;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.core.CloudEventHeaderUtils;

import org.springframework.http.HttpHeaders;

/**
 * Miscellaneous utility methods to assist with Cloud Events in the context of Spring Web
 * frameworks. Primarily intended for the internal use within Spring-based frameworks or
 * integrations.
 *
 * @author Dave Syer
 * @since 2.0
 */
public class CloudEventHttpUtils {

	public static HttpHeaders toHttp(CloudEventContext attributes) {
		HttpHeaders headers = new HttpHeaders();
		for (String key : attributes.getAttributeNames()) {
			String target = CloudEventHeaderUtils.HTTP_ATTR_PREFIX + key;
			if (attributes.getAttribute(key) != null) {
				// TODO: need to convert timestamps?
				headers.set(target, attributes.getAttribute(key).toString());
			}
		}
		for (String key : attributes.getExtensionNames()) {
			String target = CloudEventHeaderUtils.HTTP_ATTR_PREFIX + key;
			if (attributes.getExtension(key) != null) {
				// TODO: need to convert timestamps?
				headers.set(target, attributes.getExtension(key).toString());
			}
		}
		return headers;
	}

	public static CloudEventBuilder fromHttp(HttpHeaders headers) {
		Map<String, Object> map = new HashMap<>();
		map.putAll(headers.toSingleValueMap());
		return CloudEventHeaderUtils.fromMap(map).withId(UUID.randomUUID().toString());
	}

}
