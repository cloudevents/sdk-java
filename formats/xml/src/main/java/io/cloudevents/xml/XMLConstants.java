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

import java.util.ArrayList;
import java.util.Collection;

final class XMLConstants {

    // Our Namepace
    static final String CE_NAMESPACE = "http://cloudevents.io/xmlformat/V1";

    // CE Attribute Type Designators
    static final String CE_ATTR_STRING = "ce:string";
    static final String CE_ATTR_BOOLEAN = "ce:boolean";
    static final String CE_ATTR_INTEGER = "ce:integer";
    static final String CE_ATTR_URI = "ce:uri";
    static final String CE_ATTR_URI_REF = "ce:uriRef";
    static final String CE_ATTR_BINARY = "ce:binary";
    static final String CE_ATTR_TIMESTAMP = "ce:timestamp";

    // CE Data Type Designators
    static final String CE_DATA_ATTR_BINARY = "xs:base64Binary";
    static final String CE_DATA_ATTR_TEXT = "xs:string";
    static final String CE_DATA_ATTR_XML = "xs:any";

    // General XML Constants
    static final String XSI_TYPE = "xsi:type";

    // Bundle these into a collection (probably could be made more efficient)
    static final Collection<String> CE_ATTR_LIST = new ArrayList<String>() {{
        add(CE_ATTR_STRING);
        add(CE_ATTR_BOOLEAN);
        add(CE_ATTR_INTEGER);
        add(CE_ATTR_TIMESTAMP);
        add(CE_ATTR_URI);
        add(CE_ATTR_URI_REF);
        add(CE_ATTR_BINARY);
    }};

    static final Collection<String> CE_DATA_ATTRS = new ArrayList<String>() {{
        add(CE_DATA_ATTR_TEXT);
        add(CE_DATA_ATTR_BINARY);
        add(CE_DATA_ATTR_XML);
    }};

    private XMLConstants() {
    }

    static boolean isCloudEventAttributeType(final String type) {
        return CE_ATTR_LIST.contains(type);
    }

    static boolean isValidDataType(final String type) {
        return CE_DATA_ATTRS.contains(type);
    }
}
