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

package io.cloudevents.http.vertx;

import io.cloudevents.core.message.Message;
import io.cloudevents.core.message.MessageVisitor;
import io.cloudevents.http.vertx.impl.VertxHttpServerResponseMessageVisitorImpl;
import io.cloudevents.visitor.CloudEventVisitor;
import io.vertx.core.http.HttpServerResponse;

/**
 * Visitor for {@link Message} that can write both structured and binary messages to a {@link HttpServerResponse}.
 * When the visit ends, the request is ended with {@link HttpServerResponse#end(io.vertx.core.buffer.Buffer)}
 */
public interface VertxHttpServerResponseMessageVisitor extends MessageVisitor<VertxHttpServerResponseMessageVisitor, HttpServerResponse>, CloudEventVisitor<HttpServerResponse> {

    static VertxHttpServerResponseMessageVisitor create(HttpServerResponse res) {
        return new VertxHttpServerResponseMessageVisitorImpl(res);
    }

}
