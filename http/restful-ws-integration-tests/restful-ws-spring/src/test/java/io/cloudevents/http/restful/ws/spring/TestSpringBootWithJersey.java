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

import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.http.restful.ws.BaseTest;
import io.cloudevents.http.restful.ws.CloudEventsProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.URI;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSpringBootWithJersey extends BaseTest {

    @LocalServerPort
    private int port;

    private WebTarget target;

    @Override
    protected WebTarget getWebTarget() {
        return target;
    }

    @BeforeEach
    void beforeEach() {
        target = ClientBuilder.newClient().register(CloudEventsProvider.class).target(URI.create("http://localhost:" + this.port + "/"));
    }

    @BeforeAll
    public static void beforeAll() {
        EventFormatProvider.getInstance().registerFormat(CSVFormat.INSTANCE);
    }

    @Test
    public void contextLoads() {
    }

}
