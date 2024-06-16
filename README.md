# Repositorio del Proyecto de Traductores e Interpretadores

### Integrantes
- Blanyer Vielma, 16-11238
- Jesús Bovea, 19-10072

En este repositorio se ecuentra el código fuente del traductor de **GCL** a **Latex** para el proyecto de la asignatura Traductores e Interpretadores.

## Prerequsitos
- Java (versión 21 o superior).
- Gradle (o utilizar el _wrapper_).

## Compilación y ejecución
Para poder compilar y empaquetar el archivo `.jar` se debe ejecutar la acción de build de Gradle:
```shell
gradle build
```
Esto creará un archivo `.jar` dentro de la raíz del proyecto en la dirección `app/build/libs/app.jar`. El archivo `app.jar` se podrá ejecutar dentro de la raíz del proyecto con:
```shell
java -jar gcl/build/libs/app.jar 
```

Del mismo modo, está disponible el _wrapper_ de Gradle en caso de que no se tenga la instalación global en el sistema o se prefiera usar dicho _wrapper_. Está disponible en la raíz del proyecto a través de:
```
./gradlew
```
