package org.example.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/** Clase que maneja los errores de sintaxis en el archivo de entrada. */
public class ManejadorErrorLexer extends BaseErrorListener {
  private boolean hayErrores;

  /** Constructor de la clase. */
  public ManejadorErrorLexer() {
    super();
    hayErrores = false;
  }

  /**
   * Extrae el token de la cadena de error.
   *
   * @param e Cadena de error.
   * @return Token extraído.
   */
  private String extractorToken(String e) {
    Pattern pattern = Pattern.compile("'([^']*)'");
    Matcher matcher = pattern.matcher(e);
    String token = null;

    if (matcher.find()) {
      token = matcher.group(1);
    }

    return token;
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
    System.out.printf(
        "Error: Unexpected character \"%s\" in row %d, column %d\n",
        extractorToken(e.toString()), line, charPositionInLine);
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
