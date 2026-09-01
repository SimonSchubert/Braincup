package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.NBackUiState
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * The n-back task: a machine-paced stream of shapes, one response per item. The player taps Match
 * whenever the shape on screen is the same as the one [n] steps back, and withholds otherwise.
 *
 * One level is one block at that level's n, which is the paradigm's own unit, and [level] *is* n:
 * level 3 is 3-back. Clearing a block unlocks the next, and the stored level carries between plays,
 * so the ladder a player walks is the adaptive procedure of Jaeggi et al. (2008) with the session
 * boundary moved. The score a run reports, the highest n reached, is also what that literature
 * reports as the outcome measure.
 *
 * The numbers are the published ones rather than something tuned for a stopwatch: 500ms on a
 * 3000ms step, 20 scored trials after the n priming ones, [TARGETS_PER_BLOCK] targets, and a block
 * cleared at [MAX_ERRORS] errors or fewer. What makes that possible is being a level game: a block
 * at this pace runs past a minute, so it never fit the 60s every timed game gets.
 *
 * The details that make it the paradigm rather than a resemblance of it:
 *  - the stream never stops for an answer. Every item gets the same [STEP_MS] whether the player
 *    responds or not, which is what forces the memory window to be updated continuously instead of
 *    rehearsed and then dumped.
 *  - [LURES_PER_BLOCK] items per block repeat the item n-1 or n+1 back. A lure is familiar but
 *    wrong, so rejecting it needs the *position* in the window, not just recognition. Without lures
 *    the task collapses into "have I seen this recently", which is not what n-back measures.
 *  - the first [n] items cannot be targets because there is nothing n back yet, and a response on
 *    them is a false alarm, as in the standard scoring.
 *  - nothing is marked mid-stream except the player's own taps. A miss is only known once its
 *    window closes, which is the instant the next item appears, so marking one would put a salient
 *    event on top of the item that most needs encoding and make the next miss more likely. The
 *    block result is reported at the end, where the reference puts it.
 */
class NBackGame(level: Int) :
    LevelGame(level, MAX_N),
    PausableTimedPhaseGame {

    /** How far back the match has to be found. The level *is* n. */
    val n: Int get() = level

    enum class Phase {
        /** A beat announcing the level before the first item, so the stream never starts cold. */
        LEAD_IN,
        STREAM,
    }

    /** What a Match tap did. [IGNORED] covers a second tap in one trial and a tap off-stream. */
    enum class Response { HIT, FALSE_ALARM, IGNORED }

    var phase: Phase = Phase.LEAD_IN
        private set

    /** Index of the open trial in the block, priming included. -1 before the first. */
    var trialInBlock: Int = -1
        private set

    /** False during the blank, so two equal shapes in a row cannot read as one. */
    var showing: Boolean = false
        private set

    var responded: Boolean = false
        private set

    /** This trial's tap, marked on the button until the trial closes. Never [Response.IGNORED]. */
    var lastResponse: Response? = null
        private set

    var hits: Int = 0
        private set

    var falseAlarms: Int = 0
        private set

    var misses: Int = 0
        private set

    /** The shapes shown in this block, oldest first. */
    private val history = mutableListOf<Shape>()

    private var targetSlots: Set<Int> = emptySet()
    private var lureSlots: Set<Int> = emptySet()

    private var trialOpen = false
    private var currentIsTarget = false

    private var streamJob: Job? = null
    private var streamPaused = false
    private var onBlockFinished: (() -> Unit)? = null

    companion object {
        /**
         * Shape identity carries the whole game, so the palette is deliberately small and every
         * shape is unmistakable. ABSTRACT_TRIANGLE is excluded on purpose: it shares the display
         * name "triangle" with TRIANGLE and would read as the same shape.
         */
        val PALETTE = listOf(
            Shape.SQUARE,
            Shape.TRIANGLE,
            Shape.CIRCLE,
            Shape.HEART,
            Shape.STAR,
            Shape.DIAMOND,
        )

        /** A ceiling no one reaches, so the ladder never runs out under a player who keeps going. */
        const val MAX_N = 9

        /** Scored trials per block, on top of the n priming ones that cannot be targets. */
        const val BLOCK_TRIALS = 20

        /**
         * 6 of 20, the published rate. They are also spread one per equal share of the block
         * rather than drawn freely, because clustered targets make a block a coin flip.
         */
        const val TARGETS_PER_BLOCK = 6

        /** Repeats of the item n-1 or n+1 back: familiar, but wrong. */
        const val LURES_PER_BLOCK = 4

        /** A brief flash, not a slideshow frame: the item is encoded and held, never re-read. */
        const val VISIBLE_MS = 500L

        /** The blank, which carries most of the step. */
        const val GAP_MS = 2500L

        /** 3000ms, the reference pace. The response window is the whole step, flash plus blank. */
        const val STEP_MS = VISIBLE_MS + GAP_MS

        /** The lead-in beat before the first item. */
        const val LEAD_IN_MS = 1500L

        /** "Fewer than 3 errors" clears a block, counting misses and false alarms alike. */
        const val MAX_ERRORS = 2
    }

    /** Trials in this block, priming included. */
    val blockLength: Int get() = n + BLOCK_TRIALS

    /** Misses plus false alarms. A block is cleared at [MAX_ERRORS] or fewer. */
    val errors: Int get() = misses + falseAlarms

    val blockCleared: Boolean get() = errors <= MAX_ERRORS

    /** How far the block has run, for the progress bar. */
    val blockProgress: Float
        get() = ((trialInBlock + 1).toFloat() / blockLength).coerceIn(0f, 1f)

    /** True while the shape on screen repeats the one n back. */
    val isTargetTrial: Boolean get() = currentIsTarget

    // -- the state machine, drivable without coroutines so the model is testable with no delays --

    override fun generateRound() {
        history.clear()
        trialInBlock = -1
        trialOpen = false
        showing = false
        responded = false
        currentIsTarget = false
        lastResponse = null
        hits = 0
        falseAlarms = 0
        misses = 0
        phase = Phase.LEAD_IN

        // One target per equal share of the block, with the shares computed on the fly because 20
        // does not divide by 6: truncating the share width would leave the last two scored slots
        // unreachable, a dead tail a player could learn to stop attending to.
        targetSlots = (0 until TARGETS_PER_BLOCK)
            .map { share ->
                val from = share * BLOCK_TRIALS / TARGETS_PER_BLOCK
                val until = (share + 1) * BLOCK_TRIALS / TARGETS_PER_BLOCK
                n + from + Random.nextInt(until - from)
            }
            .toSet()
        lureSlots = (n until blockLength)
            .filterNot { it in targetSlots }
            .shuffled()
            .take(LURES_PER_BLOCK)
            .toSet()
    }

    /** Open the next trial: pick its shape, decide whether it is a target, start its window. */
    fun beginTrial() {
        phase = Phase.STREAM
        lastResponse = null
        trialInBlock += 1
        val index = trialInBlock
        val shape = stimulusFor(index)
        history += shape
        currentIsTarget = index >= n && history[index - n] == shape
        responded = false
        trialOpen = true
        showing = true
    }

    /** End the visible half of the trial. The response window stays open through the blank. */
    fun endStimulus() {
        showing = false
    }

    /** Close the trial. An untapped target is a miss, counted but deliberately not shown. */
    fun closeTrial() {
        if (!trialOpen) return
        if (currentIsTarget && !responded) misses += 1
        trialOpen = false
        showing = false
    }

    /** The Match tap. */
    fun respond(): Response {
        if (phase != Phase.STREAM || !trialOpen || responded) return Response.IGNORED
        responded = true
        val response = if (currentIsTarget) {
            hits += 1
            Response.HIT
        } else {
            falseAlarms += 1
            Response.FALSE_ALARM
        }
        lastResponse = response
        return response
    }

    /**
     * Close the block. The result itself is reported by the finish screen, which is where the
     * buttons that act on it live; a screen of its own would be gone before it could be read.
     */
    fun endBlock() {
        if (trialOpen) closeTrial()
        // A level game never claims the flawless-run bonus; clearing the block is the reward.
        answeredAllCorrect = false
    }

    val isBlockOver: Boolean get() = trialInBlock >= blockLength - 1 && !trialOpen

    /** Start the block over from the top. This is what a pause does; see [resumeTimedPhase]. */
    fun restartBlock() = generateRound()

    private fun stimulusFor(index: Int): Shape {
        // Nothing is n back yet, so a priming trial can be anything and can never be a target.
        if (index < n) return PALETTE.random()
        val target = history[index - n]
        return when {
            index in targetSlots -> target
            index in lureSlots -> lureFor(index, target) ?: nonTarget(index, target)
            else -> nonTarget(index, target)
        }
    }

    /**
     * A repeat of the item n-1 or n+1 back. Null when neither offset is reachable this early in the
     * block or when both happen to hold the target shape, in which case the slot degrades to an
     * ordinary non-target rather than becoming an unplanned target.
     */
    private fun lureFor(index: Int, target: Shape): Shape? = neighbourOffsets(index).shuffled().map { history[index - it] }.firstOrNull { it != target }

    /**
     * An item that is neither the target nor an unplanned lure. The n-1 and n+1 items are excluded
     * as well as the n item, so the lure rate stays the controlled quantity it is meant to be: with
     * six shapes an unconstrained non-target would land on a neighbour about two trials in five,
     * which would swamp [LURES_PER_BLOCK] and make the interference load an accident of the draw.
     */
    private fun nonTarget(index: Int, target: Shape): Shape {
        val neighbours = neighbourOffsets(index).map { history[index - it] }
        val free = PALETTE.filter { it != target && it !in neighbours }
        return free.ifEmpty { PALETTE.filter { it != target } }.random()
    }

    /** The n-1 and n+1 offsets that are reachable this early in the block. */
    private fun neighbourOffsets(index: Int): List<Int> = listOf(n - 1, n + 1).filter { it >= 1 && index - it >= 0 }

    // -- the coroutine that paces the stream; all it does is sleep between state-machine calls --

    /** Run the block from the lead-in to the result, then call [onFinished]. */
    fun startStream(scope: CoroutineScope, onChange: () -> Unit, onFinished: () -> Unit) {
        streamJob?.cancel()
        streamPaused = false
        onBlockFinished = onFinished
        onChange()
        streamJob = scope.launch {
            if (phase == Phase.LEAD_IN) delay(LEAD_IN_MS.milliseconds)
            while (!isBlockOver) {
                beginTrial()
                onChange()
                delay(VISIBLE_MS.milliseconds)
                endStimulus()
                onChange()
                delay(GAP_MS.milliseconds)
                closeTrial()
                onChange()
            }
            endBlock()
            onChange()
            onFinished()
        }
    }

    override val isTimedPhaseActive: Boolean get() = streamJob != null

    override fun cancelTimedPhase() {
        streamPaused = false
        streamJob?.cancel()
        streamJob = null
    }

    override fun pauseTimedPhase() {
        if (streamJob == null) return
        streamPaused = true
        streamJob?.cancel()
        streamJob = null
    }

    /**
     * Restart the block rather than resuming mid-stream. There is no meaningful resume point inside
     * a paced stream, and a block that could be re-entered part-answered would let the quit dialog
     * be used to take the easy trials of a block and skip the hard ones.
     */
    override fun resumeTimedPhase(scope: CoroutineScope, onChange: () -> Unit) {
        if (!streamPaused) return
        streamPaused = false
        val finished = onBlockFinished ?: return
        restartBlock()
        startStream(scope, onChange, finished)
    }

    // -- Game --

    /** Not a per-round answer game: the only judgement is whether the item on screen is a target. */
    override fun isCorrect(input: String): Boolean = currentIsTarget

    override fun solution(): String = n.toString()

    override fun hint(): String? = null

    override fun toUiState(): NBackUiState = NBackUiState(
        level = level,
        phase = phase,
        currentShape = if (showing) history.lastOrNull() else null,
        blockProgress = blockProgress,
        responded = responded,
        lastResponse = lastResponse,
    )
}
