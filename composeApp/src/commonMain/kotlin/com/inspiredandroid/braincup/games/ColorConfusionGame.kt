package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.ColorConfusionUiState
import com.inspiredandroid.braincup.games.tools.Color

class ColorConfusionGame : Game() {
    enum class CellFeedback {
        NONE,
        CORRECT_SELECTED,
        WRONG_SELECTED,
        MISSED,
        CORRECT_UNSELECTED,
    }

    data class Cell(
        val word: Color,
        val fontColor: Color,
    ) {
        val isMatching: Boolean get() = word == fontColor
    }

    var cells: List<Cell> = emptyList()
    var selectedIndices: MutableSet<Int> = mutableSetOf()
    var feedbackState: List<CellFeedback> = emptyList()
    var isSubmitted: Boolean = false

    companion object {
        val GAME_COLORS = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.PURPLE, Color.YELLOW, Color.ORANGE)
    }

    override fun generateRound() {
        val matchCount = (2..5).random()
        val gridCells = mutableListOf<Cell>()

        // Generate matching cells
        repeat(matchCount) {
            val color = GAME_COLORS.random()
            gridCells.add(Cell(word = color, fontColor = color))
        }

        // Generate non-matching cells
        repeat(9 - matchCount) {
            val word = GAME_COLORS.random()
            var fontColor: Color
            do {
                fontColor = GAME_COLORS.random()
            } while (fontColor == word)
            gridCells.add(Cell(word = word, fontColor = fontColor))
        }

        cells = gridCells.shuffled()
        selectedIndices = mutableSetOf()
        feedbackState = List(9) { CellFeedback.NONE }
        isSubmitted = false
    }

    fun toggleCell(index: Int) {
        if (isSubmitted || index !in cells.indices) return
        if (index in selectedIndices) {
            selectedIndices.remove(index)
        } else {
            selectedIndices.add(index)
        }
    }

    fun submit(): Boolean {
        isSubmitted = true
        feedbackState = cells.mapIndexed { index, cell ->
            val isSelected = index in selectedIndices
            when {
                isSelected && cell.isMatching -> CellFeedback.CORRECT_SELECTED
                isSelected && !cell.isMatching -> CellFeedback.WRONG_SELECTED
                !isSelected && cell.isMatching -> CellFeedback.MISSED
                else -> CellFeedback.CORRECT_UNSELECTED
            }
        }

        val correct = feedbackState.none {
            it == CellFeedback.WRONG_SELECTED || it == CellFeedback.MISSED
        }
        if (!correct) {
            answeredAllCorrect = false
        }
        return correct
    }

    override fun isCorrect(input: String): Boolean {
        return false // Not used directly
    }

    override fun solution(): String = cells.mapIndexedNotNull { index, cell ->
        if (cell.isMatching) index.toString() else null
    }.joinToString(", ")

    override fun hint(): String? = null

    override fun toUiState(): ColorConfusionUiState = ColorConfusionUiState(
        cells = cells.mapIndexed { index, cell ->
            ColorConfusionUiState.Cell(
                word = cell.word.displayName.uppercase(),
                fontColor = cell.fontColor,
                isSelected = index in selectedIndices,
                feedback = when (feedbackState[index]) {
                    CellFeedback.NONE -> ColorConfusionUiState.CellFeedback.NONE
                    CellFeedback.CORRECT_SELECTED -> ColorConfusionUiState.CellFeedback.CORRECT_SELECTED
                    CellFeedback.WRONG_SELECTED -> ColorConfusionUiState.CellFeedback.WRONG_SELECTED
                    CellFeedback.MISSED -> ColorConfusionUiState.CellFeedback.MISSED
                    CellFeedback.CORRECT_UNSELECTED -> ColorConfusionUiState.CellFeedback.CORRECT_UNSELECTED
                },
            )
        },
        isSubmitted = isSubmitted,
    )
}
