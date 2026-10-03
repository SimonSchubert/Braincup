package com.inspiredandroid.braincup.games

import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HeadCountGameTest {

    private fun gameAt(startRound: Int, seed: Long = 1L) = HeadCountGame(Random(seed)).apply {
        round = startRound
        nextRound()
    }

    @Test
    fun theHouseNeverHoldsANegativeOrTwoDigitCount() {
        // A count below zero is impossible to show, and a count of ten or more would make the
        // pad's submit-on-length give away that the answer has two digits.
        (0..12).forEach { startRound ->
            repeat(200) { seed ->
                val game = gameAt(startRound, seed.toLong())
                var occupants = 0
                game.moves.forEach { move ->
                    assertTrue(move.leaving <= occupants, "more people left than were inside: ${game.moves}")
                    occupants += move.entering - move.leaving
                    assertTrue(occupants in 0..HeadCountGame.MAX_OCCUPANTS, "count left 0..9: ${game.moves}")
                }
                assertEquals(occupants, game.occupants())
            }
        }
    }

    @Test
    fun theFirstMoveAlwaysFillsTheEmptyHouse() {
        repeat(100) { seed ->
            val first = gameAt(0, seed.toLong()).moves.first()
            assertTrue(first.entering in 1..3, "first move should bring 1-3 people in, was $first")
            assertEquals(0, first.leaving)
        }
    }

    @Test
    fun everyMoveDoesSomethingAndFitsTheBatchLimit() {
        (0..12).forEach { startRound ->
            repeat(50) { seed ->
                gameAt(startRound, seed.toLong()).moves.forEach { move ->
                    assertTrue(move.entering + move.leaving > 0, "a move with nobody walking: $move")
                    assertTrue(move.entering <= HeadCountGame.MAX_BATCH && move.leaving <= HeadCountGame.MAX_BATCH)
                }
            }
        }
    }

    @Test
    fun openingTiersSendOnePersonAtATimeInOneDirection() {
        repeat(100) { seed ->
            gameAt(0, seed.toLong()).moves.drop(1).forEach { move ->
                assertEquals(1, move.entering + move.leaving, "opening tier is single steps: $move")
                assertTrue(move.entersFromLeft, "opening tier keeps everyone on one route")
            }
        }
    }

    @Test
    fun simultaneousMovesAppearOnlyOnceTheTierIsMixed() {
        fun hasSimultaneous(startRound: Int) = (0 until 200).any { seed ->
            gameAt(startRound, seed.toLong()).moves.any { it.entering > 0 && it.leaving > 0 }
        }
        assertFalse(hasSimultaneous(0))
        assertFalse(hasSimultaneous(3))
        assertTrue(hasSimultaneous(4), "the mixed tier should send people in and out at once")
    }

    @Test
    fun theRampClimbsWithinASingleRun() {
        val moveCounts = (0..9).map { gameAt(it).moves.size }
        assertEquals(moveCounts.sorted(), moveCounts, "move count must never drop, was $moveCounts")
        assertTrue(moveCounts.last() >= moveCounts.first() + 3, "moves should climb, was $moveCounts")
        assertTrue(moveCounts.all { it <= HeadCountGame.MAX_MOVES })

        val steps = (0..15).map { gameAt(it).stepDurationMs() }
        assertEquals(steps.sortedDescending(), steps, "the pace must never slow down, was $steps")
        assertTrue(steps.all { it >= HeadCountGame.MIN_STEP_MS })
    }

    @Test
    fun aRoundWatchesForAtMostAboutEightSeconds() {
        // A round that eats too much of the 60s makes the top of the ramp unreachable.
        (0..12).forEach { startRound ->
            val game = gameAt(startRound)
            val total = HeadCountGame.LEAD_IN_MS + game.moves.size * game.stepDurationMs()
            assertTrue(total in 4_000..8_500, "round $startRound watches for ${total}ms")
        }
    }

    @Test
    fun submitCountRecordsResultAndFlawlessRun() {
        val game = gameAt(0)
        assertTrue(game.submitCount(game.occupants().toString()))
        assertEquals(RevealResult.CORRECT, game.answerResult)
        assertTrue(game.answeredAllCorrect)

        game.nextRound()
        assertFalse(game.submitCount((game.occupants() + 1).toString()))
        assertEquals(RevealResult.WRONG, game.answerResult)
        assertFalse(game.answeredAllCorrect)
    }

    @Test
    fun repeatRoundKeepsTierAndClearsPreviousResult() {
        val game = gameAt(9)
        val moveCount = game.moves.size
        val step = game.stepDurationMs()
        game.submitCount("0")
        assertNotNull(game.answerResult)

        game.repeatRound()

        assertEquals(moveCount, game.moves.size)
        assertEquals(step, game.stepDurationMs())
        assertEquals(HeadCountGame.Phase.WATCHING, game.phase)
        assertNull(game.answerResult)
    }

    @Test
    fun theTimedPhaseWalksEveryMoveThenAsksForTheCount() = runTest {
        val game = gameAt(0)
        val seenMoves = mutableListOf<Int>()
        game.startTimedPhase(this) {
            val state = game.toUiState()
            if (state.phase == HeadCountGame.Phase.WATCHING && state.moveIndex >= 0) seenMoves += state.moveIndex
            assertNull(state.revealedCount, "the count must stay hidden until answered")
        }
        testScheduler.advanceUntilIdle()

        assertEquals(game.moves.indices.toList(), seenMoves)
        assertEquals(HeadCountGame.Phase.ANSWER, game.phase)
        assertEquals(0, game.toUiState().entering, "nobody walks once the question is up")
    }

    @Test
    fun resumingAfterAPauseDealsFreshTraffic() = runTest {
        val game = gameAt(4, seed = 7L)
        game.startTimedPhase(this) {}
        testScheduler.advanceTimeBy(HeadCountGame.LEAD_IN_MS + 10)
        val before = game.moves
        game.pauseTimedPhase()

        game.resumeTimedPhase(this) {}
        testScheduler.advanceUntilIdle()

        assertTrue(before != game.moves, "the quit dialog must not buy a second look at the same moves")
        assertEquals(HeadCountGame.Phase.ANSWER, game.phase)
    }
}
