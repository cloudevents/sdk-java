package io.cloudevents.sql;

import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class NotExpressionTest {

    @Test
    void booleanInput() {
        assertThat(Parser.parseDefault("NOT TRUE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("NOT FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void stringInput() {
        assertThat(Parser.parseDefault("NOT 'TRUE'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("NOT 'FALSE'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void invalidCast() {
        assertThat(Parser.parseDefault("NOT 10").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.Kind.INVALID_CAST)
            .asBoolean()
            .isTrue();
    }

}
