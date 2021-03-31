package io.cloudevents.sql.impl;

import io.cloudevents.sql.ParseException;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.generated.CESQLParserBaseVisitor;
import io.cloudevents.sql.generated.CESQLParserParser;
import io.cloudevents.sql.impl.expressions.*;

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

    @Override
    public ExpressionInternal visitBinaryMultiplicativeExpression(CESQLParserParser.BinaryMultiplicativeExpressionContext ctx) {
        ExpressionInternal leftExpression = visit(ctx.expression(0));
        ExpressionInternal rightExpression = visit(ctx.expression(1));

        if (ctx.STAR() != null) {
            return new MultiplicationExpression(ctx.getSourceInterval(), ctx.getText(), leftExpression, rightExpression);
        } else if (ctx.DIVIDE() != null) {
            return new DivisionExpression(ctx.getSourceInterval(), ctx.getText(), leftExpression, rightExpression);
        } else {
            return new ModuleExpression(ctx.getSourceInterval(), ctx.getText(), leftExpression, rightExpression);
        }
    }

    @Override
    public ExpressionInternal visitBinaryAdditiveExpression(CESQLParserParser.BinaryAdditiveExpressionContext ctx) {
        ExpressionInternal leftExpression = visit(ctx.expression(0));
        ExpressionInternal rightExpression = visit(ctx.expression(1));

        if (ctx.PLUS() != null) {
            return new SumExpression(ctx.getSourceInterval(), ctx.getText(), leftExpression, rightExpression);
        } else {
            return new DifferenceExpression(ctx.getSourceInterval(), ctx.getText(), leftExpression, rightExpression);
        }
    }

    @Override
    public ExpressionInternal visitBinaryComparisonExpression(CESQLParserParser.BinaryComparisonExpressionContext ctx) {
        ExpressionInternal leftExpression = visit(ctx.expression(0));
        ExpressionInternal rightExpression = visit(ctx.expression(1));

        if (ctx.EQUAL() != null) {
            // Equality operation is ambiguous, we have a specific implementation for it
            return new EqualExpression(ctx.getSourceInterval(), ctx.getText(), leftExpression, rightExpression);
        }
        if (ctx.NOT_EQUAL() != null || ctx.LESS_GREATER() != null) {
            // Equality operation is ambiguous, we have a specific implementation for it
            return new NotExpression(ctx.getSourceInterval(), ctx.getText(), new EqualExpression(ctx.getSourceInterval(), ctx.getText(), leftExpression, rightExpression));
        }

        // From this onward, just operators defined on integers
        IntegerComparisonBinaryExpression.Operation op;
        if (ctx.LESS() != null) {
            op = IntegerComparisonBinaryExpression.Operation.LESS;
        } else if (ctx.LESS_OR_EQUAL() != null) {
            op = IntegerComparisonBinaryExpression.Operation.LESS_OR_EQUAL;
        } else if (ctx.GREATER() != null) {
            op = IntegerComparisonBinaryExpression.Operation.GREATER;
        } else {
            op = IntegerComparisonBinaryExpression.Operation.GREATER_OR_EQUAL;
        }

        return new IntegerComparisonBinaryExpression(ctx.getSourceInterval(), ctx.getText(), leftExpression, rightExpression, op);
    }

    @Override
    public ExpressionInternal visitBinaryLogicExpression(CESQLParserParser.BinaryLogicExpressionContext ctx) {
        ExpressionInternal leftExpression = visit(ctx.expression(0));
        ExpressionInternal rightExpression = visit(ctx.expression(1));

        LogicalBinaryExpression.Operation op;
        if (ctx.AND() != null) {
            op = LogicalBinaryExpression.Operation.AND;
        } else if (ctx.OR() != null) {
            op = LogicalBinaryExpression.Operation.OR;
        } else {
            op = LogicalBinaryExpression.Operation.XOR;
        }

        return new LogicalBinaryExpression(ctx.getSourceInterval(), ctx.getText(), leftExpression, rightExpression, op);
    }

    @Override
    public ExpressionInternal visitLikeExpression(CESQLParserParser.LikeExpressionContext ctx) {
        ExpressionInternal leftExpression = visit(ctx.expression());
        ExpressionInternal likeExpression = new LikeExpression(
            ctx.getSourceInterval(),
            ctx.getText(),
            leftExpression,
            (ctx.stringLiteral().DQUOTED_STRING_LITERAL() != null) ?
                LiteralUtils.parseDQuotedStringLiteral(ctx.stringLiteral().DQUOTED_STRING_LITERAL()) :
                LiteralUtils.parseSQuotedStringLiteral(ctx.stringLiteral().SQUOTED_STRING_LITERAL())
        );
        return (ctx.NOT() != null) ? new NotExpression(ctx.getSourceInterval(), ctx.getText(), likeExpression) : likeExpression;
    }

    @Override
    public ExpressionInternal visitFunctionInvocation(CESQLParserParser.FunctionInvocationContext ctx) {
        List<ExpressionInternal> parameters = ctx.functionParameterList().expression().stream()
            .map(this::visit)
            .collect(Collectors.toList());

        return new FunctionInvocationExpression(ctx.getSourceInterval(), ctx.getText(), ctx.functionIdentifier().getText(), parameters);
    }
}
