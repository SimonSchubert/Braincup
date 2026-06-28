package com.inspiredandroid.braincup

import androidx.compose.runtime.Composable

/**
 * Returns a sponsors section composable with fetch state hoisted above the main-menu lazy grid,
 * so scrolling off-screen does not discard loaded sponsors.
 */
@Composable
expect fun rememberMainMenuSponsorsSection(): (@Composable () -> Unit)?
