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
 *  See details about in-memory format
 * <a href="https://github.com/cloudevents/spec/blob/v0.2/documented-extensions.md#usage">here</a>
 * 
 * @author fabiojose
 * 
 */
public interface ExtensionFormat {
	/**
	 * The in-memory format key
	 */
	String getKey();
	
	/**
	 * The extension implementation
	 */
	Extension getExtension();
}
