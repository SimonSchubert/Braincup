package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_rules_guide_intro
import braincup.composeapp.generated.resources.learn_rules_guide_title
import com.inspiredandroid.braincup.learn.RulesGuide
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.MathText
import com.inspiredandroid.braincup.ui.components.PrismCard
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
 * The rules guide: every sign convention and basic rule of arithmetic, in one scroll.
 *
 * Arithmetic's counterpart to the shape guide, and the same kind of thing: a reference, not a
 * lesson. The rule leads each card in the brand colour, the way a lesson's formula card does, so
 * the page can be scanned for the notation rather than read.
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
                    SectionHeader(section)
                }
                items(
                    items = section.rules,
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
private fun SectionHeader(section: RulesGuide.Section) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.titleSmall,
            color = Primary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = section.blurb,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RuleCell(rule: RulesGuide.Entry) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MathText(
                text = rule.rule,
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fractionSlash = true,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = rule.meaning,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            rule.example?.let { example ->
                Spacer(Modifier.height(4.dp))
                MathText(
                    text = example,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fractionSlash = true,
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
