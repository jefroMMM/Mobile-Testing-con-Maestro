#!/usr/bin/env bash
set -uo pipefail
export MAESTRO_CLI_NO_ANALYTICS=true
export MAESTRO_DRIVER_STARTUP_TIMEOUT=120000
mkdir -p evidencias/ci
date -u +'%Y-%m-%dT%H:%M:%SZ' > evidencias/ci/date-utc.txt
TZ=America/Guatemala date +'%Y-%m-%dT%H:%M:%S%z' > evidencias/ci/date-guatemala.txt
git rev-parse HEAD > evidencias/ci/tested-commit.txt
maestro --version > evidencias/ci/maestro-version.txt 2>&1
adb devices -l > evidencias/ci/devices.txt
adb shell getprop > evidencias/ci/android-properties.txt
adb install -r output/apk/taskflow-debug.apk 2>&1 | tee evidencias/ci/install.log
if [ "${PIPESTATUS[0]}" -ne 0 ]; then exit 1; fi
failed=0
printf 'execution\texit_code\tresult\n' > evidencias/ci/results.tsv
for scenario in 01 02 03 suite extra; do
  case "$scenario" in
    01) flow=.maestro/01-crear-tarea.yaml ;;
    02) flow=.maestro/02-validar-nombre.yaml ;;
    03) flow=.maestro/03-persistencia.yaml ;;
    suite) flow=.maestro ;;
    extra) flow=.maestro/extras/04-completar-tarea.yaml ;;
  esac
  run="evidencias/ci/$scenario"
  mkdir -p "$run"
  maestro --no-ansi test --format junit --output "$run/results.xml" --test-output-dir "$run/artifacts" --debug-output "$run/debug" "$flow" 2>&1 | tee "$run/maestro.log"
  status=${PIPESTATUS[0]}
  result=aprobado
  if [ "$status" -ne 0 ]; then result=fallido; failed=1; fi
  printf '%s\t%s\t%s\n' "$scenario" "$status" "$result" >> evidencias/ci/results.tsv
done
adb logcat -d -s AndroidRuntime:E > evidencias/ci/android-errors.log
exit "$failed"
