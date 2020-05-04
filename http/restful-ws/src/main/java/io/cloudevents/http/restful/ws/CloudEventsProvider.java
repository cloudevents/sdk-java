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
import io.cloudevents.CloudEventVisitor;
import io.cloudevents.format.EventFormat;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.http.restful.ws.impl.RestfulWSClientMessageVisitor;
import io.cloudevents.http.restful.ws.impl.RestfulWSMessageFactory;
import io.cloudevents.http.restful.ws.impl.RestfulWSMessageVisitor;
import io.cloudevents.http.restful.ws.impl.Utils;
import io.cloudevents.message.MessageVisitor;
import io.cloudevents.message.StructuredMessage;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

@Provider
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class CloudEventsProvider implements MessageBodyReader<CloudEvent>, MessageBodyWriter<CloudEvent>, ClientRequestFilter {

    public static MediaType CLOUDEVENT_TYPE = MediaType.valueOf("application/cloudevents");

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return CloudEvent.class.isAssignableFrom(type);
    }

    @Override
    public CloudEvent readFrom(Class<CloudEvent> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        return RestfulWSMessageFactory.create(mediaType, httpHeaders, bufferBodyInput(entityStream)).toEvent();
    }

    private byte[] bufferBodyInput(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return CloudEvent.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(CloudEvent event, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Optional<String> structuredEncodingFormat = Arrays
            .stream(annotations)
            .filter(a -> a.annotationType().equals(StructuredEncoding.class))
            .map(a -> ((StructuredEncoding) a).value())
            .findFirst();

        if (structuredEncodingFormat.isPresent()) {
            writeStructured(
                event,
                structuredEncodingFormat.get(),
                new RestfulWSMessageVisitor(httpHeaders, entityStream)
            );
        } else {
            writeBinary(
                event,
                new RestfulWSMessageVisitor(httpHeaders, entityStream)
            );
        }
    }

    private <V extends MessageVisitor<V, Void> & CloudEventVisitor<Void>> void writeBinary(CloudEvent input, V visitor) {
        input.visit(visitor);
    }

    private <V extends MessageVisitor<V, Void> & CloudEventVisitor<Void>> void writeStructured(CloudEvent input, EventFormat format, V visitor) {
        StructuredMessage
            .fromEvent(format, input)
            .visit(visitor);
    }

    private <V extends MessageVisitor<V, Void> & CloudEventVisitor<Void>> void writeStructured(CloudEvent input, String formatString, V visitor) {
        EventFormat format = EventFormatProvider.getInstance().resolveFormat(formatString);

        if (format == null) {
            throw new IllegalArgumentException("Cannot resolve format " + formatString);
        }

        writeStructured(input, format, visitor);
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (Utils.isCloudEventEntity(requestContext.getEntity())) {
            EventFormat format = EventFormatProvider.getInstance().resolveFormat(requestContext.getMediaType().toString());

            if (format != null) {
                writeStructured(
                    (CloudEvent) requestContext.getEntity(),
                    format,
                    new RestfulWSClientMessageVisitor(requestContext)
                );
            } else {
                writeBinary(
                    (CloudEvent) requestContext.getEntity(),
                    new RestfulWSClientMessageVisitor(requestContext)
                );
            }
        }
    }
}
