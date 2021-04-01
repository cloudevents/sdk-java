package io.cloudevents.sql;

import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class BinaryComparisonExpressionTest {

    @Test
    void equals() {
        assertThat(Parser.parseDefault("TRUE = FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("FALSE = FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("1 = 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("2 = 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("'abc' = '123'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("'abc' = 'abc'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void notEquals() {
        assertThat(Parser.parseDefault("TRUE != FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("FALSE != FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("1 != 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("2 != 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("'abc' != '123'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'abc' != 'abc'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void notEqualsDiamond() {
        assertThat(Parser.parseDefault("TRUE <> FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("FALSE <> FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("1 <> 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("2 <> 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("'abc' <> '123'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'abc' <> 'abc'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void integerComparison() {
        assertThat(Parser.parseDefault("2 <= 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("3 <= 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("1 < 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("2 < 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("2 >= 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("2 >= 3").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("2 > 1").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("2 > 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void implicitCasting() {
        assertThat(Parser.parseDefault("true = 'TRUE'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("'TRUE' = true").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

}
