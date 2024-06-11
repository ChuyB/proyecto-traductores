package org.example;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.example.lib.ManejadorArchivo;
import org.example.lib.ManejadorErrores;
import org.example.lib.ParserPrinter;

public class App {

  public static void main(String[] args) {

    // Verifica que se introdujo la dirección del archivo
    if (args.length != 1) {
      System.out.println("Uso: lexer <archivo>");
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
    ManejadorErrores manejadorErrores = new ManejadorErrores();
    lexer.addErrorListener(manejadorErrores);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    // Si hay errores, no contniúa con el análisis
    if (manejadorErrores.hayErrores()) return;

    GCLParser parser = new GCLParser(tokens);
    parser.setBuildParseTree(true);
    List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
    RuleContext tree = parser.program();
    String prettyTree = ParserPrinter.toPrettyTree(tree, ruleNamesList);
    System.out.println(prettyTree);
  }

}
