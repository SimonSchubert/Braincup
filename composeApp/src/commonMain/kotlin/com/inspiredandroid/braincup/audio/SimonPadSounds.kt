package com.inspiredandroid.braincup.audio

import com.inspiredandroid.braincup.games.tools.GameColor

/**
 * Classic Milton Bradley Simon pad tones (green 415 Hz, red 310 Hz, yellow 252 Hz, blue 209 Hz).
 * Paths are relative to compose multiplatform resources (`Res.readBytes`).
 */
object SimonPadSounds {
    val paths: Map<GameColor, String> = mapOf(
        GameColor.GREEN to "files/simon_green.wav",
        GameColor.RED to "files/simon_red.wav",
        GameColor.YELLOW to "files/simon_yellow.wav",
        GameColor.BLUE to "files/simon_blue.wav",
    )
}
