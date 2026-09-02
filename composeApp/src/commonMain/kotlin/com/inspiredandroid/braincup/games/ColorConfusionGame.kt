package com.inspiredandroid.braincup.games

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.app.ColorConfusionUiState
import com.inspiredandroid.braincup.app.ColorSwatchCell
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.currentTimeMillis
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

/**
 * The Stroop task: name the colour a word is printed in, while the word itself names a colour.
 *
 * Reading is automatic and faster than colour naming, so on an incongruent trial the word supplies
 * a competing answer that has to be overridden. That override is the whole measurement, and it is
 * what an earlier version of this game had no room for: it laid nine words out in a grid and asked
 * the player to pick the ones whose word matched its ink, which makes the congruent cells the
 * targets and leaves nothing to inhibit. See `docs/game-science.md`.
 *
 * The response row is [RESPONSE_COLORS] in a fixed order for the whole run, so a response costs no
 * visual search and the time between trials is dominated by the conflict rather than by hunting
 * for a swatch.
 *
 * Congruency is dealt from a shuffled bag of [BAG_SIZE] holding [CONGRUENT_PER_BAG] congruent
 * trials rather than drawn per trial, so the mix is a controlled quantity over a run this short
 * instead of an accident of the draw, and neither condition can come out in a long streak.
 */
class ColorConfusionGame(
    private val random: Random = Random.Default,
) : Game() {
    /** One trial: a colour word, printed in an ink colour. Congruent when the two agree. */
    @Immutable
    data class Trial(val word: GameColor, val ink: GameColor) {
        val isCongruent: Boolean get() = word == ink
    }

    /**
     * Every trial is the same difficulty - a Stroop list does not ramp - so there is no round to
     * resume and no difficulty bonus to earn.
     */
    override val adaptiveDifficulty: Boolean = false

    var trial: Trial = Trial(RESPONSE_COLORS[0], RESPONSE_COLORS[0])
        private set

    /** Response times of correct trials, split by condition. The congruency effect is their gap. */
    private val congruentMillis = mutableListOf<Long>()
    private val incongruentMillis = mutableListOf<Long>()

    private val congruencyBag = ArrayDeque<Boolean>()

    /**
     * When the current trial went on screen. Set in [generateRound], which the controller calls
     * one frame before the trial actually paints, so every reading carries the same small render
     * offset. That offset cancels in the difference between the two conditions, which is the only
     * thing this is used for.
     */
    private var shownAt: Long = 0L

    /** The swatch the player last tapped and whether it was right, held for the feedback beat. */
    private var feedbackIndex: Int? = null
    private var feedbackWasCorrect = false

    override fun generateRound() {
        feedbackIndex = null
        trial = nextTrial()
        shownAt = currentTimeMillis()
    }

    /**
     * Answer with the 1-based swatch position, matching the other tap-a-figure boards. Returns
     * null while the feedback beat is up, so a double tap cannot answer the same trial twice.
     */
    fun answer(slot: Int): Boolean? {
        if (feedbackIndex != null) return null
        if (slot !in 1..RESPONSE_COLORS.size) return null

        val isCorrect = RESPONSE_COLORS[slot - 1] == trial.ink
        feedbackIndex = slot
        feedbackWasCorrect = isCorrect

        if (!isCorrect) {
            answeredAllCorrect = false
            return false
        }

        // Only correct trials carry a usable reading: an error time is a time to the wrong
        // decision, which is not the quantity the congruency effect is defined over.
        val elapsed = currentTimeMillis() - shownAt
        if (trial.isCongruent) congruentMillis += elapsed else incongruentMillis += elapsed
        return true
    }

    /**
     * Median correct incongruent time minus median correct congruent time, in milliseconds, or
     * null when either condition has fewer than [MIN_TRIALS_PER_CONDITION] correct trials to take
     * a median over.
     *
     * This is a *congruency effect*, not pure interference. Splitting interference from the speed-up
     * congruent trials get would need a third, neutral condition, and a sixty-second run does not
     * hold enough trials to divide three ways. The number can come out negative; that is a real
     * reading of a run and is reported as it is.
     */
    fun congruencyEffectMillis(): Int? {
        if (congruentMillis.size < MIN_TRIALS_PER_CONDITION) return null
        if (incongruentMillis.size < MIN_TRIALS_PER_CONDITION) return null
        return (median(incongruentMillis) - median(congruentMillis)).toInt()
    }

    override fun isCorrect(input: String): Boolean = input.toIntOrNull()?.let { it in 1..RESPONSE_COLORS.size && RESPONSE_COLORS[it - 1] == trial.ink } == true

    override fun solution(): String = (RESPONSE_COLORS.indexOf(trial.ink) + 1).toString()

    override fun hint(): String? = null

    override fun toUiState(): ColorConfusionUiState {
        val marked = feedbackIndex
        val state = if (feedbackWasCorrect) AnswerFeedbackState.CORRECT else AnswerFeedbackState.WRONG
        return ColorConfusionUiState(
            word = trial.word,
            ink = trial.ink,
            swatches = RESPONSE_COLORS
                .mapIndexed { index, color ->
                    ColorSwatchCell(
                        color = color,
                        state = if (marked == index + 1) state else AnswerFeedbackState.NORMAL,
                    )
                }
                .toImmutableList(),
            isAwaitingNextTrial = marked != null,
        )
    }

    private fun nextTrial(): Trial {
        // No two trials in a row share an ink colour. Repeating the answer would let a player farm
        // points by holding one swatch, and response repetitions carry a speed-up of their own that
        // would land unevenly across the two conditions.
        val previousInk = if (round == 0) null else trial.ink
        val ink = RESPONSE_COLORS.filter { it != previousInk }.random(random)
        val word = if (nextIsCongruent()) ink else RESPONSE_COLORS.filter { it != ink }.random(random)
        return Trial(word = word, ink = ink)
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
        /**
         * The classic four-colour set, in the fixed order the response row is drawn in. Four rather
         * than six: every extra swatch adds selection time to both conditions alike, which dilutes
         * the difference the task exists to measure.
         */
        val RESPONSE_COLORS = listOf(GameColor.RED, GameColor.GREEN, GameColor.BLUE, GameColor.YELLOW)

        /** Congruency schedule: [CONGRUENT_PER_BAG] congruent trials in every [BAG_SIZE] dealt. */
        const val BAG_SIZE = 5
        const val CONGRUENT_PER_BAG = 2

        /** Correct trials a condition needs before its median is worth reporting. */
        const val MIN_TRIALS_PER_CONDITION = 5

        /** How long the tapped swatch stays marked before the next trial. */
        const val CORRECT_FEEDBACK_MILLIS = 250L
        const val WRONG_FEEDBACK_MILLIS = 700L
    }
}
