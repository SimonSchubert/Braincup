package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.components.AppScaffold
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
                items(scores) { (date, dayScores) ->
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
