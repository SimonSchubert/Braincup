package com.inspiredandroid.braincup.screenshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import com.android.ide.common.rendering.api.SessionParams
import app.cash.paparazzi.Paparazzi
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.components.GameTile
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LocalAccessiblePalette
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class TileGalleryScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_9A.copy(
            softButtons = false,
            screenWidth = 5200,
            screenHeight = 6400,
            locale = System.getenv("LOCALE"),
        ),
        showSystemUi = false,
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 0.1,
    )

    private val originalLocale: Locale = Locale.getDefault()

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        System.getenv("LOCALE")?.let { Locale.setDefault(javaLocale(it)) }
        setResourceReaderAndroidContext(paparazzi.context)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun allGameTiles() = snapTiles(GameType.entries, perRow = 6)

    @Test
    fun workingSet() = snapTiles(
        System.getenv("TILES")?.split(",")?.map { GameType.valueOf(it.trim()) }
            ?: listOf(GameType.SIMON_SAYS, GameType.CAT_QUEENS, GameType.GHOST_GRID),
        perRow = 4,
    )

    private fun snapTiles(types: List<GameType>, perRow: Int) {
        paparazzi.unsafeUpdateConfig(theme = "android:Theme.Material.NoActionBar")
        paparazzi.snapshot {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalAccessiblePalette provides (System.getenv("ACCESSIBLE") == "1"),
            ) {
                BraincupTheme(colorScheme = DarkColorScheme) {
                    Column(
                        modifier = Modifier.background(Color(0xFF1C1715)).padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        types.chunked(perRow).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                row.forEach { type ->
                                    GameTile(
                                        gameType = type,
                                        highscore = 0,
                                        onPlay = {},
                                        onViewScore = {},
                                        modifier = Modifier.size((System.getenv("TILE_DP")?.toInt() ?: 160).dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
