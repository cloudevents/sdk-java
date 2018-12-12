/**
 * Copyright 2018 The CloudEvents Authors
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

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

public final class KafkaTransportUtil {

    private static final KafkaTransportHeaders transportHeaders = new KafkaTransportHeaders();

    private KafkaTransportUtil() {
        // no-op
    }

    /**
     * Create Kafka Headers from a CloudEvent
     * @param cloudEvent Event to extract the headers from
     * @return Headers that can be used to construct a ProducerRecord
     */
    public static Headers extractRecordHeaders(final CloudEvent<?> cloudEvent) {
        final RecordHeaders headers = new RecordHeaders();

        headers.add(new RecordHeader(transportHeaders.typeKey(), ((Serde) Serdes.serdeFrom(String.class)).serializer().serialize(null, cloudEvent.getType())));
        headers.add(new RecordHeader(transportHeaders.specVersionKey(), ((Serde) Serdes.serdeFrom(String.class)).serializer().serialize(null, cloudEvent.getSpecVersion())));
        headers.add(new RecordHeader(transportHeaders.sourceKey(), ((Serde) Serdes.serdeFrom(String.class)).serializer().serialize(null, cloudEvent.getSource().toString())));
        headers.add(new RecordHeader(transportHeaders.idKey(), ((Serde) Serdes.serdeFrom(String.class)).serializer().serialize(null, cloudEvent.getId())));

        if (cloudEvent.getSchemaURL().isPresent()) {
            headers.add(new RecordHeader(transportHeaders.schemaUrlKey(), ((Serde) Serdes.serdeFrom(String.class)).serializer().serialize(null, cloudEvent.getSchemaURL().get().toString()) ));
        }

        if (cloudEvent.getTime().isPresent()) {
            headers.add(new RecordHeader(transportHeaders.timeKey(), ((Serde) Serdes.serdeFrom(String.class)).serializer().serialize(null, cloudEvent.getTime().get().toString()) ));
        }
        //todo: extensions... they are optional...

        return headers;
    }

    /**
     * Create a CloudEvent from a message consumed from a Kafka topic. Populates the CloudEvent attributes
     * from the Kafka Headers and the data from the Kafka value.
     * @param record message receieved from Kafka
     * @param <K> Message Key
     * @param <V> Message Value
     * @return CloudEvent representation of the Kafka message
     */
    public static <K, V> CloudEvent<Map<K, V>> createFromConsumerRecord(final ConsumerRecord<K, V> record) {

        final Headers headers = record.headers();
        final CloudEventBuilder builder = new CloudEventBuilder();

        try {
            builder.type(Serdes.serdeFrom(String.class).deserializer().deserialize(null, headers.lastHeader(transportHeaders.typeKey()).value()));
            builder.source(URI.create(Serdes.serdeFrom(String.class).deserializer().deserialize(null, headers.lastHeader(transportHeaders.sourceKey()).value())));
            builder.id(Serdes.serdeFrom(String.class).deserializer().deserialize(null, headers.lastHeader(transportHeaders.idKey()).value()));

            if (headers.lastHeader(transportHeaders.timeKey()) != null) {
                builder.time(ZonedDateTime.parse(Serdes.serdeFrom(String.class).deserializer().deserialize(null, headers.lastHeader(transportHeaders.timeKey()).value()), ISO_ZONED_DATE_TIME));
            }

            if (headers.lastHeader(transportHeaders.schemaUrlKey()) != null) {
                builder.schemaURL(URI.create(Serdes.serdeFrom(String.class).deserializer().deserialize(null, headers.lastHeader(transportHeaders.schemaUrlKey()).value())));
            }

            //todo: add extensions

            // use the pure key/value as the 'data' part
            final Map<K, V> rawKafkaRecord = new HashMap();
            rawKafkaRecord.put(record.key(), record.value());
            builder.data(rawKafkaRecord);

            // we hard wire to this type:
            builder.contentType("application/kafka"); // todo: move to constant

        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.build();
    }
}
