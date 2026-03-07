package com.inspiredandroid.braincup.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSData
import platform.Foundation.create

@Composable
actual fun rememberAudioPlayer(): AudioPlayer {
    val player = remember { IosAudioPlayer() }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
    return player
}

class IosAudioPlayer : AudioPlayer {
    private var avPlayer: AVAudioPlayer? = null

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override fun play(data: ByteArray, loop: Boolean) {
        stop()
        try {
            val nsData = data.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = data.size.toULong())
            }
            avPlayer = AVAudioPlayer(data = nsData, error = null).apply {
                numberOfLoops = if (loop) -1 else 0
                prepareToPlay()
                play()
            }
        } catch (_: Exception) {
        }
    }

    override fun stop() {
        avPlayer?.stop()
        avPlayer = null
    }

    override fun pause() {
        avPlayer?.pause()
    }

    override fun resume() {
        avPlayer?.play()
    }

    override fun release() = stop()
}
