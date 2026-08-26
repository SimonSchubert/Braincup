# Listing copy for languages Play cannot publish

Play Console's store-listing language list is shorter than the app's. A locale here is
translated and ships in the app, but `edits.listings.update` answers
`The requested language is not currently supported` for every code it could plausibly use,
so supply cannot upload it. The folders sit outside `fastlane/metadata/android/` because
supply uploads every locale folder it finds there and one unsupported code aborts the whole
lane part-way through the locale list.

| Locale | Probed and rejected |
| ------ | ------------------- |
| `ga-IE` | `ga`, `ga-IE` |

If Play adds one of these, move the folder back under `fastlane/metadata/android/` with the
accepted code as its name, restore its entry in `PlayStoreLocales.kt` and drop the locale
from `PLAY_UNSUPPORTED_LANGUAGES` in `scripts/check_store_listings.py`. The screenshots are
not kept here; `./gradlew :screenshotTests:generateStoreScreenshots` renders them from the
`PlayStoreLocales.kt` entry.
