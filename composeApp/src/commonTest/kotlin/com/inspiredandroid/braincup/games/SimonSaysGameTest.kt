package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.SimonSaysUiState
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SimonSaysGameTest {

    /** Runs the flash to completion so the game lands in ANSWERING, as the controller does. */
    private fun TestScope.playFlash(game: SimonSaysGame) {
        game.startShowNewPad(this) {}
        testScheduler.advanceUntilIdle()
        assertEquals(SimonSaysGame.Phase.ANSWERING, game.phase)
    }

    @Test
    fun sequenceGrowsByOneAndKeepsEarlierPads() {
        val game = SimonSaysGame(random = Random(1L))
        game.nextRound()
        val afterFirst = game.sequence
        assertEquals(1, afterFirst.size)

        game.nextRound()
        assertEquals(2, game.sequence.size)
        // Append, never regenerate: the pads already learned must survive the new round.
        assertEquals(afterFirst, game.sequence.take(1))
    }

    @Test
    fun showingPhaseFlashesOnlyTheNewPad() = runTest {
        val game = SimonSaysGame(random = Random(1L))
        repeat(4) { game.nextRound() }
        assertEquals(4, game.sequence.size)

        val flashed = mutableListOf<Int>()
        game.startShowNewPad(this) {
            if (game.currentShowIndex >= 0) flashed += game.currentShowIndex
        }
        testScheduler.advanceUntilIdle()

        // The whole point of the mode: four pads in the sequence, one flash, and it is the pad
        // the round just added. Everything before it has to come from the player's memory.
        assertEquals(listOf(3), flashed)
        assertEquals(SimonSaysGame.Phase.ANSWERING, game.phase)
    }

    @Test
    fun playerMustTapBackTheWholeSequence() {
        val game = SimonSaysGame(random = Random(1L))
        repeat(3) { game.nextRound() }

        // Tapping only the newly shown pad is not enough to clear the round.
        assertEquals(
            SimonSaysGame.SubmitResult.CorrectContinue,
            game.submitAnswer(game.sequence[0].name),
        )
        assertEquals(
            SimonSaysGame.SubmitResult.CorrectContinue,
            game.submitAnswer(game.sequence[1].name),
        )
        assertEquals(
            SimonSaysGame.SubmitResult.RoundComplete,
            game.submitAnswer(game.sequence[2].name),
        )
    }

    @Test
    fun uiStateAfterCompletingARoundLightsTheFinalTap() = runTest {
        val game = SimonSaysGame(random = Random(1L))
        repeat(2) { game.nextRound() }
        playFlash(game)

        game.submitAnswer(game.sequence[0].name)
        game.submitAnswer(game.sequence[1].name)

        // The controller renders this state during the feedback beat, with currentTapIndex sitting
        // one past the end of the sequence. It must light the last pad, not read out of bounds.
        val lit = game.toUiState().pads.filter { it.type == SimonSaysUiState.CellType.TAPPED }
        assertEquals(1, lit.size)
        assertEquals(game.sequence.last(), lit.single().color)
    }

    @Test
    fun startingARoundClearsThePreviousRoundsHighlight() = runTest {
        val game = SimonSaysGame(random = Random(1L))
        game.nextRound()
        playFlash(game)
        game.submitAnswer(game.sequence[0].name)
        assertTrue(game.toUiState().pads.any { it.type == SimonSaysUiState.CellType.TAPPED })

        game.nextRound()

        // Nothing may still look active when the new round opens, or the leftover pad reads as
        // one the game just flashed.
        val types = game.toUiState().pads.map { it.type }
        assertTrue(types.all { it == SimonSaysUiState.CellType.INACTIVE })
    }

    @Test
    fun firstWrongTapEndsTheGame() {
        val game = SimonSaysGame(random = Random(1L))
        repeat(2) { game.nextRound() }

        val wrong = SimonSaysGame.PADS.first { it != game.sequence[0] }
        assertEquals(SimonSaysGame.SubmitResult.Wrong, game.submitAnswer(wrong.name))
        assertEquals(SimonSaysGame.Phase.GAME_OVER, game.phase)
        assertEquals(wrong, game.wrongPad)
        assertFalse(game.answeredAllCorrect)
    }

    @Test
    fun newRoundResetsTapProgressAndStartsShowing() {
        val game = SimonSaysGame(random = Random(1L))
        game.nextRound()
        game.submitAnswer(game.sequence[0].name)

        game.nextRound()
        assertEquals(SimonSaysGame.Phase.SHOWING, game.phase)
        assertEquals(0, game.currentTapIndex)
        assertEquals(-1, game.currentShowIndex)
        assertNull(game.wrongPad)
    }

    @Test
    fun cancellingTheFlashLeavesThePhaseUntouched() = runTest {
        val game = SimonSaysGame(random = Random(1L))
        game.nextRound()

        game.startShowNewPad(this) {}
        game.cancelShowNewPad()
        testScheduler.advanceUntilIdle()

        // Leaving the game mid-flash must not strand a coroutine that later flips the phase.
        assertTrue(game.phase == SimonSaysGame.Phase.SHOWING)
        assertEquals(-1, game.currentShowIndex)
    }
}
