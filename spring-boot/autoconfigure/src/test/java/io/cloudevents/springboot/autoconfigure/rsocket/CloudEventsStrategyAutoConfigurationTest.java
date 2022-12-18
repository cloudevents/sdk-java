package io.cloudevents.springboot.autoconfigure.rsocket;

import io.cloudevents.spring.codec.CloudEventDecoder;
import io.cloudevents.spring.codec.CloudEventEncoder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.messaging.rsocket.RSocketStrategies;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CloudEventsStrategyAutoConfiguration}
 *
 * @author Eddú Meléndez
 */
class CloudEventsStrategyAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(CloudEventsStrategyAutoConfiguration.class, RSocketStrategiesAutoConfiguration.class));

    @Test
    void autoconfigurationDisabled() {
        this.contextRunner.withPropertyValues("cloudevents.spring.rsocket.enabled:false")
            .run(context -> assertThat(context).doesNotHaveBean("cloudEventsRSocketStrategiesCustomizer"));
    }

    @Test
    void shouldUseCloudEventsRSocketStrategiesCustomizer() {
        this.contextRunner.run(context -> {
            assertThat(context).getBeans(RSocketStrategies.class).hasSize(1);
            RSocketStrategies strategies = context.getBean(RSocketStrategies.class);
            assertThat(strategies.decoders()).hasAtLeastOneElementOfType(CloudEventDecoder.class);
            assertThat(strategies.encoders()).hasAtLeastOneElementOfType(CloudEventEncoder.class);
        });
    }

}
