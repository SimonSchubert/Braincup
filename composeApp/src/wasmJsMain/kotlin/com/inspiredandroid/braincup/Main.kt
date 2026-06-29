package com.inspiredandroid.braincup

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.inspiredandroid.braincup.navigation.bindBraincupBrowserNavigation
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        App(
            useBuiltInSponsors = true,
            onNavHostReady = { it.bindBraincupBrowserNavigation() },
        )
    }
}
