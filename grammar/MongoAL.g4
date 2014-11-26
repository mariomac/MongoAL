/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

grammar MongoAL;

query: ;
         



collectionId : LETTER LETTERORDIGIT*;

// keywords
FROM : [Ff][Rr][Oo][Mm];

// auxiliary

fragment
LETTER : [a-zA-Z$_];

fragment
LETTERORDIGIT : [a-zA-Z0-9$_];

fragment
WS  :  [ \t\r\n\u000C]+ -> skip ;