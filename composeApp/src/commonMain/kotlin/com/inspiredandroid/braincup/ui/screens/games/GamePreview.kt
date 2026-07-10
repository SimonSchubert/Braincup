package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.inspiredandroid.braincup.ui.components.GameScaffold
import com.inspiredandroid.braincup.ui.theme.BraincupTheme

/**
 * Phone portrait + landscape device sizes for game content previews.
 * [showSystemUi] is off on purpose — it injects an Activity action bar that the app does not use.
 * Landscape height is under the compact-height threshold so side-by-side layouts are exercised.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
@Preview(
    name = "Phone Portrait",
    device = Devices.PHONE,
    showBackground = true,
)
@Preview(
    name = "Phone Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
    showBackground = true,
)
annotation class GameDevicePreviews

/** Wraps game content the same way [GameScreen] does so compact-height layout is realistic. */
@Composable
internal fun GamePreviewHost(
    content: @Composable ColumnScope.() -> Unit,
) {
    BraincupTheme {
        GameScaffold(onBack = {}, content = content)
    }
}
