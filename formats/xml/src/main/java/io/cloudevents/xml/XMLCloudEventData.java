package io.cloudevents.xml;

import io.cloudevents.CloudEventData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XMLCloudEventData extends CloudEventData {

    /**
     * Get the XML {@link Element} representation of the
     * CloudEvent data.
     *
     * @return The {@link Element} representation.
     */
    Document getElement();

    static CloudEventData wrap(Document xmlDoc) {
        return new XMLDataWrapper(xmlDoc);
    }
}
