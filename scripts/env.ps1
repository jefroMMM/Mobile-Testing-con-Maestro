#Requires -Version 7.0
$ErrorActionPreference = 'Stop'
$RepoRoot = Split-Path $PSScriptRoot -Parent
$ToolRoot = Join-Path $env:LOCALAPPDATA 'MaestroAssignment'
if (-not $env:JAVA_HOME -and (Test-Path -LiteralPath "$ToolRoot/java")) {
    $jdk = Get-ChildItem -LiteralPath "$ToolRoot/java" -Directory | Select-Object -First 1
    if ($jdk) { $env:JAVA_HOME = $jdk.FullName }
}
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$ToolRoot/android-sdk" }
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
$env:MAESTRO_CLI_NO_ANALYTICS = 'true'
$env:MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED = 'true'
$env:PATH = "$env:JAVA_HOME/bin;$env:ANDROID_HOME/platform-tools;$env:ANDROID_HOME/emulator;$env:ANDROID_HOME/cmdline-tools/latest/bin;$ToolRoot/maestro/bin;$env:PATH"
