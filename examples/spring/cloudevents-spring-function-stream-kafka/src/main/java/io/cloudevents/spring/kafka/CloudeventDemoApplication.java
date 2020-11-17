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

import java.net.URI;
import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.cloudevents.spring.core.CloudEventAttributeUtils;
import io.cloudevents.spring.core.CloudEventAttributesProvider;

/**
 * Sample application that demonstrates how user functions can be triggered
 * by cloud event.
 * Given that this particular sample based on spring-cloud-function-web
 * support the function itself is a valid REST endpoint where function name
 * signifies URL path (e.g., http://localhost:8080/pojoToPojo).
 *
 * Simply start the application and post cloud event to individual
 * function - (see README for instructions)
 *
 * You can also run CloudeventDemoApplicationTests.
 *
 * @author Oleg Zhurakousky
 *
 */
@SpringBootApplication
public class CloudeventDemoApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CloudeventDemoApplication.class, args);
    }

    /*
     * This strategy will be called internally by Spring to set Cloud Event output attributes
     */
//  @Bean
    public CloudEventAttributesProvider cloudEventAttributesProvider() {
        return attributes -> CloudEventAttributeUtils.toMutable(attributes)
                    .setSource(URI.create("https://interface21.com/"))
                    .setType("com.interface21");
    }

    @Bean
    public Function<SpringReleaseEvent, SpringReleaseEvent> pojoToPojo() {
        return event -> {
            System.out.println("RECEIVED Spring Release Event: " + event);
            return event.setReleaseDateAsString("01-10-2006").setVersion("2.0");
        };
    }
}
