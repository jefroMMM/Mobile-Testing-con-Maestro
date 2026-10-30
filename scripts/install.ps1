param([string]$Device)
. "$PSScriptRoot/env.ps1"
$argsAdb = @()
if ($Device) { $argsAdb += @('-s', $Device) }
& adb @argsAdb install -r "$RepoRoot/output/apk/taskflow-debug.apk"
if ($LASTEXITCODE -ne 0) { throw 'No se pudo instalar el APK; comprueba adb devices.' }
