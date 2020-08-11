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

package io.cloudevents.bench.kafka;

import io.cloudevents.jackson.Json;
import io.cloudevents.kafka.KafkaMessageFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.record.TimestampType;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import static io.cloudevents.core.test.Data.V1_WITH_JSON_DATA_WITH_EXT;

public class KafkaConsumerMessageToCloudEventBenchmark {

    @State(Scope.Thread)
    public static class BinaryMessage {
        public ConsumerRecord<String, byte[]> message;

        public BinaryMessage() {
            // Hack to generate a consumer message
            ProducerRecord<Void, byte[]> inRecord = KafkaMessageFactory
                .createWriter("aaa")
                .writeBinary(V1_WITH_JSON_DATA_WITH_EXT);

            this.message = new ConsumerRecord<>(
                "aaa",
                0,
                0,
                0,
                TimestampType.NO_TIMESTAMP_TYPE,
                -1L,
                ConsumerRecord.NULL_SIZE,
                ConsumerRecord.NULL_SIZE,
                "aaa",
                inRecord.value(),
                inRecord.headers()
            );
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testBinaryEncoding(BinaryMessage binaryMessage, Blackhole bh) {
        bh.consume(
            KafkaMessageFactory
                .createReader(binaryMessage.message)
                .toEvent()
        );
    }

    @State(Scope.Thread)
    public static class StructuredJsonMessage {
        public ConsumerRecord<String, byte[]> message;

        public StructuredJsonMessage() {
            // Hack to generate a consumer message
            ProducerRecord<Void, byte[]> inRecord = KafkaMessageFactory
                .createWriter("aaa")
                .writeStructured(V1_WITH_JSON_DATA_WITH_EXT, Json.CONTENT_TYPE);

            this.message = new ConsumerRecord<>(
                "aaa",
                0,
                0,
                0,
                TimestampType.NO_TIMESTAMP_TYPE,
                -1L,
                ConsumerRecord.NULL_SIZE,
                ConsumerRecord.NULL_SIZE,
                "aaa",
                inRecord.value(),
                inRecord.headers()
            );
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testStructuredJsonEncoding(StructuredJsonMessage structuredJsonMessage, Blackhole bh) {
        bh.consume(
            KafkaMessageFactory
                .createReader(structuredJsonMessage.message)
                .toEvent()
        );
    }

}
