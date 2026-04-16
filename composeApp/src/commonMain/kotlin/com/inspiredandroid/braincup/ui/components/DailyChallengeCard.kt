package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun DailyChallengeCard(
    sessionStreak: Int,
    progressIndex: Int,
    totalGames: Int,
    completedToday: Boolean,
    onPlay: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                            else -> stringResource(Res.string.daily_challenge_subtitle_start, totalGames)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                if (sessionStreak > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = sessionStreak.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            text = stringResource(Res.string.daily_challenge_streak_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onPlay,
                enabled = !completedToday,
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerHoverIcon(PointerIcon.Hand),
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
