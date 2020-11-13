/*
 * Copyright 2020-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.amqp;

import java.util.Map;

import io.cloudevents.core.message.impl.MessageUtils;

class AmqpConstants {

	/**
	 * The prefix name for CloudEvent attributes for use in the <em>application-properties</em> section
	 * of an AMQP 1.0 message.
	 */
	public static final String CE_PREFIX = "cloudEvents:";

	/**
	 * The AMQP 1.0 <em>content-type</em> message property
	 */
	public static final String PROPERTY_CONTENT_TYPE = "content-type";

	/**
	 * Map a cloud event attribute name to a value. All values except the <em>datacontenttype</em> attribute are prefixed
	 * with "cloudEvents:" as mandated by the spec.
	 */
	public static final Map<String, String> ATTRIBUTES_TO_PROPERTYNAMES = MessageUtils.generateAttributesToHeadersMapping(CEA -> {
		if (CEA.equals("datacontenttype")) {
			return PROPERTY_CONTENT_TYPE;
		}
		// prefix the attribute
		return CE_PREFIX + CEA;
	});

	public static final String APP_PROPERTY_SPEC_VERSION = ATTRIBUTES_TO_PROPERTYNAMES.get("specversion");

}
