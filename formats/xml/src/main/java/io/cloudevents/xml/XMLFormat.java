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

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.rw.CloudEventDataMapper;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;

/**
 * An implemmentation of {@link EventFormat} for the XML Format.
 * This format is resolvable with {@link io.cloudevents.core.provider.EventFormatProvider} using the content type {@link #XML_CONTENT_TYPE}.
 * <p>
 * This {@link EventFormat} only works for {@link io.cloudevents.SpecVersion#V1}, as that was the first version the XML format was defined for.
 */
public class XMLFormat implements EventFormat {

    /**
     * The content type for transports sending cloudevents in XML format.
     */
    public static final String XML_CONTENT_TYPE = "application/cloudevents+xml";

    @Override
    public byte[] serialize(CloudEvent event) throws EventSerializationException {

        // Convert the CE into an XML Document
        Document d = XMLSerializer.toDocument(event);

        try {
            // Write out the XML Document
            return XMLUtils.documentToBytes(d);
        } catch (TransformerException e) {
            throw new EventSerializationException(e);
        }
    }

    @Override
    public CloudEvent deserialize(byte[] bytes, CloudEventDataMapper<? extends CloudEventData> mapper)
        throws EventDeserializationException {

        final Document doc = XMLUtils.parseIntoDocument(bytes);
        return new XMLDeserializer(doc).read(CloudEventBuilder::fromSpecVersion);

    }

    @Override
    public String serializedContentType() {
        return XML_CONTENT_TYPE;
    }

}
