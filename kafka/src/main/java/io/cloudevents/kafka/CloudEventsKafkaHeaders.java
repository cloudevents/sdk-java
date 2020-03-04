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

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.Wire;
import io.cloudevents.format.builder.EventStep;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper class for building the Kafka headers that should be attached either a binary or structured event message.
 *
 * @author Florian Hussonnois
 */
public class CloudEventsKafkaHeaders {

    /**
     * Static helper to create Kafka {@link org.apache.kafka.common.header.Headers} for the given {@link CloudEvent}.
     *
     * @param event     the {@link CloudEvent} to be used for creating corresponding Kafka headers.
     * @param builder   the {@link EventStep} to be used for building the headers.
     * @return  the {@link org.apache.kafka.common.header.Headers}
     */
    public static <T, A extends Attributes> Iterable<Header> buildHeaders(final CloudEvent<A, T> event,
                                                                          final EventStep<A, T, byte[], byte[]> builder) {
        return getHeaders(event, builder);
    }

    private static <T, A extends Attributes> Iterable<Header> getHeaders(final CloudEvent<A, T> event,
                                                                         final EventStep<A, T, byte[], byte[]> marshaller) {
        Wire<byte[], String, byte[]> marshal = marshaller.withEvent(() -> event).marshal();
        Map<String, byte[]> headers = marshal.getHeaders();
        return marshal(headers);
    }

    /**
     * Casts the Object value of header into byte[]. This is
     * guaranteed by the HeaderMapper implementation
     *
     * @param headers   the headers to convert.
     * @return          the set of {@link Header}.
     */
    static Set<Header> marshal(Map<String, byte[]> headers) {
        return headers.entrySet()
            .stream()
            .map(header ->
                    new AbstractMap.SimpleEntry<>(header.getKey(), header.getValue()))
            .map(header -> new RecordHeader(header.getKey(), header.getValue()))
            .collect(Collectors.toSet());
    }
}

