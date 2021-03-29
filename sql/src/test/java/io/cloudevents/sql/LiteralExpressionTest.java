package io.cloudevents.sql;

import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class LiteralExpressionTest {

    @Test
    void trueBooleanLiteral() {
        assertThat(Parser.parseDefault("TRUE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void falseBooleanLiteral() {
        assertThat(Parser.parseDefault("FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void integerLiteral() {
        assertThat(Parser.parseDefault("1").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(1);
        assertThat(Parser.parseDefault("0").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(0);
    }

    @Test
    void stringLiteral() {
        assertThat(Parser.parseDefault("'abc'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abc");
        assertThat(Parser.parseDefault("\"abc\"").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abc");
    }

    @Test
    void escapedStringLiteral() {
        assertThat(Parser.parseDefault("'a\"b\\'c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("a\"b'c");
        assertThat(Parser.parseDefault("\"a'b\\\"c\"").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("a'b\"c");
    }

}
