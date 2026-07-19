package com.inspiredandroid.braincup.games

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
}
