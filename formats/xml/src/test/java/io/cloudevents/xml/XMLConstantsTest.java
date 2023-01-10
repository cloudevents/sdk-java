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

import static org.assertj.core.api.Assertions.assertThat;

public class XMLConstantsTest {

    @Test
    public void verifyNS() {
        assertThat(XMLConstants.CE_NAMESPACE).isEqualTo("http://cloudevents.io/xmlformat/V1");
    }

    public void verifyContextAttributeTypes() {
        assertThat(XMLConstants.isCloudEventAttributeType("ce:boolean")).isTrue();
        assertThat(XMLConstants.isCloudEventAttributeType("ce:integer")).isTrue();
        assertThat(XMLConstants.isCloudEventAttributeType("ce:string")).isTrue();
        assertThat(XMLConstants.isCloudEventAttributeType("ce:binary")).isTrue();
        assertThat(XMLConstants.isCloudEventAttributeType("ce:uri")).isTrue();
        assertThat(XMLConstants.isCloudEventAttributeType("ce:uriRef")).isTrue();
        assertThat(XMLConstants.isCloudEventAttributeType("ce:timestamp")).isTrue();

        assertThat(XMLConstants.CE_ATTR_LIST.size()).isEqualTo(7);
    }

    public void verifyDataTypes() {
        assertThat(XMLConstants.isValidDataType("xs:string")).isTrue();
        assertThat(XMLConstants.isValidDataType("xs:base64Binary")).isTrue();
        assertThat(XMLConstants.isValidDataType("xs:any")).isTrue();

        assertThat(XMLConstants.CE_DATA_ATTRS.size()).isEqualTo(3);

    }
}
