package com.inspiredandroid.braincup.audio

import com.inspiredandroid.braincup.games.tools.Color

/**
 * Classic Milton Bradley Simon pad tones (green 415 Hz, red 310 Hz, yellow 252 Hz, blue 209 Hz).
 * Paths are relative to compose multiplatform resources (`Res.readBytes`).
 */
object SimonPadSounds {
    val paths: Map<Color, String> = mapOf(
        Color.GREEN to "files/simon_green.wav",
        Color.RED to "files/simon_red.wav",
        Color.YELLOW to "files/simon_yellow.wav",
        Color.BLUE to "files/simon_blue.wav",
    )
}
