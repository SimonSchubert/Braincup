package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_trio_howto
import com.inspiredandroid.braincup.app.TrioUiState
import com.inspiredandroid.braincup.games.TrioFill
import com.inspiredandroid.braincup.games.TrioGame
import com.inspiredandroid.braincup.games.TrioShape
import com.inspiredandroid.braincup.ui.components.LocalIsCompactHeight
import com.inspiredandroid.braincup.ui.components.TrioCardTile
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TrioContent(
    uiState: TrioUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val cellMax = if (compact) 56.dp else 72.dp
    val locked = uiState.cards.any {
        it.feedback == TrioGame.CardFeedback.CORRECT || it.feedback == TrioGame.CardFeedback.WRONG
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!compact) {
            Text(
                text = stringResource(Res.string.game_trio_howto),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = cellMax * uiState.columns),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            uiState.cards.chunked(uiState.columns).forEachIndexed { row, rowCards ->
                Row {
                    rowCards.forEachIndexed { col, card ->
                        val index = row * uiState.columns + col
                        TrioCardTile(
                            card = card,
                            locked = locked,
                            onClick = { onAnswer(index.toString()) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp),
                        )
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun TrioContentPreview() {
    GamePreviewHost {
        TrioContent(
            uiState = TrioUiState(
                cards = persistentListOf(
                    TrioUiState.Card(TrioShape.CIRCLE, 1, TrioFill.SOLID, TrioGame.CardFeedback.SELECTED),
                    TrioUiState.Card(TrioShape.SQUARE, 2, TrioFill.STRIPED, TrioGame.CardFeedback.NONE),
                    TrioUiState.Card(TrioShape.TRIANGLE, 3, TrioFill.OUTLINE, TrioGame.CardFeedback.NONE),
                    TrioUiState.Card(TrioShape.SQUARE, 1, TrioFill.OUTLINE, TrioGame.CardFeedback.NONE),
                    TrioUiState.Card(TrioShape.CIRCLE, 2, TrioFill.SOLID, TrioGame.CardFeedback.SELECTED),
                    TrioUiState.Card(TrioShape.TRIANGLE, 1, TrioFill.STRIPED, TrioGame.CardFeedback.NONE),
                    TrioUiState.Card(TrioShape.TRIANGLE, 2, TrioFill.SOLID, TrioGame.CardFeedback.NONE),
                    TrioUiState.Card(TrioShape.SQUARE, 3, TrioFill.SOLID, TrioGame.CardFeedback.NONE),
                    TrioUiState.Card(TrioShape.CIRCLE, 3, TrioFill.SOLID, TrioGame.CardFeedback.NONE),
                    TrioUiState.Card(TrioShape.SQUARE, 2, TrioFill.OUTLINE, TrioGame.CardFeedback.NONE),
                    TrioUiState.Card(TrioShape.TRIANGLE, 3, TrioFill.STRIPED, TrioGame.CardFeedback.NONE),
                    TrioUiState.Card(TrioShape.CIRCLE, 1, TrioFill.STRIPED, TrioGame.CardFeedback.NONE),
                ),
                columns = 3,
            ),
            onAnswer = {},
        )
    }
}
