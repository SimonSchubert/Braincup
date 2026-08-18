package com.inspiredandroid.braincup.screenshots

/**
 * App Store screenshots can only be uploaded for locales that already exist as an App Store version
 * localization, and creating one requires a full localized listing (name, description, keywords).
 * Braincup's App Store listing is English only, so this stays a single entry. Adding a locale here
 * (Compose resource locale, App Store locale code) is all the screenshot pipeline needs once the
 * listing itself is localized.
 */
internal fun appStoreLocales(): List<Array<String>> = listOf(
    arrayOf("en", "en-US"),
)
