package com.inspiredandroid.braincup.games.wordle

import com.inspiredandroid.braincup.app.GameUiState
import com.inspiredandroid.braincup.app.WordleLetter
import com.inspiredandroid.braincup.app.WordleLetterState
import com.inspiredandroid.braincup.app.WordleUiState
import com.inspiredandroid.braincup.games.Game
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

/**
 * Classic single-word Wordle: one secret [target], up to [MAX_GUESSES] guesses, then the game ends.
 *
 * Input arrives one action at a time via the [GameController][com.inspiredandroid.braincup.app.GameController]
 * (letters, backspace, submit) rather than the one-shot [isCorrect] path, much like LightsOut. Letter
 * matching is plain equality on the language's canonical UPPERCASE letters (see [WordleLanguage]).
 */
class WordleGame(
    private val language: WordleLanguage,
    /** The secret answer, already UPPERCASE and accent-normalized to [language]'s alphabet. */
    private val target: String,
    /** Allowed guesses; must include [target]. */
    private val validWords: Set<String>,
) : Game() {

    val wordLength: Int = language.wordLength
    val keyboardRows: List<String> = language.keyboardRows

    private val submitted = mutableListOf<List<WordleLetter>>()
    private val current = StringBuilder()
    private val keyStates = mutableMapOf<Char, WordleLetterState>()
    private var notEnoughLetters = false
    private var notInWordList = false

    var solved = false
        private set
    var finished = false
        private set

    // Wordle is a single fixed puzzle: no adaptive difficulty / round persistence, and no
    // per-round "no mistakes" bonus (that finish-screen message wouldn't make sense here).
    override val adaptiveDifficulty: Boolean = false

    init {
        answeredAllCorrect = false
    }

    /** Guesses submitted so far; equals the winning guess number after a solve. */
    val guessesUsed: Int get() = submitted.size

    /** Points for the standard medal/Finish flow: 7 - guesses on a win, 0 otherwise. */
    val score: Int get() = if (solved) MAX_GUESSES + 1 - guessesUsed else 0

    override fun generateRound() {
        // No-op: the single puzzle is built from the constructor target.
    }

    /**
     * Append a letter to the current row. When the row is full, the guess is submitted
     * automatically. Returns true if a guess was submitted (always accepted when full).
     */
    fun typeLetter(letter: Char): Boolean {
        if (finished) return false
        clearInputFeedback()
        val c = letter.uppercaseChar()
        if (c !in language.alphabet || current.length >= wordLength) return false
        current.append(c)
        return if (current.length >= wordLength) submitGuess() else false
    }

    fun backspace() {
        if (finished) return
        clearInputFeedback()
        if (current.isNotEmpty()) current.deleteAt(current.length - 1)
    }

    /** Clear the letter at [index] and any letters after it in the current row. */
    fun clearFrom(index: Int) {
        if (finished) return
        clearInputFeedback()
        if (index in 0 until current.length) {
            current.deleteRange(index, current.length)
        }
    }

    /** Submit the current row. Returns true if the guess was accepted and evaluated. */
    fun submitGuess(): Boolean {
        if (finished) return false
        clearInputFeedback()
        if (current.length < wordLength) {
            notEnoughLetters = true
            return false
        }
        val guess = current.toString()
        if (guess !in validWords) {
            notInWordList = true
            current.clear()
            return false
        }
        val evaluated = evaluate(guess, target)
        submitted.add(evaluated)
        evaluated.forEach { keyStates[it.char] = bestOf(keyStates[it.char], it.state) }
        current.clear()
        when {
            guess == target -> {
                solved = true
                finished = true
            }
            submitted.size >= MAX_GUESSES -> finished = true
        }
        return true
    }

    /** End the game without solving (reveals the answer). */
    fun giveUp() {
        clearInputFeedback()
        finished = true
    }

    override fun isCorrect(input: String): Boolean = input.uppercase() == target

    override fun solution(): String = target

    override fun hint(): String? = null

    override fun toUiState(): GameUiState {
        val rows = ArrayList<List<WordleLetter>>(MAX_GUESSES)
        rows.addAll(submitted)
        if (!finished && rows.size < MAX_GUESSES) {
            rows.add(
                List(wordLength) { i ->
                    if (i < current.length) {
                        WordleLetter(current[i], WordleLetterState.PENDING)
                    } else {
                        EMPTY_LETTER
                    }
                },
            )
        }
        while (rows.size < MAX_GUESSES) {
            rows.add(List(wordLength) { EMPTY_LETTER })
        }
        return WordleUiState(
            rows = rows.map { it.toImmutableList() }.toImmutableList(),
            keyboardRows = keyboardRows.toImmutableList(),
            keyStates = keyStates.toImmutableMap(),
            wordLength = wordLength,
            solved = solved,
            finished = finished,
            answer = if (finished) target else null,
            notEnoughLetters = notEnoughLetters,
            notInWordList = notInWordList,
        )
    }

    private fun clearInputFeedback() {
        notEnoughLetters = false
        notInWordList = false
    }

    companion object {
        const val MAX_GUESSES = 6
        private val EMPTY_LETTER = WordleLetter(' ', WordleLetterState.EMPTY)

        /**
         * Classic Wordle two-pass coloring. Pass 1 marks exact-position matches CORRECT and
         * consumes those target letters; pass 2 marks a letter PRESENT only while an unconsumed
         * instance of it remains, otherwise ABSENT. The consumption is what makes duplicate letters
         * behave correctly (e.g. guess ALLOY vs target LOYAL: only the matched/remaining Ls light up).
         */
        fun evaluate(guess: String, target: String): List<WordleLetter> {
            val states = arrayOfNulls<WordleLetterState>(guess.length)
            val remaining = HashMap<Char, Int>()
            for (c in target) remaining[c] = (remaining[c] ?: 0) + 1

            for (i in guess.indices) {
                if (i < target.length && guess[i] == target[i]) {
                    states[i] = WordleLetterState.CORRECT
                    remaining[guess[i]] = (remaining[guess[i]] ?: 0) - 1
                }
            }
            for (i in guess.indices) {
                if (states[i] != null) continue
                val c = guess[i]
                val left = remaining[c] ?: 0
                if (left > 0) {
                    states[i] = WordleLetterState.PRESENT
                    remaining[c] = left - 1
                } else {
                    states[i] = WordleLetterState.ABSENT
                }
            }
            return guess.mapIndexed { i, c -> WordleLetter(c, states[i]!!) }
        }

        /** Keyboard key precedence so a letter never downgrades: CORRECT > PRESENT > ABSENT. */
        private fun bestOf(existing: WordleLetterState?, incoming: WordleLetterState): WordleLetterState {
            if (existing == null) return incoming
            return if (rank(incoming) > rank(existing)) incoming else existing
        }

        private fun rank(state: WordleLetterState): Int = when (state) {
            WordleLetterState.CORRECT -> 3
            WordleLetterState.PRESENT -> 2
            WordleLetterState.ABSENT -> 1
            WordleLetterState.EMPTY, WordleLetterState.PENDING -> 0
        }
    }
}
