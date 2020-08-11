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

package io.cloudevents.core.provider;

import io.cloudevents.core.codec.DataSerializationException;
import io.cloudevents.core.codec.EventDataCodec;
import io.cloudevents.core.codec.impl.TextEventDataCodec;
import io.cloudevents.lang.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

@ParametersAreNonnullByDefault
public final class EventDataCodecProvider implements EventDataCodec {

    private static class SingletonContainer {
        private final static EventDataCodecProvider INSTANCE = new EventDataCodecProvider();
    }

    /**
     * @return instance of {@link EventDataCodecProvider}
     */
    public static EventDataCodecProvider getInstance() {
        return EventDataCodecProvider.SingletonContainer.INSTANCE;
    }

    private final List<EventDataCodec> codecs;

    private EventDataCodecProvider() {
        this.codecs = new ArrayList<>();

        StreamSupport.stream(
            ServiceLoader.load(EventDataCodec.class).spliterator(),
            false
        ).forEach(this::registerCodec);
        registerCodec(new TextEventDataCodec());
    }

    /**
     * Register a new {@link EventDataCodec} programmatically.
     *
     * @param codec the new format to register
     */
    public void registerCodec(EventDataCodec codec) {
        codecs.add(codec);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <C extends EventDataCodec> C getCodec(Class<C> codecClass) {
        return (C) codecs.stream()
            .filter(c -> c.getClass().equals(codecClass))
            .findFirst()
            .orElse(null);
    }

    @Override
    public byte[] serialize(@Nullable String dataContentType, Object data) throws DataSerializationException {
        if (data instanceof byte[]) {
            return (byte[]) data;
        }
        String ct = dataContentType != null ? dataContentType : "application/json";
        EventDataCodec codec = codecs
            .stream()
            .filter(c -> c.canSerialize(ct))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find any registered codec that can serialize to " + ct));
        return codec.serialize(ct, data);
    }

    @Override
    public boolean canSerialize(@Nullable String dataContentType) {
        return codecs
            .stream()
            .anyMatch(c -> c.canSerialize(dataContentType));
    }

}
