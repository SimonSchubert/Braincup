package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_play_again
import braincup.composeapp.generated.resources.session_continue
import braincup.composeapp.generated.resources.wordle_answer_was
import braincup.composeapp.generated.resources.wordle_not_enough_letters
import braincup.composeapp.generated.resources.wordle_not_in_word_list
import com.inspiredandroid.braincup.app.GameController
import com.inspiredandroid.braincup.app.WordleLetter
import com.inspiredandroid.braincup.app.WordleLetterState
import com.inspiredandroid.braincup.app.WordleUiState
import com.inspiredandroid.braincup.ui.components.GiveUpButton
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.theme.WordleAbsent
import com.inspiredandroid.braincup.ui.theme.WordleCorrect
import com.inspiredandroid.braincup.ui.theme.WordlePresent
import org.jetbrains.compose.resources.stringResource

private val TileSpacing = 6.dp
private val KeySpacing = 4.dp
private val MaxTileSize = 52.dp
private val KeyHeight = 46.dp

@Composable
internal fun ColumnScope.WordleContent(
    uiState: WordleUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
    inSessionMode: Boolean,
    onFinishedAction: () -> Unit,
) {
    Spacer(Modifier.height(8.dp))
    WordleBoard(
        uiState = uiState,
        onTileClear = { index -> onAnswer(GameController.wordleClearAt(index)) },
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(8.dp))
    WordleStatusLine(
        uiState = uiState,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    if (!uiState.finished) {
        Spacer(Modifier.height(8.dp))
        WordleKeyboard(uiState = uiState, onKey = onAnswer)
        Spacer(Modifier.height(12.dp))
        GiveUpButton(
            onGiveUp = onGiveUp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    } else {
        Spacer(Modifier.height(16.dp))
        PrimaryActionButton(
            onClick = onFinishedAction,
            value = stringResource(
                if (inSessionMode) Res.string.session_continue else Res.string.button_play_again,
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun WordleBoard(
    uiState: WordleUiState,
    onTileClear: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        val n = uiState.wordLength
        val tile = ((maxWidth - TileSpacing * (n - 1)) / n).coerceAtMost(MaxTileSize)
        val currentRowIndex = uiState.rows.indexOfFirst { row ->
            row.any { it.state == WordleLetterState.PENDING }
        }
        Column(verticalArrangement = Arrangement.spacedBy(TileSpacing)) {
            uiState.rows.forEachIndexed { rowIndex, row ->
                Row(horizontalArrangement = Arrangement.spacedBy(TileSpacing)) {
                    row.forEachIndexed { colIndex, letter ->
                        val isCurrentRow = rowIndex == currentRowIndex
                        val canClear = isCurrentRow &&
                            letter.state == WordleLetterState.PENDING &&
                            letter.char != ' '
                        WordleTile(
                            letter = letter,
                            size = tile,
                            onClear = if (canClear) {
                                { onTileClear(colIndex) }
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WordleTile(
    letter: WordleLetter,
    size: Dp,
    onClear: (() -> Unit)? = null,
) {
    val content: @Composable () -> Unit = {
        if (letter.char != ' ') {
            Text(
                text = letter.char.toString(),
                color = letter.state.tileTextColor(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
    if (onClear != null) {
        PrismTile(
            face = letter.state.tileFace(),
            modifier = Modifier.size(size),
            onClick = onClear,
        ) {
            content()
        }
    } else {
        PrismCard(
            face = letter.state.tileFace(),
            modifier = Modifier.size(size),
        ) {
            content()
        }
    }
}

@Composable
private fun WordleStatusLine(uiState: WordleUiState, modifier: Modifier = Modifier) {
    val text = when {
        uiState.answer != null -> stringResource(Res.string.wordle_answer_was, uiState.answer)
        uiState.notInWordList -> stringResource(Res.string.wordle_not_in_word_list)
        uiState.notEnoughLetters -> stringResource(Res.string.wordle_not_enough_letters)
        else -> ""
    }
    // Reserve the line height so the board/keyboard don't jump when the message appears.
    Box(modifier = modifier.height(24.dp), contentAlignment = Alignment.Center) {
        if (text.isNotEmpty()) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ColumnScope.WordleKeyboard(uiState: WordleUiState, onKey: (String) -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
    ) {
        val maxKeys = uiState.keyboardRows.maxOf { it.length }
        val keyWidth = (maxWidth - KeySpacing * (maxKeys - 1)) / maxKeys
        Column(verticalArrangement = Arrangement.spacedBy(KeySpacing)) {
            uiState.keyboardRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(KeySpacing, Alignment.CenterHorizontally),
                ) {
                    row.forEach { c ->
                        LetterKey(
                            char = c,
                            state = uiState.keyStates[c],
                            width = keyWidth,
                            onClick = { onKey(c.toString()) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LetterKey(
    char: Char,
    state: WordleLetterState?,
    width: Dp,
    onClick: () -> Unit,
) {
    PrismTile(
        face = state.keyFace(),
        modifier = Modifier
            .width(width)
            .height(KeyHeight),
        onClick = onClick,
    ) {
        Text(
            text = char.toString(),
            color = state.keyTextColor(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

// --- Color mapping -----------------------------------------------------------------------------

@Composable
private fun WordleLetterState.tileFace(): Color = when (this) {
    WordleLetterState.EMPTY -> MaterialTheme.colorScheme.surfaceVariant
    WordleLetterState.PENDING -> MaterialTheme.colorScheme.surfaceContainerHighest
    WordleLetterState.ABSENT -> WordleAbsent
    WordleLetterState.PRESENT -> WordlePresent
    WordleLetterState.CORRECT -> WordleCorrect
}

@Composable
private fun WordleLetterState.tileTextColor(): Color = when (this) {
    WordleLetterState.EMPTY, WordleLetterState.PENDING -> MaterialTheme.colorScheme.onSurface
    else -> Color.White
}

@Composable
private fun WordleLetterState?.keyFace(): Color = when (this) {
    WordleLetterState.CORRECT -> WordleCorrect
    WordleLetterState.PRESENT -> WordlePresent
    WordleLetterState.ABSENT -> WordleAbsent
    else -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun WordleLetterState?.keyTextColor(): Color = when (this) {
    WordleLetterState.CORRECT, WordleLetterState.PRESENT, WordleLetterState.ABSENT -> Color.White
    else -> MaterialTheme.colorScheme.onSurface
}
