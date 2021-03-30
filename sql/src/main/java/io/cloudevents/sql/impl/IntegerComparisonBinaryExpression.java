package io.cloudevents.sql.impl;

import org.antlr.v4.runtime.misc.Interval;

import java.util.function.BiFunction;

public class IntegerComparisonBinaryExpression extends BaseBinaryExpression {

    enum Operation {
        LESS((x, y) -> x < y),
        LESS_OR_EQUAL((x, y) -> x <= y),
        GREATER((x, y) -> x > y),
        GREATER_OR_EQUAL((x, y) -> x >= y);

        private final BiFunction<Integer, Integer, Boolean> fn;

        Operation(BiFunction<Integer, Integer, Boolean> fn) {
            this.fn = fn;
        }

        boolean evaluate(int a, int b) {
            return this.fn.apply(a, b);
        }
    }

    private final Operation operation;

    protected IntegerComparisonBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand, Operation operation) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
        this.operation = operation;
    }

    @Override
    Object evaluate(EvaluationContextImpl ctx, Object left, Object right) {
        return this.operation.evaluate(
            castToInteger(ctx, left),
            castToInteger(ctx, right)
        );
    }

}