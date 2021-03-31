package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.EvaluationExceptions;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

public class MultiplicationExpression extends BaseIntegerBinaryExpression {

    public MultiplicationExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    @Override
    Object evaluate(EvaluationRuntime runtime, int left, int right, EvaluationExceptions exceptions) {
        return left * right;
    }

}
