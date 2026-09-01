package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.trio_demo_no_match
import braincup.composeapp.generated.resources.trio_demo_one_match
import braincup.composeapp.generated.resources.trio_demo_rule
import braincup.composeapp.generated.resources.trio_demo_title
import braincup.composeapp.generated.resources.trio_demo_two_match
import com.inspiredandroid.braincup.app.TrioUiState
import com.inspiredandroid.braincup.games.TrioCard
import com.inspiredandroid.braincup.games.TrioFill
import com.inspiredandroid.braincup.games.TrioGame
import com.inspiredandroid.braincup.games.TrioShape
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Every trio the rule accepts, one row each: a trait is all-same or all-different across the three
 * cards, and at least one is all-same. All-same everywhere cannot be dealt from a deck of unique
 * cards, so what is left is three rows holding two traits and three holding one.
 *
 * TrioDemoExamplesTest checks these against [com.inspiredandroid.braincup.games.isTrioSet], so an
 * edit here cannot quietly teach a trio the game would reject.
 */
internal val TrioTwoMatchExamples = listOf(
    listOf(
        TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID),
        TrioCard(TrioShape.CIRCLE, 2, TrioFill.SOLID),
        TrioCard(TrioShape.CIRCLE, 3, TrioFill.SOLID),
    ),
    listOf(
        TrioCard(TrioShape.SQUARE, 2, TrioFill.SOLID),
        TrioCard(TrioShape.SQUARE, 2, TrioFill.STRIPED),
        TrioCard(TrioShape.SQUARE, 2, TrioFill.OUTLINE),
    ),
    listOf(
        TrioCard(TrioShape.CIRCLE, 3, TrioFill.OUTLINE),
        TrioCard(TrioShape.SQUARE, 3, TrioFill.OUTLINE),
        TrioCard(TrioShape.TRIANGLE, 3, TrioFill.OUTLINE),
    ),
)

internal val TrioOneMatchExamples = listOf(
    listOf(
        TrioCard(TrioShape.TRIANGLE, 1, TrioFill.SOLID),
        TrioCard(TrioShape.TRIANGLE, 2, TrioFill.STRIPED),
        TrioCard(TrioShape.TRIANGLE, 3, TrioFill.OUTLINE),
    ),
    listOf(
        TrioCard(TrioShape.CIRCLE, 2, TrioFill.SOLID),
        TrioCard(TrioShape.SQUARE, 2, TrioFill.STRIPED),
        TrioCard(TrioShape.TRIANGLE, 2, TrioFill.OUTLINE),
    ),
    listOf(
        TrioCard(TrioShape.CIRCLE, 1, TrioFill.STRIPED),
        TrioCard(TrioShape.SQUARE, 2, TrioFill.STRIPED),
        TrioCard(TrioShape.TRIANGLE, 3, TrioFill.STRIPED),
    ),
)

/**
 * The trap: this is a set in the card game Trio is modelled on, but not here, because nothing is
 * held constant. Shown deliberately, since the rule is otherwise only learnt by losing a guess.
 */
internal val TrioNoMatchExample = listOf(
    TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID),
    TrioCard(TrioShape.SQUARE, 2, TrioFill.STRIPED),
    TrioCard(TrioShape.TRIANGLE, 3, TrioFill.OUTLINE),
)

@Composable
fun TrioDemo(modifier: Modifier = Modifier) {
    DemoScaffold(
        title = Res.string.trio_demo_title,
        modifier = modifier,
        description = Res.string.trio_demo_rule,
    ) {
        ExampleGroup(labelRes = Res.string.trio_demo_two_match, rows = TrioTwoMatchExamples)
        Spacer(Modifier.height(16.dp))
        ExampleGroup(labelRes = Res.string.trio_demo_one_match, rows = TrioOneMatchExamples)
        Spacer(Modifier.height(16.dp))
        ExampleGroup(
            labelRes = Res.string.trio_demo_no_match,
            rows = listOf(TrioNoMatchExample),
            feedback = TrioGame.CardFeedback.DIMMED,
        )
    }
}

@Composable
private fun ExampleGroup(
    labelRes: StringResource,
    rows: List<List<TrioCard>>,
    modifier: Modifier = Modifier,
    feedback: TrioGame.CardFeedback = TrioGame.CardFeedback.NONE,
) {
    // Sized here rather than from gridCellMaxSize: seven rows of a playing-board cell would push
    // the Start button off even a tall screen.
    val cardSize = if (LocalIsCompactHeight.current) 36.dp else 44.dp
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
        )
        rows.forEach { cards ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                cards.forEach { card ->
                    TrioCardTile(
                        card = TrioUiState.Card(card.shape, card.count, card.fill, feedback),
                        locked = true,
                        onClick = {},
                        modifier = Modifier.size(cardSize),
                    )
                }
            }
        }
    }
}
