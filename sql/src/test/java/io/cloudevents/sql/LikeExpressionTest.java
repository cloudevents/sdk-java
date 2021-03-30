package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class LikeExpressionTest {

    @Test
    void noMatchingOperator() {
        assertThat(Parser.parseDefault("'abc' LIKE 'abc'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'abc' NOT LIKE 'abc'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void percentageOperator() {
        assertThat(Parser.parseDefault("'abc' LIKE 'a%b%c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'azbc' LIKE 'a%b%c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'abzc' LIKE 'a%b%c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'azzzbzzzc' LIKE 'a%b%c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'a%b%c' LIKE 'a%b%c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'ac' LIKE 'abc'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("'' LIKE 'abc'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void underscoreOperator() {
        assertThat(Parser.parseDefault("'abc' LIKE 'a_b_c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("'a_b_c' LIKE 'a_b_c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'abzc' LIKE 'a_b_c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("'azbc' LIKE 'a_b_c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("'azbzc' LIKE 'a_b_c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void escapedWildcards() {
        assertThat(Parser.parseDefault("'a_b_c' LIKE 'a\\_b\\_c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'a_b_c' NOT LIKE 'a\\_b\\_c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("'azbzc' LIKE 'a\\_b\\_c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("'abc' LIKE 'a\\_b\\_c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("'abc' LIKE 'a\\%b\\%c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("'a%b%c' LIKE 'a\\%b\\%c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("'azbzc' LIKE 'a\\%b\\%c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(Parser.parseDefault("'abc' LIKE 'a\\%b\\%c'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void fromEvent() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withExtension("myext", "abc123123%456_dzf")
            .build();

        assertThat(Parser.parseDefault("myext LIKE 'abc%123\\%456\\_d_f'").evaluate(event))
            .isNotFailed()
            .asBoolean()
            .isTrue();
        assertThat(Parser.parseDefault("myext NOT LIKE 'abc%123\\%456\\_d_f'").evaluate(event))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

}
