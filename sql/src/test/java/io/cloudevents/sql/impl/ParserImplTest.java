package io.cloudevents.sql.impl;

import io.cloudevents.sql.Expression;
import io.cloudevents.sql.Parser;
import io.cloudevents.sql.impl.expressions.ExpressionInternal;
import io.cloudevents.sql.impl.expressions.ValueExpression;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserImplTest {

    @Test
    void constantFolding() {
        Expression expression = Parser.getDefault().parse("1 + 2");
        assertThat(expression)
            .isInstanceOf(ExpressionImpl.class);

        ExpressionInternal internal = ((ExpressionImpl) expression).getExpressionInternal();
        assertThat(internal)
            .isInstanceOf(ValueExpression.class)
            .extracting(v -> ((ValueExpression) v).getValue())
            .isEqualTo(3);
    }

}
