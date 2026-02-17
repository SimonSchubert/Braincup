package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.games.GhostGridGame
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape

enum class FigureCellState { NORMAL, WRONG, CORRECT, DIMMED }

enum class AnswerButtonState { NORMAL, WRONG, CORRECT, DIMMED }

data class AnswerButton(
    val value: String,
    val state: AnswerButtonState = AnswerButtonState.NORMAL,
)

data class FigureCell(
    val figure: Figure,
    val state: FigureCellState = FigureCellState.NORMAL,
)

sealed interface GameUiState

/**
 * Represents a token in the Sherlock Calculation expression builder.
 */
sealed class ExpressionToken(val displayValue: String) {
    data class NumberToken(val value: Int, val originalIndex: Int) : ExpressionToken(value.toString())
    data class OperatorToken(val operator: String) : ExpressionToken(operator)
}

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

data class ColoredShapesUiState(
    val displayedFigure: Figure,
    val answerShape: Shape,
    val answerColor: Color,
    val stringColor: Color,
    val shapePoints: Int,
    val colorPoints: Int,
    val possibleAnswers: List<AnswerButton>,
) : GameUiState

data class SherlockCalculationUiState(
    val result: Int,
    val numbers: List<Int>,
    val solutionTokens: List<ExpressionToken>? = null,
) : GameUiState

data class ValueComparisonUiState(
    val answers: List<String>,
) : GameUiState

data class AnomalyPuzzleUiState(
    val rows: List<List<FigureCell>>,
    val columnsPerRow: Int,
) : GameUiState

data class PathFinderUiState(
    val directionFigures: List<Figure>,
    val grid: List<List<FigureCell>>,
) : GameUiState

data class GridSolverUiState(
    val gridSize: Int,
    val resultsX: List<Int>,
    val resultsY: List<Int>,
) : GameUiState

data class PatternSequenceUiState(
    val sequence: List<Figure>,
    val optionRows: List<List<FigureCell>>,
) : GameUiState

data class GhostGridUiState(
    val gridSize: Int,
    val round: Int,
    val phase: GhostGridGame.Phase,
    val cells: List<CellState>,
    val sequenceLength: Int,
    val tappedCount: Int,
) : GameUiState {
    enum class CellType { INACTIVE, ACTIVE, TAPPED, WRONG, MISSED }
    data class CellState(val type: CellType)
}

data class ColorConfusionUiState(
    val cells: List<Cell>,
    val isSubmitted: Boolean,
) : GameUiState {
    enum class CellFeedback {
        NONE,
        CORRECT_SELECTED,
        WRONG_SELECTED,
        MISSED,
        CORRECT_UNSELECTED,
    }

    data class Cell(
        val word: String,
        val fontColor: Color,
        val isSelected: Boolean,
        val feedback: CellFeedback,
    )
}

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
