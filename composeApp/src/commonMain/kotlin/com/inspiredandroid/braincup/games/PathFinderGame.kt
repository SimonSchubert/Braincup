package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Direction
import com.inspiredandroid.braincup.splitToIntList
import kotlin.random.Random

class PathFinderGame : Game() {
    val directions = mutableListOf<Direction>()
    val gridSize = 4
    var lastDirection = Direction.UP
    var startX = 0
    val startY = 0
    var currentX = 0
    var currentY = 0

    override fun nextRound() {
        directions.clear()
        lastDirection = Direction.UP
        startX = Random.nextInt(4)
        currentX = startX
        currentY = startY

        while (directions.size < 3 + round) {
            val direction = Direction.entries.random()
            when {
                direction == Direction.UP && currentY == 0 -> {
                }
                direction == Direction.DOWN && currentY == gridSize - 1 -> {
                }
                direction == Direction.LEFT && currentX == 0 -> {
                }
                direction == Direction.RIGHT && currentX == gridSize - 1 -> {
                }
                direction != lastDirection -> {
                    lastDirection = direction
                    directions.add(direction)
                    when (direction) {
                        Direction.UP -> currentY--
                        Direction.RIGHT -> currentX++
                        Direction.DOWN -> currentY++
                        Direction.LEFT -> currentX--
                    }
                }
            }
        }
    }

    private fun correctGridIndex(): Int = currentY * gridSize + currentX + 1

    override fun isCorrect(input: String): Boolean = try {
        val index = input.toInt()
        index == correctGridIndex()
    } catch (ignore: Exception) {
        val coordinates = input.splitToIntList()
        if (coordinates.count() < 2) {
            false
        } else {
            coordinates[0] == currentX + 1 && coordinates[1] == currentY + 1
        }
    }

    override fun solution(): String = "column ${currentX + 1} and row ${currentY + 1}"

    override fun getGameType(): GameType = GameType.PATH_FINDER

    override fun hint(): String? = ""
}
