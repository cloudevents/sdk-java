package io.cloudevents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.common.BinaryData;
import io.cloudevents.common.JsonData;
import io.cloudevents.common.StringData;
import io.cloudevents.json.Json;

/**
 * TODO
 *
 * @author slinkydeveloper
 */
public interface Data {

    byte[] asBinary() throws DataConversionException;

    String asString() throws DataConversionException;

    JsonNode asJson() throws DataConversionException;

    static Data newJson(JsonNode json) {
        return new JsonData(json);
    }

    static Data newJson(Object json) {
        try {
            return Json.MAPPER.valueToTree(json);
        } catch (IllegalArgumentException e) {
            throw new DataConversionException(json.getClass().toString(), "JsonNode", e);
        }
    }

    static Data newBinary(byte[] binary) {
        return new BinaryData(binary);
    }

    static Data newString(String string) {
        return new StringData(string);
    }

}
