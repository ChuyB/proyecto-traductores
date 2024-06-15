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
    | boolExpr;
boolExpr:
    | not
    | leq
    | less
    | geq
    | greater
    | equal
    | neq
    | value;
concat
    : (string | value) TkConcat (string | value)
    | concat TkConcat (string | value);

not     : TkNot value | value TkNot not;
leq     : value TkLeq value;
less    : value TkLess value;
geq     : value TkGeq value;
greater : value TkGreater value;
equal   : (value | not) TkEqual (value | not);
neq     : value TkNEqual value;
and
    : (value | boolExpr) TkAnd (value | boolExpr)
    | and TkAnd (value | boolExpr);
or
    : (value | boolExpr) TkOr (value | boolExpr)
    | or TkOr (value | boolExpr);

plus
    : value TkPlus value
    | plus TkPlus value
    | plus TkPlus mult
    | (value | mult) TkPlus (value | mult | plus);
minus
    : value TkMinus value
    | minus TkMinus value
    | minus TkMinus mult
    | (value | plus | mult) TkMinus (value | plus | minus | mult);
mult        : value TkMult value | mult TkMult value;
uMinus      : TkMinus value;

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
