package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.addOrIncrease
import com.inspiredandroid.braincup.splitToIntList
import kotlin.random.Random

class GridSolverGame : Game() {
    val entries = mutableListOf<MutableList<Int>>()
    val resultsX = mutableListOf<Int>()
    val resultsY = mutableListOf<Int>()

    fun size(): Int = when {
        round > 7 -> 4
        round > 4 -> 3
        else -> 2
    }

    private fun maxEntryValue(): Int = when {
        round > 5 -> 9
        round > 3 -> 7
        else -> 6
    }

    override fun nextRound() {
        entries.clear()
        resultsX.clear()
        resultsY.clear()

        repeat(size()) { x ->
            entries.add(mutableListOf())
            repeat(size()) {
                val entry = Random.nextInt(2, maxEntryValue())
                entries[x].add(entry)
            }
        }

        repeat(size()) { x ->
            resultsY.add(entries[x].sum())
            repeat(size()) { y ->
                resultsX.addOrIncrease(x, entries[y][x])
            }
        }
    }

    override fun isCorrect(input: String): Boolean {
        val numbers = input.splitToIntList()
        if (numbers.count() < size().times(size())) {
            return false
        }
        resultsY.forEachIndexed { index, _ ->
            val sum = numbers.subList(index * size(), index * size() + size()).sum()
            if (sum != resultsY[index]) {
                return false
            }
        }
        resultsX.forEachIndexed { index, _ ->
            val sum =
                numbers.filterIndexed { numberIndex, _ -> numberIndex % size() == index }.sum()
            if (sum != resultsX[index]) {
                return false
            }
        }
        return true
    }

    override fun solution(): String {
        var solution = ""
        entries.forEach {
            it.forEach {
                solution += "$it "
            }
        }
        return solution
    }

    override fun getGameType(): GameType = GameType.GRID_SOLVER

    override fun hint(): String? = ""
}
