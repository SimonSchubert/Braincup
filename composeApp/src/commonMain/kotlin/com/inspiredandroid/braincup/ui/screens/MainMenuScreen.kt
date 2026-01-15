package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.GameController
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.games.getName

@Composable
fun MainMenuScreen(
    controller: GameController
) {
    val storage = controller.storage
    val totalScore = storage.getTotalScore()
    val appOpenCount = storage.getAppOpenCount()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Braincup",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Train your math skills, memory and focus.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        GameController.games.forEach { gameType ->
            GameRow(
                gameType = gameType,
                highscore = storage.getHighScore(gameType.getId()),
                onPlay = { controller.navigateToInstructions(gameType) },
                onViewScore = { controller.navigateToScoreboard(gameType) }
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (appOpenCount > 1) {
                StatCard(title = "Training days", value = appOpenCount.toString())
            }
            if (totalScore > 0) {
                StatCard(title = "Total score", value = totalScore.toString())
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { controller.navigateToAchievements() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Achievements (${storage.getUnlockedAchievements().size}/${UserStorage.Achievements.entries.size})")
        }
    }
}

@Composable
private fun GameRow(
    gameType: GameType,
    highscore: Int,
    onPlay: () -> Unit,
    onViewScore: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPlay,
            modifier = Modifier.weight(1f)
        ) {
            Text(gameType.getName())
        }

        if (highscore > 0) {
            Spacer(Modifier.width(8.dp))
            OutlinedButton(
                onClick = onViewScore
            ) {
                Text("$highscore")
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
