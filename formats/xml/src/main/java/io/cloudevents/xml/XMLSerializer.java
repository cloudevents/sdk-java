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
     * Convert the CloudEvent to an XML DOM representation.
     *
     * @param ce
     * @return
     */
    static Document toDocument(CloudEvent ce) {

        // Setup the writer
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

        static final String XSI_TYPE = "xsi:type";
        static final String CLOUDEVENT_NAMESPACE = "http://cloudevents.io/xmlformat/V1";
        static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
        static final String XS_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
        static final String CE_ROOT_ELEMENT = "event";

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
            root = xmlDocument.createElementNS(CLOUDEVENT_NAMESPACE, CE_ROOT_ELEMENT);
            root.setAttribute("xmlns:xs", XS_NAMESPACE);
            root.setAttribute("xmlns:xsi", XSI_NAMESPACE);
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
                e.setAttribute(XSI_TYPE, xsiType);
            }
            e.setTextContent(value);

            root.appendChild(e);

            // Look for, and remember, the data content type
            if ("datacontenttype".equals(name)) {
                dataContentType = value;
            }
        }

        /**
         * Need refactoring..
         *
         * @param contentType
         * @return
         */
        private boolean isTextContent(String contentType) {

            if (contentType == null) {
                return false;
            }

            return contentType.startsWith("text/")
                || "application/json".equals(contentType)
                || "application/xml".equals(contentType)
                || contentType.endsWith("+json")
                || contentType.endsWith("+xml")
                ;
        }

        private boolean isXMLContent(String contentType) {
            if (contentType == null) {
                return false;
            }

            return "application/xml".equals(contentType)
                || "text/xml".equals(contentType)
                || contentType.endsWith("+xml")
                ;
        }

        private void writeBinaryData(byte[] data) {
            addElement("data", "xs:base64Binary", Base64.getEncoder().encodeToString(data));
        }

        private void writeXmlData(Document dataDoc) {

            // Create the wrapper
            Element e = xmlDocument.createElement("data");
            e.setAttribute(XSI_TYPE, "xs:any");
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

            addElement(name, "xs:string", value);
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, URI value) throws CloudEventRWException {

            addElement(name, "xs:anyURI", value.toString());
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, OffsetDateTime value) throws CloudEventRWException {

            addElement(name, "xs:dateTime", Time.writeTime(value));
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, Number value) throws CloudEventRWException {

            if (value instanceof Integer) {
                return withContextAttribute(name, (Integer) value);
            } else {
                return withContextAttribute(name, String.valueOf(value));
            }
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, Integer value) throws CloudEventRWException {

            addElement(name, "xs:int", value.toString());
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, Boolean value) throws CloudEventRWException {

            addElement(name, "xs:boolean", value.toString());
            return this;
        }

        @Override
        public CloudEventContextWriter withContextAttribute(String name, byte[] value) throws CloudEventRWException {

            addElement(name, "xs:base64Binary", Base64.getEncoder().encodeToString(value));
            return this;
        }

        @Override
        public Document end(CloudEventData data) throws CloudEventRWException {

            if (data instanceof XMLCloudEventData) {
                writeXmlData(((XMLCloudEventData) data).getElement());
            } else if (isXMLContent(dataContentType)) {
                writeXmlData(data.toBytes());
            } else if (isTextContent(dataContentType)) {
                // Handle Textual Content
                addElement("data", "xs:string", new String(data.toBytes()));
            } else {
                // Handle Binary Content
                writeBinaryData(data.toBytes());
            }
            return end();
        }

        @Override
        public Document end() throws CloudEventRWException {
            return xmlDocument;
        }
    }
}
