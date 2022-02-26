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

class XMLUtils {

    // Prevent Construction
    private XMLUtils() {
    }

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
     * @param e The Element to introspect.
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
}
