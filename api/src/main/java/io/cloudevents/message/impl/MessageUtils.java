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

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageUtils {

    /**
     * Common flow to parse an incoming message that could be structured or binary
     *
     * @param contentTypeHeaderReader
     * @param structuredMessageFactory
     * @param specVersionHeaderReader
     * @param binaryMessageFactory
     * @param unknownMessageFactory
     * @return
     */
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

    /**
     * Generate a map with cloudevents attributes as keys and header keys as values
     *
     * @param headerNameMapping mapper to generate the header name
     * @param <V>               Header key type
     * @return
     */
    public static <V> Map<String, V> generateAttributesToHeadersMapping(Function<String, V> headerNameMapping) {
        return Stream.concat(
            Stream.concat(SpecVersion.V1.getMandatoryAttributes().stream(), SpecVersion.V1.getOptionalAttributes().stream()),
            Stream.concat(SpecVersion.V03.getMandatoryAttributes().stream(), SpecVersion.V03.getOptionalAttributes().stream())
        )
            .distinct()
            .collect(Collectors.toMap(Function.identity(), headerNameMapping));
    }

}
