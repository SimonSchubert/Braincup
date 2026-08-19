package com.inspiredandroid.braincup.ui.screens.iqtest

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.iqtest.IqTestBlueprint
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.BrandedCard
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.SectionColumn
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.numeric
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun IqTestIntroScreen(
    history: ImmutableList<UserStorage.IqTestRecord>,
    onStart: () -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(Res.string.iq_test_title),
        onBack = onBack,
        scrollable = true,
    ) {
        SectionColumn {
            Text(
                text = stringResource(Res.string.iq_test_intro_headline),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(
                    Res.string.iq_test_intro_body,
                    IqTestBlueprint.ITEM_COUNT,
                    (IqTestBlueprint.TIME_LIMIT_MILLIS / 60_000L).toInt(),
                ),
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.iq_test_intro_pacing),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(20.dp))

            BrandedCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(Res.string.iq_test_disclaimer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnPrimaryContainer,
                )
            }

            if (history.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(Res.string.iq_test_retake_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(Res.string.iq_test_history_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                history.forEach { record ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = formatDate(record.epochMillis),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = record.iq.toString(),
                            style = MaterialTheme.typography.titleMedium.numeric(),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        PrimaryActionButton(
            onClick = onStart,
            value = stringResource(Res.string.iq_test_start),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(24.dp))
    }
}

@DevicePreviews
@Composable
private fun IqTestIntroScreenPreview() {
    ScreenPreviewHost {
        IqTestIntroScreen(
            history = persistentListOf(
                UserStorage.IqTestRecord(1_755_000_000_000L, 1L, rawScore = 22, durationSeconds = 640),
                UserStorage.IqTestRecord(1_754_000_000_000L, 2L, rawScore = 18, durationSeconds = 720),
            ),
            onStart = {},
            onBack = {},
        )
    }
}

@DevicePreviews
@Composable
private fun IqTestIntroScreenFirstRunPreview() {
    ScreenPreviewHost {
        IqTestIntroScreen(history = persistentListOf(), onStart = {}, onBack = {})
    }
}
