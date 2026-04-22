package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
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
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryContainer,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        DailyChallengeProgressDots(
                            progressIndex = progressIndex,
                            totalGames = totalGames,
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
                    disabledContainerColor = OnPrimaryContainer.copy(alpha = 0.12f),
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
}

@Composable
private fun DailyChallengeProgressDots(
    progressIndex: Int,
    totalGames: Int,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(totalGames) { index ->
            val filled = index < progressIndex
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (filled) Primary else OnPrimaryContainer.copy(alpha = 0.12f),
                        shape = CircleShape,
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (filled) Primary else OnPrimaryContainer.copy(alpha = 0.45f),
                        shape = CircleShape,
                    ),
            )
        }
    }
}
