# Puente Kotlin para el TDSL

Este módulo toma un programa del TDSL (el lenguaje de planeación: `Person`, `Task`, `Lunch`/`Meeting`/`Report`/`Pay`, `duration`...), deja que Rascal lo parsee, exporta el árbol como JSON y lo consume desde Kotlin para armar un reporte. Es la pieza que conecta el front de Rascal con una app JVM normal.

## Cómo funciona

El flujo es el mismo patrón "Rascal como subproceso" que usan otros proyectos del curso:

1. Kotlin lanza el shell de Rascal: `java -jar rascal-shell-stable.jar Json.rsc ast <archivo.tdsl>`.
2. `Json.rsc` parsea el `.tdsl` y serializa el resultado a JSON, imprimiéndolo por stdout.
3. Kotlin limpia la salida, extrae el objeto JSON y lo deserializa con `kotlinx.serialization` a clases tipadas (`Planning`, `PersonTasks`, `Task`, `Action`...).
4. `Report.kt` recorre esas clases y arma un resumen: tareas por persona, total a pagar en euros y tiempo total planificado.

Si no encuentra el jar, Kotlin cae a un AST de muestra (`examples/program.ast.json`) para que el demo corra igual.

## Por qué se serializa desde el árbol concreto (CST) y no desde el AST

`implode(#Planning, ...)` no funciona en el estado actual del repo: `Syntax.rsc` y `AST.rsc` están desfasados (por ejemplo, la producción `task` de la gramática tiene un campo `name` que el constructor `task` del AST no tiene), así que `implode` lanza `IllegalArgument`. Por eso `Json.rsc` serializa recorriendo el árbol concreto con *concrete syntax matching*, igual que hace `Generator.rsc`. No depende de `typepal` ni de `rascal-lsp`: solo de `Syntax`, `Parser` y `ParseTree`.

## El JSON

Cada nodo lleva una etiqueta `"node"`. Ejemplo de una tarea:

```json
{
  "node": "task",
  "name": "Bob",
  "priority": 2,
  "action": { "node": "payment", "action": { "node": "paymentAction", "amount": 5000 } },
  "duration": [ { "node": "duration", "amount": 1, "unit": "hour" } ]
}
```

Del lado Kotlin, `Action` se deserializa con un `JsonContentPolymorphicSerializer` que decide la subclase según `"node"` (`lunch`/`meeting`/`paper`/`payment`).

## Cómo correrlo

Necesitas `rascal-shell-stable.jar` en la raíz del repo (un nivel arriba de `kotlin-app/`).

Probar solo el lado Rascal:

```bash
cd src/main/rascal
java -Drascal.projectPath="$(pwd)" -jar ../../../rascal-shell-stable.jar Json.rsc ast ../../../kotlin-app/examples/program.tdsl
```

Correr el puente completo (Kotlin llama a Rascal y arma el reporte):

```bash
cd kotlin-app
gradle run
```

Correr los tests (deserializan el AST de muestra y verifican el resumen):

```bash
cd kotlin-app
gradle test
```

## Notas de versiones

- El proyecto Rascal apunta a `0.40.17`, igual que el `rascal-shell-stable.jar`.
- El módulo Kotlin usa el plugin de Kotlin `1.9.22`, que necesita un JDK 21 o menor para compilar. Si tu `java` por defecto es más nuevo (p. ej. 25), corre Gradle apuntando a un JDK 21:

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home gradle test
```

## Archivos

- `src/main/rascal/Json.rsc` (en el proyecto Rascal, no en `kotlin-app/`): serializa el CST de `Planning` a JSON.
- `kotlin-app/src/main/kotlin/tdsl/bridge/Ast.kt`: clases tipadas + deserialización polimórfica.
- `kotlin-app/src/main/kotlin/tdsl/bridge/Report.kt`: agregaciones y reporte.
- `kotlin-app/src/main/kotlin/tdsl/bridge/RascalService.kt`: ejecuta el jar como subproceso.
- `kotlin-app/src/main/kotlin/tdsl/bridge/Main.kt`: punto de entrada.
- `kotlin-app/examples/`: un `.tdsl` de muestra y su JSON pre-generado.
