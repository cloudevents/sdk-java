package io.cloudevents.http.vertx.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.message.impl.BaseGenericBinaryMessageImpl;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;

import java.util.Objects;
import java.util.function.BiConsumer;

public class BinaryVertxMessageImpl extends BaseGenericBinaryMessageImpl<String, String> {

    private final MultiMap headers;

    public BinaryVertxMessageImpl(SpecVersion version, MultiMap headers, Buffer body) {
        super(version, (body != null) ? body.getBytes() : null);

        Objects.requireNonNull(headers);
        this.headers = headers;
    }

    @Override
    protected boolean isContentTypeHeader(String key) {
        return key.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE.toString());
    }

    @Override
    protected boolean isCloudEventsHeader(String key) {
        return key.length() > 3 && key.substring(0, CloudEventsHeaders.CE_PREFIX.length()).toLowerCase().startsWith(CloudEventsHeaders.CE_PREFIX);
    }

    @Override
    protected String toCloudEventsKey(String key) {
        return key.substring(CloudEventsHeaders.CE_PREFIX.length()).toLowerCase();
    }

    @Override
    protected void forEachHeader(BiConsumer<String, String> fn) {
        this.headers.forEach(e -> fn.accept(e.getKey(), e.getValue()));
    }

    @Override
    protected String toCloudEventsValue(String value) {
        return value;
    }
}
