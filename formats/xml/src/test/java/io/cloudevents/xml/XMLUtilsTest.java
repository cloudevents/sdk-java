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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class XMLUtilsTest {

    @Test
    public void testChildCount() throws ParserConfigurationException {

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

    @ParameterizedTest
    @MethodSource("xmlTestContentTypes")
    public void testXmlContentType(String contentType, boolean expected) {

        Assertions.assertEquals(expected, XMLUtils.isXmlContent(contentType), contentType);
    }

    @ParameterizedTest
    @MethodSource("textTestContentTypes")
    public void testTextContentType(String contentType, boolean expected) {

        Assertions.assertEquals(expected, XMLUtils.isTextContent(contentType), contentType);

    }

    static Stream<Arguments> xmlTestContentTypes() {

        return Stream.of(

            // Good Examples
            Arguments.of("application/xml", true),
            Arguments.of("application/xml;charset=utf-8", true),
            Arguments.of("application/xml;\tcharset = \"utf-8\"", true),
            Arguments.of("application/cloudevents+xml;charset=UTF-8", true),
            Arguments.of("application/cloudevents+xml", true),
            Arguments.of("text/xml", true),
            Arguments.of("text/xml;charset=utf-8", true),
            Arguments.of("text/cloudevents+xml;charset=UTF-8", true),
            Arguments.of("text/xml;\twhatever", true),
            Arguments.of("text/xml; boundary=something", true),
            Arguments.of("text/xml;foo=\"bar\"", true),
            Arguments.of("text/xml; charset = \"us-ascii\"", true),
            Arguments.of("text/xml; \t", true),
            Arguments.of("text/xml;", true),

            // Bad Examples

            Arguments.of("applications/xml", false),
            Arguments.of("application/xmll", false),
            Arguments.of("application/fobar", false),
            Arguments.of("text/json ", false),
            Arguments.of("text/json ;", false),
            Arguments.of("test/xml", false),
            Arguments.of("application/json", false)

        );
    }

    static Stream<Arguments> textTestContentTypes() {

        return Stream.of(

            // Text Content
            Arguments.of("text/foo", true),
            Arguments.of("text/plain", true),
            Arguments.of("application/xml", true),
            Arguments.of("application/json", true),
            Arguments.of("application/foo+json", true),

            // Not Text Content
            Arguments.of("image/png", false)

        );
    }

}
