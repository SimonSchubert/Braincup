package com.inspiredandroid.braincup.games

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RuleShiftGameTest {

    private val keys = RuleShiftGame.keyCards

    /** The slot the card belongs in under [rule], 1-based. */
    private fun slotFor(card: RuleShiftCard, rule: RuleShiftGame.Rule): Int = when (rule) {
        RuleShiftGame.Rule.COLOR -> keys.indexOfFirst { it.color == card.color } + 1
        RuleShiftGame.Rule.SHAPE -> keys.indexOfFirst { it.shape == card.shape } + 1
        RuleShiftGame.Rule.COUNT -> card.count
    }

    /** Sort correctly until the rule moves, then return the rule that was just left behind. */
    private fun completeCategory(game: RuleShiftGame) {
        val rule = game.rule
        repeat(RuleShiftGame.CATEGORY_CRITERION) {
            game.sort(slotFor(game.stimulus, rule))
            game.nextRound()
        }
    }

    @Test
    fun `the deck holds only cards pointing at three different key cards`() {
        assertEquals(24, RuleShiftGame.deck.size)
        RuleShiftGame.deck.forEach { card ->
            val slots = RuleShiftGame.Rule.entries.map { slotFor(card, it) }
            assertEquals(
                3,
                slots.toSet().size,
                "card $card is ambiguous: its rules point at slots $slots",
            )
        }
    }

    @Test
    fun `every dealt card comes from the unambiguous deck`() {
        val game = RuleShiftGame(Random(1L))
        repeat(300) {
            game.nextRound()
            assertTrue(game.stimulus in RuleShiftGame.deck, "dealt ${game.stimulus}")
        }
    }

    @Test
    fun `the rule holds for exactly the criterion of correct sorts, then moves`() {
        val game = RuleShiftGame(Random(2L))
        game.nextRound()
        val original = game.rule

        repeat(RuleShiftGame.CATEGORY_CRITERION - 1) {
            val result = game.sort(slotFor(game.stimulus, original))
            assertEquals(false, result?.completedCategory)
            assertEquals(original, game.rule, "rule moved early")
            game.nextRound()
        }

        val last = game.sort(slotFor(game.stimulus, original))
        assertEquals(true, last?.completedCategory)
        assertNotEquals(original, game.rule, "rule did not move after the criterion")
        assertEquals(1, game.categoriesCompleted)
    }

    @Test
    fun `a wrong sort resets progress toward the category`() {
        val game = RuleShiftGame(Random(3L))
        game.nextRound()
        val rule = game.rule

        repeat(RuleShiftGame.CATEGORY_CRITERION - 1) {
            game.sort(slotFor(game.stimulus, rule))
            game.nextRound()
        }
        // One error, then a full run of six: the category completes on the sixth, not the second.
        game.sort(wrongSlotFor(game, rule))
        game.nextRound()
        repeat(RuleShiftGame.CATEGORY_CRITERION - 1) {
            assertEquals(0, game.categoriesCompleted)
            game.sort(slotFor(game.stimulus, rule))
            game.nextRound()
        }
        assertEquals(true, game.sort(slotFor(game.stimulus, rule))?.completedCategory)
    }

    @Test
    fun `the rule never moves to the one it just left`() {
        val game = RuleShiftGame(Random(4L))
        game.nextRound()
        repeat(RuleShiftGame.MAX_CATEGORIES) {
            val before = game.rule
            completeCategory(game)
            assertNotEquals(before, game.rule)
        }
        assertEquals(RuleShiftGame.MAX_CATEGORIES, game.categoriesCompleted)
    }

    @Test
    fun `the run ends once every category is done`() {
        val game = RuleShiftGame(Random(12L))
        game.nextRound()
        repeat(RuleShiftGame.MAX_CATEGORIES) {
            assertFalse(game.isOver, "ended early at ${game.categoriesCompleted} categories")
            completeCategory(game)
        }
        assertTrue(game.isOver)
        assertEquals(RuleShiftGame.MAX_CATEGORIES, game.categoriesCompleted)
    }

    @Test
    fun `the run ends when the deck runs out`() {
        val game = RuleShiftGame(Random(13L))
        game.nextRound()
        // Always sorting to the same slot cannot complete a category, so only the deck can end it.
        repeat(RuleShiftGame.TRIALS) {
            assertFalse(game.isOver, "ended early after ${game.trialsUsed} cards")
            game.sort(1)
            game.nextRound()
        }
        assertTrue(game.isOver)
        assertEquals(RuleShiftGame.TRIALS, game.trialsUsed)
        assertEquals(0, game.cardsRemaining)
    }

    @Test
    fun `sorting by the rule that was just correct counts as a perseverative error`() {
        val game = RuleShiftGame(Random(5L))
        game.nextRound()
        val abandoned = game.rule
        completeCategory(game)
        game.nextRound()

        assertEquals(0, game.perseverativeErrors)
        // The card is deliberately sorted under the rule that worked a moment ago.
        game.sort(slotFor(game.stimulus, abandoned))
        assertEquals(1, game.perseverativeErrors)
    }

    @Test
    fun `an error on the free probe after a shift does not cost the flawless run`() {
        val game = RuleShiftGame(Random(6L))
        game.nextRound()
        val abandoned = game.rule
        completeCategory(game)
        game.nextRound()

        // The first card after a silent shift cannot be known, so getting it wrong is the task
        // working rather than the player failing.
        game.sort(slotFor(game.stimulus, abandoned))
        assertTrue(game.answeredAllCorrect, "the unavoidable post-shift probe cleared the flag")

        // The next error is an ordinary one and does clear it.
        game.nextRound()
        game.sort(wrongSlotFor(game, game.rule))
        assertFalse(game.answeredAllCorrect)
    }

    @Test
    fun `the opening trial is free too, since no rule has been shown yet`() {
        val game = RuleShiftGame(Random(11L))
        game.nextRound()
        game.sort(wrongSlotFor(game, game.rule))
        assertTrue(game.answeredAllCorrect, "the opening probe cleared the flag")
    }

    @Test
    fun `a second tap during the feedback beat is ignored`() {
        val game = RuleShiftGame(Random(7L))
        game.nextRound()
        val slot = slotFor(game.stimulus, game.rule)

        assertEquals(true, game.sort(slot)?.isCorrect)
        assertNull(game.sort(slot), "a repeat tap sorted the same card twice")
    }

    @Test
    fun `isCorrect agrees with the active rule`() {
        val game = RuleShiftGame(Random(8L))
        repeat(100) {
            game.nextRound()
            val correct = slotFor(game.stimulus, game.rule)
            assertTrue(game.isCorrect(correct.toString()))
            (1..keys.size).filter { it != correct }.forEach {
                assertFalse(game.isCorrect(it.toString()), "slot $it accepted under ${game.rule}")
            }
        }
    }

    @Test
    fun `key cards are aligned so each index carries its own count colour and shape`() {
        assertContentEquals(listOf(1, 2, 3, 4), keys.map { it.count })
        assertEquals(keys.size, keys.map { it.color }.toSet().size)
        assertEquals(keys.size, keys.map { it.shape }.toSet().size)
    }

    @Test
    fun `the run is not adaptive`() {
        assertFalse(RuleShiftGame().adaptiveDifficulty)
    }

    /** Any slot that is wrong under [rule] for the card now on the table. */
    private fun wrongSlotFor(game: RuleShiftGame, rule: RuleShiftGame.Rule): Int = (1..keys.size).first { it != slotFor(game.stimulus, rule) }
}
