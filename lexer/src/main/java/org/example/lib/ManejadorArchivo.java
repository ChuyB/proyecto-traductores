package org.example.lib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ManejadorArchivo {
  private String dirArchivo;
  private StringBuilder contenido = new StringBuilder();

  public ManejadorArchivo(String dirArchivo) {
    this.dirArchivo = dirArchivo;
  }

  public String getContenido() {
    return contenido.toString();
  }

  public void procesarArchivo() {
    try (BufferedReader lector = new BufferedReader(new FileReader(dirArchivo))) {
      String linea;
      while ((linea = lector.readLine()) != null) {
        contenido.append(linea).append("\n");
      }
    } catch(IOException e) {
      System.err.println("Error al leer el archivo: " + e.getMessage());
      System.exit(1);
    }
  }

}
