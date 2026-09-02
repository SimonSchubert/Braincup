package com.inspiredandroid.braincup.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.Paparazzi
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.screens.GameScreen
import com.inspiredandroid.braincup.ui.screens.MainMenuScreenContent
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestResultScreen
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * The six shots both stores ask for, shared by the four device suites.
 *
 * Which screens are shown, in which order and in which theme is a listing decision, not a device
 * one, so it lives here rather than four times over. The suites differ only in the device they
 * render on, the locales they run, the filename prefix, and whether iOS chrome is drawn around
 * the frame.
 */
internal object StoreShots {

    /**
     * Hand-picked portrait lineup so the home shot shows strong demos without scrolling. The
     * tablets pass null and get the real menu, which has the room for it.
     */
    val PhoneLineup: ImmutableList<GameType> = persistentListOf(
        GameType.CAT_QUEENS,
        GameType.GHOST_GRID,
        GameType.SPOT_THE_NEW,
        GameType.MINI_CHESS,
        GameType.DIGIT_MEMORY,
        GameType.PATTERN_SEQUENCE,
    )

    @Composable
    fun MainMenu(gameTypes: ImmutableList<GameType>?) {
        MainMenuScreenContent(
            totalXp = 250,
            sessionStreak = 14,
            sessionProgressIndex = 0,
            sessionTotalGames = 5,
            sessionCompletedToday = false,
            highscores = mainMenuHighscores,
            unlockedCount = 5,
            showDailyChallenge = false,
            gameTypes = gameTypes,
        )
    }

    @Composable
    fun IqTestResult() {
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

    @Composable
    fun AnomalyPuzzle() = Game(createAnomalyPuzzleUiState(), 50_000L)

    @Composable
    fun SherlockCalculation() = Game(createSherlockCalculationUiState(), 40_000L)

    @Composable
    fun PathFinder() = Game(createPathFinderUiState(), 30_000L)

    @Composable
    fun PatternSequence() = Game(createPatternSequenceUiState(), 20_000L)

    @Composable
    private fun Game(gameUiState: com.inspiredandroid.braincup.app.GameUiState, timeRemaining: Long) {
        GameScreen(
            gameUiState = gameUiState,
            timeRemaining = timeRemaining,
            onAnswer = {},
            onGiveUp = {},
            onBack = {},
        )
    }
}

/**
 * Renders one store shot into [name], themed and wrapped the way the listing wants it.
 *
 * [chrome] draws the iOS device frame for the App Store suites; the Play suites pass null and are
 * captured bare, because Play adds its own frame.
 */
internal fun Paparazzi.snapStore(
    name: String,
    darkTheme: Boolean,
    chrome: IosDevice? = null,
    content: @Composable () -> Unit,
) {
    unsafeUpdateConfig(
        theme = if (darkTheme) {
            "android:Theme.Material.NoActionBar"
        } else {
            "android:Theme.Material.Light.NoActionBar"
        },
    )
    snapshot(name = name) {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            BraincupTheme(colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (chrome == null) {
                        content()
                    } else {
                        IosDeviceChrome(chrome) {
                            content()
                        }
                    }
                }
            }
        }
    }
}
