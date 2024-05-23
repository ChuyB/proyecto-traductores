package src;

public class Token {
      private TokenKind kind;
      private int row;
      private int column;
      

      public Token(int tkdeclare, String token, int fila, int columna, int i) {
            
      }

      public TokenKind getKind() {
            return kind;
      }

      public int getRow() {
            return row;
      }
      
      public int getColumn() {
            return column;
      }  
}
