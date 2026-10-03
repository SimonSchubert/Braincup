package com.inspiredandroid.braincup.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
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
    private var pcmLoop: PcmLoop? = null

    override fun play(data: ByteArray, loop: Boolean) {
        stop()
        // MediaPlayer.isLooping leaves an audible gap at the wrap, which knocks the music off beat.
        if (loop) {
            val wav = parsePcm16Wav(data)
            if (wav != null) {
                pcmLoop = PcmLoop(wav)
                return
            }
        }
        try {
            val source = ByteArrayMediaDataSource(data)
            val player = MediaPlayer()
            mediaPlayer = player
            player.setDataSource(source)
            player.isLooping = loop
            player.setOnErrorListener { _, _, _ ->
                stop()
                true
            }
            // One-shots (Simon pads, etc.) prepare synchronously so the tone lines up with the
            // flash/tap. Ambient loops stay async so we never block the UI thread on large files.
            if (loop) {
                player.setOnPreparedListener { it.start() }
                player.prepareAsync()
            } else {
                player.prepare()
                player.start()
            }
        } catch (_: Exception) {
            stop()
        }
    }

    override fun stop() {
        pcmLoop?.stop()
        pcmLoop = null
        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {
        }
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun pause() {
        pcmLoop?.pause()
        try {
            mediaPlayer?.pause()
        } catch (_: Exception) {
        }
    }

    override fun resume() {
        pcmLoop?.resume()
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

private class Pcm16Wav(
    val sampleRate: Int,
    val channelCount: Int,
    val bytes: ByteArray,
    val dataOffset: Int,
    val dataLength: Int,
)

private fun parsePcm16Wav(bytes: ByteArray): Pcm16Wav? {
    fun chunkId(at: Int) = String(bytes, at, 4, Charsets.US_ASCII)
    fun le16(at: Int) = (bytes[at].toInt() and 0xFF) or ((bytes[at + 1].toInt() and 0xFF) shl 8)
    fun le32(at: Int) = le16(at) or (le16(at + 2) shl 16)

    if (bytes.size < 12 || chunkId(0) != "RIFF" || chunkId(8) != "WAVE") return null
    var audioFormat = 0
    var channelCount = 0
    var sampleRate = 0
    var bitsPerSample = 0
    var position = 12
    while (position + 8 <= bytes.size) {
        val size = le32(position + 4)
        val body = position + 8
        if (size < 0) return null
        when (chunkId(position)) {
            "fmt " -> {
                if (body + 16 > bytes.size) return null
                audioFormat = le16(body)
                channelCount = le16(body + 2)
                sampleRate = le32(body + 4)
                bitsPerSample = le16(body + 14)
            }

            "data" -> {
                if (audioFormat != 1 || bitsPerSample != 16 || channelCount !in 1..2) return null
                val frameSize = 2 * channelCount
                val length = minOf(size, bytes.size - body) / frameSize * frameSize
                if (length <= 0) return null
                return Pcm16Wav(sampleRate, channelCount, bytes, body, length)
            }
        }
        position = body + size + (size and 1)
    }
    return null
}

/**
 * Streams the samples on its own thread and wraps by writing the start right after the end, so
 * the loop is sample-accurate. stop() and pause() interrupt a blocked write, which lets the thread
 * notice it was stopped and release the track itself.
 */
private class PcmLoop(private val wav: Pcm16Wav) {
    @Volatile
    private var stopped = false

    @Volatile
    private var paused = false

    @Volatile
    private var track: AudioTrack? = null

    init {
        Thread(::run, "braincup-music").apply { isDaemon = true }.start()
    }

    private fun run() {
        val channelMask =
            if (wav.channelCount == 2) AudioFormat.CHANNEL_OUT_STEREO else AudioFormat.CHANNEL_OUT_MONO
        val audioTrack = try {
            val minBufferSize =
                AudioTrack.getMinBufferSize(wav.sampleRate, channelMask, AudioFormat.ENCODING_PCM_16BIT)
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(wav.sampleRate)
                        .setChannelMask(channelMask)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .build(),
                )
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setBufferSizeInBytes(maxOf(minBufferSize, 0) * 2)
                .build()
        } catch (_: Exception) {
            return
        }
        track = audioTrack
        try {
            if (stopped) return
            if (!paused) audioTrack.play()
            val end = wav.dataOffset + wav.dataLength
            var position = wav.dataOffset
            while (!stopped) {
                val written =
                    audioTrack.write(wav.bytes, position, end - position, AudioTrack.WRITE_BLOCKING)
                if (written < 0) break
                position += written
                if (position >= end) position = wav.dataOffset
            }
        } catch (_: Exception) {
        } finally {
            track = null
            try {
                audioTrack.stop()
            } catch (_: Exception) {
            }
            audioTrack.release()
        }
    }

    fun stop() {
        stopped = true
        try {
            track?.pause()
            track?.flush()
        } catch (_: Exception) {
        }
    }

    fun pause() {
        paused = true
        try {
            track?.pause()
        } catch (_: Exception) {
        }
    }

    fun resume() {
        paused = false
        try {
            track?.play()
        } catch (_: Exception) {
        }
    }
}
