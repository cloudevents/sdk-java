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

package io.cloudevents.core.message.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.rw.CloudEventRWException;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.cloudevents.rw.CloudEventRWException.newUnknownEncodingException;

/**
 * Collection of utilities useful to implement {@link MessageReader} and {@link io.cloudevents.core.message.MessageWriter} related code.
 */
public class MessageUtils {

    /**
     * Common flow to parse an incoming message that could be structured or binary.
     *
     * @param contentTypeHeaderReader  supplier that returns the content type header, if any
     * @param structuredMessageFactory factory to create the structured {@link MessageReader} from the provided {@link EventFormat}
     * @param specVersionHeaderReader  supplier that returns the spec version header, if any
     * @param binaryMessageFactory     factory to create the binary {@link MessageReader} from the provided {@link SpecVersion}
     * @return the instantiated {@link MessageReader}
     * @throws CloudEventRWException if something goes wrong while resolving the {@link SpecVersion} or if the message has unknown encoding
     */
    public static MessageReader parseStructuredOrBinaryMessage(
        Supplier<String> contentTypeHeaderReader,
        Function<EventFormat, MessageReader> structuredMessageFactory,
        Supplier<String> specVersionHeaderReader,
        Function<SpecVersion, MessageReader> binaryMessageFactory
    ) throws CloudEventRWException {
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

        throw newUnknownEncodingException();
    }

    /**
     * Generate a map with cloudevents attributes as keys and header keys as values
     *
     * @param headerNameMapping mapper to generate the header name
     * @param <V>               Header key type
     * @return the generated mapping
     */
    public static <V> Map<String, V> generateAttributesToHeadersMapping(Function<String, V> headerNameMapping) {
        return Stream.concat(
            Stream.concat(SpecVersion.V1.getMandatoryAttributes().stream(), SpecVersion.V1.getOptionalAttributes().stream()),
            Stream.concat(SpecVersion.V03.getMandatoryAttributes().stream(), SpecVersion.V03.getOptionalAttributes().stream())
        )
            .distinct()
            .collect(Collectors.toMap(Function.identity(), headerNameMapping));
    }

    /**
     * @param expected the expected encoding
     * @param actual   the actual encoding
     * @return a new instance of {@link IllegalStateException}.
     */
    public static IllegalStateException generateWrongEncoding(Encoding expected, Encoding actual) {
        return new IllegalStateException("Cannot visit message as " + expected + " because the actual encoding is " + actual);
    }

}
