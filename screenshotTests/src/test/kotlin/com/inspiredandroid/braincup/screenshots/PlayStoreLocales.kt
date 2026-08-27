package com.inspiredandroid.braincup.screenshots

import java.util.Locale

/**
 * Play store screenshot matrix: Compose resource locale paired with the supply folder name
 * under fastlane/metadata/android/.
 *
 * A resource locale may appear more than once. Play sells a listing per region, so en, es, fr,
 * pt and zh-rTW each back several supply folders that share one set of UI strings and differ
 * only in store copy: the app reads the same as en-US in en-GB, but the listing can be its own.
 * zh-rTW is the Compose/Android resource qualifier for Traditional Chinese, and backs both the
 * Taiwan (zh-TW) and Hong Kong (zh-HK) listings.
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
    arrayOf("en", "en-AU"),
    arrayOf("en", "en-CA"),
    arrayOf("en", "en-GB"),
    arrayOf("en", "en-IN"),
    arrayOf("en", "en-SG"),
    arrayOf("en", "en-ZA"),
    arrayOf("es", "es-ES"),
    arrayOf("es", "es-419"),
    arrayOf("es", "es-US"),
    arrayOf("et", "et"),
    arrayOf("fa", "fa"),
    arrayOf("fi", "fi-FI"),
    arrayOf("fil", "fil"),
    arrayOf("fr", "fr-FR"),
    arrayOf("fr", "fr-CA"),
    arrayOf("gu", "gu"),
    arrayOf("he", "iw-IL"),
    arrayOf("hi", "hi-IN"),
    arrayOf("hr", "hr"),
    arrayOf("hu", "hu-HU"),
    arrayOf("id", "id"),
    arrayOf("is", "is-IS"),
    arrayOf("it", "it-IT"),
    arrayOf("ja", "ja-JP"),
    arrayOf("kn", "kn-IN"),
    arrayOf("ko", "ko-KR"),
    arrayOf("lt", "lt"),
    arrayOf("lv", "lv"),
    arrayOf("ml", "ml-IN"),
    arrayOf("ms", "ms"),
    arrayOf("mr", "mr-IN"),
    arrayOf("nb", "no-NO"),
    arrayOf("nl", "nl-NL"),
    arrayOf("pa", "pa"),
    arrayOf("pl", "pl-PL"),
    arrayOf("pt", "pt-BR"),
    arrayOf("pt", "pt-PT"),
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
    arrayOf("zh-rTW", "zh-HK"),
)
