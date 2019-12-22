package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Direction
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
            val direction = Direction.values().random()
            when {
                direction == Direction.UP && currentY == 0 -> {
                    println("cancel: $direction")
                }
                direction == Direction.DOWN && currentY == gridSize - 1 -> {
                    println("cancel: $direction")
                }
                direction == Direction.LEFT && currentX == 0 -> {
                    println("cancel: $direction")
                }
                direction == Direction.RIGHT && currentX == gridSize - 1 -> {
                    println("cancel: $direction")
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

    override fun isCorrect(input: String): Boolean {
        return input == correctGridIndex().toString()
    }

    override fun solution(): String {
        return "row ${currentY + 1} and column ${currentX + 1}"
    }

    override fun getGameType(): GameType {
        return GameType.PATH_FINDER
    }

    override fun hint(): String? {
        return ""
    }
}