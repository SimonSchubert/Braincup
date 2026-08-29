package com.inspiredandroid.braincup.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.material3.ColorScheme
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.api.UserStorage
import com.russhwolf.settings.MapSettings
import com.inspiredandroid.braincup.ui.screens.FinishScreen
import com.inspiredandroid.braincup.ui.screens.MatchstickRiddlesMenuScreenContent
import com.inspiredandroid.braincup.ui.screens.GameScreen
import com.inspiredandroid.braincup.ui.screens.InstructionsScreen
import com.inspiredandroid.braincup.ui.screens.MainMenuScreenContent
import com.inspiredandroid.braincup.ui.screens.ScoreboardScreen
import com.inspiredandroid.braincup.ui.screens.SessionCompleteScreen
import com.inspiredandroid.braincup.ui.screens.SessionInterstitialScreen
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestIntroScreen
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestPlayScreen
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestResultScreen
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestReviewScreen
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.LocalAccessiblePalette
import com.inspiredandroid.braincup.ui.theme.OledColorScheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_9A.copy(softButtons = false),
        showSystemUi = true,
        maxPercentDifference = 0.1,
    )

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        setResourceReaderAndroidContext(paparazzi.context)
    }

    /**
     * [colorScheme] overrides the light/dark pair for schemes that have no [darkTheme] shorthand
     * (OLED); [accessiblePalette] turns on the color-blind palette the way App.kt does, so the
     * combinations that only misbehave together can be captured.
     */
    fun Paparazzi.snap(
        darkTheme: Boolean = false,
        colorScheme: ColorScheme? = null,
        accessiblePalette: Boolean = false,
        content: @Composable () -> Unit,
    ) {
        val resolvedScheme = colorScheme ?: if (darkTheme) DarkColorScheme else LightColorScheme
        unsafeUpdateConfig(
            theme = if (darkTheme || colorScheme != null) {
                "android:Theme.Material.NoActionBar"
            } else {
                "android:Theme.Material.Light.NoActionBar"
            },
        )

        snapshot {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalAccessiblePalette provides accessiblePalette,
            ) {
                BraincupTheme(colorScheme = resolvedScheme) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        content()
                    }
                }
            }
        }
    }


    @Test
    fun mainMenu() {
        paparazzi.snap(darkTheme = true) {
            MainMenuScreenContent(
                totalXp = 250,
                sessionStreak = 14,
                sessionProgressIndex = 0,
                sessionTotalGames = 4,
                sessionCompletedToday = false,
                highscores = mainMenuHighscores,
                unlockedCount = 5,
            )
        }
    }

    @Test
    fun mainMenuSessionInProgress() {
        paparazzi.snap {
            MainMenuScreenContent(
                totalXp = 250,
                sessionStreak = 14,
                sessionProgressIndex = 2,
                sessionTotalGames = 4,
                sessionCompletedToday = false,
                highscores = mainMenuHighscores,
                unlockedCount = 5,
            )
        }
    }

    @Test
    fun mainMenuSessionCompleted() {
        paparazzi.snap {
            MainMenuScreenContent(
                totalXp = 250,
                sessionStreak = 15,
                sessionProgressIndex = 4,
                sessionTotalGames = 4,
                sessionCompletedToday = true,
                highscores = mainMenuHighscores,
                unlockedCount = 5,
            )
        }
    }

    @Test
    fun sessionInterstitialFirst() {
        paparazzi.snap {
            SessionInterstitialScreen(
                nextGame = GameType.MENTAL_CALCULATION,
                nextGameIndex = 0,
                totalGames = 4,
                runningTotal = 0,
                onContinue = {},
                onExit = {},
            )
        }
    }

    @Test
    fun sessionInterstitialMid() {
        paparazzi.snap {
            SessionInterstitialScreen(
                nextGame = GameType.GHOST_GRID,
                nextGameIndex = 2,
                totalGames = 4,
                runningTotal = 17,
                onContinue = {},
                onExit = {},
            )
        }
    }

    @Test
    fun sessionCompleteStreakIncreased() {
        paparazzi.snap {
            SessionCompleteScreen(
                gameIds = persistentListOf(
                    GameType.MENTAL_CALCULATION.id,
                    GameType.PATH_FINDER.id,
                    GameType.GHOST_GRID.id,
                    GameType.FLASH_CROWD.id,
                ),
                scores = persistentListOf(8, 6, 4, 7),
                streakBefore = 14,
                streakAfter = 15,
                xpGained = 80,
                levelChange = null,
                onDone = {},
            )
        }
    }

    @Test
    fun iqTestIntroFirstRun() {
        paparazzi.snap {
            IqTestIntroScreen(
                history = persistentListOf(),
                onStart = {},
                onBack = {},
            )
        }
    }

    @Test
    fun iqTestIntroWithHistory() {
        paparazzi.snap {
            IqTestIntroScreen(
                history = persistentListOf(
                    UserStorage.IqTestRecord(1_755_000_000_000L, 1L, rawScore = 22, durationSeconds = 640),
                    UserStorage.IqTestRecord(1_754_000_000_000L, 2L, rawScore = 18, durationSeconds = 720),
                ),
                onStart = {},
                onBack = {},
            )
        }
    }

    @Test
    fun iqTestPlay() {
        paparazzi.snap {
            IqTestPlayScreen(
                uiState = createIqTestPlayUiState(),
                timeRemainingMillis = 9 * 60_000L + 12_000L,
                onSelect = {},
                onPrevious = {},
                onNext = {},
                onFinish = {},
                onRequestQuit = {},
            )
        }
    }

    @Test
    fun iqTestPlayDark() {
        paparazzi.snap(darkTheme = true) {
            IqTestPlayScreen(
                uiState = createIqTestPlayUiState(itemIndex = 3, selectedOption = null),
                timeRemainingMillis = 14 * 60_000L,
                onSelect = {},
                onPrevious = {},
                onNext = {},
                onFinish = {},
                onRequestQuit = {},
            )
        }
    }

    /**
     * Item 28 at this seed governs size by progression while position puts every figure on the 2x2
     * sub-grid, which halves each slot. That combination once left an option and its near-miss
     * distractor under 4dp apart, and no golden covered it.
     */
    @Test
    fun iqTestPlaySizeOnSubGrid() {
        paparazzi.snap {
            IqTestPlayScreen(
                uiState = createIqTestPlayUiState(itemIndex = 28, selectedOption = null),
                timeRemainingMillis = 2 * 60_000L + 30_000L,
                onSelect = {},
                onPrevious = {},
                onNext = {},
                onFinish = {},
                onRequestQuit = {},
            )
        }
    }

    @Test
    fun iqTestResultAverage() {
        paparazzi.snap {
            IqTestResultScreen(
                uiState = createIqTestResultUiState(rawScore = 17),
                onReview = {},
                onDone = {},
            )
        }
    }

    @Test
    fun iqTestResultHigh() {
        paparazzi.snap {
            IqTestResultScreen(
                uiState = createIqTestResultUiState(
                    rawScore = 27,
                    levelChange = UserStorage.LevelChange(
                        oldLevel = 5,
                        newLevel = 6,
                        totalXpBefore = 1200,
                        totalXpAfter = 1260,
                    ),
                ),
                onReview = {},
                onDone = {},
            )
        }
    }

    @Test
    fun iqTestResultBelowRange() {
        paparazzi.snap {
            IqTestResultScreen(
                uiState = createIqTestResultUiState(rawScore = 4, isPersonalBest = false),
                onReview = {},
                onDone = {},
            )
        }
    }

    @Test
    fun iqTestResultDark() {
        paparazzi.snap(darkTheme = true) {
            IqTestResultScreen(
                uiState = createIqTestResultUiState(rawScore = 22),
                onReview = {},
                onDone = {},
            )
        }
    }

    @Test
    fun iqTestReview() {
        paparazzi.snap {
            IqTestReviewScreen(
                uiState = createIqTestReviewUiState(),
                onPrevious = {},
                onNext = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameColoredShapes() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createColoredShapesUiState(),
                timeRemaining = 45_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameAnomalyPuzzle() {
        paparazzi.snap(darkTheme = true) {
            GameScreen(
                gameUiState = createAnomalyPuzzleUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameDigitMemoryShowing() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createDigitMemoryShowingUiState(),
                timeRemaining = 52_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameDigitMemorySolving() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createDigitMemorySolvingUiState(),
                timeRemaining = 48_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameDigitMemoryRecall() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createDigitMemoryRecallUiState(),
                timeRemaining = 44_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun finishNewHighscore() {
        paparazzi.snap {
            FinishScreen(
                gameType = GameType.MENTAL_CALCULATION,
                score = 12,
                isNewHighscore = true,
                answeredAllCorrect = true,
                highscore = 12,
                xpGained = 12,
                totalXpAfter = 50,
                onPlayRandom = {},
                onPlayAgain = {},
                onMenu = {},
            )
        }
    }

    @Test
    fun finishNoHighscore() {
        paparazzi.snap {
            FinishScreen(
                gameType = GameType.MENTAL_CALCULATION,
                score = 5,
                isNewHighscore = false,
                answeredAllCorrect = false,
                highscore = 12,
                xpGained = 5,
                totalXpAfter = 205,
                onPlayRandom = {},
                onPlayAgain = {},
                onMenu = {},
            )
        }
    }

    @Test
    fun gameMentalCalculation() {
        paparazzi.snap(darkTheme = true) {
            GameScreen(
                gameUiState = createMentalCalculationUiState(),
                timeRemaining = 55_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameBubbleSum() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createBubbleSumUiState(),
                timeRemaining = 55_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameQuickSum() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createQuickSumUiState(),
                timeRemaining = 55_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameNBack() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createNBackUiState(),
                timeRemaining = 55_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameSherlockCalculation() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createSherlockCalculationUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameChainCalculation() {
        paparazzi.snap(darkTheme = true) {
            GameScreen(
                gameUiState = createChainCalculationUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameFractionCalculation() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createFractionCalculationUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameValueComparison() {
        paparazzi.snap(darkTheme = true) {
            GameScreen(
                gameUiState = createValueComparisonUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gamePathFinder() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createPathFinderUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameMiniSudoku() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createMiniSudokuUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameLightsOut() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createLightsOutUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameSlidingPuzzle() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createSlidingPuzzleUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameShikaku() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createShikakuUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameNurikabe() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createNurikabeUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameCatQueens() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createCatQueensUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameKnot() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createKnotUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameSoloChess() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createSoloChessUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameSchulteTable() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createSchulteTableUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gamePatternSequence() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createPatternSequenceUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameColorConfusion() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createColorConfusionUiState(),
                timeRemaining = 45_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameTrio() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createTrioUiState(),
                timeRemaining = 45_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameMentalFlex() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createMentalFlexUiState(),
                timeRemaining = 45_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    // The high-score card pins its own face and ink. Material You can resolve `primaryContainer` to
    // a pale grey, and the card's text used to inherit the ambient near-white: white on pale grey.
    // Snapshotting it in both schemes is what keeps that from coming back a third time.
    @Test
    fun scoreboard() {
        paparazzi.snap {
            ScoreboardScreen(
                gameType = GameType.MENTAL_ROTATIONS,
                storage = scoreboardStorage(),
                onBack = {},
            )
        }
    }

    @Test
    fun scoreboardOled() {
        paparazzi.snap(colorScheme = OledColorScheme) {
            ScoreboardScreen(
                gameType = GameType.MENTAL_ROTATIONS,
                storage = scoreboardStorage(),
                onBack = {},
            )
        }
    }

    @Test
    fun gameMentalRotations() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createMentalRotationsUiState(),
                timeRemaining = 45_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameGhostGrid() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createGhostGridUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameGhostGridGameOver() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createGhostGridGameOverUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameSimonSays() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createSimonSaysUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameSimonSaysGameOver() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createSimonSaysGameOverUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameVisualMemory() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createVisualMemoryUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameFlashCrowd() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createFlashCrowdUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameVisualMemoryGameOver() {
        paparazzi.snap {
            GameScreen(
                gameUiState = createVisualMemoryGameOverUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gamePatternSequenceOledColorblind() {
        paparazzi.snap(colorScheme = OledColorScheme, accessiblePalette = true) {
            GameScreen(
                gameUiState = createPatternSequenceUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameVisualMemoryOledColorblind() {
        paparazzi.snap(colorScheme = OledColorScheme, accessiblePalette = true) {
            GameScreen(
                gameUiState = createVisualMemoryUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    /** The one state that puts both achromatic slots -- ROSA and GREY_LIGHT -- in the same grid. */
    @Test
    fun gameVisualMemoryGameOverOledColorblind() {
        paparazzi.snap(colorScheme = OledColorScheme, accessiblePalette = true) {
            GameScreen(
                gameUiState = createVisualMemoryGameOverUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameAnomalyPuzzleOledColorblind() {
        paparazzi.snap(colorScheme = OledColorScheme, accessiblePalette = true) {
            GameScreen(
                gameUiState = createAnomalyPuzzleUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameSimonSaysOled() {
        paparazzi.snap(colorScheme = OledColorScheme) {
            GameScreen(
                gameUiState = createSimonSaysUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameLightsOutOled() {
        paparazzi.snap(colorScheme = OledColorScheme) {
            GameScreen(
                gameUiState = createLightsOutUiState(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameTrioOled() {
        paparazzi.snap(colorScheme = OledColorScheme) {
            GameScreen(
                gameUiState = createTrioUiState(),
                timeRemaining = 45_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    // The animated tutorials have no other coverage at all, so this pins at least one of them:
    // a demo that throws on composition (a missing CompositionLocal, a bad resource id) shows up
    // here rather than the first time someone opens the instructions on a device.
    @Test
    fun instructionsMentalFlex() {
        paparazzi.snap {
            InstructionsScreen(
                gameType = GameType.MENTAL_FLEX,
                storage = UserStorage(MapSettings()),
                onStart = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameMentalFlexOled() {
        paparazzi.snap(colorScheme = OledColorScheme) {
            GameScreen(
                gameUiState = createMentalFlexUiState(),
                timeRemaining = 45_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameMentalRotationsOled() {
        paparazzi.snap(colorScheme = OledColorScheme) {
            GameScreen(
                gameUiState = createMentalRotationsUiState(),
                timeRemaining = 45_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun matchstickRiddlesMenu() {
        paparazzi.snap {
            MatchstickRiddlesMenuScreenContent(
                solved = persistentSetOf("one_plus_one", "nine_minus_four", "nine_minus_three"),
                onRiddleSelected = {},
                onBack = {},
            )
        }
    }
}
