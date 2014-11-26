/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

grammar MongoAL;

query: FROM simpleId
     stage+;

stage : groupStage; // | matchStage | sortStage;

groupStage : GROUP_BY compoundId AS simpleId;
    
         



// values and identifiers
integer : DIGIT+;
simpleId : LETTER (LETTER|DIGIT)*;
compoundId : simpleId (arrayIndex)? (DOT compoundId)?;           
arrayIndex : LBRACK integer RBRACK;
             
// keywords
FROM : [Ff][Rr][Oo][Mm];
GROUP_BY : [Gg][Rr][Oo][Uu][Pp]WS[Bb][Yy];
AS : [Aa][Ss];

// separators
COMMA   : ',';
DOT     : '.';
LBRACK  : '[';
RBRACK  : ']';

// operators



// auxiliary


DIGIT : [0-9];

LETTER : [a-zA-Z$_];

WS  :  [ \t\r\n\u000C]+ -> skip;