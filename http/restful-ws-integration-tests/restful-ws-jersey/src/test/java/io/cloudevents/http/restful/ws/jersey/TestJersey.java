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

package io.cloudevents.http.restful.ws.jersey;

import com.github.hanleyt.JerseyExtension;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.http.restful.ws.BaseTest;
import io.cloudevents.http.restful.ws.CloudEventsProvider;
import io.cloudevents.http.restful.ws.TestResource;
import io.cloudevents.mock.CSVFormat;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

public class TestJersey extends BaseTest {

    private WebTarget target;

    @Override
    protected WebTarget getWebTarget() {
        return target;
    }

    @BeforeAll
    public static void beforeAll() {
        EventFormatProvider.getInstance().registerFormat(CSVFormat.INSTANCE);
    }

    @BeforeEach
    void beforeEach(WebTarget target) {
        this.target = target;
    }

    @RegisterExtension
    JerseyExtension jerseyExtension = new JerseyExtension(this::configureJersey, this::configureJerseyClient);

    private Application configureJersey() {
        return new ResourceConfig(TestResource.class)
            .register(CloudEventsProvider.class);
    }

    private ClientConfig configureJerseyClient(ExtensionContext extensionContext, ClientConfig clientConfig) {
        return clientConfig
            .register(CloudEventsProvider.class);
    }

}
