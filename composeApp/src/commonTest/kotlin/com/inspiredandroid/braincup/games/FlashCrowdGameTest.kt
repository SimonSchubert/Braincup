package com.inspiredandroid.braincup.games

import kotlin.math.PI
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The point of these is the confound, not the drawing.
 *
 * Cumulative dot area and average dot size both move with count unless something stops them, and
 * either one answers "which side has more" without anyone judging number. A game leaking one of
 * them is not the ANS task it claims to be, so the cue tests below are the reason this file exists:
 * they measure how often a player betting purely on ink, or purely on dot size, would be right.
 *
 * Neither can reach a coin flip. Total area is count times average dot area, so pinning one of them
 * against count drags the other away; see [FlashCrowdGame]. So the bar is not zero leak, which is
 * unreachable, but three things that are: each cue comes in under the alternating design this
 * replaced, the two leak by the same amount, and what remains sits on the wide ratios where the
 * number judgment was easy anyway rather than on the narrow ones that decide a run.
 */
class FlashCrowdGameTest {

    /**
     * Rounds sampled the way they are actually played rather than by running one game to round 400.
     *
     * A 60s run reaches somewhere around round 8-15, and the ratio ramp
     * ([FlashCrowdGame.getDifficultyRatio]) hardens with it. Sampling one long game would put
     * almost every round at the 9:10 ceiling, which is both the least representative case and the
     * most forgiving one to test against, since a narrow ratio hides a cue in the noise.
     */
    private fun rounds(seed: Long, games: Int = 60, roundsPerGame: Int = 15): List<Snapshot> {
        val random = Random(seed)
        return List(games) {
            val game = FlashCrowdGame(random)
            List(roundsPerGame) {
                game.nextRound()
                Snapshot(
                    moreSide = game.moreSide,
                    leftCount = game.leftCount,
                    rightCount = game.rightCount,
                    leftDots = game.leftDots,
                    rightDots = game.rightDots,
                )
            }
        }.flatten()
    }

    private data class Snapshot(
        val moreSide: FlashCrowdGame.Side,
        val leftCount: Int,
        val rightCount: Int,
        val leftDots: List<FlashCrowdGame.Dot>,
        val rightDots: List<FlashCrowdGame.Dot>,
    ) {
        private val moreDots get() = if (moreSide == FlashCrowdGame.Side.LEFT) leftDots else rightDots
        private val fewerDots get() = if (moreSide == FlashCrowdGame.Side.LEFT) rightDots else leftDots

        val allDots get() = leftDots + rightDots

        /** True when the busier side also carried the most ink. */
        val moreSideHadMoreArea: Boolean get() = moreDots.area() > fewerDots.area()

        /** True when the busier side also had the smaller dots. */
        val moreSideHadSmallerDots: Boolean
            get() = moreDots.map { it.radius }.average() < fewerDots.map { it.radius }.average()

        private fun List<FlashCrowdGame.Dot>.area(): Float = fold(0f) { sum, dot -> sum + PI.toFloat() * dot.radius * dot.radius }
    }

    /** How often a fixed heuristic would be right. A cue is dead when this sits near 0.5. */
    private fun share(rounds: List<Snapshot>, cue: (Snapshot) -> Boolean): Float = rounds.count(cue).toFloat() / rounds.size

    /**
     * What the alternating-matchings design this replaces would score on the same rounds, measured
     * rather than quoted: it matches on average dot size half the time and on total area the other
     * half. Both cues have to come in under it, or the rework was not worth doing.
     */
    private val binaryBaselineInk = 0.69f
    private val binaryBaselineSize = 0.76f

    @Test
    fun `betting on total ink beats the design this replaced`() {
        // The confound the sizing exists to kill. With every dot sized independently this was 1.0:
        // the busier side always carried more ink, so the game never had to be played as a number
        // task at all.
        val hit = share(rounds(seed = 1L)) { it.moreSideHadMoreArea }
        assertTrue(hit < binaryBaselineInk, "total ink predicted the answer on ${hit * 100}% of rounds")
    }

    @Test
    fun `betting on dot size beats the design this replaced`() {
        // The mirror image, and the trap in the obvious fix: equalising the areas would invert the
        // cue rather than remove it, handing the player "smaller dots means more" instead.
        val hit = share(rounds(seed = 2L)) { it.moreSideHadSmallerDots }
        assertTrue(hit < binaryBaselineSize, "dot size predicted the answer on ${hit * 100}% of rounds")
    }

    @Test
    fun `the two cues leak by the same amount`() {
        // Neither can be driven to chance, so the design settles for spending the residual evenly.
        // A change that tips it lands here first: favouring one cue means a player who finds it
        // does better than one who finds the other, which is exactly the strategy to deny them.
        val sample = rounds(seed = 3L)
        val ink = share(sample) { it.moreSideHadMoreArea }
        val size = share(sample) { it.moreSideHadSmallerDots }
        assertTrue(abs(ink - size) < 0.05f, "ink leaked ${ink * 100}% but size leaked ${size * 100}%")
    }

    @Test
    fun `what leak is left sits on the rounds that are already easy`() {
        // The residual is not spread evenly over the ramp, and that is the point. It is largest at
        // the widest ratio, where telling the sides apart by number is trivial anyway, and smallest
        // at the ceiling, where the judgment is actually hard and a cue would be worth having.
        val easy = rounds(seed = 4L, roundsPerGame = 2)
        val hard = rounds(seed = 5L, games = 60, roundsPerGame = 20).drop(16)
        val easyInk = share(easy) { it.moreSideHadMoreArea }
        val hardInk = share(hard) { it.moreSideHadMoreArea }
        assertTrue(hardInk < easyInk, "the leak did not shrink as the ratio narrowed")
        assertTrue(hardInk < 0.6f, "ink still predicted the answer on ${hardInk * 100}% of hard rounds")
    }

    @Test
    fun `every dot is drawn, so the array matches the count being scored`() {
        rounds(seed = 4L).forEach { round ->
            assertEquals(round.leftCount, round.leftDots.size, "left array did not match leftCount")
            assertEquals(round.rightCount, round.rightDots.size, "right array did not match rightCount")
        }
    }

    @Test
    fun `every dot stays inside the canvas`() {
        rounds(seed = 5L).forEach { round ->
            round.allDots.forEach { dot ->
                assertTrue(dot.x - dot.radius >= -0.001f, "dot ran off the left edge: $dot")
                assertTrue(dot.x + dot.radius <= 1.001f, "dot ran off the right edge: $dot")
                assertTrue(dot.y - dot.radius >= -0.001f, "dot ran off the top edge: $dot")
                assertTrue(dot.y + dot.radius <= 1.001f, "dot ran off the bottom edge: $dot")
            }
        }
    }

    @Test
    fun `the radius clamp never bites`() {
        // The clamp is a backstop. If a radius is actually reaching it, the area that radius was
        // just scaled to is wrong, and the cue balance the other tests check is being quietly
        // eroded; the constants need retuning rather than the clamp widening.
        val margin = 0.001f
        rounds(seed = 6L).forEach { round ->
            round.allDots.forEach { dot ->
                assertTrue(
                    dot.radius > FlashCrowdGame.MIN_RADIUS + margin &&
                        dot.radius < FlashCrowdGame.MAX_RADIUS - margin,
                    "radius ${dot.radius} reached the safety clamp",
                )
            }
        }
    }

    @Test
    fun `dots stay large enough to see and small enough to fit`() {
        rounds(seed = 7L).forEach { round ->
            val coverage = round.leftDots.fold(0f) { sum, d -> sum + PI.toFloat() * d.radius * d.radius }
            assertTrue(coverage < 0.5f, "dots covered ${coverage * 100}% of the canvas")
        }
    }
}
