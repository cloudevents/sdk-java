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

package io.cloudevents.spring.webmvc;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import io.cloudevents.spring.core.CloudEventAttributeUtils;
import io.cloudevents.spring.core.MutableCloudEventAttributes;
import io.cloudevents.spring.http.CloudEventHttpUtils;
/**
 * Sample application that demonstrates classing Spring MVC RestController
 *
 * Simply start the application and post cloud events (see README for instructions)
 *
 * You can also run CloudeventDemoApplicationTests.
 *
 * @author Dave Syer
 *
 */
@SpringBootApplication
@RestController
public class CloudeventDemoApplication {

	public static void main(String[] args) throws Exception {
	    SpringApplication.run(CloudeventDemoApplication.class, args);
	}

	@PostMapping("/")
    public ResponseEntity<Person> binary(@RequestBody Person person, @RequestHeader HttpHeaders headers) {
        MutableCloudEventAttributes attributes = CloudEventHttpUtils.fromHttp(headers)
                .setId(UUID.randomUUID().toString())
                .setSource(URI.create("https://spring.io/ce-webmvc/binary"))
                .setType(person.getClass().getName());
        HttpHeaders outgoing = CloudEventHttpUtils.toHttp(attributes);
        return ResponseEntity.ok().headers(outgoing).body(person);
    }

    @PostMapping(path = "/", consumes = "application/cloudevents+json")
    public ResponseEntity<Object> structured(@RequestBody Map<String, Object> body,
            @RequestHeader HttpHeaders headers) {
        MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(body)
                .setId(UUID.randomUUID().toString())
                .setSource(URI.create("https://spring.io/ce-webmvc/structured"))
                .setType(Person.class.getName());
        HttpHeaders outgoing = CloudEventHttpUtils.toHttp(attributes);
        return ResponseEntity.ok().headers(outgoing).body(body.get(CloudEventAttributeUtils.DATA));
    }

    public static class Person {
        private String name;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Person [name=" + this.name + "]";
        }
    }

}
