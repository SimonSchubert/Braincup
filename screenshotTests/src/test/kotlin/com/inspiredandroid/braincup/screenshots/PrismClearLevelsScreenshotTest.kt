package com.inspiredandroid.braincup.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.inspiredandroid.braincup.games.PrismClearGame
import com.inspiredandroid.braincup.games.PrismClearLevels
import com.inspiredandroid.braincup.ui.components.PrismClearLevelCard
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * One snapshot per Prism Clear catalog level for human review.
 * After recording, run `:screenshotTests:generatePrismClearLevelGallery` for a single HTML page.
 */
@RunWith(Parameterized::class)
class PrismClearLevelsScreenshotTest(
    private val levelId: Int,
) {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_9A.copy(
            softButtons = false,
            screenHeight = 1200,
        ),
        showSystemUi = false,
        maxPercentDifference = 0.1,
    )

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        setResourceReaderAndroidContext(paparazzi.context)
    }

    @Test
    fun levelBoard() {
        val ui = PrismClearGame.uiStateForLevel(levelId)
        paparazzi.snapshot(name = "prismClear_level_${levelId.toString().padStart(2, '0')}") {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                BraincupTheme(colorScheme = LightColorScheme) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        PrismClearLevelCard(
                            uiState = ui,
                            cellSize = if (ui.rows >= 7) 26.dp else 32.dp,
                        )
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "L{0}")
        fun levels(): List<Array<Any>> =
            (1..PrismClearLevels.COUNT).map { arrayOf(it) }
    }
}
