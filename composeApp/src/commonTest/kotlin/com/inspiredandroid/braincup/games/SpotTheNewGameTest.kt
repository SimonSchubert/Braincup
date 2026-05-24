package com.inspiredandroid.braincup.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SpotTheNewGameTest {

    /** A game that has finished the memorize phase and is ready for answering. */
    private fun answeringGame(): SpotTheNewGame = SpotTheNewGame().apply {
        startMemorizing()
        startAnswering()
    }

    /** Taps the correct (new) tile and returns the result. */
    private fun SpotTheNewGame.tapNew(): SpotTheNewGame.SubmitResult {
        val index = displayed.indexOf(newAnimal)
        assertTrue(index >= 0, "new animal must be present in the displayed list")
        return submitAnswer(index.toString())
    }

    @Test
    fun adaptiveDifficultyIsDisabled() {
        assertFalse(SpotTheNewGame().adaptiveDifficulty)
    }

    @Test
    fun memorizePhaseSeedsThreeAnimals() {
        val game = SpotTheNewGame()
        game.startMemorizing()

        assertEquals(SpotTheNewGame.Phase.MEMORIZING, game.phase)
        assertEquals(SpotTheNewGame.SEED_COUNT, game.displayed.size)
        assertEquals(SpotTheNewGame.SEED_COUNT, game.seenCount)
        assertEquals(game.displayed.size, game.displayed.toSet().size, "seed animals must be unique")
        assertNull(game.newAnimal)
    }

    @Test
    fun firstAnswerRoundAddsOneNewAnimalToTheSeed() {
        val game = SpotTheNewGame()
        game.startMemorizing()
        val seed = game.displayed.toSet()

        game.startAnswering()

        assertEquals(SpotTheNewGame.Phase.ANSWERING, game.phase)
        assertEquals(1, game.round)
        assertEquals(SpotTheNewGame.SEED_COUNT + 1, game.displayed.size)
        assertNotNull(game.newAnimal)
        assertTrue(game.newAnimal in game.displayed)
        assertFalse(game.newAnimal in seed, "the new animal must not be one of the memorized seed")
        // The other three displayed animals are exactly the memorized seed.
        assertEquals(seed, game.displayed.filterNot { it == game.newAnimal }.toSet())
    }

    @Test
    fun displayCountGrowsByOneEveryTwoRounds() {
        val game = answeringGame()

        val sizes = mutableListOf<Int>()
        repeat(8) {
            sizes.add(game.displayed.size)
            game.tapNew()
        }

        assertEquals(listOf(4, 4, 5, 5, 6, 6, 7, 7), sizes)
    }

    @Test
    fun targetDisplayCountIsCappedAtMaxDisplay() {
        // The cap is a safety bound; with the current animal set it is never reached in play.
        assertEquals(SpotTheNewGame.MAX_DISPLAY, SpotTheNewGame.targetDisplayCount(1000))
        for (round in 0..1000) {
            assertTrue(SpotTheNewGame.targetDisplayCount(round) <= SpotTheNewGame.MAX_DISPLAY)
        }
    }

    @Test
    fun newAnimalIsAlwaysUnseenAndDisplayedAnimalsAreUnique() {
        val game = SpotTheNewGame()
        game.startMemorizing()
        val everSeen = game.displayed.toMutableSet() // the memorized seed
        game.startAnswering()

        // Stop before the pool is exhausted (20 animals - 3 seed = 17 answerable rounds).
        repeat(15) {
            val current = game.newAnimal
            assertNotNull(current)
            assertFalse(current in everSeen, "new animal must never have been shown before")
            assertEquals(
                game.displayed.size,
                game.displayed.toSet().size,
                "all animals on screen must be unique",
            )
            everSeen.add(current)
            game.tapNew()
        }
    }

    @Test
    fun correctTapGrowsSeenPool() {
        val game = answeringGame()

        repeat(5) { i ->
            assertEquals(SpotTheNewGame.SEED_COUNT + i, game.seenCount)
            val result = game.tapNew()
            assertEquals(SpotTheNewGame.SubmitResult.Correct, result)
            assertEquals(SpotTheNewGame.SEED_COUNT + i + 1, game.seenCount)
        }
    }

    @Test
    fun wrongTapEndsTheGame() {
        val game = answeringGame()

        val wrongIndex = game.displayed.indexOfFirst { it != game.newAnimal }
        assertTrue(wrongIndex >= 0)

        val result = game.submitAnswer(wrongIndex.toString())

        assertEquals(SpotTheNewGame.SubmitResult.Wrong, result)
        assertEquals(SpotTheNewGame.Phase.GAME_OVER, game.phase)
        assertFalse(game.answeredAllCorrect)
    }

    @Test
    fun gameOverRevealMarksWrongAndCorrectTiles() {
        val game = answeringGame()

        val wrongIndex = game.displayed.indexOfFirst { it != game.newAnimal }
        game.submitAnswer(wrongIndex.toString())

        val cells = game.toUiState().cells
        assertEquals(1, cells.count { it.type == SpotTheNewGame.CellType.CORRECT })
        assertEquals(1, cells.count { it.type == SpotTheNewGame.CellType.WRONG })
        assertEquals(game.newAnimal, cells.first { it.type == SpotTheNewGame.CellType.CORRECT }.animal)
    }

    @Test
    fun tapsDuringMemorizePhaseAreIgnored() {
        val game = SpotTheNewGame()
        game.startMemorizing()

        val result = game.submitAnswer("0")

        assertEquals(SpotTheNewGame.SubmitResult.Wrong, result)
        assertEquals(SpotTheNewGame.Phase.MEMORIZING, game.phase, "memorize taps must not end the game")
    }

    @Test
    fun submittingAfterGameOverIsIgnored() {
        val game = answeringGame()
        val wrongIndex = game.displayed.indexOfFirst { it != game.newAnimal }
        game.submitAnswer(wrongIndex.toString())

        val result = game.submitAnswer("0")
        assertEquals(SpotTheNewGame.SubmitResult.Wrong, result)
        assertEquals(SpotTheNewGame.Phase.GAME_OVER, game.phase)
    }

    @Test
    fun seeingEveryAnimalEndsWithMaxScore() {
        val game = answeringGame()

        var taps = 0
        var lastResult: SpotTheNewGame.SubmitResult? = null
        repeat(SpotTheNewGame.ALL_ANIMALS.size + 5) {
            if (lastResult is SpotTheNewGame.SubmitResult.PoolExhausted) return@repeat
            lastResult = game.tapNew()
            taps++
        }

        assertEquals(SpotTheNewGame.SubmitResult.PoolExhausted, lastResult)
        assertTrue(game.poolExhausted)
        // Max score = every animal seen minus the 3 memorized at the start.
        assertEquals(SpotTheNewGame.ALL_ANIMALS.size - SpotTheNewGame.SEED_COUNT, taps)
    }
}
