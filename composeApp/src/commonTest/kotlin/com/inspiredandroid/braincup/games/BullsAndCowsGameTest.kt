package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.BullsAndCowsUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BullsAndCowsGameTest {

    @Test
    fun testGenerateRoundAndGuessEvaluation() {
        val game = BullsAndCowsGame()
        game.nextRound()
        game.setSecretForTesting("1234")

        // Validating secret initialization
        assertEquals("1234", game.solution())

        // Test digits typing
        game.typeDigit('1')
        game.typeDigit('2')
        game.typeDigit('3')
        game.typeDigit('5')
        assertEquals("1235", game.currentGuess)

        // Type existing digit should be ignored
        game.typeDigit('1')
        assertEquals("1235", game.currentGuess)

        // Test backspace
        game.backspace()
        assertEquals("123", game.currentGuess)

        game.typeDigit('4')
        assertEquals("1234", game.currentGuess)

        // Tap a middle digit slot: remove that digit, rest shifts left
        game.removeAt(1)
        assertEquals("134", game.currentGuess)
        game.typeDigit('2')
        assertEquals("1342", game.currentGuess)
        // Rebuild a full correct guess for the win path below
        game.removeAt(0)
        game.removeAt(0)
        game.removeAt(0)
        game.removeAt(0)
        game.typeDigit('1')
        game.typeDigit('2')
        game.typeDigit('3')
        game.typeDigit('4')
        assertEquals("1234", game.currentGuess)

        // Evaluate guess
        val result = game.evaluate("1234")
        assertEquals(4, result.bulls)
        assertEquals(0, result.cows)

        val result2 = game.evaluate("4321")
        assertEquals(0, result2.bulls)
        assertEquals(4, result2.cows)

        val result3 = game.evaluate("1356")
        assertEquals(1, result3.bulls)
        assertEquals(1, result3.cows)

        // Submit guess
        assertTrue(game.submitGuess())
        assertTrue(game.finished)
        assertTrue(game.won)

        val uiState = game.toUiState() as BullsAndCowsUiState
        assertEquals(1, uiState.guesses.size)
        assertEquals("1234", uiState.guesses[0].guess)
        assertEquals(4, uiState.guesses[0].bulls)
        assertEquals(0, uiState.guesses[0].cows)
    }
}
