. "$PSScriptRoot/env.ps1"
& sdkmanager.bat 'emulator' 'system-images;android-29;default;x86_64'
if ($LASTEXITCODE -ne 0) { throw 'No se pudo instalar la imagen del emulador.' }
'no' | & avdmanager.bat create avd --name TaskFlow_API29 --package 'system-images;android-29;default;x86_64' --device 'pixel_2' --force
if ($LASTEXITCODE -ne 0) { throw 'No se pudo crear el AVD.' }

