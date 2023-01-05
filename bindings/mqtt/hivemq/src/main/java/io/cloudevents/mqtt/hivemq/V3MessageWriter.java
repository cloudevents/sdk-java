package io.cloudevents.mqtt.hivemq;

import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3PublishBuilder;
import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;

class V3MessageWriter implements MessageWriter<CloudEventWriter<Mqtt3PublishBuilder>, Mqtt3PublishBuilder> {

    Mqtt3PublishBuilder.Complete builder;

    V3MessageWriter(Mqtt3PublishBuilder.Complete builder) {
        this.builder = builder;
    }

    @Override
    public CloudEventWriter<Mqtt3PublishBuilder> create(SpecVersion version) throws CloudEventRWException {
        // No-Op
        return null;
    }

    @Override
    public Mqtt3PublishBuilder setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        // No-Op
        return null;
    }

    @Override
    public Mqtt3PublishBuilder writeStructured(CloudEvent event, String format) {
        EventFormat eventFormat = EventFormatProvider.getInstance().resolveFormat(format);

        if (eventFormat != null) {
            return writeStructured(event, EventFormatProvider.getInstance().resolveFormat(format));
        } else {
            throw CloudEventRWException.newOther("Unsupported Format: " + format);
        }
    }

    @Override
    public Mqtt3PublishBuilder writeStructured(CloudEvent event, EventFormat format) {
        final byte[] data = format.serialize(event);
        builder.payload(data);
        return builder;
    }

    @Override
    public Mqtt3PublishBuilder writeBinary(CloudEvent event) {

        throw CloudEventRWException.newOther("MQTT V3 Does not support CloudEvent Binary mode");

    }
}
