package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.EvaluationExceptions;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InExpression extends BaseExpression {

    // leftOperand IN (setExpressions...)
    private final ExpressionInternal leftExpression;
    private final List<ExpressionInternal> setExpressions;

    // TODO this expression can be optimized if the ExpressionInternal are all ValueExpression (aka set is composed by literals)
    public InExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftExpression, List<ExpressionInternal> setExpressions) {
        super(expressionInterval, expressionText);
        this.leftExpression = leftExpression;
        this.setExpressions = setExpressions;
    }

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, EvaluationExceptions exceptions) {
        Object leftValue = leftExpression.evaluate(runtime, event, exceptions);
        Set<Object> set = setExpressions.stream()
            .map(expr -> expr.evaluate(runtime, event, exceptions))
            .collect(Collectors.toSet());

        return set.contains(leftValue);
    }
}
