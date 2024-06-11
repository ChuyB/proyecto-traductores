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

  private void verificarExtension(){
    if (dirArchivo == null) {
      throw new IllegalArgumentException("Debe ingresar la dirección al archivo de entrada");
    }
    String extension = "";
    int iUltimaExtension = dirArchivo.lastIndexOf('.');
    int ultimoSeparadorWin = dirArchivo.lastIndexOf('\\');
    int ultimoSeparadorLin = dirArchivo.lastIndexOf('/');
    int ultimoSeparador = Math.max(ultimoSeparadorWin, ultimoSeparadorLin);

    if (iUltimaExtension > ultimoSeparador) {
      extension = dirArchivo.substring(iUltimaExtension + 1);
    }

    if (!extension.equals("gcl")) {
      throw new IllegalArgumentException("El archivo debe tener extensión .gcl");
    }
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
    // Verifica que la extensión del archivo sea .gcl
    try {
      verificarExtension();
    } catch (Exception e) {
      System.err.println("Error al leer el archivo: " + e.getMessage());
      System.exit(1);
    }

    // Lee los contenidos del archivo
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
