package io.cloudevents.message;

import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormat;

public interface Message extends StructuredMessage, BinaryMessage {

    Encoding getEncoding();

    default <BV extends BinaryMessageVisitor<R>, R> R visit(MessageVisitor<BV, R> visitor) throws MessageVisitException, IllegalStateException {
        switch (getEncoding()) {
            case BINARY: return this.visit((BinaryMessageVisitorFactory<BV, R>) visitor);
            case STRUCTURED: return this.visit((StructuredMessageVisitor<R>)visitor);
            default: throw Encoding.UNKNOWN_ENCODING_EXCEPTION;
        }
    }

    default CloudEvent toEvent() throws MessageVisitException, IllegalStateException {
        switch (getEncoding()) {
            case BINARY:
                return this.visit(specVersion -> {
                    switch (specVersion) {
                        case V1:
                            return CloudEvent.buildV1();
                        case V03:
                            return CloudEvent.buildV03();
                    }
                    return null; // This can never happen
                });
            case STRUCTURED:
                return this.visit(EventFormat::deserialize);
            default:
                throw Encoding.UNKNOWN_ENCODING_EXCEPTION;
        }
    };

}
