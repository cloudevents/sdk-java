package io.cloudevents.xml;

import io.cloudevents.rw.CloudEventRWException;
import org.w3c.dom.Document;
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
}
