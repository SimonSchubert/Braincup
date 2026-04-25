package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.SchulteTableUiState

class SchulteTableGame : Game() {
    sealed class TapResult {
        data object Correct : TapResult()
        data object Complete : TapResult()
        data object Wrong : TapResult()
    }

    val gridSize: Int = 4

    override val adaptiveDifficulty: Boolean = false

    val numbers = mutableListOf<Int>()
    var nextExpected: Int = 1
        private set
    var wrongTapIndex: Int? = null
        private set

    override fun generateRound() {
        val total = gridSize * gridSize
        numbers.clear()
        numbers.addAll((1..total).shuffled())
        nextExpected = 1
        wrongTapIndex = null
    }

    fun tapCell(index: Int): TapResult {
        if (index !in numbers.indices) return TapResult.Wrong
        if (numbers[index] == nextExpected) {
            nextExpected++
            wrongTapIndex = null
            return if (nextExpected > gridSize * gridSize) TapResult.Complete else TapResult.Correct
        }
        wrongTapIndex = index
        return TapResult.Wrong
    }

    fun clearWrongTap() {
        wrongTapIndex = null
    }

    override fun isCorrect(input: String): Boolean = false

    override fun solution(): String = ""

    override fun hint(): String? = null

    override fun toUiState(): SchulteTableUiState {
        val cells = numbers.mapIndexed { index, value ->
            val type = when {
                index == wrongTapIndex -> SchulteTableUiState.CellType.WRONG
                value < nextExpected -> SchulteTableUiState.CellType.TAPPED
                else -> SchulteTableUiState.CellType.NORMAL
            }
            SchulteTableUiState.CellState(number = value, type = type)
        }
        return SchulteTableUiState(gridSize = gridSize, cells = cells)
    }
}
