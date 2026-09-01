package com.inspiredandroid.braincup.ui.components

import com.inspiredandroid.braincup.games.TrioCard
import com.inspiredandroid.braincup.games.isTrioSet
import com.inspiredandroid.braincup.games.trioSetHardness
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrioDemoExamplesTest {

    @Test
    fun everyValidExampleIsASet() {
        (TrioTwoMatchExamples + TrioOneMatchExamples).forEach { row ->
            assertEquals(3, row.size)
            assertTrue(isTrioSet(row[0], row[1], row[2]), "not a set: $row")
        }
    }

    @Test
    fun theCounterexampleIsNotASet() {
        assertEquals(3, TrioNoMatchExample.size)
        val (a, b, c) = TrioNoMatchExample
        assertFalse(isTrioSet(a, b, c))
        assertEquals(3, trioSetHardness(a, b, c))
    }

    @Test
    fun eachGroupHoldsTheNumberOfTraitsItsHeadingClaims() {
        TrioTwoMatchExamples.forEach { assertEquals(2, it.matchingTraits(), "$it") }
        TrioOneMatchExamples.forEach { assertEquals(1, it.matchingTraits(), "$it") }
    }

    @Test
    fun theSixRowsCoverEveryValidPatternExactlyOnce() {
        val patterns = (TrioTwoMatchExamples + TrioOneMatchExamples).map { it.matchPattern() }
        assertEquals(6, patterns.size)
        assertEquals(6, patterns.toSet().size, "duplicate pattern in $patterns")
    }

    private fun List<TrioCard>.matchPattern(): List<Boolean> = listOf(
        all { it.shape == this[0].shape },
        all { it.count == this[0].count },
        all { it.fill == this[0].fill },
    )

    private fun List<TrioCard>.matchingTraits(): Int = matchPattern().count { it }
}
