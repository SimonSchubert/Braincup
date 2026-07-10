package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.inspiredandroid.braincup.ui.components.GameScaffold
import com.inspiredandroid.braincup.ui.theme.BraincupTheme

/**
 * Phone portrait + landscape device sizes.
 * [showSystemUi] is off on purpose — it injects an Activity action bar the app does not use.
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
annotation class DevicePreviews

/** Wraps game content the same way [com.inspiredandroid.braincup.ui.screens.GameScreen] does. */
@Composable
internal fun GamePreviewHost(
    content: @Composable ColumnScope.() -> Unit,
) {
    BraincupTheme {
        GameScaffold(onBack = {}, content = content)
    }
}

/** Theme wrapper for screens that already include their own scaffold. */
@Composable
internal fun ScreenPreviewHost(content: @Composable () -> Unit) {
    BraincupTheme(content = content)
}
