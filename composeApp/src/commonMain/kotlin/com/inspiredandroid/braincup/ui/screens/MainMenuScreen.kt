package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.PlayGamesBridge
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.GameController
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.wordle.WordleLanguages
import com.inspiredandroid.braincup.matchstickriddles.MatchstickRiddles
import com.inspiredandroid.braincup.rememberMainMenuSponsorsSection
import com.inspiredandroid.braincup.ui.components.DailyChallengeCard
import com.inspiredandroid.braincup.ui.components.GameTile
import com.inspiredandroid.braincup.ui.components.MatchstickRiddlesTile
import com.inspiredandroid.braincup.ui.components.NormalChessTile
import com.inspiredandroid.braincup.ui.components.NormalSudokuTile
import com.inspiredandroid.braincup.ui.components.PlayerLevelCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.PrismTrophy
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.LocalAccessiblePalette
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.Primary
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.intl.Locale as ComposeLocale

@Composable
fun MainMenuScreen(
    controller: GameController,
    onOpenSettings: () -> Unit = {},
    useBuiltInSponsors: Boolean = false,
) {
    val sessionState by controller.sessionState.collectAsState()
    val sessionStreak by controller.sessionStreak.collectAsState()
    val session = sessionState
    val totalGames = session?.gameIds?.size ?: UserStorage.SESSION_GAME_COUNT
    val progressIndex = session?.currentIndex ?: 0
    val completedToday = remember(session) { controller.storage.isSessionCompletedToday() }

    val totalXp by controller.totalXp.collectAsState()
    val highscores by controller.highscores.collectAsState()
    val unlockedCount by controller.unlockedAchievementCount.collectAsState()
    val normalSudokuCompleted = remember(controller) {
        controller.storage.getCompletedNormalSudokuIds().size
    }
    val matchstickRiddlesSolved = remember(controller) {
        controller.storage.getSolvedMatchstickRiddleIds().size
    }
    val matchstickRiddlesTotal = remember { MatchstickRiddles.all.size }

    MainMenuScreenContent(
        totalXp = totalXp,
        sessionStreak = sessionStreak,
        sessionProgressIndex = progressIndex,
        sessionTotalGames = totalGames,
        sessionCompletedToday = completedToday,
        highscores = highscores.toImmutableMap(),
        unlockedCount = unlockedCount,
        normalSudokuCompleted = normalSudokuCompleted,
        matchstickRiddlesSolved = matchstickRiddlesSolved,
        matchstickRiddlesTotal = matchstickRiddlesTotal,
        onOpenSettings = onOpenSettings,
        onPlayDaily = { controller.startDailySession() },
        onPlay = { controller.navigateToInstructions(it) },
        onViewScore = { controller.navigateToScoreboard(it) },
        onAchievements = { controller.navigateToAchievements() },
        onNormalSudoku = { controller.navigateToNormalSudokuMenu() },
        onNormalChess = { controller.navigateToNormalChessMenu() },
        onMatchstickRiddles = { controller.navigateToMatchstickRiddlesMenu() },
        onShowBrainCup = if (PlayGamesBridge.onShowBrainCup != null) {
            { controller.showBrainCup() }
        } else {
            null
        },
        useBuiltInSponsors = useBuiltInSponsors,
    )
}

@Composable
fun MainMenuScreenContent(
    totalXp: Int,
    sessionStreak: Int,
    sessionProgressIndex: Int,
    sessionTotalGames: Int,
    sessionCompletedToday: Boolean,
    highscores: ImmutableMap<String, Int>,
    unlockedCount: Int,
    normalSudokuCompleted: Int = 0,
    matchstickRiddlesSolved: Int = 0,
    matchstickRiddlesTotal: Int = 0,
    showDailyChallenge: Boolean = true,
    onOpenSettings: () -> Unit = {},
    onPlayDaily: () -> Unit = {},
    onPlay: (GameType) -> Unit = {},
    onViewScore: (GameType) -> Unit = {},
    onAchievements: () -> Unit = {},
    onNormalSudoku: () -> Unit = {},
    onNormalChess: () -> Unit = {},
    onMatchstickRiddles: () -> Unit = {},
    onShowBrainCup: (() -> Unit)? = null,
    useBuiltInSponsors: Boolean = false,
) {
    val builtInSponsorsSection = if (useBuiltInSponsors) rememberMainMenuSponsorsSection() else null
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val colorblindEnabled = LocalAccessiblePalette.current
    // Match Compose's UI locale (same source as stringResource), not a one-shot cached JVM default.
    val wordleAvailable = WordleLanguages.resolve(ComposeLocale.current.language) != null
    val visibleGameTypes = remember(colorblindEnabled, wordleAvailable) {
        GameType.displayOrder.filterNot {
            (colorblindEnabled && it.requiresColorVision) ||
                (it == GameType.WORDLE && !wordleAvailable)
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp + bottomInset),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header — full hero layout for first-run, compact for returning users
        item(span = { GridItemSpan(maxLineSpan) }, contentType = "header") {
            val settingsIcon: @Composable () -> Unit = {
                PrismTile(
                    face = Primary,
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .hoverHand()
                        .size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_settings_24),
                        contentDescription = stringResource(Res.string.settings_open),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            if (totalXp > 0) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(Res.drawable.ic_success),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.Center)
                            .height(154.dp),
                    )
                    Box(modifier = Modifier.align(Alignment.TopEnd)) {
                        settingsIcon()
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(Res.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Primary,
                        )
                        Spacer(Modifier.height(12.dp))
                        Image(
                            painterResource(Res.drawable.ic_success),
                            contentDescription = null,
                            modifier = Modifier.height(154.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(Res.string.app_tagline),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    Box(modifier = Modifier.align(Alignment.TopEnd)) {
                        settingsIcon()
                    }
                }
            }
        }

        // Player level card — hidden until the user has earned any XP
        if (totalXp > 0) {
            item(span = { GridItemSpan(maxLineSpan) }, contentType = "player_level") {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    PlayerLevelCard(
                        totalXp = totalXp,
                        onShowBrainCup = onShowBrainCup,
                        modifier = Modifier.widthIn(max = 420.dp),
                    )
                }
            }
        }

        // Daily challenge card — hidden once today's session is done
        if (showDailyChallenge && !sessionCompletedToday) {
            item(span = { GridItemSpan(maxLineSpan) }, contentType = "daily_challenge") {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    DailyChallengeCard(
                        sessionStreak = sessionStreak,
                        progressIndex = sessionProgressIndex,
                        totalGames = sessionTotalGames,
                        completedToday = sessionCompletedToday,
                        onPlay = onPlayDaily,
                        modifier = Modifier.widthIn(max = 420.dp),
                    )
                }
            }
        }

        // Game tiles, grouped by category
        items(
            visibleGameTypes,
            key = { it.id },
            contentType = { "game_tile" },
        ) { gameType ->
            GameTile(
                gameType = gameType,
                highscore = highscores[gameType.id] ?: 0,
                onPlay = { onPlay(gameType) },
                onViewScore = { onViewScore(gameType) },
            )
        }

        // Divider separating the mini games from the full-size "normal" games below.
        item(span = { GridItemSpan(maxLineSpan) }, contentType = "normal_divider") {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 2.dp,
                color = Primary.copy(alpha = 0.5f),
            )
        }

        // Full-size "normal" game entries, shown as square tiles alongside the mini games.
        item(contentType = "normal_sudoku") {
            NormalSudokuTile(
                completedCount = normalSudokuCompleted,
                onClick = onNormalSudoku,
            )
        }
        item(contentType = "normal_chess") {
            NormalChessTile(onClick = onNormalChess)
        }
        item(contentType = "matchstick_riddles") {
            MatchstickRiddlesTile(
                solvedCount = matchstickRiddlesSolved,
                total = matchstickRiddlesTotal,
                onClick = onMatchstickRiddles,
            )
        }

        // Footer
        item(span = { GridItemSpan(maxLineSpan) }, contentType = "footer") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(16.dp))

                PrismTile(
                    face = Primary,
                    modifier = Modifier
                        .hoverHand()
                        .height(56.dp),
                    onClick = onAchievements,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 12.dp),
                    ) {
                        PrismTrophy(
                            tint = MedalGold,
                            modifier = Modifier
                                .size(28.dp),
                        )
                        Text(
                            stringResource(Res.string.achievements_button, unlockedCount, UserStorage.Achievements.entries.size),
                            color = Color.White,
                        )
                        PrismTrophy(
                            tint = MedalGold,
                            modifier = Modifier
                                .size(28.dp),
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        item(
            key = "sponsors",
            span = { GridItemSpan(maxLineSpan) },
            contentType = "sponsors",
        ) {
            builtInSponsorsSection?.invoke()
        }
    }
}

@DevicePreviews
@Composable
private fun MainMenuScreenPreview() {
    ScreenPreviewHost {
        MainMenuScreenContent(
            totalXp = 1250,
            sessionStreak = 3,
            sessionProgressIndex = 1,
            sessionTotalGames = 4,
            sessionCompletedToday = false,
            highscores = persistentMapOf(),
            unlockedCount = 4,
            normalSudokuCompleted = 2,
            matchstickRiddlesSolved = 1,
            matchstickRiddlesTotal = 18,
            showDailyChallenge = true,
        )
    }
}
