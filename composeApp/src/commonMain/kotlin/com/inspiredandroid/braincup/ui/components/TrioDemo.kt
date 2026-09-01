package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.trio_demo_group_not_set
import braincup.composeapp.generated.resources.trio_demo_group_set
import braincup.composeapp.generated.resources.trio_demo_reason_mixed
import braincup.composeapp.generated.resources.trio_demo_reason_nothing_same
import braincup.composeapp.generated.resources.trio_demo_rule
import braincup.composeapp.generated.resources.trio_demo_title
import braincup.composeapp.generated.resources.trio_demo_traits
import braincup.composeapp.generated.resources.trio_legend_different
import braincup.composeapp.generated.resources.trio_legend_same
import braincup.composeapp.generated.resources.trio_trait_count
import braincup.composeapp.generated.resources.trio_trait_fill
import braincup.composeapp.generated.resources.trio_trait_shape
import com.inspiredandroid.braincup.app.TrioUiState
import com.inspiredandroid.braincup.games.TrioCard
import com.inspiredandroid.braincup.games.TrioFill
import com.inspiredandroid.braincup.games.TrioGame
import com.inspiredandroid.braincup.games.TrioShape
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * How a trait comes out across three cards. The game's rule in these terms: no trait may be
 * [MIXED], and at least one has to be [SAME].
 */
internal enum class TraitVerdict { SAME, DIFFERENT, MIXED }

/** One demo row. [whyNot] is set when the row is there to be rejected, and captions it. */
internal data class TrioExample(val cards: List<TrioCard>, val whyNot: StringResource? = null)

/**
 * Two accepted trios, between them covering every trait as the shared one, then one rejection per
 * half of the rule, since each is otherwise only learnt by losing a guess.
 *
 * The first rejection splits shape two against one while sharing fill, so it also says that sharing
 * a trait is not on its own enough; its odd card carries a single mark, the only size at which a
 * lone shape is unmistakable. The second is the trap the other way round: a set in the card game
 * Trio is modelled on, but not here, because nothing at all is held constant.
 *
 * TrioDemoExamplesTest holds every row to [com.inspiredandroid.braincup.games.isTrioSet], so an
 * edit here cannot teach a trio the game would judge the other way.
 */
internal val TrioExamples = listOf(
    TrioExample(
        listOf(
            TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID),
            TrioCard(TrioShape.CIRCLE, 2, TrioFill.SOLID),
            TrioCard(TrioShape.CIRCLE, 3, TrioFill.SOLID),
        ),
    ),
    TrioExample(
        listOf(
            TrioCard(TrioShape.CIRCLE, 2, TrioFill.SOLID),
            TrioCard(TrioShape.SQUARE, 2, TrioFill.STRIPED),
            TrioCard(TrioShape.TRIANGLE, 2, TrioFill.OUTLINE),
        ),
    ),
    TrioExample(
        listOf(
            TrioCard(TrioShape.SQUARE, 1, TrioFill.SOLID),
            TrioCard(TrioShape.CIRCLE, 2, TrioFill.SOLID),
            TrioCard(TrioShape.CIRCLE, 3, TrioFill.SOLID),
        ),
        whyNot = Res.string.trio_demo_reason_mixed,
    ),
    TrioExample(
        listOf(
            TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID),
            TrioCard(TrioShape.SQUARE, 2, TrioFill.STRIPED),
            TrioCard(TrioShape.TRIANGLE, 3, TrioFill.OUTLINE),
        ),
        whyNot = Res.string.trio_demo_reason_nothing_same,
    ),
)

/** The per-trait reading of a row, derived from the cards so a badge cannot contradict them. */
internal fun traitVerdicts(cards: List<TrioCard>): List<Pair<StringResource, TraitVerdict>> = listOf(
    Res.string.trio_trait_shape to verdictOf(cards.map { it.shape }),
    Res.string.trio_trait_count to verdictOf(cards.map { it.count }),
    Res.string.trio_trait_fill to verdictOf(cards.map { it.fill }),
)

private fun verdictOf(values: List<Any>): TraitVerdict = when (values.toSet().size) {
    1 -> TraitVerdict.SAME
    values.size -> TraitVerdict.DIFFERENT
    else -> TraitVerdict.MIXED
}

/**
 * The rule shown rather than stated: a key to the three traits, then example trios labelled trait
 * by trait with whether that trait is the same on all three cards, one of each, or mixed.
 *
 * The earlier version listed every accepted pattern under two wordy headings and left the reader to
 * work out which trait each row was holding constant, which is the whole difficulty of the game.
 * The badges do that decoding, so the eye can go straight to the green "=".
 */
@Composable
fun TrioDemo(modifier: Modifier = Modifier) {
    val (sets, notSets) = TrioExamples.partition { it.whyNot == null }
    DemoScaffold(title = Res.string.trio_demo_title, modifier = modifier) {
        TraitKey()
        Spacer(Modifier.height(12.dp))
        MarkLegend()
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(Res.string.trio_demo_rule),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(18.dp))
        ExampleGroup(labelRes = Res.string.trio_demo_group_set, isSet = true, examples = sets)
        Spacer(Modifier.height(20.dp))
        ExampleGroup(labelRes = Res.string.trio_demo_group_not_set, isSet = false, examples = notSets)
    }
}

/**
 * Every value a card can carry, one trait per row, each row holding the other two traits fixed.
 *
 * Without it "one of each" reads as "any three", and "how many", sitting under a row of three
 * cards, reads as counting the cards - the one thing it never means.
 */
@Composable
private fun TraitKey(modifier: Modifier = Modifier) {
    val rows = listOf(
        Res.string.trio_trait_shape to TrioShape.entries.map { TrioCard(it, 1, TrioFill.SOLID) },
        Res.string.trio_trait_count to (1..3).map { TrioCard(TrioShape.CIRCLE, it, TrioFill.SOLID) },
        Res.string.trio_trait_fill to TrioFill.entries.map { TrioCard(TrioShape.CIRCLE, 1, it) },
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(Res.string.trio_demo_traits),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        rows.forEach { (labelRes, values) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    // Fixed, so the three value columns line up under each other.
                    modifier = Modifier.width(84.dp),
                )
                values.forEach { card -> TraitKeyChip(card) }
            }
        }
    }
}

/**
 * One value of one trait. Flat rather than a prism tile, so the key does not read as three more
 * example cards, but still on a face of its own, or a row of one, two and three marks runs together
 * into one row of six.
 */
@Composable
private fun TraitKeyChip(card: TrioCard, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(start = 6.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .size(width = 46.dp, height = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        TrioCardGlyphs(
            shape = card.shape,
            count = card.count,
            fill = card.fill,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxSize(),
            padding = PaddingValues(horizontal = 5.dp, vertical = 4.dp),
        )
    }
}

/** Says what the two badge marks mean before the examples start using them. */
@Composable
private fun MarkLegend(modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        TraitBadge(Res.string.trio_legend_same, TraitVerdict.SAME, markFirst = true)
        TraitBadge(Res.string.trio_legend_different, TraitVerdict.DIFFERENT, markFirst = true)
    }
}

@Composable
private fun ExampleGroup(
    labelRes: StringResource,
    isSet: Boolean,
    examples: List<TrioExample>,
    modifier: Modifier = Modifier,
) {
    val color = if (isSet) SuccessGreen else MaterialTheme.colorScheme.error
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
                text = stringResource(labelRes),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
        examples.forEach { ExampleRow(it) }
    }
}

@Composable
private fun ExampleRow(example: TrioExample, modifier: Modifier = Modifier) {
    // Bigger than a playing-board cell, deliberately: a card divides its face between up to three
    // marks, so at board size the glyphs the whole rule turns on came out about 6dp across, too
    // small to tell a striped triangle from a solid one. The screen scrolls; these have to be read.
    val cardSize = if (LocalIsCompactHeight.current) 52.dp else 64.dp
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            example.cards.forEach { card ->
                TrioCardTile(
                    // Undimmed, rejected rows included: a row you are meant to read trait by trait
                    // cannot be read through 30% alpha.
                    card = TrioUiState.Card(card.shape, card.count, card.fill, TrioGame.CardFeedback.NONE),
                    locked = true,
                    onClick = {},
                    modifier = Modifier.size(cardSize),
                )
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            traitVerdicts(example.cards).forEach { (labelRes, verdict) ->
                TraitBadge(labelRes, verdict)
            }
        }
        if (example.whyNot != null) {
            Text(
                text = stringResource(example.whyNot),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

/**
 * A trait name and its mark on one tinted pill, the mark leading in the legend and trailing under
 * an example. The mark carries the meaning on its own - equals, struck-through equals, cross - so
 * the tint is a scanning aid rather than the information, and the demo survives without colour.
 */
@Composable
private fun TraitBadge(
    labelRes: StringResource,
    verdict: TraitVerdict,
    modifier: Modifier = Modifier,
    markFirst: Boolean = false,
) {
    val ink = when (verdict) {
        TraitVerdict.SAME -> SuccessGreen
        TraitVerdict.DIFFERENT -> MaterialTheme.colorScheme.onSurfaceVariant
        TraitVerdict.MIXED -> MaterialTheme.colorScheme.error
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ink.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (markFirst) VerdictMark(verdict, ink)
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = ink,
        )
        if (!markFirst) VerdictMark(verdict, ink)
    }
}

@Composable
private fun VerdictMark(verdict: TraitVerdict, color: Color) {
    Box(Modifier.size(12.dp), contentAlignment = Alignment.Center) {
        when (verdict) {
            TraitVerdict.SAME -> ChunkyEquals(color, Modifier.fillMaxSize())
            TraitVerdict.DIFFERENT -> ChunkyNotEquals(color, Modifier.fillMaxSize())
            TraitVerdict.MIXED -> ChunkyCross(color, Modifier.fillMaxSize())
        }
    }
}
