grammar GCL;

program : block;

block       : TkOBlock (declare | (instruct | sequencing) | declare (instruct | sequencing)) TkCBlock;
sequencing  : declStmt (TkSemicolon declStmt)* | instruct TkSemicolon instruct | sequencing TkSemicolon instruct;
declare     : TkDeclare declStmt | TkDeclare sequencing;
declStmt    : TkId (TkComma TkId)* TkTwoPoints type;

instruct    : block | asig | if | do | for | print;

expr        : literal | string | ident | expr boolBOp expr | minus | plus;
ident       : TkId;
boolExpr    : expr boolBOp expr | not;
numExpr     : expr numBOp expr | minus | plus;

boolUOp     : not expr;
concat      : (string | ident | readArray) TkConcat (string | ident | readArray) | concat TkConcat (string | ident | readArray);
boolBOp     : (TkOr | TkAnd | TkLess | TkLeq | TkGeq | TkGreater | TkEqual | TkNEqual);
numBOp      : (TkPlus | TkMinus | TkMult);

minus       : TkMinus (literal | ident) | (literal | ident) TkMinus (literal | ident) | minus TkMinus (literal | ident) | (literal | ident) TkMinus minus;
plus        : TkPlus (literal | ident) | (literal | ident) TkPlus (literal | ident) | plus TkPlus (literal | ident) | (literal | ident) TkPlus plus;

writeArray  : ident TkOpenPar twoPoints TkClosePar | writeArray TkOpenPar twoPoints TkClosePar | writeArray TkOBracket expr TkCBracket;
readArray   : ident TkOBracket (ident | literal) TkCBracket | readArray TkOBracket (ident | literal) TkCBracket;
twoPoints   : expr TkTwoPoints expr;
comma       : expr TkComma expr | comma TkComma expr;

not         : TkNot (literal | ident | readArray);
leq         : (literal | ident | readArray) TkLeq (literal | ident | readArray);
geq         : (literal | ident | readArray) TkGeq (literal | ident | readArray);
eq          : (literal | ident | readArray) TkEqual (literal | ident | readArray);
neq         : (literal | ident | readArray) TkNEqual (literal | ident | readArray);
less        : (literal | ident | readArray) TkLess (literal | ident | readArray);
greater     : (literal | ident | readArray) TkGreater (literal | ident | readArray);
and         : (not | leq | geq | eq | neq | less | greater | literal)  TkAnd (not | leq | geq | eq | neq | less | greater | literal) | and TkAnd (not | leq | geq | eq | neq | less | greater | literal);
or          : (not | leq | geq | eq | neq | less | greater | literal) TkOr (not | leq | geq | eq | neq | less | greater | literal) | or TkOr (not | leq | geq | eq | neq | less | greater | literal);

then        : (and | or | not | leq | geq | eq | neq | less | greater) TkArrow (instruct | sequencing);
guard       : then TkGuard then | guard TkGuard then;

in          : ident TkIn to;
to          : expr TkTo expr;

print       : TkPrint (string | ident | concat);
asig        : ident TkAsig ((expr | comma) | writeArray);
if          : TkIf (then | guard) TkFi;
for         : TkFor in TkArrow instruct TkRof;
do          : TkDo then TkOd;

literal     : TkNum | TkTrue | TkFalse;
string      : TkString;
type        : TkInt | TkBool | array;
array       : TkArray TkOBracket TkNum TkSoForth TkNum TkCBracket;

TkTrue      : 'true';
TkFalse     : 'false';

TkIn        : 'in';
TkTo        : 'to';
TkIf        : 'if';
TkFi        : 'fi';
TkDo        : 'do';
TkOd        : 'od';
TkFor       : 'for';
TkRof       : 'rof';
TkDeclare   : 'declare';
TkInt       : 'int';
TkBool      : 'bool';
TkPrint     : 'print';
TkArray     : 'array';
TkSkip      : 'skip';

fragment
Letter      : [a-zA-Z];
fragment
Digit       : [0-9];
TkId        : (Letter | '_') (Letter | Digit | '_')*;

TkNum       : [0-9]+;
TkString    : '"' ( ~('\\' | '"' | [\n]) | ('\\' ["n\\]) )* '"';


TkOBlock    : '|[';
TkCBlock    : ']|';
TkSoForth   : '..';
TkComma     : ',';
TkOpenPar   : '(';
TkClosePar  : ')';
TkAsig      : ':=';
TkSemicolon : ';';
TkArrow     : '-->';
TkGuard     : '[]';

TkPlus      : '+';
TkMinus     : '-';
TkMult      : '*';
TkOr        : '\\/';
TkAnd       : '/\\';
TkNot       : '!';
TkLess      : '<';
TkLeq       : '<=';
TkGeq       : '>=';
TkGreater   : '>';
TkEqual     : '==';
TkNEqual    : '!=';
TkOBracket  : '[';
TkCBracket  : ']';
TkTwoPoints : ':';
TkConcat    : '.';

Ignore      : (EOF | [ \t\r\n]+ | '//' ~[\r\n]*) -> skip;
