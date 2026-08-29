package com.inspiredandroid.braincup.ui.screens.iqtest

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.IqTestResultUiState
import com.inspiredandroid.braincup.games.iqtest.IqScoring
import com.inspiredandroid.braincup.games.iqtest.TierResult
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.BellCurve
import com.inspiredandroid.braincup.ui.components.BrandedCard
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismProgressBar
import com.inspiredandroid.braincup.ui.components.SectionColumn
import com.inspiredandroid.braincup.ui.components.TextPrismButton
import com.inspiredandroid.braincup.ui.components.XpAndLevelDisplay
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.numeric
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun IqTestResultScreen(
    uiState: IqTestResultUiState,
    onReview: () -> Unit,
    onDone: () -> Unit,
) {
    AppScaffold(
        title = stringResource(Res.string.iq_test_result_title),
        onBack = onDone,
        scrollable = true,
    ) {
        SectionColumn {
            Text(
                text = stringResource(Res.string.iq_test_estimated_iq),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = uiState.iq.toString(),
                style = MaterialTheme.typography.displayMedium.numeric(),
                color = Primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(uiState.band.labelRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = if (uiState.isBelowMeasurableRange) {
                    stringResource(Res.string.iq_test_percentile_low)
                } else {
                    stringResource(Res.string.iq_test_percentile, uiState.percentile.roundToInt())
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(20.dp))

            BellCurve(score = uiState.iq)

            Spacer(Modifier.height(20.dp))

            if (uiState.isBelowMeasurableRange) {
                BrandedCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Text(
                        text = stringResource(Res.string.iq_test_below_range),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            if (uiState.isPersonalBest) {
                BrandedCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(Res.string.iq_test_personal_best),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            Text(
                text = stringResource(Res.string.iq_test_raw_score, uiState.rawScore, uiState.itemCount),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(Res.string.iq_test_duration, formatDuration(uiState.durationSeconds)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        XpAndLevelDisplay(xpGained = uiState.xpGained, levelChange = uiState.levelChange)

        Spacer(Modifier.height(24.dp))

        SectionColumn {
            Text(
                text = stringResource(Res.string.iq_test_breakdown_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            uiState.tierBreakdown.forEach { tier ->
                TierRow(tier)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.iq_test_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(24.dp))

        TextPrismButton(
            onClick = onReview,
            value = stringResource(Res.string.iq_test_review),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(12.dp))

        PrimaryActionButton(
            onClick = onDone,
            value = stringResource(Res.string.iq_test_done),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TierRow(tier: TierResult) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
    ) {
        Text(
            text = stringResource(Res.string.iq_test_difficulty_level, tier.tier + 1),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(72.dp),
        )
        PrismProgressBar(
            progress = { tier.correct / tier.total.toFloat() },
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            fillColor = Primary,
            modifier = Modifier.weight(1f).height(10.dp),
        )
        Text(
            text = stringResource(Res.string.iq_test_breakdown_value, tier.correct, tier.total),
            style = MaterialTheme.typography.labelMedium.numeric(),
            textAlign = TextAlign.End,
            modifier = Modifier.width(48.dp).padding(start = 8.dp),
        )
    }
}

@DevicePreviews
@Composable
private fun IqTestResultScreenPreview() {
    ScreenPreviewHost {
        IqTestResultScreen(
            uiState = previewResultUiState(rawScore = 22),
            onReview = {},
            onDone = {},
        )
    }
}

internal fun previewResultUiState(rawScore: Int): IqTestResultUiState {
    val iq = IqScoring.iqFor(rawScore)
    return IqTestResultUiState(
        rawScore = rawScore,
        itemCount = 30,
        iq = iq,
        percentile = IqScoring.percentileFor(iq),
        band = IqScoring.bandFor(iq),
        isBelowMeasurableRange = IqScoring.isBelowMeasurableRange(rawScore),
        tierBreakdown = persistentListOf(
            TierResult(0, 3, 3),
            TierResult(1, 4, 4),
            TierResult(2, 5, 5),
            TierResult(3, 4, 5),
            TierResult(4, 3, 5),
            TierResult(5, 2, 5),
            TierResult(6, 1, 3),
        ),
        durationSeconds = 11 * 60 + 42,
        xpGained = UserStorage.IQ_TEST_COMPLETION_XP,
        levelChange = UserStorage.LevelChange(
            oldLevel = 5,
            newLevel = 6,
            totalXpBefore = 1200,
            totalXpAfter = 1260,
        ),
        isPersonalBest = true,
    )
}
