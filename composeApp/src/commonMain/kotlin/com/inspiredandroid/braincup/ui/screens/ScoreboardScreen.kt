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
                    text = highscore.toString(),
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
                score = 1,
                tint = Color(0xFFCD7F32),
                highscore = highscore,
            )
            MedalRequirement(
                score = gameType.silverScore,
                tint = Color(0xFFC0C0C0),
                highscore = highscore,
            )
            MedalRequirement(
                score = gameType.goldScore,
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
                items(scores, key = { (date, _) -> date }) { (date, dayScores) ->
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
                                text = dayScores.joinToString(", "),
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
    score: Int,
    tint: Color,
    highscore: Int,
) {
    val achieved = highscore >= score
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
            text = score.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = if (achieved) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        )
    }
}
