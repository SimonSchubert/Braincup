package com.inspiredandroid.braincup

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    System.setProperty("apple.awt.application.appearance", "system")
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Braincup",
        ) {
            App()
        }
    }
}
