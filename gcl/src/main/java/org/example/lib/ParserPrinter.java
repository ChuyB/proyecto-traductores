package org.example.lib;

import java.util.List;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.Tree;

/**
 * La clase proporciona un método para generar una representación en cadena de un árbol sintáctico
 * abstracto (AST) creado por un parser de ANTLR.
 */
public class ParserPrinter {
  /** Constante que representa el salto de línea del sistema operativo. */
  public static final String Eol = System.lineSeparator();

  /** Constante que representa la cadena utilizada para la sangría en la salida. */
  public static final String Indents = "-";

  private static int level;

  /**
   * Genera una representación en cadena del árbol sintáctico abstracto (AST) proporcionado.
   *
   * @param t El árbol sintáctico abstracto que se desea convertir en cadena.
   * @param ruleNames Una lista que contiene los nombres de las reglas utilizadas por el analizador
   *     ANTLR. El orden de los nombres de las reglas debe corresponder a los índices de las reglas
   *     en el analizador.
   * @return La representación en cadena del árbol AST, con sangría para mejorar la legibilidad.
   */
  public static String toStringTree(final Tree t, final List<String> ruleNames) {
    level = 0;
    return process(t, ruleNames);
  }

  /*
   * Método recursivo que recorre el árbol AST y construye la representación en
   * cadena.
   */
  private static String process(final Tree t, final List<String> ruleNames) {
    StringBuilder sb = new StringBuilder();

    if (t instanceof RuleContext) {
      RuleContext rctx = (RuleContext) t;
      String ruleName = ruleNames.get(rctx.getRuleIndex());

      if (ruleIsPrintable(ruleName)) {
        level++;
        sb.append(indent(level));
        sb.append(ruleName.substring(0, 1).toUpperCase() + ruleName.substring(1));
        if (ruleShouldPrintValue(ruleName)) {
          sb.append(": " + rctx.getText());
        }
      }

      for (int i = 0; i < t.getChildCount(); i++) {
        sb.append(process(t.getChild(i), ruleNames));
      }

      if (ruleIsPrintable(ruleName)) {
        level--;
      }
    }

    return sb.toString();
  }

  /*
   * Genera la identación basado en el nivel actual de anidamiento.
   */
  private static String indent(int level) {
    StringBuilder sb = new StringBuilder();
    if (level > 0) {
      sb.append(Eol);
      for (int cnt = 0; cnt < level - 1; cnt++) {
        sb.append(Indents);
      }
    }
    return sb.toString();
  }

  private static Boolean ruleIsPrintable(String ruleName) {
    return !(ruleName.equals("program") || ruleName.equals("expr") || ruleName.equals("instruct"));
  }

  private static Boolean ruleShouldPrintValue(String ruleName) {
    return ruleName.equals("ident") || ruleName.equals("literal") || ruleName.equals("string");
  }
}
