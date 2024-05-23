grammar GCL;

program : block;

block       : TkOBlock (stmt)* TkCBlock;
stmt        : decl | instruct;
decl        : TkDeclare TkId (TkComma TkId)* TkTwoPoints type (TkSemicolon TkId (TkComma TkId)* TkTwoPoints type)*;
instruct    : instruct TkSemicolon instruct | assign | if | do | for | print;

expr        : TkId | primitive | expr bOperator expr | uOperator expr;
boolExpr    : expr boolBOp expr | uOperator expr;
numExpr     : expr numBOp expr;

bOperator   : (numBOp | boolBOp);
boolBOp     : (TkOr | TkAnd | TkLess | TkLeq | TkGeq | TkGreater | TkEqual | TkNEqual);
numBOp      : (TkPlus | TkMinus | TkMult);
uOperator   : (TkNot);

print       : TkPrint expr;
assign      : TkId TkAsig expr;

if          : TkIf (condition instruct)* TkArrow TkFi;
do          : TkDo (condition instruct)* TkArrow TkOd;
for         : TkFor TkId TkIn expr TkTo expr TkArrow instruct TkRof;

condition   : boolExpr | TkGuard boolExpr;

primitive   : TkNum | TkString | TkTrue | TkFalse;
type        : TkInt | TkBool | TkArray;

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

fragment
Letter      : [a-zA-Z];
fragment 
Digit       : [0-9];
TkId        : (Letter | '_') (Letter | Digit | '_')*;

TkNum       : [0-9]+;
TkString    : '"' (~["])+ '"';
TkArray     : 'array[' TkNum '..' TkNum ']';

TkTrue      : 'true';
TkFalse     : 'false';

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

Ignore      : ([ \t\r\n]+ | '//' ~[\r\n]*) -> skip;
