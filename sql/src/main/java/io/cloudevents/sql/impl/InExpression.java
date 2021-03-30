package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationException;
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
    public Object evaluate(EvaluationContextImpl ctx, CloudEvent event) throws EvaluationException {
        Object leftValue = leftExpression.evaluate(ctx, event);
        Set<Object> set = setExpressions.stream()
            .map(expr -> expr.evaluate(ctx, event))
            .collect(Collectors.toSet());

        return set.contains(leftValue);
    }
}
