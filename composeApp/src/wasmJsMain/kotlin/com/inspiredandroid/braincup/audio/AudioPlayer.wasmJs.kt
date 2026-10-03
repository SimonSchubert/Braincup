@file:OptIn(ExperimentalWasmJsInterop::class)

package com.inspiredandroid.braincup.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.ExperimentalWasmJsInterop
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

@JsFun("(audio) => { audio.pause(); }")
private external fun suspendAudio(audio: JsAny)

@JsFun("(url) => { URL.revokeObjectURL(url); }")
private external fun revokeUrl(url: JsString)

// A looping <audio> element leaves a gap at the wrap that knocks the music off beat; a Web Audio
// buffer source loops sample-accurately. Browsers keep a new AudioContext suspended until the
// first user gesture, so the loop is unlocked on the first pointer or key press.
@JsFun(
    """(base64) => {
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
    const ctx = new AudioContext();
    const loop = { ctx: ctx, source: null, stopped: false, paused: false };
    if (ctx.state === 'suspended') {
        const unlock = () => {
            document.removeEventListener('pointerdown', unlock, true);
            document.removeEventListener('keydown', unlock, true);
            if (!loop.stopped && !loop.paused) ctx.resume().catch(() => {});
        };
        document.addEventListener('pointerdown', unlock, true);
        document.addEventListener('keydown', unlock, true);
    }
    ctx.decodeAudioData(bytes.buffer).then((buffer) => {
        if (loop.stopped) return;
        const source = ctx.createBufferSource();
        source.buffer = buffer;
        source.loop = true;
        source.connect(ctx.destination);
        source.start();
        loop.source = source;
    }).catch(() => {});
    return loop;
}""",
)
private external fun startLoop(base64: JsString): JsAny

@JsFun("(loop) => { loop.stopped = true; loop.ctx.close().catch(() => {}); }")
private external fun stopLoop(loop: JsAny)

@JsFun("(loop) => { loop.paused = true; loop.ctx.suspend().catch(() => {}); }")
private external fun pauseLoop(loop: JsAny)

@JsFun("(loop) => { loop.paused = false; loop.ctx.resume().catch(() => {}); }")
private external fun resumeLoop(loop: JsAny)

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
    private var webAudioLoop: JsAny? = null

    @OptIn(ExperimentalEncodingApi::class)
    override fun play(data: ByteArray, loop: Boolean) {
        stop()
        try {
            val base64 = Base64.encode(data)
            if (loop) {
                webAudioLoop = startLoop(base64.toJsString())
                return
            }
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
        webAudioLoop?.let { stopLoop(it) }
        webAudioLoop = null
        audio?.let { pauseAudio(it) }
        audio = null
        blobUrl?.let { revokeUrl(it.toJsString()) }
        blobUrl = null
    }

    override fun pause() {
        webAudioLoop?.let { pauseLoop(it) }
        audio?.let { suspendAudio(it) }
    }

    override fun resume() {
        webAudioLoop?.let { resumeLoop(it) }
        audio?.let { playAudio(it) }
    }

    override fun release() = stop()
}
