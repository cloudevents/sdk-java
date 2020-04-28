/*
 * Copyright 2020 The CloudEvents Authors
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

package io.cloudevents.format.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.message.BinaryMessage;
import io.cloudevents.message.BinaryMessageVisitor;
import io.cloudevents.message.BinaryMessageVisitorFactory;
import io.cloudevents.message.MessageVisitException;

import java.io.IOException;

public class CloudEventDeserializer extends StdDeserializer<CloudEvent> {

    protected CloudEventDeserializer() {
        super(CloudEvent.class);
    }

    private static class JsonMessage implements BinaryMessage {

        private final JsonParser p;
        private final ObjectNode node;

        public JsonMessage(JsonParser p, ObjectNode node) {
            this.p = p;
            this.node = node;
        }

        @Override
        public <T extends BinaryMessageVisitor<V>, V> V visit(BinaryMessageVisitorFactory<T, V> visitorFactory) throws MessageVisitException, IllegalStateException {
            try {
                SpecVersion specVersion = SpecVersion.parse(getStringNode(this.node, this.p, "specversion"));
                BinaryMessageVisitor<V> visitor = visitorFactory.createBinaryMessageVisitor(specVersion);

                // Read mandatory attributes
                for (String attr : specVersion.getMandatoryAttributes()) {
                    if (!"specversion".equals(attr)) {
                        visitor.setAttribute(attr, getStringNode(this.node, this.p, attr));
                    }
                }

                // Parse datacontenttype if any
                String contentType = getOptionalStringNode(this.node, this.p, "datacontenttype");
                if (contentType != null) {
                    visitor.setAttribute("datacontenttype", contentType);
                }

                // Read optional attributes
                for (String attr : specVersion.getOptionalAttributes()) {
                    if (!"datacontentencoding".equals(attr)) { // Skip datacontentencoding, we need it later
                        String val = getOptionalStringNode(this.node, this.p, attr);
                        if (val != null) {
                            visitor.setAttribute(attr, val);
                        }
                    }
                }

                // Now let's handle the data
                switch (specVersion) {
                    case V03:
                        boolean isBase64 = "base64".equals(getOptionalStringNode(this.node, this.p, "datacontentencoding"));
                        if (node.has("data")) {
                            if (isBase64) {
                                visitor.setBody(node.remove("data").binaryValue());
                            } else {
                                if (JsonFormat.dataIsJsonContentType(contentType)) {
                                    // This solution is quite bad, but i see no alternatives now.
                                    // Hopefully in future we can improve it
                                    visitor.setBody(node.remove("data").toString().getBytes());
                                } else {
                                    JsonNode data = node.remove("data");
                                    assertNodeType(data, JsonNodeType.STRING, "data", "Because content type is not a json, only a string is accepted as data");
                                    visitor.setBody(data.asText().getBytes());
                                }
                            }
                        }
                    case V1:
                        if (node.has("data_base64") && node.has("data")) {
                            throw MismatchedInputException.from(p, CloudEvent.class, "CloudEvent cannot have both 'data' and 'data_base64' fields");
                        }
                        if (node.has("data_base64")) {
                            visitor.setBody(node.remove("data_base64").binaryValue());
                        } else if (node.has("data")) {
                            if (JsonFormat.dataIsJsonContentType(contentType)) {
                                // This solution is quite bad, but i see no alternatives now.
                                // Hopefully in future we can improve it
                                visitor.setBody(node.remove("data").toString().getBytes());
                            } else {
                                JsonNode data = node.remove("data");
                                assertNodeType(data, JsonNodeType.STRING, "data", "Because content type is not a json, only a string is accepted as data");
                                visitor.setBody(data.asText().getBytes());
                            }
                        }
                }

                // Now let's process the extensions
                node.fields().forEachRemaining(entry -> {
                    String extensionName = entry.getKey();
                    JsonNode extensionValue = entry.getValue();

                    switch (extensionValue.getNodeType()) {
                        case BOOLEAN:
                            visitor.setExtension(extensionName, extensionValue.booleanValue());
                            break;
                        case NUMBER:
                            visitor.setExtension(extensionName, extensionValue.numberValue());
                            break;
                        case STRING:
                            visitor.setExtension(extensionName, extensionValue.textValue());
                            break;
                        default:
                            visitor.setExtension(extensionName, extensionValue.toString());
                    }

                });

                return visitor.end();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(MismatchedInputException.from(this.p, CloudEvent.class, e.getMessage()));
            }
        }

        private String getStringNode(ObjectNode objNode, JsonParser p, String attributeName) throws JsonProcessingException {
            String val = getOptionalStringNode(objNode, p, attributeName);
            if (val == null) {
                throw MismatchedInputException.from(p, CloudEvent.class, "Missing mandatory " + attributeName + " attribute");
            }
            return val;
        }

        private String getOptionalStringNode(ObjectNode objNode, JsonParser p, String attributeName) throws JsonProcessingException {
            JsonNode unparsedSpecVersion = objNode.remove(attributeName);
            if (unparsedSpecVersion == null) {
                return null;
            }
            assertNodeType(unparsedSpecVersion, JsonNodeType.STRING, attributeName, null);
            return unparsedSpecVersion.asText();
        }

        private void assertNodeType(JsonNode node, JsonNodeType type, String attributeName, String desc) throws JsonProcessingException {
            if (node.getNodeType() != type) {
                throw MismatchedInputException.from(
                    p,
                    CloudEvent.class,
                    "Wrong type " + node.getNodeType() + " for attribute " + attributeName + ", expecting " + type + (desc != null ? ". " + desc : "")
                );
            }
        }
    }

    @Override
    public CloudEvent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // In future we could eventually find a better solution avoiding this buffering step, but now this is the best option
        // Other sdk does the same in order to support all versions
        ObjectNode node = ctxt.readValue(p, ObjectNode.class);

        try {
            return new JsonMessage(p, node).toEvent();
        } catch (RuntimeException e) {
            // Yeah this is bad but it's needed to support checked exceptions...
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw MismatchedInputException.wrapWithPath(e, null);
        }
    }
}
