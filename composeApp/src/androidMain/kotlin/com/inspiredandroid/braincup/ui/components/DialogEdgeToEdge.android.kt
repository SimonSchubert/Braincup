package com.inspiredandroid.braincup.ui.components

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

actual fun prismDialogProperties(): DialogProperties = DialogProperties(
    dismissOnBackPress = true,
    dismissOnClickOutside = true,
    usePlatformDefaultWidth = false,
    // Draw under the system bars so the scrim covers them instead of the system drawing its own
    // (lighter) bar protection. Must be set at construction; toggling the window flag later does
    // not re-lay-out the dialog content.
    decorFitsSystemWindows = false,
)

@Composable
actual fun DialogWindowEdgeToEdgeTweaks() {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = (view.parent as? DialogWindowProvider)?.window ?: return
    SideEffect {
        // The dialog draws its own scrim, so drop the window's default dim; otherwise it compounds
        // with the scrim and, since the dim is not applied uniformly behind the bars, leaves the
        // bars a different shade than the body.
        window.setDimAmount(0f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
    }
}
