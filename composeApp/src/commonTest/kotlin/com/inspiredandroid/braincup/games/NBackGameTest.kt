package com.inspiredandroid.braincup.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NBackGameTest {

    @Test
    fun firstRoundShowsTheShortestSequence() {
        val game = NBackGame()
        game.nextRound()
        assertEquals(NBackGame.MIN_LENGTH, game.sequenceLength)
        assertEquals(NBackGame.Phase.MEMORIZE, game.phase)
    }

    @Test
    fun sequenceLengthRampsWithRoundsUpToTheCap() {
        val lengths = (0..6).map { startRound ->
            NBackGame().apply {
                round = startRound
                nextRound()
            }.sequenceLength
        }
        assertEquals(listOf(3, 4, 5, 6, 6, 6, 6), lengths, "length should grow to the cap, was $lengths")
    }

    @Test
    fun sequenceShapesAreDistinctWithinARound() {
        repeat(10) {
            val game = NBackGame()
            game.round = 3 // longest sequence
            game.nextRound()
            assertEquals(
                game.sequence.size,
                game.sequence.toSet().size,
                "a round must not repeat a shape: ${game.sequence}",
            )
            assertTrue(game.sequence.all { it in NBackGame.PALETTE })
        }
    }

    @Test
    fun askPositionIsInsideTheSequenceAndNamesTheAnswer() {
        repeat(20) {
            val game = NBackGame()
            game.round = 2
            game.nextRound()
            assertTrue(game.askPosition in game.sequence.indices)
            assertEquals(game.sequence[game.askPosition], game.answerShape())
        }
    }

    @Test
    fun answerIsAlwaysOneOfSixPaletteShapes() {
        // A blind guess is right 1 in 6, far below a medal, so the tap interaction is not farmable.
        assertEquals(6, NBackGame.PALETTE.size)
        val game = NBackGame()
        game.nextRound()
        assertTrue(game.answerShape() in NBackGame.PALETTE)
    }

    @Test
    fun submitScoresTheShapeAtTheAskedPosition() {
        val game = NBackGame()
        game.nextRound()
        assertTrue(game.submitRecall(game.answerShape().name))
        assertEquals(NBackGame.RecallResult.CORRECT, game.recallResult)
        assertTrue(game.answeredAllCorrect)

        game.nextRound()
        val wrong = NBackGame.PALETTE.first { it != game.answerShape() }
        assertFalse(game.submitRecall(wrong.name))
        assertEquals(NBackGame.RecallResult.WRONG, game.recallResult)
        assertFalse(game.answeredAllCorrect, "a wrong tap ends the flawless run")
    }

    @Test
    fun isCorrectMatchesTheAnswerShape() {
        val game = NBackGame()
        game.nextRound()
        val answer = game.answerShape()
        assertTrue(game.isCorrect(answer.name))
        assertFalse(game.isCorrect(NBackGame.PALETTE.first { it != answer }.name))
    }

    @Test
    fun repeatRoundKeepsTheLengthAndClearsTheReveal() {
        val game = NBackGame()
        game.round = 2
        game.nextRound()
        val length = game.sequenceLength
        game.submitRecall("nonsense")
        assertEquals(NBackGame.RecallResult.WRONG, game.recallResult)

        game.repeatRound()

        assertEquals(length, game.sequenceLength, "a replay must keep the same length")
        assertEquals(NBackGame.Phase.MEMORIZE, game.phase)
        assertNull(game.recallResult, "the replay must clear the previous reveal")
    }

    @Test
    fun scoreIsNotCarriedAcrossRuns() {
        assertFalse(NBackGame().adaptiveDifficulty)
        assertEquals(
            0,
            GameType.N_BACK.difficultyBonus(startRound = 5, baseScore = 8, adaptiveDifficulty = false),
        )
    }

    @Test
    fun toUiStateHidesShapesUntilFlashedAndAnswerUntilTapped() {
        val game = NBackGame()
        game.nextRound()
        val memorizing = game.toUiState()
        assertEquals(NBackGame.Phase.MEMORIZE, memorizing.phase)
        assertNull(memorizing.currentShape, "no shape is on screen before the flash starts")
        assertEquals(game.sequenceLength, memorizing.sequenceLength)
        assertEquals(game.askPosition, memorizing.askPosition)
        assertEquals(NBackGame.PALETTE, memorizing.options.toList())
        assertNull(memorizing.revealAnswer, "the answer must stay hidden until the player taps")

        val answer = game.answerShape()
        game.submitRecall(answer.name)
        val revealed = game.toUiState()
        assertEquals(answer, revealed.revealAnswer)
        assertEquals(NBackGame.RecallResult.CORRECT, revealed.recallResult)
    }
}
