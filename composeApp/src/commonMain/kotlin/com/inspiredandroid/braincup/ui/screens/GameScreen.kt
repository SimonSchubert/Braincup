package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inspiredandroid.braincup.app.AnomalyPuzzleUiState
import com.inspiredandroid.braincup.app.BubbleSumUiState
import com.inspiredandroid.braincup.app.CatQueensUiState
import com.inspiredandroid.braincup.app.ChainCalculationUiState
import com.inspiredandroid.braincup.app.ColorConfusionUiState
import com.inspiredandroid.braincup.app.ColoredShapesUiState
import com.inspiredandroid.braincup.app.DigitMemoryUiState
import com.inspiredandroid.braincup.app.FlagsUiState
import com.inspiredandroid.braincup.app.FlashCrowdUiState
import com.inspiredandroid.braincup.app.FractionCalculationUiState
import com.inspiredandroid.braincup.app.GameController
import com.inspiredandroid.braincup.app.GameUiState
import com.inspiredandroid.braincup.app.GhostGridUiState
import com.inspiredandroid.braincup.app.KnotUiState
import com.inspiredandroid.braincup.app.LightsOutUiState
import com.inspiredandroid.braincup.app.MentalCalculationUiState
import com.inspiredandroid.braincup.app.MiniChessUiState
import com.inspiredandroid.braincup.app.MiniSudokuUiState
import com.inspiredandroid.braincup.app.NBackUiState
import com.inspiredandroid.braincup.app.NurikabeUiState
import com.inspiredandroid.braincup.app.OrbitTrackerUiState
import com.inspiredandroid.braincup.app.PathFinderUiState
import com.inspiredandroid.braincup.app.PatternSequenceUiState
import com.inspiredandroid.braincup.app.QuickSumUiState
import com.inspiredandroid.braincup.app.SchulteTableUiState
import com.inspiredandroid.braincup.app.SherlockCalculationUiState
import com.inspiredandroid.braincup.app.ShikakuUiState
import com.inspiredandroid.braincup.app.SimonSaysUiState
import com.inspiredandroid.braincup.app.SlidingPuzzleUiState
import com.inspiredandroid.braincup.app.SoloChessUiState
import com.inspiredandroid.braincup.app.SpotTheNewUiState
import com.inspiredandroid.braincup.app.TowerOfHanoiUiState
import com.inspiredandroid.braincup.app.ValueComparisonUiState
import com.inspiredandroid.braincup.app.VisualMemoryUiState
import com.inspiredandroid.braincup.app.WordleUiState
import com.inspiredandroid.braincup.games.BubbleSumGame
import com.inspiredandroid.braincup.games.SpotTheNewGame
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.ui.components.GameScaffold
import com.inspiredandroid.braincup.ui.screens.games.AnomalyPuzzleContent
import com.inspiredandroid.braincup.ui.screens.games.BubbleSumContent
import com.inspiredandroid.braincup.ui.screens.games.CatQueensContent
import com.inspiredandroid.braincup.ui.screens.games.ChainCalculationContent
import com.inspiredandroid.braincup.ui.screens.games.ColorConfusionContent
import com.inspiredandroid.braincup.ui.screens.games.ColoredShapesContent
import com.inspiredandroid.braincup.ui.screens.games.DigitMemoryContent
import com.inspiredandroid.braincup.ui.screens.games.FlagsContent
import com.inspiredandroid.braincup.ui.screens.games.FlashCrowdContent
import com.inspiredandroid.braincup.ui.screens.games.FractionCalculationContent
import com.inspiredandroid.braincup.ui.screens.games.GhostGridContent
import com.inspiredandroid.braincup.ui.screens.games.KnotContent
import com.inspiredandroid.braincup.ui.screens.games.LightsOutContent
import com.inspiredandroid.braincup.ui.screens.games.MemorizeTimeProgressBar
import com.inspiredandroid.braincup.ui.screens.games.MentalCalculationContent
import com.inspiredandroid.braincup.ui.screens.games.MiniChessContent
import com.inspiredandroid.braincup.ui.screens.games.MiniSudokuContent
import com.inspiredandroid.braincup.ui.screens.games.NBackContent
import com.inspiredandroid.braincup.ui.screens.games.NurikabeContent
import com.inspiredandroid.braincup.ui.screens.games.OrbitTrackerContent
import com.inspiredandroid.braincup.ui.screens.games.PathFinderContent
import com.inspiredandroid.braincup.ui.screens.games.PatternSequenceContent
import com.inspiredandroid.braincup.ui.screens.games.QuickSumContent
import com.inspiredandroid.braincup.ui.screens.games.SchulteTableContent
import com.inspiredandroid.braincup.ui.screens.games.SherlockCalculationContent
import com.inspiredandroid.braincup.ui.screens.games.ShikakuContent
import com.inspiredandroid.braincup.ui.screens.games.SimonSaysContent
import com.inspiredandroid.braincup.ui.screens.games.SlidingPuzzleContent
import com.inspiredandroid.braincup.ui.screens.games.SoloChessContent
import com.inspiredandroid.braincup.ui.screens.games.SpotTheNewContent
import com.inspiredandroid.braincup.ui.screens.games.StopwatchDisplay
import com.inspiredandroid.braincup.ui.screens.games.TimeProgressIndicator
import com.inspiredandroid.braincup.ui.screens.games.TowerOfHanoiContent
import com.inspiredandroid.braincup.ui.screens.games.ValueComparisonContent
import com.inspiredandroid.braincup.ui.screens.games.VisualMemoryContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Live game host. Timer values are taken as [StateFlow]s and collected only inside the progress
 * bar so a 100ms countdown does not restart game content.
 *
 * For previews/screenshots, prefer the overload that accepts fixed [Long] values.
 */
@Composable
fun GameScreen(
    gameUiState: GameUiState,
    timeRemaining: StateFlow<Long>,
    elapsedTime: StateFlow<Long>,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
    onBack: () -> Unit,
    inSessionMode: Boolean = false,
    isTimerPaused: Boolean = false,
    onWordleFinishedAction: () -> Unit = {},
    /** Live ball positions during Orbit Tracker MOVING; ignored for other games. */
    orbitBallPositions: StateFlow<List<Pair<Float, Float>>>? = null,
    /** Live Bubble Sum frames (position + visibility); ignored for other games. */
    bubbleSumFrames: StateFlow<List<BubbleSumGame.BubbleFrame>>? = null,
    /** Reports the measured Bubble Sum arena size in pixels; ignored for other games. */
    onBubbleSumArenaSize: (Float, Float) -> Unit = { _, _ -> },
) {
    val progressBarModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)

    val needsProgressBar = when (gameUiState) {
        is VisualMemoryUiState -> gameUiState.phase == VisualMemoryGame.Phase.MEMORIZING
        is SpotTheNewUiState -> gameUiState.phase == SpotTheNewGame.Phase.MEMORIZING
        is GhostGridUiState,
        is SimonSaysUiState,
        is OrbitTrackerUiState,
        is MiniChessUiState,
        is LightsOutUiState,
        is SlidingPuzzleUiState,
        is TowerOfHanoiUiState,
        is ShikakuUiState,
        is NurikabeUiState,
        is CatQueensUiState,
        is KnotUiState,
        is SoloChessUiState,
        is WordleUiState,
        -> false
        else -> true
    }

    GameScaffold(
        onBack = onBack,
        progressBar = if (needsProgressBar) {
            {
                GameProgressBar(
                    gameUiState = gameUiState,
                    timeRemaining = timeRemaining,
                    elapsedTime = elapsedTime,
                    isTimerPaused = isTimerPaused,
                    modifier = progressBarModifier,
                )
            }
        } else {
            null
        },
        fillContent = gameUiState is FlagsUiState || gameUiState is BubbleSumUiState,
    ) {
        // Force LTR for gameplay content: math expressions, digit sequences, directional
        // arrows, and asymmetric shapes carry semantic meaning that breaks under RTL
        // mirroring. Bidi text rendering inside Text composables is unaffected.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            when (gameUiState) {
                is MentalCalculationUiState -> MentalCalculationContent(gameUiState, onAnswer)
                is QuickSumUiState -> QuickSumContent(gameUiState, onAnswer)
                is BubbleSumUiState -> BubbleSumContent(
                    uiState = gameUiState,
                    liveFrames = bubbleSumFrames,
                    onAnswer = onAnswer,
                    onArenaSize = onBubbleSumArenaSize,
                )
                is ChainCalculationUiState -> ChainCalculationContent(gameUiState, onAnswer, onGiveUp)
                is FractionCalculationUiState -> FractionCalculationContent(gameUiState, onAnswer, onGiveUp)
                is ColoredShapesUiState -> ColoredShapesContent(gameUiState, onAnswer)
                is SherlockCalculationUiState -> SherlockCalculationContent(gameUiState, onAnswer, onGiveUp)
                is ValueComparisonUiState -> ValueComparisonContent(gameUiState, onAnswer)
                is AnomalyPuzzleUiState -> AnomalyPuzzleContent(gameUiState, onAnswer)
                is PathFinderUiState -> PathFinderContent(gameUiState, onAnswer)
                is MiniSudokuUiState -> MiniSudokuContent(gameUiState, onAnswer)
                is LightsOutUiState -> LightsOutContent(gameUiState, onAnswer, onGiveUp)
                is SlidingPuzzleUiState -> SlidingPuzzleContent(gameUiState, onAnswer, onGiveUp)
                is TowerOfHanoiUiState -> TowerOfHanoiContent(gameUiState, onAnswer, onGiveUp)
                is ShikakuUiState -> ShikakuContent(gameUiState, onAnswer, onGiveUp)
                is NurikabeUiState -> NurikabeContent(gameUiState, onAnswer, onGiveUp)
                is CatQueensUiState -> CatQueensContent(gameUiState, onAnswer, onGiveUp)
                is KnotUiState -> KnotContent(gameUiState, onAnswer, onGiveUp)
                is SoloChessUiState -> SoloChessContent(gameUiState, onAnswer, onGiveUp)
                is SchulteTableUiState -> SchulteTableContent(gameUiState, onAnswer)
                is PatternSequenceUiState -> PatternSequenceContent(gameUiState, onAnswer)
                is VisualMemoryUiState -> VisualMemoryContent(gameUiState, onAnswer)
                is SpotTheNewUiState -> SpotTheNewContent(gameUiState, onAnswer)
                is GhostGridUiState -> GhostGridContent(gameUiState, onAnswer)
                is SimonSaysUiState -> SimonSaysContent(gameUiState, onAnswer)
                is ColorConfusionUiState -> ColorConfusionContent(gameUiState, onAnswer)
                is OrbitTrackerUiState -> OrbitTrackerContent(
                    uiState = gameUiState,
                    livePositions = orbitBallPositions,
                    onAnswer = onAnswer,
                )
                is FlashCrowdUiState -> FlashCrowdContent(gameUiState, onAnswer)
                is MiniChessUiState -> MiniChessContent(gameUiState, onAnswer)
                is FlagsUiState -> FlagsContent(gameUiState, onAnswer)
                is DigitMemoryUiState -> DigitMemoryContent(gameUiState, onAnswer)
                is NBackUiState -> NBackContent(gameUiState, onAnswer)
                is WordleUiState -> WordleContent(
                    uiState = gameUiState,
                    onAnswer = onAnswer,
                    onGiveUp = onGiveUp,
                    inSessionMode = inSessionMode,
                    onFinishedAction = onWordleFinishedAction,
                )
            }
        }
    }
}

/**
 * Preview/screenshot overload with fixed timer values. Wraps them in remembered flows so the
 * production path always isolates timer collection to the progress bar.
 */
@Composable
fun GameScreen(
    gameUiState: GameUiState,
    timeRemaining: Long,
    elapsedTime: Long = 0L,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
    onBack: () -> Unit,
    inSessionMode: Boolean = false,
    isTimerPaused: Boolean = false,
    onWordleFinishedAction: () -> Unit = {},
) {
    val timeFlow = remember { MutableStateFlow(timeRemaining) }
    val elapsedFlow = remember { MutableStateFlow(elapsedTime) }
    LaunchedEffect(timeRemaining) { timeFlow.value = timeRemaining }
    LaunchedEffect(elapsedTime) { elapsedFlow.value = elapsedTime }
    GameScreen(
        gameUiState = gameUiState,
        timeRemaining = timeFlow,
        elapsedTime = elapsedFlow,
        onAnswer = onAnswer,
        onGiveUp = onGiveUp,
        onBack = onBack,
        inSessionMode = inSessionMode,
        isTimerPaused = isTimerPaused,
        onWordleFinishedAction = onWordleFinishedAction,
    )
}

/**
 * Collects timer flows here only so the rest of [GameScreen] can skip while the bar ticks.
 */
@Composable
private fun GameProgressBar(
    gameUiState: GameUiState,
    timeRemaining: StateFlow<Long>,
    elapsedTime: StateFlow<Long>,
    isTimerPaused: Boolean,
    modifier: Modifier = Modifier,
) {
    when {
        gameUiState is VisualMemoryUiState &&
            gameUiState.phase == VisualMemoryGame.Phase.MEMORIZING -> {
            MemorizeTimeProgressBar(
                totalMillis = VisualMemoryGame.memorizeDurationMillis(gameUiState.round).toFloat(),
                isTimerPaused = isTimerPaused,
                restartKey = gameUiState.round,
                modifier = modifier,
            )
        }
        gameUiState is SpotTheNewUiState &&
            gameUiState.phase == SpotTheNewGame.Phase.MEMORIZING -> {
            MemorizeTimeProgressBar(
                totalMillis = SpotTheNewGame.MEMORIZE_MILLIS.toFloat(),
                isTimerPaused = isTimerPaused,
                modifier = modifier,
            )
        }
        gameUiState is SchulteTableUiState -> {
            val elapsed by elapsedTime.collectAsStateWithLifecycle()
            StopwatchDisplay(elapsedMillis = elapsed, modifier = modifier)
        }
        gameUiState is FlagsUiState -> {
            val remaining by timeRemaining.collectAsStateWithLifecycle()
            TimeProgressIndicator(
                progress = remaining / GameController.FLAGS_ROUND_TIME_MILLIS.toFloat(),
                modifier = modifier,
            )
        }
        else -> {
            val remaining by timeRemaining.collectAsStateWithLifecycle()
            TimeProgressIndicator(
                progress = remaining / 60_000f,
                modifier = modifier,
            )
        }
    }
}
