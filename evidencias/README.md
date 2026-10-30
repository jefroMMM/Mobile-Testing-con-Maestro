# Inventario de evidencias auténticas

Ejecución principal: [GitHub Actions 40982924351](https://github.com/jefroMMM/Mobile-Testing-con-Maestro/actions/runs/40982924351), finalizada satisfactoriamente. Código probado: `70268e2ca5a730477c965ef0430aacb8f87a7749`.

El registro comenzó en `2026-10-29T18:59:53-0600` (Guatemala), equivalente a `2026-10-30T00:59:53Z`. Ambas referencias se conservan en `ci/date-guatemala.txt` y `ci/date-utc.txt`.

## Contenido

- `ci/results.tsv`: códigos de salida y resultados de 01, 02, 03, suite y extra; los cinco aprobaron.
- `ci/01/`, `ci/02/`, `ci/03/`, `ci/suite/`, `ci/extra/`: logs de Maestro, JUnit `results.xml`, diagnósticos y capturas originales. Suite: tres casos aprobados y cero fallos.
- `ci/tested-commit.txt`: commit exacto de la ejecución.
- `ci/java.txt`, `ci/gradle-version.txt`, `ci/maestro-version.txt`, `ci/devices.txt`, `ci/android-properties.txt`: versiones y entorno real.
- `ci/install.log`: instalación satisfactoria del APK en el emulador.
- `ci/build/`: log de compilación/lint, SHA-256 y reporte HTML original de lint.
- `capturas/`: copias de consulta de PNG originales, copiadas **sin editar la imagen**, con rutas estables por escenario.
- `build/local-gradle.log`: compilación y lint local aprobados en Windows; se sustituyó únicamente la ruta personal del proyecto por una indicación explícita.
- `environment-windows.json`: versiones verificadas localmente, sin publicar el serial del teléfono. Los registros temporales de instalación y licencias se conservan localmente, fuera del historial.
- `resultados-verificados.json`: resumen derivado de los JUnit y de los comandos completados, reproducible con `python scripts/summarize-results.py`.
- `historial/`: intentos previos, incluido checksum mal transcrito y fallo de lint por atributo API 27 con mínimo API 26, corregidos antes de la ejecución aprobada.

## Capturas estables

| Escenario | Archivo | Estado observado |
| --- | --- | --- |
| 01 | `capturas/01-creacion/resultado.png` | Tarea creada y contador de una tarea |
| 02 | `capturas/02-validacion/error.png` | Mensaje de nombre obligatorio |
| 02 | `capturas/02-validacion/lista-vacia.png` | Cero tareas después del rechazo |
| 03 | `capturas/03-persistencia/antes.png` | Tarea guardada antes de cerrar |
| 03 | `capturas/03-persistencia/despues.png` | Misma tarea después de reabrir |
| Extra | `capturas/04-completar/resultado.png` | Tarea completada y estado persistente |

Las imágenes documentan estados visibles; los logs y JUnit acreditan la ejecución y sus assertions. No hay capturas simuladas ni resultados fabricados. `emulator-5554` identifica el emulador de CI y no es un serial personal. El identificador USB del teléfono físico se omite de los registros públicos. La instalación física fue bloqueada por la política Xiaomi y no se presenta como aprobada; las pruebas de entrega corresponden al emulador.

Los artefactos originales de Actions tienen retención de 30 días. Estas copias versionadas y la release permiten conservar la evidencia de entrega.
