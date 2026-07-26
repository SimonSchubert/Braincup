package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.BullsAndCowsContent(
    uiState: BullsAndCowsUiState,
    onAnswer: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    // Scroll to the end when a new guess is added
    LaunchedEffect(uiState.guesses.size) {
        if (uiState.guesses.isNotEmpty()) {
            listState.animateScrollToItem(uiState.guesses.size - 1)
        }
    }

    Spacer(Modifier.height(8.dp))

    // Guesses List
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        if (uiState.guesses.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.game_bulls_and_cows_howto),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.guesses) { guess ->
                    GuessRow(guess = guess)
                }
            }
        }
    }

    Spacer(Modifier.height(12.dp))

    // Info Line or Goal Secret
    if (uiState.finished) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (uiState.won) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer,
                border = BorderStroke(
                    1.dp,
                    if (uiState.won) SuccessGreen else MaterialTheme.colorScheme.error,
                ),
                modifier = Modifier.padding(8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.bulls_and_cows_secret_was, uiState.secret ?: ""),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.won) SuccessGreen else MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    } else {
        // Active typing slots
        DigitMemorySlots(
            length = 4,
            value = uiState.currentGuess,
            accent = MaterialTheme.colorScheme.primary,
            revealColor = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }

    Spacer(Modifier.height(12.dp))

    // Standard control buttons or custom keypad
    if (!uiState.finished) {
        BullsAndCowsKeyboard(
            currentGuess = uiState.currentGuess,
            onKey = onAnswer,
        )
    }
}

@Composable
private fun GuessRow(guess: BullsAndCowsGuess) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = guess.guess,
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = numberFontFamily(),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BadgeChip(
                label = stringResource(Res.string.bulls_and_cows_bulls, guess.bulls),
                color = SuccessGreen,
            )
            BadgeChip(
                label = stringResource(Res.string.bulls_and_cows_cows, guess.cows),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun BadgeChip(label: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun BullsAndCowsKeyboard(
    currentGuess: String,
    onKey: (String) -> Unit,
) {
    val isPhoneStyle = LocalNumberPadAscending.current
    val row1 = if (isPhoneStyle) listOf("1", "2", "3", "4", "5") else listOf("7", "8", "9", "4", "5")
    val row2 = if (isPhoneStyle) listOf("6", "7", "8", "9", "0") else listOf("1", "2", "3", "0", "6")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth(0.95f),
        ) {
            row1.forEach { key ->
                val used = key in currentGuess
                DigitKey(
                    value = key,
                    enabled = !used && currentGuess.length < 4,
                    onClick = { onKey(key) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth(0.95f),
        ) {
            row2.forEach { key ->
                val used = key in currentGuess
                DigitKey(
                    value = key,
                    enabled = !used && currentGuess.length < 4,
                    onClick = { onKey(key) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(0.95f),
        ) {
            // Delete Key
            PrismTile(
                face = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .hoverHand(),
                onClick = { onKey(GameController.WORDLE_DELETE) },
            ) {
                Text(
                    text = stringResource(Res.string.button_backspace),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Enter Key
            val canSubmit = currentGuess.length == 4
            PrismTile(
                face = if (canSubmit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .hoverHand(canSubmit),
                isClickable = canSubmit,
                onClick = { onKey(GameController.WORDLE_ENTER) },
            ) {
                Text(
                    text = stringResource(Res.string.wordle_enter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (canSubmit) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                )
            }
        }
    }
}

@Composable
private fun DigitKey(
    value: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f),
        modifier = modifier
            .height(46.dp)
            .hoverHand(enabled),
        isClickable = enabled,
        onClick = onClick,
    ) {
        Text(
            text = value,
            color = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = numberFontFamily(),
        )
    }
}

@DevicePreviews
@Composable
private fun BullsAndCowsContentPreview() {
    GamePreviewHost {
        BullsAndCowsContent(
            uiState = BullsAndCowsUiState(
                guesses = persistentListOf(
                    BullsAndCowsGuess("1234", 1, 2),
                    BullsAndCowsGuess("5678", 0, 1),
                ),
                currentGuess = "90",
                finished = false,
                won = false,
                secret = null,
            ),
            onAnswer = {},
        )
    }
}
