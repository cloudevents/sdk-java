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

import io.cloudevents.CloudEventContext;

/**
 * Strategy that should be implemented by the user to help with outgoing Cloud Event
 * headers. <br>
 * <br>
 * NOTE: The provided `attributes` may or may not be initialized with default values, so
 * it is the responsibility of the user to ensure that all required Cloud Events
 * attributes are set. That said, various Spring frameworks which utilize this interface
 * will ensure that the 'provided' attributes are initialized with default values, leaving
 * to responsible to only set the attributes you need. <br>
 * Once implemented, simply configure it as a bean and the framework will invoke it before
 * the outbound Cloud Event Message is finalized.
 *
 * <pre>
 * &#64;Bean
 * public CloudEventHeadersProvider cloudEventHeadersProvider() {
 * 	return attributes -&gt;
 *		CloudEventHeaderUtils.fromAttributes(attributes).withSource("https://interface21.com/").withType("com.interface21").build();
 * }
 * </pre>
 *
 * @author Oleg Zhurakousky
 * @author Dave Syer
 * @since 2.0
 */
@FunctionalInterface
public interface CloudEventHeadersProvider {

	/**
	 * @param attributes instance of {@link CloudEventContext}
	 * @return modified {@link CloudEventContext}
	 */
	CloudEventContext getOutputHeaders(CloudEventContext attributes);

}
