/*
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
package io.cloudevents.http.vertx;

import io.cloudevents.CloudEvent;
import io.cloudevents.http.vertx.impl.VertxCloudEventsImpl;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerRequest;

@VertxGen
public interface VertxCloudEvents {

    static VertxCloudEvents create() {
        return new VertxCloudEventsImpl();
    }

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T> void readFromRequest(HttpServerRequest request, Handler<AsyncResult<CloudEvent<T>>> resultHandler);

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T> void writeToHttpClientRequest(CloudEvent<T> ce, HttpClientRequest request);
}
