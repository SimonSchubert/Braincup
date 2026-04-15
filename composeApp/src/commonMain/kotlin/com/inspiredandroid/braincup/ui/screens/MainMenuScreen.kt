package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.GameController
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.components.DailyChallengeCard
import com.inspiredandroid.braincup.ui.components.GameTile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainMenuScreen(
    controller: GameController,
    isMuted: Boolean = false,
    onToggleMute: () -> Unit = {},
) {
    val sessionState by controller.sessionState.collectAsState()
    val sessionStreak by controller.sessionStreak.collectAsState()
    val session = sessionState
    val totalGames = session?.gameIds?.size ?: UserStorage.SESSION_GAME_COUNT
    val progressIndex = session?.currentIndex ?: 0
    val completedToday = remember(session) { controller.storage.isSessionCompletedToday() }

    MainMenuScreenContent(
        totalScore = remember { controller.storage.getTotalScore() },
        sessionStreak = sessionStreak,
        sessionProgressIndex = progressIndex,
        sessionTotalGames = totalGames,
        sessionCompletedToday = completedToday,
        highscores = remember { GameType.entries.associate { it.id to controller.storage.getHighScore(it.id) } },
        unlockedCount = remember { controller.storage.getUnlockedAchievements().size },
        isMuted = isMuted,
        onToggleMute = onToggleMute,
        onPlayDaily = { controller.startDailySession() },
        onPlay = { controller.navigateToInstructions(it) },
        onViewScore = { controller.navigateToScoreboard(it) },
        onAchievements = { controller.navigateToAchievements() },
    )
}

@Composable
fun MainMenuScreenContent(
    totalScore: Int,
    sessionStreak: Int,
    sessionProgressIndex: Int,
    sessionTotalGames: Int,
    sessionCompletedToday: Boolean,
    highscores: Map<String, Int>,
    unlockedCount: Int,
    isMuted: Boolean = false,
    onToggleMute: () -> Unit = {},
    onPlayDaily: () -> Unit = {},
    onPlay: (GameType) -> Unit = {},
    onViewScore: (GameType) -> Unit = {},
    onAchievements: () -> Unit = {},
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp + bottomInset),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header
        item(span = { GridItemSpan(maxLineSpan) }, contentType = "header") {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.offset(y = 16.dp),
                    )

                    Image(
                        painterResource(Res.drawable.ic_success),
                        contentDescription = null,
                        modifier = Modifier.height(190.dp),
                    )

                    Text(
                        text = stringResource(Res.string.app_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(y = -20.dp),
                    )
                }

                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .pointerHoverIcon(PointerIcon.Hand),
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isMuted) "Unmute" else "Mute",
                    )
                }
            }
        }

        // Daily challenge card
        item(span = { GridItemSpan(maxLineSpan) }, contentType = "daily_challenge") {
            DailyChallengeCard(
                sessionStreak = sessionStreak,
                progressIndex = sessionProgressIndex,
                totalGames = sessionTotalGames,
                completedToday = sessionCompletedToday,
                onPlay = onPlayDaily,
            )
        }

        // Game tiles
        items(GameType.entries, key = { it.id }, contentType = { "game_tile" }) { gameType ->
            GameTile(
                gameType = gameType,
                highscore = highscores[gameType.id] ?: 0,
                onPlay = { onPlay(gameType) },
                onViewScore = { onViewScore(gameType) },
            )
        }

        // Footer
        item(span = { GridItemSpan(maxLineSpan) }, contentType = "footer") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (sessionStreak > 0) {
                        StatCard(title = stringResource(Res.string.stat_training_days), value = sessionStreak.toString())
                    }
                    if (totalScore > 0) {
                        StatCard(title = stringResource(Res.string.stat_total_score), value = totalScore.toString())
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onAchievements,
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
