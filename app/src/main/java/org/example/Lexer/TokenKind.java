package src;

public class TokenKind {

      // Tokens para denotar separadores
      public static final int TkOBlock = 1;
      public static final int TkCBlock = 2;
      public static final int TkSoForth = 3;
      public static final int TkOpenPar = 4;
      public static final int TkClosePar = 5;
      public static final int TkAsig = 6;
      public static final int TkSemicolon = 7;
      public static final int TkArrow = 8;
      public static final int TkGuard = 9;
      
      // tokens para denotar operadores aritmeticos, booleanos y relacionales o de manipulacion de arreglos y cadena de caracteres
      public static final int TkPlus = 10;
      public static final int TkMinus = 11;
      public static final int TkMult = 12;
      public static final int TkOr = 13;
      public static final int TkAnd = 14;
      public static final int TkNot = 15;
      public static final int TkLeq = 16;
      public static final int TkGeq = 17;
      public static final int TkGreater = 18;
      public static final int TkEqual = 19;
      public static final int TkNEqual = 20;
      public static final int TkOBracket = 21;
      public static final int TkCBracket = 22;
      public static final int TkConcat = 23;

      // Tokens para denotar palabras reservadas
      public static final int TkIf = 24;
      public static final int TkDo = 25;
      public static final int TkOd = 27;
      public static final int TkFor = 27;
      public static final int TkId = 28;
      public static final int TkNum = 29;
      public static final int TkString = 30;
      public static final int TkTrue = 31;
      public static final int TkFalse = 32;
}
