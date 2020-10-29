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
