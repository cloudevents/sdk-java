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
package io.cloudevents.protobuf;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.test.v1.proto.Test.Decimal;
import io.cloudevents.test.v1.proto.Test.Quote;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;

import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests to verify appropriate handling of protobuf message data.
 *
 */
public class ProtoMessageDataTest {
    EventFormat protoFormat = new ProtobufFormat();

    @Test
    public void verifyDataWrapper() {
        Quote aQuote = makeQuote();

        CloudEventData ced = ProtoCloudEventData.wrap(aQuote);
        assertThat(ced).isNotNull();
        assertThat(ced.toBytes()).isNotNull();
        assertThat(ced).isInstanceOf(ProtoCloudEventData.class);

        ProtoCloudEventData pced = (ProtoCloudEventData) ced;
        assertThat(pced.getAny()).isNotNull();
    }

    @Test
    public void verifyMessage() throws InvalidProtocolBufferException {
        // Create the busines event data
        Quote pyplQuote = makeQuote();

        // Create the CloudEvent.
        CloudEvent cloudEvent = CloudEventBuilder.v1()
            .withId("ID1")
            .withType("TEST.PROTO")
            .withSource(URI.create("http://localhost/source"))
            .withData(ProtoCloudEventData.wrap(pyplQuote))
            .build();

        // Serialize the CloudEvent
        byte[] raw = protoFormat.serialize(cloudEvent);

        // Sanity.
        assertThat(raw).isNotNull();
        assertThat(raw).hasSizeGreaterThan(0);

        // Now deserialize the event -----------------------------------------
        CloudEvent newCloudEvent = protoFormat.deserialize(raw);

        // Get get Data
        CloudEventData eventData = newCloudEvent.getData();

        // Sanity
        assertThat(eventData).isNotNull();
        assertThat(eventData).isInstanceOf(ProtoCloudEventData.class);

        Any newAny = ((ProtoCloudEventData) eventData).getAny();
        assertThat(newAny).isNotNull();

        // Hydrate the data - maybe there's a cleaner way to do this.
        Quote newQuote = newAny.unpack(Quote.class);
        assertThat(newQuote).ignoringRepeatedFieldOrder().isEqualTo(pyplQuote);
    }

    // -----------------------------------------------

    private Decimal makeDecimal(BigDecimal value) {
        Decimal.Builder builder = Decimal.newBuilder();
        builder.setUnscaled(value.unscaledValue().longValue());
        builder.setScale(value.scale());

        return builder.build();
    }

    private Quote makeQuote() {
        Quote.Builder builder = Quote.newBuilder();
        builder.setSymbol("PYPL");
        builder.setPrice(makeDecimal(new BigDecimal("254.20")));
        builder.setVolume(7132000);

        return builder.build();
    }

}
