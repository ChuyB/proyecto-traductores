package org.example.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.example.lib.types.Type;

/** SymbolTable */
public class SymbolTable {

  private Map<String, Type> symbols;

  public SymbolTable() {
    this.symbols = new HashMap<>();
  }

  public boolean contains(String identifier) {
    return symbols.containsKey(identifier);
  }

  public Type get(String identifier) {
    return symbols.get(identifier);
  }

  public void put(String identifier, Type type) {
    symbols.put(identifier, type);
  }

  public Set<Map.Entry<String, Type>> entrySet() {
    return symbols.entrySet();
  }
}
