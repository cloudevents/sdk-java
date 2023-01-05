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

import io.cloudevents.rw.CloudEventRWException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class XMLUtils {

    private static final Pattern XML_PATTERN = Pattern.compile("^(application|text)\\/([a-zA-Z]+\\+)?xml(;.*)*$");
    private static final Pattern TEXT_PATTERN = Pattern.compile("^application\\/([a-zA-Z]+\\+)?(xml|json)(;.*)*$");


    // Prevent Construction
    private XMLUtils() {
    }

    /**
     * Parse a byte stream into an XML {@link Document}
     *
     * @param data
     * @return Document
     * @throws CloudEventRWException
     */
    static Document parseIntoDocument(byte[] data) throws CloudEventRWException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(data));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw CloudEventRWException.newOther(e);
        }

    }

    /**
     * Obtain a byte array representation of an {@link Document}
     *
     * @param doc {@link Document}
     * @return byte[]
     * @throws TransformerException
     */
    static byte[] documentToBytes(Document doc) throws TransformerException {

        // Build our transformer
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer t = tFactory.newTransformer();

        // Assign the source and result
        Source src = new DOMSource(doc);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(os);

        // Write out the document
        t.transform(src, result);

        // And we're done
        return os.toByteArray();
    }

    /**
     * Get the number of child elements of an {@link Element}
     *
     * @param e The {@link Element} to introspect.
     * @return The count of child elements
     */
    static int countOfChildElements(Element e) {

        if (e == null) {
            return 0;
        }


        int retVal = 0;

        NodeList nodeLIst = e.getChildNodes();

        for (int i = 0; i < nodeLIst.getLength(); i++) {
            final Node n = nodeLIst.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE) {
                retVal++;
            }
        }

        return retVal;
    }

    /**
     * Determine if the given content-type string indicates XML content.
     * @param contentType
     * @return
     */
    static boolean isXmlContent(String contentType){

        if (contentType == null){
            return false;
        }

        final Matcher m = XML_PATTERN.matcher(contentType);

        return m.matches();

    }

    /**
     * Detemrine if the given content-type indicates textual content.
     * @param contentType
     * @return
     */
    static boolean isTextContent(String contentType) {

        if (contentType == null) {
            return false;
        }

        if (contentType.startsWith("text/")) {
            return true;
        }

        final Matcher m = TEXT_PATTERN.matcher(contentType);

        return m.matches();

    }
}
