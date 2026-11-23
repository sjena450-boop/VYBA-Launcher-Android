# VYBA Launcher & Theme Engine

**VYBA** (`com.vyba.launcher`) is a Material 3 Android home-screen launcher and live theme customization app built with Kotlin and Jetpack Compose.

## Features

- **Home Launcher & App Drawer**: Real `CATEGORY_HOME` launcher with live clock/date header, customizable 4/5-column home grid, 5-slot dock, instant-search app drawer, and long-press app actions (pin/unpin, hide, app info, uninstall).
- **Live Theme Studio**: 4 curated VYBA presets (*Obsidian Pulse*, *Nordic Frost*, *Amber Dusk*, *Emerald Mono*) plus Android 12+ Material You Dynamic Color, Light/Dark/System mode, corner-radius tokens (*Sharp*, *Balanced*, *Rounded*), and icon shapes (*Circle*, *Squircle*, *Rounded Square*).
- **Wallpaper Studio**: Built-in procedural gradient wallpapers rendered on demand and applied directly to Home Screen, Lock Screen, or Both via `WallpaperManager`.
- **Launcher Settings**: One-tap Default Home Launcher setup (`RoleManager` / `ACTION_HOME_SETTINGS`), hidden apps manager, and DataStore persistence.

## Architecture

```text
app/src/main/java/com/vyba/launcher/
├── core/theme/          # Color tokens, typography, shapes, spacing, reusable UI components
├── data/
│   ├── local/           # DataStore Preferences persistence
│   ├── models/          # AppInfo, ThemeConfig, LauncherPreferences, WallpaperPreset
│   └── repositories/    # PackageManager app loader & WallpaperManager applier
├── features/
│   ├── home/            # Home screen, dock, and searchable App Drawer
│   ├── themes/          # Live Theme & Icon Studio
│   ├── wallpapers/      # Curated wallpaper gallery & applier
│   └── settings/        # Default launcher, hidden apps, reset, version info
└── ui/navigation/       # Compose Navigation graph
```

## Build & APK Instructions

### Prerequisites
- Android Studio Ladybug (or newer) with JDK 17 and Android SDK 35 (`minSdk 26`, `targetSdk 35`).

### Debug APK
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (Signed)
Create `keystore.properties` in the project root (git-ignored):
```properties
storeFile=/path/to/vyba-release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=vyba
keyPassword=YOUR_KEY_PASSWORD
```
Then run:
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`
