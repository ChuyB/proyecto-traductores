package org.example.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Clase que maneja los errores de sintaxis en el archivo de entrada.
 */
public class ManejadorErrores extends BaseErrorListener {
  /**
   * Extrae el token de la cadena de error.
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

  public static final ManejadorErrores INSTANCE = new ManejadorErrores();

  /**
   * Manejador que imprime el error de sintaxis.
   */ 
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
  }
}
