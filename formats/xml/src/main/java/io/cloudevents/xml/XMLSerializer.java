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

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.rw.CloudEventContextReader;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import io.cloudevents.types.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Base64;

class XMLSerializer {

    /**
     * Convert a CloudEvent to an XML {@link Document}.
     *
     * @param ce
     * @return
     */
    static Document toDocument(CloudEvent ce) {

        // Set up the writer
        XMLCloudEventWriter eventWriter = new XMLCloudEventWriter(ce.getSpecVersion());

        // Process the Context Attributes
        final CloudEventContextReader cloudEventContextReader = CloudEventUtils.toContextReader(ce);
        cloudEventContextReader.readContext(eventWriter);

        // Now handle the Data

        final CloudEventData data = ce.getData();
        if (data != null) {
            return eventWriter.end(data);
        } else {
            return eventWriter.end();
        }
    }

    private static class XMLCloudEventWriter implements CloudEventWriter<Document> {

        private final Document xmlDocument;
        private final Element root;
        private final SpecVersion specVersion;
        private String dataContentType;

        XMLCloudEventWriter(SpecVersion specVersion) throws EventSerializationException {

            this.specVersion = specVersion;

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder xmlBuilder = null;
            try {
                xmlBuilder = dbf.newDocumentBuilder();
                xmlDocument = xmlBuilder.newDocument();
            } catch (ParserConfigurationException e) {
                throw new EventSerializationException(e);
            }

            // Start the Document
            root = xmlDocument.createElementNS(XMLConstants.CE_NAMESPACE, XMLConstants.XML_ROOT_ELEMENT);
            root.setAttribute("xmlns:xs", XMLConstants.XS_NAMESPACE);
            root.setAttribute("xmlns:xsi", XMLConstants.XSI_NAMESPACE);
            root.setAttribute("specversion", specVersion.toString());
            xmlDocument.appendChild(root);
        }

        /**
         * Add a context attribute to the root element.
         *
         * @param name
         * @param xsiType
         * @param value
         */
        private void addElement(String name, String xsiType, String value) {

            Element e = xmlDocument.createElement(name);

            // If this is one of the REQUIRED or OPTIONAL context attributes then we
            // don't need to communicate the type information.

            if (!specVersion.getAllAttributes().contains(name)) {
                e.setAttribute(XMLConstants.XSI_TYPE, xsiType);
            }

            e.setTextContent(value);

            root.appendChild(e);

            // Look for, and remember, the data content type
            if ("datacontenttype".equals(name)) {
                dataContentType = value;
            }
        }

        private void writeXmlData(Document dataDoc) {

            // Create the wrapper
            Element e = xmlDocument.createElement("data");
            e.setAttribute(XMLConstants.XSI_TYPE, XMLConstants.CE_DATA_ATTR_XML);
            root.appendChild(e);

            // Get the Root Element
            Element dataRoot = dataDoc.getDocumentElement();

            // Copy the element into our document
            Node newNode = xmlDocument.importNode(dataRoot, true);

            // And add it to data holder.
            e.appendChild(newNode);
        }

        private void writeXmlData(byte[] data) {
            writeXmlData(XMLUtils.parseIntoDocument(data));
        }

        // CloudEvent Writer ------------------------------------------------------------

        @Override
        public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {

            addElement(name, XMLConstants.CE_ATTR_STRING, value);
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, URI value) throws CloudEventRWException {

            addElement(name, XMLConstants.CE_ATTR_URI, value.toString());
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, OffsetDateTime value) throws CloudEventRWException {

            addElement(name, XMLConstants.CE_ATTR_TIMESTAMP, Time.writeTime(value));
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, Number value) throws CloudEventRWException {

            if (value instanceof Integer integer) {
                return withContextAttribute(name, integer);
            } else {
                return withContextAttribute(name, String.valueOf(value));
            }
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, Integer value) throws CloudEventRWException {

            addElement(name, XMLConstants.CE_ATTR_INTEGER, value.toString());
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, Boolean value) throws CloudEventRWException {

            addElement(name, XMLConstants.CE_ATTR_BOOLEAN, value.toString());
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, byte[] value) throws CloudEventRWException {

            addElement(name, XMLConstants.CE_ATTR_BINARY, Base64.getEncoder().encodeToString(value));
            return this;
        }

        @Override
        public Document end(CloudEventData data) throws CloudEventRWException {

            if (data instanceof XMLCloudEventData eventData) {
                writeXmlData(eventData.getDocument());
            } else if (XMLUtils.isXmlContent(dataContentType)) {
                writeXmlData(data.toBytes());
            } else if (XMLUtils.isTextContent(dataContentType)) {
                // Handle Textual Content
                addElement("data", XMLConstants.CE_DATA_ATTR_TEXT, new String(data.toBytes()));
            } else {
                // Handle Binary Content
                final String encodedValue = Base64.getEncoder().encodeToString(data.toBytes());
                addElement("data", XMLConstants.CE_DATA_ATTR_BINARY, encodedValue);
            }
            return end();
        }

        @Override
        public Document end() throws CloudEventRWException {
            return xmlDocument;
        }
    }
}
