package org.example.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.example.GCLBaseVisitor;
import org.example.GCLParser;
import org.example.lib.types.ArrayType;
import org.example.lib.types.BoolType;
import org.example.lib.types.IntType;
import org.example.lib.types.StringType;
import org.example.lib.types.Type;

/** GCLVisitor */
public class TypeChekingVisitor extends GCLBaseVisitor<Type> {
  private List<SymbolTable> historicSymbolTables;
  private Stack<SymbolTable> symbolTables;
  private TypeErrorListener errorListener;

  public TypeChekingVisitor() {
    this.symbolTables = new Stack<>();
    this.historicSymbolTables = new ArrayList<>();
    this.errorListener = new TypeErrorListener();
    symbolTables.push(new SymbolTable());
  }

  public void addErrorListener(TypeErrorListener listener) {
    this.errorListener = listener;
  }

  @Override
  public Type visitBlock(GCLParser.BlockContext ctx) {
    symbolTables.push(new SymbolTable());
    historicSymbolTables.add(symbolTables.peek());
    visitChildren(ctx);
    symbolTables.pop();

    return null;
  }

  @Override
  public Type visitDeclareBody(GCLParser.DeclareBodyContext ctx) {
    SymbolTable symbolTable = symbolTables.peek();
    Type type = visit(ctx.type());
    for (int i = 0; i < ctx.TkId().size(); i++) {
      String id = ctx.TkId(i).getText();

      if (symbolTable.contains(id)) {
        errorListener.reportError(ctx, String.format("Variable \"%s\" is already declared", id));
      } else {
        symbolTable.put(id, type);
      }
    }

    return type;
  }

  @Override
  public Type visitAsig(GCLParser.AsigContext ctx) {
    Type identType = visit(ctx.ident());
    Type exprType;

    if (ctx.expr() != null) {
      exprType = visit(ctx.expr());
    } else if (ctx.comma() != null) {
      exprType = visit(ctx.comma());
    } else if (ctx.writeArray() != null) {
      exprType = visit(ctx.writeArray());
    } else {
      exprType = visit(ctx.readArray());
    }

    if (identType.equals(exprType)) {
      return identType;
    }

    errorListener.reportError(
        ctx,
        String.format(
            "Type mismatch: %s can not be assigned to variable \"%s\" of type %s",
            exprType, ctx.ident().getText(), identType.getType()));
    return null;
  }

  @Override
  public Type visitComma(GCLParser.CommaContext ctx) {
    int size = ctx.expr().size();
    return new ArrayType(size);
  }

  @Override
  public Type visitType(GCLParser.TypeContext ctx) {
    if (ctx.TkInt() != null) {
      return new IntType();
    } else if (ctx.TkBool() != null) {
      return new BoolType();
    } else if (ctx.array() != null) {
      int startIndex = Integer.parseInt(ctx.array().TkNum(0).getText());
      int endIndex = Integer.parseInt(ctx.array().TkNum(1).getText());
      if (ctx.array().TkMinus().size() > 0) {
        startIndex *= -1;
      } else if (ctx.array().TkMinus().size() > 1) {
        endIndex *= -1;
      }
      return new ArrayType(startIndex, endIndex);
    } else {
      return new StringType();
    }
  }

  @Override
  public Type visitLiteral(GCLParser.LiteralContext ctx) {
    if (ctx.TkNum() != null) {
      return new IntType();
    }

    return new BoolType();
  }

  @Override
  public Type visitString(GCLParser.StringContext ctx) {
    return new StringType();
  }

  @Override
  public Type visitIdent(GCLParser.IdentContext ctx) {
    String id = ctx.getText();
    for (int i = symbolTables.size() - 1; i >= 0; i--) {
      SymbolTable symbolTable = symbolTables.get(i);
      if (symbolTable.contains(id)) {
        return symbolTable.get(id);
      }
    }

    errorListener.reportError(ctx, String.format("Variable \"%s\" is not declared", ctx.getText()));
    return null;
  }

  public String toStringBlockSymbols(int blockNumber, int level, String indents) {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    for (int cnt = 0; cnt < level; cnt++) {
      sb.append(indents);
    }
    sb.append("Symbols Table\n");
    Set<Map.Entry<String, Type>> entry = historicSymbolTables.get(blockNumber).entrySet();
    entry.forEach(
        (e) -> {
          for (int cnt = 0; cnt < level + 1; cnt++) {
            sb.append(indents);
          }
          sb.append("Ident: " + e.getKey() + " | type: " + e.getValue().getType() + "\n");
        });

    return sb.toString().substring(0, sb.length() - 1);
  }
}
