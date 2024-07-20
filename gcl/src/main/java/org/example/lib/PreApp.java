package org.example.lib;

public abstract class PreApp {
      public abstract String repr();

      @Override
      public String toString() {
            return this.repr();
      }

      // Clase para Variables
      public static class Variable extends PreApp {
            private String name;

            public Variable(String name) {
                  this.name = name;
            }

            @Override
            public String repr() {
                  return name;
            }
      }

      // Clase para Constantes
      public static class Constant extends PreApp {
            private String value;

            public Constant(String value) {
                  this.value = value;
            }

            @Override
            public String repr() {
                  return value;
            }
      }

      // Clase para Aplicación de Funciones
      public static class FunctionApplication extends PreApp {
            private PreApp func;
            private PreApp[] args;

            public FunctionApplication(PreApp func, PreApp... args) {
                  this.func = func;
                  this.args = args;
            }

            @Override
            public String repr() {
                  StringBuilder argsRepr = new StringBuilder();
                  for (PreApp arg : args) {
                        argsRepr.append(" ").append(arg.repr());
                  }
                  return "(" + func.repr() + argsRepr.toString() + ")";
            }
      }

      // Clase para Expresiones Lambda
      public static class LambdaExpression extends PreApp {
            private Variable var;
            private PreApp body;

            public LambdaExpression(Variable var, PreApp body) {
                  this.var = var;
                  this.body = body;
            }

            @Override
            public String repr() {
                  return "(λ " + var.repr() + ". " + body.repr() + ")";
            }
      }
}

