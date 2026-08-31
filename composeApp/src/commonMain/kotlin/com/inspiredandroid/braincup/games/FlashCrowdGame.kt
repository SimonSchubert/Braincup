package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.FeedbackMessage
import com.inspiredandroid.braincup.app.FlashCrowdUiState
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Which side has more dots, judged too fast to count.
 *
 * The task is the standard Approximate Number System acuity measure: two arrays flash, the player
 * picks the more numerous, and difficulty is the ratio between the counts rather than their size
 * (see [getDifficultyRatio]).
 *
 * ## Why the dots are sized the way they are
 *
 * Number is not the only thing that varies when you put more dots on a side. Total ink and average
 * dot size both move with count, and either one answers the question without anyone judging number
 * at all. Sizing every dot independently, as this used to, holds average size constant and so
 * hands the player cumulative area as a perfect cue on every round: the game was winnable on
 * "which side is blobbier".
 *
 * The two cues cannot both be removed, and no sizing rule can manage it. Total area is count times
 * average dot area, so pinning either one against count forces the other to track it: equalise the
 * areas and the busier side simply becomes the one with the smaller dots. Panamath (Halberda,
 * Mazzocco & Feigenson, 2008) splits the difference by alternating the two matchings across trials,
 * which on this game's counts and ratios leaves ink right 69% of the time and dot size 76%.
 *
 * So this aims at the reachable target instead, taking the continuous version of that idea (Gebuis
 * & Reynvoet, 2011): total area scales with the *square root* of the count, the exact midpoint
 * between the two matchings, so neither cue is favoured, and [AREA_JITTER_LOG] then varies each
 * side's area independently to bury most of what is left. Both cues land near 60%, and the residual
 * falls where it costs least: about 72% at the 1:2 ratio, where telling the sides apart by number is
 * already trivial, and about 54% at the 9:10 ceiling, where the judgment actually decides a run.
 *
 * `FlashCrowdGameTest` measures all of that rather than trusting it, and fails if either cue creeps
 * back above the alternating baseline, if the two stop leaking equally, or if the leak stops
 * shrinking as the ratio narrows.
 */
class FlashCrowdGame(private val random: Random = Random.Default) : Game() {
    override val adaptiveDifficulty: Boolean = false

    enum class Side { LEFT, RIGHT }

    data class Dot(val x: Float, val y: Float, val radius: Float)

    var moreSide = Side.LEFT
    var leftDots = emptyList<Dot>()
    var rightDots = emptyList<Dot>()
    var leftCount = 0
    var rightCount = 0
    var roundKey = 0

    override fun generateRound() {
        moreSide = Side.entries.random(random)

        val ratio = getDifficultyRatio()
        val moreCount = random.nextInt(15, 26)
        val fewerCount = (moreCount * ratio).toInt().coerceAtLeast(1)

        if (moreSide == Side.LEFT) {
            leftCount = moreCount
            rightCount = fewerCount
        } else {
            leftCount = fewerCount
            rightCount = moreCount
        }

        leftDots = placeDots(radiiFor(leftCount))
        rightDots = placeDots(radiiFor(rightCount))
        roundKey++
    }

    override fun isCorrect(input: String): Boolean = input == moreSide.name.lowercase()

    override fun solution(): String {
        val count = if (moreSide == Side.LEFT) leftCount else rightCount
        return "${moreSide.name.lowercase().replaceFirstChar { it.uppercase() }} ($count)"
    }

    override fun solutionMessage(): FeedbackMessage {
        val count = if (moreSide == Side.LEFT) leftCount else rightCount
        return FeedbackMessage.SideCount(isLeft = moreSide == Side.LEFT, count = count)
    }

    override fun hint(): String? = null

    override fun toUiState() = FlashCrowdUiState(
        roundKey = roundKey,
        leftDots = leftDots.map { FlashCrowdUiState.Dot(it.x, it.y, it.radius) }.toImmutableList(),
        rightDots = rightDots.map { FlashCrowdUiState.Dot(it.x, it.y, it.radius) }.toImmutableList(),
    )

    /**
     * Ratio between the two counts, which is what actually sets difficulty: discriminating 20 from
     * 10 is easy at any size, 20 from 18 is hard.
     */
    internal fun getDifficultyRatio(): Double = when {
        round <= 1 -> 1.0 / 2.0
        round <= 3 -> 2.0 / 3.0
        round <= 5 -> 3.0 / 4.0
        round <= 7 -> 4.0 / 5.0
        round <= 9 -> 5.0 / 6.0
        round <= 11 -> 6.0 / 7.0
        round <= 13 -> 7.0 / 8.0
        round <= 15 -> 8.0 / 9.0
        else -> 9.0 / 10.0
    }

    /**
     * Radii for one side's [count] dots.
     *
     * Total area is [REFERENCE_AREA] scaled by the square root of the count, which is the midpoint
     * between the two matchings the literature alternates: at an exponent of 1 average dot size is
     * constant and area gives the answer away, at 0 area is constant and dot size gives it away,
     * and at 0.5 each is wrong by the same amount. That balances the two cues but does not weaken
     * them, so [AREA_JITTER_LOG] then varies the total independently per side, which is the part
     * that actually costs a cue its predictive value.
     *
     * The per-dot [DOT_JITTER] comes first so a side is not a field of identical discs; the radii
     * are then scaled by the single factor that lands their areas on the target. Scaling keeps the
     * jitter's spread and hits the total exactly, which clamping each radius separately would not.
     */
    private fun radiiFor(count: Int): List<Float> {
        val areaJitter = exp((random.nextFloat() - 0.5f) * 2f * AREA_JITTER_LOG)
        val targetArea = REFERENCE_AREA * sqrt(count.toFloat() / REFERENCE_COUNT) * areaJitter
        val jittered = List(count) { 1f + (random.nextFloat() - 0.5f) * 2f * DOT_JITTER }
        val unscaled = PI.toFloat() * jittered.fold(0f) { sum, r -> sum + r * r }
        val scale = sqrt(targetArea / unscaled)
        // The clamp is a backstop, not part of the design: the constants are chosen so the band is
        // not reached in normal play, and every radius that hits it perturbs the area it was just
        // scaled to. `FlashCrowdGameTest` fails if it ever starts biting.
        return jittered.map { (it * scale).coerceIn(MIN_RADIUS, MAX_RADIUS) }
    }

    /**
     * Scatter one dot per radius, keeping them apart where it can.
     *
     * The separation it demands decays across the attempt budget and reaches zero at the end, so
     * every radius always yields a dot. That matters for more than looks: the answer is scored
     * against [leftCount] and [rightCount], so a dot that failed to find a home would leave the
     * player looking at an array that does not match the count they are being marked on.
     */
    private fun placeDots(radii: List<Float>): List<Dot> {
        val dots = mutableListOf<Dot>()
        radii.forEach { radius ->
            var candidate = randomDot(radius)
            var attempt = 1
            while (attempt < PLACEMENT_ATTEMPTS &&
                dots.any { it.tooClose(candidate, SEPARATION * (1f - attempt.toFloat() / PLACEMENT_ATTEMPTS)) }
            ) {
                candidate = randomDot(radius)
                attempt++
            }
            dots.add(candidate)
        }
        return dots
    }

    private fun randomDot(radius: Float): Dot {
        val span = (1f - 2 * radius).coerceAtLeast(0f)
        return Dot(
            x = radius + random.nextFloat() * span,
            y = radius + random.nextFloat() * span,
            radius = radius,
        )
    }

    private fun Dot.tooClose(other: Dot, separation: Float): Boolean {
        val dx = other.x - x
        val dy = other.y - y
        return sqrt(dx * dx + dy * dy) < (radius + other.radius) * separation
    }

    companion object {
        /**
         * Safety band on a drawn radius, in fractions of the canvas width.
         *
         * Wide enough never to be reached in play (measured extremes are 0.016 and 0.072), because
         * a clamped radius silently misses the area it was just scaled to. It is a guard against a
         * pathological count, not a tuning knob; `FlashCrowdGameTest` fails if it starts biting.
         */
        internal const val MIN_RADIUS = 0.013f
        internal const val MAX_RADIUS = 0.085f

        /** The count and dot radius the area scale is anchored to: mid-range for both. */
        private const val REFERENCE_COUNT = 16f
        private const val REFERENCE_RADIUS = 0.034f
        private const val REFERENCE_AREA = REFERENCE_COUNT * PI.toFloat() * REFERENCE_RADIUS * REFERENCE_RADIUS

        /**
         * Half-width of a side's total-area jitter, in log units: the factor spans `e^-0.7 .. e^0.7`,
         * or 0.50x to 2.01x.
         *
         * Chosen by sweeping it against how often each cue then gives the answer away. Widening it
         * keeps helping, but with a falling return and a real cost: the dots at the bottom of the
         * range have to stay big enough to see during a 750ms flash. At 0.7 both cues sit near 60%
         * and the smallest dot drawn is about 0.016 of the canvas, a shade under what the old fixed
         * band already shipped. Past roughly 1.0 the gain is under a point per step and the dots
         * start disappearing.
         */
        private const val AREA_JITTER_LOG = 0.7f

        /** Spread of one side's radii around their mean, as a fraction of it. */
        private const val DOT_JITTER = 0.2f

        private const val PLACEMENT_ATTEMPTS = 50

        /** Fraction of the summed radii two dot centres must clear to count as separated. */
        private const val SEPARATION = 0.8f
    }
}
