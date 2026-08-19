package com.inspiredandroid.braincup.games.iqtest

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The scale is only honest if it does not flatter someone who learned nothing from the matrix.
 * These run whole simulated attempts through the real generator and scale rather than asserting on
 * the table in isolation.
 */
class IqTestGuessingTest {

    @Test
    fun aRandomGuesserLandsOnTheFloor() {
        val random = Random(2024)
        val scores = (0 until ATTEMPTS).map { attempt ->
            val test = IqTest(seed = random.nextLong())
            repeat(IqTestBlueprint.ITEM_COUNT) { index ->
                test.goTo(index)
                test.select(random.nextInt(test.problemAt(index).options.size))
            }
            test.rawScore
        }
        val mean = scores.average()
        assertTrue(
            mean in IqTestBlueprint.chanceLevel - 2.0..IqTestBlueprint.chanceLevel + 2.0,
            "guessing averaged $mean, expected near chance ${IqTestBlueprint.chanceLevel}",
        )
        val flattered = scores.count { !IqScoring.isBelowMeasurableRange(it) && IqScoring.iqFor(it) > 90 }
        assertTrue(
            flattered <= ATTEMPTS / 20,
            "$flattered of $ATTEMPTS guessed runs scored above 90; the scale flatters chance",
        )
    }

    /**
     * Solving the easy half and guessing the rest should read as roughly average, which is the
     * anchor the whole scale hangs on.
     */
    @Test
    fun solvingTheEasyHalfReadsAsAverage() {
        val random = Random(7)
        val scores = (0 until ATTEMPTS).map {
            val test = IqTest(seed = random.nextLong())
            repeat(IqTestBlueprint.ITEM_COUNT) { index ->
                test.goTo(index)
                val problem = test.problemAt(index)
                val pick = if (IqTestBlueprint.tierFor(index) <= 2) {
                    problem.correctOptionIndex
                } else {
                    random.nextInt(problem.options.size)
                }
                test.select(pick)
            }
            IqScoring.iqFor(test.rawScore)
        }
        val mean = scores.average()
        assertTrue(mean in 85.0..105.0, "easy-half solver averaged IQ $mean, expected near 100")
    }

    @Test
    fun skippingEverythingScoresBelowGuessing() {
        val skipped = IqTest(seed = 1L)
        assertTrue(IqScoring.isBelowMeasurableRange(skipped.rawScore))
        assertTrue(IqScoring.iqFor(skipped.rawScore) == IqScoring.MIN_IQ)
    }

    private companion object {
        const val ATTEMPTS = 60
    }
}
