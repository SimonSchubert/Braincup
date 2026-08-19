package com.inspiredandroid.braincup.games.iqtest

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.iq_band_average
import braincup.composeapp.generated.resources.iq_band_below_average
import braincup.composeapp.generated.resources.iq_band_high_average
import braincup.composeapp.generated.resources.iq_band_low_average
import braincup.composeapp.generated.resources.iq_band_superior
import braincup.composeapp.generated.resources.iq_band_very_superior
import com.inspiredandroid.braincup.games.matrix.MatrixGenerator
import org.jetbrains.compose.resources.StringResource
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * Turns a raw score on [IqTestBlueprint] into an estimated IQ.
 *
 * ## What the number is, and is not
 *
 * A real IQ score is norm-referenced: it is a person's rank inside a standardized sample of the
 * population, measured under controlled conditions. This app has no such sample, so the number
 * here is a *model estimate*, not a measurement, and the UI has to say so.
 *
 * ## The model
 *
 * A Rasch (1PL) model with a guessing floor, one difficulty per [IqTestBlueprint] tier:
 *
 *     P(correct | ability) = c + (1 - c) * logistic(ability - difficulty)
 *
 * where `c = 1 / optionCount` for the item's tier, ability and difficulty are in logits, and
 * [TIER_DIFFICULTY_LOGITS] holds the per-tier difficulties. Population ability is taken as
 * normal with SD [ABILITY_SD_LOGITS], mapped onto the conventional IQ scale of mean 100, SD 15.
 *
 * Summing that curve over the 30 items gives the expected raw score for any ability. Inverting it
 * gives [IQ_BY_RAW_SCORE]. The table is baked rather than computed at runtime so the mapping
 * cannot drift by a point between JVM and JS floating point; `IqScoringTest` recomputes it from
 * the constants below and asserts they still agree.
 *
 * ## Floor and ceiling
 *
 * A pure guesser is expected to score [IqTestBlueprint.chanceLevel] (5.25 of 30), so every raw
 * score at or below chance collapses onto [MIN_IQ] and [isBelowMeasurableRange] reports true.
 * At the top the curve saturates: 29 and 30 correct are nearly indistinguishable, so [MAX_IQ] is
 * a ceiling on what this test can resolve rather than a claim about the player.
 */
object IqScoring {

    const val MIN_IQ = 60
    const val MAX_IQ = 145

    const val MEAN_IQ = 100.0
    const val SD_IQ = 15.0

    val TIER_DIFFICULTY_LOGITS = doubleArrayOf(-2.4, -1.6, -0.8, 0.0, 0.8, 1.6, 2.4)

    const val ABILITY_SD_LOGITS = 1.4

    private val IQ_BY_RAW_SCORE = intArrayOf(
        60, 60, 60, 60, 60, 60, 60, 65,
        71, 76, 80, 83, 86, 89, 92, 95,
        97, 100, 102, 105, 108, 110, 113, 116,
        119, 122, 126, 130, 135, 144, 145,
    )

    /** Highest raw score that a guesser could plausibly reach, so the table floors through it. */
    private const val CHANCE_CEILING = 6

    fun iqFor(rawScore: Int): Int = IQ_BY_RAW_SCORE[rawScore.coerceIn(0, IqTestBlueprint.ITEM_COUNT)]

    fun isBelowMeasurableRange(rawScore: Int): Boolean = rawScore <= CHANCE_CEILING

    fun percentileFor(iq: Int): Double = standardNormalCdf((iq - MEAN_IQ) / SD_IQ) * 100.0

    fun bandFor(iq: Int): IqBand = IqBand.entries.last { iq >= it.minIq }

    /** Expected raw score at [abilityLogits] under the model above. Exercised by the scoring test. */
    fun expectedRawScore(abilityLogits: Double): Double = (0 until IqTestBlueprint.ITEM_COUNT).sumOf { item ->
        val tier = IqTestBlueprint.tierFor(item)
        val guess = 1.0 / MatrixGenerator.optionCountFor(tier)
        guess + (1 - guess) / (1 + exp(-(abilityLogits - TIER_DIFFICULTY_LOGITS[tier])))
    }

    /** Abramowitz & Stegun 26.2.17; absolute error below 7.5e-8, which is far past what we show. */
    private fun standardNormalCdf(z: Double): Double {
        val absZ = abs(z)
        val t = 1.0 / (1.0 + 0.2316419 * absZ)
        val poly = t * (
            0.319381530 + t * (
                -0.356563782 + t * (
                    1.781477937 + t * (-1.821255978 + t * 1.330274429)
                    )
                )
            )
        val upperTail = exp(-0.5 * absZ * absZ) / sqrt(2.0 * PI) * poly
        return if (z >= 0) 1.0 - upperTail else upperTail
    }
}

enum class IqBand(val minIq: Int, val labelRes: StringResource) {
    BELOW_AVERAGE(0, Res.string.iq_band_below_average),
    LOW_AVERAGE(80, Res.string.iq_band_low_average),
    AVERAGE(90, Res.string.iq_band_average),
    HIGH_AVERAGE(110, Res.string.iq_band_high_average),
    SUPERIOR(120, Res.string.iq_band_superior),
    VERY_SUPERIOR(130, Res.string.iq_band_very_superior),
}
