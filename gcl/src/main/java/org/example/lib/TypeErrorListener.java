package org.example.lib;

import org.antlr.v4.runtime.ParserRuleContext;

/** TypeErrorListener */
public class TypeErrorListener {
  private StringBuilder sb;
  private int errorCount;

  public TypeErrorListener() {
    sb = new StringBuilder();
    errorCount = 0;
  }

  public void reportError(ParserRuleContext context, String message) {
    if (errorCount == 0) {
      int row = context.getStart().getLine();
      int column = context.getStart().getCharPositionInLine() + 1;
      sb.append(String.format("%s at line %s and column %s\n", message, row, column));
      errorCount++;
    }
  }

  public boolean hayErrores() {
    return errorCount > 0;
  }

  public void printErrores() {
    System.out.print(sb.toString());
  }
}
