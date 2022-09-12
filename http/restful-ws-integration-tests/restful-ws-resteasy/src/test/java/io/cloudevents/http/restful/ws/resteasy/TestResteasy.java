
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

package io.cloudevents.http.restful.ws.resteasy;

import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.http.restful.ws.BaseTest;
import io.cloudevents.http.restful.ws.CloudEventsProvider;
import io.cloudevents.http.restful.ws.TestResource;
import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class TestResteasy extends BaseTest {

    private static VertxResteasyDeployment resteasyDeployment;
    private static WebTarget target;

    @Override
    protected WebTarget getWebTarget() {
        return target;
    }

    @BeforeAll
    public static void beforeClass() throws Exception {
        EventFormatProvider.getInstance().registerFormat(CSVFormat.INSTANCE);

        String base = TestPortProvider.generateBaseUrl();
        TestResteasy.resteasyDeployment = VertxContainer.start(base);
        TestResteasy.resteasyDeployment.getProviderFactory().register(CloudEventsProvider.class);
        TestResteasy.resteasyDeployment.getRegistry().addPerRequestResource(TestResource.class);

        TestResteasy.target = ClientBuilder.newClient().register(CloudEventsProvider.class).target(base);
    }

    @AfterAll
    public static void after() throws Exception {
        TestResteasy.resteasyDeployment.stop();
    }

}
