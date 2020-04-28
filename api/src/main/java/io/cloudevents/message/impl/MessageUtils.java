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
