package io.cloudevents.kafka.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.message.impl.BaseGenericBinaryMessageImpl;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.BiConsumer;

public class KafkaBinaryMessageImpl extends BaseGenericBinaryMessageImpl<byte[]> {

    private final Headers headers;

    public KafkaBinaryMessageImpl(SpecVersion version, Headers headers, byte[] payload) {
        super(version, payload);

        Objects.requireNonNull(headers);
        this.headers = headers;
    }

    @Override
    protected String getContentTypeHeaderKey() {
        return KafkaHeaders.CONTENT_TYPE;
    }

    @Override
    protected String getHeaderKeyPrefix() {
        return KafkaHeaders.CE_PREFIX;
    }

    @Override
    protected void forEachHeader(BiConsumer<String, byte[]> fn) {
        this.headers.forEach(h -> fn.accept(h.key(), h.value()));
    }

    @Override
    protected String headerValueToString(byte[] value) {
        return new String(value, StandardCharsets.UTF_8);
    }
}
