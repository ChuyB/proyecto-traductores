package org.example.lib.types;

/** ArrayType */
public class ArrayType implements Type {
  private int length;
  private int startIndex;
  private int endIndex;

  public ArrayType(int startIndex, int endIndex) {
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    length = endIndex - startIndex + 1;
  }

  public ArrayType(int length) {
    this.length = length;
    startIndex = 0;
    endIndex = length - 1;
  }

  public boolean equals(Type type) {
    return type instanceof ArrayType && ((ArrayType) type).length == length;
  }

  public String getType() {
    return String.format("array[%d..%d]", startIndex, endIndex);
  }

  public String getTypeVerbose() {
    return String.format("array with length=%d", length);
  }
}
