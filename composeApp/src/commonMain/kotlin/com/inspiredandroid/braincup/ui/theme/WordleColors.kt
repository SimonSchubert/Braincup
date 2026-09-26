package com.inspiredandroid.braincup.ui.theme

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.WordleLetterState
import com.inspiredandroid.braincup.ui.components.ChunkyCheck

/** Tile and keyboard colors shared by live play, instruction demo, and menu tile preview. */
@Composable
fun WordleLetterState.tileFace(): Color = when (this) {
    WordleLetterState.EMPTY -> MaterialTheme.colorScheme.surfaceVariant
    WordleLetterState.PENDING -> MaterialTheme.colorScheme.surfaceContainerHighest
    WordleLetterState.ABSENT -> WordleAbsent
    WordleLetterState.PRESENT -> if (LocalAccessiblePalette.current) WordlePresentAccessible else WordlePresent
    WordleLetterState.CORRECT -> if (LocalAccessiblePalette.current) WordleCorrectAccessible else WordleCorrect
}

@Composable
fun WordleLetterState.tileTextColor(): Color = when (this) {
    WordleLetterState.EMPTY, WordleLetterState.PENDING -> MaterialTheme.colorScheme.onSurface
    WordleLetterState.PRESENT -> if (LocalAccessiblePalette.current) WordlePresentAccessibleText else Color.White
    else -> Color.White
}

@Composable
fun WordleLetterState?.keyFace(): Color {
    val scored = this?.takeIf { it.isScored }
    return scored?.tileFace() ?: MaterialTheme.colorScheme.surfaceVariant
}

@Composable
fun WordleLetterState?.keyTextColor(): Color {
    val scored = this?.takeIf { it.isScored }
    return scored?.tileTextColor() ?: MaterialTheme.colorScheme.onSurface
}

private val WordleLetterState.isScored: Boolean
    get() = this == WordleLetterState.CORRECT || this == WordleLetterState.PRESENT || this == WordleLetterState.ABSENT

/**
 * With the colour-blind setting on, a letter in the right spot also carries a tick in its corner,
 * so it can be told from a letter in the wrong spot without seeing the hue at all.
 */
@Composable
fun BoxScope.WordleCorrectMark(state: WordleLetterState?, size: Dp, modifier: Modifier = Modifier) {
    if (state != WordleLetterState.CORRECT || !LocalAccessiblePalette.current) return
    ChunkyCheck(
        color = state.tileTextColor(),
        modifier = modifier.align(Alignment.BottomEnd).padding(2.dp).size(size),
    )
}
