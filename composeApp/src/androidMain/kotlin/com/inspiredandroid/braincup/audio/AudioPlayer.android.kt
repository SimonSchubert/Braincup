package com.inspiredandroid.braincup.audio

import android.content.Context
import android.media.MediaDataSource
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberAudioPlayer(): AudioPlayer {
    val context = LocalContext.current.applicationContext
    val player = remember { AndroidAudioPlayer(context) }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
    return player
}

class AndroidAudioPlayer(private val context: Context) : AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    override fun play(data: ByteArray, loop: Boolean) {
        stop()
        try {
            val source = ByteArrayMediaDataSource(data)
            val player = MediaPlayer()
            mediaPlayer = player
            player.setDataSource(source)
            player.isLooping = loop
            player.setOnPreparedListener { it.start() }
            player.setOnErrorListener { _, _, _ ->
                stop()
                true
            }
            player.prepareAsync()
        } catch (_: Exception) {
            stop()
        }
    }

    override fun stop() {
        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {
        }
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (_: Exception) {
        }
    }

    override fun resume() {
        try {
            mediaPlayer?.start()
        } catch (_: Exception) {
        }
    }

    override fun release() = stop()
}

private class ByteArrayMediaDataSource(private val data: ByteArray) : MediaDataSource() {
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (position >= data.size) return -1
        val available = (data.size - position).toInt()
        val length = if (size < available) size else available
        System.arraycopy(data, position.toInt(), buffer, offset, length)
        return length
    }

    override fun getSize(): Long = data.size.toLong()

    override fun close() {}
}
