package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape

sealed interface GameUiState

data class MentalCalculationUiState(
    val calculation: String,
    val answerLength: Int,
) : GameUiState

data class ChainCalculationUiState(
    val calculation: String,
    val answer: Int,
) : GameUiState

data class FractionCalculationUiState(
    val calculation: String,
    val answerString: String,
) : GameUiState

data class ColorConfusionUiState(
    val displayedFigure: Figure,
    val answerShape: Shape,
    val answerColor: Color,
    val stringColor: Color,
    val shapePoints: Int,
    val colorPoints: Int,
    val possibleAnswers: List<String>,
) : GameUiState

data class SherlockCalculationUiState(
    val result: Int,
    val numbers: List<Int>,
) : GameUiState

data class ValueComparisonUiState(
    val answers: List<String>,
) : GameUiState

data class AnomalyPuzzleUiState(
    val rows: List<List<Figure>>,
    val columnsPerRow: Int,
    val wrongAnswerIndex: Int? = null,
    val correctAnswerIndex: Int? = null,
) : GameUiState

data class PathFinderUiState(
    val directionFigures: List<Figure>,
    val startX: Int,
    val startY: Int,
) : GameUiState

data class GridSolverUiState(
    val gridSize: Int,
    val resultsX: List<Int>,
    val resultsY: List<Int>,
) : GameUiState

data class PatternSequenceUiState(
    val sequence: List<Figure>,
    val optionRows: List<List<Figure>>,
) : GameUiState

data class VisualMemoryUiState(
    val round: Int,
    val phase: VisualMemoryGame.Phase,
    val countdown: Int,
    val cells: List<CellState>,
    val answerOptions: List<AnswerOption>,
    val currentTargetFigure: Figure?,
) : GameUiState {

    enum class CellType {
        EMPTY,
        MEMORIZING,
        REVEALED,
        HIDDEN,
        CURRENT_TARGET,
        WRONG,
    }

    data class CellState(
        val type: CellType,
        val figure: Figure?,
    )

    data class AnswerOption(
        val figure: Figure,
        val figureIndex: Int,
        val enabled: Boolean,
        val isWrong: Boolean = false,
    )
}
