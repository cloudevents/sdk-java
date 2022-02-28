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
package io.cloudevents.xml;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.rw.*;
import io.cloudevents.types.Time;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.net.URI;
import java.util.Base64;

class XMLDeserializer implements CloudEventReader {

    private final Document xmlDocument;

    XMLDeserializer(Document doc) {
        this.xmlDocument = doc;
    }

    // CloudEventReader -------------------------------------------------------

    @Override
    public <W extends CloudEventWriter<R>, R> R read(
        CloudEventWriterFactory<W, R> writerFactory,
        CloudEventDataMapper<? extends CloudEventData> mapper) throws CloudEventRWException {

        // Grab the Root and ensure it's what we expect.
        final Element root = xmlDocument.getDocumentElement();

        checkValidRootElement(root);

        // Get the specversion and build the CE Writer
        String specVer = root.getAttribute("specversion");

        if (specVer == null) {
            throw CloudEventRWException.newInvalidSpecVersion("null - Missing XML attribute");
        }

        final SpecVersion specVersion = SpecVersion.parse(specVer);
        final CloudEventWriter<R> writer = writerFactory.create(specVersion);

        // Now iterate through the elements

        NodeList nodes = root.getChildNodes();
        Element dataElement = null;

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element e = (Element) node;

                // Sanity
                ensureValidContextAttribute(e);

                // Grab all the useful markers.
                final String attrName = e.getLocalName();
                final String attrType = extractAttributeType(e);
                final String attrValue = e.getTextContent();

                // Check if this is a Required or Optional attribute
                if (specVersion.getAllAttributes().contains(attrName)) {
                    // Yep .. Just write it out.
                    writer.withContextAttribute(attrName, attrValue);
                } else {
                    if (XMLConstants.XML_DATA_ELEMENT.equals(attrName)) {
                        // Just remember the data node for now..
                        dataElement = e;
                    } else {
                        // Handle the extension attributes
                        switch (attrType) {
                            case XMLConstants.CE_ATTR_STRING:
                                writer.withContextAttribute(attrName, attrValue);
                                break;
                            case XMLConstants.CE_ATTR_INTEGER:
                                writer.withContextAttribute(attrName, Integer.valueOf(attrValue));
                                break;
                            case XMLConstants.CE_ATTR_TIMESTAMP:
                                writer.withContextAttribute(attrName, Time.parseTime(attrValue));
                                break;
                            case XMLConstants.CE_ATTR_BOOLEAN:
                                writer.withContextAttribute(attrName, Boolean.valueOf(attrValue));
                                break;
                            case XMLConstants.CE_ATTR_URI:
                                writer.withContextAttribute(attrName, URI.create(attrValue));
                                break;
                            case XMLConstants.CE_ATTR_URI_REF:
                                writer.withContextAttribute(attrName, URI.create(attrValue));
                                break;
                            case XMLConstants.CE_ATTR_BINARY:
                                writer.withContextAttribute(attrName, Base64.getDecoder().decode(attrValue));
                                break;
                        }
                    }
                }
            }
        }

        // And handle any data

        if (dataElement != null) {
            return writer.end(processData(dataElement));
        } else {
            return writer.end();
        }

    }

    // Private Methods --------------------------------------------------------

    /**
     * Geyt the first child {@link Element} of an {@link Element}
     *
     * @param e
     * @return The first child, or NULL if there isn't one.
     */
    private Element findFirstElement(Element e) {

        NodeList nodeList = e.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) n;
            }
        }

        return null;
    }

    /**
     * Process the business event data of the XNML Formatted
     * event.
     * <p>
     * This may result in an XML specific data wrapper being returned
     * depending on payload.
     *
     * @param data
     * @return {@link CloudEventData} The data wrapper.
     * @throws CloudEventRWException
     */
    private CloudEventData processData(Element data) throws CloudEventRWException {
        CloudEventData retVal = null;

        final String attrType = extractAttributeType(data);

        // Process based on the defined `xsi:type` of the data element.

        switch (attrType) {
            case XMLConstants.CE_DATA_ATTR_TEXT:
                retVal = new TextCloudEventData(data.getTextContent());
                break;
            case XMLConstants.CE_DATA_ATTR_BINARY:
                String eData = data.getTextContent();
                retVal = BytesCloudEventData.wrap(Base64.getDecoder().decode(eData));
                break;
            case XMLConstants.CE_DATA_ATTR_XML:
                try {
                    // Ensure it's acceptable before we move forward.
                    ensureValidDataElement(data);

                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    Document newDoc = dbf.newDocumentBuilder().newDocument();

                    Element eventData = findFirstElement(data);

                    Element newRoot = newDoc.createElementNS(eventData.getNamespaceURI(), eventData.getLocalName());
                    newDoc.appendChild(newRoot);

                    // Copy the children...
                    NodeList nodesToCopy = eventData.getChildNodes();

                    for (int i = 0; i < nodesToCopy.getLength(); i++) {
                        Node n = nodesToCopy.item(i);

                        if (n.getNodeType() == Node.ELEMENT_NODE) {
                            Node newNode = newDoc.importNode(n, true);
                            newRoot.appendChild(newNode);
                        }
                    }

                    newDoc.normalizeDocument();
                    retVal = XMLCloudEventData.wrap(newDoc);

                } catch (ParserConfigurationException e) {
                    throw CloudEventRWException.newDataConversion(e, null, null);
                }
                break;
            default:
                // I don't believe this is reachable
                break;
        }

        return retVal;
    }

    /**
     * Ensure that the root elemement of the received XML document is valid
     * in our context.
     *
     * @param e The root {@link Element}
     * @throws CloudEventRWException
     */
    private void checkValidRootElement(Element e) throws CloudEventRWException {

        // It must be the name we expect.
        if (!XMLConstants.XML_ROOT_ELEMENT.equals(e.getLocalName())) {
            throw CloudEventRWException.newInvalidDataType(e.getLocalName(), XMLConstants.XML_ROOT_ELEMENT);
        }

        // It must be in the CE namespace.
        if (!XMLConstants.CE_NAMESPACE.equalsIgnoreCase(e.getNamespaceURI())) {
            throw CloudEventRWException.newInvalidDataType(e.getNamespaceURI(), "Namespace: " + XMLConstants.CE_NAMESPACE);
        }
    }

    /**
     * Ensure the XML `data` element is well formed.
     *
     * @param dataEl
     * @throws CloudEventRWException
     */
    private void ensureValidDataElement(Element dataEl) throws CloudEventRWException {

        // It must have a single child
        final int childCount = XMLUtils.countOfChildElements(dataEl);
        if (childCount != 1) {
            throw CloudEventRWException.newInvalidDataType("data has " + childCount + " children", "1 expected");
        }

        // And there must be a valid type descriminator
        final String xsiType = dataEl.getAttribute(XMLConstants.XSI_TYPE);

        if (xsiType == null) {
            throw CloudEventRWException.newInvalidDataType("NULL", "xsi:type oneOf [xs:base64Binary, xs:string, xs:any]");
        }
    }

    /**
     * Ensure a CludEvent context attribute representation is as expected.
     *
     * @param el
     * @throws CloudEventRWException
     */
    private void ensureValidContextAttribute(Element el) throws CloudEventRWException {

        final String localName = el.getLocalName();

        // It must be in our namespace
        if (!XMLConstants.CE_NAMESPACE.equals(el.getNamespaceURI())) {
            final String allowedTxt = el.getLocalName() + " Expected namespace: " + XMLConstants.CE_NAMESPACE;
            throw CloudEventRWException.newInvalidDataType(el.getNamespaceURI(), allowedTxt);
        }

        // It must be all lowercase
        if (!allLowerCase(localName)) {
            throw CloudEventRWException.newInvalidDataType(localName, " context atttribute names MUST be lowercase");
        }

        // A bit kludgy, not relevent for 'data' - should refactor
        if (!XMLConstants.XML_DATA_ELEMENT.equals(localName)) {
            // It must not have any children
            if (XMLUtils.countOfChildElements(el) != 0) {
                throw CloudEventRWException.newInvalidDataType(el.getLocalName(), "Unexpected child element(s)");
            }
        }

    }

    private String extractAttributeType(Element e) {

        final Attr a = e.getAttributeNodeNS(XMLConstants.XSI_NAMESPACE, "type");

        if (a != null) {
            return a.getValue();
        } else {
            return null;
        }
    }

    private boolean allLowerCase(String s) {
        if (s == null) {
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    // DataWrapper Inner Classes

    public class TextCloudEventData implements CloudEventData {

        private final String text;

        TextCloudEventData(String text) {
            this.text = text;
        }

        @Override
        public byte[] toBytes() {
            return text.getBytes();
        }
    }

}
