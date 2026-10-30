param([switch]$Software)
. "$PSScriptRoot/env.ps1"
$emulatorArgs = @('-avd','TaskFlow_API29','-no-snapshot','-no-boot-anim','-gpu','swiftshader_indirect','-memory','1536','-cores','2')
if ($Software) { $emulatorArgs += @('-accel','off') }
Start-Process -FilePath "$env:ANDROID_HOME/emulator/emulator.exe" -ArgumentList $emulatorArgs -WindowStyle Hidden
Write-Output 'Emulador iniciado. Espera a que adb shell getprop sys.boot_completed devuelva 1.'

