package com.inspiredandroid.braincup.screenshots

import java.util.Locale

/**
 * Play store screenshot matrix: Compose resource locale paired with the supply folder name
 * under fastlane/metadata/android/.
 *
 * es-419 reuses the Spanish UI strings; only the store listing copy differs from es-ES.
 * zh-rTW is the Compose/Android resource qualifier for Traditional Chinese (Taiwan).
 *
 * The supply folder name has to be a code Play accepts, and Play is inconsistent about region
 * suffixes: it takes de-DE and cs-CZ but rejects bg-BG and sk-SK in favour of bare bg and sk.
 * scripts/check_store_listings.py holds the accepted set and fails the build on a wrong one.
 * Irish is absent because Play publishes no Irish store listing at all; its copy is parked in
 * fastlane/metadata/android-unsupported/.
 */
internal fun javaLocale(resourceLocale: String): Locale {
    val tag = resourceLocale.replace("-r", "-").replace('_', '-')
    return Locale.forLanguageTag(tag)
}

internal fun playStoreLocales(): List<Array<String>> = listOf(
    arrayOf("ar", "ar"),
    arrayOf("bg", "bg"),
    arrayOf("bn", "bn-BD"),
    arrayOf("ca", "ca"),
    arrayOf("cs", "cs-CZ"),
    arrayOf("da", "da-DK"),
    arrayOf("de", "de-DE"),
    arrayOf("el", "el-GR"),
    arrayOf("en", "en-US"),
    arrayOf("es", "es-ES"),
    arrayOf("es", "es-419"),
    arrayOf("et", "et"),
    arrayOf("fa", "fa"),
    arrayOf("fi", "fi-FI"),
    arrayOf("fil", "fil"),
    arrayOf("fr", "fr-FR"),
    arrayOf("gu", "gu"),
    arrayOf("he", "iw-IL"),
    arrayOf("hi", "hi-IN"),
    arrayOf("hr", "hr"),
    arrayOf("hu", "hu-HU"),
    arrayOf("id", "id"),
    arrayOf("is", "is-IS"),
    arrayOf("it", "it-IT"),
    arrayOf("ja", "ja-JP"),
    arrayOf("ko", "ko-KR"),
    arrayOf("lt", "lt"),
    arrayOf("lv", "lv"),
    arrayOf("ms", "ms"),
    arrayOf("mr", "mr-IN"),
    arrayOf("nb", "no-NO"),
    arrayOf("nl", "nl-NL"),
    arrayOf("pl", "pl-PL"),
    arrayOf("pt", "pt-BR"),
    arrayOf("ro", "ro"),
    arrayOf("ru", "ru-RU"),
    arrayOf("sk", "sk"),
    arrayOf("sl", "sl"),
    arrayOf("sr", "sr"),
    arrayOf("sv", "sv-SE"),
    arrayOf("ta", "ta-IN"),
    arrayOf("te", "te-IN"),
    arrayOf("th", "th"),
    arrayOf("tr", "tr-TR"),
    arrayOf("uk", "uk"),
    arrayOf("ur", "ur"),
    arrayOf("vi", "vi"),
    arrayOf("zh", "zh-CN"),
    arrayOf("zh-rTW", "zh-TW"),
)
