package io.cloudevents.nats.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;
import io.nats.client.impl.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.BiConsumer;

public class NatsMessageReaderImpl extends BaseGenericBinaryMessageReaderImpl<String, byte[]> {

    private final Headers headers;

    public NatsMessageReaderImpl(SpecVersion version, Headers headers, byte[] body) {
        super(version, body != null && body.length > 0 ? BytesCloudEventData.wrap(body) : null);

        Objects.requireNonNull(headers);
        this.headers = headers;
    }

    @Override
    protected boolean isContentTypeHeader(String key) {
        return key.equals(NatsHeaders.CONTENT_TYPE);
    }

    @Override
    protected boolean isCloudEventsHeader(String key) {
        return key.startsWith(NatsHeaders.CE_PREFIX);
    }

    @Override
    protected String toCloudEventsKey(String key) {
        return NatsHeaders.headerUnmapper(key);
    }

    @Override
    protected void forEachHeader(BiConsumer<String, byte[]> fn) {
        this.headers.forEach((header, value) -> {
            if (!value.isEmpty()) {
                fn.accept(header, NatsHeaders.unescapeHeaderValue(value.get(0)).getBytes(StandardCharsets.UTF_8));
            }
        });
    }

    @Override
    protected String toCloudEventsValue(byte[] value) {
        return new String(value, StandardCharsets.UTF_8);
    }
}
