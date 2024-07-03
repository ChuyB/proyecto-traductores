package org.example.lib.types;

/** StringType */
public class StringType implements Type {
  public boolean equals(Type type) {
    return type instanceof StringType;
  }

  public String getType() {
    return "string";
  }
}
