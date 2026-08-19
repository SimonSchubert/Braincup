package com.inspiredandroid.braincup.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.Density
import com.android.resources.ScreenOrientation
import com.android.resources.ScreenRatio
import com.android.resources.ScreenSize
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

/**
 * Renders the App Store "13 inch iPad" screenshot set (2064x2752). 2048x2732 also passes validation
 * but collides with the retired 12.9 inch display type, so deliver has to guess from the file name.
 */
@RunWith(Parameterized::class)
class IosTabletStoreScreenshotTest(
    private val locale: String,
    private val appStoreLocale: String,
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun locales() = appStoreLocales()

        // iPad Pro 13": 1032x1376pt at @2x.
        private val IPAD_13 = DeviceConfig(
            screenWidth = 2064,
            screenHeight = 2752,
            xdpi = 264,
            ydpi = 264,
            orientation = ScreenOrientation.PORTRAIT,
            density = Density.XHIGH,
            ratio = ScreenRatio.NOTLONG,
            size = ScreenSize.XLARGE,
            softButtons = false,
        )
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = IPAD_13,
        useDeviceResolution = true,
    )

    private lateinit var originalLocale: Locale

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale(locale))

        paparazzi.unsafeUpdateConfig(deviceConfig = IPAD_13.copy(locale = locale))
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
        paparazzi.snapshot(name = "ipad_${appStoreLocale.lowercase()}_$name") {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                BraincupTheme(colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        IosDeviceChrome(IosDevice.IPAD) {
                            content()
                        }
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
