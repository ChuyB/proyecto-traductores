package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Lexer {
  public static void main(String[] args) {
    try {
      BufferedReader br = new BufferedReader(new FileReader("entrada.gcl"));
      String line;
      Lexico lexico = new Lexico();
      while ((line = br.readLine()) != null) {
        continue;
      }
      br.close();
    } catch (IOException e) {
      System.out.println("Error al leer el archivo");
    }
  }
}
