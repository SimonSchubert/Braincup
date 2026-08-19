package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.WordleAbsent
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.BullsAndCowsContent(
    uiState: BullsAndCowsUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
    onFinishedAction: () -> Unit,
) {
    val listState = rememberLazyListState()
    val compact = LocalIsCompactHeight.current
    // Always Continue: non-session goes to the medal/XP finish screen; session advances the challenge.
    val finishedActionLabel = stringResource(Res.string.session_continue)
    val triesLabel = stringResource(Res.string.finish_tries, uiState.guesses.size.toString())

    // Scroll to the end when a new guess is added
    LaunchedEffect(uiState.guesses.size) {
        if (uiState.guesses.isNotEmpty()) {
            listState.animateScrollToItem(uiState.guesses.size - 1)
        }
    }

    if (compact) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Guesses list + active slots / result CTA
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    if (uiState.guesses.isNotEmpty()) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            itemsIndexed(uiState.guesses) { index, guess ->
                                GuessRow(guess = guess, index = index, compact = true)
                            }
                        }
                    }
                }

                Text(
                    text = triesLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp),
                )

                if (uiState.finished) {
                    ResultBanner(uiState = uiState, compact = true)
                    Spacer(Modifier.height(6.dp))
                    PrimaryActionButton(
                        onClick = onFinishedAction,
                        value = finishedActionLabel,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                } else {
                    DigitMemorySlots(
                        length = 4,
                        value = uiState.currentGuess,
                        accent = MaterialTheme.colorScheme.primary,
                        revealColor = null,
                        onRemoveAt = { index -> onAnswer(KeyboardCommand.clearAt(index)) },
                        modifier = Modifier.widthIn(max = 240.dp),
                    )
                    GiveUpButton(onGiveUp = onGiveUp)
                }
            }

            if (!uiState.finished) {
                BullsAndCowsKeyboard(
                    currentGuess = uiState.currentGuess,
                    absentDigits = uiState.absentDigits,
                    onKey = onAnswer,
                    compact = true,
                    modifier = Modifier
                        .weight(1.15f)
                        .widthIn(max = 360.dp),
                )
            }
        }
    } else {
        // fillContent layout: guesses grow into free space; slots + pad sit at the bottom.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp),
        ) {
            if (uiState.guesses.isNotEmpty()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(uiState.guesses) { index, guess ->
                        GuessRow(guess = guess, index = index, compact = false)
                    }
                }
            }
        }

        Text(
            text = triesLabel,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 4.dp),
        )

        if (uiState.finished) {
            ResultBanner(
                uiState = uiState,
                compact = false,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            PrimaryActionButton(
                onClick = onFinishedAction,
                value = finishedActionLabel,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
            )
        } else {
            Spacer(Modifier.height(8.dp))
            DigitMemorySlots(
                length = 4,
                value = uiState.currentGuess,
                accent = MaterialTheme.colorScheme.primary,
                revealColor = null,
                onRemoveAt = { index -> onAnswer(KeyboardCommand.clearAt(index)) },
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(8.dp))
            BullsAndCowsKeyboard(
                currentGuess = uiState.currentGuess,
                absentDigits = uiState.absentDigits,
                onKey = onAnswer,
                compact = false,
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
            )
            GiveUpButton(
                onGiveUp = onGiveUp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun ResultBanner(
    uiState: BullsAndCowsUiState,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(if (compact) 8.dp else 12.dp),
        color = if (uiState.won) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer,
        border = BorderStroke(
            1.dp,
            if (uiState.won) SuccessGreen else MaterialTheme.colorScheme.error,
        ),
        modifier = modifier,
    ) {
        Text(
            text = stringResource(Res.string.bulls_and_cows_secret_was, uiState.secret ?: ""),
            style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (uiState.won) SuccessGreen else MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(
                horizontal = if (compact) 12.dp else 16.dp,
                vertical = if (compact) 6.dp else 8.dp,
            ),
        )
    }
}

@Composable
private fun GuessRow(guess: BullsAndCowsGuess, index: Int, compact: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (compact) 8.dp else 12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(if (compact) 8.dp else 12.dp))
            .padding(
                horizontal = if (compact) 10.dp else 16.dp,
                vertical = if (compact) 6.dp else 10.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
        ) {
            Text(
                text = "#${index + 1}",
                style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = guess.guess,
                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall,
                fontFamily = numberFontFamily(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 12.dp)) {
            BadgeChip(
                label = stringResource(Res.string.bulls_and_cows_bulls, guess.bulls),
                color = SuccessGreen,
                compact = compact,
            )
            BadgeChip(
                label = stringResource(Res.string.bulls_and_cows_cows, guess.cows),
                color = MaterialTheme.colorScheme.primary,
                compact = compact,
            )
        }
    }
}

@Composable
private fun BadgeChip(label: String, color: Color, compact: Boolean) {
    Surface(
        shape = RoundedCornerShape(if (compact) 6.dp else 8.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
    ) {
        Text(
            text = label,
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(
                horizontal = if (compact) 6.dp else 8.dp,
                vertical = if (compact) 2.dp else 4.dp,
            ),
        )
    }
}

/**
 * Standard 3-column number pad (phone or calculator order via [LocalNumberPadAscending]).
 * No backspace — tap a filled digit slot to remove it. Full-width Enter submits when 4 digits.
 * Digits proven absent (0B+0C guesses) are greyed and disabled permanently for the round.
 */
@Composable
private fun BullsAndCowsKeyboard(
    currentGuess: String,
    absentDigits: ImmutableSet<Char>,
    onKey: (String) -> Unit,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    // Same rows as [NumberPad]: calculator 7-8-9 top, phone flips to 1-2-3 top.
    val numberRows = listOf(
        listOf("7", "8", "9"),
        listOf("4", "5", "6"),
        listOf("1", "2", "3"),
    )
    val orderedRows = if (LocalNumberPadAscending.current) {
        numberRows.asReversed()
    } else {
        numberRows
    }

    val gap = if (compact) 4.dp else 8.dp
    val keyHeight = if (compact) 34.dp else 46.dp
    val enterHeight = if (compact) 36.dp else 48.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = if (compact) 4.dp else 8.dp),
        verticalArrangement = Arrangement.spacedBy(gap),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        orderedRows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(gap),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { key ->
                    val digit = key.first()
                    val absent = digit in absentDigits
                    val usedInDraft = key in currentGuess
                    DigitKey(
                        value = key,
                        enabled = !absent && !usedInDraft && currentGuess.length < 4,
                        absent = absent,
                        onClick = { onKey(key) },
                        modifier = Modifier.weight(1f),
                        height = keyHeight,
                        compact = compact,
                    )
                }
            }
        }

        // Bottom row: empty | 0 | empty — matches [NumberPad] centering of zero.
        Row(
            horizontalArrangement = Arrangement.spacedBy(gap),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(Modifier.weight(1f))
            val zeroAbsent = '0' in absentDigits
            val zeroUsed = "0" in currentGuess
            DigitKey(
                value = "0",
                enabled = !zeroAbsent && !zeroUsed && currentGuess.length < 4,
                absent = zeroAbsent,
                onClick = { onKey("0") },
                modifier = Modifier.weight(1f),
                height = keyHeight,
                compact = compact,
            )
            Spacer(Modifier.weight(1f))
        }

        val canSubmit = currentGuess.length == 4
        PrismTile(
            face = if (canSubmit) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(enterHeight)
                .hoverHand(canSubmit),
            isClickable = canSubmit,
            onClick = { onKey(KeyboardCommand.ENTER) },
        ) {
            Text(
                text = stringResource(Res.string.wordle_enter),
                style = if (compact) {
                    MaterialTheme.typography.titleSmall
                } else {
                    MaterialTheme.typography.titleMedium
                },
                fontWeight = FontWeight.Bold,
                // Both faces are theme-derived, so the label has to track them: a hardcoded white
                // goes invisible on a light Material You primary, and a 38%-alpha onSurface only
                // reaches 2.7:1 against the disabled face on the dark schemes.
                color = if (canSubmit) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

@Composable
private fun DigitKey(
    value: String,
    enabled: Boolean,
    absent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp,
    compact: Boolean,
) {
    // No alpha on the face: PrismTile paints it straight onto the page with drawPath, so a
    // translucent face composites against the background rather than a surface and all but
    // disappears on the dark schemes. Every key is disabled at once while a full guess is staged,
    // so a muted-but-legible pad matters more than a faded one.
    val face = when {
        absent -> WordleAbsent
        enabled -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val textColor = when {
        absent -> Color.White
        enabled -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    PrismTile(
        face = face,
        modifier = modifier
            .height(height)
            .hoverHand(enabled),
        isClickable = enabled,
        onClick = onClick,
    ) {
        Text(
            text = value,
            color = textColor,
            style = if (compact) {
                MaterialTheme.typography.titleSmall
            } else {
                MaterialTheme.typography.titleMedium
            },
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
                    BullsAndCowsGuess("5678", 0, 0),
                ),
                currentGuess = "90",
                finished = false,
                won = false,
                secret = null,
                absentDigits = persistentSetOf('5', '6', '7', '8'),
            ),
            onAnswer = {},
            onGiveUp = {},
            onFinishedAction = {},
        )
    }
}
