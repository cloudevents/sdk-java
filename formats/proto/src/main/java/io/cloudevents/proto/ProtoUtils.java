package io.cloudevents.proto;

import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEventOrBuilder;

import java.util.Base64;

class ProtoUtils {

    private ProtoUtils()
    {
    }

    /**
     * Obtain a {@link String} representation of a protobuf context attribute
     *
     * @param ce
     * @param key
     * @return
     */
    static String getAttributeAsString(CloudEventOrBuilder ce, String key)
    {

        String retVal;

        try {

            final CloudEvent.CloudEventAttributeValue attr;

            attr = ce.getAttributesOrThrow(key);

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
                    retVal = Base64.getEncoder().encodeToString(attr.getCeBytes().toByteArray());
                    break;

                default:
                    // Yikes, Unknown type..
                    // This really should never happen.
                    retVal = null;
                    break;
            }
        } catch (IllegalArgumentException iae) {

            // No attribute with the given name exists.
            retVal = null;
        }

        return retVal;
    }
}
