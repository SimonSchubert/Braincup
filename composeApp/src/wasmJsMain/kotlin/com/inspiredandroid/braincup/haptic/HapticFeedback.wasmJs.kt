package com.inspiredandroid.braincup.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberHapticSuccess(): () -> Unit = remember { {} }
