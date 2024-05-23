package org.example;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.example.lib.ManejadorArchivo;
import org.example.lib.ManejadorErrores;
import org.example.lib.Printer;

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
    lexer.addErrorListener(ManejadorErrores.INSTANCE);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    tokens.fill();

    // Imprime los tokens
    for (Token token : tokens.getTokens()) {
      Printer.imprimirTokens(token);
    }
  }
}
