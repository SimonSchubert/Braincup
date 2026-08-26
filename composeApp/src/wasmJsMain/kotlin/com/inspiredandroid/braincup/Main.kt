package com.inspiredandroid.braincup

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.inspiredandroid.braincup.navigation.bindBraincupBrowserNavigation
import com.inspiredandroid.braincup.navigation.detectWebBasePath
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.resources.configureWebResources

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Compose fetches resources with the relative path "./composeResources/...", which the browser
    // resolves against the current document URL. Browser navigation rewrites that URL to nested
    // routes like /Braincup/matchstick/42, so a relative path would resolve to
    // /Braincup/matchstick/composeResources/... and 404. Pin resources to an absolute path instead.
    val basePath = detectWebBasePath(window.location.pathname)
    configureWebResources {
        resourcePathMapping { path -> "$basePath/$path" }
    }

    // ComposeViewport clears its container on composition creation, which is what takes the static
    // loading splash in index.html off the screen. Nothing else should be parked in <body>.
    ComposeViewport(document.body!!) {
        App(
            useBuiltInSponsors = true,
            onNavHostReady = { it.bindBraincupBrowserNavigation() },
        )
    }
}
