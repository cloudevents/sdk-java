package io.cloudevents.mqtt.hivemq;

import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;

class V5MessageWriter implements MessageWriter<CloudEventWriter<Mqtt5PublishBuilder.Complete>, Mqtt5PublishBuilder.Complete>, CloudEventWriter<Mqtt5PublishBuilder.Complete> {

    private final Mqtt5PublishBuilder.Complete builder;

    V5MessageWriter(Mqtt5PublishBuilder.Complete builder) {
        this.builder = builder;
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
        builder.userProperties().add(name, value).applyUserProperties();
        return this;
    }

    @Override
    public Mqtt5PublishBuilder.Complete end(CloudEventData data) throws CloudEventRWException {
        builder.payload(data.toBytes());
        return end();
    }

    @Override
    public Mqtt5PublishBuilder.Complete end() throws CloudEventRWException {
        return builder;
    }


    @Override
    public CloudEventWriter<Mqtt5PublishBuilder.Complete> create(SpecVersion version) throws CloudEventRWException {
        withContextAttribute("specversion", version.toString());
        return this;
    }

    @Override
    public Mqtt5PublishBuilder.Complete setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        builder.contentType(format.serializedContentType());
        builder.payload(value);
        return end();
    }
}
