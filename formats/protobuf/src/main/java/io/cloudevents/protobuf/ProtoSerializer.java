package io.cloudevents.protobuf;

import static io.cloudevents.protobuf.ProtobufFormat.PROTO_DATA_CONTENT_TYPE;
import static io.cloudevents.v1.proto.CloudEvent.ID_FIELD_NUMBER;
import static io.cloudevents.v1.proto.CloudEvent.SOURCE_FIELD_NUMBER;
import static io.cloudevents.v1.proto.CloudEvent.SPEC_VERSION_FIELD_NUMBER;
import static io.cloudevents.v1.proto.CloudEvent.TYPE_FIELD_NUMBER;
import static io.cloudevents.v1.proto.CloudEvent.getDescriptor;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.rw.CloudEventContextReader;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.Builder;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Provides functionality for turning a {@link io.cloudevents.CloudEvent} to the protobuf representation {@link CloudEvent}.
 */
public class ProtoSerializer {

    /**
     * Convert the Java SDK CloudEvent into a protobuf representation.
     *
     * <p> If the data payload is set with "datacontenttype" set to "application/protobuf", the data provided
     * must be wrapped in {@link com.google.protobuf.Any}. Storing protobuf data in an Any is required by the
     * CloudEvent protobuf format spec.
     *
     * @param ce The {@link io.cloudevents.CloudEvent} to convert.
     * @return A {@link CloudEvent} protobuf object that can be serialized to the protobuf binary format.
     * @throws InvalidProtocolBufferException If the content type of the data is "application/protobuf" but
     *  the data is not wrapped in {@link com.google.protobuf.Any}.
     */
    public static CloudEvent toProto(io.cloudevents.CloudEvent ce) throws InvalidProtocolBufferException {
        CloudEvent.Builder builder = CloudEvent.newBuilder();

        builder.setSpecVersion(ce.getSpecVersion().toString());

        // Copy the attributes
        ProtoContextWriter protoContextWriter = new ProtoContextWriter(builder, ce.getSpecVersion());
        final CloudEventContextReader cloudEventContextReader = CloudEventUtils.toContextReader(ce);
        cloudEventContextReader.readContext(protoContextWriter);

        // Copy the data
        final String contentType = ce.getDataContentType();
        final CloudEventData data = ce.getData();
        if (data != null) {
            if (Objects.equals(contentType, PROTO_DATA_CONTENT_TYPE)) {
                // This will throw if the data provided is not an Any. The protobuf CloudEvent spec requires proto data to be stored as
                // an Any. I would be amenable to allowing people to also pass raw unwrapped protobufs, but it complicates the logic here.
                // Perhpas that can be a follow up if there is a need.
                Any dataAsAny = Any.parseFrom(data.toBytes());
                builder.setProtoData(dataAsAny);
            } else if (isTextType(contentType)) {
                builder.setTextDataBytes(ByteString.copyFrom(data.toBytes()));
            } else {
                ByteString byteString = ByteString.copyFrom(data.toBytes());
                builder.setBinaryData(byteString);
            }
        }

        return builder.build();
    }

    // The proto spec says all text data should go into the text field. It is really difficult to figure out every case
    // of text data based on the media type though, so I am just going to check for some common cases.
    private static boolean isTextType(String type) {
        if (type == null) {
            return false;
        }
        return type.startsWith("text/") || "application/json".equals(type);
    }

    /**
     * Defines a {@link CloudEventContextWriter} that will allow setting the attributes within a Protobuf object.
     */
    public static class ProtoContextWriter implements CloudEventContextWriter {

        /**
         * CloudEvent required attributes are stored as plain fields in the protobuf format, while optional
         * attributes are set in a map. This field mapping allows us to identify where in the protobuf CloudEvent message a
         * required field should be placed.
         */
        private final static Map<String, FieldDescriptor> attributeToFieldNumV1;
        /**
         * Contains a mapping of CloudEvent required attributes to protobuf field descriptors by
         * CloudEvent specification version. This mapping is intended to make supporting future versions
         * easy, by simply adding a new mapping in this class.
         */
        private final static Map<SpecVersion, Map<String, FieldDescriptor>> versionToAttrs;
        static {
            Map<String, FieldDescriptor> tmp = new HashMap<>();
            tmp.put(CloudEventV1.ID, getDescriptor().findFieldByNumber(ID_FIELD_NUMBER));
            tmp.put(CloudEventV1.SOURCE, getDescriptor().findFieldByNumber(SOURCE_FIELD_NUMBER));
            tmp.put(CloudEventV1.TYPE, getDescriptor().findFieldByNumber(TYPE_FIELD_NUMBER));
            tmp.put(CloudEventV1.SPECVERSION, getDescriptor().findFieldByNumber(SPEC_VERSION_FIELD_NUMBER));
            attributeToFieldNumV1 = Collections.unmodifiableMap(tmp);

            versionToAttrs = Collections.singletonMap(SpecVersion.V1, attributeToFieldNumV1);
        }

        private final Builder protoBuilder;
        private final Map<String, FieldDescriptor> requiredAttributeNumberMap;

        /**
         * Create a new ProtoContextWRiter that will set attributes on the provided proto builder.
         * @param protoBuilder The {@link CloudEvent.Builder} to set attributes on.
         * @param spec The spec to set attributes for.
         */
        public ProtoContextWriter(CloudEvent.Builder protoBuilder, SpecVersion spec) {
            this.protoBuilder = protoBuilder;
            this.requiredAttributeNumberMap = versionToAttrs.get(spec);
            if (this.requiredAttributeNumberMap == null) {
                throw CloudEventRWException.newInvalidSpecVersion(spec.toString());
            }
        }

        /**
         * Checks if this attribute is a required field, and if so, set it. This is slightly
         * wonky because in the protobuf representation for cloud events, required attributes are
         * fields, whereas optional attributes and extensions are stored in a map.
         *
         * @param name The name of the attribute
         * @param value The value to set
         * @return true if name was a required field that was set, false otherwise.
         */
        private boolean setRequiredField(String name, Object value) {
            FieldDescriptor fieldDescriptor = this.requiredAttributeNumberMap.get(name);
            if (fieldDescriptor == null) {
                return false;
            }
            this.protoBuilder.setField(fieldDescriptor, value);
            return true;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, String value)
            throws CloudEventRWException {
            if (!setRequiredField(name, value)) {
                this.protoBuilder.putAttributes(name,
                    CloudEventAttributeValue.newBuilder().setCeString(value).build());
            }
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, URI value)
            throws CloudEventRWException {
            if (!setRequiredField(name, value.toString())) {
                // This is a bit of a hack. The java SDK doesn't differentiate between absolute and reference for its URIs, unlike the protobuf representation.
                // So here, if we are certain this is an absolute URI, then put it in the URI type, otherwise the UriRef type.
                final CloudEventAttributeValue.Builder builder = CloudEventAttributeValue.newBuilder();
                if (value.isAbsolute()) {
                    this.protoBuilder.putAttributes(name, builder.setCeUri(value.toString()).build());
                } else {
                    this.protoBuilder.putAttributes(name, builder.setCeUriRef(value.toString()).build());
                }
            }
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, OffsetDateTime value)
            throws CloudEventRWException {
            Instant instant = value.toInstant();
            Timestamp ts = Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
            if (!setRequiredField(name, ts)) {
                this.protoBuilder.putAttributes(name, CloudEventAttributeValue.newBuilder().setCeTimestamp(ts).build());
            }
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, Number value)
            throws CloudEventRWException {
            if (!setRequiredField(name, value.intValue())) {
                final CloudEventAttributeValue.Builder builder = CloudEventAttributeValue.newBuilder();
                // Int values are only supported by the proto spec so far
                builder.setCeInteger(value.intValue());
                this.protoBuilder.putAttributes(name, builder.build());
            }
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, Boolean value)
            throws CloudEventRWException {
            if (!setRequiredField(name, value)) {
                this.protoBuilder.putAttributes(name, CloudEventAttributeValue.newBuilder().setCeBoolean(value).build());
            }
            return this;
        }
    }

}
