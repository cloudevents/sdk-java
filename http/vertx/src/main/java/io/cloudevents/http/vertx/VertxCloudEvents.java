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
