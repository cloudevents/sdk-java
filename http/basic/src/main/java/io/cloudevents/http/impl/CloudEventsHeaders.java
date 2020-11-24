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
 */

package io.cloudevents.http.impl;

import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.core.v1.CloudEventV1;

import java.util.Collections;
import java.util.Map;

public final class CloudEventsHeaders {

    private CloudEventsHeaders() {}

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String CE_PREFIX = "ce-";

    public static final Map<String, String> ATTRIBUTES_TO_HEADERS = Collections.unmodifiableMap(MessageUtils.generateAttributesToHeadersMapping(v -> {
        if (v.equals(CloudEventV1.DATACONTENTTYPE)) {
            return CONTENT_TYPE;
        }
        return CE_PREFIX + v;
    }));

    public static final String SPEC_VERSION = ATTRIBUTES_TO_HEADERS.get(CloudEventV1.SPECVERSION);

}
