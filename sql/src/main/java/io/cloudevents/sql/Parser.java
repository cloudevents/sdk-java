package io.cloudevents.sql;

import io.cloudevents.sql.impl.parser.ParserBuilder;
import io.cloudevents.sql.impl.parser.ParserImpl;

public interface Parser {

    /**
     * Parse the expression.
     *
     * @param inputExpression input expression
     * @return the parsed expression
     * @throws ParseException if the expression cannot be parsed
     */
    Expression parse(String inputExpression) throws ParseException;

    /**
     * Parse the expression with default parser instance.
     *
     * @param inputExpression input expression
     * @return the parsed expression
     * @throws ParseException if the expression cannot be parsed
     */
    static Expression parseDefault(String inputExpression) throws ParseException {
        return ParserImpl.getInstance().parse(inputExpression);
    }

    /**
     * @return the default instance of the parser
     */
    static Parser getDefault() {
        return ParserImpl.getInstance();
    }

    /**
     * @return a new {@link ParserBuilder}, to create a customized parser instance
     */
    static ParserBuilder builder() {
        return new ParserBuilder();
    }

}
