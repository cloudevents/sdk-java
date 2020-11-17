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

import io.cloudevents.CloudEventAttributes;

/**
 * Strategy that should be implemented by the user to help with outgoing Cloud Event
 * attributes.
 * <br>
 * <br>
 * NOTE: The provided `attributes` may or may not be initialized with default values,
 * so it is the responsibility of the user to ensure that all required Cloud Events
 * attributes are set. That said, various Spring frameworks which utilize this interface
 * will ensure that the 'provided' attributes are initialized with default values, leaving
 * to responsible to only set the attributes you need.
 * <br>
 * Once implemented, simply configure it as a bean and the framework will invoke it before
 * the outbound Cloud Event Message is finalized.
 *
 * <pre>
 * &#64;Bean
 * public CloudEventAttributesProvider cloudEventAttributesProvider() {
 * 	return attributes -&gt;
 *		CloudEventAttributeUtils.get(attributes).setSource("https://interface21.com/").setType("com.interface21");
 * }
 * </pre>
 *
 * @author Oleg Zhurakousky
 * @author Dave Syer
 * @since 2.0
 */
@FunctionalInterface
public interface CloudEventAttributesProvider {

	/**
	 * @param attributes instance of {@link CloudEventAttributes}
	 * @return instance of modified {@link CloudEventAttributes}
	 */
	CloudEventAttributes getOutputAttributes(CloudEventAttributes attributes);

}
