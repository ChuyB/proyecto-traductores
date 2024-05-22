grammar GCL;

program : TkString;

TkIf        : 'if';
TkDo        : 'do';
TkOd        : 'od';
TkFor       : 'for';
TkDeclare   : 'declare';
TkInt       : 'int';
TkBool      : 'bool';

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

Ignore      : [ ,\n, \t, \r,'//'] -> skip;
