[![GitHub](https://img.shields.io/github/license/SimonSchubert/Braincup.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Braincup
Open source memory and focus improvement app.

## Screenshots

### Android (with Jetpack Compose)
<img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_android_01.png" width="200"> <img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_android_02.png" width="200">

### iOS (with SwiftUI)
<img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_ios_01.png" width="200"> <img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_ios_02.png" width="200">

### Terminal
<img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_mac_cli_01.png" width="400"> <img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_mac_cli_02.png" width="400">

### Web
<img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_web_01.png" width="400"> <img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_web_02.png" width="400">

## Install

### macOS

#### Homebrew
`brew tap SimonSchubert/braincup && brew install SimonSchubert/braincup/braincup`

### Web
https://braincup.schubert-simon.de/

### Android
Download apk from https://github.com/SimonSchubert/Braincup/releases and install manually. Play Store and F-Droid will follow.

## Goal
Build an open source memory and focus improvement app. The app itself consists of a bunch of small games. Right now I try to keep the games as simple as possible to allow the implementation for the terminal client.

## Doing
- Android client
- iOS client
- macOS terminal client
- Linux terminal client
- JS web client
- High score server

## Todo
- Linux gui client
- Windows terminal client
- Windows gui client
- macOS gui client
- Gamification

## Contribution
Very welcome.

## Gradle build tasks
- Build and run mac terminal client = runDebugExecutableMacosxX64
- Build and run js web client = jsWeb
- Run unit test on android target = androidTest

## Setup Android app with Jetpack Compose

Follow the instructions to download and setup Jetpack Compose: https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/README.md

Copy the content of "Braincup/shared/src/commonMain/kotlin" to "/androidx-master-dev⁩/⁨frameworks⁩/⁨support⁩/ui⁩/ui-material⁩/integration-tests⁩/material-studies⁩/⁨src⁩/⁨main"

Replace Api.kt with the following:
```
package com.inspiredandroid.braincup
object Api {
    internal fun postScore(gameId: Int, score: Int, callback: (String) -> Unit) {
        callback("99")
    }
}
```

Copy the MainActivity.kt and AppTheme.kt from "Braincup⁩/⁨app⁩/src⁩/main⁩/java⁩/com⁩/inspiredandroid⁩/braincup⁩" and replace it with the  from "androidx-master-dev/⁨frameworks⁩/support⁩/ui⁩/⁨ui-material⁩/integration-tests⁩/⁨material-studies⁩/⁨src⁩/main⁩/java⁩/⁨androidx/⁨ui⁩/⁨material⁩/studies⁩/MainActivity.kt"

Also copy over the following drawables: "ic_waiting.xml, ic_searching.xml, ic_success.xml"

Open the AndroidManifest.xml and add ```android:theme="@android:style/Theme.Light.NoTitleBar"``` to the MainActivity definition.

Open "androidx-master-dev⁩/frameworks⁩/⁨support⁩/⁨ui⁩/⁨ui-platform⁩/src⁩/main⁩/⁨java⁩/androidx⁩/ui⁩/core⁩/input/RecordingInoutConnection.kt" and replace the requestCursorUpdates function with the following:
```
    override fun requestCursorUpdates(cursorUpdateMode: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "requestCursorUpdates($cursorUpdateMode)") }
        return true
    }
```

