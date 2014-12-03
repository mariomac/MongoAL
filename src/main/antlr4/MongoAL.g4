grammar MongoAL;

@header {
    package es.bsc.mongoal;
    
    import com.fasterxml.jackson.databind.*;
    import com.mongodb.*;
    import java.util.ArrayList;
    import java.util.List;    
}

query : FROM collId=SIMPLEID stage* ;

stage : groupStage | matchStage ; // | sortStage;

groupStage : GROUP_BY
             (SIMPLEID |
             groupExpression AS SIMPLEID);

matchStage : MATCH logicalExpression;
   
arrayIndex : LBRACK INTEGER RBRACK;
compoundId : SIMPLEID (arrayIndex)? (DOT compoundId)?;


// expressions

groupExpression : addSubExpr | accumExpr;

addSubExpr  : multDivExpr ( (ADD|SUB) multDivExpr)*;
multDivExpr : atomExpr ((MULT|DIV) atomExpr)*;
atomExpr    : FLOAT | INTEGER | (LPAR addSubExpr RPAR) | compoundId | negative;
negative    : SUB addSubExpr;
              
accumExpr : (ACCADDTOSET | ACCAVG | ACCFIRST | ACCLAST | ACCMAX | ACCMIN | ACCPUSH | ACCSUM) 
            LPAR addSubExpr RPAR;

// Doesn't allow as much as flexibility as C-like logical expressions, since we are
// not considering numbers as booleans or vice-versa.
// E.g. the next expressions are not allowed
// var2+3 OR var   --> that should be --> var2+3 != 0 OR var!=0
// (cond OR cond2) == cond5

logicalExpression       : orExpression (OPAND orExpression)*;
orExpression            : atomLogicalExpression (OPOR atomLogicalExpression)*;
atomLogicalExpression   : LPAR logicalExpression RPAR | comparisonExpression | OPNOT logicalExpression;

comparisonExpression    : leftComparison (OPGT|OPGTE|OPLT|OPLTE|OPEQ|OPNEQ) rightComparison;

leftComparison : compoundId;
rightComparison : STRING | INT | FLOAT;


//LEXER

// LOGICAL OPERATORS

OPAND : [Aa][Nn][Dd];
OPOR  : [Oo][Rr];
OPNOT : [Nn][Oo][Tt];

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
AS : [Aa][Ss];
MATCH : [Mm][Aa][Tt][Cc][Hh];


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

// accumulators
ACCADDTOSET : [Aa][Dd][Dd][Tt][Oo][Se][T];
ACCAVG : [Aa][Vv][Gg];
ACCFIRST : [Ff][Ii][Rr][Ss][Tt];
ACCLAST : [Ll][Aa][Ss][Tt];
ACCMAX : [Mm][Aa][Xx];
ACCMIN : [Mm][Ii][Nn];
ACCPUSH : [Pp][Uu][Ss][Hh];
ACCSUM : [Ss][Uu][Mm];

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