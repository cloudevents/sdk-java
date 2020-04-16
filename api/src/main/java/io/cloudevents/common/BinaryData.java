package io.cloudevents.common;

import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.Data;
import io.cloudevents.DataConversionException;
import io.cloudevents.json.Json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BinaryData implements Data {

    private byte[] data;

    public BinaryData(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] asBinary() {
        return this.data;
    }

    @Override
    public String asString() {
        return new String(this.data, StandardCharsets.UTF_8);
    }

    @Override
    public JsonNode asJson() {
        try {
            return Json.MAPPER.readTree(data);
        } catch (IOException e) {
            throw new DataConversionException("[]byte", "JsonNode", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryData that = (BinaryData) o;
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return "BinaryData{" +
            "data=" + Arrays.toString(data) +
            '}';
    }
}
