package io.cloudevents.message.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.message.BinaryMessageVisitor;
import io.cloudevents.message.BinaryMessageVisitorFactory;
import io.cloudevents.message.MessageVisitException;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * This class implements a BinaryMessage, providing commong logic to most protocol bindings
 * which supports both Binary and Structured mode.
 * Content-type is handled separately using a key not prefixed with CloudEvents header prefix.
 *
 * @param <HV> Header value type
 */
public abstract class BaseGenericBinaryMessageImpl<HV> extends BaseBinaryMessage {

    private final SpecVersion version;
    private final byte[] body;

    protected BaseGenericBinaryMessageImpl(SpecVersion version, byte[] body) {
        Objects.requireNonNull(version);
        this.version = version;
        this.body = body;
    }

    @Override
    public <T extends BinaryMessageVisitor<V>, V> V visit(BinaryMessageVisitorFactory<T, V> visitorFactory) throws MessageVisitException, IllegalStateException {
        BinaryMessageVisitor<V> visitor = visitorFactory.createBinaryMessageVisitor(this.version);

        // Grab from headers the attributes and extensions
        this.forEachHeader((k, v) -> {
            try {
                if (k.substring(0, getHeaderKeyPrefix().length()).equalsIgnoreCase(getHeaderKeyPrefix())) {
                    String name = k.substring(getHeaderKeyPrefix().length()).toLowerCase();
                    if (name.equals("specversion")) {
                        return;
                    }
                    if (this.version.getAllAttributes().contains(name)) {
                        visitor.setAttribute(name, headerValueToString(v));
                    } else {
                        visitor.setExtension(name, headerValueToString(v));
                    }
                }
                if (k.equalsIgnoreCase(getContentTypeHeaderKey())) {
                    visitor.setAttribute("datacontenttype", headerValueToString(v));
                }
            } catch (StringIndexOutOfBoundsException ex) {
                // String is smaller than 3 characters and it's not equal for sure to CE_PREFIX
            }
        });

        // Set the payload
        if (this.body != null && this.body.length != 0) {
            visitor.setBody(this.body);
        }

        return visitor.end();
    }

    protected abstract String getContentTypeHeaderKey();

    protected abstract String getHeaderKeyPrefix();

    protected abstract void forEachHeader(BiConsumer<String, HV> fn);

    protected abstract String headerValueToString(HV value);

}
