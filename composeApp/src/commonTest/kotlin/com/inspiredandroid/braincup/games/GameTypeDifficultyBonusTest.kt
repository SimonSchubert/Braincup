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
        assertEquals(10, game.difficultyBonus(startRound = 10, baseScore = 5, adaptiveDifficulty = true))
        assertEquals(0, game.difficultyBonus(startRound = 0, baseScore = 18, adaptiveDifficulty = true))
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
        val baseScore = 8
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
}
