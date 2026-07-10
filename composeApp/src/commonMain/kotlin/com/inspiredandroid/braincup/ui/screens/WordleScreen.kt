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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.inspiredandroid.braincup.ui.components.LocalIsCompactHeight
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.GamePreviewHost
import com.inspiredandroid.braincup.ui.theme.keyFace
import com.inspiredandroid.braincup.ui.theme.keyTextColor
import com.inspiredandroid.braincup.ui.theme.tileFace
import com.inspiredandroid.braincup.ui.theme.tileTextColor
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.stringResource

private val TileSpacing = 6.dp
private val CompactTileSpacing = 4.dp
private val KeySpacing = 4.dp
private val MaxTileSize = 52.dp

/** Cap so a 6-row board still leaves room for status + give-up in landscape. */
private val CompactMaxTileSize = 36.dp
private val KeyHeight = 46.dp
private val CompactKeyHeight = 34.dp

@Composable
internal fun ColumnScope.WordleContent(
    uiState: WordleUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
    inSessionMode: Boolean,
    onFinishedAction: () -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val onTileClear: (Int) -> Unit = { index -> onAnswer(GameController.wordleClearAt(index)) }
    val finishedActionLabel = stringResource(
        if (inSessionMode) Res.string.session_continue else Res.string.button_play_again,
    )

    if (compact) {
        // Side-by-side: board on the left, keyboard on the right. Stacking them vertically
        // overflows phone landscape and pushes the keyboard off-screen.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WordleBoard(
                    uiState = uiState,
                    onTileClear = onTileClear,
                    maxTileSize = CompactMaxTileSize,
                    tileSpacing = CompactTileSpacing,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
                WordleStatusLine(uiState = uiState)
                Spacer(Modifier.height(6.dp))
                if (!uiState.finished) {
                    GiveUpButton(onGiveUp = onGiveUp)
                } else {
                    PrimaryActionButton(
                        onClick = onFinishedAction,
                        value = finishedActionLabel,
                    )
                }
            }
            if (!uiState.finished) {
                WordleKeyboard(
                    uiState = uiState,
                    onKey = onAnswer,
                    keyHeight = CompactKeyHeight,
                    modifier = Modifier
                        .weight(1.15f)
                        .widthIn(max = 420.dp),
                )
            }
        }
    } else {
        Spacer(Modifier.height(8.dp))
        WordleBoard(
            uiState = uiState,
            onTileClear = onTileClear,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(8.dp))
        WordleStatusLine(
            uiState = uiState,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        if (!uiState.finished) {
            Spacer(Modifier.height(8.dp))
            WordleKeyboard(
                uiState = uiState,
                onKey = onAnswer,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(12.dp))
            GiveUpButton(
                onGiveUp = onGiveUp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        } else {
            Spacer(Modifier.height(16.dp))
            PrimaryActionButton(
                onClick = onFinishedAction,
                value = finishedActionLabel,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun WordleBoard(
    uiState: WordleUiState,
    onTileClear: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxTileSize: Dp = MaxTileSize,
    tileSpacing: Dp = TileSpacing,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        val n = uiState.wordLength
        val tile = ((maxWidth - tileSpacing * (n - 1)) / n)
            .coerceAtMost(maxTileSize)
            .coerceAtLeast(24.dp)
        val currentRowIndex = uiState.rows.indexOfFirst { row ->
            row.any { it.state == WordleLetterState.PENDING }
        }
        Column(verticalArrangement = Arrangement.spacedBy(tileSpacing)) {
            uiState.rows.forEachIndexed { rowIndex, row ->
                Row(horizontalArrangement = Arrangement.spacedBy(tileSpacing)) {
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
private fun WordleKeyboard(
    uiState: WordleUiState,
    onKey: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyHeight: Dp = KeyHeight,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
    ) {
        val maxKeys = uiState.keyboardRows.maxOf { it.length }
        val keyWidth = ((maxWidth - KeySpacing * (maxKeys - 1)) / maxKeys).coerceAtLeast(20.dp)
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
                            height = keyHeight,
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
    height: Dp,
    onClick: () -> Unit,
) {
    PrismTile(
        face = state.keyFace(),
        modifier = Modifier
            .width(width)
            .height(height),
        onClick = onClick,
    ) {
        Text(
            text = char.toString(),
            color = state.keyTextColor(),
            style = if (height < 40.dp) {
                MaterialTheme.typography.titleSmall
            } else {
                MaterialTheme.typography.titleMedium
            },
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@DevicePreviews
@Composable
private fun WordleContentPreview() {
    val emptyRow = persistentListOf(
        WordleLetter(' ', WordleLetterState.EMPTY),
        WordleLetter(' ', WordleLetterState.EMPTY),
        WordleLetter(' ', WordleLetterState.EMPTY),
        WordleLetter(' ', WordleLetterState.EMPTY),
        WordleLetter(' ', WordleLetterState.EMPTY),
    )
    val pendingRow = persistentListOf(
        WordleLetter('B', WordleLetterState.PENDING),
        WordleLetter('R', WordleLetterState.PENDING),
        WordleLetter('A', WordleLetterState.PENDING),
        WordleLetter('I', WordleLetterState.PENDING),
        WordleLetter('N', WordleLetterState.PENDING),
    )
    GamePreviewHost {
        WordleContent(
            uiState = WordleUiState(
                rows = persistentListOf(pendingRow, emptyRow, emptyRow, emptyRow, emptyRow, emptyRow),
                keyboardRows = persistentListOf("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"),
                keyStates = persistentMapOf(),
                wordLength = 5,
                solved = false,
                finished = false,
                answer = null,
                notEnoughLetters = false,
                notInWordList = false,
            ),
            onAnswer = {},
            onGiveUp = {},
            inSessionMode = false,
            onFinishedAction = {},
        )
    }
}
