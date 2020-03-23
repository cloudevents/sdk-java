package io.cloudevents;

import com.fasterxml.jackson.databind.JsonNode;

public interface Data {

    byte[] asBinary();

    JsonNode asJson();

    static Data newJson(JsonNode json) {
        //TODO
        return null;
    }

    static Data newJson(Object json) {
        //TODO
        return null;
    }

    static Data newBinary(byte[] binary) {
        //TODO
        return null;
    }

}
