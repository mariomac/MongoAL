/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
grammar MongoAL;

query: FROM SIMPLEID stage+;

stage : groupStage; // | matchStage | sortStage;

groupStage : GROUP_BY
             (SIMPLEID |
             expression AS SIMPLEID); 
   
arrayIndex : LBRACK INTEGER RBRACK;
compoundId : SIMPLEID (arrayIndex)? (DOT compoundId)?;


// expressions

expression : addSubExpr | accumExpr;            

addSubExpr  : multDivExpr ( (ADD|SUB) multDivExpr)*;
multDivExpr : atomExpr ((MULT|DIV) atomExpr)*;
atomExpr    : FLOAT | INTEGER | (LPAR addSubExpr RPAR) | compoundId | negative;
negative    : SUB addSubExpr;
              
accumExpr : (ACCADDTOSET | ACCAVG | ACCFIRST | ACCLAST | ACCMAX | ACCMIN | ACCPUSH | ACCSUM) 
            LPAR addSubExpr RPAR ;
              

//LEXER
             
// keywords
FROM : [Ff][Rr][Oo][Mm];
GROUP_BY : [Gg][Rr][Oo][Uu][Pp]WS[Bb][Yy];
AS : [Aa][Ss];

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

// auxiliary


fragment DIGIT : [0-9] ;

fragment LETTER : [a-zA-Z$_];

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) -> skip
    ;