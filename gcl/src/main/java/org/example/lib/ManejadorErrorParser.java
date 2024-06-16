package org.example.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

/** Clase que maneja los errores de sintaxis en el archivo de entrada. */
public class ManejadorErrorParser extends BaseErrorListener {
  private boolean hayErrores;

  /** Constructor de la clase. */
  public ManejadorErrorParser() {
    super();
    hayErrores = false;
  }

  /** Manejador que imprime el error de sintaxis. */
  @Override
  public void syntaxError(
      Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line,
      int charPositionInLine,
      String msg,
      RecognitionException e) {
    Token token = (Token) offendingSymbol;
    System.out.printf(
      "Syntax error in row %d, column %d: unexpected token \'%s\'",
      line, charPositionInLine, token.getText()
    );
    this.hayErrores = true;
  }

  /**
   * Verifica si hay errores en el archivo.
   *
   * @return True si hay errores, false en caso contrario.
   */
  public boolean hayErrores() {
    return hayErrores;
  }
}
