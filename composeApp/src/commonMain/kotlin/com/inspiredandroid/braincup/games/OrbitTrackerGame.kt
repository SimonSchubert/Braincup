package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.OrbitTrackerUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class OrbitTrackerGame : Game() {
    sealed class SubmitResult {
        data object CorrectContinue : SubmitResult()
        data object RoundComplete : SubmitResult()
        data object Wrong : SubmitResult()
    }

    enum class Phase {
        HIGHLIGHTING,
        MOVING,
        ANSWERING,
        GAME_OVER,
    }

    data class Ball(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        val isTarget: Boolean,
    )

    var phase: Phase = Phase.HIGHLIGHTING
        private set

    var balls: List<Ball> = emptyList()
        private set

    var selectedIndices: MutableSet<Int> = mutableSetOf()
        private set

    var feedbackState: List<BallFeedback> = emptyList()
        private set

    private var animationJob: Job? = null

    enum class BallFeedback {
        NONE,
        CORRECT_SELECTED,
        WRONG_SELECTED,
        MISSED,
    }

    private data class DifficultyConfig(
        val totalBalls: Int,
        val targets: Int,
        val speed: Float,
    )

    private fun difficultyForRound(r: Int): DifficultyConfig = when {
        r <= 0 -> DifficultyConfig(8, 3, 0.15f)
        r == 1 -> DifficultyConfig(9, 3, 0.15f)
        r == 2 -> DifficultyConfig(10, 3, 0.18f)
        r == 3 -> DifficultyConfig(10, 4, 0.18f)
        r == 4 -> DifficultyConfig(12, 4, 0.20f)
        r == 5 -> DifficultyConfig(12, 4, 0.22f)
        r == 6 -> DifficultyConfig(12, 5, 0.22f)
        else -> DifficultyConfig(14, 5, 0.25f)
    }

    private fun moveDurationMillis(): Long {
        val seconds = (5f + round * 0.5f).coerceAtMost(10f)
        return (seconds * 1000).toLong()
    }

    companion object {
        private const val BALL_RADIUS = 0.04f
        private val HIGHLIGHT_DURATION = 2.seconds
        private val FRAME_DELAY = 16.milliseconds
    }

    override fun generateRound() {
        val config = difficultyForRound(round)
        val newBalls = mutableListOf<Ball>()
        val margin = BALL_RADIUS * 2

        for (i in 0 until config.totalBalls) {
            val isTarget = i < config.targets
            var x: Float
            var y: Float
            var attempts = 0
            do {
                x = margin + (Random.nextFloat() * (1f - 2 * margin))
                y = margin + (Random.nextFloat() * (1f - 2 * margin))
                attempts++
            } while (attempts < 100 &&
                newBalls.any { existing ->
                    val dx = existing.x - x
                    val dy = existing.y - y
                    sqrt(dx * dx + dy * dy) < BALL_RADIUS * 3
                }
            )

            val angle = Random.nextFloat() * 2 * PI.toFloat()
            val vx = kotlin.math.cos(angle) * config.speed
            val vy = kotlin.math.sin(angle) * config.speed

            newBalls.add(Ball(x, y, vx, vy, isTarget))
        }

        balls = newBalls.shuffled()
        selectedIndices = mutableSetOf()
        feedbackState = List(balls.size) { BallFeedback.NONE }
        phase = Phase.HIGHLIGHTING
    }

    fun startHighlightAndMove(scope: CoroutineScope, onStateChanged: () -> Unit) {
        animationJob?.cancel()
        animationJob = scope.launch {
            // Highlight phase
            phase = Phase.HIGHLIGHTING
            onStateChanged()
            delay(HIGHLIGHT_DURATION)

            // Moving phase
            phase = Phase.MOVING
            onStateChanged()

            val moveDuration = moveDurationMillis()
            val startTime = currentTimeMillis()
            var lastFrameTime = startTime

            while (currentTimeMillis() - startTime < moveDuration) {
                val now = currentTimeMillis()
                val delta = ((now - lastFrameTime) / 1000f).coerceAtMost(0.05f)
                lastFrameTime = now
                updateBallPositions(delta)
                onStateChanged()
                delay(FRAME_DELAY)
            }

            // Answering phase
            phase = Phase.ANSWERING
            onStateChanged()
        }
    }

    fun cancelAnimation() {
        animationJob?.cancel()
        animationJob = null
    }

    fun selectBall(index: Int): SubmitResult {
        if (phase != Phase.ANSWERING || index !in balls.indices || index in selectedIndices) {
            return SubmitResult.CorrectContinue
        }

        selectedIndices.add(index)
        val ball = balls[index]

        if (!ball.isTarget) {
            // Wrong ball — show feedback and end game
            feedbackState = balls.mapIndexed { i, b ->
                when {
                    i == index -> BallFeedback.WRONG_SELECTED
                    i in selectedIndices && b.isTarget -> BallFeedback.CORRECT_SELECTED
                    b.isTarget -> BallFeedback.MISSED
                    else -> BallFeedback.NONE
                }
            }
            answeredAllCorrect = false
            phase = Phase.GAME_OVER
            return SubmitResult.Wrong
        }

        // Correct ball — mark as selected
        feedbackState = feedbackState.toMutableList().apply {
            set(index, BallFeedback.CORRECT_SELECTED)
        }

        val allTargetsSelected = balls.indices.all { i ->
            !balls[i].isTarget || i in selectedIndices
        }

        return if (allTargetsSelected) {
            SubmitResult.RoundComplete
        } else {
            SubmitResult.CorrectContinue
        }
    }

    fun updateBallPositions(deltaSeconds: Float) {
        val config = difficultyForRound(round)

        // Move balls
        for (ball in balls) {
            ball.x += ball.vx * deltaSeconds
            ball.y += ball.vy * deltaSeconds

            // Wall collisions
            if (ball.x - BALL_RADIUS < 0f) {
                ball.x = BALL_RADIUS
                ball.vx = -ball.vx
            }
            if (ball.x + BALL_RADIUS > 1f) {
                ball.x = 1f - BALL_RADIUS
                ball.vx = -ball.vx
            }
            if (ball.y - BALL_RADIUS < 0f) {
                ball.y = BALL_RADIUS
                ball.vy = -ball.vy
            }
            if (ball.y + BALL_RADIUS > 1f) {
                ball.y = 1f - BALL_RADIUS
                ball.vy = -ball.vy
            }
        }

        // Ball-ball collisions
        for (i in balls.indices) {
            for (j in i + 1 until balls.size) {
                val a = balls[i]
                val b = balls[j]
                val dx = b.x - a.x
                val dy = b.y - a.y
                val dist = sqrt(dx * dx + dy * dy)
                val minDist = BALL_RADIUS * 2

                if (dist < minDist && dist > 0.0001f) {
                    // Normalize collision vector
                    val nx = dx / dist
                    val ny = dy / dist

                    // Relative velocity
                    val dvx = a.vx - b.vx
                    val dvy = a.vy - b.vy
                    val dvDotN = dvx * nx + dvy * ny

                    // Only resolve if balls are approaching
                    if (dvDotN > 0) {
                        a.vx -= dvDotN * nx
                        a.vy -= dvDotN * ny
                        b.vx += dvDotN * nx
                        b.vy += dvDotN * ny
                    }

                    // Separate overlapping balls
                    val overlap = (minDist - dist) / 2f
                    a.x -= overlap * nx
                    a.y -= overlap * ny
                    b.x += overlap * nx
                    b.y += overlap * ny
                }
            }
        }

        // Re-normalize speeds to prevent drift
        for (ball in balls) {
            val speed = sqrt(ball.vx * ball.vx + ball.vy * ball.vy)
            if (speed > 0.0001f) {
                ball.vx = ball.vx / speed * config.speed
                ball.vy = ball.vy / speed * config.speed
            }
        }
    }

    override fun isCorrect(input: String): Boolean = false

    override fun solution(): String = balls.mapIndexedNotNull { index, ball ->
        if (ball.isTarget) index.toString() else null
    }.joinToString(", ")

    override fun hint(): String? = null

    override fun toUiState(): OrbitTrackerUiState {
        val targetCount = balls.count { it.isTarget }
        return OrbitTrackerUiState(
            balls = balls.mapIndexed { index, ball ->
                OrbitTrackerUiState.BallState(
                    x = ball.x,
                    y = ball.y,
                    isTarget = ball.isTarget,
                    isSelected = index in selectedIndices,
                    feedback = when (feedbackState[index]) {
                        BallFeedback.NONE -> OrbitTrackerUiState.BallFeedback.NONE
                        BallFeedback.CORRECT_SELECTED -> OrbitTrackerUiState.BallFeedback.CORRECT_SELECTED
                        BallFeedback.WRONG_SELECTED -> OrbitTrackerUiState.BallFeedback.WRONG_SELECTED
                        BallFeedback.MISSED -> OrbitTrackerUiState.BallFeedback.MISSED
                    },
                )
            },
            phase = phase,
            targetCount = targetCount,
            selectedCount = selectedIndices.size,
        )
    }

    private fun currentTimeMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
}
