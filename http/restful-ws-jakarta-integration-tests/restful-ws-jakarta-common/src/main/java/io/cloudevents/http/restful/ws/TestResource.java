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
import io.cloudevents.core.test.Data;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class TestResource {

    @GET
    @Path("getMinEvent")
    public CloudEvent getMinEvent() {
        return Data.V1_MIN;
    }

    @GET
    @Path("getStructuredEvent")
    @StructuredEncoding("application/cloudevents+csv")
    public CloudEvent getStructuredEvent() {
        return Data.V1_MIN;
    }

    @GET
    @Path("getEvent")
    public CloudEvent getEvent() {
        return Data.V1_WITH_JSON_DATA_WITH_EXT_STRING;
    }

    @POST
    @Path("postEventWithoutBody")
    public Response postEventWithoutBody(CloudEvent inputEvent) {
        if (inputEvent.equals(Data.V1_MIN)) {
            return Response.ok().build();
        }
        return Response.serverError().build();
    }

    @POST
    @Path("postEvent")
    public Response postEvent(CloudEvent inputEvent) {
        if (inputEvent.equals(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING)) {
            return Response.ok().build();
        }
        return Response.serverError().build();
    }
}
