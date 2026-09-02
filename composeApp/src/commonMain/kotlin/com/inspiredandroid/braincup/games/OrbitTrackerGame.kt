package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.OrbitTrackerUiState
import com.inspiredandroid.braincup.games.tools.MovingBall
import com.inspiredandroid.braincup.games.tools.currentTimeMillis
import com.inspiredandroid.braincup.games.tools.spawnPosition
import com.inspiredandroid.braincup.games.tools.stepBalls
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class OrbitTrackerGame :
    Game(),
    TimedPhaseGame {
    enum class Phase {
        HIGHLIGHTING,
        MOVING,
        ANSWERING,
        GAME_OVER,
    }

    data class Ball(
        override var x: Float,
        override var y: Float,
        override var vx: Float,
        override var vy: Float,
        val isTarget: Boolean,
    ) : MovingBall

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

        for (i in 0 until config.totalBalls) {
            val isTarget = i < config.targets
            val (x, y) = spawnPosition(newBalls, BALL_RADIUS, width = 1f, height = 1f)

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

    /**
     * Runs highlight → move → answer. Phase transitions call [onPhaseChanged] (full UI state).
     * During the moving loop only [onFrame] is invoked so callers can push lightweight position
     * updates without rebuilding [OrbitTrackerUiState] at 60fps.
     */
    fun startHighlightAndMove(
        scope: CoroutineScope,
        onPhaseChanged: () -> Unit,
        onFrame: () -> Unit = onPhaseChanged,
    ) {
        animationJob?.cancel()
        animationJob = scope.launch {
            // Highlight phase
            phase = Phase.HIGHLIGHTING
            onPhaseChanged()
            delay(HIGHLIGHT_DURATION)

            // Moving phase
            phase = Phase.MOVING
            onPhaseChanged()

            val moveDuration = moveDurationMillis()
            val startTime = currentTimeMillis()
            var lastFrameTime = startTime

            while (currentTimeMillis() - startTime < moveDuration) {
                val now = currentTimeMillis()
                val delta = ((now - lastFrameTime) / 1000f).coerceAtMost(0.05f)
                lastFrameTime = now
                updateBallPositions(delta)
                onFrame()
                delay(FRAME_DELAY)
            }

            // Answering phase
            phase = Phase.ANSWERING
            onPhaseChanged()
        }
    }

    override fun cancelTimedPhase() {
        animationJob?.cancel()
        animationJob = null
    }

    fun selectBall(index: Int): SequenceSubmitResult {
        if (phase != Phase.ANSWERING || index !in balls.indices || index in selectedIndices) {
            return SequenceSubmitResult.CorrectContinue
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
            return SequenceSubmitResult.Wrong
        }

        // Correct ball — mark as selected
        feedbackState = feedbackState.toMutableList().apply {
            set(index, BallFeedback.CORRECT_SELECTED)
        }

        val allTargetsSelected = balls.indices.all { i ->
            !balls[i].isTarget || i in selectedIndices
        }

        return if (allTargetsSelected) {
            SequenceSubmitResult.RoundComplete
        } else {
            SequenceSubmitResult.CorrectContinue
        }
    }

    fun updateBallPositions(deltaSeconds: Float) {
        val config = difficultyForRound(round)
        stepBalls(balls, deltaSeconds, BALL_RADIUS, width = 1f, height = 1f, speed = config.speed)
    }

    override fun isCorrect(input: String): Boolean = false

    override fun solution(): String = balls.mapIndexedNotNull { index, ball ->
        if (ball.isTarget) index.toString() else null
    }.joinToString(", ")

    override fun toUiState(): OrbitTrackerUiState {
        val targetCount = balls.count { it.isTarget }
        return OrbitTrackerUiState(
            balls = balls.mapIndexed { index, ball ->
                OrbitTrackerUiState.BallState(
                    x = ball.x,
                    y = ball.y,
                    isTarget = ball.isTarget,
                    isSelected = index in selectedIndices,
                    feedback = feedbackState[index],
                )
            }.toImmutableList(),
            phase = phase,
            targetCount = targetCount,
            selectedCount = selectedIndices.size,
        )
    }
}
