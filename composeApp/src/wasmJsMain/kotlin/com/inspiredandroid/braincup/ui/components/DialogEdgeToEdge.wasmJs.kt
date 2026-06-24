package com.inspiredandroid.braincup.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

actual fun prismDialogProperties(): DialogProperties = DialogProperties(
    dismissOnBackPress = true,
    dismissOnClickOutside = true,
    usePlatformDefaultWidth = false,
)

@Composable
actual fun DialogWindowEdgeToEdgeTweaks() {
    // No system bars in the browser.
}
