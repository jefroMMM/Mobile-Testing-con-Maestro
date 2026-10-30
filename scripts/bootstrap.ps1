#Requires -Version 7.0
param([switch]$WithEmulator)
$ErrorActionPreference = 'Stop'
$ToolRoot = Join-Path $env:LOCALAPPDATA 'MaestroAssignment'
New-Item -ItemType Directory -Force $ToolRoot | Out-Null
function Get-VerifiedArchive([string]$Url, [string]$Path, [string]$Sha256) {
    if (-not (Test-Path -LiteralPath $Path) -or (Get-FileHash -LiteralPath $Path -Algorithm SHA256).Hash -ne $Sha256) {
        & curl.exe -fLsS --retry 3 $Url -o $Path
        if ($LASTEXITCODE -ne 0) { throw "Descarga fallida: $Url" }
    }
    if ((Get-FileHash -LiteralPath $Path -Algorithm SHA256).Hash -ne $Sha256) { throw "SHA256 incorrecto: $Path" }
}
if (-not (Test-Path -LiteralPath "$ToolRoot/java/jdk-17.0.20.1+1/bin/java.exe")) {
    Get-VerifiedArchive 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.20.1%2B1/OpenJDK17U-jdk_x64_windows_hotspot_17.0.20.1_1.zip' "$ToolRoot/jdk.zip" 'e53a79c3c3d86865bd7e787903884331068e71321714ffd44f145785affc7cb0'
    Expand-Archive -LiteralPath "$ToolRoot/jdk.zip" -DestinationPath "$ToolRoot/java" -Force
}
if (-not (Test-Path -LiteralPath "$ToolRoot/android-sdk/cmdline-tools/latest/bin/sdkmanager.bat")) {
    Get-VerifiedArchive 'https://dl.google.com/android/repository/commandlinetools-win-15859902_latest.zip' "$ToolRoot/sdk.zip" '90ae805d20434428bffcb699c290860f19bb5f66a67e6b330067e3de801fb04a'
    Expand-Archive -LiteralPath "$ToolRoot/sdk.zip" -DestinationPath "$ToolRoot/cmdline" -Force
    New-Item -ItemType Directory -Force "$ToolRoot/android-sdk/cmdline-tools" | Out-Null
    Move-Item -LiteralPath "$ToolRoot/cmdline/cmdline-tools" -Destination "$ToolRoot/android-sdk/cmdline-tools/latest"
}
if (-not (Test-Path -LiteralPath "$ToolRoot/maestro/bin/maestro.bat")) {
    Get-VerifiedArchive 'https://github.com/mobile-dev-inc/Maestro/releases/download/cli-2.12.0/maestro.zip' "$ToolRoot/maestro.zip" 'a6044a84542f522f434c5dc3d609e60c7446d423f3ed1b2ae513f5c48663ec5e'
    Expand-Archive -LiteralPath "$ToolRoot/maestro.zip" -DestinationPath $ToolRoot -Force
}
. "$PSScriptRoot/env.ps1"
1..100 | ForEach-Object { 'y' } | & sdkmanager.bat --licenses
if ($LASTEXITCODE -ne 0) { throw 'No se pudieron aceptar las licencias del SDK.' }
& sdkmanager.bat 'platform-tools' 'platforms;android-35' 'build-tools;34.0.0'
if ($LASTEXITCODE -ne 0) { throw 'Falló la instalación de paquetes Android.' }
& java -version
& adb version
& maestro --version
if ($WithEmulator) { & "$PSScriptRoot/create-emulator.ps1" }
Write-Output 'Herramientas listas. Ejecuta scripts/build.ps1.'
