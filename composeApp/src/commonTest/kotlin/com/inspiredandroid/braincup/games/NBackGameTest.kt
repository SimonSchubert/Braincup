package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Shape
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NBackGameTest {

    private fun game(level: Int = 2) = NBackGame(level = level).apply { nextRound() }

    /** Drive a whole block, answering each trial with [respond]. Returns the shapes shown. */
    private fun NBackGame.playBlock(respond: (index: Int, isTarget: Boolean) -> Boolean): List<Shape> {
        val shown = mutableListOf<Shape>()
        while (!isBlockOver) {
            beginTrial()
            shown += requireNotNull(toUiState().currentShape)
            if (respond(trialInBlock, isTargetTrial)) this.respond()
            endStimulus()
            closeTrial()
        }
        endBlock()
        return shown
    }

    private fun NBackGame.playBlockPerfectly() = playBlock { _, isTarget -> isTarget }

    // -- the level is n --

    @Test
    fun theLevelIsN() {
        assertEquals(1, game(level = 1).n)
        assertEquals(5, game(level = 5).n)
        assertEquals(NBackGame.MAX_N, game(level = NBackGame.MAX_N + 3).n, "the ladder has a ceiling")
        assertEquals(1, game(level = 0).n, "and a floor")
    }

    @Test
    fun aBlockIsTheReferenceLength() {
        assertEquals(20, NBackGame.BLOCK_TRIALS, "20 scored trials, as in the published task")
        assertEquals(6, NBackGame.TARGETS_PER_BLOCK, "6 targets, as in the published task")
        assertEquals(3000L, NBackGame.STEP_MS, "3000ms per item, as in the published task")
        assertEquals(2, NBackGame.MAX_ERRORS, "cleared on fewer than 3 errors, as in the published task")
        val g = game(level = 3)
        assertEquals(3 + 20, g.blockLength, "n priming trials on top of the scored ones")
    }

    // -- the stream --

    @Test
    fun aBlockHasTheTextbookTargetAndLureCounts() {
        repeat(30) {
            val g = game()
            val n = g.n
            val shown = g.playBlockPerfectly()

            val targets = (n until shown.size).count { shown[it] == shown[it - n] }
            val lures = (n until shown.size).count { index ->
                shown[index] != shown[index - n] &&
                    listOf(n - 1, n + 1)
                        .filter { it >= 1 && index - it >= 0 }
                        .any { shown[index] == shown[index - it] }
            }
            assertEquals(NBackGame.TARGETS_PER_BLOCK, targets, "targets in $shown")
            // A lure slot degrades to an ordinary non-target when both neighbour positions happen
            // to hold the target shape, so this is a ceiling rather than an equality. It is a
            // ceiling at all only because non-target trials avoid the neighbours too.
            assertTrue(lures <= NBackGame.LURES_PER_BLOCK, "lures in $shown")
        }
    }

    @Test
    fun everyScoredSlotCanHoldATarget() {
        // 20 trials do not divide by 6 targets, so a share width that truncated would leave the
        // block's last slots unreachable and a player could learn to stop attending at the end.
        val n = 2
        val seen = mutableSetOf<Int>()
        repeat(300) {
            val g = game(level = n)
            val shown = g.playBlockPerfectly()
            (n until shown.size).forEach { index ->
                if (shown[index] == shown[index - n]) seen += index - n
            }
        }
        assertEquals(
            (0 until NBackGame.BLOCK_TRIALS).toSet(),
            seen,
            "every scored position must be able to hold a target",
        )
    }

    @Test
    fun almostEveryPlannedLureLands() {
        // A lure slot can degrade, so the achievable bar is a rate, not an equality per block. What
        // has to hold is that the interference load is a designed quantity rather than a leftover
        // of the draw: nearly every planned lure lands, and none is ever added by accident.
        val blocks = 120
        val total = (0 until blocks).sumOf {
            val g = game()
            val n = g.n
            val shown = g.playBlockPerfectly()
            (n until shown.size).count { index ->
                shown[index] != shown[index - n] &&
                    listOf(n - 1, n + 1)
                        .filter { o -> o >= 1 && index - o >= 0 }
                        .any { o -> shown[index] == shown[index - o] }
            }
        }
        val mean = total.toDouble() / blocks
        assertTrue(
            mean > NBackGame.LURES_PER_BLOCK * 0.85,
            "only $mean of ${NBackGame.LURES_PER_BLOCK} planned lures land per block",
        )
    }

    @Test
    fun aNonTargetIsNeverAccidentallyAMatch() {
        repeat(30) {
            val g = game(level = 3)
            val n = g.n
            val shown = mutableListOf<Shape>()
            val targetFlags = mutableListOf<Boolean>()
            while (!g.isBlockOver) {
                g.beginTrial()
                shown += requireNotNull(g.toUiState().currentShape)
                targetFlags += g.isTargetTrial
                g.endStimulus()
                g.closeTrial()
            }
            (n until shown.size).forEach { index ->
                assertEquals(
                    shown[index] == shown[index - n],
                    targetFlags[index],
                    "trial $index of $shown was scored as target=${targetFlags[index]}",
                )
            }
        }
    }

    @Test
    fun primingTrialsAreNeverTargetsAndAResponseThereIsAFalseAlarm() {
        val g = game(level = 4)
        repeat(g.n) {
            g.beginTrial()
            assertFalse(g.isTargetTrial, "nothing is n back yet during priming")
            assertEquals(NBackGame.Response.FALSE_ALARM, g.respond())
            g.endStimulus()
            g.closeTrial()
        }
        assertEquals(4, g.falseAlarms)
        assertEquals(0, g.hits)
    }

    // -- scoring the block --

    @Test
    fun aFlawlessBlockIsCleared() {
        val g = game()
        g.playBlockPerfectly()
        assertEquals(NBackGame.TARGETS_PER_BLOCK, g.hits)
        assertEquals(0, g.misses)
        assertEquals(0, g.falseAlarms)
        assertEquals(0, g.errors)
        assertTrue(g.blockCleared)
    }

    @Test
    fun missesAndFalseAlarmsCountAlikeTowardTheErrorBudget() {
        // Two misses, no false alarms: exactly at the budget, so still cleared.
        val twoMisses = game()
        var seen = 0
        twoMisses.playBlock { _, isTarget ->
            if (isTarget) seen++
            isTarget && seen > 2
        }
        assertEquals(2, twoMisses.misses)
        assertEquals(2, twoMisses.errors)
        assertTrue(twoMisses.blockCleared, "$NBackGame.MAX_ERRORS errors still clears")

        // One miss and two false alarms: three errors, over the budget.
        val mixed = game()
        var targetsSeen = 0
        var alarms = 0
        mixed.playBlock { _, isTarget ->
            when {
                isTarget -> {
                    targetsSeen++
                    targetsSeen > 1
                }
                alarms < 2 -> {
                    alarms++
                    true
                }
                else -> false
            }
        }
        assertEquals(1, mixed.misses)
        assertEquals(2, mixed.falseAlarms)
        assertEquals(3, mixed.errors)
        assertFalse(mixed.blockCleared)
    }

    @Test
    fun anIdleBlockIsNotCleared() {
        val g = game()
        g.playBlock { _, _ -> false }
        assertEquals(NBackGame.TARGETS_PER_BLOCK, g.misses, "every target went untapped")
        assertFalse(g.blockCleared)
    }

    @Test
    fun aBlockOfNothingButTappingIsNotCleared() {
        // The floor the target rate has to hold: answering everything is not a way through.
        val g = game()
        g.playBlock { _, _ -> true }
        assertEquals(NBackGame.TARGETS_PER_BLOCK, g.hits)
        assertTrue(g.falseAlarms > NBackGame.MAX_ERRORS)
        assertFalse(g.blockCleared)
    }

    @Test
    fun aLevelNeverClaimsTheFlawlessRunBonus() {
        val g = game()
        g.playBlockPerfectly()
        assertFalse(g.answeredAllCorrect, "clearing the block is the reward; there is no bonus point")
    }

    // -- responses --

    @Test
    fun aSecondTapInOneTrialIsIgnored() {
        val g = game()
        g.beginTrial()
        assertEquals(NBackGame.Response.FALSE_ALARM, g.respond())
        assertEquals(1, g.falseAlarms)
        assertEquals(NBackGame.Response.IGNORED, g.respond())
        assertEquals(1, g.falseAlarms, "a second tap changes nothing")
    }

    @Test
    fun aTapOutsideTheStreamIsIgnored() {
        val g = game()
        assertEquals(NBackGame.Phase.LEAD_IN, g.phase)
        assertEquals(NBackGame.Response.IGNORED, g.respond())

        g.playBlockPerfectly()
        assertEquals(NBackGame.Response.IGNORED, g.respond(), "a closed block takes no taps")
    }

    @Test
    fun aTapIsMarkedForTheRestOfItsTrialAndNoLonger() {
        val g = game()
        g.beginTrial()
        assertNull(g.toUiState().lastResponse, "nothing is marked before a tap")

        g.respond()
        assertEquals(NBackGame.Response.FALSE_ALARM, g.toUiState().lastResponse)
        g.endStimulus()
        assertEquals(
            NBackGame.Response.FALSE_ALARM,
            g.toUiState().lastResponse,
            "the mark holds through the blank, which is still the same trial",
        )

        g.closeTrial()
        g.beginTrial()
        assertNull(g.toUiState().lastResponse, "a new item starts unmarked")
    }

    @Test
    fun aMissIsCountedButNeverMarkedMidStream() {
        // The whole reason misses are silent: the mark would land on the next item, which is the
        // one that most needs encoding, and turn one miss into two.
        val g = game()
        g.beginTrial()
        while (!g.isTargetTrial) {
            g.endStimulus()
            g.closeTrial()
            g.beginTrial()
        }
        g.endStimulus()
        g.closeTrial()

        assertEquals(1, g.misses)
        g.beginTrial()
        assertNull(g.toUiState().lastResponse, "the next item carries no mark from the miss")
    }

    // -- lifecycle --

    @Test
    fun theBlockRunsToItsEndEvenOnceItCannotBeCleared() {
        val g = game()
        var trials = 0
        g.playBlock { _, _ ->
            trials++
            false
        }
        assertEquals(g.blockLength, trials, "a failed block is not cut short")
    }

    @Test
    fun aPauseRestartsTheBlockFromTheTop() {
        val g = game()
        repeat(5) {
            g.beginTrial()
            g.endStimulus()
            g.closeTrial()
        }
        assertEquals(4, g.trialInBlock)

        g.restartBlock() // what resumeTimedPhase does after a pause

        assertEquals(-1, g.trialInBlock, "the block starts over")
        assertEquals(NBackGame.Phase.LEAD_IN, g.phase)
        assertEquals(0, g.hits)
        assertEquals(0, g.falseAlarms)
        assertEquals(0, g.misses)
    }

    @Test
    fun blockProgressRunsFromZeroToOne() {
        val g = game()
        assertEquals(0f, g.blockProgress, "nothing has been shown at the lead-in")
        g.playBlockPerfectly()
        assertEquals(1f, g.blockProgress, "and it is full once the block ends")
    }

    // -- ui state --

    @Test
    fun toUiStateHidesTheShapeThroughTheBlankAndOffStream() {
        val g = game(level = 3)
        assertNull(g.toUiState().currentShape, "the lead-in shows no stimulus")

        g.beginTrial()
        val streaming = g.toUiState()
        assertEquals(NBackGame.Phase.STREAM, streaming.phase)
        assertEquals(3, streaming.level)
        assertTrue(streaming.currentShape in NBackGame.PALETTE)

        g.endStimulus()
        assertNull(g.toUiState().currentShape, "the blank keeps two equal shapes apart")
    }

    @Test
    fun theBlockSummaryCountsHitsAndErrors() {
        val g = game()
        var seen = 0
        g.playBlock { _, isTarget ->
            if (isTarget) seen++
            isTarget && seen > 1
        }
        assertEquals(NBackGame.TARGETS_PER_BLOCK - 1, g.hits)
        assertEquals(1, g.errors, "a miss is a mistake, even though no wrong tap was made")
        assertTrue(g.blockCleared, "one miss is inside the budget")
    }

    @Test
    fun theSummaryCountsMissesAndWrongTapsAlike() {
        // The two lines count different things, so they must never contradict each other: a block
        // with a miss and none with a wrong tap still has to report a mistake.
        val missed = game()
        var targets = 0
        missed.playBlock { _, isTarget ->
            if (isTarget) targets++
            isTarget && targets > 1
        }
        assertEquals(NBackGame.TARGETS_PER_BLOCK - 1, missed.hits)
        assertEquals(1, missed.errors, "a missed match is a mistake")

        // And the mirror: every match found, one wrong tap. The matches line stays full.
        val alarmed = game()
        var alarms = 0
        alarmed.playBlock { _, isTarget ->
            when {
                isTarget -> true
                alarms < 1 -> {
                    alarms++
                    true
                }
                else -> false
            }
        }
        assertEquals(NBackGame.TARGETS_PER_BLOCK, alarmed.hits, "a wrong tap does not cost a match")
        assertEquals(1, alarmed.errors)
        assertTrue(alarmed.blockCleared)
    }

    // -- medals --

    @Test
    fun medalsSitOnReachableLevels() {
        assertTrue(GameType.N_BACK.usesLevelLabel, "the score is the highest n cleared")
        assertTrue(
            GameType.N_BACK.goldScore < NBackGame.MAX_N,
            "gold has to sit under the ladder's ceiling",
        )
        assertTrue(GameType.N_BACK.silverScore < GameType.N_BACK.goldScore)
        assertEquals(
            0,
            GameType.N_BACK.difficultyBonus(startRound = 5, baseScore = 8, adaptiveDifficulty = false),
        )
    }
}
