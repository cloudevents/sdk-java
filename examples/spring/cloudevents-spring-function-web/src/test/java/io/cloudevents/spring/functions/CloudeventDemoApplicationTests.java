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

package io.cloudevents.spring.functions;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.UUID;

import io.cloudevents.spring.core.CloudEventAttributeUtils;
import io.cloudevents.spring.core.MutableCloudEventAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.function.json.JsonMapper;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SocketUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Zhurakousky
 *
 */
public class CloudeventDemoApplicationTests {

	private TestRestTemplate testRestTemplate = new TestRestTemplate();

	@BeforeEach
	public void init() throws Exception {
		System.setProperty("server.port", String.valueOf(SocketUtils.findAvailableTcpPort()));
	}

	/*
	 * This test demonstrates consumption of Cloud Event via HTTP POST - binary-mode
	 * message. According to specification -
	 * https://github.com/cloudevents/spec/blob/v1.0/spec.md - A "binary-mode message" is
	 * one where the event data is stored in the message body, and event attributes are
	 * stored as part of message meta-data.
	 *
	 * The above means that it fits perfectly with Spring Message model and as such there
	 * is absolutely nothing that needs to be done at the framework or user level to
	 * consume it. It just works!
	 */
	@Test
	public void testAsBinary() throws Exception {
		try (ConfigurableApplicationContext context = SpringApplication.run(CloudeventDemoApplication.class)) {
			HttpHeaders headers = this.buildHeaders(MediaType.APPLICATION_JSON);
			// will work with either content type
			// HttpHeaders headers =
			// this.buildHeaders(MediaType.valueOf(CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE
			// + "+json"));

			String payload = "{\"releaseDate\":\"24-03-2004\", \"releaseName\":\"Spring Framework\", \"version\":\"1.0\"}";

			RequestEntity<String> re = new RequestEntity<>(payload, headers, HttpMethod.POST,
					URI.create("http://localhost:" + System.getProperty("server.port") + "/pojoToPojo"));
			ResponseEntity<String> response = testRestTemplate.exchange(re, String.class);

			SpringReleaseEvent springEvent = context.getBean(JsonMapper.class).fromJson(response.getBody(),
					SpringReleaseEvent.class);
            assertThat(springEvent.getVersion()).isEqualTo("2.0");
            assertThat(springEvent.getReleaseDate()).isEqualTo(new SimpleDateFormat("dd-MM-yyyy").parse("01-10-2006"));

            /*
             * Uncomment this and comment the next two assertion if
             * CloudEventAttributesProvider is enabled in CloudeventDemoApplication
             */
//            assertThat(response.getHeaders().getFirst("ce-" + MutableCloudEventAttributes.SOURCE))
//                    .isEqualTo("https://interface21.com/");
//            assertThat(response.getHeaders().getFirst("ce-" + MutableCloudEventAttributes.TYPE))
//                    .isEqualTo("com.interface21");

            assertThat(response.getHeaders().getFirst("ce-" + MutableCloudEventAttributes.SOURCE))
                    .isEqualTo("http://spring.io/application-application");
            assertThat(response.getHeaders()
                    .getFirst("ce-" + MutableCloudEventAttributes.TYPE)) .isEqualTo(SpringReleaseEvent.class.getName());
		}
	}


	@Test
	public void testAsStrtuctured() throws Exception {
		String payload = "{\n" +
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

		try (ConfigurableApplicationContext context = SpringApplication.run(CloudeventDemoApplication.class)) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.valueOf(CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json"));

			RequestEntity<String> re = new RequestEntity<>(payload, headers, HttpMethod.POST,
					URI.create("http://localhost:" + System.getProperty("server.port") + "/pojoToPojo"));
			ResponseEntity<String> response = testRestTemplate.exchange(re, String.class);

			SpringReleaseEvent springEvent = context.getBean(JsonMapper.class).fromJson(response.getBody(),
					SpringReleaseEvent.class);
            assertThat(springEvent.getVersion()).isEqualTo("2.0");
            assertThat(springEvent.getReleaseDate()).isEqualTo(new SimpleDateFormat("dd-MM-yyyy").parse("01-10-2006"));

            /*
             * Uncomment this and comment the next two assertion if
             * CloudEventAttributesProvider is enabled in CloudeventDemoApplication
             */
//            assertThat(response.getHeaders().getFirst("ce-" + MutableCloudEventAttributes.SOURCE))
//                    .isEqualTo("https://interface21.com/");
//            assertThat(response.getHeaders().getFirst("ce-" + MutableCloudEventAttributes.TYPE))
//                    .isEqualTo("com.interface21");

            assertThat(response.getHeaders().getFirst("ce-" + MutableCloudEventAttributes.SOURCE))
                    .isEqualTo("http://spring.io/application-application");
            assertThat(response.getHeaders().getFirst("ce-" + MutableCloudEventAttributes.TYPE))
                    .isEqualTo(SpringReleaseEvent.class.getName());
		}
	}

	private HttpHeaders buildHeaders(MediaType contentType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(contentType);
		headers.set(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + MutableCloudEventAttributes.ID,
				UUID.randomUUID().toString());
		headers.set(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + MutableCloudEventAttributes.SOURCE,
				"https://spring.io/");
		headers.set(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + MutableCloudEventAttributes.SPECVERSION, "1.0");
		headers.set(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + MutableCloudEventAttributes.TYPE,
				"org.springframework");
		return headers;
	}

}
