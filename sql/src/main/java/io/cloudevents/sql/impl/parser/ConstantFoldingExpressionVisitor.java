package io.cloudevents.sql.impl.parser;

import io.cloudevents.SpecVersion;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.ExpressionInternalVisitor;
import io.cloudevents.sql.impl.expressions.BaseBinaryExpression;
import io.cloudevents.sql.impl.expressions.BaseUnaryExpression;
import io.cloudevents.sql.impl.expressions.ExistsExpression;
import io.cloudevents.sql.impl.expressions.ValueExpression;

public class ConstantFoldingExpressionVisitor implements ExpressionInternalVisitor<ExpressionInternal> {

    @Override
    public ExpressionInternal visitExpressionInternal(ExpressionInternal expressionInternal) {
        return expressionInternal;
    }

    @Override
    public ExpressionInternal visitBaseBinaryExpression(BaseBinaryExpression baseBinaryExpression) {
        ExpressionInternal left = baseBinaryExpression.getLeftOperand().visit(this);
        ExpressionInternal right = baseBinaryExpression.getRightOperand().visit(this);

        baseBinaryExpression.setLeftOperand(left);
        baseBinaryExpression.setRightOperand(right);
        return baseBinaryExpression;
    }

    @Override
    public ExpressionInternal visitExistsExpression(ExistsExpression existsExpression) {
        if (SpecVersion.V1.getMandatoryAttributes().contains(existsExpression.getKey())) {
            // If the attribute is a mandatory attribute of the spec, there's no need to check it
            return new ValueExpression(existsExpression.expressionInterval(), existsExpression.expressionText(), true);
        }
        return existsExpression;
    }

    @Override
    public ExpressionInternal visitBaseUnaryExpression(BaseUnaryExpression baseUnaryExpression) {
        ExpressionInternal inner = baseUnaryExpression.getOperand().visit(this);

        baseUnaryExpression.setOperand(inner);
        return baseUnaryExpression;
    }
}
