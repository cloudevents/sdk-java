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
package io.cloudevents.mqtt.hivemq;

import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MqttMessageFactoryTest {

    @Test
    public void createV3Writer() {
    }

    @Test
    public void createV5Writer() {
        Assertions.assertNotNull(MqttMessageFactory.createWriter((Mqtt5PublishBuilder.Complete) Mqtt5Publish.builder()));
    }

    @Test
    public void create3Reader() {

        Mqtt3Publish msg = Mqtt3Publish.builder().topic("test").build();
        Assertions.assertNotNull(MqttMessageFactory.createReader(msg));
    }

    @Test
    public void createV5ReaderFromStructured() {

        // If the content-type is present then hopefully it's a
        // cloudvent one.

        EventFormat ef = CSVFormat.INSTANCE;

        EventFormatProvider.getInstance().registerFormat(ef);

        Mqtt5Publish msg = Mqtt5Publish.builder()
            .topic("test")
            .contentType(ef.serializedContentType())
            .build();

        Assertions.assertNotNull(MqttMessageFactory.createReader(msg));

    }

    @Test
    public void createV5ReaderFromBinary() {

        Mqtt5Publish msg = Mqtt5Publish.builder()
            .topic("test")
            .userProperties().add("specversion", "1.0").applyUserProperties()
            .build();
        Assertions.assertNotNull(MqttMessageFactory.createReader(msg));

    }
}
