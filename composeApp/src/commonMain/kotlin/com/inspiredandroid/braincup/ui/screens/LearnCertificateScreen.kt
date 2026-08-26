package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.app_name
import braincup.composeapp.generated.resources.learn_certificate_earned
import braincup.composeapp.generated.resources.learn_certificate_headline
import braincup.composeapp.generated.resources.learn_certificate_intro
import braincup.composeapp.generated.resources.learn_certificate_title
import braincup.composeapp.generated.resources.learn_lesson_finish
import braincup.composeapp.generated.resources.learn_quiz_failed
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.CertificateMedal
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.Primary
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

@Composable
fun LearnCertificateScreen(
    unit: LearnUnit,
    storage: UserStorage,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    val certificate = remember(storage, unit) { storage.getLearnCertificate(unit) }
    LearnCertificateScreenContent(
        unit = unit,
        earnedEpochDay = certificate?.earnedEpochDay,
        onDone = onDone,
        onBack = onBack,
    )
}

@Composable
fun LearnCertificateScreenContent(
    unit: LearnUnit,
    earnedEpochDay: Int?,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(Res.string.learn_certificate_title),
        onBack = onBack,
    ) {
        if (earnedEpochDay == null) {
            Text(
                text = stringResource(Res.string.learn_quiz_failed),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp),
            )
            PrimaryActionButton(
                onClick = onDone,
                value = stringResource(Res.string.learn_lesson_finish),
            )
            return@AppScaffold
        }

        CertificateCard(
            unit = unit,
            earnedEpochDay = earnedEpochDay,
            modifier = Modifier
                .widthIn(max = ContentMaxWidth)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(24.dp))
        PrimaryActionButton(
            onClick = onDone,
            value = stringResource(Res.string.learn_lesson_finish),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CertificateCard(
    unit: LearnUnit,
    earnedEpochDay: Int,
    modifier: Modifier = Modifier,
) {
    PrismCard(
        face = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(width = 2.dp, color = MedalGold)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.learn_certificate_headline),
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            CertificateMedal(modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.learn_certificate_intro),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(unit.topic.titleRes),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(2.dp))
            // The sub-topic, not the grade band: the test that was passed was "Fractions", and two
            // certificates reading "Arithmetic, Grades 3-5" said nothing about what was learned.
            Text(
                text = unit.title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
            if (earnedEpochDay > 0) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.learn_certificate_earned, formatEpochDay(earnedEpochDay)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.labelMedium,
                color = Primary,
            )
        }
    }
}

/** Epoch day → ISO date, matching how the rest of the app derives calendar days (UTC). */
private fun formatEpochDay(epochDay: Int): String = Instant
    .fromEpochMilliseconds(epochDay.toLong() * 86_400_000L)
    .toLocalDateTime(TimeZone.UTC)
    .date
    .toString()

@DevicePreviews
@Composable
private fun LearnCertificateScreenPreview() {
    ScreenPreviewHost {
        LearnCertificateScreenContent(
            unit = LearnCatalog.units(MathTopic.GEOMETRY).first(),
            earnedEpochDay = 20_000,
            onDone = {},
            onBack = {},
        )
    }
}
