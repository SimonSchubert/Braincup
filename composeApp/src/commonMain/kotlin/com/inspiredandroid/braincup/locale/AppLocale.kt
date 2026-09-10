package com.inspiredandroid.braincup.locale

import androidx.compose.runtime.staticCompositionLocalOf
import com.inspiredandroid.braincup.games.wordle.deviceLanguageTag

/**
 * The app's language override.
 *
 * Compose Multiplatform 1.12 has no public API for the resource locale: `LocalComposeEnvironment`
 * and the `ResourceEnvironment` constructor are both `internal`, so `stringResource` can only be
 * steered through what `androidx.compose.ui.text.intl.Locale.current` returns, which every target
 * reads from a process-wide platform default. [apply] mutates that default; App.kt re-keys the
 * composition so the new value is picked up, since `Locale.current` is not snapshot state.
 */
object AppLocale {
    private var override: String? = null

    /**
     * The language the app is rendering in: the user's pick, else the platform default. For code
     * outside composition, which cannot read `Locale.current`.
     */
    fun currentTag(): String = override ?: deviceLanguageTag()

    /** [tag] is a BCP 47 tag from [supportedAppLanguages], or null to follow the platform again. */
    fun apply(tag: String?) {
        override = tag
        setPlatformLanguage(tag)
    }
}

/**
 * The picked language, published to the composition purely to drive recomposition.
 *
 * `Locale.current` is not snapshot state, so changing the platform default cannot invalidate the
 * `stringResource` call sites on its own. This local is deliberately *static*: changing a static
 * CompositionLocal disables skipping for its whole subtree, so every call site re-runs and re-reads
 * the locale. `key()` would also do that, but by throwing the subtree away along with everything it
 * remembers - list scroll positions, open dialogs, a game in progress.
 */
val LocalAppLanguage = staticCompositionLocalOf<String?> { null }

/** Points the platform locale default at [tag]; null restores the language the app started with. */
internal expect fun setPlatformLanguage(tag: String?)

// Compose derives the layout direction from the platform configuration (on Android the Activity's
// Configuration), not from Locale.current, so an in-app language change would otherwise leave the
// previous language's mirroring behind: picking German on an Arabic device kept a right-to-left
// layout. App.kt provides this instead. Matches the RTL set Compose itself uses.
private val rightToLeftLanguages = setOf("ar", "fa", "he", "iw", "ji", "ur", "yi")

/** True when [language] (an ISO 639 code, as `Locale.current.language` reports it) reads right to left. */
fun isRightToLeftLanguage(language: String): Boolean = language in rightToLeftLanguages

/**
 * Languages that write a number's fractional part after a comma.
 *
 * A hand-kept table rather than a platform lookup, for the same reason [rightToLeftLanguages] is
 * one: the app ships a fixed list of languages, so the answer is knowable here and testable
 * against `supportedAppLanguages` without four `actual`s and a mock locale per target.
 *
 * Arabic and Persian are deliberately **not** in it. ICU gives them U+066B, the Arabic decimal
 * separator, which belongs with Arabic-Indic digits; every number in this app is drawn in Western
 * digits in the Rubik number face, which does not carry that glyph, so it would come out as tofu
 * beside a Western numeral. A full stop is what those locales get with Western digits anyway.
 */
private val decimalCommaLanguages = setOf(
    "bg", "ca", "cs", "da", "de", "el", "es", "et", "fi", "fr", "hr", "hu", "id", "is", "it",
    "lt", "lv", "nb", "nl", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "tr", "uk", "vi",
)

/**
 * What [language] writes between a number's whole part and its fraction.
 *
 * This is a correctness question rather than a cosmetic one in a maths app: a German learner
 * reading "0.35" on a formula card is reading a number written the way they are taught not to.
 */
fun decimalSeparatorFor(language: String): Char = if (language in decimalCommaLanguages) ',' else '.'
