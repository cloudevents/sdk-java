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
package io.cloudevents.extensions;

/**
 * The in-memory format to be used by the structured content mode.
 * <br>
 * See details about in-memory format
 * <a href="https://github.com/cloudevents/spec/blob/v0.2/documented-extensions.md#usage">here</a>
 * @author fabiojose
 *
 */
public interface InMemoryFormat {

	/**
	 * The in-memory format key
	 */
	String getKey();
	
	/**
	 * The in-memory format value
	 */
	Object getValue();
	
	/**
	 * The type reference for the value. That should be used during the
	 * unmarshal.
	 */
	Class<?> getValueType();
	
	public static InMemoryFormat of(final String key, final Object value,
			final Class<?> valueType) {
		return new InMemoryFormat() {
			@Override
			public String getKey() {
				return key;
			}

			@Override
			public Object getValue() {
				return value;
			}

			@Override
			public Class<?> getValueType() {
				return valueType;
			}
		};
	}
}
