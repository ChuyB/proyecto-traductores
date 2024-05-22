package src;

import java.util.ArrayList;
import src.TokenKind;
import src.Token;

public class Lexico {
      private ArrayList<Token> tokens;
      private int fila;
      private int columna;

      public Lexico() {
            this.tokens = new ArrayList<Token>();
            this.fila = 0;
            this.columna = 0;
      }

      public void analizar(String line) {

      }
}