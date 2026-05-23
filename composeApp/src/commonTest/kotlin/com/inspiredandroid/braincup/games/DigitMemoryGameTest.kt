package com.inspiredandroid.braincup.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DigitMemoryGameTest {

    @Test
    fun sequenceLengthGrowsEachRound() {
        val game = DigitMemoryGame()
        game.nextRound()
        assertEquals(1, game.round)
        assertEquals(4, game.sequence.length)

        game.nextRound()
        assertEquals(2, game.round)
        assertEquals(5, game.sequence.length)

        game.nextRound()
        assertEquals(6, game.sequence.length)
    }

    @Test
    fun newRoundStartsInShowingPhase() {
        val game = DigitMemoryGame()
        game.nextRound()
        assertEquals(DigitMemoryGame.Phase.SHOWING, game.phase)
        assertNull(game.recallResult)
        assertNull(game.revealedMathAnswer)
    }

    @Test
    fun correctMathAdvancesWithoutReveal() {
        val game = DigitMemoryGame()
        game.nextRound()
        assertTrue(game.submitMath(game.problemAnswer))
        assertNull(game.revealedMathAnswer)

        game.advanceToRecall()
        assertEquals(DigitMemoryGame.Phase.RECALL, game.phase)
        assertNull(game.revealedMathAnswer)
    }

    @Test
    fun wrongMathForfeitsRoundAndExposesAnswer() {
        val game = DigitMemoryGame()
        game.nextRound()
        val wrong = (game.problemAnswer.toInt() + 1).toString()
        assertFalse(game.submitMath(wrong))
        assertEquals(game.problemAnswer, game.revealedMathAnswer)
        assertFalse(game.answeredAllCorrect)
    }

    @Test
    fun repeatRoundKeepsSameLengthWithoutAdvancing() {
        val game = DigitMemoryGame()
        game.nextRound()
        game.nextRound() // round 2, length 5
        assertEquals(2, game.round)
        assertEquals(5, game.sequence.length)

        game.repeatRound()
        // Same difficulty: round counter unchanged and length preserved.
        assertEquals(2, game.round)
        assertEquals(5, game.sequence.length)
        assertEquals(DigitMemoryGame.Phase.SHOWING, game.phase)
        // A regenerated round resets the per-round transient state.
        assertNull(game.recallResult)
        assertNull(game.revealedMathAnswer)
    }

    @Test
    fun correctRecallReportsCorrect() {
        val game = DigitMemoryGame()
        game.nextRound()
        game.advanceToRecall()
        assertTrue(game.submitRecall(game.sequence))
        assertEquals(DigitMemoryGame.RecallResult.CORRECT, game.recallResult)
        assertTrue(game.answeredAllCorrect)
    }

    @Test
    fun wrongRecallReportsWrongAndClearsPerfectFlag() {
        val game = DigitMemoryGame()
        game.nextRound()
        game.advanceToRecall()
        assertFalse(game.submitRecall(game.sequence + "9"))
        assertEquals(DigitMemoryGame.RecallResult.WRONG, game.recallResult)
        assertFalse(game.answeredAllCorrect)
    }

    @Test
    fun recallComparesAsStringSoLeadingZerosMatter() {
        // isCorrect is a pure string compare, so a numerically-equal but differently-padded
        // input must not be accepted.
        val game = DigitMemoryGame()
        game.nextRound()
        assertEquals(game.sequence, game.solution())
        if (game.sequence.startsWith("0")) {
            assertFalse(game.isCorrect(game.sequence.trimStart('0')))
        }
    }

    @Test
    fun generatedProblemAnswerIsValidAndNonNegative() {
        // Cover the randomness across many rounds and game instances.
        repeat(300) {
            val game = DigitMemoryGame()
            repeat(8) {
                game.nextRound()
                val parts = game.problem.split(" ")
                assertEquals(3, parts.size, "Unexpected problem format: ${game.problem}")
                val a = parts[0].toInt()
                val b = parts[2].toInt()
                val expected = when (parts[1]) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    else -> error("Unexpected operator in ${game.problem}")
                }
                assertEquals(expected.toString(), game.problemAnswer)
                assertTrue(expected >= 0, "Problem produced a negative answer: ${game.problem}")
            }
        }
    }

    @Test
    fun digitsAreInRange() {
        repeat(100) {
            val game = DigitMemoryGame()
            game.nextRound()
            assertTrue(game.sequence.isNotEmpty() && game.sequence.all { it in '0'..'9' })
        }
    }

    @Test
    fun adaptiveDifficultyIsDisabled() {
        assertFalse(DigitMemoryGame().adaptiveDifficulty)
    }
}
