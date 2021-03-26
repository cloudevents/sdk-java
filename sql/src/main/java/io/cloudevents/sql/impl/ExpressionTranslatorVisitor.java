package io.cloudevents.sql.impl;

import io.cloudevents.sql.generated.CESQLParserBaseVisitor;
import io.cloudevents.sql.generated.CESQLParserParser;

public class ExpressionTranslatorVisitor extends CESQLParserBaseVisitor<ExpressionInternal> {

    @Override
    public ExpressionInternal visitCesql(CESQLParserParser.CesqlContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public ExpressionInternal visitBooleanLiteral(CESQLParserParser.BooleanLiteralContext ctx) {
        if (ctx.TRUE() != null) {
            return new ValueExpression(true);
        } else {
            return new ValueExpression(false);
        }
    }

}
