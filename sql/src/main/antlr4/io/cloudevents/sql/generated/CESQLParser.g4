grammar CESQLParser;

import CESQLLexer;

// Entrypoint
cesql: expression EOF;

// Structure of operations, function invocations and expression
functionInvocation
    : functionIdentifier functionParameterList
    ;

unaryOperation
    : NOT expression #unaryLogicExpression
    | MINUS expression # unaryNumericExpression
    ;

expression
    : functionInvocation #functionInvocationExpression
    | unaryOperation #unaryExpression
    // LIKE, EXISTS and IN takes precedence over all the other operators
    | expression NOT? LIKE stringLiteral #likeExpression
    | EXISTS identifier #existsExpression
    | expression NOT? IN setExpression #inExpression
    // Numeric operations
    | expression (STAR | DIVIDE | MODULE) expression #binaryMultiplicativeExpression
    | expression (PLUS | MINUS) expression #binaryAdditiveExpression
    // Comparison operations
    | expression (EQUAL | EXCLAMATION EQUAL | LESS_GREATER | GREATER_OR_EQUAL | LESS_OR_EQUAL | LESS | GREATER) expression #binaryComparisonExpression
    // Logic operations
    |<assoc=right> expression (AND | OR | XOR) expression #binaryLogicExpression
    // Subexpressions and atoms
    | LR_BRACKET expression RR_BRACKET #subExpression
    | atom #atomExpression
    ;

atom
    : booleanLiteral #booleanAtom
    | integerLiteral #integerAtom
    | stringLiteral #stringAtom
    | identifier #identifierAtom
    ;

// Identifiers

identifier: IDENTIFIER;
functionIdentifier: FUNCTION_IDENTIFIER;

// Literals

booleanLiteral: (TRUE | FALSE);
stringLiteral: (DQUOTED_STRING_LITERAL | SQUOTED_STRING_LITERAL);
integerLiteral: INTEGER_LITERAL;

// Functions

functionParameterList
    : LR_BRACKET ( expression ( COMMA expression )* )? RR_BRACKET
    ;

// Sets

setExpression
    : LR_BRACKET expression ( COMMA expression )* RR_BRACKET // Empty sets are not allowed
    ;
