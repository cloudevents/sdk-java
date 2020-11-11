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
import io.cloudevents.CloudEventData;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.rw.CloudEventDataMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Deserializer for {@link CloudEvent}.
 * <p>
 * To configure the {@link CloudEventDataMapper} to use, you can provide the instance through the configuration key
 * {@link CloudEventDeserializer#MAPPER_CONFIG}.
 */
public class CloudEventDeserializer implements Deserializer<CloudEvent> {

    public final static String MAPPER_CONFIG = "cloudevents.datamapper";

    private CloudEventDataMapper<? extends CloudEventData> mapper = null;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Object mapperConfig = configs.get(MAPPER_CONFIG);
        if (mapperConfig instanceof CloudEventDataMapper) {
            this.mapper = (CloudEventDataMapper<? extends CloudEventData>) mapperConfig;
        } else if (mapperConfig != null) {
            throw new IllegalArgumentException(MAPPER_CONFIG + " can be of type String or " + CloudEventDataMapper.class.getCanonicalName());
        }
    }

    @Override
    public CloudEvent deserialize(String topic, byte[] data) {
        throw new UnsupportedOperationException("CloudEventDeserializer supports only the signature deserialize(String, Headers, byte[])");
    }

    @Override
    public CloudEvent deserialize(String topic, Headers headers, byte[] data) {
        MessageReader reader = KafkaMessageFactory.createReader(headers, data);
        if (mapper == null) {
            return reader.toEvent();
        } else {
            return reader.toEvent(mapper);
        }
    }
}
