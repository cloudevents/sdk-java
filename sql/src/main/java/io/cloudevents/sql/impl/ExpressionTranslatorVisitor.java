package io.cloudevents.sql.impl;

import io.cloudevents.sql.ParseException;
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

    @Override
    public ExpressionInternal visitIntegerLiteral(CESQLParserParser.IntegerLiteralContext ctx) {
        try {
            return ValueExpression.fromIntegerLiteral(ctx.INTEGER_LITERAL());
        } catch (RuntimeException e) {
            throw new ParseException(e); //TODO this should contain the interval and the literal value!
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
            throw new ParseException(e); //TODO this should contain the interval and the literal value!
        }
    }
}
