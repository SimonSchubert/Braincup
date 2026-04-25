package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.components.AppScaffold
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScoreboardScreen(
    gameType: GameType,
    storage: UserStorage,
    onBack: () -> Unit,
) {
    val highscore = storage.getHighScore(gameType.id)
    val scores = storage.getScores(gameType.id)

    AppScaffold(
        title = stringResource(Res.string.scoreboard_title, stringResource(gameType.displayNameRes)),
        onBack = onBack,
        scrollable = false,
    ) {
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.scoreboard_highscore),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = if (highscore > 0) gameType.formatScore(highscore) else "—",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            MedalRequirement(
                gameType = gameType,
                threshold = 1,
                tint = Color(0xFFCD7F32),
                highscore = highscore,
            )
            MedalRequirement(
                gameType = gameType,
                threshold = gameType.silverScore,
                tint = Color(0xFFC0C0C0),
                highscore = highscore,
            )
            MedalRequirement(
                gameType = gameType,
                threshold = gameType.goldScore,
                tint = Color(0xFFFFD700),
                highscore = highscore,
            )
        }

        Spacer(Modifier.height(16.dp))

        if (scores.isEmpty()) {
            Text(
                text = stringResource(Res.string.scoreboard_no_scores),
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            ) {
                items(scores, key = { "${it.day}/${it.month}/${it.year}" }) { group ->
                    val date = "${group.day.toString().padStart(2, '0')}.${group.month.toString().padStart(2, '0')}.${group.year}"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                        ) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Text(
                                text = group.scores.joinToString(", ") { gameType.formatScore(it) },
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MedalRequirement(
    gameType: GameType,
    threshold: Int,
    tint: Color,
    highscore: Int,
) {
    val achieved = gameType.meetsScore(highscore, threshold)
    val label = if (gameType.lowerScoreIsBetter) {
        "≤${gameType.formatScore(threshold)}"
    } else {
        threshold.toString()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painterResource(Res.drawable.ic_icons8_counter_gold),
            contentDescription = null,
            tint = if (achieved) tint else MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(32.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (achieved) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        )
    }
}
