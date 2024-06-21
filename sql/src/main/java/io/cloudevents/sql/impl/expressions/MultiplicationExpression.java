package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.impl.ExceptionFactoryImpl;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import org.antlr.v4.runtime.misc.Interval;

public class MultiplicationExpression extends BaseIntegerBinaryExpression {

    public MultiplicationExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    @Override
    EvaluationResult evaluate(EvaluationRuntime runtime, int left, int right, ExceptionFactory exceptions) {
        return new EvaluationResult(left * right);
    }

}
