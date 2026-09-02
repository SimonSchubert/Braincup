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
import com.inspiredandroid.braincup.app.GameUiState
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.api.UserStorage
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

    /**
     * One game board, rendered the way the app renders it.
     *
     * Every game shot is the same call with a different [gameUiState], so the callbacks and the
     * scaffolding live here rather than in forty copies. The snapshot file is named after the test
     * method, so these stay one method per shot.
     */
    fun snapGame(
        gameUiState: GameUiState,
        timeRemaining: Long,
        darkTheme: Boolean = false,
        colorScheme: ColorScheme? = null,
        accessiblePalette: Boolean = false,
    ) {
        paparazzi.snap(
            darkTheme = darkTheme,
            colorScheme = colorScheme,
            accessiblePalette = accessiblePalette,
        ) {
            GameScreen(
                gameUiState = gameUiState,
                timeRemaining = timeRemaining,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
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
        snapGame(createColoredShapesUiState(), 45_000L)
    }

    @Test
    fun gameAnomalyPuzzle() {
        snapGame(createAnomalyPuzzleUiState(), 50_000L, darkTheme = true)
    }

    @Test
    fun gameDigitMemoryShowing() {
        snapGame(createDigitMemoryShowingUiState(), 52_000L)
    }

    @Test
    fun gameDigitMemorySolving() {
        snapGame(createDigitMemorySolvingUiState(), 48_000L)
    }

    @Test
    fun gameDigitMemoryRecall() {
        snapGame(createDigitMemoryRecallUiState(), 44_000L)
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

    // The one piece of finish-screen UI that only one game reaches, so nothing else would catch it
    // breaking. A negative value is the interesting case: it must render as a reading, not as an
    // absent one.
    @Test
    fun finishCongruencyEffect() {
        paparazzi.snap {
            FinishScreen(
                gameType = GameType.COLOR_CONFUSION,
                score = 45,
                isNewHighscore = true,
                answeredAllCorrect = false,
                highscore = 45,
                xpGained = 45,
                totalXpAfter = 205,
                congruencyEffectMs = 184,
                onPlayRandom = {},
                onPlayAgain = {},
                onMenu = {},
            )
        }
    }

    @Test
    fun gameMentalCalculation() {
        snapGame(createMentalCalculationUiState(), 55_000L, darkTheme = true)
    }

    @Test
    fun gameBubbleSum() {
        snapGame(createBubbleSumUiState(), 55_000L)
    }

    @Test
    fun gameQuickSum() {
        snapGame(createQuickSumUiState(), 55_000L)
    }

    @Test
    fun gameNBack() {
        snapGame(createNBackUiState(), 55_000L)
    }

    @Test
    fun gameSherlockCalculation() {
        snapGame(createSherlockCalculationUiState(), 50_000L)
    }

    @Test
    fun gameChainCalculation() {
        snapGame(createChainCalculationUiState(), 50_000L, darkTheme = true)
    }

    @Test
    fun gameFractionCalculation() {
        snapGame(createFractionCalculationUiState(), 50_000L)
    }

    @Test
    fun gameValueComparison() {
        snapGame(createValueComparisonUiState(), 50_000L, darkTheme = true)
    }

    @Test
    fun gamePathFinder() {
        snapGame(createPathFinderUiState(), 50_000L)
    }

    @Test
    fun gameMiniSudoku() {
        snapGame(createMiniSudokuUiState(), 50_000L)
    }

    @Test
    fun gameLightsOut() {
        snapGame(createLightsOutUiState(), 50_000L)
    }

    @Test
    fun gameSlidingPuzzle() {
        snapGame(createSlidingPuzzleUiState(), 50_000L)
    }

    @Test
    fun gameShikaku() {
        snapGame(createShikakuUiState(), 50_000L)
    }

    @Test
    fun gameNurikabe() {
        snapGame(createNurikabeUiState(), 50_000L)
    }

    @Test
    fun gameCatQueens() {
        snapGame(createCatQueensUiState(), 50_000L)
    }

    @Test
    fun gameKnot() {
        snapGame(createKnotUiState(), 50_000L)
    }

    @Test
    fun gameSoloChess() {
        snapGame(createSoloChessUiState(), 50_000L)
    }

    @Test
    fun gameSchulteTable() {
        snapGame(createSchulteTableUiState(), 50_000L)
    }

    @Test
    fun gamePatternSequence() {
        snapGame(createPatternSequenceUiState(), 50_000L)
    }

    @Test
    fun gameColorConfusion() {
        snapGame(createColorConfusionUiState(), 45_000L)
    }

    @Test
    fun gameTrio() {
        snapGame(createTrioUiState(), 45_000L)
    }

    @Test
    fun gameRuleShift() {
        snapGame(createRuleShiftUiState(), 45_000L)
    }

    // Rule Shift is not gated by requiresColorVision: sorting only ever needs "same colour or not",
    // never a colour named. This snapshot is what backs that claim.
    @Test
    fun gameRuleShiftColorblind() {
        snapGame(createRuleShiftUiState(), 45_000L, accessiblePalette = true)
    }

    @Test
    fun gameMentalFlex() {
        snapGame(createMentalFlexUiState(), 45_000L)
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
        snapGame(createMentalRotationsUiState(), 45_000L)
    }

    @Test
    fun gameGhostGrid() {
        snapGame(createGhostGridUiState(), 50_000L)
    }

    @Test
    fun gameGhostGridGameOver() {
        snapGame(createGhostGridGameOverUiState(), 50_000L)
    }

    @Test
    fun gameSimonSays() {
        snapGame(createSimonSaysUiState(), 50_000L)
    }

    @Test
    fun gameSimonSaysGameOver() {
        snapGame(createSimonSaysGameOverUiState(), 50_000L)
    }

    @Test
    fun gameVisualMemory() {
        snapGame(createVisualMemoryUiState(), 50_000L)
    }

    @Test
    fun gameFlashCrowd() {
        snapGame(createFlashCrowdUiState(), 50_000L)
    }

    @Test
    fun gameVisualMemoryGameOver() {
        snapGame(createVisualMemoryGameOverUiState(), 50_000L)
    }

    @Test
    fun gamePatternSequenceOledColorblind() {
        snapGame(createPatternSequenceUiState(), 50_000L, colorScheme = OledColorScheme, accessiblePalette = true)
    }

    @Test
    fun gameVisualMemoryOledColorblind() {
        snapGame(createVisualMemoryUiState(), 50_000L, colorScheme = OledColorScheme, accessiblePalette = true)
    }

    /** The one state that puts both achromatic slots -- ROSA and GREY_LIGHT -- in the same grid. */
    @Test
    fun gameVisualMemoryGameOverOledColorblind() {
        snapGame(createVisualMemoryGameOverUiState(), 50_000L, colorScheme = OledColorScheme, accessiblePalette = true)
    }

    @Test
    fun gameAnomalyPuzzleOledColorblind() {
        snapGame(createAnomalyPuzzleUiState(), 50_000L, colorScheme = OledColorScheme, accessiblePalette = true)
    }

    @Test
    fun gameSimonSaysOled() {
        snapGame(createSimonSaysUiState(), 50_000L, colorScheme = OledColorScheme)
    }

    @Test
    fun gameLightsOutOled() {
        snapGame(createLightsOutUiState(), 50_000L, colorScheme = OledColorScheme)
    }

    @Test
    fun gameTrioOled() {
        snapGame(createTrioUiState(), 45_000L, colorScheme = OledColorScheme)
    }

    // The animated tutorials have no other coverage at all, so this pins at least one of them:
    // a demo that throws on composition (a missing CompositionLocal, a bad resource id) shows up
    // here rather than the first time someone opens the instructions on a device.
    @Test
    fun instructionsColorConfusion() {
        paparazzi.snap {
            InstructionsScreen(
                gameType = GameType.COLOR_CONFUSION,
                storage = screenshotStorage(),
                onStart = {},
                onBack = {},
            )
        }
    }

    @Test
    fun instructionsRuleShift() {
        paparazzi.snap {
            InstructionsScreen(
                gameType = GameType.RULE_SHIFT,
                storage = screenshotStorage(),
                onStart = {},
                onBack = {},
            )
        }
    }

    @Test
    fun instructionsMentalFlex() {
        paparazzi.snap {
            InstructionsScreen(
                gameType = GameType.MENTAL_FLEX,
                storage = screenshotStorage(),
                onStart = {},
                onBack = {},
            )
        }
    }

    // Flash Crowd carries the longest paradigm name and the longest citation of the seven, so this
    // is the snapshot that catches the research note's text wrapping; instructionsGhostGrid holds
    // the short end of the same range.
    @Test
    fun instructionsFlashCrowd() {
        paparazzi.snap {
            InstructionsScreen(
                gameType = GameType.FLASH_CROWD,
                storage = screenshotStorage(),
                onStart = {},
                onBack = {},
            )
        }
    }

    // The research note (see GameScience) in a second game's layout, so a change to the card is
    // caught in more than the one screen that happened to already have coverage.
    @Test
    fun instructionsGhostGrid() {
        paparazzi.snap {
            InstructionsScreen(
                gameType = GameType.GHOST_GRID,
                storage = screenshotStorage(),
                onStart = {},
                onBack = {},
            )
        }
    }

    // The one instructions screen that is a list rather than a demo: a trait key over four
    // annotated example rows, which is also the tallest body any game puts in that slot.
    @Test
    fun instructionsTrio() {
        paparazzi.snap {
            InstructionsScreen(
                gameType = GameType.TRIO,
                storage = screenshotStorage(),
                onStart = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameColorConfusionOled() {
        snapGame(createColorConfusionUiState(), 45_000L, colorScheme = OledColorScheme)
    }

    @Test
    fun gameRuleShiftOled() {
        snapGame(createRuleShiftUiState(), 45_000L, colorScheme = OledColorScheme)
    }

    @Test
    fun gameMentalFlexOled() {
        snapGame(createMentalFlexUiState(), 45_000L, colorScheme = OledColorScheme)
    }

    @Test
    fun gameMentalRotationsOled() {
        snapGame(createMentalRotationsUiState(), 45_000L, colorScheme = OledColorScheme)
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
