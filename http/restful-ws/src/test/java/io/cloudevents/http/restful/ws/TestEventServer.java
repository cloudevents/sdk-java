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

package io.cloudevents.http.restful.ws;

import io.cloudevents.CloudEvent;
import io.cloudevents.test.Data;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class TestEventServer {

    @GET
    public CloudEvent getEventWithoutBody() {
        return Data.V1_MIN;
    }

    @GET
    public CloudEvent getEvent() {
        return Data.V1_WITH_JSON_DATA_WITH_EXT_STRING;
    }

    @POST
    public Response postEventWithoutBody(CloudEvent inputEvent) {
        if (inputEvent.equals(Data.V1_MIN)) {
            return Response.ok().build();
        }
        return Response.serverError().build();
    }

    @POST
    public Response postEvent(CloudEvent inputEvent) {
        if (inputEvent.equals(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING)) {
            return Response.ok().build();
        }
        return Response.serverError().build();
    }
}
