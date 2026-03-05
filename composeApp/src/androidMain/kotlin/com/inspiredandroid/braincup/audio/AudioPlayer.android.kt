package com.inspiredandroid.braincup.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File

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
    private var tempFile: File? = null

    override fun play(data: ByteArray, loop: Boolean) {
        stop()
        try {
            val file = File.createTempFile("braincup_audio", ".wav", context.cacheDir)
            file.writeBytes(data)
            tempFile = file
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                isLooping = loop
                prepare()
                start()
            }
        } catch (_: Exception) {
        }
    }

    override fun stop() {
        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {
        }
        mediaPlayer?.release()
        mediaPlayer = null
        tempFile?.delete()
        tempFile = null
    }

    override fun release() = stop()
}
