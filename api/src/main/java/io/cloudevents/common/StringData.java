package io.cloudevents.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.Data;
import io.cloudevents.DataConversionException;
import io.cloudevents.json.Json;

import java.io.IOException;
import java.util.Objects;

public class StringData implements Data {

    private String data;

    public StringData(String data) {
        this.data = data;
    }

    @Override
    public byte[] asBinary() {
        return this.data.getBytes();
    }

    @Override
    public String asString() {
        return this.data;
    }

    @Override
    public JsonNode asJson() {
        try {
            return Json.MAPPER.readTree(data);
        } catch (IOException e) {
            throw new DataConversionException("String", "JsonNode", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringData that = (StringData) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "StringData{" +
            "data='" + data + '\'' +
            '}';
    }
}
