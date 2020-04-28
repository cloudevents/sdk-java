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

import io.cloudevents.SpecVersion;
import io.cloudevents.format.EventFormat;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.message.Message;

import java.util.function.Function;
import java.util.function.Supplier;

public class MessageUtils {

    public static Message parseStructuredOrBinaryMessage(
        Supplier<String> contentTypeHeaderReader,
        Function<EventFormat, Message> structuredMessageFactory,
        Supplier<String> specVersionHeaderReader,
        Function<SpecVersion, Message> binaryMessageFactory,
        Supplier<Message> unknownMessageFactory
    ) {
        // Let's try structured mode
        String ct = contentTypeHeaderReader.get();
        if (ct != null) {
            EventFormat format = EventFormatProvider.getInstance().resolveFormat(ct);
            if (format != null) {
                return structuredMessageFactory.apply(format);
            }

        }

        // Let's try binary mode
        String specVersionUnparsed = specVersionHeaderReader.get();
        if (specVersionUnparsed != null) {
            return binaryMessageFactory.apply(SpecVersion.parse(specVersionUnparsed));
        }

        return unknownMessageFactory.get();
    }

}
