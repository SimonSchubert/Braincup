package com.inspiredandroid.braincup.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.JsAny
import kotlin.js.JsString
import kotlin.js.toJsString

@JsFun(
    """(base64) => {
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
    const blob = new Blob([bytes], {type: 'audio/wav'});
    return URL.createObjectURL(blob);
}""",
)
private external fun createBlobUrl(base64: JsString): JsString

@JsFun("(src) => new Audio(src)")
private external fun createAudio(src: JsString): JsAny

@JsFun("(audio, loop) => { audio.loop = loop; }")
private external fun setLoop(audio: JsAny, loop: Boolean)

@JsFun("(audio) => { audio.play().catch(() => {}); }")
private external fun playAudio(audio: JsAny)

@JsFun("(audio) => { audio.pause(); audio.currentTime = 0; }")
private external fun pauseAudio(audio: JsAny)

@JsFun("(url) => { URL.revokeObjectURL(url); }")
private external fun revokeUrl(url: JsString)

@Composable
actual fun rememberAudioPlayer(): AudioPlayer {
    val player = remember { WasmAudioPlayer() }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
    return player
}

class WasmAudioPlayer : AudioPlayer {
    private var audio: JsAny? = null
    private var blobUrl: String? = null

    @OptIn(ExperimentalEncodingApi::class)
    override fun play(data: ByteArray, loop: Boolean) {
        stop()
        try {
            val base64 = Base64.encode(data)
            val url = createBlobUrl(base64.toJsString())
            blobUrl = url.toString()
            audio = createAudio(url).also { a ->
                setLoop(a, loop)
                playAudio(a)
            }
        } catch (_: Exception) {
        }
    }

    override fun stop() {
        audio?.let { pauseAudio(it) }
        audio = null
        blobUrl?.let { revokeUrl(it.toJsString()) }
        blobUrl = null
    }

    override fun release() = stop()
}
