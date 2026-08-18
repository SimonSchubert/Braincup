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
import com.android.resources.Navigation
import com.android.resources.ScreenOrientation
import com.android.resources.ScreenRatio
import com.android.resources.ScreenSize
import com.inspiredandroid.braincup.ui.screens.GameScreen
import com.inspiredandroid.braincup.ui.screens.MainMenuScreenContent
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Renders the app at desktop-window proportions (1600x1144 px at 2x = 800x572 dp) so
 * `scripts/render_device_frames.py` can drop the result into a macOS window or a browser
 * window and produce the `media/screen_mac_*.png` / `media/screen_web_*.png` set.
 *
 * Deliberately not the tablet snapshots: those are 2560 px wide, so scaled into a window frame
 * the UI would read as a tiny high-DPI screenshot instead of a desktop app.
 */
class DesktopFrameScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DESKTOP_WINDOW,
        showSystemUi = false,
        useDeviceResolution = true,
    )

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        setResourceReaderAndroidContext(paparazzi.context)
    }

    private fun snap(
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
        paparazzi.snapshot {
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
        snap(darkTheme = true) {
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
    fun gameSherlockCalculation() {
        snap(darkTheme = false) {
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
        snap(darkTheme = true) {
            GameScreen(
                gameUiState = createPathFinderUiState(),
                timeRemaining = 30_000L,
                onAnswer = {},
                onGiveUp = {},
                onBack = {},
            )
        }
    }

    companion object {
        private val DESKTOP_WINDOW = DeviceConfig(
            screenHeight = 1600,
            screenWidth = 1144,
            xdpi = 276,
            ydpi = 276,
            orientation = ScreenOrientation.LANDSCAPE,
            density = Density.XHIGH,
            locale = "en",
            ratio = ScreenRatio.NOTLONG,
            size = ScreenSize.LARGE,
            softButtons = false,
            navigation = Navigation.NONAV,
        )
    }
}
