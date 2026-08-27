package com.inspiredandroid.braincup.locale

/**
 * A UI language the app ships translations for.
 *
 * [tag] is a BCP 47 language tag, not a Compose resource qualifier: Traditional Chinese is
 * "zh-TW" here and `values-zh-rTW` on disk. The tag has to survive a round trip through the
 * platform locale APIs and come back out of `Locale.current` as language + region, which is what
 * Compose Resources matches on.
 *
 * [nativeName] is the endonym, the name of the language in that language. Language names are
 * deliberately not translated: someone hunting for their own language recognizes "Deutsch" in
 * every locale, and it keeps 52 names out of strings.xml.
 */
data class AppLanguage(val tag: String, val nativeName: String)

/**
 * Every locale with a `composeApp/src/commonMain/composeResources/values-<locale>/strings.xml`,
 * kept in sync with `androidApp/src/main/res/xml/locales_config.xml` (the canonical list both
 * `scripts/check_localizations.py` and `scripts/check_store_listings.py` read).
 *
 * Ordered for reading, not sorted at runtime: English first, then the Latin-script names
 * alphabetically with diacritics folded, then the other scripts grouped. `sortedBy { nativeName }`
 * would be code-point order, which files "Čeština" after "Tiếng Việt".
 */
val supportedAppLanguages: List<AppLanguage> = listOf(
    AppLanguage("en", "English"),
    // Latin script.
    AppLanguage("id", "Bahasa Indonesia"),
    AppLanguage("ms", "Bahasa Melayu"),
    AppLanguage("ca", "Català"),
    AppLanguage("cs", "Čeština"),
    AppLanguage("da", "Dansk"),
    AppLanguage("de", "Deutsch"),
    AppLanguage("et", "Eesti"),
    AppLanguage("es", "Español"),
    AppLanguage("fil", "Filipino"),
    AppLanguage("fr", "Français"),
    AppLanguage("ga", "Gaeilge"),
    AppLanguage("hr", "Hrvatski"),
    AppLanguage("is", "Íslenska"),
    AppLanguage("it", "Italiano"),
    AppLanguage("lv", "Latviešu"),
    AppLanguage("lt", "Lietuvių"),
    AppLanguage("hu", "Magyar"),
    AppLanguage("nl", "Nederlands"),
    AppLanguage("nb", "Norsk bokmål"),
    AppLanguage("pl", "Polski"),
    AppLanguage("pt", "Português"),
    AppLanguage("ro", "Română"),
    AppLanguage("sk", "Slovenčina"),
    AppLanguage("sl", "Slovenščina"),
    AppLanguage("fi", "Suomi"),
    AppLanguage("sv", "Svenska"),
    AppLanguage("vi", "Tiếng Việt"),
    AppLanguage("tr", "Türkçe"),
    // Greek and Cyrillic.
    AppLanguage("el", "Ελληνικά"),
    AppLanguage("bg", "Български"),
    AppLanguage("ru", "Русский"),
    AppLanguage("sr", "Српски"),
    AppLanguage("uk", "Українська"),
    // Right-to-left.
    AppLanguage("he", "עברית"),
    AppLanguage("ar", "العربية"),
    AppLanguage("fa", "فارسی"),
    AppLanguage("ur", "اردو"),
    // Indic scripts.
    AppLanguage("hi", "हिन्दी"),
    AppLanguage("mr", "मराठी"),
    AppLanguage("bn", "বাংলা"),
    AppLanguage("pa", "ਪੰਜਾਬੀ"),
    AppLanguage("gu", "ગુજરાતી"),
    AppLanguage("ta", "தமிழ்"),
    AppLanguage("te", "తెలుగు"),
    AppLanguage("kn", "ಕನ್ನಡ"),
    AppLanguage("ml", "മലയാളം"),
    AppLanguage("th", "ไทย"),
    // CJK.
    AppLanguage("zh", "简体中文"),
    AppLanguage("zh-TW", "繁體中文"),
    AppLanguage("ja", "日本語"),
    AppLanguage("ko", "한국어"),
)

/** The entry whose [AppLanguage.tag] is [tag], or null for an unknown tag or system default. */
fun appLanguageForTag(tag: String?): AppLanguage? = tag?.let { wanted -> supportedAppLanguages.firstOrNull { it.tag == wanted } }
