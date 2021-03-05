/*
 * Copyright 2020-Present The CloudEvents Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.spring.messaging;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.rw.CloudEventRWException;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;

import static io.cloudevents.spring.messaging.CloudEventsHeaders.CE_PREFIX;

/**
 * Utility class for copying message headers to and from {@link CloudEventContext}.
 *
 * @author Dave Syer
 */
public class CloudEventContextUtils {

    /**
     * Helper method for converting {@link MessageHeaders} to a {@link CloudEventContext}.
     * The input headers <b>must</b> represent a valid event in "binary" form, i.e. it must have headers
     * {@code ce-id}, {@code ce-specversion} etc.
     *
     * @param headers the input message headers
     * @return a {@link CloudEventContext} that can be used to create a new {@link CloudEvent}
     * @throws CloudEventRWException if something goes wrong while converting the headers to {@link CloudEventContext}
     */
    public static CloudEventContext fromMap(Map<String, Object> headers) throws CloudEventRWException {
        Object value = headers.get(CloudEventsHeaders.SPEC_VERSION);
        SpecVersion version = value == null ? SpecVersion.V1 : SpecVersion.parse(value.toString());
        return CloudEventUtils.toEvent(new MessageBinaryMessageReader(version, headers));
    }

    /**
     * Helper method for extracting {@link MessageHeaders} from a {@link CloudEventContext}. The
     * result will contain headers canonicalized with a {@code ce-} prefix, analogous to the
     * "binary" message format in Cloud Events.
     *
     * @param context the input {@link CloudEventContext}
     * @return the response headers represented by the event
     * @throws CloudEventRWException if something goes wrong while converting the {@link CloudEventContext} to headers
     */
    public static Map<String, Object> toMap(CloudEventContext context) throws CloudEventRWException {
        Map<String, Object> headers = new HashMap<>();
        // Probably this should be done in CloudEventContextReaderAdapter
        headers.put(CE_PREFIX + "specversion", context.getSpecVersion().toString());
        MessageBuilderMessageWriter writer = new MessageBuilderMessageWriter(headers);
        CloudEventUtils.toContextReader(context).readContext(writer);
        return writer.end().getHeaders();
    }

}
