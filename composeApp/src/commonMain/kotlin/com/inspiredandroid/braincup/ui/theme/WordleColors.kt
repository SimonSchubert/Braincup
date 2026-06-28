package com.inspiredandroid.braincup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.inspiredandroid.braincup.app.WordleLetterState

/** Tile and keyboard colors shared by live play, instruction demo, and menu tile preview. */
@Composable
fun WordleLetterState.tileFace(): Color = when (this) {
    WordleLetterState.EMPTY -> MaterialTheme.colorScheme.surfaceVariant
    WordleLetterState.PENDING -> MaterialTheme.colorScheme.surfaceContainerHighest
    WordleLetterState.ABSENT -> WordleAbsent
    WordleLetterState.PRESENT -> WordlePresent
    WordleLetterState.CORRECT -> WordleCorrect
}

@Composable
fun WordleLetterState.tileTextColor(): Color = when (this) {
    WordleLetterState.EMPTY, WordleLetterState.PENDING -> MaterialTheme.colorScheme.onSurface
    else -> Color.White
}

@Composable
fun WordleLetterState?.keyFace(): Color = when (this) {
    WordleLetterState.CORRECT -> WordleCorrect
    WordleLetterState.PRESENT -> WordlePresent
    WordleLetterState.ABSENT -> WordleAbsent
    else -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
fun WordleLetterState?.keyTextColor(): Color = when (this) {
    WordleLetterState.CORRECT, WordleLetterState.PRESENT, WordleLetterState.ABSENT -> Color.White
    else -> MaterialTheme.colorScheme.onSurface
}
