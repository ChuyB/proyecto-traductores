package org.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class App {

  public static void main(String[] args) {
    String input = "|[declare a, b, _ : int; d, e, f : array[0..2]|";
    CharStream charStreams = CharStreams.fromString(input);

    GCLLexer lexer = new GCLLexer(charStreams);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    tokens.fill();

    for (Token token : tokens.getTokens()) {
      int line = token.getLine();
      int column = token.getCharPositionInLine();
      String tokenName = GCLLexer.VOCABULARY.getSymbolicName(token.getType());
      String tokenContent = token.getText();
      System.out.printf("%s(\"%s\") %d %d\n", tokenName, tokenContent, line, column);
    }
  }
}
