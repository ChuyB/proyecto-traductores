package org.example.lib;

import java.util.List;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.Tree;
import org.example.GCLParser;
import org.example.lib.types.ArrayType;
import org.example.lib.types.Type;

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

  private static int blockNumber;

  /**
   * Genera una representación en cadena del árbol sintáctico abstracto (AST) proporcionado.
   *
   * @param t El árbol sintáctico abstracto que se desea convertir en cadena.
   * @param ruleNames Una lista que contiene los nombres de las reglas utilizadas por el analizador
   *     ANTLR. El orden de los nombres de las reglas debe corresponder a los índices de las reglas
   *     en el analizador.
   * @return La representación en cadena del árbol AST, con sangría para mejorar la legibilidad.
   */
  public static String toStringTree(
      final Tree t, final List<String> ruleNames, TypeChekingVisitor visitor) {
    level = 0;
    blockNumber = -1;
    return process(t, ruleNames, visitor);
  }

  /*
   * Método recursivo que recorre el árbol AST y construye la representación en
   * cadena.
   */
  private static String process(
      final Tree t, final List<String> ruleNames, TypeChekingVisitor visitor) {
    StringBuilder sb = new StringBuilder();

    if (t instanceof RuleContext) {
      RuleContext rctx = (RuleContext) t;
      String ruleName = ruleNames.get(rctx.getRuleIndex());

      if (ruleIsPrintable(ruleName)) {
        level++;
        if (ruleName.equals("block")) {
          blockNumber++;
        }

        sb.append(indent(level));
        if (!ruleName.equals("declareBody")) {
          if (ruleName.equals("skip")) {
            sb.append("skip");
          } else if (ruleName.equals("uMinus")) {
            sb.append("Minus");
          } else {
            sb.append(ruleName.substring(0, 1).toUpperCase() + ruleName.substring(1));
          }
        }

        if (ruleName.equals("block")) {
          sb.append(visitor.toStringBlockSymbols(blockNumber, level, Indents));
        }

        if (ruleShouldPrintValue(ruleName)) {
          String type;
          if (ruleName.equals("ident")) {
            GCLParser.IdentContext ident = (GCLParser.IdentContext) t;
            type = visitor.getIdentType(ident.getText(), blockNumber);
          } else if (ruleName.equals("literal")) {
            GCLParser.LiteralContext literal = (GCLParser.LiteralContext) t;
            Type rawType = visitor.visitLiteral(literal);
            type = rawType.getType();
          } else {
            type = "string";
          }

          sb.append(": " + rctx.getText() + " | type: " + type);
        }

        if (ruleName.equals("comma")) {
          visitor.addTablesToStack(blockNumber);
          ArrayType type = (ArrayType) visitor.visitComma((GCLParser.CommaContext) t);
          sb.append(" | type: " + type.getTypeVerbose());
        }

        if (ruleName.equals("equal")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitEqual((GCLParser.EqualContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("notEqual")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitNotEqual((GCLParser.NotEqualContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("less")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitLess((GCLParser.LessContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("leq")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitLeq((GCLParser.LeqContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("greater")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitGreater((GCLParser.GreaterContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("geq")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitGeq((GCLParser.GeqContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("and")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitAnd((GCLParser.AndContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("or")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitOr((GCLParser.OrContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("not")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitNot((GCLParser.NotContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("plus")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitPlus((GCLParser.PlusContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("mult")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitMult((GCLParser.MultContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("minus")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitMinus((GCLParser.MinusContext) t);
          sb.append(" | type: " + type.getType());
        }

        if (ruleName.equals("readArray")) {
          visitor.addTablesToStack(blockNumber);
          Type type = visitor.visitReadArray((GCLParser.ReadArrayContext) t);
          sb.append(" | type: " + type.getType());
        }
      }

      visitor.clearStack();

      for (int i = 0; i < t.getChildCount(); i++) {
        if (!ruleName.equals("declare")) {
          sb.append(process(t.getChild(i), ruleNames, visitor));
        }
      }

      if (ruleName.equals("block")) {
        blockNumber--;
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
    return !(ruleName.equals("program")
        || ruleName.equals("expr")
        || ruleName.equals("instruct")
        || ruleName.equals("type")
        || ruleName.equals("array")
        || ruleName.equals("boolExpr")
        || ruleName.equals("boolOp")
        || ruleName.equals("numExpr")
        || ruleName.equals("value")
        || ruleName.equals("parAnd")
        || ruleName.equals("parOr")
        || ruleName.equals("parNumExpr")
        || ruleName.equals("declare")
        || ruleName.equals("declareBody"));
  }

  private static Boolean ruleShouldPrintValue(String ruleName) {
    return ruleName.equals("ident") || ruleName.equals("literal") || ruleName.equals("string");
  }

  public static String formatDeclare(String text) {
    StringBuilder sb = new StringBuilder();
    boolean isNegative = false;
    char[] chars = text.toCharArray();
    for (char c : chars) {
      if (Character.isDigit(c)) {
        if (isNegative) {
          sb.append("Literal: -" + c);
          isNegative = false;
        } else {
          sb.append("Literal: " + c);
        }
        continue;
      }
      switch (c) {
        case ',':
          sb.append(c + " ");
          break;
        case ':':
          sb.append(" " + c + " ");
          break;
        case '-':
          isNegative = true;
          break;
        default:
          sb.append(c);
          break;
      }
    }
    return sb.toString();
  }
}
