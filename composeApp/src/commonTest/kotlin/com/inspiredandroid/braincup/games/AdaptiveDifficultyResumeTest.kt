package com.inspiredandroid.braincup.games

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Adaptive games resume at a stored round, so their difficulty must be derived from the
 * round value, not stepped on exact round matches (which never fire when resuming past them).
 * Otherwise veterans play the easiest difficulty while collecting the difficulty bonus.
 */
class AdaptiveDifficultyResumeTest {

    @Test
    fun mentalCalculationRestoresMaxNumberWhenResumed() {
        fun maxNumberAtStartRound(startRound: Int): Int = MentalCalculationGame()
            .apply {
                round = startRound
                nextRound()
            }.maxNumber

        assertEquals(30, maxNumberAtStartRound(0))
        assertEquals(50, maxNumberAtStartRound(5))
        assertEquals(70, maxNumberAtStartRound(10))
        assertEquals(100, maxNumberAtStartRound(13))
        assertEquals(150, maxNumberAtStartRound(19))
        assertEquals(150, maxNumberAtStartRound(40))
    }

    @Test
    fun sherlockCalculationRestoresMinNumbersNeededWhenResumed() {
        fun minNumbersAtStartRound(startRound: Int): Int = SherlockCalculationGame()
            .apply {
                round = startRound
                nextRound()
            }.minNumbersNeeded

        assertEquals(2, minNumbersAtStartRound(0))
        assertEquals(3, minNumbersAtStartRound(3))
        assertEquals(4, minNumbersAtStartRound(5))
        assertEquals(4, minNumbersAtStartRound(20))
    }

    @Test
    fun mentalRotationsFigureSizeIsDerivedFromRound() {
        fun cubesAtStartRound(startRound: Int): Int = MentalRotationsGame(Random(5L))
            .apply {
                round = startRound
                nextRound()
            }.referenceCubes.size

        // Arms grow at rounds 3, 6 and 10, and each arm adds 2 or 3 cubes, so a resumed run must
        // come back bigger than a fresh one rather than restarting at the two-arm size.
        assertEquals(true, cubesAtStartRound(0) < cubesAtStartRound(6))
        assertEquals(true, cubesAtStartRound(6) < cubesAtStartRound(20))
    }

    @Test
    fun trioHardnessIsDerivedFromRound() {
        fun hasHardness(startRound: Int, hardness: Int): Boolean {
            val game = TrioGame(Random(7L)).apply {
                round = startRound
                nextRound()
            }
            return findTrioSets(game.cards).any { indices ->
                trioSetHardness(game.cards[indices[0]], game.cards[indices[1]], game.cards[indices[2]]) == hardness
            }
        }

        assertEquals(true, hasHardness(0, 1))
        assertEquals(true, hasHardness(5, 2))
        assertEquals(true, hasHardness(10, 2))
    }

    @Test
    fun patternSequenceRestoresTierWhenResumed() {
        fun optionsAtStartRound(startRound: Int): Int = PatternSequenceGame(Random(3L))
            .apply {
                round = startRound
                nextRound()
            }.problem.options.size

        assertEquals(4, optionsAtStartRound(0))
        assertEquals(6, optionsAtStartRound(5))
        assertEquals(8, optionsAtStartRound(11))
        assertEquals(8, optionsAtStartRound(40))
    }
}
