package io.cloudevents.message;

import io.cloudevents.CloudEvent;

@FunctionalInterface
public interface BinaryMessage {

    /**
     * @param visitorFactory
     * @throws MessageVisitException
     * @throws IllegalStateException If the message is not a valid binary message
     */
    <V extends BinaryMessageVisitor<R>, R> R visit(BinaryMessageVisitorFactory<V, R> visitorFactory) throws MessageVisitException, IllegalStateException;

    default CloudEvent toEvent() throws MessageVisitException, IllegalStateException {
        return this.visit(specVersion -> {
            switch (specVersion) {
                case V1: return CloudEvent.buildV1();
                case V03: return CloudEvent.buildV03();
            }
            return null; // This can never happen
        });
    };

}
