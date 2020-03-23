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

/**
 *
 * @author fabiojose
 *
 */
public interface Builder {

	/**
	 * To build a brand new instance of {@link CloudEvent}
	 */
	CloudEvent build();

	/**
	 * To build a brand new instance of {@link CloudEvent} with another
	 * type of 'data'
	 * @param id The new id for the new instance
	 * @param base The base {@link CloudEvent} to copy its attributes
	 * @param newData The new 'data'
	 */
	CloudEvent build(CloudEvent base, String id, Data newData);
}
