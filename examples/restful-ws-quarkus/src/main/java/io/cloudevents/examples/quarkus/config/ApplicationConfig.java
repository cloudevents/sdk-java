package io.cloudevents.examples.quarkus.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import tools.jackson.databind.json.JsonMapper;

@ApplicationScoped
public class ApplicationConfig {
    @Produces
    @ApplicationScoped
    public JsonMapper jsonMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }
}
