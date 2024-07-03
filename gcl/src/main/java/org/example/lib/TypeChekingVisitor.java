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

    if (exprType == null || identType == null)
      return null;

    if (identType.equals(exprType)) {
      return identType;
    }

    if (identType instanceof ArrayType && exprType instanceof ArrayType) {
      int identLenght = ((ArrayType) identType).getLength();
      int exprLength = ((ArrayType) exprType).getLength();
      errorListener.reportError(
          ctx,
          String.format(
              "Type mismatch: Array of length %d can not be assigned to array of length %d",
              exprLength, identLenght));
      return identType;
    }

    errorListener.reportError(
        ctx,
        String.format(
            "Type mismatch: %s can not be assigned to variable \"%s\" of type %s",
            exprType.getType(), ctx.ident().getText(), identType.getType()));
    return identType;
  }

  @Override
  public Type visitWriteArray(GCLParser.WriteArrayContext ctx) {
    Type generalType;
    if (ctx.writeArray() != null) {
      generalType = visit(ctx.writeArray());
    } else {
      generalType = visit(ctx.ident());
    }
    if (!(generalType instanceof ArrayType)) {
      errorListener.reportError(
          ctx, String.format("Variable \"%s\" is not an array", ctx.ident().getText()));
      return generalType;
    }
    ArrayType identType = (ArrayType) generalType;
    visit(ctx.twoPoints());
    int index = Integer.parseInt(ctx.twoPoints().expr(0).getText());
    if (index < identType.getStartIndex() || index > identType.getEndIndex()) {
      errorListener.reportError(
          ctx,
          String.format(
              "Index %d is out of bounds for array with bounds %d to %d",
              index, identType.getStartIndex(), identType.getEndIndex()));
    }
    return identType;
  }

  @Override
  public Type visitReadArray(GCLParser.ReadArrayContext ctx) {
    Type leftType;
    Type elementType;
    if (ctx.writeArray() != null) {
      leftType = visit(ctx.writeArray());
    } else if (ctx.readArray() != null) {
      // leftType = visit(ctx.readArray());
      leftType = new IntType();
    } else {
      leftType = visit(ctx.ident(0));
    }

    if (!(leftType instanceof ArrayType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Can't read a value in element of type %s that is not an array", leftType.getType()));
      return null;
    }

    int index;
    if (ctx.literal() != null) {
      elementType = visit(ctx.literal());
      index = Integer.parseInt(ctx.literal().getText());
    } else {
      elementType = visit(ctx.ident(1));
      index = Integer.parseInt(ctx.ident(1).getText());
    }

    if (!(elementType instanceof IntType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Array index must be an integer, not an element of type %s", elementType.getType()));
      return null;
    }

    if (index < ((ArrayType) leftType).getStartIndex()
        || index > ((ArrayType) leftType).getEndIndex()) {
      errorListener.reportError(
          ctx,
          String.format(
              "Index %d is out of bounds for array with bounds %d to %d",
              index, ((ArrayType) leftType).getStartIndex(), ((ArrayType) leftType).getEndIndex()));
    }

    return new IntType();
  }

  @Override
  public Type visitTwoPoints(GCLParser.TwoPointsContext ctx) {
    Type expr1 = visit(ctx.expr(0));
    Type expr2 = visit(ctx.expr(1));

    if (!(expr1 instanceof IntType)) {
      errorListener.reportError(
          ctx, String.format("Index \"%s\" should be an int", ctx.expr(0).getText()));
      return null;
    }
    if (!(expr2 instanceof IntType)) {
      errorListener.reportError(
          ctx, String.format("Index \"%s\" should be an int", ctx.expr(1).getText()));
      return null;
    }

    return expr1;
  }

  @Override
  public Type visitComma(GCLParser.CommaContext ctx) {
    if (ctx.comma() != null) {
      ArrayType leftSize = (ArrayType) visit(ctx.comma());
      if (leftSize == null)
        return null;

      Type expr1 = visit(ctx.expr(0));
      if (!(expr1 instanceof IntType)) {
        errorListener.reportError(
            ctx, String.format("Index \"%s\" should be an int", ctx.expr(0).getText()));
        return null;
      }
      return new ArrayType(1 + leftSize.getLength());
    } else {
      Type expr1 = visit(ctx.expr(0));
      Type expr2 = visit(ctx.expr(1));
      if (!(expr1 instanceof IntType)) {
        errorListener.reportError(
            ctx, String.format("Index \"%s\" should be an int", ctx.expr(0).getText()));
        return null;
      }
      if (!(expr2 instanceof IntType)) {
        errorListener.reportError(
            ctx, String.format("Index \"%s\" should be an int", ctx.expr(1).getText()));
        return null;
      }

      return new ArrayType(2);
    }
  }

  @Override
  public Type visitConcat(GCLParser.ConcatContext ctx) {
    Type leftType;
    Type rightType;
    if (ctx.concat() != null) {
      leftType = visit(ctx.concat());
      if (ctx.string(0) != null) {
        rightType = visit(ctx.string(0));
      } else {
        rightType = visit(ctx.value(0));
      }
    } else if (ctx.string(0) != null) {
      leftType = visit(ctx.string(0));
      if (ctx.string(1) != null) {
        rightType = visit(ctx.string(1));
      } else {
        rightType = visit(ctx.value(0));
      }
    } else {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else {
        rightType = visit(ctx.string(0));
      }
    }

    if (!(leftType instanceof IntType || leftType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not string or can't be casted to string",
              leftType.getType()));
      return null;
    }

    if (!(rightType instanceof IntType || rightType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not string or can't be casted to string",
              rightType.getType()));
      return null;
    }

    return new StringType();
  }

  @Override
  public Type visitAnd(GCLParser.AndContext ctx) {
    Type leftType;
    Type rightType;

    if (ctx.and() != null) {
      leftType = visit(ctx.and());
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else if (ctx.boolExpr(0) != null) {
        rightType = visit(ctx.boolExpr(0));
      } else {
        rightType = visit(ctx.parOr());
      }
    } else if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else if (ctx.boolExpr(0) != null) {
        rightType = visit(ctx.boolExpr(0));
      } else if (ctx.and() != null) {
        rightType = visit(ctx.and());
      } else {
        rightType = visit(ctx.parOr());
      }
    } else {
      leftType = visit(ctx.boolExpr(0));
      if (ctx.boolExpr(1) != null) {
        rightType = visit(ctx.boolExpr(1));
      } else if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else if (ctx.and() != null) {
        rightType = visit(ctx.and());
      } else {
        rightType = visit(ctx.parOr());
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof BoolType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not boolean or can't be casted to boolean",
              leftType.getType()));
      return null;
    }

    if (!(rightType instanceof BoolType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not boolean or can't be casted to boolean",
              rightType.getType()));
      return null;
    }

    return new BoolType();
  }

  @Override
  public Type visitBoolExpr(GCLParser.BoolExprContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Type visitBoolOp(GCLParser.BoolOpContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Type visitNot(GCLParser.NotContext ctx) {
    Type value;
    if (ctx.not() != null) {
      value = visit(ctx.not());
    } else if (ctx.value() != null) {
      value = visit(ctx.value());
    } else if (ctx.numExpr() != null){
      value = visit(ctx.numExpr());
    } else {
      value = visit(ctx.equal());
    }

    if (value instanceof BoolType) {
      return value;
    } else {
      errorListener.reportError(ctx, "Operator \"not\" can only be applied to booleans");
      return null;
    }
  }

  @Override
  public Type visitOr(GCLParser.OrContext ctx) {
    Type leftType;
    Type rightType;

    if (ctx.or() != null) {
      leftType = visit(ctx.or());
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else if (ctx.boolExpr() != null) {
        rightType = visit(ctx.boolExpr());
      } else {
        rightType = visit(ctx.and());
      }
    } else if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else if (ctx.boolExpr() != null) {
        rightType = visit(ctx.boolExpr());
      } else if (ctx.or() != null) {
        rightType = visit(ctx.or());
      } else {
        rightType = visit(ctx.and());
      }
    } else {
      leftType = visit(ctx.boolExpr());
      if (ctx.boolExpr() != null) {
        rightType = visit(ctx.boolExpr());
      } else if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else if (ctx.or() != null) {
        rightType = visit(ctx.or());
      } else {
        rightType = visit(ctx.and());
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof BoolType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not boolean or can't be casted to boolean",
              leftType.getType()));
      return null;
    }

    if (!(rightType instanceof BoolType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not boolean or can't be casted to boolean",
              rightType.getType()));
      return null;
    }

    return new BoolType();
  }

  @Override
  public Type visitLeq(GCLParser.LeqContext ctx) {
    Type leftType;
    Type rightType;
    if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else {
        rightType = visit(ctx.numExpr(0));
      }
    } else {
      leftType = visit(ctx.numExpr(0));
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else {
        rightType = visit(ctx.numExpr(1));
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof IntType || leftType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not int or can't be casted to int", leftType.getType()));
      return null;
    }

    if (!(rightType instanceof IntType || rightType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not int or can't be casted to int", rightType.getType()));
      return null;
    }

    return new BoolType();
  }

  @Override
  public Type visitLess(GCLParser.LessContext ctx) {
    Type leftType;
    Type rightType;
    if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else {
        rightType = visit(ctx.numExpr(0));
      }
    } else {
      leftType = visit(ctx.numExpr(0));
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else {
        rightType = visit(ctx.numExpr(1));
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof IntType || leftType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not int or can't be casted to int", leftType.getType()));
      return null;
    }

    if (!(rightType instanceof IntType || rightType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not int or can't be casted to int", rightType.getType()));
      return null;
    }

    return new BoolType();
  }

  @Override
  public Type visitNumExpr(GCLParser.NumExprContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Type visitGeq(GCLParser.GeqContext ctx) {
    Type leftType;
    Type rightType;
    if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else {
        rightType = visit(ctx.numExpr(0));
      }
    } else {
      leftType = visit(ctx.numExpr(0));
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else {
        rightType = visit(ctx.numExpr(1));
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof IntType || leftType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not int or can't be casted to int", leftType.getType()));
      return null;
    }

    if (!(rightType instanceof IntType || rightType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not int or can't be casted to int", rightType.getType()));
      return null;
    }

    return new BoolType();
  }

  @Override
  public Type visitGreater(GCLParser.GreaterContext ctx) {
    Type leftType;
    Type rightType;
    if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else {
        rightType = visit(ctx.numExpr(0));
      }
    } else {
      leftType = visit(ctx.numExpr(0));
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else {
        rightType = visit(ctx.numExpr(1));
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof IntType || leftType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not int or can't be casted to int", leftType.getType()));
      return null;
    }

    if (!(rightType instanceof IntType || rightType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not int or can't be casted to int", rightType.getType()));
      return null;
    }

    return new BoolType();
  }

  @Override
  public Type visitEqual(GCLParser.EqualContext ctx) {
    Type leftType;
    Type rightType;
    if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else {
        rightType = visit(ctx.numExpr(0));
      }
    } else {
      leftType = visit(ctx.numExpr(0));
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else {
        rightType = visit(ctx.numExpr(1));
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof IntType || leftType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not int or can't be casted to int", leftType.getType()));
      return null;
    }

    if (!(rightType instanceof IntType || rightType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not int or can't be casted to int", rightType.getType()));
      return null;
    }

    return new BoolType();
  }

  @Override
  public Type visitPlus(GCLParser.PlusContext ctx) {
    Type leftType;
    Type rightType;
    if (ctx.plus() != null) {
      leftType = visit(ctx.plus());
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else if (ctx.mult(0) != null) {
        rightType = visit(ctx.mult(0));
      } else {
        rightType = visit(ctx.parNumExpr());
      }
    } else if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else if (ctx.mult(0) != null) {
        rightType = visit(ctx.mult(0));
      } else if (ctx.plus() != null) {
        rightType = visit(ctx.plus());
      } else {
        rightType = visit(ctx.parNumExpr());
      }
    } else {
      leftType = visit(ctx.mult(0));
      if (ctx.mult(1) != null) {
        rightType = visit(ctx.mult(1));
      } else if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else if (ctx.plus() != null) {
        rightType = visit(ctx.plus());
      } else {
        rightType = visit(ctx.parNumExpr());
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof IntType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not an integer or can't be casted to integer",
              leftType.getType()));
      return null;
    }

    if (!(rightType instanceof IntType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not an integer or can't be casted to integer",
              rightType.getType()));
      return null;
    }

    return new IntType();
  }

  @Override
  public Type visitMinus(GCLParser.MinusContext ctx) {
    Type leftType;
    Type rightType;
    if (ctx.minus() != null) {
      leftType = visit(ctx.minus());
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else if (ctx.mult(0) != null) {
        rightType = visit(ctx.mult(0));
      } else if (ctx.plus(0) != null) {
        rightType = visit(ctx.plus(0));
      } else {
        rightType = visit(ctx.parNumExpr());
      }
    } else if (ctx.plus(0) != null) {
      leftType = visit(ctx.plus(0));
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else if (ctx.mult(0) != null) {
        rightType = visit(ctx.mult(0));
      } else if (ctx.plus(1) != null) {
        rightType = visit(ctx.plus(1));
      } else if (ctx.minus() != null) {
        rightType = visit(ctx.minus());
      } else {
        rightType = visit(ctx.parNumExpr());
      }
    } else if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else if (ctx.mult(0) != null) {
        rightType = visit(ctx.mult(0));
      } else if (ctx.minus() != null) {
        rightType = visit(ctx.minus());
      } else if (ctx.plus(0) != null) {
        rightType = visit(ctx.plus(0));
      } else {
        rightType = visit(ctx.parNumExpr());
      }
    } else {
      leftType = visit(ctx.mult(0));
      if (ctx.mult(1) != null) {
        rightType = visit(ctx.mult(1));
      } else if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else if (ctx.minus() != null) {
        rightType = visit(ctx.minus());
      } else if (ctx.plus(0) != null) {
        rightType = visit(ctx.plus(0));
      } else {
        rightType = visit(ctx.parNumExpr());
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof IntType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not an integer or can't be casted to integer",
              leftType.getType()));
      return null;
    }

    if (!(rightType instanceof IntType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not an integer or can't be casted to integer",
              rightType.getType()));
      return null;
    }

    return new IntType();
  }

  @Override
  public Type visitMult(GCLParser.MultContext ctx) {
    Type leftType;
    Type rightType;
    if (ctx.mult() != null) {
      leftType = visit(ctx.mult());
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else {
        rightType = visit(ctx.parNumExpr(0));
      }
    } else if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else {
        rightType = visit(ctx.parNumExpr(0));
      }
    } else {
      leftType = visit(ctx.parNumExpr(0));
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else {
        rightType = visit(ctx.parNumExpr(1));
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof IntType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not an integer or can't be casted to integer",
              leftType.getType()));
      return null;
    }

    if (!(rightType instanceof IntType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not an integer or can't be casted to integer",
              rightType.getType()));
      return null;
    }

    return new IntType();
  }

  @Override
  public Type visitParNumExpr(GCLParser.ParNumExprContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Type visitUMinus(GCLParser.UMinusContext ctx) {
    Type value = visit(ctx.value());
    if (value instanceof IntType) {
      return value;
    } else {
      errorListener.reportError(ctx, "Unary minus can only be applied to integers");
      return null;
    }
  }

  @Override
  public Type visitNotEqual(GCLParser.NotEqualContext ctx) {
    Type leftType;
    Type rightType;
    if (ctx.value(0) != null) {
      leftType = visit(ctx.value(0));
      if (ctx.value(1) != null) {
        rightType = visit(ctx.value(1));
      } else {
        rightType = visit(ctx.numExpr(0));
      }
    } else {
      leftType = visit(ctx.numExpr(0));
      if (ctx.value(0) != null) {
        rightType = visit(ctx.value(0));
      } else {
        rightType = visit(ctx.numExpr(1));
      }
    }

    if (leftType == null || rightType == null)
      return null;

    if (!(leftType instanceof IntType || leftType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not string or can't be casted to string",
              leftType.getType()));
      return null;
    }

    if (!(rightType instanceof IntType || rightType instanceof StringType)) {
      errorListener.reportError(
          ctx,
          String.format(
              "Expression of type %s is not string or can't be casted to string",
              rightType.getType()));
      return null;
    }

    return new IntType();
  }

  @Override
  public Type visitExpr(GCLParser.ExprContext ctx) {
    return visitChildren(ctx);
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
  public Type visitValue(GCLParser.ValueContext ctx) {
    return visitChildren(ctx);
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
