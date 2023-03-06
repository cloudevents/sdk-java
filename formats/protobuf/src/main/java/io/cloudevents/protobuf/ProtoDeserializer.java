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

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.rw.*;
import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map.Entry;

/**
 * Implements a {@link CloudEventReader} that can deserialize a {@link CloudEvent} protobuf representation;
 */
class ProtoDeserializer implements CloudEventReader {
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
                    writer.withContextAttribute(name, val.getCeBytes().toByteArray());
                    break;
                case CE_URI:
                    writer.withContextAttribute(name, URI.create(val.getCeUri()));
                    break;
                case CE_URI_REF:
                    writer.withContextAttribute(name, URI.create(val.getCeUriRef()));
                    break;
                case CE_TIMESTAMP:
                    com.google.protobuf.Timestamp timestamp = val.getCeTimestamp();
                    writer.withContextAttribute(name, covertProtoTimestamp(timestamp));
                    break;
                case ATTR_NOT_SET:
                    // In the case of an unset attribute, (where they built the object but didn't put anything in it),
                    // treat it as omitted, i.e. do nothing.
            }
        }

        // Process the data
        CloudEventData data = null;
        byte[] raw = null;

        switch (this.protoCe.getDataCase()) {
            case BINARY_DATA:
                raw = this.protoCe.getBinaryData().toByteArray();
                data = BytesCloudEventData.wrap(raw);
                break;
            case TEXT_DATA:
                raw = this.protoCe.getTextData().getBytes(StandardCharsets.UTF_8);
                data = BytesCloudEventData.wrap(raw);
                break;
            case PROTO_DATA:
                data = new ProtoDataWrapper(this.protoCe.getProtoData());
                break;
            case DATA_NOT_SET:
                break;
        }

        if (data != null) {
            return writer.end(mapper.map(data));
        } else {
            return writer.end();
        }

    }

    /**
     * Convert a {@link com.google.protobuf.Timestamp} to a {@link OffsetDateTime}. Note that protobuf timestamps are assumed
     * to be in UTC time, so the resulting OffsetDateTime will also be.
     * @param timestamp The timestamp to convert
     * @return An OffsetDateTime representing the input protobuf timestamp
     */
    private OffsetDateTime covertProtoTimestamp(com.google.protobuf.Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return instant.atOffset(ZoneOffset.UTC);
    }

}
