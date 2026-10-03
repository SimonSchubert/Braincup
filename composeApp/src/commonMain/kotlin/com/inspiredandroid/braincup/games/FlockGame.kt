package com.inspiredandroid.braincup.games

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.app.FlockUiState
import com.inspiredandroid.braincup.games.tools.currentTimeMillis
import kotlin.random.Random

/**
 * The Eriksen flanker task: answer the direction the middle bird flies, while the birds either side
 * of it fly either the same way or the opposite way.
 *
 * The flankers are not neutral noise. Every one of them points along a valid response, so on an
 * incongruent trial they prime the wrong swipe and it has to be held back. The time that costs over
 * a congruent trial is the measurement, and it only exists because the flankers sit close enough to
 * the target that they cannot be ignored by looking away from them. See `docs/game-science.md`.
 *
 * Four directions rather than the classic two: a swipe has four natural answers, and a guess is
 * right a quarter of the time instead of half. Incongruent flankers always point the *opposite* way,
 * never sideways, so every incongruent trial carries the same, maximal response conflict instead of
 * a mix of two strengths.
 *
 * Congruency is dealt from a shuffled bag of [BAG_SIZE] holding [CONGRUENT_PER_BAG] congruent
 * trials, an even split as in Eriksen & Eriksen (1974), for the same reason Color Confusion deals
 * its own: over a sixty-second run the mix should be a controlled quantity, not an accident.
 */
class FlockGame(
    private val random: Random = Random.Default,
) : Game() {
    enum class Direction(val rotationDegrees: Float) {
        UP(270f),
        RIGHT(0f),
        DOWN(90f),
        LEFT(180f),
        ;

        val opposite: Direction
            get() = when (this) {
                UP -> DOWN
                RIGHT -> LEFT
                DOWN -> UP
                LEFT -> RIGHT
            }
    }

    @Immutable
    data class Trial(val target: Direction, val flankers: Direction) {
        val isCongruent: Boolean get() = target == flankers
    }

    /** A flanker list does not ramp: every trial is the same difficulty. */
    override val adaptiveDifficulty: Boolean = false

    var trial: Trial = Trial(Direction.RIGHT, Direction.RIGHT)
        private set

    private val congruentMillis = mutableListOf<Long>()
    private val incongruentMillis = mutableListOf<Long>()

    private val congruencyBag = ArrayDeque<Boolean>()

    /**
     * Set in [generateRound], one frame before the trial paints. The offset is the same on every
     * trial and cancels in the difference between the two conditions, the only thing this feeds.
     */
    private var shownAt: Long = 0L

    private var feedback: AnswerFeedbackState = AnswerFeedbackState.NORMAL

    override fun generateRound() {
        feedback = AnswerFeedbackState.NORMAL
        trial = nextTrial()
        shownAt = currentTimeMillis()
    }

    /** Null while the feedback beat is up, so a second swipe cannot answer the same trial twice. */
    fun answer(direction: Direction): Boolean? {
        if (feedback != AnswerFeedbackState.NORMAL) return null

        val isCorrect = direction == trial.target
        feedback = if (isCorrect) AnswerFeedbackState.CORRECT else AnswerFeedbackState.WRONG

        if (!isCorrect) {
            answeredAllCorrect = false
            return false
        }

        // Only correct trials carry a usable reading: an error time is a time to the wrong decision.
        val elapsed = currentTimeMillis() - shownAt
        if (trial.isCongruent) congruentMillis += elapsed else incongruentMillis += elapsed
        return true
    }

    /**
     * Median correct incongruent time minus median correct congruent time, in milliseconds, or null
     * when either condition has fewer than [MIN_TRIALS_PER_CONDITION] correct trials. Like Color
     * Confusion's, this is a congruency effect rather than pure interference (there is no neutral
     * condition), and it is reported as it comes out, negative included.
     */
    fun congruencyEffectMillis(): Int? {
        if (congruentMillis.size < MIN_TRIALS_PER_CONDITION) return null
        if (incongruentMillis.size < MIN_TRIALS_PER_CONDITION) return null
        return (median(incongruentMillis) - median(congruentMillis)).toInt()
    }

    override fun isCorrect(input: String): Boolean = input == trial.target.name

    override fun solution(): String = trial.target.name

    override fun toUiState(): FlockUiState = FlockUiState(
        target = trial.target,
        flankers = trial.flankers,
        feedback = feedback,
    )

    private fun nextTrial(): Trial {
        // No two trials in a row share a target: a repeated response carries a speed-up of its own
        // that would land unevenly across the two conditions.
        val previousTarget = if (round == 0) null else trial.target
        val target = Direction.entries.filter { it != previousTarget }.random(random)
        val flankers = if (nextIsCongruent()) target else target.opposite
        return Trial(target = target, flankers = flankers)
    }

    private fun nextIsCongruent(): Boolean {
        if (congruencyBag.isEmpty()) {
            congruencyBag += List(BAG_SIZE) { it < CONGRUENT_PER_BAG }.shuffled(random)
        }
        return congruencyBag.removeFirst()
    }

    private fun median(values: List<Long>): Long {
        val sorted = values.sorted()
        val middle = sorted.size / 2
        return if (sorted.size % 2 == 1) sorted[middle] else (sorted[middle - 1] + sorted[middle]) / 2
    }

    companion object {
        const val FLANKERS_PER_SIDE = 2

        const val BAG_SIZE = 4
        const val CONGRUENT_PER_BAG = 2

        const val MIN_TRIALS_PER_CONDITION = 5

        const val CORRECT_FEEDBACK_MILLIS = 250L
        const val WRONG_FEEDBACK_MILLIS = 700L
    }
}
