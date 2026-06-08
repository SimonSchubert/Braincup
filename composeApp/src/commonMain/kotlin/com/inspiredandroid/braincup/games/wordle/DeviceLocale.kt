package com.inspiredandroid.braincup.games.wordle

/**
 * The current device UI language as a language tag (e.g. "en", "de", or "en-US"). Used to pick the
 * Wordle word list / keyboard and to decide whether the game is offered at all. Mirrors the
 * `audio/AudioPlayer` expect/actual pattern.
 */
expect fun deviceLanguageTag(): String
