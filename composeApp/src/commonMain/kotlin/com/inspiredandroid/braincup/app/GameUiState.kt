package com.inspiredandroid.braincup.app

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.chess.PieceType
import com.inspiredandroid.braincup.games.ColorConfusionGame
import com.inspiredandroid.braincup.games.DigitMemoryGame
import com.inspiredandroid.braincup.games.GhostGridGame
import com.inspiredandroid.braincup.games.MentalFlexGame
import com.inspiredandroid.braincup.games.NBackGame
import com.inspiredandroid.braincup.games.OrbitTrackerGame
import com.inspiredandroid.braincup.games.QuickSumGame
import com.inspiredandroid.braincup.games.RevealResult
import com.inspiredandroid.braincup.games.RuleShiftCard
import com.inspiredandroid.braincup.games.SimonSaysGame
import com.inspiredandroid.braincup.games.SpotTheNewGame
import com.inspiredandroid.braincup.games.TrioFill
import com.inspiredandroid.braincup.games.TrioGame
import com.inspiredandroid.braincup.games.TrioShape
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.games.matrix.MatrixPanel
import com.inspiredandroid.braincup.games.tools.Animal
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Operator
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

/** How an answer reveal marked a board cell or an answer button. */
enum class AnswerFeedbackState { NORMAL, WRONG, CORRECT, DIMMED }

enum class SequenceCellType { INACTIVE, ACTIVE, TAPPED, WRONG, MISSED }

@Immutable
data class AnswerButton(
    val value: String,
    val state: AnswerFeedbackState = AnswerFeedbackState.NORMAL,
)

/**
 * A board cell that answer feedback can recolour. The self type keeps
 * [GameController.withFeedbackStates] generic over cells that hold different artwork.
 */
@Immutable
sealed interface FeedbackCell<T : FeedbackCell<T>> {
    val state: AnswerFeedbackState

    fun withState(state: AnswerFeedbackState): T
}

@Immutable
data class FigureCell(
    val figure: Figure,
    override val state: AnswerFeedbackState = AnswerFeedbackState.NORMAL,
) : FeedbackCell<FigureCell> {
    override fun withState(state: AnswerFeedbackState) = copy(state = state)
}

@Immutable
data class RuleShiftKeyCell(
    val card: RuleShiftCard,
    override val state: AnswerFeedbackState = AnswerFeedbackState.NORMAL,
) : FeedbackCell<RuleShiftKeyCell> {
    override fun withState(state: AnswerFeedbackState) = copy(state = state)
}

@Immutable
sealed interface GameUiState

/**
 * A [GameUiState] whose screen carries no run timer: the attempt ends on the board itself
 * (solved, wrong tap, out of guesses) rather than when a countdown expires.
 */
@Immutable
sealed interface UntimedUiState : GameUiState

/**
 * An [UntimedUiState] for a puzzle scored by the level reached, so every one of these boards can
 * show its [level] the same way.
 */
@Immutable
sealed interface LevelUiState : UntimedUiState {
    val level: Int
}

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
    val answerResult: RevealResult?,
) : GameUiState

@Immutable
data class NBackUiState(
    /** The level is n: level 3 is 3-back. */
    override val level: Int,
    val phase: NBackGame.Phase,
    /** Null during the blank between two items, and outside the stream. */
    val currentShape: Shape?,
    /** How far the block has run. The only progress signal, since there is no clock. */
    val blockProgress: Float,
    val responded: Boolean,
    /** This trial's tap, marked on the Match button until the trial closes. */
    val lastResponse: NBackGame.Response?,
) : LevelUiState

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
    val answerColor: GameColor,
    val stringColor: GameColor,
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

/**
 * Task switching. [rows] is the single row of candidate tiles, carried as rows plus
 * [columnsPerRow] so `GameController.withFeedbackStates` can colour it like any other figure
 * board. Deliberately not an [UntimedUiState]: the run is scored over 60 seconds.
 */
@Immutable
data class MentalFlexUiState(
    val rule: MentalFlexGame.Rule,
    /** The two figures forming the rule cue; see [MentalFlexGame.cueExemplar]. */
    val cueExemplar: ImmutableList<Figure>,
    val target: Figure,
    val rows: ImmutableList<ImmutableList<FigureCell>>,
    val columnsPerRow: Int,
) : GameUiState

/**
 * Card sorting against an undisclosed rule. [keyCards] never change during a run; only the tapped
 * one is recoloured, for the feedback beat that is the player's sole source of information.
 *
 * Carries no streak or category counter on purpose: either would announce the silent rule change a
 * trial early. [cardsRemaining] is safe and stands in for the shrinking deck of the real test.
 *
 * An [UntimedUiState]: the run ends on the deck, not on a clock, because the task is not a measure
 * of speed.
 */
@Immutable
data class RuleShiftUiState(
    val keyCards: ImmutableList<RuleShiftKeyCell>,
    val stimulus: RuleShiftCard,
    /** True while the feedback beat is up, so the board can stop taking taps. */
    val isAwaitingNextCard: Boolean,
    val cardsRemaining: Int,
) : UntimedUiState

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
    override val level: Int,
) : LevelUiState

@Immutable
data class SlidingPuzzleUiState(
    val gridSize: Int,
    val tiles: ImmutableList<Int>,
    val moves: Int,
    override val level: Int,
) : LevelUiState

@Immutable
data class TowerOfHanoiUiState(
    val diskCount: Int,
    val pegsBottomToTop: ImmutableList<ImmutableList<Int>>,
    val selectedPeg: Int?,
    val rejectedPeg: Int? = null,
    val rejectFromPeg: Int? = null,
    val rejectFeedbackKey: Int = 0,
    val moves: Int,
    override val level: Int,
) : LevelUiState

@Immutable
data class ShikakuUiState(
    val rows: Int,
    val cols: Int,
    val clueByCellIndex: ImmutableMap<Int, Int>,
    val rectangles: ImmutableList<InclusiveRect>,
    override val level: Int,
) : LevelUiState {
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
    override val level: Int,
    val violation: Violation? = null,
) : LevelUiState {
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
    override val level: Int,
) : LevelUiState

@Immutable
data class KnotUiState(
    val rows: Int,
    val cols: Int,
    val endpoints: ImmutableList<Endpoint>,
    val paths: ImmutableMap<Int, ImmutableList<Int>>,
    override val level: Int,
) : LevelUiState {
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
    override val level: Int,
    val stuck: Boolean,
) : LevelUiState

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
    override val level: Int,
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
) : LevelUiState

@Immutable
data class MatrixOptionCell(
    val panel: MatrixPanel,
    override val state: AnswerFeedbackState = AnswerFeedbackState.NORMAL,
) : FeedbackCell<MatrixOptionCell> {
    override fun withState(state: AnswerFeedbackState) = copy(state = state)
}

@Immutable
data class PatternSequenceUiState(
    /** Nine panels in reading order; the entry the player has to supply is null. */
    val matrix: ImmutableList<MatrixPanel?>,
    val optionRows: ImmutableList<ImmutableList<MatrixOptionCell>>,
    val optionColumns: Int,
) : GameUiState

@Immutable
data class GhostGridUiState(
    val gridSize: Int,
    val round: Int,
    val phase: GhostGridGame.Phase,
    val cells: ImmutableList<CellState>,
    val sequenceLength: Int,
    val tappedCount: Int,
) : UntimedUiState {
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
) : UntimedUiState {
    @Immutable
    data class PadState(val color: GameColor, val type: SequenceCellType)
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
        val word: GameColor,
        val fontColor: GameColor,
        val isSelected: Boolean,
        val feedback: ColorConfusionGame.CellFeedback,
    )
}

@Immutable
data class TrioUiState(
    val cards: ImmutableList<Card>,
    val columns: Int,
) : GameUiState {
    @Immutable
    data class Card(
        val shape: TrioShape,
        val count: Int,
        val fill: TrioFill,
        val feedback: TrioGame.CardFeedback,
    )
}

@Immutable
data class OrbitTrackerUiState(
    val balls: ImmutableList<BallState>,
    val phase: OrbitTrackerGame.Phase,
    val targetCount: Int,
    val selectedCount: Int,
) : UntimedUiState {
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
data class MentalRotationsUiState(
    val roundKey: Int,
    val reference: Figure,
    val candidate: Figure,
    val answers: ImmutableList<AnswerButton>,
) : GameUiState {
    /**
     * A figure already flattened to the screen plane by the game, in arbitrary units: the screen
     * scales [width] x [height] to fit and draws [cubes] in the order given, which is back to
     * front, so later cubes paint over the ones they hide.
     */
    @Immutable
    data class Figure(
        val cubes: ImmutableList<ProjectedCube>,
        val width: Float,
        val height: Float,
    )

    /** Top-centre of a unit cube's top face, in the same units as [Figure.width]. */
    @Immutable
    data class ProjectedCube(val x: Float, val y: Float)
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
    val recallResult: RevealResult?,
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
) : UntimedUiState

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
) : UntimedUiState

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
) : UntimedUiState
