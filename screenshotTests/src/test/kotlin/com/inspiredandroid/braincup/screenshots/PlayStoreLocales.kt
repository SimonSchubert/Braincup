package com.inspiredandroid.braincup.screenshots

import java.util.Locale

/**
 * Play store screenshot matrix: Compose resource locale paired with the supply folder name
 * under fastlane/metadata/android/.
 *
 * es-419 reuses the Spanish UI strings; only the store listing copy differs from es-ES.
 * zh-rTW is the Compose/Android resource qualifier for Traditional Chinese (Taiwan).
 */
internal fun javaLocale(resourceLocale: String): Locale {
    val tag = resourceLocale.replace("-r", "-").replace('_', '-')
    return Locale.forLanguageTag(tag)
}

internal fun playStoreLocales(): List<Array<String>> = listOf(
    arrayOf("ar", "ar"),
    arrayOf("bg", "bg-BG"),
    arrayOf("bn", "bn-BD"),
    arrayOf("ca", "ca"),
    arrayOf("cs", "cs-CZ"),
    arrayOf("da", "da-DK"),
    arrayOf("de", "de-DE"),
    arrayOf("el", "el-GR"),
    arrayOf("en", "en-US"),
    arrayOf("es", "es-ES"),
    arrayOf("es", "es-419"),
    arrayOf("et", "et-EE"),
    arrayOf("fa", "fa"),
    arrayOf("fi", "fi-FI"),
    arrayOf("fil", "fil"),
    arrayOf("fr", "fr-FR"),
    arrayOf("ga", "ga-IE"),
    arrayOf("he", "iw-IL"),
    arrayOf("hi", "hi-IN"),
    arrayOf("hr", "hr-HR"),
    arrayOf("hu", "hu-HU"),
    arrayOf("id", "id"),
    arrayOf("it", "it-IT"),
    arrayOf("ja", "ja-JP"),
    arrayOf("ko", "ko-KR"),
    arrayOf("lt", "lt-LT"),
    arrayOf("lv", "lv-LV"),
    arrayOf("ms", "ms"),
    arrayOf("nb", "no-NO"),
    arrayOf("nl", "nl-NL"),
    arrayOf("pl", "pl-PL"),
    arrayOf("pt", "pt-BR"),
    arrayOf("ro", "ro-RO"),
    arrayOf("ru", "ru-RU"),
    arrayOf("sk", "sk-SK"),
    arrayOf("sl", "sl-SI"),
    arrayOf("sr", "sr-RS"),
    arrayOf("sv", "sv-SE"),
    arrayOf("th", "th"),
    arrayOf("tr", "tr-TR"),
    arrayOf("uk", "uk"),
    arrayOf("ur", "ur"),
    arrayOf("vi", "vi"),
    arrayOf("zh", "zh-CN"),
    arrayOf("zh-rTW", "zh-TW"),
)
