package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.PlayGamesBridge
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.GameController
import com.inspiredandroid.braincup.games.GameCategory
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.iqtest.IqScoring
import com.inspiredandroid.braincup.games.wordle.WordleLanguages
import com.inspiredandroid.braincup.learn.LearnTopicProgress
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.matchstickriddles.MatchstickRiddles
import com.inspiredandroid.braincup.rememberMainMenuSponsorsSection
import com.inspiredandroid.braincup.ui.components.DailyChallengeCard
import com.inspiredandroid.braincup.ui.components.GameTile
import com.inspiredandroid.braincup.ui.components.IqTestTile
import com.inspiredandroid.braincup.ui.components.LearnTopicTile
import com.inspiredandroid.braincup.ui.components.MatchstickRiddlesTile
import com.inspiredandroid.braincup.ui.components.MenuSectionHeader
import com.inspiredandroid.braincup.ui.components.NormalChessTile
import com.inspiredandroid.braincup.ui.components.NormalSudokuTile
import com.inspiredandroid.braincup.ui.components.PegSolitaireTile
import com.inspiredandroid.braincup.ui.components.PlayerLevelCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.PrismTrophy
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.LearnSectionAccent
import com.inspiredandroid.braincup.ui.theme.LocalAccessiblePalette
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.UntimedSectionAccent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.intl.Locale as ComposeLocale

/** Width the settings button occupies in the header, kept clear so the title cannot run under it. */
private val SettingsIconClearance = 56.dp

/** Grid metrics, shared by the layout and by the column count the collapsed sections are sized in. */
private val MenuMinTileSize = 150.dp
private val MenuTileSpacing = 12.dp
private val MenuHorizontalPadding = 16.dp

@Composable
fun MainMenuScreen(
    controller: GameController,
    onOpenSettings: () -> Unit = {},
    onIqTest: () -> Unit = {},
    useBuiltInSponsors: Boolean = false,
) {
    val sessionState by controller.sessionState.collectAsStateWithLifecycle()
    val sessionStreak by controller.sessionStreak.collectAsStateWithLifecycle()
    val session = sessionState
    val totalGames = session?.gameIds?.size ?: UserStorage.SESSION_GAME_COUNT
    val progressIndex = session?.currentIndex ?: 0
    val completedToday = remember(session) { controller.storage.isSessionCompletedToday() }

    val totalXp by controller.totalXp.collectAsStateWithLifecycle()
    val highscores by controller.highscores.collectAsStateWithLifecycle()
    // Copied once per highscore change rather than on every recomposition of the menu: the
    // conversion walks all ~40 entries and the menu recomposes on XP, streak and session ticks.
    val immutableHighscores = remember(highscores) { highscores.toImmutableMap() }
    val unlockedCount by controller.unlockedAchievementCount.collectAsStateWithLifecycle()
    val storageRevision by controller.storageRevision.collectAsStateWithLifecycle()
    val normalSudokuCompleted = remember(controller, storageRevision) {
        controller.storage.getCompletedNormalSudokuIds().size
    }
    val matchstickRiddlesSolved = remember(controller, storageRevision) {
        controller.storage.getSolvedMatchstickRiddleIds().size
    }
    val matchstickRiddlesTotal = remember { MatchstickRiddles.all.size }
    val bestIq = remember(controller, storageRevision) {
        controller.storage.getBestIqTestRawScore()?.let { IqScoring.iqFor(it) }
    }
    val learnProgress = remember(controller, storageRevision) {
        controller.storage.getAllLearnTopicProgress().toImmutableList()
    }

    val onPlayDaily = remember(controller) { { controller.startDailySession() } }
    val onPlay = remember(controller) { { gameType: GameType -> controller.navigateToInstructions(gameType) } }
    val onViewScore = remember(controller) { { gameType: GameType -> controller.navigateToScoreboard(gameType) } }
    val onAchievements = remember(controller) { { controller.navigateToAchievements() } }
    val onNormalSudoku = remember(controller) { { controller.navigateToNormalSudokuMenu() } }
    val onNormalChess = remember(controller) { { controller.navigateToNormalChessMenu() } }
    val onMatchstickRiddles = remember(controller) { { controller.navigateToMatchstickRiddlesMenu() } }
    val onPegSolitaire = remember(controller) { { controller.navigateToPegSolitaire() } }
    val onLearnTopic = remember(controller) { { topic: MathTopic -> controller.navigateToLearnTopic(topic) } }
    val onShowBrainCup = remember(controller) {
        if (PlayGamesBridge.onShowBrainCup != null) {
            { controller.showBrainCup() }
        } else {
            null
        }
    }

    MainMenuScreenContent(
        totalXp = totalXp,
        sessionStreak = sessionStreak,
        sessionProgressIndex = progressIndex,
        sessionTotalGames = totalGames,
        sessionCompletedToday = completedToday,
        highscores = immutableHighscores,
        unlockedCount = unlockedCount,
        normalSudokuCompleted = normalSudokuCompleted,
        matchstickRiddlesSolved = matchstickRiddlesSolved,
        matchstickRiddlesTotal = matchstickRiddlesTotal,
        bestIq = bestIq,
        onOpenSettings = onOpenSettings,
        onIqTest = onIqTest,
        onPlayDaily = onPlayDaily,
        onPlay = onPlay,
        onViewScore = onViewScore,
        onAchievements = onAchievements,
        onNormalSudoku = onNormalSudoku,
        onNormalChess = onNormalChess,
        onMatchstickRiddles = onMatchstickRiddles,
        onPegSolitaire = onPegSolitaire,
        learnProgress = learnProgress,
        onLearnTopic = onLearnTopic,
        onShowBrainCup = onShowBrainCup,
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
    bestIq: Int? = null,
    showDailyChallenge: Boolean = true,
    /**
     * Optional override for which mini-game tiles appear (and in which order).
     * Used by store screenshots to hand-pick a compact portrait lineup; null = full menu.
     */
    gameTypes: ImmutableList<GameType>? = null,
    onOpenSettings: () -> Unit = {},
    onIqTest: () -> Unit = {},
    onPlayDaily: () -> Unit = {},
    onPlay: (GameType) -> Unit = {},
    onViewScore: (GameType) -> Unit = {},
    onAchievements: () -> Unit = {},
    onNormalSudoku: () -> Unit = {},
    onNormalChess: () -> Unit = {},
    onMatchstickRiddles: () -> Unit = {},
    onPegSolitaire: () -> Unit = {},
    /** Learn section state, one entry per topic. Empty hides the section (store screenshots). */
    learnProgress: ImmutableList<LearnTopicProgress> = persistentListOf(),
    onLearnTopic: (MathTopic) -> Unit = {},
    onShowBrainCup: (() -> Unit)? = null,
    useBuiltInSponsors: Boolean = false,
    /** Hoisted so the caller can restore or drive the scroll position. */
    gridState: LazyGridState = rememberLazyGridState(),
) {
    val builtInSponsorsSection = if (useBuiltInSponsors) rememberMainMenuSponsorsSection() else null
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val colorblindEnabled = LocalAccessiblePalette.current
    // Match Compose's UI locale (same source as stringResource), not a one-shot cached JVM default.
    val wordleAvailable = WordleLanguages.resolve(ComposeLocale.current.language) != null
    val visibleGameTypes = remember(colorblindEnabled, wordleAvailable, gameTypes) {
        val base = gameTypes ?: GameType.displayOrder
        base.filterNot {
            (colorblindEnabled && it.requiresColorVision) ||
                (it == GameType.WORDLE && !wordleAvailable)
        }
    }

    val untimedGames = remember(visibleGameTypes) {
        visibleGameTypes.filter { it.listedAsUntimed }
    }
    val gamesByCategory = remember(visibleGameTypes) {
        GameCategory.entries
            .map { category ->
                category to visibleGameTypes.filter { it.category == category && !it.listedAsUntimed }
            }
            .filter { (_, games) -> games.isNotEmpty() }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = MenuMinTileSize),
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = MenuHorizontalPadding),
        contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp + bottomInset),
        horizontalArrangement = Arrangement.spacedBy(MenuTileSpacing),
        verticalArrangement = Arrangement.spacedBy(MenuTileSpacing),
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
                        painter = painterResource(Res.drawable.ic_mascot),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.Center)
                            .height(160.dp),
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
                            textAlign = TextAlign.Center,
                            // The name is a wordmark, so it shrinks to fit rather than wrapping:
                            // at a large font scale it no longer fits the line, and "Brainc" over
                            // "up" reads as damage rather than as a logo. The padding keeps it
                            // clear of the settings button pinned to the same box, which it had
                            // grown wide enough to run underneath.
                            maxLines = 1,
                            autoSize = TextAutoSize.StepBased(
                                minFontSize = 16.sp,
                                maxFontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            ),
                            modifier = Modifier.padding(horizontal = SettingsIconClearance),
                        )
                        Spacer(Modifier.height(12.dp))
                        Image(
                            painterResource(Res.drawable.ic_mascot),
                            contentDescription = null,
                            modifier = Modifier.height(150.dp),
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
                        modifier = Modifier.widthIn(max = ContentMaxWidth),
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
                        modifier = Modifier.widthIn(max = ContentMaxWidth),
                    )
                }
            }
        }

        if (gameTypes != null) {
            // Store screenshots hand-pick a short lineup and want it flat: filter chips and
            // section headings would push the chosen tiles off the frame.
            items(
                visibleGameTypes,
                key = { it.id },
                contentType = { "game_tile" },
            ) { gameType ->
                GameTile(
                    gameType = gameType,
                    highscore = highscores[gameType.id] ?: 0,
                    onPlay = onPlay,
                    onViewScore = onViewScore,
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }, contentType = "normal_divider") {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    thickness = 2.dp,
                    color = Primary.copy(alpha = 0.5f),
                )
            }
            item(contentType = "iq_test") {
                IqTestTile(bestIq = bestIq, onClick = onIqTest)
            }
            item(contentType = "normal_chess") {
                NormalChessTile(onClick = onNormalChess)
            }
            item(contentType = "normal_sudoku") {
                NormalSudokuTile(completedCount = normalSudokuCompleted, onClick = onNormalSudoku)
            }
            item(contentType = "matchstick_riddles") {
                MatchstickRiddlesTile(
                    solvedCount = matchstickRiddlesSolved,
                    total = matchstickRiddlesTotal,
                    onClick = onMatchstickRiddles,
                )
            }
            item(contentType = "peg_solitaire") {
                PegSolitaireTile(onClick = onPegSolitaire)
            }
        } else {
            // One section per skill, every game in it on screen. The categories already drove the
            // ordering here; naming them and pinning the name is what turns a run of 39 tiles into
            // something you can keep your place in.
            gamesByCategory.forEach { (category, games) ->
                stickyHeader(key = "header-${category.name}", contentType = "section_header") {
                    MenuSectionHeader(
                        title = stringResource(category.displayNameRes),
                        accentColor = Color(category.accentColor),
                    )
                }
                items(
                    games,
                    key = { it.id },
                    contentType = { "game_tile" },
                ) { gameType ->
                    GameTile(
                        gameType = gameType,
                        highscore = highscores[gameType.id] ?: 0,
                        onPlay = onPlay,
                        onViewScore = onViewScore,
                    )
                }
            }

            // The untimed games, under their own heading rather than behind a bare divider. What
            // they share is not size but pace: a body of content to work through, at whatever
            // speed, rather than a sixty second drill. The IQ test in particular was going
            // undiscovered at the bottom of the mini-game grid.
            stickyHeader(key = "header-untimed", contentType = "section_header") {
                MenuSectionHeader(
                    title = stringResource(Res.string.menu_untimed_title),
                    accentColor = UntimedSectionAccent,
                )
            }
            item(contentType = "untimed_tile") {
                IqTestTile(bestIq = bestIq, onClick = onIqTest)
            }
            item(contentType = "untimed_tile") {
                NormalChessTile(onClick = onNormalChess)
            }
            item(contentType = "untimed_tile") {
                NormalSudokuTile(completedCount = normalSudokuCompleted, onClick = onNormalSudoku)
            }
            item(contentType = "untimed_tile") {
                MatchstickRiddlesTile(
                    solvedCount = matchstickRiddlesSolved,
                    total = matchstickRiddlesTotal,
                    onClick = onMatchstickRiddles,
                )
            }
            item(contentType = "untimed_tile") {
                PegSolitaireTile(onClick = onPegSolitaire)
            }
            // Real [GameType]s that belong here by pace rather than by skill. They keep their
            // medal and highscore, and take this section's taller tile so the row stays even.
            items(
                untimedGames,
                key = { it.id },
                contentType = { "untimed_tile" },
            ) { gameType ->
                GameTile(
                    gameType = gameType,
                    highscore = highscores[gameType.id] ?: 0,
                    onPlay = onPlay,
                    onViewScore = onViewScore,
                    untimedStyle = true,
                )
            }

            // Learn keeps the beta note in its subtitle: that is a warning about the content, not
            // a caption, so it is the one heading here that still carries one.
            if (learnProgress.isNotEmpty()) {
                stickyHeader(key = "header-learn", contentType = "section_header") {
                    MenuSectionHeader(
                        title = stringResource(Res.string.learn_section_title),
                        accentColor = LearnSectionAccent,
                        subtitle = stringResource(Res.string.learn_section_subtitle),
                    )
                }
                items(
                    learnProgress,
                    key = { "learn-${it.topic.id}" },
                    contentType = { "learn_tile" },
                ) { topicProgress ->
                    LearnTopicTile(
                        topic = topicProgress.topic,
                        certificates = topicProgress.certificates,
                        unitsTotal = topicProgress.unitsTotal,
                        onClick = onLearnTopic,
                    )
                }
            }
        }

        // Footer
        item(span = { GridItemSpan(maxLineSpan) }, contentType = "footer") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(16.dp))

                PrismTile(
                    face = Primary,
                    modifier = Modifier
                        .hoverHand()
                        // A minimum rather than a fixed height: the label carries the medal count,
                        // and at a large font scale a fixed 56dp cut it off mid-glyph and took the
                        // count with it.
                        .defaultMinSize(minHeight = 56.dp),
                    onClick = onAchievements,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        PrismTrophy(
                            tint = MedalGold,
                            modifier = Modifier
                                .size(28.dp),
                        )
                        Text(
                            stringResource(Res.string.achievements_button, unlockedCount, UserStorage.Achievements.entries.size),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f, fill = false),
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
            learnProgress = MathTopic.entries.map { LearnTopicProgress.empty(it) }.toImmutableList(),
        )
    }
}
