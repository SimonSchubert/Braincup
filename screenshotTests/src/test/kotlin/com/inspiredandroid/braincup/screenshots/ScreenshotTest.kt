package com.inspiredandroid.braincup.screenshots

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.screens.FinishScreen
import com.inspiredandroid.braincup.ui.screens.GameScreen
import com.inspiredandroid.braincup.ui.screens.MainMenuScreenContent
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
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
        content: @Composable () -> Unit,
    ) {
        unsafeUpdateConfig(theme = "android:Theme.Material.Light.NoActionBar")

        snapshot {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                BraincupTheme {
                    content()
                }
            }
        }
    }


    @Test
    fun mainMenu() {
        paparazzi.snap {
            MainMenuScreenContent(
                totalScore = 250,
                appOpenCount = 14,
                highscores = mainMenuHighscores,
                unlockedCount = 5,
            )
        }
    }

    @Test
    fun gameColorConfusion() {
        paparazzi.snap {
            GameScreen(
                game = createColorConfusionGame(),
                timeRemaining = 45_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameAnomalyPuzzle() {
        paparazzi.snap {
            GameScreen(
                game = createAnomalyPuzzleGame(),
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
                onPlayRandom = {},
                onPlayAgain = {},
                onMenu = {},
            )
        }
    }

    @Test
    fun gameMentalCalculation() {
        paparazzi.snap {
            GameScreen(
                game = createMentalCalculationGame(),
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
                game = createSherlockCalculationGame(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameChainCalculation() {
        paparazzi.snap {
            GameScreen(
                game = createChainCalculationGame(),
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
                game = createFractionCalculationGame(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameValueComparison() {
        paparazzi.snap {
            GameScreen(
                game = createValueComparisonGame(),
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
                game = createPathFinderGame(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gameGridSolver() {
        paparazzi.snap {
            GameScreen(
                game = createGridSolverGame(),
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
                game = createPatternSequenceGame(),
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
                game = createVisualMemoryGame(),
                timeRemaining = 50_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }
}
