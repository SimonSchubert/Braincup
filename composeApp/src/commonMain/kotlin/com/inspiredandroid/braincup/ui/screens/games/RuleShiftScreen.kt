package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_rule_shift_cards_left
import braincup.composeapp.generated.resources.game_rule_shift_howto
import com.inspiredandroid.braincup.app.RuleShiftKeyCell
import com.inspiredandroid.braincup.app.RuleShiftUiState
import com.inspiredandroid.braincup.games.RuleShiftGame
import com.inspiredandroid.braincup.ui.components.*
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

/**
 * Key cards on top, the card to sort underneath, as the test lays them out. Nothing on screen
 * tracks the streak: the player is meant to be surprised when the rule moves.
 */
@Composable
internal fun ColumnScope.RuleShiftContent(
    uiState: RuleShiftUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val keySymbol = if (compact) 13.dp else 17.dp
    val stimulusSymbol = if (compact) 24.dp else 32.dp

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .widthIn(max = 84.dp * uiState.keyCards.size)
            .align(Alignment.CenterHorizontally),
    ) {
        uiState.keyCards.forEachIndexed { index, cell ->
            RuleShiftKeyTile(
                cell = cell,
                symbolSize = keySymbol,
                // The whole row stops taking taps for the feedback beat, not just the card that
                // was tapped: the round is already decided, and the game ignores the input anyway.
                isEnabled = !uiState.isAwaitingNextCard,
                onClick = { onAnswer("${index + 1}") },
                modifier = Modifier.weight(1f).aspectRatio(1f).padding(4.dp),
            )
        }
    }

    Spacer(Modifier.height(if (compact) 18.dp else 30.dp))

    // Drawn bare rather than on a tile, so it never reads as a fifth thing to tap. It follows the
    // key cards directly, the way the card to sort sits under them in the test.
    RuleShiftCardFace(
        card = uiState.stimulus,
        symbolSize = stimulusSymbol,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )

    Spacer(Modifier.height(if (compact) 16.dp else 26.dp))

    BoardInstructionLine(
        text = stringResource(Res.string.game_rule_shift_howto),
        isError = false,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 24.dp),
    )

    Spacer(Modifier.height(if (compact) 8.dp else 12.dp))

    // The deck standing in for the run timer. Categories completed is deliberately not shown: it
    // would tick over on the exact trial the rule moves.
    Text(
        text = stringResource(Res.string.game_rule_shift_cards_left, uiState.cardsRemaining),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
}

@Composable
private fun RuleShiftKeyTile(
    cell: RuleShiftKeyCell,
    symbolSize: Dp,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = ruleShiftKeyFace(cell.state),
        isClickable = isEnabled,
        onClick = if (isEnabled) onClick else ({}),
        modifier = modifier,
    ) {
        RuleShiftCardFace(
            card = cell.card,
            symbolSize = symbolSize,
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
        )
    }
}

@DevicePreviews
@Composable
private fun RuleShiftContentPreview() {
    GamePreviewHost {
        RuleShiftContent(
            uiState = RuleShiftUiState(
                keyCards = RuleShiftGame.keyCards.map { RuleShiftKeyCell(it) }.toImmutableList(),
                stimulus = RuleShiftGame.deck.first(),
                isAwaitingNextCard = false,
                cardsRemaining = 41,
            ),
            onAnswer = {},
        )
    }
}
