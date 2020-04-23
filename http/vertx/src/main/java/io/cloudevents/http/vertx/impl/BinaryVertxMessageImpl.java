package io.cloudevents.http.vertx.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.http.vertx.VertxMessage;
import io.cloudevents.message.*;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BinaryVertxMessageImpl implements VertxMessage {

    public static final Map<String, CharSequence> OPTIMIZED_ATTRIBUTES_TO_HEADERS = Stream.concat(
        Stream.concat(SpecVersion.V1.getMandatoryAttributes().stream(), SpecVersion.V1.getOptionalAttributes().stream()),
        Stream.concat(SpecVersion.V03.getMandatoryAttributes().stream(), SpecVersion.V03.getOptionalAttributes().stream())
    )
        .distinct()
        .collect(Collectors.toMap(Function.identity(), v -> HttpHeaders.createOptimized("ce-" + v)));

    public static final Map<CharSequence, String> OPTIMIZED_HEADERS_TO_ATTRIBUTES = OPTIMIZED_ATTRIBUTES_TO_HEADERS
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public static final String CE_PREFIX = "ce-";

    public static final CharSequence CE_SPEC_VERSION_HEADER = HttpHeaders.createOptimized("ce-specversion");

    private final SpecVersion version;
    private final MultiMap headers;
    private final Buffer body;

    public BinaryVertxMessageImpl(SpecVersion version, MultiMap headers, Buffer body) {
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public Encoding getEncoding() {
        return Encoding.BINARY;
    }

    @Override
    public <T extends BinaryMessageVisitor<V>, V> V visit(BinaryMessageVisitorFactory<T, V> visitorFactory) throws MessageVisitException, IllegalStateException {
        BinaryMessageVisitor<V> visitor = visitorFactory.createBinaryMessageVisitor(this.version);

        // Grab from headers the attributes and extensions
        this.headers.forEach(e -> {
            if (e.getKey().startsWith(CE_PREFIX)) {
                String name = e.getKey().substring(3);
                if (name.equals("specversion")) {
                    return;
                }
                if (this.version.getAllAttributes().contains(name)) {
                    visitor.setAttribute(name, e.getValue());
                } else {
                    visitor.setExtension(name, e.getValue());
                }
            }
        });

        String ct = this.headers.get(HttpHeaders.CONTENT_TYPE);
        if (ct != null) {
            visitor.setAttribute("datacontenttype", ct);
        }

        // Set the payload
        if (this.body != null && this.body.length() != 0) {
            visitor.setBody(this.body.getBytes());
        }

        return visitor.end();
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
        throw Encoding.WRONG_ENCODING_EXCEPTION;
    }
}
