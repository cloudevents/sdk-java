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

package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.kafka.impl.KafkaSerializerMessageWriterImpl;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Kafka {@link Serializer} for {@link CloudEvent}.
 * <p>
 * To configure the encoding to use when serializing the event, you can use the {@link CloudEventSerializer#ENCODING_CONFIG} configuration key,
 * which accepts both a {@link String} or a variant of the enum {@link Encoding}. If you configure the Encoding as {@link Encoding#STRUCTURED},
 * you MUST configure the {@link EventFormat} to use with {@link CloudEventSerializer#EVENT_FORMAT_CONFIG}, specifying a {@link String}
 * corresponding to the content type of the event format or specifying an instance of {@link EventFormat}.
 */
public class CloudEventSerializer implements Serializer<CloudEvent> {

    /**
     * The configuration key for the {@link Encoding} to use when serializing the event.
     */
    public final static String ENCODING_CONFIG = "cloudevents.serializer.encoding";

    /**
     * The configuration key for the {@link EventFormat} to use when serializing the event in structured mode.
     */
    public final static String EVENT_FORMAT_CONFIG = "cloudevents.serializer.event_format";

    private Encoding encoding = Encoding.BINARY;
    private EventFormat format = null;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (isKey) {
            throw new IllegalArgumentException("Cannot use CloudEventSerializer as key serializer");
        }
        Object encodingConfig = configs.get(ENCODING_CONFIG);
        if (encodingConfig instanceof String string) {
            this.encoding = Encoding.valueOf(string);
        } else if (encodingConfig instanceof Encoding encoding1) {
            this.encoding = encoding1;
        } else if (encodingConfig != null) {
            throw new IllegalArgumentException(ENCODING_CONFIG + " can be of type String or " + Encoding.class.getCanonicalName());
        }

        if (this.encoding == Encoding.STRUCTURED) {
            Object eventFormatConfig = configs.get(EVENT_FORMAT_CONFIG);
            if (eventFormatConfig instanceof String string) {
                this.format = EventFormatProvider.getInstance().resolveFormat(string);
                if (this.format == null) {
                    throw new IllegalArgumentException(EVENT_FORMAT_CONFIG + " cannot be resolved with " + eventFormatConfig);
                }
            } else if (eventFormatConfig instanceof EventFormat eventFormat) {
                this.format = eventFormat;
            } else {
                throw new IllegalArgumentException(EVENT_FORMAT_CONFIG + " cannot be null and can be of type String or " + EventFormat.class.getCanonicalName());
            }
        }
    }

    @Override
    public byte[] serialize(String topic, CloudEvent data) {
        throw new UnsupportedOperationException("CloudEventSerializer supports only the signature serialize(String, Headers, CloudEvent)");
    }

    @Override
    public byte[] serialize(String topic, Headers headers, CloudEvent data) {
        if (encoding == Encoding.STRUCTURED) {
            return new KafkaSerializerMessageWriterImpl(headers)
                .writeStructured(data, this.format);
        } else {
            return new KafkaSerializerMessageWriterImpl(headers)
                .writeBinary(data);
        }
    }
}
