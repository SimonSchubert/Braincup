package com.inspiredandroid.braincup.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator

@Composable
actual fun rememberHapticSuccess(): () -> Unit = remember {
    {
        val generator = UIImpactFeedbackGenerator()
        generator.impactOccurred()
    }
}
