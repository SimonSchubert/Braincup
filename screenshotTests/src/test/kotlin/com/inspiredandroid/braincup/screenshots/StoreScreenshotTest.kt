package com.inspiredandroid.braincup.screenshots

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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Locale

@RunWith(Parameterized::class)
class StoreScreenshotTest(
    private val locale: String,
    private val playStoreLocale: String,
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun locales() = listOf(
            arrayOf("ar", "ar"),
            arrayOf("bn", "bn-BD"),
            arrayOf("de", "de-DE"),
            arrayOf("en", "en-US"),
            arrayOf("es", "es-ES"),
            arrayOf("fr", "fr-FR"),
            arrayOf("hi", "hi-IN"),
            arrayOf("id", "id"),
            arrayOf("it", "it-IT"),
            arrayOf("ja", "ja-JP"),
            arrayOf("ko", "ko-KR"),
            arrayOf("nl", "nl-NL"),
            arrayOf("pl", "pl-PL"),
            arrayOf("pt", "pt-BR"),
            arrayOf("ru", "ru-RU"),
            arrayOf("th", "th"),
            arrayOf("tr", "tr-TR"),
            arrayOf("uk", "uk"),
            arrayOf("vi", "vi"),
            arrayOf("zh", "zh-CN"),
        )
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_9A.copy(softButtons = false),
        showSystemUi = true,
    )

    private lateinit var originalLocale: Locale

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale(locale))

        paparazzi.unsafeUpdateConfig(
            deviceConfig = DeviceConfig.PIXEL_9A.copy(
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
        content: @Composable () -> Unit,
    ) {
        paparazzi.unsafeUpdateConfig(theme = "android:Theme.Material.Light.NoActionBar")
        paparazzi.snapshot(name = "store_${playStoreLocale}_$name") {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                BraincupTheme {
                    content()
                }
            }
        }
    }

    @Test
    fun mainMenu() {
        snap("01") {
            MainMenuScreenContent(
                totalScore = 250,
                appOpenCount = 14,
                highscores = mainMenuHighscores,
                unlockedCount = 5,
            )
        }
    }

    @Test
    fun gameColoredShapes() {
        snap("02") {
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
        snap("03") {
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
        snap("04") {
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
        snap("05") {
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
        snap("06") {
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
