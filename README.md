# Fotolicht

Minimal Android app that turns the display into a full-screen color light.

## Features

- Full-screen color output
- Tap anywhere to show or hide the controls
- Brightness slider
- Hue spectrum slider with tap and drag support
- Screen stays awake while the app is open

## Open In Android Studio

1. Open `/mnt/c/proj/fotolicht` in Android Studio.
2. Let Android Studio use JDK 17 for Gradle.
3. Install Android SDK Platform 35 if Studio prompts for it.
4. Sync the project and run the `app` configuration on a device or emulator.

## Notes

- `local.properties` is intentionally not checked in; Android Studio creates it automatically for your local SDK path.
- The Gradle wrapper properties are included. If Android Studio asks to download Gradle 8.10.2, accept it.
