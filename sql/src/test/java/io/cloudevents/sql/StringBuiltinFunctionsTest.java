package io.cloudevents.sql;

import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class StringBuiltinFunctionsTest {

    @Test
    void lengthFunction() {
        assertThat(Parser.parseDefault("LENGTH('abc')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(3);
        assertThat(Parser.parseDefault("LENGTH('')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(0);
        assertThat(Parser.parseDefault("LENGTH('2')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(1);
        assertThat(Parser.parseDefault("LENGTH(TRUE)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(4);
    }

    @Test
    void concatFunction() {
        assertThat(Parser.parseDefault("CONCAT('a', 'b', 'c')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abc");
        assertThat(Parser.parseDefault("CONCAT()").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEmpty();
        assertThat(Parser.parseDefault("CONCAT('a')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("a");
    }

    @Test
    void concatWSFunction() {
        assertThat(Parser.parseDefault("CONCAT_WS(',', 'a', 'b', 'c')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("a,b,c");
        assertThat(Parser.parseDefault("CONCAT_WS(',')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEmpty();
        assertThat(Parser.parseDefault("CONCAT_WS(',', 'a')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("a");
    }

    @Test
    void lowerFunction() {
        assertThat(Parser.parseDefault("LOWER('ABC')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abc");
        assertThat(Parser.parseDefault("LOWER('AbC')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abc");
        assertThat(Parser.parseDefault("LOWER('abc')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abc");
    }

    @Test
    void upperFunction() {
        assertThat(Parser.parseDefault("UPPER('abc')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("ABC");
        assertThat(Parser.parseDefault("UPPER('AbC')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("ABC");
        assertThat(Parser.parseDefault("UPPER('ABC')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("ABC");
    }

    @Test
    void trimFunction() {
        assertThat(Parser.parseDefault("TRIM('   a b c   ')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("a b c");
        assertThat(Parser.parseDefault("TRIM('   a b c')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("a b c");
        assertThat(Parser.parseDefault("TRIM('a b c   ')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("a b c");
        assertThat(Parser.parseDefault("TRIM('a b c')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("a b c");
    }

    @Test
    void leftFunction() {
        assertThat(Parser.parseDefault("LEFT('abc', 2)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("ab");
        assertThat(Parser.parseDefault("LEFT('abc', 10)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abc");
        assertThat(Parser.parseDefault("LEFT('', 0)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("");
        assertThat(Parser.parseDefault("LEFT('abc', -2)").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.ErrorKind.FUNCTION_EXECUTION)
            .asString()
            .isEqualTo("abc");
    }

    @Test
    void rightFunction() {
        assertThat(Parser.parseDefault("RIGHT('abc', 2)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("bc");
        assertThat(Parser.parseDefault("RIGHT('abc', 10)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abc");
        assertThat(Parser.parseDefault("RIGHT('', 0)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("");
        assertThat(Parser.parseDefault("RIGHT('abc', -2)").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.ErrorKind.FUNCTION_EXECUTION)
            .asString()
            .isEqualTo("abc");
    }

    @Test
    void substringFunction() {
        assertThat(Parser.parseDefault("SUBSTRING('abcdef', 1, 5)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("bcde");
        assertThat(Parser.parseDefault("SUBSTRING('abcdef', 1, 2)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("b");
        assertThat(Parser.parseDefault("SUBSTRING('abcdef', 1, 1)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("");
        assertThat(Parser.parseDefault("SUBSTRING('abcdef', 1, 10)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("bcdef");
        assertThat(Parser.parseDefault("SUBSTRING('abcdef', -1, 5)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abcde");
        assertThat(Parser.parseDefault("SUBSTRING('abcdef', -1, 10)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abcdef");
        assertThat(Parser.parseDefault("SUBSTRING('abcdef', 0, 6)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("abcdef");
        assertThat(Parser.parseDefault("SUBSTRING('', 0, 0)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEmpty();
        assertThat(Parser.parseDefault("SUBSTRING('abc', 0, -1)").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.ErrorKind.FUNCTION_EXECUTION)
            .asString()
            .isEqualTo("abc");
        assertThat(Parser.parseDefault("SUBSTRING('abc', 3, 2)").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.ErrorKind.FUNCTION_EXECUTION)
            .asString()
            .isEqualTo("abc");
    }

}
