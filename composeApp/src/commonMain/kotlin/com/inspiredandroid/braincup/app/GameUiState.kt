package com.inspiredandroid.braincup.app

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.games.GhostGridGame
import com.inspiredandroid.braincup.games.OrbitTrackerGame
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape

enum class FigureCellState { NORMAL, WRONG, CORRECT, DIMMED }

enum class AnswerButtonState { NORMAL, WRONG, CORRECT, DIMMED }

@Immutable
data class AnswerButton(
    val value: String,
    val state: AnswerButtonState = AnswerButtonState.NORMAL,
)

@Immutable
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

@Immutable
data class MentalCalculationUiState(
    val calculation: String,
    val answerLength: Int,
) : GameUiState

@Immutable
data class ChainCalculationUiState(
    val calculation: String,
    val answer: Int,
) : GameUiState

@Immutable
data class FractionCalculationUiState(
    val calculation: String,
    val answerString: String,
) : GameUiState

@Immutable
data class ColoredShapesUiState(
    val displayedFigure: Figure,
    val answerShape: Shape,
    val answerColor: Color,
    val stringColor: Color,
    val shapePoints: Int,
    val colorPoints: Int,
    val possibleAnswers: List<AnswerButton>,
) : GameUiState

@Immutable
data class SherlockCalculationUiState(
    val result: Int,
    val numbers: List<Int>,
    val solutionTokens: List<ExpressionToken>? = null,
) : GameUiState

@Immutable
data class ValueComparisonUiState(
    val answers: List<AnswerButton>,
) : GameUiState

@Immutable
data class AnomalyPuzzleUiState(
    val rows: List<List<FigureCell>>,
    val columnsPerRow: Int,
) : GameUiState

@Immutable
data class PathFinderUiState(
    val directionFigures: List<Figure>,
    val grid: List<List<FigureCell>>,
) : GameUiState

@Immutable
data class MiniSudokuUiState(
    val gridSize: Int,
    val blockRows: Int,
    val blockCols: Int,
    val initialValues: List<Int?>,
    val solutionValues: List<Int>? = null,
) : GameUiState

@Immutable
data class PatternSequenceUiState(
    val sequence: List<Figure>,
    val optionRows: List<List<FigureCell>>,
) : GameUiState

@Immutable
data class GhostGridUiState(
    val gridSize: Int,
    val round: Int,
    val phase: GhostGridGame.Phase,
    val cells: List<CellState>,
    val sequenceLength: Int,
    val tappedCount: Int,
) : GameUiState {
    enum class CellType { INACTIVE, ACTIVE, TAPPED, WRONG, MISSED }

    @Immutable
    data class CellState(val type: CellType)
}

@Immutable
data class SchulteTableUiState(
    val gridSize: Int,
    val cells: List<CellState>,
) : GameUiState {
    enum class CellType { NORMAL, TAPPED, WRONG }

    @Immutable
    data class CellState(val number: Int, val type: CellType)
}

@Immutable
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

    @Immutable
    data class Cell(
        val word: Color,
        val fontColor: Color,
        val isSelected: Boolean,
        val feedback: CellFeedback,
    )
}

@Immutable
data class OrbitTrackerUiState(
    val balls: List<BallState>,
    val phase: OrbitTrackerGame.Phase,
    val targetCount: Int,
    val selectedCount: Int,
) : GameUiState {
    enum class BallFeedback {
        NONE,
        CORRECT_SELECTED,
        WRONG_SELECTED,
        MISSED,
    }

    @Immutable
    data class BallState(
        val x: Float,
        val y: Float,
        val isTarget: Boolean,
        val isSelected: Boolean,
        val feedback: BallFeedback,
    )
}

@Immutable
data class FlashCrowdUiState(
    val roundKey: Int,
    val leftDots: List<Dot>,
    val rightDots: List<Dot>,
) : GameUiState {
    @Immutable
    data class Dot(val x: Float, val y: Float, val radius: Float)
}

@Immutable
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

    @Immutable
    data class CellState(
        val type: CellType,
        val figure: Figure?,
    )

    @Immutable
    data class AnswerOption(
        val figure: Figure,
        val figureIndex: Int,
        val enabled: Boolean,
        val isWrong: Boolean = false,
    )
}
