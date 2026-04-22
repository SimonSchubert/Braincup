package com.inspiredandroid.braincup.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

@Composable
expect fun AppNavHost(
    navController: NavHostController,
    startDestination: Any,
    builder: NavGraphBuilder.() -> Unit,
)
