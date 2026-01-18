package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.GameController
import com.inspiredandroid.braincup.games.GameType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainMenuScreen(
    controller: GameController,
) {
    val totalScore = remember { controller.storage.getTotalScore() }
    val appOpenCount = remember { controller.storage.getAppOpenCount() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.padding(top = 16.dp).padding(bottom = 8.dp),
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.TopCenter),
            )

            Image(
                painterResource(Res.drawable.ic_success),
                contentDescription = null,
                modifier = Modifier
                    .height(190.dp)
                    .align(Alignment.Center),
            )

            Text(
                text = stringResource(Res.string.app_tagline),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
            )
        }

        val highscores = remember {
            GameController.games.associate { it.id to controller.storage.getHighScore(it.id) }
        }

        GameController.games.forEach { gameType ->
            GameRow(
                gameType = gameType,
                highscore = highscores[gameType.id] ?: 0,
                onPlay = { controller.navigateToInstructions(gameType) },
                onViewScore = { controller.navigateToScoreboard(gameType) },
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (appOpenCount > 1) {
                StatCard(title = stringResource(Res.string.stat_training_days), value = appOpenCount.toString())
            }
            if (totalScore > 0) {
                StatCard(title = stringResource(Res.string.stat_total_score), value = totalScore.toString())
            }
        }

        Spacer(Modifier.height(16.dp))

        val unlockedCount = remember { controller.storage.getUnlockedAchievements().size }

        Button(
            onClick = { controller.navigateToAchievements() },
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand)
                .widthIn(max = 420.dp)
                .height(56.dp),
        ) {
            Text(stringResource(Res.string.achievements_button, unlockedCount, UserStorage.Achievements.entries.size))
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun GameRow(
    gameType: GameType,
    highscore: Int,
    onPlay: () -> Unit,
    onViewScore: () -> Unit,
) {
    Button(
        onClick = onPlay,
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .widthIn(max = 420.dp)
            .height(56.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(gameType.icon),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            )
            Spacer(Modifier.width(12.dp))
            Text(stringResource(gameType.displayNameRes))

            Spacer(Modifier.weight(1f))

            if (highscore > 0) {
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(onClick = onViewScore),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$highscore", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}
