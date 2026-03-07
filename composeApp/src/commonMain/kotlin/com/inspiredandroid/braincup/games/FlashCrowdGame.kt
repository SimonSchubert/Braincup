package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.FeedbackMessage
import com.inspiredandroid.braincup.app.FlashCrowdUiState
import kotlin.math.sqrt
import kotlin.random.Random

class FlashCrowdGame : Game() {

    enum class Side { LEFT, RIGHT }

    data class Dot(val x: Float, val y: Float, val radius: Float)

    var moreSide = Side.LEFT
    var leftDots = emptyList<Dot>()
    var rightDots = emptyList<Dot>()
    var leftCount = 0
    var rightCount = 0
    var roundKey = 0

    override fun generateRound() {
        moreSide = Side.entries.random()

        val ratio = getDifficultyRatio()
        val moreCount = Random.nextInt(15, 26)
        val fewerCount = (moreCount * ratio).toInt().coerceAtLeast(1)

        if (moreSide == Side.LEFT) {
            leftCount = moreCount
            rightCount = fewerCount
        } else {
            leftCount = fewerCount
            rightCount = moreCount
        }

        val leftIsMore = moreSide == Side.LEFT
        leftDots = generateDots(leftCount, isMoreSide = leftIsMore)
        rightDots = generateDots(rightCount, isMoreSide = !leftIsMore)
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
        leftDots = leftDots.map { FlashCrowdUiState.Dot(it.x, it.y, it.radius) },
        rightDots = rightDots.map { FlashCrowdUiState.Dot(it.x, it.y, it.radius) },
    )

    private fun getDifficultyRatio(): Double = when {
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

    private fun generateDots(count: Int, isMoreSide: Boolean): List<Dot> {
        val minRadius = 0.02f
        val maxRadius = 0.055f

        val dots = mutableListOf<Dot>()
        var attempts = 0
        while (dots.size < count && attempts < count * 50) {
            val radius = Random.nextFloat() * (maxRadius - minRadius) + minRadius
            val x = radius + Random.nextFloat() * (1f - 2 * radius)
            val y = radius + Random.nextFloat() * (1f - 2 * radius)

            val overlaps = dots.any { existing ->
                val dx = existing.x - x
                val dy = existing.y - y
                val dist = sqrt(dx * dx + dy * dy)
                dist < (existing.radius + radius) * 0.8f
            }

            if (!overlaps) {
                dots.add(Dot(x, y, radius))
            }
            attempts++
        }
        return dots
    }
}
