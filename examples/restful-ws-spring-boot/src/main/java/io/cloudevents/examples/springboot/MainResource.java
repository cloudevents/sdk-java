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

package io.cloudevents.examples.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.cloudevents.core.CloudEventUtils.mapData;

@Path("/")
public class MainResource {

    public static final String HAPPY_BIRTHDAY_EVENT_TYPE = "happybirthday.myapplication";

    @Autowired
    ObjectMapper objectMapper;

    @POST
    @Path("happy_birthday")
    public Response handleHappyBirthdayEvent(CloudEvent inputEvent) {
        if (!inputEvent.getType().equals(HAPPY_BIRTHDAY_EVENT_TYPE)) {
            return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity("Event type should be \"" + HAPPY_BIRTHDAY_EVENT_TYPE + "\" but is \"" + inputEvent.getType() + "\"")
                .build();
        }

        PojoCloudEventData<User> cloudEventData = mapData(inputEvent, PojoCloudEventDataMapper.from(objectMapper, User.class));

        if (cloudEventData == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity("Event should contain the user")
                .build();
        }

        User user = cloudEventData.getValue();
        user.setAge(user.getAge() + 1);

        CloudEvent outputEvent = CloudEventBuilder.from(inputEvent)
            .withData(PojoCloudEventData.wrap(user, objectMapper::writeValueAsBytes))
            .build();

        return Response.ok(outputEvent).build();
    }
}
