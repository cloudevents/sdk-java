package io.cloudevents.protobuf;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventReader;
import io.cloudevents.rw.CloudEventWriter;
import io.cloudevents.rw.CloudEventWriterFactory;
import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map.Entry;

/**
 * Implements a {@link CloudEventReader} that can deserialize a {@link CloudEvent} protobuf representation;
 */
public class ProtoDeserializer implements CloudEventReader {

    private static final Base64.Encoder base64Encoder = Base64.getEncoder();

    private final CloudEvent protoCe;

    public ProtoDeserializer(CloudEvent protoCe) {
        this.protoCe = protoCe;
    }

    @Override
    public <W extends CloudEventWriter<R>, R> R read(
        CloudEventWriterFactory<W, R> writerFactory,
        CloudEventDataMapper<? extends CloudEventData> mapper) throws CloudEventRWException {
        SpecVersion specVersion = SpecVersion.parse(this.protoCe.getSpecVersion());

        final CloudEventWriter<R> writer = writerFactory.create(specVersion);

        // Required attributes
        writer.withContextAttribute(CloudEventV1.ID, this.protoCe.getId());
        writer.withContextAttribute(CloudEventV1.SOURCE, this.protoCe.getSource());
        writer.withContextAttribute(CloudEventV1.TYPE, this.protoCe.getType());

        // Optional attributes
        for (Entry<String, CloudEventAttributeValue> entry : this.protoCe.getAttributesMap().entrySet()) {
            String name = entry.getKey();
            CloudEventAttributeValue val = entry.getValue();
            switch (val.getAttrCase()) {
                case CE_BOOLEAN:
                    writer.withContextAttribute(name, val.getCeBoolean());
                    break;
                case CE_INTEGER:
                    writer.withContextAttribute(name, val.getCeInteger());
                    break;
                case CE_STRING:
                    writer.withContextAttribute(name, val.getCeString());
                    break;
                case CE_BYTES:
                    //@TODO - Upgrade once PR 353 is closed.
                    final byte[] rawBytes = val.getCeBytes().toByteArray();
                    final String base64String = Base64.getEncoder().encodeToString(rawBytes);
                    writer.withContextAttribute(name, base64String);
                    break;
                case CE_URI:
                    writer.withContextAttribute(name, URI.create(val.getCeUri()));
                    break;
                case CE_URI_REF:
                    writer.withContextAttribute(name, URI.create(val.getCeUriRef()));
                    break;
                case CE_TIMESTAMP:
                    com.google.protobuf.Timestamp timestamp = val.getCeTimestamp();
                    Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
                    writer.withContextAttribute(name, instant.atOffset(ZoneOffset.UTC));
                    break;
                case ATTR_NOT_SET:
                    // In the case of an unset attribute, (where they built the object but didn't put anything in it),
                    // treat it as omitted, i.e. do nothing.
            }
        }

        // Process the data
        byte[] raw = null;
        switch (this.protoCe.getDataCase()) {
            case BINARY_DATA:
                raw = this.protoCe.getBinaryData().toByteArray();
                break;
            case TEXT_DATA:
                raw = this.protoCe.getTextData().getBytes(StandardCharsets.UTF_8);
                break;
            case PROTO_DATA:
                raw = this.protoCe.getProtoData().toByteArray();
                break;
            case DATA_NOT_SET:
                // No data to write
                return writer.end();
        }
        CloudEventData data = BytesCloudEventData.wrap(raw);
        return writer.end(mapper.map(data));
    }
}
