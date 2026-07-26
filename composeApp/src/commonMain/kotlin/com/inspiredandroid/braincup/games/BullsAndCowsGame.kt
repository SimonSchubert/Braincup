package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.BullsAndCowsGuess
import com.inspiredandroid.braincup.app.BullsAndCowsUiState
import com.inspiredandroid.braincup.app.GameUiState
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

/**
 * Bulls and Cows (Deductive Logic):
 * The system generates a secret 4-digit number with unique digits.
 * The player inputs a guess.
 * "Bulls" represent a correct digit in the correct position.
 * "Cows" represent a correct digit but in the wrong position.
 * The player cracks the code in as few turns as possible.
 */
class BullsAndCowsGame : Game() {
    private var secret = ""
    private val guesses = mutableListOf<BullsAndCowsGuess>()
    var currentGuess = ""
        private set

    var finished = false
        private set
    var won = false
        private set

    override val adaptiveDifficulty: Boolean = false

    init {
        answeredAllCorrect = false
    }

    override fun generateRound() {
        guesses.clear()
        currentGuess = ""
        finished = false
        won = false

        // Generate a 4-digit number with unique digits.
        // It's standard for Bulls & Cows to allow unique digits from 0-9.
        // If we want to allow leading zeros, we can just pick from 0..9.
        // Let's generate 4 unique digits.
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
        // Standard Bulls and Cows allows any digits, but since we are breaking a unique digits code,
        // let's check if the player already inputted that digit. It's helpful if we enforce or at least
        // allow typing unique digits. Let's allow unique digits to stay true to the deduction constraint.
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

    fun submitGuess(): Boolean {
        if (finished) return false
        if (currentGuess.length < 4) return false

        val result = evaluate(currentGuess)
        guesses.add(result)

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

    override fun isCorrect(input: String): Boolean {
        return input == secret
    }

    override fun solution(): String = secret

    override fun hint(): String? = null

    override fun toUiState(): GameUiState {
        return BullsAndCowsUiState(
            guesses = guesses.toImmutableList(),
            currentGuess = currentGuess,
            finished = finished,
            won = won,
            secret = if (finished) secret else null,
        )
    }
}
