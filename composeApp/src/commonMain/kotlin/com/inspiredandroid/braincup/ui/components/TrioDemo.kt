package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import braincup.composeapp.generated.resources.trio_demo_group_not_set
import braincup.composeapp.generated.resources.trio_demo_group_set
import braincup.composeapp.generated.resources.trio_demo_rule
import braincup.composeapp.generated.resources.trio_demo_title
import braincup.composeapp.generated.resources.trio_reason_same_fill
import braincup.composeapp.generated.resources.trio_reason_same_number
import braincup.composeapp.generated.resources.trio_reason_same_number_fill
import braincup.composeapp.generated.resources.trio_reason_same_shape
import braincup.composeapp.generated.resources.trio_reason_same_shape_fill
import braincup.composeapp.generated.resources.trio_reason_same_shape_number
import braincup.composeapp.generated.resources.trio_trait_count
import braincup.composeapp.generated.resources.trio_trait_fill
import braincup.composeapp.generated.resources.trio_trait_shape
import braincup.composeapp.generated.resources.trio_wrong_nothing_same
import braincup.composeapp.generated.resources.trio_wrong_trait
import com.inspiredandroid.braincup.app.TrioUiState
import com.inspiredandroid.braincup.games.TrioCard
import com.inspiredandroid.braincup.games.TrioFill
import com.inspiredandroid.braincup.games.TrioGame
import com.inspiredandroid.braincup.games.TrioShape
import com.inspiredandroid.braincup.games.TrioTrait
import com.inspiredandroid.braincup.games.isTrioSet
import com.inspiredandroid.braincup.games.mixedTrioTraits
import com.inspiredandroid.braincup.games.sharesAnyTrait
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Every trait gets a turn as the one held the same, and as the one that breaks a near miss. The
 * first near miss is the case from issue #70: a shared shape with two fills alike. The last is the
 * all-different trio, which the card game Set accepts and this one does not.
 */
internal val TrioExamples: List<List<TrioCard>> = listOf(
    listOf(
        TrioCard(TrioShape.TRIANGLE, 1, TrioFill.OUTLINE),
        TrioCard(TrioShape.TRIANGLE, 2, TrioFill.OUTLINE),
        TrioCard(TrioShape.TRIANGLE, 3, TrioFill.OUTLINE),
    ),
    listOf(
        TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID),
        TrioCard(TrioShape.CIRCLE, 2, TrioFill.STRIPED),
        TrioCard(TrioShape.CIRCLE, 3, TrioFill.OUTLINE),
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
    listOf(
        TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID),
        TrioCard(TrioShape.CIRCLE, 2, TrioFill.SOLID),
        TrioCard(TrioShape.CIRCLE, 3, TrioFill.STRIPED),
    ),
    listOf(
        TrioCard(TrioShape.CIRCLE, 2, TrioFill.SOLID),
        TrioCard(TrioShape.SQUARE, 2, TrioFill.STRIPED),
        TrioCard(TrioShape.SQUARE, 2, TrioFill.OUTLINE),
    ),
    listOf(
        TrioCard(TrioShape.TRIANGLE, 1, TrioFill.SOLID),
        TrioCard(TrioShape.TRIANGLE, 1, TrioFill.STRIPED),
        TrioCard(TrioShape.TRIANGLE, 2, TrioFill.OUTLINE),
    ),
    listOf(
        TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID),
        TrioCard(TrioShape.SQUARE, 2, TrioFill.STRIPED),
        TrioCard(TrioShape.TRIANGLE, 3, TrioFill.OUTLINE),
    ),
)

/**
 * Why a trio counts, picked from the traits it holds the same rather than written per example, so
 * a caption cannot drift from the cards above it. One whole sentence per case, because a sentence
 * assembled from trait names does not survive translation into gendered languages.
 */
internal fun trioMatchReason(a: TrioCard, b: TrioCard, c: TrioCard): StringResource {
    val same = TrioTrait.entries.filter { trait -> setOf(a, b, c).map { it.value(trait) }.toSet().size == 1 }.toSet()
    return when (same) {
        setOf(TrioTrait.SHAPE) -> Res.string.trio_reason_same_shape
        setOf(TrioTrait.COUNT) -> Res.string.trio_reason_same_number
        setOf(TrioTrait.FILL) -> Res.string.trio_reason_same_fill
        setOf(TrioTrait.SHAPE, TrioTrait.COUNT) -> Res.string.trio_reason_same_shape_number
        setOf(TrioTrait.SHAPE, TrioTrait.FILL) -> Res.string.trio_reason_same_shape_fill
        setOf(TrioTrait.COUNT, TrioTrait.FILL) -> Res.string.trio_reason_same_number_fill
        else -> error("not a trio: $a, $b, $c")
    }
}

private fun TrioCard.value(trait: TrioTrait): Any = when (trait) {
    TrioTrait.SHAPE -> shape
    TrioTrait.COUNT -> count
    TrioTrait.FILL -> fill
}

internal val TrioTrait.label: StringResource
    get() = when (this) {
        TrioTrait.SHAPE -> Res.string.trio_trait_shape
        TrioTrait.COUNT -> Res.string.trio_trait_count
        TrioTrait.FILL -> Res.string.trio_trait_fill
    }

@Composable
fun TrioDemo(modifier: Modifier = Modifier) {
    val (sets, notSets) = TrioExamples.partition { (a, b, c) -> isTrioSet(a, b, c) }
    DemoScaffold(title = Res.string.trio_demo_title, modifier = modifier) {
        Text(
            text = stringResource(Res.string.trio_demo_rule),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(Modifier.height(20.dp))
        ExampleGroup(isSet = true, examples = sets)
        Spacer(Modifier.height(24.dp))
        ExampleGroup(isSet = false, examples = notSets)
    }
}

@Composable
private fun ExampleGroup(isSet: Boolean, examples: List<List<TrioCard>>) {
    val color = if (isSet) SuccessGreen else MaterialTheme.colorScheme.error
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        // Wide enough that two uncaptioned rows read as two trios, not one grid of six cards.
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                if (isSet) {
                    ChunkyCheck(color, Modifier.fillMaxSize())
                } else {
                    ChunkyCross(color, Modifier.fillMaxSize())
                }
            }
            Text(
                text = stringResource(if (isSet) Res.string.trio_demo_group_set else Res.string.trio_demo_group_not_set),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
        examples.forEach { ExampleRow(it) }
    }
}

@Composable
private fun ExampleRow(cards: List<TrioCard>) {
    val (a, b, c) = cards
    val cardSize = if (LocalIsCompactHeight.current) 52.dp else 64.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            cards.forEach { card ->
                TrioCardTile(
                    card = TrioUiState.Card(card.shape, card.count, card.fill, TrioGame.CardFeedback.NONE),
                    locked = true,
                    onClick = {},
                    modifier = Modifier.size(cardSize),
                )
            }
        }
        if (isTrioSet(a, b, c)) {
            Text(
                text = stringResource(trioMatchReason(a, b, c)),
                style = MaterialTheme.typography.bodySmall,
                color = SuccessGreen,
                textAlign = TextAlign.Center,
            )
        }
        // Worded like the in-game message after a wrong guess, so the two teach the same thing.
        if (!sharesAnyTrait(a, b, c)) {
            Text(
                text = stringResource(Res.string.trio_wrong_nothing_same),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }
        mixedTrioTraits(a, b, c).forEach { trait ->
            Text(
                text = stringResource(Res.string.trio_wrong_trait, stringResource(trait.label)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }
    }
}
