package com.inspiredandroid.braincup.games.iqtest

import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IqScoringTest {

    @Test
    fun tableIsMonotoneAcrossEveryRawScore() {
        var previous = IqScoring.iqFor(0)
        for (raw in 1..IqTestBlueprint.ITEM_COUNT) {
            val current = IqScoring.iqFor(raw)
            assertTrue(current >= previous, "raw $raw dropped from $previous to $current")
            previous = current
        }
    }

    @Test
    fun tableFloorsAtChanceAndCeilingsAtPerfect() {
        for (raw in 0..6) {
            assertEquals(IqScoring.MIN_IQ, IqScoring.iqFor(raw), "raw $raw should sit on the floor")
            assertTrue(IqScoring.isBelowMeasurableRange(raw))
        }
        assertTrue(!IqScoring.isBelowMeasurableRange(7))
        assertEquals(IqScoring.MAX_IQ, IqScoring.iqFor(IqTestBlueprint.ITEM_COUNT))
    }

    @Test
    fun outOfRangeRawScoresClamp() {
        assertEquals(IqScoring.MIN_IQ, IqScoring.iqFor(-5))
        assertEquals(IqScoring.MAX_IQ, IqScoring.iqFor(IqTestBlueprint.ITEM_COUNT + 5))
    }

    /**
     * The published table is baked so it cannot drift between JVM and JS floating point. This
     * recomputes it from the documented model and fails if the two ever part company.
     */
    @Test
    fun bakedTableStillMatchesTheModel() {
        for (raw in 7 until IqTestBlueprint.ITEM_COUNT) {
            val modelled = iqForRawScoreFromModel(raw)
            val baked = IqScoring.iqFor(raw)
            assertTrue(
                abs(modelled - baked) <= 1,
                "raw $raw: table says $baked, model says $modelled",
            )
        }
    }

    @Test
    fun chanceLevelMatchesTheOptionCountsTheBlueprintActuallyUses() {
        // Expected raw score for an infinitely weak player is pure guessing.
        assertTrue(abs(IqScoring.expectedRawScore(-40.0) - IqTestBlueprint.chanceLevel) < 1e-6)
    }

    @Test
    fun percentileIsCentredOnTheMean() {
        assertEquals(50, IqScoring.percentileFor(100).roundToInt())
        assertEquals(84, IqScoring.percentileFor(115).roundToInt())
        assertEquals(16, IqScoring.percentileFor(85).roundToInt())
        assertEquals(98, IqScoring.percentileFor(130).roundToInt())
    }

    @Test
    fun percentileRisesWithScore() {
        var previous = IqScoring.percentileFor(IqScoring.MIN_IQ)
        for (iq in IqScoring.MIN_IQ + 1..IqScoring.MAX_IQ) {
            val current = IqScoring.percentileFor(iq)
            assertTrue(current > previous, "percentile did not rise at $iq")
            previous = current
        }
    }

    @Test
    fun bandsCoverTheWholeRangeAndSwitchAtTheirBoundaries() {
        assertEquals(IqBand.BELOW_AVERAGE, IqScoring.bandFor(IqScoring.MIN_IQ))
        assertEquals(IqBand.BELOW_AVERAGE, IqScoring.bandFor(79))
        assertEquals(IqBand.LOW_AVERAGE, IqScoring.bandFor(80))
        assertEquals(IqBand.AVERAGE, IqScoring.bandFor(90))
        assertEquals(IqBand.AVERAGE, IqScoring.bandFor(109))
        assertEquals(IqBand.HIGH_AVERAGE, IqScoring.bandFor(110))
        assertEquals(IqBand.SUPERIOR, IqScoring.bandFor(120))
        assertEquals(IqBand.VERY_SUPERIOR, IqScoring.bandFor(130))
        assertEquals(IqBand.VERY_SUPERIOR, IqScoring.bandFor(IqScoring.MAX_IQ))
    }

    /** Bisects [IqScoring.expectedRawScore], which is the inverse the baked table was built from. */
    private fun iqForRawScoreFromModel(rawScore: Int): Int {
        var low = -6.0
        var high = 6.0
        repeat(200) {
            val mid = (low + high) / 2
            if (IqScoring.expectedRawScore(mid) < rawScore) low = mid else high = mid
        }
        val theta = (low + high) / 2
        val iq = IqScoring.MEAN_IQ + theta / IqScoring.ABILITY_SD_LOGITS * IqScoring.SD_IQ
        return iq.roundToInt().coerceIn(IqScoring.MIN_IQ, IqScoring.MAX_IQ)
    }
}
