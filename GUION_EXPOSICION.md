# Guion de exposición · aproximadamente tres minutos

Fecha de referencia: 29 de octubre de 2026. Los tres escenarios aprobaron individualmente y como suite en un emulador Android 10 de GitHub Actions. Este guion utiliza esas evidencias reales y no requiere un teléfono físico.

## 0:00–0:25 · La aplicación

**QUÉ DECIR:**

«Para esta actividad desarrollamos TaskFlow, una aplicación Android para organizar tareas personales. Permite crear una tarea, impedir que se guarde sin nombre y marcarla como completada. Los datos se guardan en el propio dispositivo, de modo que no necesitamos una cuenta ni un servidor para utilizarla.»

**QUÉ MOSTRAR:**

La captura real `evidencias/capturas/01-creacion/resultado.png` y la descripción del README. Esta evidencia permite explicar la app sin instalarla en un teléfono.

## 0:25–0:50 · Qué es Maestro

**QUÉ DECIR:**

«Maestro permite automatizar recorridos sobre una aplicación móvil. Escribimos los pasos en archivos YAML: abrir la aplicación, pulsar un botón, escribir y comprobar lo que aparece. Usamos identificadores estables para encontrar los controles, así los pasos no dependen de tocar una posición exacta de la pantalla.»

**QUÉ MOSTRAR:**

Abrir `.maestro/01-crear-tarea.yaml` y señalar `launchApp`, `tapOn`, `inputText` y el ID `task_name_input`. Mostrar `app/src/main/res/values/ids.xml` para relacionar ese ID con la app.

## 0:50–1:20 · Creación y validación

**QUÉ DECIR:**

«El primer escenario comienza con una lista vacía, abre el formulario y guarda una tarea válida. Después comprueba que el nombre aparece y que el contador indica una tarea. El segundo intenta guardar sin escribir un nombre. Exige el mensaje de validación y, al volver a la lista, comprueba que no se creó ninguna tarea.»

**QUÉ MOSTRAR:**

Los archivos `01-crear-tarea.yaml` y `02-validar-nombre.yaml`. Señalar `validation_error`. Mostrar las capturas reales `01-creacion/resultado.png`, `02-validacion/error.png` y `02-validacion/lista-vacia.png` de `evidencias/capturas/`.

## 1:20–1:50 · Persistencia y assertions

**QUÉ DECIR:**

«El tercer escenario crea una tarea, cierra la aplicación y la abre otra vez. En esa segunda apertura no borra los datos: así puede comprobar si la tarea se conserva. Las assertions son comprobaciones automáticas. Una exige que el nombre de la tarea sea visible; otra verifica que ya no aparezca el aviso de lista vacía. Si una condición no se cumple, la prueba debe fallar.»

**QUÉ MOSTRAR:**

Abrir `03-persistencia.yaml`. Señalar `stopApp`, `clearState: false`, `assertVisible` y `assertNotVisible`. Comparar las capturas reales `evidencias/capturas/03-persistencia/antes.png` y `despues.png`.

## 1:50–2:20 · Resultados reales

**QUÉ DECIR:**

«Los tres escenarios aprobaron por separado y también como conjunto: tres de tres, sin fallos. Además, aprobó una prueba para completar una tarea y conservar ese estado. Las ejecuciones se hicieron en un emulador Android 10 de GitHub. Conservamos los registros, los resultados automáticos y las capturas reales. Esto demuestra los recorridos definidos en ese entorno, aunque no prueba todos los modelos de teléfono.»

**QUÉ MOSTRAR:**

La tabla de resultados del README, `evidencias/ci/results.tsv` y `evidencias/ci/suite/results.xml`. Señalar los tres casos con `SUCCESS` y cero fallos. Mostrar la ejecución de GitHub Actions 40982924351 con estado satisfactorio.

## 2:20–2:45 · Cómo reproducir

**QUÉ DECIR:**

«Para repetir las pruebas podemos ejecutar el workflow Android y Maestro desde GitHub Actions. También están los pasos locales para compilar, instalar el APK y lanzar cada escenario o la suite. Cada recorrido limpia sus datos al inicio y conserva el resultado para revisarlo. El README indica los comandos y la ubicación de las evidencias.»

**QUÉ MOSTRAR:**

En GitHub, mostrar **Actions → Android y Maestro → Run workflow**. En el README, señalar `pwsh -ExecutionPolicy Bypass -File .\scripts\test.ps1 -Scenario all` y la carpeta `evidencias/`. No es necesario iniciar otra ejecución durante la exposición.

## 2:45–3:00 · Conclusión

**QUÉ DECIR:**

«La entrega reúne una aplicación instalable y pruebas aprobadas de creación, validación y conservación de datos. Una prueba debe demostrar su resultado con comprobaciones y evidencias reales. El repositorio, el APK y el informe permiten revisar el trabajo y reproducir los recorridos.»

**QUÉ MOSTRAR:**

El repositorio, el informe académico y la auditoría de la rúbrica. Abrir la release `entrega-v1.0.0`, donde se encuentran el APK instalable y el informe PDF publicados. Las evidencias de CI respaldan los resultados expuestos.
