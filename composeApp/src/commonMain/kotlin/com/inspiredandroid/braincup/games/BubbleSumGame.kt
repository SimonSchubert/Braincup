package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.BubbleSumUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Bubble Sum: add the numbers on moving bubbles under the global 60s timer.
 *
 * Early rounds keep numbers always visible. Later rounds use staggered blink: each bubble
 * spends time fully visible, then a warning color, then hides — phases offset per bubble so
 * they do not all disappear together.
 */
class BubbleSumGame : Game() {

    enum class VisibilityPhase {
        /** Number shown in the normal face color. */
        VISIBLE,

        /** Number still shown, but face color changes to warn the player it will hide soon. */
        WARNING,

        /** Number hidden (bubble shell remains). */
        HIDDEN,
    }

    data class Bubble(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        val value: Int,
        val blinkPhaseOffsetMs: Long,
        var phase: VisibilityPhase = VisibilityPhase.VISIBLE,
    ) {
        val showsNumber: Boolean get() = phase != VisibilityPhase.HIDDEN
    }

    /** Lightweight per-frame snapshot (positions + visibility). Not used for full UI state. */
    data class BubbleFrame(
        val x: Float,
        val y: Float,
        val phase: VisibilityPhase,
    )

    var bubbles: List<Bubble> = emptyList()
        private set

    /**
     * Arena extents in short-edge units: the shorter axis is always 1f and the longer one grows
     * past it, so [BALL_RADIUS] stays the same fraction of the short edge whatever shape the
     * layout hands us, and a taller arena simply gives the bubbles more room to travel.
     * Defaults to a square until the UI reports its canvas.
     */
    var arenaWidth: Float = 1f
        private set
    var arenaHeight: Float = 1f
        private set

    private var animationJob: Job? = null
    private var motionStartMillis: Long = 0L
    private var roundKey: Int = 0

    private enum class BlinkMode {
        ALWAYS_ON,

        /** Per-bubble phase offsets so numbers never all hide at once. */
        STAGGERED,
    }

    private data class DifficultyConfig(
        val bubbleCount: Int,
        val minValue: Int,
        val maxValue: Int,
        val speed: Float,
        val blinkMode: BlinkMode,
        val visibleMs: Long,
        val hiddenMs: Long,
        val sumMin: Int,
        val sumMax: Int,
    )

    private fun difficultyForRound(r: Int): DifficultyConfig = when {
        r <= 1 -> DifficultyConfig(
            bubbleCount = 3,
            minValue = 1,
            maxValue = 6,
            speed = 0.12f,
            blinkMode = BlinkMode.ALWAYS_ON,
            visibleMs = 0,
            hiddenMs = 0,
            sumMin = 8,
            sumMax = 40,
        )
        r <= 3 -> DifficultyConfig(
            bubbleCount = 4,
            minValue = 1,
            maxValue = 9,
            speed = 0.15f,
            blinkMode = BlinkMode.ALWAYS_ON,
            visibleMs = 0,
            hiddenMs = 0,
            sumMin = 8,
            sumMax = 40,
        )
        r <= 5 -> DifficultyConfig(
            bubbleCount = 4,
            minValue = 1,
            maxValue = 9,
            speed = 0.16f,
            blinkMode = BlinkMode.STAGGERED,
            visibleMs = 5000,
            hiddenMs = 900,
            sumMin = 10,
            sumMax = 50,
        )
        r <= 7 -> DifficultyConfig(
            bubbleCount = 5,
            minValue = 2,
            maxValue = 12,
            speed = 0.18f,
            blinkMode = BlinkMode.STAGGERED,
            visibleMs = 4500,
            hiddenMs = 1000,
            sumMin = 15,
            sumMax = 80,
        )
        r <= 9 -> DifficultyConfig(
            bubbleCount = 6,
            minValue = 3,
            maxValue = 15,
            speed = 0.20f,
            blinkMode = BlinkMode.STAGGERED,
            visibleMs = 4000,
            hiddenMs = 1100,
            sumMin = 20,
            sumMax = 100,
        )
        else -> DifficultyConfig(
            bubbleCount = 7,
            minValue = 5,
            maxValue = 20,
            speed = 0.22f,
            blinkMode = BlinkMode.STAGGERED,
            visibleMs = 3500,
            hiddenMs = 1300,
            sumMin = 25,
            sumMax = 150,
        )
    }

    companion object {
        /** Bubble radius in short-edge units, matching [arenaWidth] / [arenaHeight]. */
        const val BALL_RADIUS = 0.068f

        /**
         * Face stays in warning color this long immediately before the number hides. Keep it
         * short next to a round's visible window: the warning is a heads-up to read the number,
         * and when it grows to a large share of the cycle most bubbles sit yellow at once, which
         * reads as hectic rather than urgent.
         */
        const val WARNING_MS = 1500L
        private val FRAME_DELAY = 16.milliseconds
        private const val VISIBILITY_GRACE_MS = 500L
        private const val MAX_VALUE_ATTEMPTS = 80
    }

    private fun blinkCycleMs(config: DifficultyConfig): Long {
        if (config.blinkMode == BlinkMode.ALWAYS_ON) return 0L
        return config.visibleMs + WARNING_MS + config.hiddenMs
    }

    /**
     * Reports the arena's pixel size. Positions are remapped in proportion so bubbles keep
     * filling the arena across a rotation or window resize instead of bunching into the old shape.
     */
    fun setArenaSize(widthPx: Float, heightPx: Float) {
        if (widthPx <= 0f || heightPx <= 0f) return
        val shortEdge = min(widthPx, heightPx)
        val newWidth = widthPx / shortEdge
        val newHeight = heightPx / shortEdge
        if (abs(newWidth - arenaWidth) < 0.001f && abs(newHeight - arenaHeight) < 0.001f) return

        val scaleX = newWidth / arenaWidth
        val scaleY = newHeight / arenaHeight
        arenaWidth = newWidth
        arenaHeight = newHeight
        for (bubble in bubbles) {
            bubble.x = (bubble.x * scaleX).coerceIn(BALL_RADIUS, newWidth - BALL_RADIUS)
            bubble.y = (bubble.y * scaleY).coerceIn(BALL_RADIUS, newHeight - BALL_RADIUS)
        }
    }

    override fun generateRound() {
        val config = difficultyForRound(round)
        val values = generateValues(config)
        val newBubbles = mutableListOf<Bubble>()
        val margin = BALL_RADIUS * 2
        val cycle = blinkCycleMs(config)

        for (i in values.indices) {
            var x: Float
            var y: Float
            var attempts = 0
            do {
                x = margin + (Random.nextFloat() * (arenaWidth - 2 * margin))
                y = margin + (Random.nextFloat() * (arenaHeight - 2 * margin))
                attempts++
            } while (
                attempts < 100 &&
                newBubbles.any { existing ->
                    val dx = existing.x - x
                    val dy = existing.y - y
                    sqrt(dx * dx + dy * dy) < BALL_RADIUS * 3
                }
            )

            val angle = Random.nextFloat() * 2 * PI.toFloat()
            // Spread phase offsets evenly around the blink cycle so at most a subset is hidden
            // at any moment (never all numbers disappearing together).
            val offset = when {
                config.blinkMode == BlinkMode.ALWAYS_ON || cycle <= 0L -> 0L
                else -> {
                    val count = config.bubbleCount.coerceAtLeast(1)
                    (i * cycle / count) % cycle
                }
            }

            newBubbles.add(
                Bubble(
                    x = x,
                    y = y,
                    vx = cos(angle) * config.speed,
                    vy = sin(angle) * config.speed,
                    value = values[i],
                    blinkPhaseOffsetMs = offset,
                    phase = VisibilityPhase.VISIBLE,
                ),
            )
        }

        bubbles = newBubbles
        roundKey++
        motionStartMillis = 0L
    }

    private fun generateValues(config: DifficultyConfig): List<Int> {
        repeat(MAX_VALUE_ATTEMPTS) {
            val values = List(config.bubbleCount) {
                Random.nextInt(config.minValue, config.maxValue + 1)
            }
            val sum = values.sum()
            if (sum in config.sumMin..config.sumMax) {
                return values
            }
        }
        // Fallback: clamp a simple sequence into a reasonable sum.
        val base = List(config.bubbleCount) { config.minValue + it % (config.maxValue - config.minValue + 1) }
        return base
    }

    fun targetSum(): Int = bubbles.sumOf { it.value }

    fun answerLength(): Int = targetSum().toString().length

    fun frames(): List<BubbleFrame> = bubbles.map { BubbleFrame(it.x, it.y, it.phase) }

    /**
     * Continuous motion loop. [onFrame] should push lightweight position/visibility updates
     * without rebuilding full [BubbleSumUiState].
     */
    fun startMotion(
        scope: CoroutineScope,
        onFrame: () -> Unit,
    ) {
        animationJob?.cancel()
        motionStartMillis = currentTimeMillis()
        updateVisibility(motionStartMillis)
        animationJob = scope.launch {
            onFrame()
            var lastFrameTime = motionStartMillis
            while (true) {
                val now = currentTimeMillis()
                val delta = ((now - lastFrameTime) / 1000f).coerceAtMost(0.05f)
                lastFrameTime = now
                updateBallPositions(delta)
                updateVisibility(now)
                onFrame()
                delay(FRAME_DELAY)
            }
        }
    }

    fun cancelAnimation() {
        animationJob?.cancel()
        animationJob = null
    }

    fun updateBallPositions(deltaSeconds: Float) {
        val config = difficultyForRound(round.coerceAtLeast(1) - 1)

        for (bubble in bubbles) {
            bubble.x += bubble.vx * deltaSeconds
            bubble.y += bubble.vy * deltaSeconds

            if (bubble.x - BALL_RADIUS < 0f) {
                bubble.x = BALL_RADIUS
                bubble.vx = -bubble.vx
            }
            if (bubble.x + BALL_RADIUS > arenaWidth) {
                bubble.x = arenaWidth - BALL_RADIUS
                bubble.vx = -bubble.vx
            }
            if (bubble.y - BALL_RADIUS < 0f) {
                bubble.y = BALL_RADIUS
                bubble.vy = -bubble.vy
            }
            if (bubble.y + BALL_RADIUS > arenaHeight) {
                bubble.y = arenaHeight - BALL_RADIUS
                bubble.vy = -bubble.vy
            }
        }

        for (i in bubbles.indices) {
            for (j in i + 1 until bubbles.size) {
                val a = bubbles[i]
                val b = bubbles[j]
                val dx = b.x - a.x
                val dy = b.y - a.y
                val dist = sqrt(dx * dx + dy * dy)
                val minDist = BALL_RADIUS * 2

                if (dist < minDist && dist > 0.0001f) {
                    val nx = dx / dist
                    val ny = dy / dist
                    val dvx = a.vx - b.vx
                    val dvy = a.vy - b.vy
                    val dvDotN = dvx * nx + dvy * ny
                    if (dvDotN > 0) {
                        a.vx -= dvDotN * nx
                        a.vy -= dvDotN * ny
                        b.vx += dvDotN * nx
                        b.vy += dvDotN * ny
                    }
                    val overlap = (minDist - dist) / 2f
                    a.x -= overlap * nx
                    a.y -= overlap * ny
                    b.x += overlap * nx
                    b.y += overlap * ny
                }
            }
        }

        for (bubble in bubbles) {
            val speed = sqrt(bubble.vx * bubble.vx + bubble.vy * bubble.vy)
            if (speed > 0.0001f) {
                bubble.vx = bubble.vx / speed * config.speed
                bubble.vy = bubble.vy / speed * config.speed
            }
        }
    }

    fun updateVisibility(nowMs: Long) {
        val config = difficultyForRound(round.coerceAtLeast(1) - 1)
        if (config.blinkMode == BlinkMode.ALWAYS_ON) {
            bubbles.forEach { it.phase = VisibilityPhase.VISIBLE }
            return
        }

        val elapsed = if (motionStartMillis > 0L) nowMs - motionStartMillis else 0L
        applyVisibilityElapsed(elapsed, config)
    }

    /**
     * Applies blink phases as if [elapsedMs] have passed since motion start.
     * Exposed for unit tests of the warning → hidden sequence.
     */
    fun applyVisibilityElapsed(elapsedMs: Long) {
        val config = difficultyForRound(round.coerceAtLeast(1) - 1)
        applyVisibilityElapsed(elapsedMs, config)
    }

    private fun applyVisibilityElapsed(elapsedMs: Long, config: DifficultyConfig) {
        if (config.blinkMode == BlinkMode.ALWAYS_ON) {
            bubbles.forEach { it.phase = VisibilityPhase.VISIBLE }
            return
        }
        if (elapsedMs < VISIBILITY_GRACE_MS) {
            bubbles.forEach { it.phase = VisibilityPhase.VISIBLE }
            return
        }

        val cycle = blinkCycleMs(config)
        if (cycle <= 0L) {
            bubbles.forEach { it.phase = VisibilityPhase.VISIBLE }
            return
        }

        val warnStart = config.visibleMs
        val hideStart = config.visibleMs + WARNING_MS
        for (bubble in bubbles) {
            val t = (elapsedMs + bubble.blinkPhaseOffsetMs) % cycle
            bubble.phase = when {
                t < warnStart -> VisibilityPhase.VISIBLE
                t < hideStart -> VisibilityPhase.WARNING
                else -> VisibilityPhase.HIDDEN
            }
        }
    }

    /** Exposed for tests: whether the current round uses any blinking. */
    fun usesBlink(): Boolean {
        val config = difficultyForRound(round.coerceAtLeast(1) - 1)
        return config.blinkMode != BlinkMode.ALWAYS_ON
    }

    /** Exposed for tests: how long a number stays fully visible before the warning starts. */
    fun visibleWindowMs(): Long = difficultyForRound(round.coerceAtLeast(1) - 1).visibleMs

    /** Exposed for tests: whether blink offsets differ across bubbles. */
    fun usesStaggeredBlink(): Boolean {
        val config = difficultyForRound(round.coerceAtLeast(1) - 1)
        return config.blinkMode == BlinkMode.STAGGERED
    }

    override fun isCorrect(input: String): Boolean = input.trim() == targetSum().toString()

    override fun solution(): String = targetSum().toString()

    override fun hint(): String? = null

    override fun toUiState(): BubbleSumUiState = BubbleSumUiState(
        bubbles = bubbles.map { BubbleSumUiState.BubbleState(it.value) }.toImmutableList(),
        answerLength = answerLength(),
        roundKey = roundKey,
    )

    private fun currentTimeMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
}
