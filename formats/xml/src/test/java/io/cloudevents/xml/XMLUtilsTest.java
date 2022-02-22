package io.cloudevents.xml;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.assertj.core.api.Assertions.assertThat;

public class XMLUtilsTest {

    @Test
    public void voidTestChildCount() throws ParserConfigurationException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = dbf.newDocumentBuilder().newDocument();

        Element root = doc.createElement("root");
        doc.appendChild(root);

        // NO Children on root thus far
        assertThat(XMLUtils.countOfChildElements(root)).isEqualTo(0);

        // Add a child
        Element c1 = doc.createElement("ChildOne");
        root.appendChild(c1);

        assertThat(XMLUtils.countOfChildElements(root)).isEqualTo(1);

        // Add a another child
        Element c2 = doc.createElement("ChildTwo");
        root.appendChild(c2);

        assertThat(XMLUtils.countOfChildElements(root)).isEqualTo(2);

    }
}
