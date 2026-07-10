package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.AnomalyPuzzleUiState
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
import com.inspiredandroid.braincup.app.NurikabeUiState
import com.inspiredandroid.braincup.app.OrbitTrackerUiState
import com.inspiredandroid.braincup.app.PathFinderUiState
import com.inspiredandroid.braincup.app.PatternSequenceUiState
import com.inspiredandroid.braincup.app.SchulteTableUiState
import com.inspiredandroid.braincup.app.SherlockCalculationUiState
import com.inspiredandroid.braincup.app.ShikakuUiState
import com.inspiredandroid.braincup.app.SlidingPuzzleUiState
import com.inspiredandroid.braincup.app.SoloChessUiState
import com.inspiredandroid.braincup.app.SpotTheNewUiState
import com.inspiredandroid.braincup.app.ValueComparisonUiState
import com.inspiredandroid.braincup.app.VisualMemoryUiState
import com.inspiredandroid.braincup.app.WordleUiState
import com.inspiredandroid.braincup.games.SpotTheNewGame
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.ui.components.GameScaffold
import com.inspiredandroid.braincup.ui.screens.games.AnomalyPuzzleContent
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
import com.inspiredandroid.braincup.ui.screens.games.NurikabeContent
import com.inspiredandroid.braincup.ui.screens.games.OrbitTrackerContent
import com.inspiredandroid.braincup.ui.screens.games.PathFinderContent
import com.inspiredandroid.braincup.ui.screens.games.PatternSequenceContent
import com.inspiredandroid.braincup.ui.screens.games.SchulteTableContent
import com.inspiredandroid.braincup.ui.screens.games.SherlockCalculationContent
import com.inspiredandroid.braincup.ui.screens.games.ShikakuContent
import com.inspiredandroid.braincup.ui.screens.games.SlidingPuzzleContent
import com.inspiredandroid.braincup.ui.screens.games.SoloChessContent
import com.inspiredandroid.braincup.ui.screens.games.SpotTheNewContent
import com.inspiredandroid.braincup.ui.screens.games.StopwatchDisplay
import com.inspiredandroid.braincup.ui.screens.games.TimeProgressIndicator
import com.inspiredandroid.braincup.ui.screens.games.ValueComparisonContent
import com.inspiredandroid.braincup.ui.screens.games.VisualMemoryContent

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
    val progressBar: (@Composable () -> Unit)? = when {
        gameUiState is VisualMemoryUiState &&
            gameUiState.phase == VisualMemoryGame.Phase.MEMORIZING -> {
            val round = gameUiState.round
            val bar: @Composable () -> Unit = {
                MemorizeTimeProgressBar(
                    totalMillis = VisualMemoryGame.memorizeDurationMillis(round).toFloat(),
                    isTimerPaused = isTimerPaused,
                    restartKey = round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            bar
        }
        gameUiState is SpotTheNewUiState &&
            gameUiState.phase == SpotTheNewGame.Phase.MEMORIZING -> {
            val bar: @Composable () -> Unit = {
                MemorizeTimeProgressBar(
                    totalMillis = SpotTheNewGame.MEMORIZE_MILLIS.toFloat(),
                    isTimerPaused = isTimerPaused,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            bar
        }
        gameUiState is SpotTheNewUiState -> null
        gameUiState is VisualMemoryUiState ||
            gameUiState is GhostGridUiState ||
            gameUiState is OrbitTrackerUiState ||
            gameUiState is MiniChessUiState ||
            gameUiState is LightsOutUiState ||
            gameUiState is SlidingPuzzleUiState ||
            gameUiState is ShikakuUiState ||
            gameUiState is NurikabeUiState ||
            gameUiState is CatQueensUiState ||
            gameUiState is KnotUiState ||
            gameUiState is SoloChessUiState ||
            gameUiState is WordleUiState -> null
        gameUiState is SchulteTableUiState -> {
            val bar: @Composable () -> Unit = {
                StopwatchDisplay(
                    elapsedMillis = elapsedTime,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            bar
        }
        gameUiState is FlagsUiState -> {
            val bar: @Composable () -> Unit = {
                TimeProgressIndicator(
                    progress = timeRemaining / GameController.FLAGS_ROUND_TIME_MILLIS.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            bar
        }
        else -> {
            val bar: @Composable () -> Unit = {
                TimeProgressIndicator(
                    progress = timeRemaining / 60000f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            bar
        }
    }
    GameScaffold(
        onBack = onBack,
        progressBar = progressBar,
        fillContent = gameUiState is FlagsUiState,
    ) {
        // Force LTR for gameplay content: math expressions, digit sequences, directional
        // arrows, and asymmetric shapes carry semantic meaning that breaks under RTL
        // mirroring. Bidi text rendering inside Text composables is unaffected.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            when (gameUiState) {
                is MentalCalculationUiState -> MentalCalculationContent(gameUiState, onAnswer)
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
                is ColorConfusionUiState -> ColorConfusionContent(gameUiState, onAnswer)
                is OrbitTrackerUiState -> OrbitTrackerContent(gameUiState, onAnswer)
                is FlashCrowdUiState -> FlashCrowdContent(gameUiState, onAnswer)
                is MiniChessUiState -> MiniChessContent(gameUiState, onAnswer)
                is FlagsUiState -> FlagsContent(gameUiState, onAnswer)
                is DigitMemoryUiState -> DigitMemoryContent(gameUiState, onAnswer)
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
