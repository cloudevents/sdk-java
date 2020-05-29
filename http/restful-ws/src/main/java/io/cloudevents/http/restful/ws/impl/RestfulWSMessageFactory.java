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

package io.cloudevents.http.restful.ws.impl;

import io.cloudevents.core.message.Message;
import io.cloudevents.core.message.impl.GenericStructuredMessage;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.core.message.impl.UnknownEncodingMessage;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public final class RestfulWSMessageFactory {

    private RestfulWSMessageFactory() {
    }

    public static Message create(MediaType mediaType, MultivaluedMap<String, String> headers, byte[] payload) throws IllegalArgumentException {
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> headers.getFirst(HttpHeaders.CONTENT_TYPE),
            format -> new GenericStructuredMessage(format, payload),
            () -> headers.getFirst(CloudEventsHeaders.SPEC_VERSION),
            sv -> new BinaryRestfulWSMessageImpl(sv, headers, payload),
            UnknownEncodingMessage::new
        );
    }

}
