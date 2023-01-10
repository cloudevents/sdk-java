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

import io.cloudevents.CloudEventData;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class XMLDataWrapperTest {

    @Test
    /**
     * Verify that the extension attributes are correctly
     * handled.
     */
    public void verifyWrapping() throws IOException {

        byte[] raw = TestUtils.getData("v1/min.xml");
        Document d = XMLUtils.parseIntoDocument(raw);

        CloudEventData cde = XMLCloudEventData.wrap(d);
        assertThat(cde).isNotNull();
        assertThat(cde).isInstanceOf(CloudEventData.class);

        // We should be able to get the byte data
        byte[] data = cde.toBytes();
        assertThat(data).isNotNull();
        assertThat(data).isNotEmpty();

        // Now verify our variant
        XMLCloudEventData xcde = (XMLCloudEventData) cde;
        assertThat(xcde.getDocument()).isNotNull();

    }
}
