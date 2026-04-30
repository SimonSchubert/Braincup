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
import com.inspiredandroid.braincup.ui.screens.GameScreen
import com.inspiredandroid.braincup.ui.screens.MainMenuScreenContent
import com.inspiredandroid.braincup.ui.screens.SessionCompleteScreen
import com.inspiredandroid.braincup.ui.screens.SessionInterstitialScreen
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
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
        unsafeUpdateConfig(theme = "android:Theme.Material.Light.NoActionBar")

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
                sessionTotalGames = 5,
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
                sessionTotalGames = 5,
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
                sessionProgressIndex = 5,
                sessionTotalGames = 5,
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
                totalGames = 5,
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
                totalGames = 5,
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
                gameIds = listOf(
                    GameType.MENTAL_CALCULATION.id,
                    GameType.COLORED_SHAPES.id,
                    GameType.PATH_FINDER.id,
                    GameType.GHOST_GRID.id,
                    GameType.FLASH_CROWD.id,
                ),
                scores = listOf(8, 5, 6, 4, 7),
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
}
