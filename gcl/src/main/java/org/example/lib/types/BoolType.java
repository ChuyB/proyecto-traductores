package org.example.lib.types;

/** BoolType */
public class BoolType implements Type {
  public boolean equals(Type type) {
    return type instanceof BoolType;
  }

  public String getType() {
    return "bool";
  }
}
