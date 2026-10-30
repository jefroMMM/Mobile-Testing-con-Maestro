param([string]$Device)
. "$PSScriptRoot/env.ps1"
$argsAdb = @()
if ($Device) { $argsAdb += @('-s', $Device) }
& adb @argsAdb shell pm clear dev.maestro.taskflow
if ($LASTEXITCODE -ne 0) { throw 'No se pudieron limpiar los datos.' }
