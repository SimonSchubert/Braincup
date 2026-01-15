package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.GameController
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.games.getName
import org.jetbrains.compose.resources.painterResource

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
            .verticalScroll(rememberScrollState())
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
            Image(
                painter = painterResource(gameType.getIcon()),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
            Spacer(Modifier.width(8.dp))
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

private fun GameType.getIcon() = when (this) {
    GameType.MENTAL_CALCULATION -> Res.drawable.ic_mental_calculation
    GameType.CHAIN_CALCULATION -> Res.drawable.ic_chain_calculation
    GameType.COLOR_CONFUSION -> Res.drawable.ic_color_confusion
    GameType.SHERLOCK_CALCULATION -> Res.drawable.ic_sherlock_calculation
    GameType.FRACTION_CALCULATION -> Res.drawable.ic_fraction_calculation
    GameType.ANOMALY_PUZZLE -> Res.drawable.ic_anomaly_puzzle
    GameType.PATH_FINDER -> Res.drawable.ic_path_finder
    GameType.VALUE_COMPARISON -> Res.drawable.ic_value_comparison
    GameType.GRID_SOLVER -> Res.drawable.ic_grid_solver
    GameType.RIDDLE -> Res.drawable.ic_mental_calculation // fallback
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
