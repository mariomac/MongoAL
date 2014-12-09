grammar MongoAL;

@header {
    package es.bsc.mongoal;
}

query : FROM collId=SIMPLEID stage* ;

stage : groupStage | matchStage | sortStage;

groupStage : GROUP_BY (addSubExpr | NOTHING)
            (accumExpr AS SIMPLEID)*;

matchStage : MATCH logicalExpression;
   
arrayIndex : LBRACK INTEGER RBRACK;
compoundId : SIMPLEID (arrayIndex)? (DOT compoundId)?;


// expressions
// simpleid stores the accumulator
accumExpr : SIMPLEID
            LPAR addSubExpr RPAR;

addSubExpr  : multDivExpr ( (ADD|SUB) multDivExpr)?;
multDivExpr : atomExpr ((MULT|DIV|MOD) atomExpr)?;
atomExpr    : FLOAT | INTEGER | (LPAR addSubExpr RPAR) | compoundId | negative;
negative    : SUB addSubExpr;
              
sortStage : SORT_BY sortCriteria (COMMA sortCriteria)*;
sortCriteria : compoundId (ASCENDING|DESCENDING)?;


// Doesn't allow as much as flexibility as C-like logical expressions, since we are
// not considering numbers as booleans or vice-versa.
// E.g. the next expressions are not allowed
// var2+3 OR var   --> that should be --> var2+3 != 0 OR var!=0
// (cond OR cond2) == cond5

logicalExpression       : orExpression (OPAND orExpression)*;
orExpression            : atomLogicalExpression (OPOR atomLogicalExpression)*;
atomLogicalExpression   : LPAR logicalExpression RPAR | comparisonExpression; // | OPNOT logicalExpression;

comparisonExpression    : leftComparison (OPGT|OPGTE|OPLT|OPLTE|OPEQ|OPNEQ) rightComparison;

leftComparison : compoundId;
rightComparison : STRING | INTEGER | FLOAT;


//LEXER

// LOGICAL OPERATORS

OPAND : [Aa][Nn][Dd];
OPOR  : [Oo][Rr];
OPNOT : [Nn][Oo][Tt];
OPNOR : [Nn][Oo][Rr];

// comparison operators

OPGT  : '>';
OPGTE : '>=';
OPLT  : '<';
OPLTE : '<=';
OPEQ  : '=';
OPNEQ : '!=';

// by the moment we don't support OPNOR :

// keywords
FROM : [Ff][Rr][Oo][Mm];
GROUP_BY : [Gg][Rr][Oo][Uu][Pp]WS[Bb][Yy];
NOTHING : [Nn][Oo][Tt][Hh][Ii][Nn][Gg];
AS : [Aa][Ss];
MATCH : [Mm][Aa][Tt][Cc][Hh];
SORT_BY : [Ss][Oo][Rr][Tt]WS[Bb][Yy];
ASCENDING : [Aa][Ss][Cc][Ee][Nn][Dd][Ii][Nn][Gg];
DESCENDING : [Dd][Ee][Ss][Cc][Ee][Nn][Dd][Ii][Nn][Gg];

// separators
COMMA   : ',';
DOT     : '.';
LBRACK  : '[';
RBRACK  : ']';
ADD     : '+';
SUB     : '-';
MULT    : '*';
DIV     : '/';
MOD     : '%';
LPAR    : '(';
RPAR    : ')';

// LEXER

// values and identifiers
INTEGER : [ADD|SUB]? DIGIT+;
FLOAT   : [ADD|SUB]? DIGIT+ (DOT DIGIT+)?;
SIMPLEID : LETTER ( LETTER |DIGIT)*;

// strings can be surronded by 'simple' or "double" commas
STRING :  ('"' (ESC_CHAR | ~["\\])* '"') | ('\'' (ESC_CHAR | ~[\'\\])* '\'');

fragment ESC_CHAR :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;

// auxiliary


fragment DIGIT : [0-9] ;

fragment LETTER : [a-zA-Z$_];

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) -> skip
    ;
