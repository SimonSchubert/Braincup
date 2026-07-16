package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.QuickSumUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Quick Sum: flash anzan. Terms appear one at a time in the same spot at a machine-set pace and
 * vanish; the player holds a running total and enters it when the sequence ends.
 *
 * Each round runs two phases:
 *  1. [Phase.FLASHING] - terms flash in sequence, then the pad appears.
 *  2. [Phase.ANSWER]   - the total is entered, then revealed green/red.
 *
 * Unlike the other math games the pace is not the player's to choose, which is what makes this a
 * processing-speed test rather than an arithmetic one. Difficulty rises through term count, term
 * magnitude and flash rate, but total flash time stays near 4s at every tier so rounds-per-minute
 * (the score) stays comparable across the ramp.
 *
 * A correct total advances the ramp; a wrong one replays the same tier with fresh terms.
 */
class QuickSumGame : Game() {
    // Always start at the slowest tier so scores are comparable run-to-run.
    override val adaptiveDifficulty: Boolean = false

    enum class Phase { FLASHING, ANSWER }

    enum class AnswerResult { CORRECT, WRONG }

    var phase: Phase = Phase.FLASHING
        private set

    /** The terms to add, in flash order. */
    var terms: List<Int> = emptyList()
        private set

    /** Index into [terms] of the term currently being flashed. */
    var currentIndex: Int = 0
        private set

    /** False during the blank gap between two terms. */
    var showingTerm: Boolean = true
        private set

    /** Non-null while revealing the total after a submission (drives the green/red reveal). */
    var answerResult: AnswerResult? = null
        private set

    private var flashJob: Job? = null
    private var flashPaused = false

    private data class DifficultyConfig(
        val termCount: Int,
        val minValue: Int,
        val maxValue: Int,
        val stepMs: Long,
    )

    /**
     * The pace only eases down to [MIN_STEP_MS]: most of the climb is term count and term size,
     * not raw speed. Reading a flashed number has a floor that practice does not move much, so a
     * ramp that keeps cutting the step turns unfair long before it turns hard, and the game stops
     * being about arithmetic at all.
     *
     * Tiers are two rounds wide because the whole ramp has to fit inside one 60s run. A round
     * costs roughly 8-10s (lead-in, flash, typing, reveal) and the difficulty never carries over
     * ([adaptiveDifficulty] is false), so a player sees about 6-7 rounds and nothing above round
     * ~7 ever appears in normal play. Widening these tiers does not make the game gentler, it
     * just hides the top of the ramp.
     */
    private fun configForRound(r: Int): DifficultyConfig = when {
        r <= 1 -> DifficultyConfig(termCount = 4, minValue = 1, maxValue = 9, stepMs = 1000)
        r <= 3 -> DifficultyConfig(termCount = 5, minValue = 1, maxValue = 9, stepMs = 950)
        r <= 5 -> DifficultyConfig(termCount = 6, minValue = 1, maxValue = 12, stepMs = 900)
        r <= 7 -> DifficultyConfig(termCount = 7, minValue = 2, maxValue = 15, stepMs = 850)
        r <= 9 -> DifficultyConfig(termCount = 8, minValue = 2, maxValue = 19, stepMs = 800)
        else -> DifficultyConfig(termCount = MAX_TERMS, minValue = 2, maxValue = 25, stepMs = MIN_STEP_MS)
    }

    companion object {
        /**
         * Share of a step spent blank. Without a gap two equal consecutive terms (7, 7) read as one
         * unchanging 7 and become uncountable.
         */
        private const val GAP_FRACTION = 0.22f
        private const val MIN_GAP_MS = 100L

        /** The fastest the top tier ever flashes. The ramp is not allowed below this. */
        const val MIN_STEP_MS = 750L

        /** Most terms a round can flash, reached from round 10 by fast players only. */
        const val MAX_TERMS = 9

        /**
         * Blank beat before the first term of a round. Without it the previous round's revealed
         * total runs straight into terms the player is already meant to be adding, leaving no
         * moment to clear the old number out of their head.
         */
        const val LEAD_IN_MS = 700L
    }

    override fun generateRound() {
        // round is still 0 here (Game.nextRound increments after generateRound).
        val config = configForRound(round)
        phase = Phase.FLASHING
        answerResult = null
        currentIndex = 0
        showingTerm = true
        terms = List(config.termCount) { Random.nextInt(config.minValue, config.maxValue + 1) }
    }

    /** Config for the round now in play, i.e. after [Game.nextRound] bumped [round]. */
    private fun activeConfig(): DifficultyConfig = configForRound(round.coerceAtLeast(1) - 1)

    /** Total time each term occupies, blank gap included. */
    fun stepDurationMs(): Long = activeConfig().stepMs

    /** Blank time between two terms. */
    fun gapMs(): Long = (stepDurationMs() * GAP_FRACTION).toLong().coerceAtLeast(MIN_GAP_MS)

    /** How long a term is actually on screen. */
    fun visibleMs(): Long = stepDurationMs() - gapMs()

    fun termCount(): Int = terms.size

    fun targetSum(): Int = terms.sum()

    fun answerLength(): Int = targetSum().toString().length

    /** Wait out the blank lead-in, flash the terms in order, then open the answer phase. */
    fun startFlashing(scope: CoroutineScope, onChange: () -> Unit) {
        flashJob?.cancel()
        flashPaused = false
        phase = Phase.FLASHING
        // -1 while the lead-in runs: no term has been shown yet, so no progress dot is lit.
        currentIndex = -1
        showingTerm = false
        onChange()
        val visible = visibleMs().milliseconds
        val gap = gapMs().milliseconds
        flashJob = scope.launch {
            delay(LEAD_IN_MS.milliseconds)
            for (i in terms.indices) {
                currentIndex = i
                showingTerm = true
                onChange()
                delay(visible)
                showingTerm = false
                onChange()
                delay(gap)
            }
            phase = Phase.ANSWER
            onChange()
        }
    }

    fun cancelFlashing() {
        flashPaused = false
        flashJob?.cancel()
        flashJob = null
    }

    fun pauseFlashing() {
        if (flashJob == null || phase != Phase.FLASHING) return
        flashPaused = true
        flashJob?.cancel()
        flashJob = null
    }

    /**
     * Restart the round with fresh terms rather than resuming mid-sequence. Half a flashed term has
     * no meaningful resume point, and replaying the same terms would turn the quit dialog into a
     * free second look that could be farmed by reopening it.
     */
    fun resumeFlashing(scope: CoroutineScope, onChange: () -> Unit) {
        if (!flashPaused) return
        flashPaused = false
        repeatRound()
        startFlashing(scope, onChange)
    }

    /** Re-generate the round at the current tier with fresh terms, without advancing the ramp. */
    fun repeatRound() {
        round -= 1
        nextRound()
    }

    /** Submit the total. Returns whether it matched; exposes [answerResult] for the reveal. */
    fun submitSum(input: String): Boolean {
        val ok = isCorrect(input)
        answerResult = if (ok) AnswerResult.CORRECT else AnswerResult.WRONG
        if (!ok) answeredAllCorrect = false
        return ok
    }

    override fun isCorrect(input: String): Boolean = input.trim() == targetSum().toString()

    override fun solution(): String = targetSum().toString()

    override fun hint(): String? = null

    override fun toUiState(): QuickSumUiState = QuickSumUiState(
        phase = phase,
        currentTerm = if (phase == Phase.FLASHING && showingTerm) terms.getOrNull(currentIndex) else null,
        termIndex = currentIndex,
        termCount = terms.size,
        answerLength = answerLength(),
        revealedSum = if (answerResult != null) targetSum().toString() else null,
        answerResult = answerResult,
    )
}
