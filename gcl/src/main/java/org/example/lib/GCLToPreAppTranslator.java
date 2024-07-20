package org.example.lib;

import org.example.GCLParser;
import org.example.GCLParser.ProgramContext;
import org.example.GCLParser.ExprContext;

public class GCLToPreAppTranslator {

      public PreApp translateProgram(GCLParser.ProgramContext tree) {
            System.out.println("Translating program");
            return null;
      }

      public PreApp translateExpression(GCLParser.ExprContext tree) {
            System.out.println("Translating expression");
            return null;
      }
}
