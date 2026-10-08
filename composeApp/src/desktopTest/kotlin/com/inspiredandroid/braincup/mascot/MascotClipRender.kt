package com.inspiredandroid.braincup.mascot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.ui.components.mascot.Mascot
import com.inspiredandroid.braincup.ui.components.mascot.MascotMood
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import kotlin.test.Test

/**
 * Renders the animated mascot to a PNG sequence for review. Skipped unless MASCOT_CLIP_DIR is
 * set; MASCOT_CLIP_MOODS replaces the mood script as `frame=MOOD,...`. Frame time and
 * coroutine time advance from one counter, as the promo renderer did,
 * so the tap gesture's timeouts agree with the animation clock.
 */
class MascotClipRender {
    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun render() {
        val outDir = System.getenv("MASCOT_CLIP_DIR")?.let(::File) ?: return
        outDir.mkdirs()
        val fps = 30
        val seconds = System.getenv("MASCOT_CLIP_SECONDS")?.toInt() ?: 14
        val tapFrames = listOf(380)
        val moodAtFrame = System.getenv("MASCOT_CLIP_MOODS")
            ?.split(',')
            ?.associate { entry -> entry.substringBefore('=').toInt() to MascotMood.valueOf(entry.substringAfter('=')) }
            ?: mapOf(
                30 to MascotMood.APPROVING,
                100 to MascotMood.NEUTRAL,
                140 to MascotMood.SAD,
                200 to MascotMood.NEUTRAL,
                240 to MascotMood.DELIGHTED,
                320 to MascotMood.NEUTRAL,
            )
        var mood by mutableStateOf(MascotMood.NEUTRAL)
        val scheduler = TestCoroutineScheduler()
        val scene = ImageComposeScene(
            width = 440,
            height = 480,
            density = Density(2f),
            coroutineContext = UnconfinedTestDispatcher(scheduler),
        )
        val center = Offset(220f, 260f)
        try {
            scene.setContent {
                Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.BottomCenter) {
                    Mascot(Modifier.padding(start = 56.dp, bottom = 12.dp).fillMaxSize(0.62f), mood = mood)
                }
            }
            for (frame in 0 until fps * seconds) {
                val millis = frame * 1000L / fps
                val delta = millis - scheduler.currentTime
                if (delta > 0) scheduler.advanceTimeBy(delta)
                scheduler.runCurrent()
                moodAtFrame[frame]?.let { mood = it }
                if (frame in tapFrames) scene.tap(PointerEventType.Press, center, millis)
                if (frame - 2 in tapFrames) scene.tap(PointerEventType.Release, center, millis)
                val image = scene.render(nanoTime = millis * 1_000_000L)
                try {
                    val data = image.encodeToData(EncodedImageFormat.PNG) ?: error("encode failed")
                    try {
                        File(outDir, "frame_%05d.png".format(frame)).writeBytes(data.bytes)
                    } finally {
                        data.close()
                    }
                } finally {
                    image.close()
                }
            }
        } finally {
            scene.close()
        }
    }

    private fun ImageComposeScene.tap(type: PointerEventType, at: Offset, millis: Long) {
        sendPointerEvent(eventType = type, position = at, timeMillis = millis, type = PointerType.Touch)
    }
}
