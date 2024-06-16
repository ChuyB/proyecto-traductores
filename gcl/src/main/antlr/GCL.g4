grammar GCL;

program : block;

block       : TkOBlock (declare | (instruct | sequencing) | declare (instruct | sequencing)) TkCBlock;
sequencing  : instruct TkSemicolon instruct | sequencing TkSemicolon instruct;
declare     : TkDeclare declareBody | TkDeclare sequencing;

instruct    : block | asig | if | do | for | print | skip | declareBody;

declareBody : TkId (TkComma TkId)* TkTwoPoints type;
expr        : value | boolOp | numExpr;
ident       : TkId;
numExpr
    : plus
    | minus
    | mult;
boolOp
    : and
    | or
    | (boolExpr);
boolExpr
    : not
    | leq
    | less
    | geq
    | greater
    | equal
    | notEqual;
concat
    : (string | value) TkConcat (string | value)
    | concat TkConcat (string | value);

not     : TkNot ((value | numExpr) | equal) | TkNot not;
leq     : (value | numExpr) TkLeq (value | numExpr);
less    : (value | numExpr) TkLess (value | numExpr);
geq     : (value | numExpr) TkGeq (value | numExpr);
greater : (value | numExpr) TkGreater (value | numExpr);
equal   : (value | numExpr) TkEqual (value | numExpr | not);
notEqual: (value | numExpr) TkNEqual (value | numExpr);
and
    : value TkAnd value
    | and TkAnd (value | boolExpr | parOr)
    | (value | boolExpr) TkAnd (value | boolExpr | parOr)
    | parOr TkAnd (value | boolExpr | and)
    | TkOpenPar value TkAnd value TkClosePar
    | TkOpenPar and TkAnd (value | boolExpr | parOr) TkClosePar
    | TkOpenPar (value | boolExpr) TkAnd (value | boolExpr | parOr) TkClosePar
    | TkOpenPar parOr TkAnd (value | boolExpr | and) TkClosePar;
or
    : value TkOr value
    | or TkOr (boolExpr | value | and)
    | (value | boolExpr | and) TkOr (value | boolOp)
    | TkOpenPar value TkOr value TkClosePar
    | TkOpenPar or TkOr (boolExpr | value | and) TkClosePar
    | TkOpenPar (value | boolExpr | and) TkOr (value | boolOp) TkClosePar;
parOr   : TkOpenPar or TkClosePar;

plus
    : value TkPlus value
    | plus TkPlus (value | mult | parNumExpr)
    | (value | mult) TkPlus (value | mult | plus | parNumExpr)
    | TkOpenPar value TkPlus value TkClosePar
    | TkOpenPar plus TkPlus value TkClosePar
    | TkOpenPar plus TkPlus mult TkClosePar
    | TkOpenPar (value | mult) TkPlus (value | mult | plus) TkClosePar;
minus
    : value TkMinus value
    | minus TkMinus (value | mult | parNumExpr)
    | (value | plus | mult) TkMinus (value | plus | minus | mult | parNumExpr)
    | TkOpenPar value TkMinus value TkClosePar
    | TkOpenPar minus TkMinus value TkClosePar
    | TkOpenPar minus TkMinus mult TkClosePar
    | TkOpenPar (value | plus | mult) TkMinus (value | plus | minus | mult) TkClosePar;
parNumExpr
    : TkOpenPar plus TkClosePar
    | TkOpenPar minus TkClosePar
    | TkOpenPar mult TkClosePar;
mult
    : value TkMult value
    | mult TkMult (value | parNumExpr)
    | (value | parNumExpr) TkMult (value | parNumExpr)
    | TkOpenPar value TkMult value TkClosePar
    | TkOpenPar mult TkMult value TkClosePar;
uMinus
    : TkMinus value
    | TkOpenPar TkMinus value TkClosePar;

writeArray  : ident TkOpenPar twoPoints TkClosePar | writeArray TkOpenPar twoPoints TkClosePar;
readArray   : (ident | writeArray) TkOBracket (ident | literal) TkCBracket | readArray TkOBracket (ident | literal) TkCBracket;
twoPoints   : expr TkTwoPoints expr;
comma       : expr TkComma expr | comma TkComma expr;

then        : boolOp TkArrow (instruct | sequencing);
guard       : then TkGuard then | guard TkGuard then;

in          : ident TkIn to;
to          : expr TkTo expr;

print       : TkPrint (string | ident | concat);
asig        : ident TkAsig ((expr | comma) | writeArray | readArray);
if          : TkIf (then | guard) TkFi;
for         : TkFor in TkArrow instruct TkRof;
do          : TkDo (then | guard) TkOd;
skip        : TkSkip;

value       : literal | ident | readArray | uMinus;
literal     : TkNum | TkTrue | TkFalse;
string      : TkString;
type        : TkInt | TkBool | array;
array       : TkArray TkOBracket (TkNum | TkMinus TkNum) TkSoForth (TkNum | TkMinus TkNum) TkCBracket;

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
