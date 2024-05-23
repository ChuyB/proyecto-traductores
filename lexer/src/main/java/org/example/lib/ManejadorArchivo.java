package org.example.lib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase que maneja la lectura de un archivo.
 */
public class ManejadorArchivo {
  private String dirArchivo;
  private StringBuilder contenido = new StringBuilder();

  /**
   * Constructor de la clase.
   * @param dirArchivo Dirección del archivo a leer.
   */
  public ManejadorArchivo(String dirArchivo) {
    this.dirArchivo = dirArchivo;
  }

  /**
   * Obtiene el contenido del archivo.
   * @return Contenido del archivo.
   */
  public String getContenido() {
    return contenido.toString();
  }

  /**
   * Procesa el archivo y guarda su contenido.
   */
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
