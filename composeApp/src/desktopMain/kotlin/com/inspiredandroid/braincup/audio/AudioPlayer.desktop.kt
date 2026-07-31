package com.inspiredandroid.braincup.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import java.io.ByteArrayInputStream
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
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

/**
 * Desktop audio runs on a dedicated daemon thread. Opening a [Clip] on the AWT/Compose UI thread
 * can stall the whole window (especially on rapid one-shot restarts while ambient music holds
 * another line), which felt like the Simon board freezing on double-tap.
 */
class DesktopAudioPlayer : AudioPlayer {
    private val audioExecutor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "braincup-audio").apply { isDaemon = true }
    }

    private var loopClip: Clip? = null

    /** Cached one-shots keyed by identity of the stable resource [ByteArray]. */
    private val oneShotClips = HashMap<Int, Clip>()

    @Volatile
    private var released = false

    override fun play(data: ByteArray, loop: Boolean) {
        runOnAudioThread {
            if (released) return@runOnAudioThread
            if (loop) {
                playLoop(data)
            } else {
                playOneShot(data)
            }
        }
    }

    private fun playLoop(data: ByteArray) {
        stopLoop()
        try {
            val stream = AudioSystem.getAudioInputStream(ByteArrayInputStream(data))
            val clip = AudioSystem.getClip()
            clip.open(stream)
            clip.loop(Clip.LOOP_CONTINUOUSLY)
            clip.start()
            loopClip = clip
        } catch (_: Exception) {
            loopClip = null
        }
    }

    private fun playOneShot(data: ByteArray) {
        val key = System.identityHashCode(data)
        try {
            var clip = oneShotClips[key]
            if (clip == null) {
                val stream = AudioSystem.getAudioInputStream(ByteArrayInputStream(data))
                clip = AudioSystem.getClip()
                clip.open(stream)
                oneShotClips[key] = clip
            } else {
                // Restart without re-open: open/close on every tap is what hung the UI thread.
                if (clip.isRunning) {
                    clip.stop()
                }
                clip.framePosition = 0
            }
            clip.start()
        } catch (_: Exception) {
            oneShotClips.remove(key)?.let { broken ->
                try {
                    broken.close()
                } catch (_: Exception) {
                }
            }
        }
    }

    override fun stop() {
        runOnAudioThread {
            stopLoop()
            for (clip in oneShotClips.values) {
                try {
                    if (clip.isRunning) clip.stop()
                    clip.framePosition = 0
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun stopLoop() {
        try {
            loopClip?.stop()
        } catch (_: Exception) {
        }
        try {
            loopClip?.close()
        } catch (_: Exception) {
        }
        loopClip = null
    }

    override fun pause() {
        runOnAudioThread {
            try {
                loopClip?.stop()
            } catch (_: Exception) {
            }
        }
    }

    override fun resume() {
        runOnAudioThread {
            try {
                loopClip?.start()
            } catch (_: Exception) {
            }
        }
    }

    override fun release() {
        runOnAudioThread {
            released = true
            stopLoop()
            for (clip in oneShotClips.values) {
                try {
                    clip.stop()
                } catch (_: Exception) {
                }
                try {
                    clip.close()
                } catch (_: Exception) {
                }
            }
            oneShotClips.clear()
        }
        audioExecutor.shutdown()
    }

    private fun runOnAudioThread(block: () -> Unit) {
        if (audioExecutor.isShutdown) return
        if (Thread.currentThread().name == "braincup-audio") {
            block()
            return
        }
        try {
            audioExecutor.execute {
                try {
                    block()
                } catch (_: Exception) {
                }
            }
        } catch (_: RejectedExecutionException) {
        }
    }
}
