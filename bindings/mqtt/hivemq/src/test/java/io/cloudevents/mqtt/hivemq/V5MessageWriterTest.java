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

import com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperty;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class V5MessageWriterTest {

    private final Mqtt5PublishBuilder builder;
    private final V5MessageWriter writer;

    V5MessageWriterTest() {
        builder = Mqtt5Publish.builder();
        writer = new V5MessageWriter((Mqtt5PublishBuilder.Complete) builder);
        builder.topic("tester");
    }

    @Test
    public void testWithContextAttribute() {

        Assertions.assertNotNull(writer.withContextAttribute("test", "testing"));

        Mqtt5Publish msg = ((Mqtt5PublishBuilder.Complete) builder).build();

        ensureProperty(msg, "test", "testing");
    }

    @Test
    public void testWithContextAttributes() {

        Assertions.assertNotNull(writer.withContextAttribute("test1", "testing1"));
        Assertions.assertNotNull(writer.withContextAttribute("test2", "testing2"));

        Mqtt5Publish msg = ((Mqtt5PublishBuilder.Complete) builder).build();

        ensureProperty(msg, "test1", "testing1");
        ensureProperty(msg, "test2", "testing2");
    }

    @Test
    public void testEnd() {
        Assertions.assertNotNull(writer.end());
    }

    @Test
    public void testEndWithData() {
        final byte[] tData = {0x00, 0x02, 0x42};

        Assertions.assertNotNull(writer.end(BytesCloudEventData.wrap(tData)));

        Mqtt5Publish msg = ((Mqtt5PublishBuilder.Complete) builder).build();

        Assertions.assertNotNull(msg.getPayloadAsBytes());
        Assertions.assertEquals(msg.getPayloadAsBytes().length, tData.length);

    }

    @Test
    public void testCreate() {
        Assertions.assertNotNull(writer.create(SpecVersion.V1));

        Mqtt5Publish msg = ((Mqtt5PublishBuilder.Complete) builder).build();
        ensureProperty(msg, "specversion", SpecVersion.V1.toString());

    }

    private void ensureProperty(Mqtt5Publish msg, String name, String val) {

        List<Mqtt5UserProperty> props = (List<Mqtt5UserProperty>) msg.getUserProperties().asList();

        Mqtt5UserProperty prop = null;

        for (Mqtt5UserProperty up : props) {

            if (up.getName().toString().equals(name)) {
                prop = up;
                break;
            }
        }

        Assertions.assertNotNull(prop);
        Assertions.assertEquals(prop.getValue().toString(), val);

    }
}
