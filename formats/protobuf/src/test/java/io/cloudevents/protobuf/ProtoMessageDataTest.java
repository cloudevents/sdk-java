package io.cloudevents.protobuf;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import org.junit.jupiter.api.Test;
import io.cloudevents.test.v1.proto.Test.Quote;
import io.cloudevents.test.v1.proto.Test.Decimal;

import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.net.URI;

/**
 * Tests to verify appropriate handling of protobuf message data.
 *
 */
public class ProtoMessageDataTest {

    EventFormat protoFormat = new ProtobufFormat();

    @Test
    public void verifyMessage() {

        // Create the busines event data
        Quote pyplQuote = makeQuote();

        // Create the CloudEvent.
        CloudEvent cloudEvent = CloudEventBuilder.v1()
            .withId("ID1")
            .withType("TEST.PROTO")
            .withSource(URI.create("http://localhost/source"))
            .withData(new ProtoData(pyplQuote))
            .build();

        // Serialize the CloudEvent
        byte[] raw = protoFormat.serialize(cloudEvent);

        // Sanity.
        assertNotNull(raw);
        assertTrue(raw.length > 0);

        // Now deserialize the event -----------------------------------------
        CloudEvent newCloudEvent = protoFormat.deserialize(raw);

        // Get get Data
        CloudEventData eventData = newCloudEvent.getData();

        // Sanity
        assertNotNull(eventData);
        assertNotNull(eventData instanceof ProtoCloudEventData);

        Message newMessage = ((ProtoCloudEventData) eventData).getMessage();
        assertNotNull(newMessage);
        assertTrue(newMessage instanceof Any);

        try {

            // Hydrate the data - maybe there's a cleaner way to do this.
            Quote newQuote = ((Any) newMessage).unpack(Quote.class);

            assertThat(newQuote).ignoringRepeatedFieldOrder().isEqualTo(pyplQuote);

        } catch (InvalidProtocolBufferException e) {
            fail(e.getMessage());
        }


    }

    /**
     * A DataHolder
     */
    private class ProtoData implements ProtoCloudEventData {

        private final Message protoMessage;

        ProtoData(Message msg){
            this.protoMessage = msg;
        }
        @Override
        public Message getMessage() {
            return protoMessage;
        }

        @Override
        public byte[] toBytes() {
            return protoMessage.toByteArray();
        }
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
