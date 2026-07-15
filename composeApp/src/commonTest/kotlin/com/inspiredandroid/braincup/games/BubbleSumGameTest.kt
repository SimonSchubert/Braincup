package com.inspiredandroid.braincup.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BubbleSumGameTest {

    @Test
    fun firstRoundHasThreeBubblesAndAlwaysVisible() {
        val game = BubbleSumGame()
        game.nextRound()
        assertEquals(1, game.round)
        assertEquals(3, game.bubbles.size)
        assertFalse(game.usesBlink())
        assertTrue(game.bubbles.all { it.phase == BubbleSumGame.VisibilityPhase.VISIBLE })
    }

    @Test
    fun bubbleCountRampsWithRounds() {
        val game = BubbleSumGame()
        game.nextRound()
        assertEquals(3, game.bubbles.size)

        // Advance to generateRound with round==2 (after two more nextRound calls from round 1)
        game.nextRound() // generate with round=1 → still 3
        game.nextRound() // generate with round=2 → 4
        assertEquals(4, game.bubbles.size)

        repeat(5) { game.nextRound() } // land past round index 7
        // After enough advances we should be at 5+ bubbles
        assertTrue(game.bubbles.size >= 5)
    }

    @Test
    fun targetSumMatchesBubbleValues() {
        val game = BubbleSumGame()
        game.nextRound()
        val expected = game.bubbles.sumOf { it.value }
        assertEquals(expected, game.targetSum())
        assertEquals(expected.toString(), game.solution())
        assertEquals(expected.toString().length, game.answerLength())
    }

    @Test
    fun isCorrectAcceptsTrimmedSum() {
        val game = BubbleSumGame()
        game.nextRound()
        val sum = game.targetSum().toString()
        assertTrue(game.isCorrect(sum))
        assertTrue(game.isCorrect("  $sum  "))
        assertFalse(game.isCorrect((game.targetSum() + 1).toString()))
    }

    @Test
    fun midRoundsIntroduceStaggeredBlink() {
        val game = BubbleSumGame()
        // generateRound uses round before increment: need generateRound with r>=4
        // nextRound x5: generates with 0,1,2,3,4 → after 5th, round=5, config from r=4
        repeat(5) { game.nextRound() }
        assertTrue(game.usesBlink())
        assertTrue(game.usesStaggeredBlink())
        val offsets = game.bubbles.map { it.blinkPhaseOffsetMs }.toSet()
        assertTrue(offsets.size > 1, "blink should use multiple phase offsets so numbers hide out of sync")
    }

    @Test
    fun lateRoundsKeepStaggeredBlink() {
        val game = BubbleSumGame()
        // generate with r>=8 → 9 nextRound calls (0..8)
        repeat(9) { game.nextRound() }
        assertTrue(game.usesBlink())
        assertTrue(game.usesStaggeredBlink())
        val offsets = game.bubbles.map { it.blinkPhaseOffsetMs }.toSet()
        assertTrue(offsets.size > 1, "staggered blink should use multiple phase offsets")
    }

    @Test
    fun valuesArePositiveAndSumWithinHardCap() {
        val game = BubbleSumGame()
        repeat(15) {
            game.nextRound()
            assertTrue(game.bubbles.all { it.value > 0 })
            assertTrue(game.targetSum() <= 150)
        }
    }

    @Test
    fun toUiStateMapsValuesAndAnswerLength() {
        val game = BubbleSumGame()
        game.nextRound()
        val ui = game.toUiState()
        assertEquals(game.bubbles.map { it.value }, ui.bubbles.map { it.value })
        assertEquals(game.answerLength(), ui.answerLength)
        assertTrue(ui.roundKey > 0)
    }

    @Test
    fun visibilityGraceKeepsNumbersVisibleAtStart() {
        val game = BubbleSumGame()
        // Blink round
        repeat(5) { game.nextRound() }
        assertTrue(game.usesBlink())
        // Before motion starts, updateVisibility with now=0 and motionStart=0 keeps grace path
        game.updateVisibility(0L)
        assertTrue(game.bubbles.all { it.phase == BubbleSumGame.VisibilityPhase.VISIBLE })
    }

    @Test
    fun warningPhaseComesBeforeHidden() {
        val game = BubbleSumGame()
        // Blink round: generateRound with r=4 after 5 nextRound calls (visibleMs = 1800)
        repeat(5) { game.nextRound() }
        assertTrue(game.usesBlink())
        assertEquals(3000L, BubbleSumGame.WARNING_MS)

        // Zero-offset bubble (first in stagger layout) — phases relative to cycle timeline.
        val zeroOffset = game.bubbles.minBy { it.blinkPhaseOffsetMs }
        val offset = zeroOffset.blinkPhaseOffsetMs

        // During normal visible window (after grace, before visibleMs).
        game.applyVisibilityElapsed(600L - offset)
        assertEquals(
            BubbleSumGame.VisibilityPhase.VISIBLE,
            game.bubbles.first { it.blinkPhaseOffsetMs == offset }.phase,
        )

        // Warning window: t in [visibleMs, visibleMs + WARNING_MS)
        game.applyVisibilityElapsed(1800L + 100L - offset)
        val warningBubble = game.bubbles.first { it.blinkPhaseOffsetMs == offset }
        assertEquals(BubbleSumGame.VisibilityPhase.WARNING, warningBubble.phase)
        assertTrue(warningBubble.showsNumber)

        // Hidden after warning ends
        game.applyVisibilityElapsed(1800L + BubbleSumGame.WARNING_MS + 50L - offset)
        val hiddenBubble = game.bubbles.first { it.blinkPhaseOffsetMs == offset }
        assertEquals(BubbleSumGame.VisibilityPhase.HIDDEN, hiddenBubble.phase)
        assertFalse(hiddenBubble.showsNumber)
    }
}
