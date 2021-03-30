package io.cloudevents.sql.impl;

import org.antlr.v4.runtime.misc.Interval;

import java.util.function.BiFunction;

public class MathBinaryExpression extends BaseBinaryExpression {

    enum Operation {
        SUM(Integer::sum),
        DIFFERENCE((x, y) -> x - y),
        MULTIPLICATION((x, y) -> x * y),
        DIVISION((x, y) -> x / y),
        MODULE((x, y) -> x % y);

        private final BiFunction<Integer, Integer, Integer> fn;

        Operation(BiFunction<Integer, Integer, Integer> fn) {
            this.fn = fn;
        }

        int evaluate(int a, int b) {
            return this.fn.apply(a, b);
        }
    }

    private final Operation operation;

    protected MathBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand, Operation operation) {
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
