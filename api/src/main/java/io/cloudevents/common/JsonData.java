package io.cloudevents.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.Data;
import io.cloudevents.DataConversionException;
import io.cloudevents.json.Json;

import java.util.Objects;

public class JsonData implements Data {

    private JsonNode data;

    public JsonData(JsonNode data) {
        this.data = data;
    }

    @Override
    public byte[] asBinary() {
        try {
            return Json.MAPPER.writeValueAsBytes(this.data);
        } catch (JsonProcessingException e) {
            throw new DataConversionException("JsonNode", "[]byte", e);
        }
    }

    @Override
    public String asString() {
        try {
            return Json.MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new DataConversionException("JsonNode", "String", e);
        }
    }

    @Override
    public JsonNode asJson() {
        return this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonData jsonData = (JsonData) o;
        return Objects.equals(data, jsonData.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "JsonData{" +
            "data=" + data +
            '}';
    }
}
