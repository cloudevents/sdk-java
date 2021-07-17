package io.cloudevents.springboot.autoconfigure.http.codec;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CloudEventsCodecAutoConfiguration}
 *
 * @author Eddú Meléndez
 */
class CloudEventsCodecAutoConfigurationTest {

    private final ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(CloudEventsCodecAutoConfiguration.class, CodecsAutoConfiguration.class));

    @Test
    void autoconfigurationDisabled() {
        this.contextRunner.withPropertyValues("cloudevents.spring.webflux.enabled:false")
            .run(context -> assertThat(context).doesNotHaveBean("cloudEventsCodecCustomizer"));
    }

    @Test
    void cloudEventsCodecCustomizerIsAutoConfigured() {
        this.contextRunner.run(context -> assertThat(context).hasBean("cloudEventsCodecCustomizer"));
    }

}
