package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val SpeakerBodyPath = listOf(
    0.00f to 0.38f,
    0.28f to 0.38f,
    0.58f to 0.08f,
    0.58f to 0.92f,
    0.28f to 0.62f,
    0.00f to 0.62f,
)

private val SmallWavePath = listOf(
    0.68f to 0.34f,
    0.80f to 0.50f,
    0.68f to 0.66f,
)

private val LargeWavePath = listOf(
    0.84f to 0.18f,
    0.98f to 0.50f,
    0.84f to 0.82f,
)

@Composable
fun PrismSpeaker(
    tint: Color,
    muted: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        PrismPolygon(
            points = SpeakerBodyPath,
            face = tint,
            modifier = Modifier.fillMaxSize(),
        )
        if (!muted) {
            PrismPolygon(
                points = SmallWavePath,
                face = tint,
                modifier = Modifier.fillMaxSize(),
            )
            PrismPolygon(
                points = LargeWavePath,
                face = tint,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
