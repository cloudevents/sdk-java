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

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.Json;
import io.cloudevents.kafka.KafkaMessageFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import static io.cloudevents.core.test.Data.V1_WITH_JSON_DATA_WITH_EXT;


public class CloudEventToKafkaProducerMessageBenchmark {

    @State(Scope.Thread)
    public static class Event {
        public CloudEvent event = CloudEventBuilder.v1(V1_WITH_JSON_DATA_WITH_EXT).build();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testBinaryEncoding(Event event, Blackhole bh) {
        bh.consume(
            KafkaMessageFactory
                .createWriter("aaa")
                .writeBinary(event.event)
        );
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testStructuredJsonEncoding(Event event, Blackhole bh) {
        bh.consume(
            KafkaMessageFactory
                .createWriter("aaa")
                .writeStructured(event.event, Json.CONTENT_TYPE)
        );
    }

}
