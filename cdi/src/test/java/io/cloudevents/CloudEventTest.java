/**
 * Copyright 2018 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents;

import io.cloudevents.beans.Receiver;
import io.cloudevents.beans.Router;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;

@RunWith(Arquillian.class)
public class CloudEventTest extends AbstractTestBase {


    @Inject
    private Router router;

    @Deployment
    public static JavaArchive createDeployment() {
        return AbstractTestBase.createFrameworkDeployment()
                .addPackage(Router.class.getPackage());
    }

    @Test
    public void testDispatch(final Receiver receiver) throws Exception {
        router.routeMe();
        Mockito.verify(receiver, Mockito.times(1)).ack();
    }
}
