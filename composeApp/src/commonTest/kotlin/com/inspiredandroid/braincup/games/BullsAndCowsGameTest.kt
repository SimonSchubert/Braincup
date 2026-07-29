package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.BullsAndCowsUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        assertEquals(1, game.guessesUsed)

        val uiState = game.toUiState() as BullsAndCowsUiState
        assertEquals(1, uiState.guesses.size)
        assertEquals("1234", uiState.guesses[0].guess)
        assertEquals(4, uiState.guesses[0].bulls)
        assertEquals(0, uiState.guesses[0].cows)
        assertEquals("1234", uiState.secret)
    }

    @Test
    fun zeroFeedbackMarksDigitsAbsentAndBlocksTyping() {
        val game = BullsAndCowsGame()
        game.nextRound()
        game.setSecretForTesting("1234")

        game.typeDigit('5')
        game.typeDigit('6')
        game.typeDigit('7')
        game.typeDigit('8')
        assertTrue(game.submitGuess())
        assertFalse(game.finished)

        val uiState = game.toUiState() as BullsAndCowsUiState
        assertEquals(setOf('5', '6', '7', '8'), uiState.absentDigits)

        // Absent digits cannot be re-typed
        game.typeDigit('5')
        assertEquals("", game.currentGuess)
        game.typeDigit('9')
        game.typeDigit('0')
        game.typeDigit('1')
        game.typeDigit('2')
        assertEquals("9012", game.currentGuess)
    }

    @Test
    fun giveUpRevealsSecretWithoutWin() {
        val game = BullsAndCowsGame()
        game.nextRound()
        game.setSecretForTesting("9876")

        game.typeDigit('1')
        game.typeDigit('2')
        game.typeDigit('3')
        game.typeDigit('4')
        assertTrue(game.submitGuess())

        game.giveUp()
        assertTrue(game.finished)
        assertFalse(game.won)

        val uiState = game.toUiState() as BullsAndCowsUiState
        assertEquals("9876", uiState.secret)
        assertEquals(1, uiState.guesses.size)
    }

    @Test
    fun partialFeedbackDoesNotMarkDigitsAbsent() {
        val game = BullsAndCowsGame()
        game.nextRound()
        game.setSecretForTesting("1234")

        game.typeDigit('1')
        game.typeDigit('5')
        game.typeDigit('6')
        game.typeDigit('7')
        assertTrue(game.submitGuess())

        val uiState = game.toUiState() as BullsAndCowsUiState
        assertTrue(uiState.absentDigits.isEmpty())
        // Digit 5 still allowed even though it was a miss (only 0B+0C eliminates)
        game.typeDigit('5')
        assertEquals("5", game.currentGuess)
    }
}
