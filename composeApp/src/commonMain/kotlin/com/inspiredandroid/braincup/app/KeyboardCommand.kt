package com.inspiredandroid.braincup.app

/**
 * The guess-a-secret boards (Wordle, Bulls & Cows) drive one on-screen keyboard through the same
 * `onAnswer(String)` channel as everything else: ENTER submits the current guess, DELETE
 * backspaces, [clearAt] wipes the guess from a tile, and any other input is the single character
 * that was typed.
 *
 * Each sentinel starts with U+0001 so it can never collide with something the player typed,
 * whatever alphabet the language pack uses.
 */
internal object KeyboardCommand {

    const val ENTER = "\u0001ENTER"
    const val DELETE = "\u0001DEL"

    private const val CLEAR_PREFIX = "\u0001CLEAR"

    fun clearAt(index: Int): String = "$CLEAR_PREFIX$index"

    /** The tile index of a clear command, or null when [input] is not one. */
    fun clearIndexOf(input: String): Int? = if (input.startsWith(CLEAR_PREFIX)) input.removePrefix(CLEAR_PREFIX).toIntOrNull() else null
}
