/*
 * Copyright 2018-Present The CloudEvents Authors
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

package io.cloudevents.message.impl;

import io.cloudevents.message.Encoding;
import io.cloudevents.message.Message;
import io.cloudevents.message.MessageVisitException;
import io.cloudevents.message.StructuredMessageVisitor;

public abstract class BaseBinaryMessage implements Message {

    @Override
    public Encoding getEncoding() {
        return Encoding.BINARY;
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
        throw Encoding.UNKNOWN_ENCODING_EXCEPTION;
    }
}
