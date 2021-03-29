package io.cloudevents.sql.impl;

import io.cloudevents.sql.ParseException;
import io.cloudevents.sql.Type;
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
            return new ValueExpression(ctx.getSourceInterval(), ctx.getText(), true);
        } else {
            return new ValueExpression(ctx.getSourceInterval(), ctx.getText(), false);
        }
    }

    @Override
    public ExpressionInternal visitIntegerLiteral(CESQLParserParser.IntegerLiteralContext ctx) {
        try {
            return ValueExpression.fromIntegerLiteral(ctx.INTEGER_LITERAL());
        } catch (RuntimeException e) {
            throw ParseException.cannotParseValue(ctx, Type.INTEGER, e);
        }
    }

    @Override
    public ExpressionInternal visitStringLiteral(CESQLParserParser.StringLiteralContext ctx) {
        try {
            if (ctx.DQUOTED_STRING_LITERAL() != null) {
                return ValueExpression.fromDQuotedStringLiteral(ctx.DQUOTED_STRING_LITERAL());
            } else {
                return ValueExpression.fromSQuotedStringLiteral(ctx.SQUOTED_STRING_LITERAL());
            }
        } catch (RuntimeException e) {
            throw ParseException.cannotParseValue(ctx, Type.STRING, e);
        }
    }

    @Override
    public ExpressionInternal visitSubExpression(CESQLParserParser.SubExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public ExpressionInternal visitExistsExpression(CESQLParserParser.ExistsExpressionContext ctx) {
        return new ExistsExpression(ctx.getSourceInterval(), ctx.getText(), ctx.identifier().getText());
    }
}
