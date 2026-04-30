package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainerDisabled
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainerOutline
import com.inspiredandroid.braincup.ui.theme.Primary
import org.jetbrains.compose.resources.stringResource

@Composable
fun DailyChallengeCard(
    sessionStreak: Int,
    progressIndex: Int,
    totalGames: Int,
    completedToday: Boolean,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BrandedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.daily_challenge_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryContainer,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = when {
                        completedToday -> stringResource(Res.string.daily_challenge_subtitle_done)
                        progressIndex > 0 -> stringResource(
                            Res.string.daily_challenge_subtitle_progress,
                            progressIndex,
                            totalGames,
                        )
                        sessionStreak > 0 -> stringResource(
                            Res.string.daily_challenge_subtitle_start,
                            totalGames,
                        )
                        else -> stringResource(
                            Res.string.daily_challenge_subtitle_start_no_streak,
                            totalGames,
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnPrimaryContainer,
                )
                if (progressIndex > 0 && !completedToday) {
                    Spacer(Modifier.height(8.dp))
                    ProgressDots(
                        currentIndex = progressIndex,
                        total = totalGames,
                        completedColor = Primary,
                        currentColor = OnPrimaryContainerDisabled,
                        mutedColor = OnPrimaryContainerDisabled,
                        activeSize = 12.dp,
                        inactiveSize = 12.dp,
                        completedBorder = Primary,
                        mutedBorder = OnPrimaryContainerOutline,
                    )
                }
            }
            if (sessionStreak > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = sessionStreak.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnPrimaryContainer,
                    )
                    Text(
                        text = stringResource(Res.string.daily_challenge_streak_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = OnPrimaryContainer,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onPlay,
            enabled = !completedToday,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = OnPrimaryContainerDisabled,
                disabledContentColor = OnPrimaryContainer,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .hoverHand(!completedToday),
        ) {
            Text(
                text = when {
                    completedToday -> stringResource(Res.string.daily_challenge_button_done)
                    progressIndex > 0 -> stringResource(Res.string.daily_challenge_button_resume)
                    else -> stringResource(Res.string.daily_challenge_button_start)
                },
            )
        }
    }
}
