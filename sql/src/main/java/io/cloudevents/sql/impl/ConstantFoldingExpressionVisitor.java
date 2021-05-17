package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.expressions.*;

public class ConstantFoldingExpressionVisitor implements ExpressionInternalVisitor<ExpressionInternal> {

    @Override
    public ExpressionInternal visitExpressionInternal(ExpressionInternal expressionInternal) {
        return expressionInternal;
    }

    @Override
    public ExpressionInternal visitAccessAttributeExpression(AccessAttributeExpression accessAttributeExpression) {
        return accessAttributeExpression;
    }

    @Override
    public ExpressionInternal visitBaseBinaryExpression(BaseBinaryExpression baseBinaryExpression) {
        ExpressionInternal left = baseBinaryExpression.getLeftOperand().visit(this);
        ExpressionInternal right = baseBinaryExpression.getRightOperand().visit(this);

        if (left instanceof ValueExpression && right instanceof ValueExpression) {
            // I can do constant folding!
            return new ValueExpression(
                baseBinaryExpression.expressionInterval(),
                baseBinaryExpression.expressionText(),
                baseBinaryExpression.evaluate(
                    EvaluationRuntime.getDefault(),
                    ((ValueExpression) left).getValue(),
                    ((ValueExpression) right).getValue(),
                    FailFastExceptionThrower.getInstance()
                )
            );
        }

        baseBinaryExpression.setLeftOperand(left);
        baseBinaryExpression.setRightOperand(right);
        return baseBinaryExpression;
    }

    @Override
    public ExpressionInternal visitExistsExpression(ExistsExpression existsExpression) {
        return ExpressionInternalVisitor.super.visitExistsExpression(existsExpression);
    }

    @Override
    public ExpressionInternal visitFunctionInvocationExpression(FunctionInvocationExpression functionInvocationExpression) {
        return ExpressionInternalVisitor.super.visitFunctionInvocationExpression(functionInvocationExpression);
    }

    @Override
    public ExpressionInternal visitInExpression(InExpression inExpression) {
        return ExpressionInternalVisitor.super.visitInExpression(inExpression);
    }

    @Override
    public ExpressionInternal visitLikeExpression(LikeExpression likeExpression) {
        return ExpressionInternalVisitor.super.visitLikeExpression(likeExpression);
    }

    @Override
    public ExpressionInternal visitBaseUnaryExpression(BaseUnaryExpression baseUnaryExpression) {
        ExpressionInternal inner = baseUnaryExpression.getOperand().visit(this);

        if (inner instanceof ValueExpression) {
            return new ValueExpression(
                baseUnaryExpression.expressionInterval(),
                baseUnaryExpression.expressionText(),
                baseUnaryExpression.evaluate(EvaluationRuntime.getDefault(), ((ValueExpression) inner).getValue(), FailFastExceptionThrower.getInstance())
            );
        }

        baseUnaryExpression.setOperand(inner);
        return baseUnaryExpression;
    }

    @Override
    public ExpressionInternal visitValueExpression(ValueExpression valueExpression) {
        return valueExpression;
    }
}
