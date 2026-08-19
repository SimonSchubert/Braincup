package com.inspiredandroid.braincup.screenshots

import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.ScreenOrientation
import com.inspiredandroid.braincup.app.MissingOperatorsUiState
import com.inspiredandroid.braincup.ui.screens.GameScreen
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TmpMissingOpsTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_9A.copy(softButtons = false),
        showSystemUi = true,
        maxPercentDifference = 0.1,
    )

    @get:Rule
    val dummy = org.junit.rules.TestName()

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        setResourceReaderAndroidContext(paparazzi.context)
    }

    private fun state() = MissingOperatorsUiState(
        numbers = persistentListOf(4, 9, 3),
        targetResult = 26,
        operatorsCount = 2,
    )

    @Test
    fun portrait() {
        paparazzi.snapshot {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                BraincupTheme(colorScheme = LightColorScheme) {
                    Surface {
                        GameScreen(
                            gameUiState = state(),
                            timeRemaining = 50_000L,
                            onAnswer = {},
                            onGiveUp = {},
                            onBack = {},
                        )
                    }
                }
            }
        }
    }
}
