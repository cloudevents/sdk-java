/*
 * Copyright 2020-Present The CloudEvents Authors
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

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.cloudevents.spring.core.CloudEventAttributeUtils;
import io.cloudevents.spring.core.MutableCloudEventAttributes;
import org.junit.jupiter.api.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.concurrent.ListenableFuture;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Zhurakousky
 *
 */
public class CloudeventDemoApplicationTests {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testAsBinary() throws Exception {
		try (ConfigurableApplicationContext context = SpringApplication.run(CloudeventDemoApplication.class)) {
			KafkaTemplate kafka = context.getBean(KafkaTemplate.class);

			String binaryEvent = "{\"releaseDate\":\"24-03-2004\", \"releaseName\":\"Spring Framework\", \"version\":\"1.0\"}";

			Message<byte[]> message = MessageBuilder.withPayload(binaryEvent.getBytes(StandardCharsets.UTF_8))
					.setHeader(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + MutableCloudEventAttributes.ID,
							UUID.randomUUID().toString())
					.setHeader(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + MutableCloudEventAttributes.SOURCE,
							"https://spring.io/")
					.setHeader(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + MutableCloudEventAttributes.SPECVERSION,
							"1.0")
					.setHeader(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + MutableCloudEventAttributes.TYPE,
							"org.springframework")
					.setHeader(KafkaHeaders.TOPIC, "pojoToPojo-in-0").build();

			ListenableFuture<SendResult<String, String>> future = kafka.send(message);

			assertThat(future.get(1000, TimeUnit.MILLISECONDS).getRecordMetadata()).isNotNull();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testAsStructured() throws Exception {
		try (ConfigurableApplicationContext context = SpringApplication.run(CloudeventDemoApplication.class)) {
			KafkaTemplate kafka = context.getBean(KafkaTemplate.class);

			String structuredEvent = "{\n" +
			        "    \"specversion\" : \"1.0\",\n" +
			        "    \"type\" : \"org.springframework\",\n" +
			        "    \"source\" : \"https://spring.io/\",\n" +
			        "    \"id\" : \"A234-1234-1234\",\n" +
			        "    \"datacontenttype\" : \"application/json\",\n" +
			        "    \"data\" : {\n" +
			        "        \"version\" : \"1.0\",\n" +
			        "        \"releaseName\" : \"Spring Framework\",\n" +
			        "        \"releaseDate\" : \"24-03-2004\"\n" +
			        "    }\n" +
			        "}";

			System.out.println(structuredEvent);
			Message<byte[]> message = MessageBuilder.withPayload(structuredEvent.getBytes(StandardCharsets.UTF_8))
					.setHeader(MessageHeaders.CONTENT_TYPE,
							CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json")
					.setHeader(KafkaHeaders.TOPIC, "pojoToPojo-in-0").build();

			ListenableFuture<SendResult<String, String>> future = kafka.send(message);

			assertThat(future.get(1000, TimeUnit.MILLISECONDS).getRecordMetadata()).isNotNull();
		}
	}

}
