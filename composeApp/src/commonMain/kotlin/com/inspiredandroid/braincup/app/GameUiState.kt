package com.inspiredandroid.braincup.app

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.games.ColorConfusionGame
import com.inspiredandroid.braincup.games.DigitMemoryGame
import com.inspiredandroid.braincup.games.GhostGridGame
import com.inspiredandroid.braincup.games.NBackGame
import com.inspiredandroid.braincup.games.OrbitTrackerGame
import com.inspiredandroid.braincup.games.QuickSumGame
import com.inspiredandroid.braincup.games.SimonSaysGame
import com.inspiredandroid.braincup.games.SpotTheNewGame
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.games.minichess.PieceType
import com.inspiredandroid.braincup.games.tools.Animal
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Operator
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

enum class FigureCellState { NORMAL, WRONG, CORRECT, DIMMED }

enum class AnswerButtonState { NORMAL, WRONG, CORRECT, DIMMED }

enum class SequenceCellType { INACTIVE, ACTIVE, TAPPED, WRONG, MISSED }

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

@Immutable
sealed interface GameUiState

@Immutable
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
data class BubbleSumUiState(
    val bubbles: ImmutableList<BubbleState>,
    val answerLength: Int,
    val roundKey: Int,
) : GameUiState {
    @Immutable
    data class BubbleState(val value: Int)
}

@Immutable
data class QuickSumUiState(
    val phase: QuickSumGame.Phase,
    val currentTerm: Int?,
    val termIndex: Int,
    val termCount: Int,
    val answerLength: Int,
    val revealedSum: String?,
    val answerResult: QuickSumGame.AnswerResult?,
) : GameUiState

@Immutable
data class NBackUiState(
    val phase: NBackGame.Phase,
    val currentShape: Shape?,
    val showIndex: Int,
    val sequenceLength: Int,
    val askIndex: Int,
    val options: ImmutableList<Shape>,
    val revealAnswer: Shape?,
    val recallResult: NBackGame.RecallResult?,
) : GameUiState

@Immutable
data class ChainCalculationUiState(
    val calculation: String,
    val answer: Int,
) : GameUiState

@Immutable
data class MissingOperatorsUiState(
    val numbers: ImmutableList<Int>,
    val targetResult: Int,
    val operatorsCount: Int,
    val submittedOperators: ImmutableList<Operator>? = null,
    val correctOperators: ImmutableList<Operator>? = null,
    val feedbackRevealedSlots: ImmutableSet<Int> =
        kotlinx.collections.immutable.persistentSetOf(),
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
data class TowerOfHanoiUiState(
    val diskCount: Int,
    val pegsBottomToTop: ImmutableList<ImmutableList<Int>>,
    val selectedPeg: Int?,
    val rejectedPeg: Int? = null,
    val rejectFromPeg: Int? = null,
    val rejectFeedbackKey: Int = 0,
    val moves: Int,
    val level: Int,
) : GameUiState

@Immutable
data class ShikakuUiState(
    val rows: Int,
    val cols: Int,
    val clueByCellIndex: ImmutableMap<Int, Int>,
    val rectangles: ImmutableList<InclusiveRect>,
    val level: Int,
) : GameUiState {
    @Immutable
    data class InclusiveRect(
        val top: Int,
        val left: Int,
        val bottom: Int,
        val right: Int,
        val isValid: Boolean,
    )
}

@Immutable
data class CatQueensUiState(
    val size: Int,
    val regionIdByCellIndex: ImmutableList<Int>,
    val cats: ImmutableSet<Int>,
    val invalidCats: ImmutableSet<Int>,
    val level: Int,
    val violation: Violation? = null,
) : GameUiState {
    enum class Violation { ROW, COLUMN, ZONE, TOUCHING }
}

@Immutable
data class NurikabeUiState(
    val rows: Int,
    val cols: Int,
    val clueByCellIndex: ImmutableMap<Int, Int>,
    val seaCells: ImmutableSet<Int>,
    val satisfiedCells: ImmutableSet<Int>,
    val invalidCells: ImmutableSet<Int>,
    val forbiddenPoolCells: ImmutableSet<Int>,
    val disconnectedSeaCells: ImmutableSet<Int>,
    val level: Int,
) : GameUiState

@Immutable
data class KnotUiState(
    val rows: Int,
    val cols: Int,
    val endpoints: ImmutableList<Endpoint>,
    val paths: ImmutableMap<Int, ImmutableList<Int>>,
    val level: Int,
) : GameUiState {
    @Immutable
    data class Endpoint(val color: Int, val a: Int, val b: Int)
}

@Immutable
data class SoloChessUiState(
    val size: Int,
    val pieces: ImmutableMap<Int, PieceType>,
    val remainingCapturesByCell: ImmutableMap<Int, Int>,
    val kingCell: Int?,
    val selected: Int?,
    val targets: ImmutableSet<Int>,
    val level: Int,
    val stuck: Boolean,
) : GameUiState

@Immutable
data class PrismClearClearWave(
    val cellsBeforeClear: ImmutableList<Int?>,
    val clearedIndices: ImmutableList<Int>,
    val cellsAfterGravity: ImmutableList<Int?>,
)

@Immutable
data class PrismClearUiState(
    val rows: Int,
    val cols: Int,
    val tileOrdinals: ImmutableList<Int?>,
    val selectedIndex: Int?,
    val movesUsed: Int,
    val level: Int,
    val stuck: Boolean,
    val canUndo: Boolean = false,
    val rejectedFrom: Int? = null,
    val rejectedTo: Int? = null,
    val rejectFeedbackKey: Int = 0,
    val clearWaves: ImmutableList<PrismClearClearWave> = kotlinx.collections.immutable.persistentListOf(),
    val tileOrdinalsBeforeSwap: ImmutableList<Int?> = kotlinx.collections.immutable.persistentListOf(),
    val swapFromIndex: Int? = null,
    val swapToIndex: Int? = null,
    val boardAnimationKey: Int = 0,
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
    @Immutable
    data class CellState(val type: SequenceCellType)
}

@Immutable
data class SimonSaysUiState(
    val round: Int,
    val phase: SimonSaysGame.Phase,
    val pads: ImmutableList<PadState>,
    val sequenceLength: Int,
    val tappedCount: Int,
) : GameUiState {
    @Immutable
    data class PadState(val color: Color, val type: SequenceCellType)
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
    @Immutable
    data class Cell(
        val word: Color,
        val fontColor: Color,
        val isSelected: Boolean,
        val feedback: ColorConfusionGame.CellFeedback,
    )
}

@Immutable
data class OrbitTrackerUiState(
    val balls: ImmutableList<BallState>,
    val phase: OrbitTrackerGame.Phase,
    val targetCount: Int,
    val selectedCount: Int,
) : GameUiState {
    @Immutable
    data class BallState(
        val x: Float,
        val y: Float,
        val isTarget: Boolean,
        val isSelected: Boolean,
        val feedback: OrbitTrackerGame.BallFeedback,
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
        val index: Int,
        val type: SpotTheNewGame.CellType,
    )
}

@Immutable
data class DigitMemoryUiState(
    val phase: DigitMemoryGame.Phase,
    val sequence: String,
    val sequenceLength: Int,
    val problem: String,
    val answerLength: Int,
    val revealedMathAnswer: String?,
    val recallResult: DigitMemoryGame.RecallResult?,
) : GameUiState

enum class WordleLetterState {
    EMPTY,
    PENDING,
    ABSENT,
    PRESENT,
    CORRECT,
}

@Immutable
data class WordleLetter(val char: Char, val state: WordleLetterState)

@Immutable
data class WordleUiState(
    val rows: ImmutableList<ImmutableList<WordleLetter>>,
    val keyboardRows: ImmutableList<String>,
    val keyStates: ImmutableMap<Char, WordleLetterState>,
    val wordLength: Int,
    val solved: Boolean,
    val finished: Boolean,
    val answer: String?,
    val notEnoughLetters: Boolean,
    val notInWordList: Boolean,
) : GameUiState

@Immutable
data class BullsAndCowsGuess(
    val guess: String,
    val bulls: Int,
    val cows: Int,
)

@Immutable
data class BullsAndCowsUiState(
    val guesses: ImmutableList<BullsAndCowsGuess>,
    val currentGuess: String,
    val finished: Boolean,
    val won: Boolean,
    val secret: String?,
    val absentDigits: ImmutableSet<Char> = persistentSetOf(),
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
    val stalematingMovesByFrom: ImmutableMap<Int, ImmutableSet<Int>>,
    val lastMoveFromIndex: Int?,
    val lastMoveToIndex: Int?,
    val whiteInCheck: Boolean,
    val blackInCheck: Boolean,
    val isAiThinking: Boolean,
    val outcome: MiniChessOutcome?,
    val halfMoveCount: Int,
    val halfMoveCap: Int,
    val pointsForWin: Int,
) : GameUiState
