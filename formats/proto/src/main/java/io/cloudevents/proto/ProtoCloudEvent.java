package io.cloudevents.proto;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.v1.proto.CloudEvent;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import java.io.StringReader;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;

/**
 * A CloudEvent that is backed by a Google Protobuf structure.
 */
class ProtoCloudEvent implements io.cloudevents.CloudEvent {

    // The spec-compliant protobuf representation

    private final io.cloudevents.v1.proto.CloudEvent protoEvent;
    private final SpecVersion specVersion;

    ProtoCloudEvent(io.cloudevents.v1.proto.CloudEvent protoEvent)
    {

        this.protoEvent = protoEvent;
        this.specVersion = SpecVersion.parse(protoEvent.getSpecVersion());

    }

    @Override
    public CloudEventData getData()
    {

        CloudEventData retVal;

        switch (protoEvent.getDataCase()) {
            case BINARY_DATA:
                retVal = new BinaryDataAccessor(protoEvent);
                break;
            case PROTO_DATA:
                retVal = new ProtoDataAccessor(protoEvent);
                break;
            case TEXT_DATA:
                if ("application/json".equalsIgnoreCase(getDataContentType())) {
                    retVal = new JsonValueAccessor(protoEvent);
                } else {
                    retVal = new TextDataAccessor(protoEvent);
                }
                break;
            default:
                retVal = null;
                break;
        }

        return retVal;
    }

    @Override
    public SpecVersion getSpecVersion()
    {
        return specVersion;
    }

    @Override
    public String getId()
    {
        return protoEvent.getId();
    }

    @Override
    public String getType()
    {
        return protoEvent.getType();
    }

    @Override
    public URI getSource()
    {
        return URI.create(protoEvent.getSource());
    }

    @Override
    public String getDataContentType()
    {
        return getStringAttribute("datacontenttype");
    }

    @Override
    public URI getDataSchema()
    {

        URI retVal;

        try {
            final CloudEvent.CloudEventAttributeValue attr;

            attr = protoEvent.getAttributesOrThrow("dataschema");

            switch (attr.getAttrCase()) {
                case CE_URI:
                    retVal = URI.create(attr.getCeUri());
                    break;
                case CE_STRING:
                    retVal = URI.create(attr.getCeString());
                    break;
                default:
                    retVal = null;
                    break;
            }
        } catch (IllegalArgumentException iae) {
            retVal = null;
        }

        return retVal;
    }

    @Override
    public String getSubject()
    {
        return getStringAttribute("subject");
    }

    @Override
    public OffsetDateTime getTime()
    {

        Object obj = getAttribute("time");
        OffsetDateTime retVal;

        if (obj != null) {

            // Hopefully it's actually a timestamp.
            if (obj instanceof OffsetDateTime) {
                retVal = (OffsetDateTime) obj;
            } else if (obj instanceof String) {
                // Best effort string conversion
                retVal = OffsetDateTime.parse((String) obj);
            } else {
                // Can't win them all.
                retVal = null;
            }
        } else {
            // Attribute not present
            retVal = null;
        }

        return retVal;

    }

    @Override
    public Object getAttribute(String attributeName)
    {

        Object retVal;

        final CloudEvent.CloudEventAttributeValue attr;

        try {
            attr = protoEvent.getAttributesOrThrow(attributeName);

            switch (attr.getAttrCase()) {

                case CE_STRING:
                    retVal = attr.getCeString();
                    break;

                case CE_INTEGER:
                    retVal = attr.getCeInteger();
                    break;

                case CE_BOOLEAN:
                    retVal = attr.getCeBoolean();
                    break;

                case CE_URI:
                    retVal = URI.create(attr.getCeUri());
                    break;

                case CE_URI_REF:
                    retVal = attr.getCeUriRef();
                    break;

                case CE_BYTES:
                    retVal = attr.getCeBytes().toByteArray();
                    break;

                case CE_TIMESTAMP:

                    final Timestamp ts = attr.getCeTimestamp();

                    final Instant anInstant = Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
                    retVal = anInstant.atOffset(ZoneOffset.UTC);
                    break;

                // Should never happen
                default:
                    retVal = null;
                    break;
            }
        } catch (IllegalArgumentException iae) {
            // No Such Attribute Value
            retVal = null;
        }

        return retVal;

    }

    @Override
    public Object getExtension(String extensionName)
    {
        return getAttribute(extensionName);
    }

    @Override
    public Set<String> getExtensionNames()
    {
        final Predicate<String> isAnExtension = x -> !(specVersion.getAllAttributes().contains(x));

        return protoEvent.getAttributesMap().keySet().stream()
            .filter(isAnExtension)
            .collect(toSet());
    }

    // Package ------------------------------------------------------------------------------

    CloudEvent getProtobufMessage()
    {
        return protoEvent;
    }

    // Private ------------------------------------------------------------------------------

    private String getStringAttribute(String key)
    {

        String retVal;

        try {
            CloudEvent.CloudEventAttributeValue attr;

            attr = protoEvent.getAttributesOrThrow(key);

            switch (attr.getAttrCase()) {
                case CE_STRING:
                    retVal = attr.getCeString();
                    break;

                case CE_BOOLEAN:
                    retVal = Boolean.toString(attr.getCeBoolean());
                    break;

                case CE_INTEGER:
                    retVal = Integer.toString(attr.getCeInteger());
                    break;

                case CE_TIMESTAMP:
                    retVal = attr.getCeTimestamp().toString();
                    break;

                case CE_URI:
                    retVal = attr.getCeUri();
                    break;

                case CE_URI_REF:
                    retVal = attr.getCeUriRef();
                    break;

                case CE_BYTES:
                    // Spec dictates Base64 encoding for String representation
                    retVal = Base64.getEncoder().encodeToString(attr.getCeBytes().toByteArray());
                    break;

                default:
                    // Yikes ..
                    retVal = null;
                    break;
            }
        } catch (IllegalArgumentException iae) {

            // Attribute not present
            retVal = null;
        }

        return retVal;

    }

    // Inner Classes --------------------------------------------------------

    /**
     * A Collection of accessors to the 'data' aspect of the event.
     */
    class DataAccessor {
        private final CloudEvent protoEvent;

        protected DataAccessor(CloudEvent protoEvent)
        {
            this.protoEvent = protoEvent;
        }

        protected CloudEvent getProto()
        {
            return protoEvent;
        }
    }

    class BinaryDataAccessor extends DataAccessor implements CloudEventData {

        BinaryDataAccessor(CloudEvent protoEvent)
        {
            super(protoEvent);
        }

        @Override
        public byte[] toBytes()
        {
            return getProto().getBinaryData().toByteArray();
        }
    }

    class TextDataAccessor extends DataAccessor implements TextCloudEventData {

        TextDataAccessor(CloudEvent protoEvent)
        {
            super(protoEvent);
        }

        @Override
        public String getText()
        {
            return getProto().getTextData();
        }

        @Override
        public byte[] toBytes()
        {
            return getText().getBytes();
        }
    }

    class JsonValueAccessor extends TextDataAccessor implements JsonValueCloudEventData {

        JsonValueAccessor(CloudEvent protoEvent)
        {
            super(protoEvent);
        }

        @Override
        public JsonValue getJsonValue()
        {

            JsonReader jReader = Json.createReader(new StringReader(getText()));
            JsonStructure jsonEvent = jReader.read();
            return jsonEvent;

        }
    }

    class ProtoDataAccessor extends DataAccessor implements ProtoCloudEventData {

        ProtoDataAccessor(CloudEvent protoEvent)
        {
            super(protoEvent);
        }

        @Override
        public Message getMessage()
        {
            return getProto().getProtoData();
        }

        @Override
        public byte[] toBytes()
        {
            return getProto().getProtoData().getValue().toByteArray();
        }
    }
}
