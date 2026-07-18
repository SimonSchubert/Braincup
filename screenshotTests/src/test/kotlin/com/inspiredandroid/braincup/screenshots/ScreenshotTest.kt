package com.inspiredandroid.braincup.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.screens.FinishScreen
import com.inspiredandroid.braincup.ui.screens.MatchstickRiddlesMenuScreenContent
import com.inspiredandroid.braincup.ui.screens.GameScreen
import com.inspiredandroid.braincup.ui.screens.MainMenuScreenContent
import com.inspiredandroid.braincup.ui.screens.SessionCompleteScreen
import com.inspiredandroid.braincup.ui.screens.SessionInterstitialScreen
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import kotlinx.collections.immutable.persistentListOf
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

    fun Paparazzi.snap(
        darkTheme: Boolean = false,
        content: @Composable () -> Unit,
    ) {
        unsafeUpdateConfig(
            theme = if (darkTheme) {
                "android:Theme.Material.NoActionBar"
            } else {
                "android:Theme.Material.Light.NoActionBar"
            },
        )

        snapshot {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                BraincupTheme(colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme) {
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
    fun matchstickRiddlesMenu() {
        paparazzi.snap {
            MatchstickRiddlesMenuScreenContent(
                solved = setOf("one_plus_one", "nine_minus_four", "nine_minus_three"),
                onRiddleSelected = {},
                onBack = {},
            )
        }
    }
}
