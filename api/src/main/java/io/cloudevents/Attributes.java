/**
 * Copyright 2019 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents;

import java.net.URI;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The marker interface for CloudEvents attributes
 *
 * @author fabiojose
 *
 */
public interface Attributes {

	/**
	 * @return Identifies the event. Producers MUST ensure that source + id is unique for each distinct event
	 */
	@NotBlank
	String getId();

	/**
	 * @return A value describing the type of event related to the originating occurrence.
	 */
	@NotBlank
	String getType();

	/**
	 * @return The context in which an event happened.
	 */
	@NotNull
	URI getSource();

	/**
	 * @return The version of the CloudEvents specification which the event uses
	 */
	@NotBlank
	SpecVersion getSpecversion();

	/**
	 * A common way to get the media type of CloudEvents 'data';
	 * @return If has a value, it MUST follows the <a href="https://tools.ietf.org/html/rfc2046">RFC2046</a>
	 */
	@JsonIgnore
	Optional<String> getMediaType();

}
