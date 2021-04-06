package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class InExpressionTest {

    @Test
    void onlyNumericLiterals() {
        assertThat(Parser.parseDefault("123 IN (1, 2, 3, 12, 13, 23, 123)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("123 NOT IN (1, 2, 3, 12, 13, 23, 123)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void onlyStringLiterals() {
        assertThat(Parser.parseDefault("'abc' IN ('abc', \"bcd\")").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("'aaa' IN ('abc', \"bcd\")").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void onlyBooleanLiterals() {
        assertThat(Parser.parseDefault("TRUE IN (TRUE, FALSE)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("TRUE IN (FALSE)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void mixedLiteralsAndIdentifiersOfSameType() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withExtension("myext", Data.SOURCE)
            .build();

        assertThat(Parser.parseDefault("source IN (myext, 'abc')").evaluate(event))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("source IN (id, '" + event.getSource().toString() + "')").evaluate(event))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("source IN (id, 'xyz')").evaluate(event))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void typeCoercion() {
        assertThat(Parser.parseDefault("'true' IN (TRUE, 'false')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("'true' IN ('TRUE', 'false')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("TRUE IN ('true', 'false')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        // Note: STRING(TRUE) = 'true' lowercase!
        assertThat(Parser.parseDefault("'TRUE' IN (TRUE, 'false')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("1 IN ('1', '2')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("'1' IN (1, 2)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

}
