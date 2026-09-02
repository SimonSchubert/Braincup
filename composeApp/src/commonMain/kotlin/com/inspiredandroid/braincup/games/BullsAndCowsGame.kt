package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.BullsAndCowsGuess
import com.inspiredandroid.braincup.app.BullsAndCowsUiState
import com.inspiredandroid.braincup.app.GameUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

/**
 * Bulls and Cows (Deductive Logic):
 * The system generates a secret 4-digit number with unique digits.
 * The player inputs a guess.
 * "Bulls" represent a correct digit in the correct position.
 * "Cows" represent a correct digit but in the wrong position.
 * The player cracks the code in as few turns as possible.
 *
 * Digits from a guess that scored 0 bulls and 0 cows are known-absent and
 * cannot be entered again (keyboard greys them out).
 */
class BullsAndCowsGame : Game() {
    private var secret = ""
    private val guesses = mutableListOf<BullsAndCowsGuess>()
    private val absentDigits = mutableSetOf<Char>()

    var currentGuess = ""
        private set

    var finished = false
        private set
    var won = false
        private set

    /** Guesses submitted so far; equals the winning try count after a solve. */
    val guessesUsed: Int get() = guesses.size

    override val adaptiveDifficulty: Boolean = false

    init {
        answeredAllCorrect = false
    }

    override fun generateRound() {
        guesses.clear()
        absentDigits.clear()
        currentGuess = ""
        finished = false
        won = false

        // Generate a 4-digit number with unique digits (0-9, leading zeros allowed).
        val digits = (0..9).shuffled()
        secret = digits.take(4).joinToString("")
    }

    /**
     * Set a custom secret (mainly for testing or custom initializations).
     */
    fun setSecretForTesting(customSecret: String) {
        secret = customSecret
    }

    fun typeDigit(digit: Char) {
        if (finished) return
        if (digit !in '0'..'9') return
        if (digit in absentDigits) return
        // Unique digits only — matches classic Bulls & Cows.
        if (digit in currentGuess) return
        if (currentGuess.length < 4) {
            currentGuess += digit
        }
    }

    fun backspace() {
        if (finished) return
        if (currentGuess.isNotEmpty()) {
            currentGuess = currentGuess.dropLast(1)
        }
    }

    /** Remove the digit at [index]; later digits shift left so focus is the next empty slot. */
    fun removeAt(index: Int) {
        if (finished) return
        if (index !in currentGuess.indices) return
        currentGuess = currentGuess.removeRange(index, index + 1)
    }

    fun submitGuess(): Boolean {
        if (finished) return false
        if (currentGuess.length < 4) return false

        val result = evaluate(currentGuess)
        guesses.add(result)

        // 0B + 0C means every digit in this guess is not in the secret.
        if (result.bulls == 0 && result.cows == 0) {
            result.guess.forEach { absentDigits.add(it) }
        }

        if (currentGuess == secret) {
            won = true
            finished = true
        }

        currentGuess = ""
        return true
    }

    fun giveUp() {
        finished = true
        won = false
    }

    fun evaluate(guess: String): BullsAndCowsGuess {
        var bulls = 0
        var cows = 0
        for (i in guess.indices) {
            if (guess[i] == secret[i]) {
                bulls++
            } else if (guess[i] in secret) {
                cows++
            }
        }
        return BullsAndCowsGuess(guess, bulls, cows)
    }

    override fun isCorrect(input: String): Boolean = input == secret

    override fun solution(): String = secret

    override fun toUiState(): GameUiState = BullsAndCowsUiState(
        guesses = guesses.toImmutableList(),
        currentGuess = currentGuess,
        finished = finished,
        won = won,
        secret = if (finished) secret else null,
        absentDigits = absentDigits.toImmutableSet(),
    )
}
