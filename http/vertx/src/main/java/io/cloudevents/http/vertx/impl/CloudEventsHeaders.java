/*
 * Copyright 2018-Present The CloudEvents Authors
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

package io.cloudevents.http.vertx.impl;

import io.cloudevents.SpecVersion;
import io.vertx.core.http.HttpHeaders;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CloudEventsHeaders {

    public static final String CE_PREFIX = "ce-";

    public static final Map<String, CharSequence> ATTRIBUTES_TO_HEADERS = Stream.concat(
        Stream.concat(SpecVersion.V1.getMandatoryAttributes().stream(), SpecVersion.V1.getOptionalAttributes().stream()),
        Stream.concat(SpecVersion.V03.getMandatoryAttributes().stream(), SpecVersion.V03.getOptionalAttributes().stream())
    )
        .distinct()
        .collect(Collectors.toMap(Function.identity(), v -> {
            if (v.equals("datacontenttype")) {
                return HttpHeaders.CONTENT_TYPE;
            }
            return HttpHeaders.createOptimized(CE_PREFIX + v);
        }));

    public static final CharSequence SPEC_VERSION = ATTRIBUTES_TO_HEADERS.get("specversion");

}
