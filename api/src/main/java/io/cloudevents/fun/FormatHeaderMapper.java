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
package io.cloudevents.fun;

import java.util.Map;

/**
 * 
 * @author fabiojose
 * @param <H> The header value type
 * 
 */
@FunctionalInterface
public interface FormatHeaderMapper<H> {

	/**
	 * Maps the 'attributes' and 'extensions' of CloudEvent envelop to
	 * 'headers' of binary format
	 * @param attributes
	 * @return
	 */
	Map<String, H> map(Map<String, String> attributes,
			Map<String, String> extensions);
	
}
