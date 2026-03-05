package com.inspiredandroid.braincup.audio

import androidx.compose.runtime.Composable

interface AudioPlayer {
    fun play(data: ByteArray, loop: Boolean = true)
    fun stop()
    fun release()
}

@Composable
expect fun rememberAudioPlayer(): AudioPlayer
