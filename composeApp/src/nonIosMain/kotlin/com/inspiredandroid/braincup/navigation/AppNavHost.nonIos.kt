package com.inspiredandroid.braincup.navigation

import androidx.compose.animation.EnterTransition
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
                forwardEnterTransition()
            } else {
                firstEnterConsumed[0] = true
                EnterTransition.None
            }
        },
        exitTransition = { forwardExitTransition() },
        popEnterTransition = { backEnterTransition() },
        popExitTransition = { backExitTransition() },
        builder = builder,
    )
}
