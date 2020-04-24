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
package io.cloudevents.kafka;

import io.cloudevents.SpecVersion;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class for building the Kafka headers that should be attached either a binary or structured event message.
 *
 * @author Francesco Guardiani
 * @author Florian Hussonnois
 */
public class CloudEventsKafkaHeaders {

    public static final String CONTENT_TYPE = "content-type";

    public static final Map<String, String> ATTRIBUTES_TO_HEADERS = Stream.concat(
        Stream.concat(SpecVersion.V1.getMandatoryAttributes().stream(), SpecVersion.V1.getOptionalAttributes().stream()),
        Stream.concat(SpecVersion.V03.getMandatoryAttributes().stream(), SpecVersion.V03.getOptionalAttributes().stream())
    )
        .distinct()
        .collect(Collectors.toMap(Function.identity(), v -> {
            if (v.equals("datacontenttype")) {
                return CONTENT_TYPE;
            }
            return "ce_" + v;
        }));

    public static final String SPEC_VERSION = ATTRIBUTES_TO_HEADERS.get("specversion");

}

