. "$PSScriptRoot/env.ps1"
Push-Location -LiteralPath $RepoRoot
try {
    $out = Join-Path $RepoRoot 'evidencias/build'
    New-Item -ItemType Directory -Force $out | Out-Null
    & ./gradlew.bat --no-daemon --console=plain assembleDebug lintDebug 2>&1 | Tee-Object -FilePath "$out/gradle.log"
    if ($LASTEXITCODE -ne 0) { throw "Gradle falló: $LASTEXITCODE" }
    New-Item -ItemType Directory -Force output/apk | Out-Null
    Copy-Item -LiteralPath 'app/build/outputs/apk/debug/app-debug.apk' -Destination 'output/apk/taskflow-debug.apk' -Force
    Get-FileHash -LiteralPath 'output/apk/taskflow-debug.apk' -Algorithm SHA256 | Format-List | Out-File "$out/apk-sha256.txt"
} finally { Pop-Location }

