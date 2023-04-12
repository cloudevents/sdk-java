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

import com.google.protobuf.InvalidProtocolBufferException;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.ContentType;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.rw.CloudEventDataMapper;

/**
 * An implemmentation of {@link EventFormat} for <a href="github.com/cloudevents/spec/blob/v1.0.1/protobuf-format">the protobuf format</a>.
 * This format is resolvable with {@link io.cloudevents.core.provider.EventFormatProvider} using the content type {@link #PROTO_CONTENT_TYPE}.
 * The data payload in the CloudEvent can also be a protobuf; in this case, you must wrap the data in {@link com.google.protobuf.Any} and set the
 * "datacontenttype" to {@link #PROTO_DATA_CONTENT_TYPE}.
 * <p>
 * This {@link EventFormat} only works for {@link io.cloudevents.SpecVersion#V1}, as that was the first version the protobuf format was defined for.
 */
public class ProtobufFormat implements EventFormat {

    /**
     * The content type for transports sending cloudevents in the protocol buffer format.
     */
    public static final String PROTO_CONTENT_TYPE = "application/cloudevents+protobuf";
    /**
     * The content type to set for the "datacontenttype" attribute if the data is stored in protocol buffer format.
     * Note that if this content type is used, the stored data must be wrapped in {@link com.google.protobuf.Any} as specified by the protobuf format spec.
     */
    public static final String PROTO_DATA_CONTENT_TYPE = "application/protobuf";


    @Override
    public byte[] serialize(CloudEvent event) throws EventSerializationException {
        try {
            final io.cloudevents.v1.proto.CloudEvent asProto = ProtoSerializer.toProto(event);
            return asProto.toByteArray();
        } catch (InvalidProtocolBufferException e) {
            throw new EventSerializationException(e);
        }
    }

    @Override
    public CloudEvent deserialize(byte[] bytes, CloudEventDataMapper<? extends CloudEventData> mapper)
	    throws EventDeserializationException {
        try {
            final io.cloudevents.v1.proto.CloudEvent ceProto = io.cloudevents.v1.proto.CloudEvent.parseFrom(bytes);
            return new ProtoDeserializer(ceProto).read(CloudEventBuilder::fromSpecVersion);
        } catch (InvalidProtocolBufferException e) {
            throw new EventDeserializationException(e);
        }
    }

    @Override
    public String serializedContentType() {
        return PROTO_CONTENT_TYPE;
    }

}
