package io.cloudevents.sql.impl.parser;

import io.cloudevents.sql.Expression;
import io.cloudevents.sql.Parser;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.expressions.BaseBinaryExpression;
import io.cloudevents.sql.impl.expressions.ExistsExpression;
import io.cloudevents.sql.impl.expressions.ValueExpression;
import io.cloudevents.sql.impl.runtime.ExpressionImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstantFoldingTest {

    @Test
    void withBinaryExpression() {
        Expression expression = Parser.getDefault().parse("1 + 2");
        assertThat(expression)
            .isInstanceOf(ExpressionImpl.class);

        ExpressionInternal internal = ((ExpressionImpl) expression).getExpressionInternal();
        assertThat(internal)
            .isInstanceOf(ValueExpression.class)
            .extracting(v -> ((ValueExpression) v).getValue())
            .isEqualTo(3);
    }

    @Test
    void withUnaryExpression() {
        Expression expression = Parser.getDefault().parse("-1");
        assertThat(expression)
            .isInstanceOf(ExpressionImpl.class);

        ExpressionInternal internal = ((ExpressionImpl) expression).getExpressionInternal();
        assertThat(internal)
            .isInstanceOf(ValueExpression.class)
            .extracting(v -> ((ValueExpression) v).getValue())
            .isEqualTo(-1);
    }

    @Test
    void withBinaryAndUnaryExpression() {
        Expression expression = Parser.getDefault().parse("id + -2");
        assertThat(expression)
            .isInstanceOf(ExpressionImpl.class);

        ExpressionInternal internal = ((ExpressionImpl) expression).getExpressionInternal();
        assertThat(internal)
            .isInstanceOf(BaseBinaryExpression.class)
            .extracting(v -> ((BaseBinaryExpression) v).getRightOperand())
            .isInstanceOf(ValueExpression.class)
            .extracting(v -> ((ValueExpression) v).getValue())
            .isEqualTo(-2);
    }

    @Test
    void existsExpressionOfARequiredAttribute() {
        Expression expression = Parser.getDefault().parse("EXISTS id");
        assertThat(expression)
            .isInstanceOf(ExpressionImpl.class);

        ExpressionInternal internal = ((ExpressionImpl) expression).getExpressionInternal();
        assertThat(internal)
            .isInstanceOf(ValueExpression.class)
            .extracting(v -> ((ValueExpression) v).getValue())
            .isEqualTo(true);
    }

    @Test
    void existsExpressionOfANonRequiredAttribute() {
        Expression expression = Parser.getDefault().parse("EXISTS time");
        assertThat(expression)
            .isInstanceOf(ExpressionImpl.class);

        ExpressionInternal internal = ((ExpressionImpl) expression).getExpressionInternal();
        assertThat(internal)
            .isInstanceOf(ExistsExpression.class);
    }
}
