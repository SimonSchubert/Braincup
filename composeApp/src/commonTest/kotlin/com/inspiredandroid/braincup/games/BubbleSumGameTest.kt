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
    fun arenaDefaultsToSquareAndTracksReportedShape() {
        val game = BubbleSumGame()
        assertEquals(1f, game.arenaWidth)
        assertEquals(1f, game.arenaHeight)

        // Short edge is always 1f, so a 300x600 canvas is twice as tall as it is wide.
        game.setArenaSize(300f, 600f)
        assertEquals(1f, game.arenaWidth)
        assertEquals(2f, game.arenaHeight)

        game.setArenaSize(800f, 400f)
        assertEquals(2f, game.arenaWidth)
        assertEquals(1f, game.arenaHeight)
    }

    @Test
    fun ignoresEmptyArenaSize() {
        val game = BubbleSumGame()
        game.setArenaSize(300f, 600f)
        game.setArenaSize(0f, 0f)
        assertEquals(1f, game.arenaWidth)
        assertEquals(2f, game.arenaHeight)
    }

    @Test
    fun bubblesSpawnInsideATallArena() {
        val game = BubbleSumGame()
        game.setArenaSize(300f, 600f)
        repeat(10) {
            game.nextRound()
            assertTrue(
                game.bubbles.all { it.x in BubbleSumGame.BALL_RADIUS..(game.arenaWidth - BubbleSumGame.BALL_RADIUS) },
                "bubbles should spawn within the arena width",
            )
            assertTrue(
                game.bubbles.all { it.y in BubbleSumGame.BALL_RADIUS..(game.arenaHeight - BubbleSumGame.BALL_RADIUS) },
                "bubbles should spawn within the arena height",
            )
        }
    }

    @Test
    fun bubblesUseTheFullHeightOfATallArena() {
        val game = BubbleSumGame()
        game.setArenaSize(300f, 600f)
        game.round = 9
        game.nextRound()
        // Positions are random, so assert over a long run rather than a single spawn: bubbles
        // must reach the half of the arena that would not exist under the old square.
        var reachedLowerHalf = false
        repeat(600) {
            game.updateBallPositions(0.016f)
            if (game.bubbles.any { it.y > 1f }) reachedLowerHalf = true
            assertTrue(
                game.bubbles.all { it.y <= game.arenaHeight + 0.01f && it.y >= -0.01f },
                "bubbles must stay inside the arena height",
            )
            assertTrue(
                game.bubbles.all { it.x <= game.arenaWidth + 0.01f && it.x >= -0.01f },
                "bubbles must stay inside the arena width",
            )
        }
        assertTrue(reachedLowerHalf, "bubbles should travel into the space a square arena lacked")
    }

    @Test
    fun resizingKeepsBubblesInsideTheNewArena() {
        val game = BubbleSumGame()
        game.setArenaSize(300f, 600f)
        game.nextRound()
        repeat(60) { game.updateBallPositions(0.016f) }

        // Rotate: the tall arena becomes a wide one.
        game.setArenaSize(600f, 300f)
        assertTrue(
            game.bubbles.all { it.x in BubbleSumGame.BALL_RADIUS..(game.arenaWidth - BubbleSumGame.BALL_RADIUS) },
            "bubbles should be remapped inside the rotated arena width",
        )
        assertTrue(
            game.bubbles.all { it.y in BubbleSumGame.BALL_RADIUS..(game.arenaHeight - BubbleSumGame.BALL_RADIUS) },
            "bubbles should be remapped inside the rotated arena height",
        )
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
        // Blink round: generateRound with r=4 after 5 nextRound calls.
        repeat(5) { game.nextRound() }
        assertTrue(game.usesBlink())
        val visible = game.visibleWindowMs()

        // Zero-offset bubble (first in stagger layout): phases relative to cycle timeline.
        val zeroOffset = game.bubbles.minBy { it.blinkPhaseOffsetMs }
        val offset = zeroOffset.blinkPhaseOffsetMs

        // During normal visible window (after grace, before visibleMs).
        game.applyVisibilityElapsed(600L - offset)
        assertEquals(
            BubbleSumGame.VisibilityPhase.VISIBLE,
            game.bubbles.first { it.blinkPhaseOffsetMs == offset }.phase,
        )

        // Warning window: t in [visibleMs, visibleMs + WARNING_MS)
        game.applyVisibilityElapsed(visible + 100L - offset)
        val warningBubble = game.bubbles.first { it.blinkPhaseOffsetMs == offset }
        assertEquals(BubbleSumGame.VisibilityPhase.WARNING, warningBubble.phase)
        assertTrue(warningBubble.showsNumber)

        // Hidden after warning ends
        game.applyVisibilityElapsed(visible + BubbleSumGame.WARNING_MS + 50L - offset)
        val hiddenBubble = game.bubbles.first { it.blinkPhaseOffsetMs == offset }
        assertEquals(BubbleSumGame.VisibilityPhase.HIDDEN, hiddenBubble.phase)
        assertFalse(hiddenBubble.showsNumber)
    }

    @Test
    fun blinkStaysCalmAcrossTheCycle() {
        // Warning and hidden are events against a calm background, not the resting state. Sweeping
        // whole cycles, only a minority of bubbles may be warning or hidden at any instant: when
        // the warning grows to a large share of the cycle most bubbles sit yellow at once and the
        // board reads as hectic.
        for (roundIndex in listOf(4, 6, 8, 12)) {
            val game = BubbleSumGame()
            game.round = roundIndex
            game.nextRound()
            assertTrue(game.usesStaggeredBlink(), "round $roundIndex should blink")

            val count = game.bubbles.size
            for (elapsed in 1000L..30_000L step 50L) {
                game.applyVisibilityElapsed(elapsed)
                val warning = game.bubbles.count { it.phase == BubbleSumGame.VisibilityPhase.WARNING }
                val hidden = game.bubbles.count { it.phase == BubbleSumGame.VisibilityPhase.HIDDEN }
                assertTrue(
                    warning * 2 < count,
                    "round $roundIndex had $warning of $count bubbles yellow at once (${elapsed}ms)",
                )
                assertTrue(
                    hidden * 2 < count,
                    "round $roundIndex hid $hidden of $count numbers at once (${elapsed}ms)",
                )
            }
        }
    }

    @Test
    fun everyBubbleStillHidesWithinAReasonableWindow() {
        // The calm background must not turn the mechanic off: each number still has to disappear,
        // and often enough to matter inside a 60s game.
        val game = BubbleSumGame()
        game.round = 12
        game.nextRound()
        val hidAtLeastOnce = game.bubbles.map { false }.toMutableList()
        for (elapsed in 1000L..12_000L step 50L) {
            game.applyVisibilityElapsed(elapsed)
            game.bubbles.forEachIndexed { index, bubble ->
                if (bubble.phase == BubbleSumGame.VisibilityPhase.HIDDEN) hidAtLeastOnce[index] = true
            }
        }
        assertTrue(hidAtLeastOnce.all { it }, "every bubble should hide at least once within 12s")
    }
}
