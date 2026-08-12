package com.inspiredandroid.braincup.games

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrioGameTest {

    @Test
    fun allSameOrAllDifferentIsASet() {
        val a = TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID)
        val b = TrioCard(TrioShape.CIRCLE, 2, TrioFill.SOLID)
        val c = TrioCard(TrioShape.CIRCLE, 3, TrioFill.SOLID)
        assertTrue(isTrioSet(a, b, c))
        assertEquals(1, trioSetHardness(a, b, c))
    }

    @Test
    fun allAttributesDifferentIsNotASet() {
        val a = TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID)
        val b = TrioCard(TrioShape.SQUARE, 2, TrioFill.STRIPED)
        val c = TrioCard(TrioShape.TRIANGLE, 3, TrioFill.OUTLINE)
        assertFalse(isTrioSet(a, b, c))
        assertEquals(3, trioSetHardness(a, b, c))
    }

    @Test
    fun mixedAttributeIsNotASet() {
        val a = TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID)
        val b = TrioCard(TrioShape.CIRCLE, 1, TrioFill.STRIPED)
        val c = TrioCard(TrioShape.SQUARE, 1, TrioFill.OUTLINE)
        assertFalse(isTrioSet(a, b, c))
    }

    @Test
    fun completingCardIsUniqueAndFormsASet() {
        val a = TrioCard(TrioShape.SQUARE, 2, TrioFill.STRIPED)
        val b = TrioCard(TrioShape.TRIANGLE, 2, TrioFill.SOLID)
        val c = completingTrioCard(a, b)
        assertEquals(TrioCard(TrioShape.CIRCLE, 2, TrioFill.OUTLINE), c)
        assertTrue(isTrioSet(a, b, c))
        assertEquals(c, completingTrioCard(b, a))
    }

    @Test
    fun deckHasEveryUniqueCardOnce() {
        val deck = allTrioCards()
        assertEquals(27, deck.size)
        assertEquals(27, deck.toSet().size)
    }

    @Test
    fun everyDealtBoardHasASetAndNoDuplicates() {
        repeat(40) { seed ->
            val game = TrioGame(Random(seed.toLong())).apply { nextRound() }
            assertEquals(TrioGame.BOARD_SIZE, game.cards.size)
            assertEquals(game.cards.size, game.cards.toSet().size)
            assertTrue(findTrioSets(game.cards).isNotEmpty(), "no set in seed $seed")
        }
    }

    @Test
    fun earlyRoundsGuaranteeAnEasySet() {
        repeat(20) { seed ->
            val game = TrioGame(Random(seed.toLong())).apply {
                round = 0
                nextRound()
            }
            val sets = findTrioSets(game.cards)
            assertTrue(sets.any { trioSetHardness(game.cards[it[0]], game.cards[it[1]], game.cards[it[2]]) == 1 })
        }
    }

    @Test
    fun lateRoundsGuaranteeAHardSet() {
        repeat(20) { seed ->
            val game = TrioGame(Random(seed.toLong())).apply {
                round = 10
                nextRound()
            }
            val sets = findTrioSets(game.cards)
            assertTrue(sets.any { trioSetHardness(game.cards[it[0]], game.cards[it[1]], game.cards[it[2]]) == 2 })
        }
    }

    @Test
    fun tapTogglesThenJudgesOnTheThirdCard() {
        val game = gameWithKnownSet()
        assertEquals(TrioGame.TapResult.Toggled, game.tap(0))
        assertEquals(TrioGame.TapResult.Toggled, game.tap(1))
        assertEquals(setOf(0, 1), game.selected)
        assertEquals(TrioGame.TapResult.Correct, game.tap(2))
        assertEquals(TrioGame.CardFeedback.CORRECT, game.feedback)
        assertEquals(TrioGame.TapResult.Ignored, game.tap(3))
    }

    @Test
    fun wrongTrioFlashesAndCanBeCleared() {
        val game = gameWithKnownSet()
        assertEquals(TrioGame.TapResult.Toggled, game.tap(0))
        assertEquals(TrioGame.TapResult.Toggled, game.tap(1))
        assertEquals(TrioGame.TapResult.Wrong, game.tap(3))
        assertEquals(TrioGame.CardFeedback.WRONG, game.feedback)
        assertFalse(game.answeredAllCorrect)
        game.clearSelection()
        assertTrue(game.selected.isEmpty())
        assertEquals(TrioGame.CardFeedback.NONE, game.feedback)
    }

    @Test
    fun deselectBeforeThirdCard() {
        val game = gameWithKnownSet()
        game.tap(0)
        game.tap(0)
        assertTrue(game.selected.isEmpty())
        assertEquals(TrioGame.TapResult.Toggled, game.tap(0))
    }

    @Test
    fun isCorrectAndSolutionUseTheKnownSet() {
        val game = gameWithKnownSet()
        assertTrue(game.isCorrect("0,1,2"))
        assertFalse(game.isCorrect("0,1,3"))
        assertEquals("1, 2, 3", game.solution())
    }

    @Test
    fun nextRoundClearsSelection() {
        val game = gameWithKnownSet()
        game.tap(0)
        game.nextRound()
        assertTrue(game.selected.isEmpty())
        assertEquals(TrioGame.CardFeedback.NONE, game.feedback)
        assertEquals(TrioGame.BOARD_SIZE, game.cards.size)
    }

    private fun gameWithKnownSet(): TrioGame {
        val a = TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID)
        val b = TrioCard(TrioShape.CIRCLE, 2, TrioFill.SOLID)
        val c = TrioCard(TrioShape.CIRCLE, 3, TrioFill.SOLID)
        val d = TrioCard(TrioShape.SQUARE, 1, TrioFill.STRIPED)
        val rest = allTrioCards().filter { it != a && it != b && it != c && it != d }.take(8)
        return TrioGame(Random(0L)).apply { loadBoard(listOf(a, b, c, d) + rest) }
    }
}
