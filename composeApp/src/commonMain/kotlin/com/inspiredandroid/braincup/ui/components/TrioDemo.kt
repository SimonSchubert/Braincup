package com.inspiredandroid.braincup.ui.components

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.trio_demo_correct
import braincup.composeapp.generated.resources.trio_demo_rule
import braincup.composeapp.generated.resources.trio_demo_title
import com.inspiredandroid.braincup.app.TrioUiState
import com.inspiredandroid.braincup.games.TrioFill
import com.inspiredandroid.braincup.games.TrioGame
import com.inspiredandroid.braincup.games.TrioShape
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private data class TrioDemoRound(
    val cards: List<TrioUiState.Card>,
)

private val Rounds = listOf(
    TrioDemoRound(
        cards = listOf(
            TrioUiState.Card(TrioShape.CIRCLE, 1, TrioFill.SOLID, TrioGame.CardFeedback.NONE),
            TrioUiState.Card(TrioShape.CIRCLE, 2, TrioFill.SOLID, TrioGame.CardFeedback.NONE),
            TrioUiState.Card(TrioShape.CIRCLE, 3, TrioFill.SOLID, TrioGame.CardFeedback.NONE),
        ),
    ),
    TrioDemoRound(
        cards = listOf(
            TrioUiState.Card(TrioShape.SQUARE, 2, TrioFill.OUTLINE, TrioGame.CardFeedback.NONE),
            TrioUiState.Card(TrioShape.SQUARE, 2, TrioFill.STRIPED, TrioGame.CardFeedback.NONE),
            TrioUiState.Card(TrioShape.SQUARE, 2, TrioFill.SOLID, TrioGame.CardFeedback.NONE),
        ),
    ),
    TrioDemoRound(
        cards = listOf(
            TrioUiState.Card(TrioShape.CIRCLE, 2, TrioFill.SOLID, TrioGame.CardFeedback.NONE),
            TrioUiState.Card(TrioShape.SQUARE, 2, TrioFill.SOLID, TrioGame.CardFeedback.NONE),
            TrioUiState.Card(TrioShape.TRIANGLE, 2, TrioFill.SOLID, TrioGame.CardFeedback.NONE),
        ),
    ),
)

private val DemoCaptions = persistentListOf(
    Res.string.trio_demo_rule,
    Res.string.trio_demo_correct,
)

private const val ScanMillis = 1600L
private const val SelectStaggerMillis = 450L
private const val SolvedHoldMillis = 1400L
private const val RoundRestMillis = 350L
private const val LoopEndHoldMillis = 700L

@Composable
fun TrioDemo(modifier: Modifier = Modifier) {
    var roundIndex by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf(emptySet<Int>()) }
    var solved by remember { mutableStateOf(false) }
    var captionRes by remember { mutableStateOf(Res.string.trio_demo_rule) }

    LaunchedEffect(Unit) {
        while (true) {
            for (index in Rounds.indices) {
                val round = Rounds[index]
                roundIndex = index
                selected = emptySet()
                solved = false
                captionRes = Res.string.trio_demo_rule
                delay(ScanMillis)

                for (cardIndex in round.cards.indices) {
                    selected = selected + cardIndex
                    delay(SelectStaggerMillis)
                }

                solved = true
                captionRes = Res.string.trio_demo_correct
                delay(SolvedHoldMillis)
                delay(RoundRestMillis)
            }
            delay(LoopEndHoldMillis)
        }
    }

    val cards = Rounds[roundIndex].cards
    val cellMax = gridCellMaxSize
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.trio_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.widthIn(max = cellMax * cards.size),
        ) {
            cards.forEachIndexed { index, card ->
                val feedback = when {
                    solved -> TrioGame.CardFeedback.CORRECT
                    index in selected -> TrioGame.CardFeedback.SELECTED
                    else -> TrioGame.CardFeedback.NONE
                }
                TrioCardTile(
                    card = card.copy(feedback = feedback),
                    locked = true,
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp),
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        DemoCaption(current = captionRes, all = DemoCaptions)
    }
}
