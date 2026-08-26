package com.inspiredandroid.braincup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Cell face colors shared by gameplay, instruction demo, and menu tile preview. */
object SpotTheNewColors {
    @Composable
    fun normalFace(): Color = MaterialTheme.colorScheme.surfaceContainer

    // Not @Composable: a fixed palette colour, unlike the two theme-derived faces around it.
    fun highlightFace(): Color = PrimaryContainer

    @Composable
    fun wrongFace(): Color = MaterialTheme.colorScheme.error
}
