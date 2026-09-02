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
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.screens.GameScreen
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestResultScreen
import com.inspiredandroid.braincup.ui.screens.MainMenuScreenContent
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import kotlinx.collections.immutable.persistentListOf
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
 * Renders the App Store "6.9 inch iPhone" screenshot set (1320x2868, the only iPhone size App Store
 * Connect still asks for; Apple scales it down for smaller devices).
 */
@RunWith(Parameterized::class)
class IosStoreScreenshotTest(
    private val locale: String,
    private val appStoreLocale: String,
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun locales() = appStoreLocales()

        // iPhone 16 Pro Max: 440x956pt at @3x.
        private val IPHONE_69 = DeviceConfig(
            screenWidth = 1320,
            screenHeight = 2868,
            xdpi = 460,
            ydpi = 460,
            orientation = ScreenOrientation.PORTRAIT,
            density = Density.XXHIGH,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            softButtons = false,
        )
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = IPHONE_69,
        useDeviceResolution = true,
    )

    private lateinit var originalLocale: Locale

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale(locale))

        paparazzi.unsafeUpdateConfig(deviceConfig = IPHONE_69.copy(locale = locale))
        setResourceReaderAndroidContext(paparazzi.context)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    private fun snap(name: String, darkTheme: Boolean, content: @Composable () -> Unit) {
        paparazzi.snapStore(
            name = "iphone_${appStoreLocale.lowercase()}_$name",
            darkTheme = darkTheme,
            chrome = IosDevice.IPHONE,
            content = content,
        )
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
