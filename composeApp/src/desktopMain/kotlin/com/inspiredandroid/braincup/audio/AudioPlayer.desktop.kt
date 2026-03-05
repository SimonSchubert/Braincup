package com.inspiredandroid.braincup.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

@Composable
actual fun rememberAudioPlayer(): AudioPlayer {
    val player = remember { DesktopAudioPlayer() }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
    return player
}

class DesktopAudioPlayer : AudioPlayer {
    private var clip: Clip? = null

    override fun play(data: ByteArray, loop: Boolean) {
        stop()
        try {
            val stream = AudioSystem.getAudioInputStream(ByteArrayInputStream(data))
            clip = AudioSystem.getClip().apply {
                open(stream)
                if (loop) loop(Clip.LOOP_CONTINUOUSLY)
                start()
            }
        } catch (_: Exception) {
        }
    }

    override fun stop() {
        clip?.stop()
        clip?.close()
        clip = null
    }

    override fun release() = stop()
}
