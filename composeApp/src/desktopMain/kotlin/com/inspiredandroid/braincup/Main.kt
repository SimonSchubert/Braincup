package com.inspiredandroid.braincup

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.GraphicsEnvironment

fun main() {
    System.setProperty("apple.awt.application.appearance", "system")
    application {
        // 800x600 suits a desktop, but on a phone-sized display it is larger than the screen
        // and the window manager cannot shrink it: Compose owns the window size, so sway's
        // fullscreen and even an external resize leave the UI clipped. Fall back to the
        // screen itself whenever the default would not fit (the PinePhone reports 360x720).
        val screen = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
        val size =
            if (screen.width < 800 || screen.height < 600) {
                DpSize(screen.width.dp, screen.height.dp)
            } else {
                DpSize(800.dp, 600.dp)
            }
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(size = size),
            title = "Braincup",
        ) {
            App(useBuiltInSponsors = true)
        }
    }
}
