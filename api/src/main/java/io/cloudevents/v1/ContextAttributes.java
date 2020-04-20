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
package io.cloudevents.v1;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author fabiojose
 * @version 1.0
 */
public enum ContextAttributes {
    ID,
    SOURCE,
    SPECVERSION,
    TYPE,
    DATACONTENTTYPE,
    DATASCHEMA,
    SUBJECT,
    TIME;
	public static final List<String> VALUES =
		Arrays.stream(ContextAttributes.values())
		.map(ContextAttributes::toString)
		.collect(Collectors.toList());

	public static ContextAttributes parse(String value) {
	    return ContextAttributes.valueOf(value.toUpperCase());
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
