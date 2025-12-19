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

package io.cloudevents.http.restful.ws.spring;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.test.Data;
import io.cloudevents.spring.mvc.CloudEventHttpMessageConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = {"io.cloudevents.http.restful.ws"})
public class TestApplication extends SpringBootServletInitializer {
    @Configuration
    public static class WebConfig implements WebMvcConfigurer {
        @Override
        public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
            builder.addCustomConverter(new CloudEventHttpMessageConverter());
        }
    }

    @RestController
    public static class TestResource {
        @GetMapping("/getMinEvent")
        public CloudEvent getMinEvent() {
            return Data.V1_MIN;
        }

        @GetMapping(path = "/getStructuredEventCsv", produces = "application/cloudevents+csv")
        public CloudEvent getStructuredEventCsv() {
            return Data.V1_MIN;
        }

        @GetMapping(path = "/getStructuredEventJson", produces = "application/cloudevents+json")
        public CloudEvent getStructuredEventJson() {
            return Data.V1_MIN;
        }

        @GetMapping(value = "/getEvent")
        public CloudEvent getEvent() {
            return Data.V1_WITH_JSON_DATA_WITH_EXT_STRING;
        }

        @PostMapping("/postEventWithoutBody")
        public void postEventWithoutBody(@RequestBody CloudEvent inputEvent) {
            if (!inputEvent.equals(Data.V1_MIN)) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
