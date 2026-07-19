package com.inspiredandroid.braincup.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameTypeDifficultyBonusTest {

    @Test
    fun adaptiveRunAddsStartRoundAsBonus() {
        val game = GameType.MENTAL_CALCULATION
        assertEquals(0, game.difficultyBonus(startRound = 10, baseScore = 0, adaptiveDifficulty = true))
        assertEquals(0, game.difficultyBonus(startRound = 10, baseScore = 5, adaptiveDifficulty = false))
        assertEquals(5, game.difficultyBonus(startRound = 5, baseScore = 5, adaptiveDifficulty = true))
        assertEquals(0, game.difficultyBonus(startRound = 0, baseScore = 18, adaptiveDifficulty = true))
    }

    @Test
    fun bonusIsCappedAtSilverScore() {
        val game = GameType.MENTAL_CALCULATION
        assertEquals(9, game.silverScore)
        assertEquals(9, game.difficultyBonus(startRound = 10, baseScore = 5, adaptiveDifficulty = true))
        assertEquals(9, game.difficultyBonus(startRound = 100, baseScore = 1, adaptiveDifficulty = true))
        assertEquals(9, game.difficultyBonus(startRound = 9, baseScore = 5, adaptiveDifficulty = true))
        assertEquals(8, game.difficultyBonus(startRound = 8, baseScore = 5, adaptiveDifficulty = true))
    }

    @Test
    fun goldAlwaysRequiresRealAnswersRegardlessOfStartRound() {
        // The start round grows every session, so without the cap the bonus alone would
        // eventually beat any threshold. With it, gold takes at least (gold - silver) answers.
        GameType.entries.filterNot { it.lowerScoreIsBetter }.forEach { game ->
            val bonus = game.difficultyBonus(startRound = 1_000, baseScore = 1, adaptiveDifficulty = true)
            assertEquals(game.silverScore, bonus, "cap broken for $game")
            assertTrue(game.goldScore > game.silverScore, "gold not above silver for $game")
        }
    }

    @Test
    fun negativeStartRoundIsClampedToZero() {
        val game = GameType.VALUE_COMPARISON
        assertEquals(0, game.difficultyBonus(startRound = -3, baseScore = 4, adaptiveDifficulty = true))
    }

    @Test
    fun lowerScoreIsBetterNeverGetsDifficultyBonus() {
        val game = GameType.SCHULTE_TABLE
        assertEquals(0, game.difficultyBonus(startRound = 12, baseScore = 80, adaptiveDifficulty = true))
    }

    @Test
    fun totalWithBonusCanReachGoldWithoutFullBaseFromZero() {
        val game = GameType.MENTAL_CALCULATION
        val startRound = 10
        val baseScore = 9
        val bonus = game.difficultyBonus(startRound, baseScore, adaptiveDifficulty = true)
        val total = baseScore + bonus
        assertEquals(18, total)
        assertTrue(game.meetsScore(total, game.goldScore))
        assertFalse(game.meetsScore(baseScore, game.goldScore))
    }

    @Test
    fun zeroBaseNeverUnlocksGoldEvenWithHighStartRound() {
        val game = GameType.GHOST_GRID
        val bonus = game.difficultyBonus(startRound = 20, baseScore = 0, adaptiveDifficulty = true)
        val total = 0 + bonus
        assertEquals(0, total)
        assertFalse(game.meetsScore(total, game.goldScore))
    }

    @Test
    fun gamesWithoutDifficultyRampAreNotAdaptive() {
        // Their generators never read the round, so a resume bonus would reward playtime only.
        assertFalse(ColoredShapesGame().adaptiveDifficulty)
        assertFalse(ColorConfusionGame().adaptiveDifficulty)
    }
}
