package com.inspiredandroid.braincup.games.iqtest

import com.inspiredandroid.braincup.games.matrix.MatrixGenerator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IqTestTest {

    @Test
    fun blueprintCoversThirtyItemsAcrossEveryTier() {
        assertEquals(30, IqTestBlueprint.ITEM_COUNT)
        val tiers = (0 until IqTestBlueprint.ITEM_COUNT).map { IqTestBlueprint.tierFor(it) }
        assertEquals((0..MatrixGenerator.MAX_DIFFICULTY).toList(), tiers.distinct())
        assertEquals(tiers, tiers.sorted(), "the ramp must never step backwards")
        assertEquals(
            IqTestBlueprint.ITEM_COUNT,
            (0..MatrixGenerator.MAX_DIFFICULTY).sumOf { IqTestBlueprint.itemCountForTier(it) },
        )
    }

    @Test
    fun everyItemMatchesTheOptionCountItsTierDictates() {
        val test = IqTest(seed = 99L)
        for (index in 0 until IqTestBlueprint.ITEM_COUNT) {
            val problem = test.problemAt(index)
            val expected = MatrixGenerator.optionCountFor(IqTestBlueprint.tierFor(index))
            assertEquals(expected, problem.options.size, "item $index")
            assertEquals(
                MatrixGenerator.optionColumnsFor(expected),
                problem.optionColumns,
                "item $index columns",
            )
            assertTrue(problem.correctOptionIndex in problem.options.indices)
        }
    }

    @Test
    fun outOfRangeItemsAreRejected() {
        val test = IqTest(seed = 1L)
        assertFailsWith<IllegalArgumentException> { test.problemAt(-1) }
        assertFailsWith<IllegalArgumentException> { test.problemAt(IqTestBlueprint.ITEM_COUNT) }
    }

    @Test
    fun sameSeedReproducesTheSameTest() {
        val first = IqTest(seed = 7L)
        val second = IqTest(seed = 7L)
        for (index in 0 until IqTestBlueprint.ITEM_COUNT) {
            assertEquals(first.problemAt(index), second.problemAt(index), "item $index")
        }
    }

    /** Items are built lazily, so jumping ahead must still produce the in-order sequence. */
    @Test
    fun jumpingAheadDoesNotChangeWhichItemsAppear() {
        val sequential = IqTest(seed = 3L)
        val expected = (0 until IqTestBlueprint.ITEM_COUNT).map { sequential.problemAt(it) }

        val jumped = IqTest(seed = 3L)
        assertEquals(expected[20], jumped.problemAt(20))
        assertEquals(expected[0], jumped.problemAt(0))
        assertEquals(expected[29], jumped.problemAt(29))
    }

    @Test
    fun rawScoreCountsOnlyCorrectPicksAndTreatsSkipsAsWrong() {
        val test = IqTest(seed = 11L)
        val answeredCorrectly = 12
        repeat(IqTestBlueprint.ITEM_COUNT) { index ->
            test.goTo(index)
            when {
                index < answeredCorrectly -> test.select(test.problemAt(index).correctOptionIndex)
                index < answeredCorrectly + 5 -> test.select(wrongOptionFor(test, index))
                else -> Unit
            }
        }
        assertEquals(answeredCorrectly, test.rawScore)
        assertEquals(answeredCorrectly + 5, test.answeredCount)
    }

    @Test
    fun allCorrectAndAllSkippedHitTheEndsOfTheScale() {
        val perfect = IqTest(seed = 5L)
        repeat(IqTestBlueprint.ITEM_COUNT) { index ->
            perfect.goTo(index)
            perfect.select(perfect.problemAt(index).correctOptionIndex)
        }
        assertEquals(IqTestBlueprint.ITEM_COUNT, perfect.rawScore)
        assertEquals(IqScoring.MAX_IQ, IqScoring.iqFor(perfect.rawScore))

        val skipped = IqTest(seed = 5L)
        assertEquals(0, skipped.rawScore)
        assertEquals(0, skipped.answeredCount)
        assertEquals(IqScoring.MIN_IQ, IqScoring.iqFor(skipped.rawScore))
    }

    @Test
    fun tierBreakdownSumsToTheRawScore() {
        val test = IqTest(seed = 21L)
        repeat(IqTestBlueprint.ITEM_COUNT) { index ->
            test.goTo(index)
            if (index % 3 != 0) test.select(test.problemAt(index).correctOptionIndex)
        }
        val breakdown = test.tierBreakdown()
        assertEquals(test.rawScore, breakdown.sumOf { it.correct })
        assertEquals(IqTestBlueprint.ITEM_COUNT, breakdown.sumOf { it.total })
        breakdown.forEach { tier ->
            assertEquals(IqTestBlueprint.itemCountForTier(tier.tier), tier.total)
            assertTrue(tier.correct <= tier.total)
        }
    }

    @Test
    fun navigationStopsAtBothEnds() {
        val test = IqTest(seed = 13L)
        assertEquals(0, test.currentIndex)
        assertFalse(test.previous())
        repeat(IqTestBlueprint.ITEM_COUNT - 1) { assertTrue(test.next()) }
        assertTrue(test.isOnLastItem)
        assertFalse(test.next())
        assertEquals(IqTestBlueprint.ITEM_COUNT - 1, test.currentIndex)
    }

    @Test
    fun goToClampsInsteadOfThrowing() {
        val test = IqTest(seed = 17L)
        test.goTo(500)
        assertEquals(IqTestBlueprint.ITEM_COUNT - 1, test.currentIndex)
        test.goTo(-9)
        assertEquals(0, test.currentIndex)
    }

    @Test
    fun aSelectionCanBeChangedBeforeTheTestEnds() {
        val test = IqTest(seed = 23L)
        val correct = test.problemAt(0).correctOptionIndex
        test.select(wrongOptionFor(test, 0))
        assertEquals(0, test.rawScore)
        assertEquals(1, test.answeredCount)
        test.select(correct)
        assertEquals(1, test.rawScore)
        assertEquals(correct, test.responseAt(0))
        assertNull(test.responseAt(1))
    }

    private fun wrongOptionFor(test: IqTest, index: Int): Int {
        val problem = test.problemAt(index)
        return problem.options.indices.first { it != problem.correctOptionIndex }
    }
}
