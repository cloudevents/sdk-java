/*
 * Copyright 2020 The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.message;

import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormat;

public interface Message extends StructuredMessage, BinaryMessage {

    Encoding getEncoding();

    default <BV extends BinaryMessageVisitor<R>, R> R visit(MessageVisitor<BV, R> visitor) throws MessageVisitException, IllegalStateException {
        switch (getEncoding()) {
            case BINARY:
                return this.visit((BinaryMessageVisitorFactory<BV, R>) visitor);
            case STRUCTURED:
                return this.visit((StructuredMessageVisitor<R>) visitor);
            default:
                throw Encoding.UNKNOWN_ENCODING_EXCEPTION;
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
    }

    ;

}
