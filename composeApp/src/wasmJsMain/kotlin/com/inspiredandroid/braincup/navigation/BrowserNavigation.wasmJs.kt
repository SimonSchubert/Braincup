package com.inspiredandroid.braincup.navigation

import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.NavController
import androidx.navigation.bindToBrowserNavigation
import kotlinx.browser.window

@OptIn(ExperimentalBrowserHistoryApi::class)
suspend fun NavController.bindBraincupBrowserNavigation() {
    val basePath = detectWebBasePath(window.location.pathname)
    val suffix = window.location.pathname
        .removePrefix(basePath)
        .trim('/')

    if (suffix.isNotEmpty()) {
        pathSuffixToNavRoute(suffix)?.let { navigate(it) }
    }

    window.history.replaceState(null, "", "$basePath/")

    bindToBrowserNavigation { entry -> entry.toUrlPathSuffix() }
}