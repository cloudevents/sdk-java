/*
 * Copyright 2020-Present The CloudEvents Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.spring.kafka;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import io.cloudevents.CloudEvent;
import io.cloudevents.spring.messaging.CloudEventContextUtils;
import io.cloudevents.spring.messaging.CloudEventMessageConverter;
import io.cloudevents.spring.messaging.CloudEventsHeaders;

public class CloudEventRecordMessageConverter implements RecordMessageConverter {

    private RecordMessageConverter delegate = new MessagingMessageConverter();

    private CloudEventMessageConverter converter = new CloudEventMessageConverter();

    private static String KAFKA_PREFIX = "ce_";

    @Override
    public Message<?> toMessage(ConsumerRecord<?, ?> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer,
            Type payloadType) {
        Message<?> message = canonicalize(delegate.toMessage(record, acknowledgment, consumer, payloadType));
        if (payloadType.equals(CloudEvent.class)) {
            CloudEvent event = (CloudEvent) converter.fromMessage(message, CloudEvent.class);
            message = MessageBuilder.withPayload(event).copyHeaders(message.getHeaders()).build();
        }
        return message;
    }

    @Override
    public ProducerRecord<?, ?> fromMessage(Message<?> message, String defaultTopic) {
        if (message.getPayload() instanceof CloudEvent) {
            CloudEvent payload = (CloudEvent) message.getPayload();
            Map<String, Object> map = CloudEventContextUtils.toMap(payload);
            message = MessageBuilder.withPayload(payload.getData().toBytes()).copyHeaders(message.getHeaders())
                    .copyHeaders(map).build();
        }
        return delegate.fromMessage(message, defaultTopic);
    }

    private static Message<?> canonicalize(Message<?> message) {
        Map<String, Object> headers = new HashMap<>(message.getHeaders());
        for (Map.Entry<String, Object> entry : message.getHeaders().entrySet()) {
            if (entry.getKey().startsWith(KAFKA_PREFIX)) {
                headers.remove(entry.getKey());
                headers.put(CloudEventsHeaders.CE_PREFIX + entry.getKey().substring(KAFKA_PREFIX.length()),
                        stringValue(entry.getValue()));
            } else {
                headers.put(entry.getKey(), entry.getValue());
            }
        }
        return MessageBuilder.createMessage(message.getPayload(), new MessageHeaders(headers));
    }

    private static String stringValue(Object value) {
        if (value instanceof byte[]) {
            return new String((byte[]) value);
        }
        return value.toString();
    }

}
