/**
 * Copyright 2019 The CloudEvents Authors
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
 */
package io.cloudevents.kafka.impl;

import io.cloudevents.SpecVersion;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class that contains Kafka headers that should be attached either a binary or structured event message.
 *
 * @author Francesco Guardiani
 * @author Florian Hussonnois
 */
public class KafkaHeaders {

    protected static final String CE_PREFIX = "ce_";

    public static final String CONTENT_TYPE = "content-type";

    protected static final Map<String, String> ATTRIBUTES_TO_HEADERS = Stream.concat(
        Stream.concat(SpecVersion.V1.getMandatoryAttributes().stream(), SpecVersion.V1.getOptionalAttributes().stream()),
        Stream.concat(SpecVersion.V03.getMandatoryAttributes().stream(), SpecVersion.V03.getOptionalAttributes().stream())
    )
        .distinct()
        .collect(Collectors.toMap(Function.identity(), v -> {
            if (v.equals("datacontenttype")) {
                return CONTENT_TYPE;
            }
            return CE_PREFIX + v;
        }));

    public static final String SPEC_VERSION = ATTRIBUTES_TO_HEADERS.get("specversion");

    public static String getParsedKafkaHeader(Headers headers, String key) {
        Header h = headers.lastHeader(key);
        if (h == null) {
            return null;
        }
        return new String(h.value(), StandardCharsets.UTF_8);
    }

}

