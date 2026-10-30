. "$PSScriptRoot/env.ps1"
$stamp = [TimeZoneInfo]::ConvertTimeBySystemTimeZoneId([DateTime]::UtcNow, 'Central America Standard Time').ToString('yyyy-MM-ddTHH:mm:ss') + '-06:00'
$os = Get-CimInstance Win32_OperatingSystem
$cpu = Get-CimInstance Win32_Processor | Select-Object -First 1
$record = [ordered]@{
    timestamp = $stamp
    timezone = 'America/Guatemala'
    os = $os.Caption
    osVersion = $os.Version
    architecture = $os.OSArchitecture
    cpu = $cpu.Name
    firmwareVirtualization = $cpu.VirtualizationFirmwareEnabled
    powershell = $PSVersionTable.PSVersion.ToString()
    java = (& java -version 2>&1 | ForEach-Object { "$_" }) -join "`n"
    adb = (& adb version | Select-Object -First 2) -join "`n"
    maestro = (& maestro --version 2>&1 | ForEach-Object { "$_" }) -join "`n"
    manufacturer = (& adb shell getprop ro.product.manufacturer).Trim()
    model = (& adb shell getprop ro.product.model).Trim()
    androidVersion = (& adb shell getprop ro.build.version.release).Trim()
    androidApi = (& adb shell getprop ro.build.version.sdk).Trim()
    usbSerial = 'Omitido deliberadamente; no se publica un identificador personal del dispositivo.'
}
New-Item -ItemType Directory -Force "$RepoRoot/evidencias" | Out-Null
$record | ConvertTo-Json | Set-Content -LiteralPath "$RepoRoot/evidencias/environment-windows.json" -Encoding utf8
