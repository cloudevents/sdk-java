package io.cloudevents.kafka.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.message.impl.BaseGenericBinaryMessageImpl;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.BiConsumer;

public class KafkaBinaryMessageImpl extends BaseGenericBinaryMessageImpl<String, byte[]> {

    private final Headers headers;

    public KafkaBinaryMessageImpl(SpecVersion version, Headers headers, byte[] payload) {
        super(version, payload);

        Objects.requireNonNull(headers);
        this.headers = headers;
    }

    @Override
    protected boolean isContentTypeHeader(String key) {
        return key.equals(KafkaHeaders.CONTENT_TYPE);
    }

    @Override
    protected boolean isCEPrefixed(String key) {
        return key.length() > 3 && key.substring(0, KafkaHeaders.CE_PREFIX.length()).startsWith(KafkaHeaders.CE_PREFIX);
    }

    @Override
    protected String stripKeyPrefixAndParse(String key) {
        return key.substring(KafkaHeaders.CE_PREFIX.length());
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
