[![GitHub](https://img.shields.io/github/license/SimonSchubert/Braincup.svg)](https://www.apache.org/licenses/LICENSE-2.0)

<img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/play_store_feature.png" width="350">

# Braincup
Train your math skills, memory and focus.
* Important: all targets except Android are currently under construction and not able to compile *

## Screenshots

### Mobile (Android, iOS)
<img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_android_01.png" width="200"> <img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_android_02.png" width="200">

### Desktop
<img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_mac_01.png" width="400"> <img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_mac_02.png" width="400">

### Web
<img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_web_01.png" width="400"> <img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/screen_web_02.png" width="400">

## Install

### macOS

#### Homebrew
`brew tap SimonSchubert/braincup && brew install SimonSchubert/braincup/braincup`

### Web
https://braincup.app

### Android
[<img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/play_store.png" width="200">](https://play.google.com/store/apps/details?id=com.inspiredandroid.braincup)

### iOS
[<img src="https://raw.githubusercontent.com/SimonSchubert/Braincup/master/media/app_store.png" width="200">](https://apps.apple.com/us/app/braincup/id1483376887#?platform=iphone)

## Goal
Build an open source math, memory and focus improvement app. The app itself consists of a bunch of small games. Right now I try to keep the games as simple as possible to allow the implementation for the terminal client.

## Doing
- Android client
- iOS client
- macOS terminal client
- Linux terminal client
- JS web client
- Gamification

## Todo
- Linux gui client
- Windows terminal client
- Windows gui client
- macOS gui client
- High score server
- High score screen redesign(see design folder in root)

## Contribution
Very welcome.

## Gradle build tasks
- Build and install Android app = :app:installDebug
- Build and run mac terminal client = runDebugExecutableCli
- Build and run js web client = jsWebDebug, jsWebRelease
- Run unit test on android target = androidTest
- Build and copy ios framework to 'ios' folder = :shared:copyFramework

## Acknowledgments ♥
https://kotlinlang.org, https://github.com/russhwolf/multiplatform-settings, https://ktor.io, https://soywiz.com, @cssinate for design suggestions and everyone else I missed to mention.