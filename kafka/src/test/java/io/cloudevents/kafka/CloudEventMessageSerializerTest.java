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

package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.impl.CloudEventUtils;
import io.cloudevents.message.Encoding;
import io.cloudevents.message.Message;
import io.cloudevents.mock.MockBinaryMessage;
import io.cloudevents.test.Data;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventMessageSerializerTest {

    @Test
    public void serializerShouldWork() {
        String topic = "test";
        CloudEvent event = Data.V1_WITH_JSON_DATA;

        CloudEventMessageSerializer serializer = new CloudEventMessageSerializer();

        Headers headers = new RecordHeaders();

        MockBinaryMessage inMessage = new MockBinaryMessage();
        CloudEventUtils.asBinaryMessage(event).visit(inMessage);

        byte[] payload = serializer.serialize(topic, headers, inMessage);

        Message outMessage = KafkaMessageFactory.create(headers, payload);

        assertThat(outMessage.getEncoding())
            .isEqualTo(Encoding.BINARY);
        assertThat(outMessage.toEvent())
            .isEqualTo(event);
    }

}
