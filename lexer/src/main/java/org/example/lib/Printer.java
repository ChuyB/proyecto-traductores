package org.example.lib;

import org.antlr.v4.runtime.Token;
import org.example.GCLLexer;

/**
 * Clase que imprime un token.
 */
public class Printer {

  /**
   * Imprime un token.
   * @param token Token a imprimir.
   */
  public static void imprimirTokens(Token token) {
    int line = token.getLine();
    int column = token.getCharPositionInLine() + 1;
    String tokenName = GCLLexer.VOCABULARY.getSymbolicName(token.getType());
    String tokenContent = token.getText();

    // Si el token es EOF, no se imprime
    if (tokenName.equals("EOF")) return;

    if (tokenName.equals("TkId") || tokenName.equals("TkString")) {
      tokenContent = String.format("(\"%s\")", tokenContent);
    } else if (tokenName.equals("TkNum")) {
      tokenContent = String.format("(%s)", tokenContent);
    } else {
      tokenContent = "";
    }

    System.out.printf("%s%s %d %d\n", tokenName, tokenContent, line, column);
  }
}
