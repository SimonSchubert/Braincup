package com.inspiredandroid.braincup.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class QuickSumGameTest {

    @Test
    fun firstRoundFlashesFourTerms() {
        val game = QuickSumGame()
        game.nextRound()
        assertEquals(1, game.round)
        assertEquals(4, game.terms.size)
        assertEquals(QuickSumGame.Phase.FLASHING, game.phase)
        assertEquals(0, game.currentIndex)
    }

    @Test
    fun termCountRampsWithRounds() {
        val game = QuickSumGame()
        game.nextRound() // generated with round=0 → 4 terms
        assertEquals(4, game.terms.size)

        game.nextRound() // round=1 → 4
        assertEquals(4, game.terms.size)
        game.nextRound() // round=2 → 5
        assertEquals(5, game.terms.size)

        game.round = 15
        game.nextRound()
        assertEquals(QuickSumGame.MAX_TERMS, game.terms.size, "the top tier should flash nine terms")
    }

    @Test
    fun theRampClimbsWithinASingleRun() {
        // The ramp has to fit inside one 60s run: difficulty never carries over, and a round costs
        // roughly 8-10s, so a player only reaches round ~6-7. If the term count has not grown by
        // then, the top of the ramp exists only on paper.
        val termCounts = (0..7).map { startRound ->
            val game = QuickSumGame()
            game.round = startRound
            game.nextRound()
            game.termCount()
        }
        assertTrue(
            termCounts.last() >= termCounts.first() + 3,
            "terms should climb from 4 to 7 inside a reachable run, was $termCounts",
        )
        assertEquals(
            termCounts.sorted(),
            termCounts,
            "term count must never drop as rounds go up, was $termCounts",
        )
    }

    @Test
    fun termCountNeverExceedsTheCeiling() {
        (0..40).forEach { startRound ->
            val game = QuickSumGame()
            game.round = startRound
            game.nextRound()
            assertTrue(
                game.termCount() <= QuickSumGame.MAX_TERMS,
                "round $startRound flashed ${game.termCount()} terms, over the ceiling",
            )
        }
    }

    @Test
    fun flashPaceEasesDownGentlyAndHasAFloor() {
        val early = QuickSumGame()
        early.nextRound()
        val late = QuickSumGame()
        late.round = 15
        late.nextRound()

        assertTrue(
            late.stepDurationMs() < early.stepDurationMs(),
            "later rounds should flash faster",
        )
        // Reading a flashed number has a floor that practice does not move much, so the climb
        // must come mostly from term count and size rather than from cutting the step.
        assertTrue(
            late.stepDurationMs() >= QuickSumGame.MIN_STEP_MS,
            "the ramp must never flash faster than the floor",
        )
        assertTrue(
            late.stepDurationMs() >= early.stepDurationMs() / 2,
            "the top tier must not be anywhere near twice the opening pace",
        )
    }

    @Test
    fun noTierEverFlashesBelowTheFloor() {
        (0..40).forEach { startRound ->
            val game = QuickSumGame()
            game.round = startRound
            game.nextRound()
            assertTrue(
                game.stepDurationMs() >= QuickSumGame.MIN_STEP_MS,
                "round $startRound flashed at ${game.stepDurationMs()}ms, under the floor",
            )
        }
    }

    @Test
    fun flashTimeStaysInABandAcrossTiers() {
        // Later rounds do run longer, that is the cost of more terms. The cap matters because a
        // round that eats too much of the 60s stops the ramp being reachable at all.
        val durations = listOf(0, 2, 4, 6, 8, 10).map { startRound ->
            val game = QuickSumGame()
            game.round = startRound
            game.nextRound()
            game.termCount() * game.stepDurationMs()
        }
        durations.forEach { total ->
            assertTrue(
                total in 3_500..7_000,
                "a round should flash for roughly 4-7s at every tier, was ${total}ms",
            )
        }
        assertEquals(
            durations.sorted(),
            durations,
            "flash time must not drop as the ramp climbs, was $durations",
        )
    }

    @Test
    fun gapAlwaysSeparatesTerms() {
        // Without a blank gap two equal consecutive terms (7, 7) read as one unchanging 7.
        listOf(0, 2, 4, 6, 8, 10).forEach { startRound ->
            val game = QuickSumGame()
            game.round = startRound
            game.nextRound()
            assertTrue(game.gapMs() > 0, "terms must be separated by a blank gap at every tier")
            assertTrue(
                game.visibleMs() > game.gapMs(),
                "a term should be on screen longer than it is blank",
            )
            assertEquals(game.stepDurationMs(), game.visibleMs() + game.gapMs())
        }
    }

    @Test
    fun leadInGivesABreakBeforeNewTerms() {
        // The reveal must not run straight into the next round's terms: the player needs a beat
        // to clear the old total before new numbers start arriving.
        assertTrue(
            QuickSumGame.LEAD_IN_MS >= 500,
            "the break between a revealed total and the next term must be noticeable",
        )
        val game = QuickSumGame()
        game.nextRound()
        assertTrue(
            QuickSumGame.LEAD_IN_MS > game.gapMs(),
            "the between-rounds break should read as longer than a between-terms gap",
        )
    }

    @Test
    fun everyTermStaysInRoundValueRange() {
        val game = QuickSumGame()
        repeat(15) {
            game.nextRound()
            assertTrue(
                game.terms.all { term -> term in 1..25 },
                "terms drifted outside the configured range: ${game.terms}",
            )
        }
    }

    @Test
    fun solutionMatchesTermSum() {
        val game = QuickSumGame()
        game.nextRound()
        val expected = game.terms.sum()
        assertEquals(expected, game.targetSum())
        assertEquals(expected.toString(), game.solution())
        assertEquals(expected.toString().length, game.answerLength())
    }

    @Test
    fun isCorrectAcceptsTrimmedSum() {
        val game = QuickSumGame()
        game.nextRound()
        val sum = game.targetSum().toString()
        assertTrue(game.isCorrect(sum))
        assertTrue(game.isCorrect(" $sum "))
        assertFalse(game.isCorrect((game.targetSum() + 1).toString()))
    }

    @Test
    fun submitSumRecordsResultAndFlawlessRun() {
        val game = QuickSumGame()
        game.nextRound()
        assertTrue(game.submitSum(game.targetSum().toString()))
        assertEquals(QuickSumGame.AnswerResult.CORRECT, game.answerResult)
        assertTrue(game.answeredAllCorrect)

        game.nextRound()
        assertFalse(game.submitSum("-1"))
        assertEquals(QuickSumGame.AnswerResult.WRONG, game.answerResult)
        assertFalse(game.answeredAllCorrect, "a wrong total should end the flawless run")
    }

    @Test
    fun repeatRoundKeepsTierAndClearsPreviousResult() {
        val game = QuickSumGame()
        game.round = 9
        game.nextRound()
        val tierTermCount = game.terms.size
        val tierStep = game.stepDurationMs()
        game.submitSum("-1")
        assertNotNull(game.answerResult)

        game.repeatRound()

        assertEquals(tierTermCount, game.terms.size, "a replay must not change the tier")
        assertEquals(tierStep, game.stepDurationMs())
        assertEquals(QuickSumGame.Phase.FLASHING, game.phase)
        assertNull(game.answerResult, "the replay must clear the previous reveal")
    }

    @Test
    fun scoreIsNotCarriedAcrossRuns() {
        // The ramp lives inside the run, so a fresh game must always start at the slowest tier
        // and no resume bonus may apply.
        assertFalse(QuickSumGame().adaptiveDifficulty)
        assertEquals(
            0,
            GameType.QUICK_SUM.difficultyBonus(startRound = 9, baseScore = 5, adaptiveDifficulty = false),
        )
    }

    @Test
    fun toUiStateMapsFlashingTerm() {
        val game = QuickSumGame()
        game.nextRound()
        val state = game.toUiState()

        assertEquals(QuickSumGame.Phase.FLASHING, state.phase)
        assertEquals(game.terms[0], state.currentTerm)
        assertEquals(0, state.termIndex)
        assertEquals(game.terms.size, state.termCount)
        assertEquals(game.targetSum().toString().length, state.answerLength)
        assertNull(state.revealedSum, "the total must stay hidden while terms are flashing")
        assertNull(state.answerResult)
    }

    @Test
    fun toUiStateRevealsSumOnlyAfterSubmitting() {
        val game = QuickSumGame()
        game.nextRound()
        assertNull(game.toUiState().revealedSum)

        game.submitSum(game.targetSum().toString())
        val revealed = game.toUiState()
        assertEquals(game.targetSum().toString(), revealed.revealedSum)
        assertEquals(QuickSumGame.AnswerResult.CORRECT, revealed.answerResult)
    }
}
