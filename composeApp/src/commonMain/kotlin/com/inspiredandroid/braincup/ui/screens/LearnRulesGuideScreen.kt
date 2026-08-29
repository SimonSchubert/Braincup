package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_rules_guide_intro
import braincup.composeapp.generated.resources.learn_rules_guide_title
import com.inspiredandroid.braincup.learn.RulesGuide
import com.inspiredandroid.braincup.learn.isNotation
import com.inspiredandroid.braincup.learn.resolve
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.GuideSectionHeader
import com.inspiredandroid.braincup.ui.components.MathText
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.learn.LearnText
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.Primary
import org.jetbrains.compose.resources.stringResource

/**
 * One rule to a row on a phone, two on a tablet. Wider than the shape cells because a rule is a
 * line of notation and wrapping one across two lines is what makes it hard to read.
 */
private val RuleCellMinWidth = 300.dp

/**
 * A little more air than `bodySmall` carries, because the meaning is the one run on the card that
 * wraps, and Bungee capitals set on their default leading close up into a block when they do.
 */
private val MeaningLineHeight = 18.sp

/**
 * The rules guide: every sign convention and basic rule of arithmetic, in one scroll.
 *
 * Arithmetic's counterpart to the shape guide, and the same kind of thing: a reference, not a
 * lesson. The rule leads each card in the brand colour, the way a lesson's formula card does, so
 * the page can be scanned for the notation rather than read.
 *
 * A card is read down a single left edge: the rule, the sentence that says what it means, a rule
 * off, then an instance of it. Notation is set at one size and prose at another, so the three lines
 * carry a hierarchy rather than three unrelated sizes, and every line starts where the one above it
 * did - centred, a card of three runs of three different lengths shares no edge at all.
 */
@Composable
fun LearnRulesGuideScreen(onBack: () -> Unit) {
    AppScaffold(
        title = stringResource(Res.string.learn_rules_guide_title),
        onBack = onBack,
        scrollable = false,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = RuleCellMinWidth),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "intro", span = { GridItemSpan(maxLineSpan) }, contentType = "intro") {
                Text(
                    text = stringResource(Res.string.learn_rules_guide_intro, RulesGuide.ruleCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            RulesGuide.sections.forEach { section ->
                item(key = section.id, span = { GridItemSpan(maxLineSpan) }, contentType = "section") {
                    GuideSectionHeader(section)
                }
                items(
                    items = section.entries,
                    key = { it.id },
                    contentType = { "rule" },
                ) { rule ->
                    RuleCell(rule)
                }
            }
        }
    }
}

@Composable
private fun RuleCell(rule: RulesGuide.Entry) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
            // The rule leads, at the size the example used to have. It is what the page is looked
            // up for, and a card whose heading was set smaller than its footnote read bottom-up.
            // Written in letters - "a - (-b) = a + b" - so there are no values in it for the colour
            // code to speak about, and it keeps the one flat accent.
            MathText(
                text = rule.rule.resolve(),
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
                fractionSlash = true,
            )
            Spacer(Modifier.height(4.dp))
            // Two lines are reserved whether or not the sentence needs them, so cells sitting side
            // by side in a row end level instead of leaving a ragged edge under the shorter one.
            // Muted, because a bright line of Bungee capitals between two equations was the loudest
            // thing on the card and the least of what it says.
            Text(
                text = stringResource(rule.meaning),
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = MeaningLineHeight),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            rule.example?.let { example ->
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                )
                Spacer(Modifier.height(10.dp))
                // The instance of the rule, and the same size as the rule: both are notation, and
                // giving each kind of run one size is what lets the three lines read as a hierarchy
                // rather than as three unrelated sizes. It carries the three-role colouring a
                // lesson's formula card carries - "5 - (-3) = 8" reads given, structure, working,
                // structure, answer here exactly as it would in the lesson that teaches it - while
                // an example written in words ("not 5 x 4") is an aside on the rule rather than a
                // worked line, so it takes the muted tone instead of shouting in the display face.
                LearnText(
                    text = example.resolve(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                    roleColors = true,
                    notation = example.isNotation,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun LearnRulesGuideScreenPreview() {
    ScreenPreviewHost {
        LearnRulesGuideScreen(onBack = {})
    }
}
