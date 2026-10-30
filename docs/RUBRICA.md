# Auditoría de la rúbrica

Revisión: **29 de octubre de 2026**, zona `America/Guatemala`. Evidencia principal: [CI 40982924351](https://github.com/jefroMMM/Mobile-Testing-con-Maestro/actions/runs/40982924351), finalizada con `conclusion: success`. Código probado: `70268e2ca5a730477c965ef0430aacb8f87a7749`.

- [x] Aplicación móvil real e instalable: APK compilado e instalado en emulador; `evidencias/ci/install.log` registra `Success`.
- [x] Código fuente disponible: app, recursos y Gradle publicados en GitHub.
- [x] Plataforma y versiones documentadas: Windows local, Ubuntu CI, Java, Gradle, SDK, ADB, Maestro 2.12.0 y emulador Android 10/API 29 x86_64.
- [x] Tres escenarios YAML en `.maestro/`, con archivos independientes.
- [x] Tres escenarios realmente ejecutados: aprobaron individualmente y como suite; logs y JUnit en `evidencias/ci/`.
- [x] Assertions explícitas de título, contador, validación, ausencia de tarea inválida y persistencia.
- [x] Selectores estables: IDs completos de recursos Android, sin coordenadas.
- [x] Escenarios independientes: cada flujo limpia al inicio; ejecutados por separado y en suite con éxito.
- [x] Preparación y limpieza documentadas en README e informe; script auxiliar disponible.
- [x] Evidencias auténticas: logs, JUnit, diagnósticos y capturas originales de CI, con copias de consulta sin editar.
- [x] Resultados observados documentados: tres casos aprobados, suite 3/3 y caso adicional aprobado.
- [x] README reproducible: comandos con estructura real, scripts y workflow comprobado; build/lint también aprobados localmente.
- [ ] APK accesible mediante enlace público permanente: APK descargado y SHA-256 verificado; release pendiente de confirmación.
- [x] Commit identificado: código probado `70268e2ca5a730477c965ef0430aacb8f87a7749`; etiqueta final prevista `entrega-v1.0.0`.
- [x] Informe PDF completo: generado y revisado visualmente, 15 páginas con cinco figuras auténticas y tabla comparativa legible, sin recortes.
- [ ] Enlaces correctos de entrega final: repositorio, commit y CI disponibles; falta confirmar release, descarga APK y PDF.

## Resultados comprobados

| Ejecución | Resultado | Tiempo JUnit |
| --- | --- | --- |
| 01. Creación | Aprobado | 18.535 s |
| 02. Validación | Aprobado | 11.731 s |
| 03. Persistencia | Aprobado | 21.616 s |
| Suite obligatoria | 3/3 aprobados, cero fallos | 44.196 s |
| Adicional: completar | Aprobado | 20.597 s |

`evidencias/ci/results.tsv` registra código cero para las cinco ejecuciones. Los JUnit acreditan `SUCCESS`. SHA-256 del APK original de CI: `7250036ff691654b835440560303a54132a11f56d0ef553df9798ff4ed95f330`.

## Límites de la comprobación

Las pruebas acreditadas se realizaron en el emulador Android 10/API 29 de GitHub Actions, acelerado con KVM. La compilación y lint también aprobaron en Windows. La instalación opcional en un Xiaomi Android 16 fue bloqueada con `INSTALL_FAILED_USER_RESTRICTED`; no se atribuyen pruebas físicas aprobadas. El usuario eligió continuar con el emulador, suficiente para los escenarios requeridos. No se necesita modificar el teléfono para entregar o reproducir el workflow.

La etiqueta final podrá apuntar a un commit posterior que agregue documentación y evidencias; el commit de código efectivamente ejecutado permanece identificado. Los pendientes solo deben marcarse al confirmar publicación y revisión final.
