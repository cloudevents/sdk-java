package io.cloudevents.sql.impl;

import io.cloudevents.sql.ParseException;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.generated.CESQLParserBaseVisitor;
import io.cloudevents.sql.generated.CESQLParserParser;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public ExpressionInternal visitUnaryLogicExpression(CESQLParserParser.UnaryLogicExpressionContext ctx) {
        // Only 'not' is a valid unary logic expression
        ExpressionInternal internal = visit(ctx.expression());
        return new NotExpression(ctx.getSourceInterval(), ctx.getText(), internal);
    }

    @Override
    public ExpressionInternal visitUnaryNumericExpression(CESQLParserParser.UnaryNumericExpressionContext ctx) {
        // Only 'negate' is a valid unary logic expression
        ExpressionInternal internal = visit(ctx.expression());
        return new NegateExpression(ctx.getSourceInterval(), ctx.getText(), internal);
    }

    @Override
    public ExpressionInternal visitIdentifier(CESQLParserParser.IdentifierContext ctx) {
        return new AccessAttributeExpression(ctx.getSourceInterval(), ctx.getText(), ctx.getText());
    }

    @Override
    public ExpressionInternal visitInExpression(CESQLParserParser.InExpressionContext ctx) {
        ExpressionInternal leftExpression = visit(ctx.expression());
        List<ExpressionInternal> setExpressions = ctx.setExpression().expression().stream()
            .map(this::visit)
            .collect(Collectors.toList());

        ExpressionInternal inExpression = new InExpression(ctx.getSourceInterval(), ctx.getText(), leftExpression, setExpressions);
        return (ctx.NOT() != null) ? new NotExpression(ctx.getSourceInterval(), ctx.getText(), inExpression) : inExpression;
    }
}
