package org.example.lib.types;

/** IntType */
public class IntType implements Type {
  public boolean equals(Type type) {
    return type instanceof IntType;
  }

  public String getType() {
    return "int";
  }
}
