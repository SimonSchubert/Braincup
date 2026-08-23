package com.inspiredandroid.braincup.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.inspiredandroid.braincup.ui.screens.GameScreen
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestResultScreen
import com.inspiredandroid.braincup.ui.screens.MainMenuScreenContent
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Locale

@RunWith(Parameterized::class)
class TabletStoreScreenshotTest(
    private val locale: String,
    private val playStoreLocale: String,
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun locales() = playStoreLocales()
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_TABLET.copy(softButtons = false),
        showSystemUi = true,
        useDeviceResolution = true,
    )

    private lateinit var originalLocale: Locale

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(javaLocale(locale))

        paparazzi.unsafeUpdateConfig(
            deviceConfig = DeviceConfig.PIXEL_TABLET.copy(
                softButtons = false,
                locale = locale,
            ),
        )
        setResourceReaderAndroidContext(paparazzi.context)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    private fun snap(
        name: String,
        darkTheme: Boolean,
        content: @Composable () -> Unit,
    ) {
        paparazzi.unsafeUpdateConfig(
            theme = if (darkTheme) {
                "android:Theme.Material.NoActionBar"
            } else {
                "android:Theme.Material.Light.NoActionBar"
            },
        )
        paparazzi.snapshot(name = "tablet_${playStoreLocale}_$name") {
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
        snap("01", darkTheme = true) {
            MainMenuScreenContent(
                totalXp = 250,
                sessionStreak = 14,
                sessionProgressIndex = 0,
                sessionTotalGames = 5,
                sessionCompletedToday = false,
                highscores = mainMenuHighscores,
                unlockedCount = 5,
                showDailyChallenge = false,
            )
        }
    }

    @Test
    fun iqTestResult() {
        snap("02", darkTheme = true) {
            IqTestResultScreen(
                uiState = createIqTestResultUiState(
                    rawScore = 27,
                    durationSeconds = 14 * 60 + 46,
                    tierCorrect = listOf(3, 4, 4, 5, 5, 3, 3),
                ),
                onReview = {},
                onDone = {},
            )
        }
    }

    @Test
    fun gameAnomalyPuzzle() {
        snap("03", darkTheme = true) {
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
    fun gameSherlockCalculation() {
        snap("04", darkTheme = false) {
            GameScreen(
                gameUiState = createSherlockCalculationUiState(),
                timeRemaining = 40_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gamePathFinder() {
        snap("05", darkTheme = true) {
            GameScreen(
                gameUiState = createPathFinderUiState(),
                timeRemaining = 30_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    @Test
    fun gamePatternSequence() {
        snap("06", darkTheme = false) {
            GameScreen(
                gameUiState = createPatternSequenceUiState(),
                timeRemaining = 20_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }
}
