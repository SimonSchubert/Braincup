package com.inspiredandroid.braincup.app

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.games.DigitMemoryGame
import com.inspiredandroid.braincup.games.GhostGridGame
import com.inspiredandroid.braincup.games.OrbitTrackerGame
import com.inspiredandroid.braincup.games.SpotTheNewGame
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.games.minichess.PieceType
import com.inspiredandroid.braincup.games.tools.Animal
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet

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
    val possibleAnswers: ImmutableList<AnswerButton>,
) : GameUiState

@Immutable
data class SherlockCalculationUiState(
    val result: Int,
    val numbers: ImmutableList<Int>,
    val solutionTokens: ImmutableList<ExpressionToken>? = null,
) : GameUiState

@Immutable
data class ValueComparisonUiState(
    val answers: ImmutableList<AnswerButton>,
) : GameUiState

@Immutable
data class AnomalyPuzzleUiState(
    val rows: ImmutableList<ImmutableList<FigureCell>>,
    val columnsPerRow: Int,
) : GameUiState

@Immutable
data class PathFinderUiState(
    val directionFigures: ImmutableList<Figure>,
    val grid: ImmutableList<ImmutableList<FigureCell>>,
) : GameUiState

@Immutable
data class MiniSudokuUiState(
    val gridSize: Int,
    val blockRows: Int,
    val blockCols: Int,
    val initialValues: ImmutableList<Int?>,
    val solutionValues: ImmutableList<Int>? = null,
) : GameUiState

@Immutable
data class LightsOutUiState(
    val gridSize: Int,
    val cells: ImmutableList<Boolean>,
    val moves: Int,
    val level: Int,
) : GameUiState

@Immutable
data class SlidingPuzzleUiState(
    val gridSize: Int,
    val tiles: ImmutableList<Int>,
    val moves: Int,
    val level: Int,
) : GameUiState

@Immutable
data class PatternSequenceUiState(
    val sequence: ImmutableList<Figure>,
    val optionRows: ImmutableList<ImmutableList<FigureCell>>,
) : GameUiState

@Immutable
data class GhostGridUiState(
    val gridSize: Int,
    val round: Int,
    val phase: GhostGridGame.Phase,
    val cells: ImmutableList<CellState>,
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
    val cells: ImmutableList<CellState>,
) : GameUiState {
    enum class CellType { NORMAL, TAPPED, WRONG }

    @Immutable
    data class CellState(val number: Int, val type: CellType)
}

@Immutable
data class ColorConfusionUiState(
    val cells: ImmutableList<Cell>,
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
    val balls: ImmutableList<BallState>,
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
    val leftDots: ImmutableList<Dot>,
    val rightDots: ImmutableList<Dot>,
) : GameUiState {
    @Immutable
    data class Dot(val x: Float, val y: Float, val radius: Float)
}

@Immutable
data class FlagsUiState(
    val countrySlug: String,
    val possibleAnswers: ImmutableList<AnswerButton>,
    val currentScore: Int,
    val bestScore: Int,
) : GameUiState

@Immutable
data class VisualMemoryUiState(
    val round: Int,
    val phase: VisualMemoryGame.Phase,
    val countdown: Int,
    val cells: ImmutableList<CellState>,
    val answerOptions: ImmutableList<AnswerOption>,
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

@Immutable
data class SpotTheNewUiState(
    val round: Int,
    val phase: SpotTheNewGame.Phase,
    val displayedCount: Int,
    val cells: ImmutableList<CellState>,
) : GameUiState {
    @Immutable
    data class CellState(
        val animal: Animal,
        /** Position in the displayed list; the value passed back on tap. */
        val index: Int,
        val type: SpotTheNewGame.CellType,
    )
}

@Immutable
data class DigitMemoryUiState(
    val phase: DigitMemoryGame.Phase,
    /** The digits to memorize; shown during SHOWING and during the recall reveal. */
    val sequence: String,
    /** Expected recall length (drives auto-submit). */
    val sequenceLength: Int,
    /** Distraction problem text shown during SOLVING, e.g. "3 + 4". */
    val problem: String,
    /** Expected math answer length (drives auto-submit). */
    val answerLength: Int,
    /** Non-null => flash the correct math answer after a wrong attempt. */
    val revealedMathAnswer: String?,
    /** Non-null => reveal the sequence colored by correct/wrong. */
    val recallResult: DigitMemoryGame.RecallResult?,
) : GameUiState

enum class MiniChessOutcome { PLAYER_WIN, PLAYER_LOSS, DRAW }

@Immutable
data class MiniChessCell(
    val pieceType: PieceType?,
    val isWhite: Boolean,
)

@Immutable
data class MiniChessUiState(
    val cells: ImmutableList<MiniChessCell>,
    val legalMovesByFrom: ImmutableMap<Int, ImmutableSet<Int>>,
    /** Subset of [legalMovesByFrom] entries (same keys) whose move would immediately
     *  stalemate the CPU — i.e. the resulting position has no legal CPU response and the
     *  CPU is not in check. Players can use this to avoid accidental draws. */
    val stalematingMovesByFrom: ImmutableMap<Int, ImmutableSet<Int>>,
    val lastMoveFromIndex: Int?,
    val lastMoveToIndex: Int?,
    val whiteInCheck: Boolean,
    val blackInCheck: Boolean,
    val isAiThinking: Boolean,
    val outcome: MiniChessOutcome?,
    val halfMoveCount: Int,
    val halfMoveCap: Int,
    /** Points awarded for a win at the current difficulty (drives the +N XP label). */
    val pointsForWin: Int,
) : GameUiState
