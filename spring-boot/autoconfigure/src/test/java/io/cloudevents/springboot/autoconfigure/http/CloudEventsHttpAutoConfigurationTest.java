package io.cloudevents.springboot.autoconfigure.http;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CloudEventsHttpAutoConfiguration}
 *
 * @author Eddú Meléndez
 */
class CloudEventsHttpAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(CloudEventsHttpAutoConfiguration.class, WebMvcAutoConfiguration.class));

    @Test
    void autoconfigurationDisabled() {
        this.contextRunner.withPropertyValues("cloudevents.spring.web.enabled:false")
            .run(context -> assertThat(context).doesNotHaveBean("cloudEventMessageConverter"));
    }

    @Test
    void cloudEventsMessageConverterIsAutoConfigured() {
        this.contextRunner.run(context -> assertThat(context).hasBean("cloudEventMessageConverter"));
    }

}
