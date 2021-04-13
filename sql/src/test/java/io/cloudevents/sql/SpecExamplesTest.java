package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import io.cloudevents.sql.impl.functions.InfallibleOneArgumentFunction;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class SpecExamplesTest {

    @Test
    void caseInsensitiveHops() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withExtension("hop", "5")
            .withExtension("ttl", "10")
            .build();

        assertThat(
            Parser.parseDefault("int(hop) < int(ttl) and int(hop) < 1000")
                .evaluate(event)
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(
            Parser.parseDefault("INT(hop) < INT(ttl) AND INT(hop) < 1000")
                .evaluate(event)
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(
            Parser.parseDefault("hop < ttl")
                .evaluate(event)
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void equalsWithCasting() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withExtension("sequence", "5")
            .build();

        assertThat(
            Parser.parseDefault("sequence = 5")
                .evaluate(event)
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(
            Parser.parseDefault("sequence = 6")
                .evaluate(event)
        )
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void logicExpression() {
        assertThat(
            Parser.parseDefault("firstname = 'Francesco' OR subject = 'Francesco'")
                .evaluate(
                    CloudEventBuilder.v1(Data.V1_MIN)
                        .withSubject("Francesco")
                        .withExtension("firstname", "Doug")
                        .build()
                )
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(
            Parser.parseDefault("firstname = 'Francesco' OR subject = 'Francesco'")
                .evaluate(
                    CloudEventBuilder.v1(Data.V1_MIN)
                        .withSubject("Doug")
                        .withExtension("firstname", "Francesco")
                        .build()
                )
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(
            Parser.parseDefault("(firstname = 'Francesco' AND lastname = 'Guardiani') OR subject = 'Francesco Guardiani'")
                .evaluate(
                    CloudEventBuilder.v1(Data.V1_MIN)
                        .withSubject("Doug")
                        .withExtension("firstname", "Francesco")
                        .withExtension("lastname", "Guardiani")
                        .build()
                )
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(
            Parser.parseDefault("(firstname = 'Francesco' AND lastname = 'Guardiani') OR subject = 'Francesco Guardiani'")
                .evaluate(
                    CloudEventBuilder.v1(Data.V1_MIN)
                        .withSubject("Francesco Guardiani")
                        .withExtension("firstname", "Doug")
                        .withExtension("lastname", "Davis")
                        .build()
                )
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void exists() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withSubject(Data.SUBJECT)
            .build();

        assertThat(
            Parser.parseDefault("EXISTS subject")
                .evaluate(event)
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    //TODO don't remove this one!
    @Test
    void customFunction() {
        EvaluationRuntime runtime = EvaluationRuntime.builder()
            .addFunction(new InfallibleOneArgumentFunction<>(
                "MY_STRING_PREDICATE",
                String.class,
                s -> s.length() % 2 == 0
            ))
            .build();

        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withExtension("sequence", "12")
            .build();

        assertThat(
            Parser.parseDefault("MY_STRING_PREDICATE(sequence + 10)")
                .evaluate(runtime, event)
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();

    }

}
