/*
 * Copyright 2020 The CloudEvents Authors
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

package io.cloudevents.kafka;

import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

public class KafkaUtils {

    static RecordHeaders kafkaHeaders(RecordHeader... headers) {
        RecordHeaders hs = new RecordHeaders();
        for (RecordHeader h : headers) {
            hs.add(h);
        }
        return hs;
    }

    static RecordHeader header(String key, String value) {
        return new RecordHeader(key, value.getBytes());
    }

}
