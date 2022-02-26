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
