# TaskFlow · Automatización móvil con Maestro

Aplicación Android de tareas personales desarrollada para la actividad **Automatización de recorridos móviles con Maestro**. Permite crear tareas, impedir nombres vacíos, marcarlas como completadas y recuperar los datos después de cerrar la aplicación.

La interfaz está en español y funciona sin conexión. El almacenamiento utiliza `SharedPreferences` con JSON y escritura síncrona. No requiere cuenta, backend, servicios pagados ni permisos de ejecución.

## Estado verificado de la entrega

Fecha de revisión: **29 de octubre de 2026**, zona `America/Guatemala`.

| Verificación | Resultado actual |
| --- | --- |
| Código fuente Android | Implementado |
| Tres flujos independientes | Implementados; YAML analizado con PyYAML 6.0.3 |
| Compilación y APK | assembleDebug y lintDebug aprobados en CI y Windows; APK de CI generado y descargado |
| Escenario 01: creación | **Aprobado**, 18.535 s, cero fallos |
| Escenario 02: validación | **Aprobado**, 11.731 s, cero fallos |
| Escenario 03: persistencia | **Aprobado**, 21.616 s, cero fallos |
| Suite de tres escenarios | **3/3 aprobados**, cero fallos; 44.196 s según JUnit |
| Escenario adicional: completar | **Aprobado**, 20.597 s |
| Capturas móviles | Capturas auténticas en `evidencias/capturas/` y artefactos originales de CI |
| Informe PDF | Generado y revisado visualmente: 15 páginas, cinco figuras y tabla comparativa legible |
| Publicación de entrega | Pendiente de comprobación |

Los tres escenarios se ejecutaron individualmente y como suite sobre Android. Sus resultados se comprobaron en los informes JUnit, logs y capturas reales; la compilación y el análisis lint se registran por separado.

La [ejecución de CI 40982924351](https://github.com/jefroMMM/Mobile-Testing-con-Maestro/actions/runs/40982924351) completó compilación, lint, instalación y todos los recorridos. Código ejecutado: [70268e2ca5a730477c965ef0430aacb8f87a7749](https://github.com/jefroMMM/Mobile-Testing-con-Maestro/commit/70268e2ca5a730477c965ef0430aacb8f87a7749). Inicio de registro: `2026-10-29T18:59:53-0600` en Guatemala (`2026-10-30T00:59:53Z`).

## Tecnologías y plataforma

| Componente | Configuración |
| --- | --- |
| Lenguaje y UI | Java 17; vistas Android nativas |
| Identificador Android | `dev.maestro.taskflow` |
| Versión de la app | `1.0.0` / código `1` |
| Android mínimo | Android 8.0, API 26 |
| SDK de compilación y destino | API 35 |
| Android Gradle Plugin | `8.7.3` |
| Gradle | `8.9` mediante wrapper |
| JDK verificado localmente | Eclipse Temurin `17.0.20.1+1` |
| JDK utilizado en CI | Eclipse Temurin `17.0.21+9` |
| PowerShell verificado | `7.6.5` (`pwsh`) |
| ADB verificado localmente | Android Debug Bridge `37.0.1` |
| Android SDK instalado localmente | Plataforma API 35, revisión 2; Build Tools `34.0.0` |
| Maestro instalado en CI | CLI `2.12.0` |
| Android físico opcional | Xiaomi `24090RA29G`, Android 16 / API 36; instalación bloqueada por política del teléfono. No necesario para reproducir CI |
| Emulador realmente utilizado | Android 10 / API 29, x86_64, perfil `pixel_2`; ADB `emulator-5554` |

El equipo detectado ejecuta Windows 11 Pro `10.0.26200`, x64, con Intel Core i5-7440HQ. La virtualización de firmware aparece desactivada. Se conectó y autorizó un teléfono Xiaomi para las pruebas locales; su identificador USB se omite de las evidencias públicas. No se instaló un emulador local al disponer de este dispositivo. Las versiones realmente ejecutadas y el dispositivo de cada prueba deben consultarse en las evidencias.

## Estructura

```text
app/                              Código y recursos Android
.maestro/                         Tres flujos obligatorios y config.yaml
.maestro/extras/                   Flujo adicional para completar tareas
scripts/                          Instalación, compilación y pruebas en PowerShell
docs/INFORME_ACADEMICO.md           Contenido editable del informe
docs/RUBRICA.md                    Auditoría de criterios y pendientes
GUION_EXPOSICION.md                Guion de tres minutos
evidencias/                       Logs, resultados y capturas reales
output/apk/                       APK local, excluido del historial
output/pdf/                       Informe PDF final
```

## Requisitos e instalación

Se necesita Windows de 64 bits, **PowerShell 7 o posterior** (`pwsh`), Git, acceso a Internet para descargar dependencias y espacio para Android SDK, Gradle y, si se utiliza, la imagen del emulador. Los scripts se verifican con PowerShell `7.6.5`; no se promete compatibilidad con Windows PowerShell 5.1. Si `pwsh` no está disponible, seguir la [instalación oficial de PowerShell 7 para Windows](https://learn.microsoft.com/powershell/scripting/install/installing-powershell-on-windows). Android Studio es opcional: los scripts utilizan las herramientas de línea de comandos.

Desde PowerShell:

```powershell
git clone https://github.com/jefroMMM/Mobile-Testing-con-Maestro.git
Set-Location Mobile-Testing-con-Maestro
pwsh -ExecutionPolicy Bypass -File .\scripts\bootstrap.ps1
. .\scripts\env.ps1
java -version
adb version
maestro --version
```

`bootstrap.ps1` prepara las dependencias locales. `env.ps1` configura el entorno de la sesión; no requiere modificar globalmente `PATH`. Las herramientas descargadas se encuentran bajo `%LOCALAPPDATA%\MaestroAssignment`. Si ya se dispone de Java o Android SDK, se pueden definir `JAVA_HOME` y `ANDROID_HOME` antes de cargar el entorno. El SDK requiere aceptar sus licencias.

## Compilar e instalar el APK

```powershell
pwsh -ExecutionPolicy Bypass -File .\scripts\build.ps1
```

El script ejecuta `assembleDebug` y `lintDebug`, conserva el log en `evidencias/build/gradle.log` y copia el APK a `output/apk/taskflow-debug.apk`. Se genera una suma SHA-256 junto a la evidencia de compilación. Es un APK de depuración, instalable para evaluación; no es una publicación firmada para Google Play.

Con un dispositivo o emulador iniciado:

```powershell
. .\scripts\env.ps1
adb devices -l
pwsh -ExecutionPolicy Bypass -File .\scripts\install.ps1
```

Cuando hay varios dispositivos, indicar el serial que muestra `adb devices`:

```powershell
pwsh -ExecutionPolicy Bypass -File .\scripts\install.ps1 -Device emulator-5554
```

## Preparar el emulador o teléfono

Para crear un Android 10 reproducible:

```powershell
pwsh -ExecutionPolicy Bypass -File .\scripts\create-emulator.ps1
pwsh -ExecutionPolicy Bypass -File .\scripts\start-emulator.ps1
. .\scripts\env.ps1
adb devices -l
adb shell getprop sys.boot_completed
```

Esperar a que el último comando devuelva `1` antes de instalar o probar. Si no existe aceleración disponible:

```powershell
pwsh -ExecutionPolicy Bypass -File .\scripts\start-emulator.ps1 -Software
```

El modo por software se ofrece como alternativa; su disponibilidad y velocidad dependen del equipo. Para un teléfono físico: activar Opciones de desarrollador, habilitar Depuración USB, conectar un cable de datos y aceptar la huella RSA del equipo. `adb devices -l` debe mostrar `device`, no `unauthorized` u `offline`.

## Alternativa reproducible con GitHub Actions

El workflow [Android y Maestro](.github/workflows/android-maestro.yml) compila y ejecuta los recorridos sobre `ubuntu-22.04`, Java 17 y un emulador Android 10/API 29 `default`, x86_64, perfil `pixel_2`, acelerado con KVM. Está preparado para ejecutarse al publicar cambios de código en `main` o manualmente mediante **Actions → Android y Maestro → Run workflow**.

El workflow ejecuta `assembleDebug` y `lintDebug`, instala Maestro `2.12.0`, instala el APK y llama a `scripts/ci-tests.sh`. Este script ejecuta 01, 02 y 03 individualmente, después la suite completa y finalmente el escenario adicional. Aunque un recorrido falle, intenta los restantes y registra sus códigos de salida.

En la página de la ejecución, descargar el artefacto **taskflow-delivery**. Contiene `output/apk/`, el informe de lint y `evidencias/ci/`, incluyendo logs, JUnit, capturas, versiones, identificación del dispositivo, fecha UTC y de Guatemala, `tested-commit.txt` y `results.tsv`. Los artefactos de Actions se conservan durante 30 días; la release de entrega debe conservar el APK y el informe para su distribución final.

Las pruebas se vinculan al commit de `tested-commit.txt`. Si se añaden posteriormente documentación o evidencias sin modificar el código ejecutado, la etiqueta final puede apuntar a un commit posterior; ambos identificadores deben distinguirse.

## Ejecutar Maestro

Los tres escenarios se ejecutan por separado:

```powershell
pwsh -ExecutionPolicy Bypass -File .\scripts\test.ps1 -Scenario 01
pwsh -ExecutionPolicy Bypass -File .\scripts\test.ps1 -Scenario 02
pwsh -ExecutionPolicy Bypass -File .\scripts\test.ps1 -Scenario 03
```

Para la suite completa:

```powershell
pwsh -ExecutionPolicy Bypass -File .\scripts\test.ps1 -Scenario all
```

El parámetro opcional `-Device SERIAL` selecciona un dispositivo en todos los comandos anteriores. El escenario adicional comprueba completar y conservar el estado de una tarea:

```powershell
pwsh -ExecutionPolicy Bypass -File .\scripts\test.ps1 -Scenario extra
```

También pueden utilizarse directamente los comandos oficiales desde la raíz del repositorio, después de cargar `env.ps1`:

```powershell
maestro test .maestro/01-crear-tarea.yaml
maestro test .maestro/02-validar-nombre.yaml
maestro test .maestro/03-persistencia.yaml
maestro test .maestro
```

`config.yaml` selecciona únicamente los tres escenarios obligatorios. Las pruebas usan IDs de recursos Android, assertions explícitas, esperas condicionadas y capturas identificadas. No utilizan coordenadas ni pausas fijas.

## Escenarios y assertions

| Escenario | Comportamiento comprobado | Assertions principales |
| --- | --- | --- |
| `01-crear-tarea.yaml` | Crear «Preparar entrega de Maestro» | Título visible; contador de una tarea; estado vacío ausente |
| `02-validar-nombre.yaml` | Intentar guardar sin nombre | Error obligatorio visible; al volver, lista vacía y cero tareas; ningún título visible |
| `03-persistencia.yaml` | Crear «Revisar tarea persistente», detener y reabrir | Mismo título visible tras reapertura; contador de una tarea; estado vacío ausente |

Una assertion es una comprobación automática. `assertVisible` exige que el elemento indicado exista y sea visible; `assertNotVisible` comprueba su ausencia. Combinar el ID con el texto esperado permite comprobar el contenido de la tarea. Las expresiones del contador aceptan el separador visual mediante `.*`.

La condición móvil se implementa con `stopApp` y una segunda apertura con `clearState: false`. Detener el proceso y abrirlo nuevamente obliga a recuperar la información persistida. Este alcance no equivale a reiniciar el sistema operativo ni demuestra recuperación ante daños del almacenamiento.

## Preparación, limpieza y dependencias

Cada flujo comienza con `launchApp: { clearState: true }` y comprueba la lista vacía. Por ello puede ejecutarse de forma independiente y repetida. La aplicación no solicita permisos, no abre sesiones de usuario y no depende de una red durante el recorrido. No se han utilizado servicios simulados: el almacenamiento y las vistas son reales y locales.

Después de cada prueba se conservan los datos finales para inspección; el siguiente escenario los limpia al inicio. Para limpiar manualmente:

```powershell
pwsh -ExecutionPolicy Bypass -File .\scripts\clean-data.ps1
```

Esto elimina **todas las tareas** de la aplicación instalada. No debe ejecutarse entre el guardado y la reapertura del escenario 03, porque eliminaría justamente los datos que se verifican.

## Evidencias y resultados

Durante la primera comprobación en CI, `assembleDebug` terminó, pero lint detectó que `windowLightNavigationBar` requiere API 27 mientras la aplicación admite desde API 26. El atributo se trasladó a recursos `values-v27`. La ejecución 40982924351 confirmó después compilación y lint satisfactorios. Este hallazgo no corresponde a una assertion móvil fallida.

Los resultados descargados de esa ejecución se conservan en [evidencias/ci/results.tsv](evidencias/ci/results.tsv), con códigos de salida `0` para `01`, `02`, `03`, `suite` y `extra`. Cada directorio incluye su `results.xml` y `maestro.log`. La suite contiene tres casos con `status="SUCCESS"` y cero fallos; su tiempo JUnit es 44.196 s, de los cuales 44.174 s corresponden a la suma de casos. La compilación local también completó `assembleDebug` y `lintDebug` en 2 min 12 s, según `evidencias/build/local-gradle.log`.

Las copias de consulta de las capturas, conservadas sin editar las imágenes, se encuentran en [evidencias/capturas/](evidencias/capturas/). Los originales permanecen en los artefactos de cada recorrido de CI. El APK de CI tiene SHA-256:

```text
7250036ff691654b835440560303a54132a11f56d0ef553df9798ff4ed95f330
```

`scripts/test.ps1` crea una carpeta por fecha, hora local y escenario en `evidencias/runs/`. Incluye el log de Maestro, listado de dispositivos, resultado JSON, informe JUnit cuando Maestro lo emite y artefactos de diagnóstico. Las capturas generadas por `takeScreenshot` quedan dentro del directorio de artefactos de esa ejecución, con estas identificaciones:

- `01-creacion/resultado`: tarea creada.
- `02-validacion/error`: mensaje de validación.
- `02-validacion/lista-vacia`: ausencia de tarea inválida.
- `03-persistencia/antes`: tarea antes del cierre.
- `03-persistencia/despues`: tarea después de reabrir.

Para localizar las capturas, incluidas las subcarpetas que organiza Maestro:

```powershell
Get-ChildItem -LiteralPath .\evidencias -Recurse -Filter *.png
```

Un fallo del entorno se documenta por separado de una assertion fallida. Nunca debe interpretarse una falta de dispositivo como una prueba aprobada.

## Entrega académica y publicación

- [Repositorio](https://github.com/jefroMMM/Mobile-Testing-con-Maestro).
- Contenido editable del [informe académico](docs/INFORME_ACADEMICO.md).
- [Informe PDF final](output/pdf/informe-academico.pdf): generado y revisado visualmente, 15 páginas.
- [Guion de exposición](GUION_EXPOSICION.md).
- [Auditoría de la rúbrica](docs/RUBRICA.md).
- Código publicado en GitHub; identificador final previsto: etiqueta `entrega-v1.0.0`. La etiqueta aún no está verificada.
- APK de CI generado y descargado; ubicación de compilación reproducible `output/apk/taskflow-debug.apk`. El enlace público se añadirá después de verificar la publicación de la release.

El APK se distribuye como adjunto de una GitHub Release para evitar binarios de compilación en el historial Git. Si la publicación automática no está disponible: abrir el repositorio, ir a **Releases → Draft a new release**, seleccionar o crear `entrega-v1.0.0` sobre el commit de entrega, adjuntar el APK y el PDF, publicar y verificar la descarga desde el enlace de la release.

El informe se prepara como PDF y Markdown editable. No se ha acreditado acceso a la plataforma Canvas; para entregar allí se debe entrar al curso, abrir la actividad y adjuntar el PDF final junto con el enlace del repositorio y la compilación.

Para regenerar el informe desde su contenido Markdown, con Python disponible:

```powershell
python -m pip install -r scripts/requirements-report.txt
python scripts/generate-report.py
```

El generador produce `output/pdf/informe-academico.pdf`. Después de modificar el contenido o las capturas, revisar visualmente el PDF regenerado antes de entregarlo.

## Limitaciones conocidas

- La publicación final del APK y del PDF en la release sigue pendiente de confirmación.
- La virtualización de firmware está desactivada en el equipo detectado.
- Las pruebas Maestro acreditadas corresponden al emulador Android 10 de CI; no se atribuyen al teléfono Android 16 conectado.
- La instalación opcional en el Xiaomi devolvió `INSTALL_FAILED_USER_RESTRICTED`. Se continuó con el emulador aprobado; no se requiere intervención en el teléfono para esta entrega.
- No hay autenticación, sincronización entre dispositivos, edición ni eliminación individual de tareas; no se requieren para los recorridos solicitados.
- La persistencia cubre tareas y su estado en este dispositivo; limpiar datos o desinstalar la aplicación los elimina.
- Las pruebas de cierre y reapertura no cubren reinicio del teléfono, pérdida de energía ni fallos físicos de almacenamiento.
- La aplicación limita los nombres a 120 caracteres y elimina espacios al principio y al final.

## Referencias oficiales

- [Maestro Flows](https://docs.maestro.dev/maestro-flows).
- [Maestro CLI](https://docs.maestro.dev/maestro-cli).
- [Instalación de Maestro](https://docs.maestro.dev/maestro-cli/how-to-install-maestro-cli).
- [Comando launchApp](https://docs.maestro.dev/reference/commands-available/launchapp.md).
- [Comando stopApp](https://docs.maestro.dev/reference/commands-available/stopapp.md).
- [Capturas](https://docs.maestro.dev/reference/commands-available/takescreenshot.md).

