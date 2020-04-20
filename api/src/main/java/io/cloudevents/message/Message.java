package io.cloudevents.message;

import io.cloudevents.CloudEvent;

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
            case BINARY: return ((BinaryMessage)this).toEvent();
            case STRUCTURED: return ((StructuredMessage)this).toEvent();
            default: throw Encoding.UNKNOWN_ENCODING_EXCEPTION;
        }
    };

}
