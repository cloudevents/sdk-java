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

package io.cloudevents.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.CloudEventData;

import java.util.Objects;

/**
 * This class is a wrapper for Jackson {@link JsonNode} implementing the {@link CloudEventData}
 */
public class JsonCloudEventData implements CloudEventData {

    private final JsonNode node;

    public JsonCloudEventData(JsonNode node) {
        Objects.requireNonNull(node);
        this.node = node;
    }

    @Override
    public byte[] toBytes() {
        return node.toString().getBytes();
    }

    public JsonNode getNode() {
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonCloudEventData that = (JsonCloudEventData) o;
        return Objects.equals(getNode(), that.getNode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNode());
    }

    @Override
    public String toString() {
        return "JsonCloudEventData{" +
            "node=" + node +
            '}';
    }
}
