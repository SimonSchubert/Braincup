package com.inspiredandroid.braincup.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

/**
 * Back-and-forth navigation slide pattern shared across all platforms.
 *
 * Forward navigation: the new screen slides in from the right while the previous
 * one slides out to the left. Back navigation reverses it: the previous screen
 * slides back in from the left while the current one slides out to the right.
 */

internal fun forwardEnterTransition(): EnterTransition =
    slideInHorizontally(initialOffsetX = { it })

internal fun forwardExitTransition(): ExitTransition =
    slideOutHorizontally(targetOffsetX = { -it })

internal fun backEnterTransition(): EnterTransition =
    slideInHorizontally(initialOffsetX = { -it })

internal fun backExitTransition(): ExitTransition =
    slideOutHorizontally(targetOffsetX = { it })
