package com.inspiredandroid.braincup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
actual fun rememberMainMenuSponsorsSection(): (@Composable () -> Unit)? {
    ensureSponsorsImageLoader()

    var sponsors by remember { mutableStateOf<Sponsors?>(null) }

    LaunchedEffect(Unit) {
        if (sponsors == null) {
            sponsors = fetchSponsors()
        }
    }

    return {
        SponsorsSectionContent(sponsors ?: Sponsors())
    }
}
