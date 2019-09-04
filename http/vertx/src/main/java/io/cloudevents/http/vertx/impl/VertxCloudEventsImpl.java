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
package io.cloudevents.http.vertx.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.format.Wire;
import io.cloudevents.http.vertx.VertxCloudEvents;
import io.cloudevents.json.Json;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventImpl;
import io.cloudevents.v02.http.HTTPBinaryMarshaller;
import io.cloudevents.v02.http.HTTPBinaryUnmarshaller;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import static io.vertx.core.http.HttpHeaders.createOptimized;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

public final class VertxCloudEventsImpl implements VertxCloudEvents {

    private final static CharSequence BINARY_TYPE = HttpHeaders.createOptimized("application/json");
    private final static CharSequence STRUCTURED_TYPE = HttpHeaders.createOptimized("application/cloudevents+json");

    @Override
    public void readFromRequest(HttpServerRequest request, Handler<AsyncResult<CloudEvent<AttributesImpl, String>>> resultHandler) {
        this.readFromRequest(request, null, resultHandler);

    }

    @Override
    public void readFromRequest(HttpServerRequest request, Class[] extensions, Handler<AsyncResult<CloudEvent<AttributesImpl, String>>> resultHandler) {

        final MultiMap headers = request.headers();

        // binary mode
        if (headers.get(HttpHeaders.CONTENT_TYPE).equalsIgnoreCase(BINARY_TYPE.toString())) {
        	request.bodyHandler((Buffer buff) -> {
        		CloudEvent<AttributesImpl, String> event = 
        		HTTPBinaryUnmarshaller
	        		.unmarshaller(Json.umarshaller(String.class)::unmarshal)
	        		.withHeaders(() -> {
	        			final Map<String, Object> result = new HashMap<>();
	        			
	        			headers.iterator()
	        				.forEachRemaining(header -> {
	        					result.put(header.getKey(), header.getValue());
	        				});
	        			
	        			return Collections.unmodifiableMap(result);
	        		})
	        		.withPayload(() -> {
	        			return buff.toString();
	        		})
	        		.unmarshal();
        		
        		resultHandler.handle(Future.succeededFuture(event));
            });
        	
        } else if (headers.get(HttpHeaders.CONTENT_TYPE).equalsIgnoreCase(STRUCTURED_TYPE.toString())) {
            // structured read of the body
            request.bodyHandler((Buffer buff) -> {

                if (buff.length()>0) {
                	resultHandler.handle(Future.succeededFuture(Json.decodeValue(buff.toString(),
                			new TypeReference<CloudEventImpl<String>>() {})));
                } else {
                    throw new IllegalArgumentException("no cloudevent body");
                }
            });
        } else {
            throw new IllegalArgumentException("no cloudevent type identified");
        }
    }

    @Override
    public void writeToHttpClientRequest(CloudEvent<AttributesImpl, String> cloudEvent, HttpClientRequest request) {
        writeToHttpClientRequest(cloudEvent, Boolean.TRUE, request);
    }

    @Override
    public void writeToHttpClientRequest(CloudEvent<AttributesImpl, String> cloudEvent, boolean binary, HttpClientRequest request) {

        if (binary) {
        	Wire<String, String, Object> wire =
        	HTTPBinaryMarshaller.<String, String>
        		marshaller(Json.marshaller())
	        		.withEvent(() -> cloudEvent)
	        		.marshal();
        	
            // setting the right content-length:
        	request.putHeader(HttpHeaders.CONTENT_LENGTH, createOptimized("0"));
        	wire.getPayload().ifPresent((payload) -> {
        		request.putHeader(HttpHeaders.CONTENT_LENGTH, 
        			createOptimized(String.valueOf(payload.length())));
        	});            
            
            // read required headers
        	wire.getHeaders().entrySet()
            	.stream()
            	.forEach(header -> {
            		request.putHeader(createOptimized(header.getKey()), 
            			createOptimized(header.getValue().toString()));
            	});

        	wire.getPayload().ifPresent((payload) -> {
            	request.write(payload);
            });
        } else {
            // read required headers
            request.putHeader(HttpHeaders.CONTENT_TYPE, STRUCTURED_TYPE);
            final String json = Json.encode(cloudEvent);
            request.putHeader(HttpHeaders.CONTENT_LENGTH, 
            		createOptimized(String.valueOf(json.length())));
            // this the body
            request.write(json);
        }
    }
}
