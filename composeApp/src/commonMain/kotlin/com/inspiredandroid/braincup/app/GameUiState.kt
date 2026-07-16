package com.inspiredandroid.braincup.app

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.games.DigitMemoryGame
import com.inspiredandroid.braincup.games.GhostGridGame
import com.inspiredandroid.braincup.games.OrbitTrackerGame
import com.inspiredandroid.braincup.games.QuickSumGame
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

@Immutable
sealed interface GameUiState

/**
 * Represents a token in the Sherlock Calculation expression builder.
 */
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
    /** The term on screen, or null during the blank gap between terms. */
    val currentTerm: Int?,
    val termIndex: Int,
    val termCount: Int,
    val answerLength: Int,
    /** Non-null while revealing the total after a submission. */
    val revealedSum: String?,
    val answerResult: QuickSumGame.AnswerResult?,
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
data class TowerOfHanoiUiState(
    val diskCount: Int,
    /** Disks bottom→top on each of the three pegs (larger size = larger disk). */
    val pegs: ImmutableList<ImmutableList<Int>>,
    val selectedPeg: Int?,
    /** Target peg of the latest illegal drop (larger on smaller), if any. */
    val rejectedPeg: Int? = null,
    /** Source peg of the latest illegal drop (disk that failed to move). */
    val rejectFromPeg: Int? = null,
    /** Bumps on every illegal drop so the UI can re-trigger reject feedback. */
    val rejectNonce: Int = 0,
    val moves: Int,
    val level: Int,
) : GameUiState

@Immutable
data class ShikakuUiState(
    val rows: Int,
    val cols: Int,
    /** cellIndex (row*cols+col) -> clue value; absent keys are blank cells. */
    val clues: ImmutableMap<Int, Int>,
    val rectangles: ImmutableList<RectState>,
    val level: Int,
) : GameUiState {
    /** A player-drawn rectangle with inclusive grid bounds and live validity. */
    @Immutable
    data class RectState(
        val top: Int,
        val left: Int,
        val bottom: Int,
        val right: Int,
        /** True when this rectangle contains exactly one clue equal to its area. */
        val isValid: Boolean,
    )
}

@Immutable
data class CatQueensUiState(
    /** Side length of the square board; also the cat / column / region count. */
    val size: Int,
    /** region id (0 until size) for each cell, indexed by row*size+col. */
    val regions: ImmutableList<Int>,
    /** Cells with a cat placed on them. */
    val cats: ImmutableSet<Int>,
    /** Placed cats that currently break a rule; shown with a warning ring. */
    val invalidCats: ImmutableSet<Int>,
    val level: Int,
    /** The rule the current placement breaks, if any; drives the contextual error message. */
    val violation: Violation? = null,
) : GameUiState {
    enum class Violation { ROW, COLUMN, ZONE, TOUCHING }
}

@Immutable
data class NurikabeUiState(
    val rows: Int,
    val cols: Int,
    /** cellIndex (row*cols+col) -> island size; absent keys are blank cells. */
    val clues: ImmutableMap<Int, Int>,
    /** Player-painted sea cells, by cellIndex. */
    val walls: ImmutableSet<Int>,
    /** White cells of islands that are complete and correct (one clue, exact size). */
    val satisfiedCells: ImmutableSet<Int>,
    /** White cells of islands that are already wrong (one clue but too many cells). */
    val invalidCells: ImmutableSet<Int>,
    /** Sea cells that form a 2x2 pool, which Nurikabe forbids. */
    val poolCells: ImmutableSet<Int>,
    /** Sea cells stranded in a non-main component while every island is already correct. */
    val disconnectedSeaCells: ImmutableSet<Int>,
    val level: Int,
) : GameUiState

@Immutable
data class KnotUiState(
    val rows: Int,
    val cols: Int,
    /** The colored endpoint pairs; color id drives the palette index. */
    val endpoints: ImmutableList<Endpoint>,
    /** color id -> ordered cells the player has drawn so far (first cell is an endpoint). */
    val paths: ImmutableMap<Int, ImmutableList<Int>>,
    val level: Int,
) : GameUiState {
    @Immutable
    data class Endpoint(val color: Int, val a: Int, val b: Int)
}

@Immutable
data class SoloChessUiState(
    /** Side length of the square board; cells are indexed row * size + col. */
    val size: Int,
    /** cell index -> piece type currently on that cell. */
    val pieces: ImmutableMap<Int, PieceType>,
    /** cell index -> captures the piece may still make; 0 means it can no longer move. */
    val capturesLeft: ImmutableMap<Int, Int>,
    /** The king's cell; the king can never be captured, so it is the last piece standing. */
    val kingCell: Int?,
    /** The currently selected piece's cell, if any. */
    val selected: Int?,
    /** Cells the selected piece may capture (highlighted). */
    val targets: ImmutableSet<Int>,
    val level: Int,
    /** True when no piece can capture and the board is unsolved: only a restart helps. */
    val stuck: Boolean,
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

/** Per-tile state for a Wordle letter. */
enum class WordleLetterState {
    /** No letter typed yet. */
    EMPTY,

    /** Typed into the current row but not yet submitted. */
    PENDING,

    /** Submitted and not in the word. */
    ABSENT,

    /** Submitted, in the word, wrong position. */
    PRESENT,

    /** Submitted, correct position. */
    CORRECT,
}

@Immutable
data class WordleLetter(val char: Char, val state: WordleLetterState)

@Immutable
data class WordleUiState(
    /** Always [WordleGame.MAX_GUESSES] rows of [wordLength] letters: submitted, in-progress, then empty. */
    val rows: ImmutableList<ImmutableList<WordleLetter>>,
    /** On-screen keyboard layout (rows of UPPERCASE letters). */
    val keyboardRows: ImmutableList<String>,
    /** Best state seen per letter across guesses; drives keyboard key colors. */
    val keyStates: ImmutableMap<Char, WordleLetterState>,
    val wordLength: Int,
    val solved: Boolean,
    val finished: Boolean,
    /** The answer to reveal under the board when the game ends; null while playing. */
    val answer: String?,
    /** True when the last submit was rejected for being too short; cleared on the next keypress. */
    val notEnoughLetters: Boolean,
    /** True when the last submit was not in the word list; cleared on the next keypress. */
    val notInWordList: Boolean,
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
