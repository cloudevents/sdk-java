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

package io.cloudevents.core.mock;

import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.Message;
import io.cloudevents.core.message.StructuredMessageVisitor;
import io.cloudevents.core.message.impl.BaseStructuredMessage;
import io.cloudevents.visitor.CloudEventVisitException;

public class MockStructuredMessage extends BaseStructuredMessage implements Message, StructuredMessageVisitor<MockStructuredMessage> {

    private EventFormat format;
    private byte[] payload;

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws CloudEventVisitException, IllegalStateException {
        if (this.format == null) {
            throw new IllegalStateException("MockStructuredMessage is empty");
        }

        return visitor.setEvent(this.format, this.payload);
    }

    @Override
    public MockStructuredMessage setEvent(EventFormat format, byte[] value) throws CloudEventVisitException {
        this.format = format;
        this.payload = value;

        return this;
    }
}
