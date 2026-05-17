package com.inspiredandroid.braincup.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class VisualMemoryGameTest {

    @Test
    fun firstRoundCorrectAnswerCompletesRoundAndAdvances() {
        val game = VisualMemoryGame()
        game.nextRound()
        assertEquals(1, game.round)
        assertEquals(VisualMemoryGame.Phase.MEMORIZING, game.phase)

        game.startAnswerPhase()
        assertEquals(VisualMemoryGame.Phase.ANSWERING, game.phase)
        assertEquals(1, game.guessOrder.size)

        val correctFigure = game.getCurrentTargetFigure()
        val correctIndex = game.availableFigures.indexOf(correctFigure)
        val result = game.submitAnswer(correctIndex.toString())

        assertEquals(VisualMemoryGame.SubmitResult.RoundComplete, result)
        assertEquals(2, game.round)
        assertEquals(VisualMemoryGame.Phase.MEMORIZING, game.phase)
    }

    @Test
    fun adaptiveDifficultyIsDisabled() {
        // Visual Memory must always start at round 0; otherwise generateRound() leaves earlier
        // figures unplaced while the answer phase still asks for them.
        assertFalse(VisualMemoryGame().adaptiveDifficulty)
    }
}
