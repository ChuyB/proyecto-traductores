package org.example;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.example.lib.ManejadorArchivo;
import org.example.lib.ManejadorErrorLexer;
import org.example.lib.ManejadorErrorParser;
import org.example.lib.ParserPrinter;

public class App {

  public static void main(String[] args) {

    // Verifica que se introdujo la dirección del archivo
    if (args.length != 1) {
      System.out.println("Uso: gcl <archivo>");
      System.exit(1);
    }

    // Se lee el contenido del archivo
    String dirArchivo = args[0];
    ManejadorArchivo manejador = new ManejadorArchivo(dirArchivo);
    manejador.procesarArchivo();
    String contenidosArchivo = manejador.getContenido();

    // Análisis léxico del archivo
    CharStream charStreams = CharStreams.fromString(contenidosArchivo);
    GCLLexer lexer = new GCLLexer(charStreams);
    lexer.removeErrorListeners();
    ManejadorErrorLexer manejadorErrorLexer = new ManejadorErrorLexer();
    lexer.addErrorListener(manejadorErrorLexer);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    // Si hay errores, no continúa con el análisis
    if (manejadorErrorLexer.hayErrores()) return;

    // Parser del archivo
    GCLParser parser = new GCLParser(tokens);

    // Añade el manejador de errores al parser
    parser.removeErrorListeners();
    ManejadorErrorParser manejadorErroresParser = new ManejadorErrorParser();
    parser.addErrorListener(manejadorErroresParser);

    parser.setBuildParseTree(true);
    List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
    RuleContext tree = parser.program();

    // Si hay errores, no continúa con el análisis
    if (manejadorErroresParser.hayErrores()) return;

    // Imprime el árbol de análisis
    String stringTree = ParserPrinter.toStringTree(tree, ruleNamesList);
    System.out.println(stringTree.substring(2));
  }

}
