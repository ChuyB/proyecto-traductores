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
    int row = context.getStart().getLine();
    int column = context.getStart().getCharPositionInLine() + 1;
    sb.append(String.format("Error in row %d, column %d: %s\n", row, column, message));
    errorCount++;
  }

  public boolean hayErrores() {
    return errorCount > 0;
  }

  public void printErrores() {
    System.out.println(sb.toString());
  }
}
