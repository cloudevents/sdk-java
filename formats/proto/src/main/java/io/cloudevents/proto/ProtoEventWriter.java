package io.cloudevents.proto;


import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * A {@Link CloudEventWriter} that populates a protobuf structure
 */
class ProtoEventWriter implements CloudEventWriter<io.cloudevents.CloudEvent> {

    private final io.cloudevents.v1.proto.CloudEvent.Builder builder;
    private final SpecVersion specVersion;

    public ProtoEventWriter()
    {
        builder = io.cloudevents.v1.proto.CloudEvent.newBuilder();

        specVersion = SpecVersion.V1;
        builder.setSpecVersion(specVersion.toString());

    }

    // Writer ------------------------------------------------------------------

    @Override
    public io.cloudevents.CloudEvent end(CloudEventData data) throws CloudEventRWException
    {

        if (data != null) {

            // Check for our known types

            if (data instanceof ProtoCloudEventData) {

                ProtoCloudEventData pced = (ProtoCloudEventData) data;
                builder.setProtoData(Any.pack(pced.getMessage()));

            } else if (data instanceof TextCloudEventData) {

                TextCloudEventData tced = (TextCloudEventData) data;
                builder.setTextData(tced.getText());

            } else {

                String contentType = ProtoUtils.getAttributeAsString(builder, "datacontenttype");

                if (isTextContent(contentType)) {

                    String txt = new String(data.toBytes());
                    builder.setTextData(txt);

                } else {
                    // Default

                    builder.setBinaryData(ByteString.copyFrom(data.toBytes()));
                }
            }
        }
        // Done.
        return end();
    }

    @Override
    public io.cloudevents.CloudEvent end() throws CloudEventRWException
    {

        // Finalize the Protobuf Message
        return new ProtoCloudEvent(builder.build());
    }

    // Context Writer ----------------------------------------------------------

    @Override
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException
    {

        if (value == null) {
            return this;
        }

        if ("time".equalsIgnoreCase(name)) {
            return withContextAttribute(name, OffsetDateTime.parse(value));
        }

        if (isRequiredAttribute(name)) {

            setRequiredAttribute(name, value);

        } else {

            CloudEventAttributeValue.Builder ab = CloudEventAttributeValue.newBuilder();

            ab.setCeString(value);

            builder.putAttributes(name, ab.build());
        }

        return this;
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, URI value) throws CloudEventRWException
    {

        if (value != null) {

            if (isRequiredAttribute(name)) {
                setRequiredAttribute(name, value.toString());
            } else {

                CloudEventAttributeValue.Builder ab = CloudEventAttributeValue.newBuilder();

                ab.setCeUri(value.toString());

                builder.putAttributes(name, ab.build());
            }

        }

        return this;
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, OffsetDateTime value) throws CloudEventRWException
    {

        if (value != null) {

            Timestamp protoTimestamp = Timestamps.fromMillis(value.toInstant().toEpochMilli());

            CloudEventAttributeValue.Builder ab = CloudEventAttributeValue.newBuilder();
            ab.setCeTimestamp(protoTimestamp);

            builder.putAttributes(name, ab.build());
        }
        return this;
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, Number value) throws CloudEventRWException
    {
        if (value != null) {

            if (value instanceof Integer) {
                withContextAttribute(name, (Integer) value);
            } else {
                withContextAttribute(name, value.toString());
            }
        }
        return this;

    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, Boolean value) throws CloudEventRWException
    {

        if (value != null) {

            CloudEventAttributeValue.Builder ab = CloudEventAttributeValue.newBuilder();

            ab.setCeBoolean(value);

            builder.putAttributes(name, ab.build());
        }

        return this;
    }

    // @Override @TODO - this is an override in the future
    public CloudEventContextWriter withContextAttribute(String name, byte[] value) throws CloudEventRWException
    {

        if (value != null) {

            CloudEventAttributeValue.Builder ab = CloudEventAttributeValue.newBuilder();

            ab.setCeBytes(ByteString.copyFrom(value));

            builder.putAttributes(name, ab.build());
        }

        return this;
    }
    // ---------------------------------------------------------------------

    CloudEventContextWriter withContextAttribute(String name, Object value) throws CloudEventRWException
    {

        if (value != null) {

            if (value instanceof String) {
                withContextAttribute(name, (String) value);
            } else if (value instanceof Integer) {
                withContextAttribute(name, (Integer) value);
            } else if (value instanceof OffsetDateTime) {
                withContextAttribute(name, (OffsetDateTime) value);
            } else if (value instanceof Boolean) {
                withContextAttribute(name, (Boolean) value);
            } else if (value instanceof URI) {
                withContextAttribute(name, (URI) value);
            } else if (value instanceof byte[]) {
                withContextAttribute(name, (byte[]) value);
            }
            else{
                withContextAttribute(name, value.toString());
            }

        }

        return this;
    }

    CloudEventContextWriter withContextAttribute(String name, Integer value) throws CloudEventRWException
    {
        if (value != null) {
            CloudEventAttributeValue.Builder ab = CloudEventAttributeValue.newBuilder();

            ab.setCeInteger(value);

            builder.putAttributes(name, ab.build());
        }

        return this;
    }

    private boolean isRequiredAttribute(String name)
    {

        return specVersion.getMandatoryAttributes().contains(name);

    }

    private void setRequiredAttribute(String name, String value)
    {

        switch (name) {
            case "id":
                builder.setId(value);
                break;
            case "type":
                builder.setType(value);
                break;
            case "source":
                builder.setSource(value);
                break;
            case "specversion":
                builder.setSpecVersion(value);
                break;
        }
    }

    private boolean isTextContent(String contentType)
    {

        /**
         * DUBIOUS
         */

        final List<String> textTypes = Arrays.asList("application/json", "application/xml");

        // Sanity.
        if (contentType == null) {
            return false;
        }

        if (contentType.startsWith("text")) {
            return true;
        }

        return textTypes.contains(contentType);

    }

}
