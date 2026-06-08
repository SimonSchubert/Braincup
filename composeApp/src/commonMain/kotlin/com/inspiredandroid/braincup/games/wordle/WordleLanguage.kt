package com.inspiredandroid.braincup.games.wordle

/**
 * Per-language Wordle configuration.
 *
 * A Wordle word list cannot be translated: every language needs its own curated words, its own
 * alphabet/keyboard, and its own accent policy. That policy is baked into [keyboardRows] and the
 * matching word-list file (both authored together), so the runtime stays script-agnostic: because
 * the only way to enter a guess is by tapping our own keys, a guess is assembled from these exact
 * canonical UPPERCASE letters and comparison against the (identically normalized) list is plain
 * equality, with no locale-aware case folding needed at compare time.
 */
data class WordleLanguage(
    /** ISO-639 language code, e.g. "en", "de". */
    val tag: String,
    val wordLength: Int,
    /** On-screen keyboard layout: each string is one row of canonical UPPERCASE letters. */
    val keyboardRows: List<String>,
) {
    /** Secret-answer candidates: one UPPERCASE accent-normalized word per line. */
    val answersPath: String get() = "files/wordle/answers_$tag.txt"

    /** Allowed guesses; includes every [answersPath] entry plus additional valid words. */
    val guessesPath: String get() = "files/wordle/guesses_$tag.txt"

    /** Every letter that can appear in a word / on the keyboard. */
    val alphabet: Set<Char> = keyboardRows.joinToString("").toSet()
}

/**
 * Registry of the languages Wordle supports and the rule for mapping a device language to one.
 *
 * First iteration ships en, de, fr, nl. Adding another Latin/Cyrillic language later is just a
 * word-list file plus one entry here. [resolve] is strict: any tag that is not explicitly
 * supported returns null, which hides the game (the menu and [GameController] both consult it).
 */
object WordleLanguages {
    // Standard ASCII QWERTY rows (English, Dutch).
    private val QWERTY = listOf("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM")

    private val supported: Map<String, WordleLanguage> = listOf(
        WordleLanguage(tag = "en", wordLength = 5, keyboardRows = QWERTY),
        WordleLanguage(tag = "nl", wordLength = 5, keyboardRows = QWERTY),
        // German: QWERTZ + umlauts as their own keys. ß is normalized to SS in the word list,
        // so there is no ß key.
        WordleLanguage(
            tag = "de",
            wordLength = 5,
            keyboardRows = listOf("QWERTZUIOPÜ", "ASDFGHJKLÖÄ", "YXCVBNM"),
        ),
        // French: AZERTY, accents stripped to their base letter in the word list (é→E, ç→C, …),
        // so the board is 26 ASCII keys.
        WordleLanguage(
            tag = "fr",
            wordLength = 5,
            keyboardRows = listOf("AZERTYUIOP", "QSDFGHJKLM", "WXCVBN"),
        ),
    ).associateBy { it.tag }

    /** Config for a device language tag (e.g. "en-US"), or null when Wordle is unavailable for it. */
    fun resolve(tag: String): WordleLanguage? {
        val language = normalizeLanguageTag(tag)
        if (language.isBlank()) return null
        return supported[language]
    }

    /** Config for the current device language, or null when unavailable. */
    fun current(): WordleLanguage? = resolve(deviceLanguageTag())

    /** Whether Wordle is available for the current device language (drives the menu filter). */
    fun isAvailable(): Boolean = current() != null
}

/** Reduce a device language tag (e.g. "en-US", "de_DE") to its bare ISO-639 code ("en", "de"). */
internal fun normalizeLanguageTag(tag: String): String = tag.lowercase().substringBefore('-').substringBefore('_')
