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
import org.w3c.dom.Document;

/**
 * A variant of {@link CloudEventData} that supports direct access
 * to data as an XML {@link Document}
 */
public interface XMLCloudEventData extends CloudEventData {

    /**
     * Get an XML Document representation of the
     * CloudEvent data.
     *
     * @return The {@link Document} representation.
     */
    Document getDocument();

    /**
     * Wraps an XML {@link Document}
     *
     * @param xmlDoc {@link Document}
     * @return The wrapping {@link XMLCloudEventData}
     */
    static CloudEventData wrap(Document xmlDoc) {
        return new XMLDataWrapper(xmlDoc);
    }
}
