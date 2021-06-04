package io.cloudevents.sql.impl.expressions;

public interface ExpressionInternalVisitor<T> {

    T visitExpressionInternal(ExpressionInternal expressionInternal);

    default T visitAccessAttributeExpression(AccessAttributeExpression accessAttributeExpression) {
        return visitExpressionInternal(accessAttributeExpression);
    }

    default T visitBaseBinaryExpression(BaseBinaryExpression baseBinaryExpression) {
        return visitExpressionInternal(baseBinaryExpression);
    }

    default T visitExistsExpression(ExistsExpression existsExpression) {
        return visitExpressionInternal(existsExpression);
    }

    default T visitFunctionInvocationExpression(FunctionInvocationExpression functionInvocationExpression) {
        return visitExpressionInternal(functionInvocationExpression);
    }

    default T visitInExpression(InExpression inExpression) {
        return visitExpressionInternal(inExpression);
    }

    default T visitLikeExpression(LikeExpression likeExpression) {
        return visitExpressionInternal(likeExpression);
    }

    default T visitBaseUnaryExpression(BaseUnaryExpression baseUnaryExpression) {
        return visitExpressionInternal(baseUnaryExpression);
    }

    default T visitValueExpression(ValueExpression valueExpression) {
        return visitExpressionInternal(valueExpression);
    }
}
