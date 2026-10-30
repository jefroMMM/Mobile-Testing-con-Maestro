param(
    [ValidateSet('all','01','02','03','extra')][string]$Scenario = 'all',
    [string]$Device
)
. "$PSScriptRoot/env.ps1"
Push-Location -LiteralPath $RepoRoot
try {
    $stamp = [TimeZoneInfo]::ConvertTimeBySystemTimeZoneId([DateTime]::UtcNow, 'Central America Standard Time').ToString('yyyyMMdd-HHmmss')
    $runDir = "evidencias/runs/$stamp-$Scenario"
    New-Item -ItemType Directory -Force $runDir | Out-Null
    $flows = @{'all'='.maestro'; '01'='.maestro/01-crear-tarea.yaml'; '02'='.maestro/02-validar-nombre.yaml'; '03'='.maestro/03-persistencia.yaml'; 'extra'='.maestro/extras/04-completar-tarea.yaml'}
    $prefix = @('--no-ansi')
    if ($Device) { $prefix += @('--device', $Device) }
    & adb devices 2>&1 | ForEach-Object { $_ -replace '^(\S+)(\s+(?:device|unauthorized|offline)\b)', '[serial omitido]$2' } | Out-File -LiteralPath "$runDir/devices.txt"
    & git rev-parse HEAD | Out-File -LiteralPath "$runDir/tested-commit.txt"
    & maestro --version 2>&1 | Out-File -LiteralPath "$runDir/maestro-version.txt"
    & maestro @prefix test --format junit --output "$runDir/results.xml" --test-output-dir "$runDir/artifacts" --debug-output "$runDir/debug" $flows[$Scenario] 2>&1 | Tee-Object -LiteralPath "$runDir/maestro.log"
    $code = $LASTEXITCODE
    @{ scenario=$Scenario; timestamp=$stamp; timezone='America/Guatemala'; exitCode=$code; result=$(if($code -eq 0){'aprobado'}else{'fallido'}); command='Maestro CLI test'; } | ConvertTo-Json | Set-Content -LiteralPath "$runDir/result.json" -Encoding utf8
    if ($code -ne 0) { throw "Maestro terminó con código $code. Revisa $runDir/maestro.log" }
} finally { Pop-Location }
