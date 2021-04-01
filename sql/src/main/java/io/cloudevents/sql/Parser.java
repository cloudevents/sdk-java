package io.cloudevents.sql;

import io.cloudevents.sql.impl.ParserImpl;

public interface Parser {

    Expression parse(String inputExpression);

    /**
     * Parse with default parser instance
     *
     * @param inputExpression input expression
     * @return the parsed expression
     * @throws ParseException if the expression cannot be parsed
     */
    static Expression parseDefault(String inputExpression) throws ParseException {
        return ParserImpl.getInstance().parse(inputExpression);
    }

}
