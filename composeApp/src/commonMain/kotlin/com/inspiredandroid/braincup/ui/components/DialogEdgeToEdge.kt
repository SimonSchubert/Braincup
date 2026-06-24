package com.inspiredandroid.braincup.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

/**
 * [DialogProperties] for the full-screen prism dialogs, configured per platform so the dialog draws
 * edge-to-edge and its scrim extends behind the system bars, matching the edge-to-edge app. On
 * Android this means `decorFitsSystemWindows = false`; without it the dialog content is inset by the
 * status/navigation bars and the system fills those bars with its own (differently shaded) protection.
 */
expect fun prismDialogProperties(): DialogProperties

/**
 * Applies per-platform window tweaks for an edge-to-edge dialog. Call inside the dialog content.
 * On Android it drops the dialog window's default dim (the dialog draws its own scrim) and disables
 * system-bar contrast enforcement so the bars match the scrim. No-op elsewhere.
 */
@Composable
expect fun DialogWindowEdgeToEdgeTweaks()
