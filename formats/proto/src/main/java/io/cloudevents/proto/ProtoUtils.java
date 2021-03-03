package io.cloudevents.proto;

import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEventOrBuilder;

class ProtoUtils {


    static String getStringAttribute(CloudEventOrBuilder ce, String key)
    {

        String retVal;

        try {

            CloudEvent.CloudEventAttributeValue attr;

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
