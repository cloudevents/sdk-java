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

public class XMLDeserializer implements CloudEventReader {

    static final String CE_NAMESPACE = "http://cloudevents.io/xmlformat/V1";
    static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    static final String XS_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
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
            // Error
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
                if (isValidAttribute(e)) {

                    // Grab all the useful markers.
                    final String attrName = e.getLocalName();
                    final String attrType = extractAttributeType(e);
                    final String attrValue = e.getTextContent();

                    // Check if this is a Required or Optional attribute
                    if (specVersion.getAllAttributes().contains(attrName)) {
                        writer.withContextAttribute(attrName, attrValue);
                    } else {
                        if ("data".equals(attrName)) {
                            // Just remember the data node for now..
                            dataElement = e;
                        } else {
                            // Handle the extension attributes
                            switch (attrType) {
                                case "xs:string":
                                    writer.withContextAttribute(attrName, attrValue);
                                    break;
                                case "xs:int":
                                    writer.withContextAttribute(attrName, Integer.valueOf(attrValue));
                                    break;
                                case "xs:dateTime":
                                    writer.withContextAttribute(attrName, Time.parseTime(attrValue));
                                    break;
                                case "xs:boolean":
                                    writer.withContextAttribute(attrName, Boolean.valueOf(attrValue));
                                    break;
                                case "xs:anyURI":
                                    writer.withContextAttribute(attrName, URI.create(attrValue));
                                    break;
                                case "xs:base64Binary":
                                    writer.withContextAttribute(attrName, Base64.getDecoder().decode(attrValue));
                                    break;
                            }
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

    private CloudEventData processData(Element data) throws CloudEventRWException {
        CloudEventData retVal = null;

        final String attrType = extractAttributeType(data);

        switch (attrType) {
            case "xs:string":
                retVal = new TextCloudEventData(data.getTextContent());
                break;
            case "xs:base64Binary":
                String eData = data.getTextContent();
                retVal = BytesCloudEventData.wrap(Base64.getDecoder().decode(eData));
                break;
            case "xs:any":
                try {
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
                break;
        }

        return retVal;
    }

    private void checkValidRootElement(Element e) throws CloudEventRWException {

        if (!"event".equals(e.getLocalName())) {
            throw CloudEventRWException.newInvalidDataType(e.getLocalName(), "event");

        }

        if (!CE_NAMESPACE.equalsIgnoreCase(e.getNamespaceURI())) {
            throw CloudEventRWException.newInvalidDataType(e.getNamespaceURI(), "Namespace: " + CE_NAMESPACE);
        }
    }

    private boolean isValidAttribute(Node n) {

        if (!CE_NAMESPACE.equals(n.getNamespaceURI())) {
            return false;
        }

        return allLowerCase(n.getLocalName());

    }

    private String extractAttributeType(Element e) {

        Attr a = e.getAttributeNodeNS(XSI_NAMESPACE, "type");

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
