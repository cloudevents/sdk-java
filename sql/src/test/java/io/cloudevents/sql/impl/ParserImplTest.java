package io.cloudevents.sql.impl;

import io.cloudevents.sql.Expression;
import io.cloudevents.sql.Parser;
import io.cloudevents.sql.impl.expressions.BaseBinaryExpression;
import io.cloudevents.sql.impl.expressions.ExpressionInternal;
import io.cloudevents.sql.impl.expressions.ValueExpression;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserImplTest {

    @Test
    void constantFoldingWithBinaryExpression() {
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
    void constantFoldingWithUnaryExpression() {
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
    void constantFoldingWithBinaryAndUnaryExpression() {
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
}
