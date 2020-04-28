package io.cloudevents;

import io.cloudevents.message.*;
import io.cloudevents.message.impl.BaseGenericBinaryMessageImpl;
import io.cloudevents.message.impl.GenericStructuredMessage;
import io.cloudevents.message.impl.MessageUtils;
import io.cloudevents.message.impl.UnknownEncodingMessage;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java8.En;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiConsumer;

@ScenarioScoped
public class HttpSteps implements En {

    private RawHttpRequest request;

    @Inject
    public HttpSteps(CloudEventsSteps cloudEventsSteps) {
        Given("HTTP Protocol Binding is supported", () -> {
        });

        Given("an HTTP request", (String rawRequest) -> {
            request = new RawHttp().parseRequest(rawRequest);
        });

        When("parsed as HTTP request", () -> {
            byte[] body = request.getBody()
                .map(it -> {
                    try {
                        return it.asRawBytes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(null);

            Message message = MessageUtils.parseStructuredOrBinaryMessage(
                () -> request.getHeaders().getFirst("content-type").orElse(null),
                eventFormat -> new GenericStructuredMessage(eventFormat, body),
                () -> request.getHeaders().getFirst("ce-specversion").orElse(null),
                specVersion -> new BaseGenericBinaryMessageImpl<String, String>(specVersion, body) {

                    @Override
                    protected boolean isContentTypeHeader(String key) {
                        return "content-type".equalsIgnoreCase(key);
                    }

                    @Override
                    protected boolean isCloudEventsHeader(String key) {
                        return key.toLowerCase().startsWith("ce-");
                    }

                    @Override
                    protected String toCloudEventsKey(String key) {
                        return key.substring("ce-".length());
                    }

                    @Override
                    protected void forEachHeader(BiConsumer<String, String> fn) {
                        request.getHeaders().forEach(fn);
                    }

                    @Override
                    protected String toCloudEventsValue(String value) {
                        return value;
                    }
                },
                UnknownEncodingMessage::new
            );

            cloudEventsSteps.cloudEvent = Optional.of(message.toEvent());
        });
    }
}
