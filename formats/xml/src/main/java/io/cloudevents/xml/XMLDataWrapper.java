package io.cloudevents.xml;

import io.cloudevents.rw.CloudEventRWException;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;

class XMLDataWrapper implements XMLCloudEventData {

    private final Document xmlDoc;

    XMLDataWrapper(Document d) {
        this.xmlDoc = d;
    }

    @Override
    public Document getElement() {
        return xmlDoc;
    }

    @Override
    public byte[] toBytes() {
        try {
            return XMLUtils.documentToBytes(xmlDoc);
        } catch (TransformerException e) {
            throw CloudEventRWException.newOther(e);
        }
    }
}
