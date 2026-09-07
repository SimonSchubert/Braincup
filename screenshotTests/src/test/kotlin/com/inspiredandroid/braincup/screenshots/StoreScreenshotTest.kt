package com.inspiredandroid.braincup.screenshots

import androidx.compose.runtime.Composable
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
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
        fun locales() = playStoreLocales()
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
        Locale.setDefault(javaLocale(locale))

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

    private fun snap(name: String, darkTheme: Boolean, content: @Composable () -> Unit) {
        paparazzi.snapStore("store_${playStoreLocale}_$name", darkTheme, content = content)
    }
    @Test
    fun mainMenu() = snap("01", darkTheme = true) { StoreShots.MainMenu(StoreShots.PhoneLineup) }

    @Test
    fun iqTestResult() = snap("02", darkTheme = true) { StoreShots.IqTestResult() }

    @Test
    fun gameAnomalyPuzzle() = snap("03", darkTheme = true) { StoreShots.AnomalyPuzzle() }

    @Test
    fun gameSherlockCalculation() = snap("04", darkTheme = false) { StoreShots.SherlockCalculation() }

    @Test
    fun gamePathFinder() = snap("05", darkTheme = true) { StoreShots.PathFinder() }

    @Test
    fun gamePatternSequence() = snap("06", darkTheme = false) { StoreShots.PatternSequence() }
}
