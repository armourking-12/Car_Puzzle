# Car Puzzle (Parking Escape Puzzle)

A native Android puzzle game re-engineered in **Java** and **XML**, featuring full game mechanics, AdMob monetization, and local asset rendering via AndroidX `WebViewAssetLoader`.

---

## 🎮 Overview

Car Puzzle (Parking Escape Puzzle) is an isometric puzzle game where players must strategically navigate and unblock cars from cramped parking lots to escape. The game runs on a local HTML5/PixiJS engine wrapped in a native Android shell.

### Key Features
* **Native Android Architecture**: Native Java controller (`MainActivity.java`) with layout XML and adaptive vector icons.
* **Secure Local Asset Interception**: Uses AndroidX `WebViewAssetLoader` with domain `game.local` and `/assets/` path handling for secure, high-performance offline gameplay.
* **Monetization (Google AdMob)**:
  * Adaptive Banner Ads
  * Interstitial Ads
  * Rewarded Video Ads with JavaScript callback bridge (`window.rewardGranted()` & `window.adClosed()`)
* **Immersive Mode**: Fullscreen sticky immersive mode (`SYSTEM_UI_FLAG_IMMERSIVE_STICKY`).
* **JavaScript Interface (`@JavascriptInterface`)**: Two-way communication bridge (`Android`) between the native layer and Web game engine.
* **Pro License Support**: Detects `"ispro"` preference to disable ads.

---

## 📁 Project Structure

```
Car_Puzzle/
├── app/
│   ├── build.gradle                 # Module dependencies & SDK config
│   ├── proguard-rules.pro           # Proguard & JS bridge keep rules
│   └── src/main/
│       ├── AndroidManifest.xml      # App permissions, activities, & AdMob App ID
│       ├── java/
│       │   └── com/segadroid/parkingescapepuzzle/
│       │       └── MainActivity.java # Native controller & WebAppInterface bridge
│       ├── res/
│       │   ├── layout/activity_main.xml # ConstraintLayout (WebView + AdView)
│       │   ├── values/              # Strings, colors, & themes
│       │   ├── drawable/            # Vector drawables
│       │   └── mipmap-anydpi-v26/   # Adaptive icons
│       └── assets/
│           ├── info/file.html       # Game entry point
│           ├── src/                 # PixiJS, CreateJS, Howler audio, & ad integration
│           └── assets/              # Spritesheets, textures, sounds, & levels
├── build.gradle                     # Top-level Gradle configuration
├── settings.gradle                  # Gradle project settings
└── gradle.properties                # Build options & JVM memory allocation
```

---

## 🛠️ Build and Run

### Prerequisites
* **Android Studio** (Hedgehog 2023.1.1 or newer recommended)
* **JDK 17** or higher
* **Android SDK**: Compile SDK 34, Min SDK 23

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/armourking-12/Car_Puzzle.git
   ```
2. Open the project in Android Studio.
3. Allow Gradle to sync dependencies.
4. Select a connected device or emulator and click **Run** (or press `Shift + F10`).
