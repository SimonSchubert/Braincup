package com.inspiredandroid.braincup.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
actual fun AppNavHost(
    navController: NavHostController,
    startDestination: Any,
    builder: NavGraphBuilder.() -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        builder = builder,
    )
}
