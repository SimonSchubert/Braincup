package com.inspiredandroid.braincup.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
actual fun AppNavHost(
    navController: NavHostController,
    startDestination: Any,
    builder: NavGraphBuilder.() -> Unit,
) {
    val firstEnterConsumed = remember { booleanArrayOf(false) }
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            if (firstEnterConsumed[0]) {
                fadeIn(animationSpec = tween(700))
            } else {
                firstEnterConsumed[0] = true
                EnterTransition.None
            }
        },
        exitTransition = { fadeOut(animationSpec = tween(700)) },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
        builder = builder,
    )
}
