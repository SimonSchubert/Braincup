package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Operator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MissingOperatorsGameTest {

    @Test
    fun testPrecedenceEvaluation() {
        val game = MissingOperatorsGame()

        // 12 / 4 + 2 = 3 + 2 = 5
        assertEquals(5, game.evaluateTokens(listOf(12, 4, 2), listOf(Operator.DIVIDE, Operator.PLUS)))

        // 12 + 4 / 2 = 12 + 2 = 14
        assertEquals(14, game.evaluateTokens(listOf(12, 4, 2), listOf(Operator.PLUS, Operator.DIVIDE)))

        // 3 * 4 - 2 = 12 - 2 = 10
        assertEquals(10, game.evaluateTokens(listOf(3, 4, 2), listOf(Operator.MULTIPLY, Operator.MINUS)))

        // 5 - 2 * 2 = 5 - 4 = 1
        assertEquals(1, game.evaluateTokens(listOf(5, 2, 2), listOf(Operator.MINUS, Operator.MULTIPLY)))
    }

    @Test
    fun testCleanDivisionValidation() {
        val game = MissingOperatorsGame()

        // 12 / 5 is not clean (12 % 5 != 0) -> should return null
        assertEquals(null, game.evaluateTokens(listOf(12, 5, 2), listOf(Operator.DIVIDE, Operator.PLUS)))

        // Division by zero should return null
        assertEquals(null, game.evaluateTokens(listOf(12, 0), listOf(Operator.DIVIDE)))
    }

    @Test
    fun testNegativeResultValidation() {
        val game = MissingOperatorsGame()

        // 2 - 5 = -3 (negative intermediate result) -> should return null
        assertEquals(null, game.evaluateTokens(listOf(2, 5), listOf(Operator.MINUS)))
    }

    @Test
    fun testRoundGeneration() {
        val game = MissingOperatorsGame()

        // Verify game scales correctly
        // Round 1-2 should only have 3 numbers, and only PLUS and MINUS operators
        game.round = 0
        game.nextRound()
        assertEquals(3, game.numbers.size)
        assertTrue(game.correctOperators.all { it == Operator.PLUS || it == Operator.MINUS })

        // Round 10+ should have 5 numbers
        game.round = 10
        game.nextRound()
        assertEquals(5, game.numbers.size)

        // Verify solution formatting
        assertNotNull(game.solution())
        assertTrue(game.solution().contains("="))
    }

    @Test
    fun testNeverAllPlusWhenMultipleOperators() {
        val game = MissingOperatorsGame()

        // Sample many rounds with 2+ operators (3+ numbers) and ensure all-plus
        // is never a valid answer for the generated target.
        repeat(100) { i ->
            game.round = i % 15
            game.nextRound()
            val operatorCount = game.numbers.size - 1
            if (operatorCount > 1) {
                val allPlus = List(operatorCount) { Operator.PLUS }
                val allPlusResult = game.evaluateTokens(game.numbers, allPlus)
                assertTrue(
                    allPlusResult != game.targetResult,
                    "Round ${game.round}: all-plus must not equal target " +
                        "(numbers=${game.numbers}, target=${game.targetResult}, " +
                        "ops=${game.correctOperators})",
                )
                assertFalse(
                    game.correctOperators.all { it == Operator.PLUS },
                    "Round ${game.round}: correctOperators must not be all PLUS when count > 1",
                )
            }
        }
    }

    @Test
    fun testIsCorrect() {
        val game = MissingOperatorsGame()
        game.numbers = listOf(12, 4, 2)
        game.correctOperators = listOf(Operator.DIVIDE, Operator.PLUS)
        // 12 / 4 + 2 = 5
        game.toUiState() // update ui state targetResult if needed, but game sets it

        // Set targetResult manually for test stability
        game.targetResult = 5

        assertTrue(game.isCorrect("/+"))
        assertTrue(game.isCorrect(" / + "))
        assertFalse(game.isCorrect("+/"))
        assertFalse(game.isCorrect("++"))
    }
}
