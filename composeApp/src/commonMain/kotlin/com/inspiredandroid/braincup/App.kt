@file:OptIn(ExperimentalComposeUiApi::class)

package com.inspiredandroid.braincup

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import braincup.composeapp.generated.resources.Res
import com.inspiredandroid.braincup.api.PlayGamesBridge
import com.inspiredandroid.braincup.api.ReviewBridge
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.audio.rememberAudioPlayer
import com.inspiredandroid.braincup.games.getGameTypeById
import com.inspiredandroid.braincup.haptic.rememberHapticSuccess
import com.inspiredandroid.braincup.navigation.AppNavHost
import com.inspiredandroid.braincup.normalchess.NormalChessDifficulty
import com.inspiredandroid.braincup.normalchess.NormalChessMode
import com.inspiredandroid.braincup.ui.components.LocalNumberPadAscending
import com.inspiredandroid.braincup.ui.components.QuitGameDialog
import com.inspiredandroid.braincup.ui.screens.*
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.LocalAccessiblePalette
import com.inspiredandroid.braincup.ui.theme.OledColorScheme
import com.inspiredandroid.braincup.ui.theme.ThemeMode
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(
    systemColorSchemeProvider: ((dark: Boolean) -> ColorScheme)? = null,
    systemBarAppearance: @Composable (darkTheme: Boolean) -> Unit = {},
    useBuiltInSponsors: Boolean = false,
    onNavHostReady: suspend (NavController) -> Unit = {},
) {
    val navController = rememberNavController()
    val controller = remember(navController) { GameController(navController) }
    DisposableEffect(controller) {
        onDispose { controller.dispose() }
    }
    val audioPlayer = rememberAudioPlayer()

    var isMuted by remember { mutableStateOf(controller.storage.isAudioMuted()) }
    var colorblindPaletteEnabled by remember {
        mutableStateOf(controller.storage.isColorblindPaletteEnabled())
    }
    var hapticEnabled by remember { mutableStateOf(controller.storage.isHapticEnabled()) }
    var numberPadAscending by remember {
        mutableStateOf(controller.storage.isNumberPadAscending())
    }
    var themeMode by remember { mutableStateOf(controller.storage.getThemeMode()) }

    val systemDark = isSystemInDarkTheme()
    val resolvedColorScheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemColorSchemeProvider?.invoke(systemDark)
            ?: if (systemDark) DarkColorScheme else LightColorScheme
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.OLED -> OledColorScheme
    }

    // Drive the system bar icon brightness from the resolved theme rather than the OS setting, so an
    // explicit Light/Dark/OLED choice that differs from the device's dark-mode state still gets
    // legible status/navigation bar icons.
    val isDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.OLED -> true
    }

    var menuAudio by remember { mutableStateOf<ByteArray?>(null) }
    var gameAudio by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(Unit) {
        try {
            menuAudio = Res.readBytes("files/menu_ambient.wav")
        } catch (_: Exception) {
        }
        val opens = controller.storage.incrementAndGetTotalAppOpens()
        if (opens % 5 == 0) {
            ReviewBridge.requestInAppReview?.invoke()
        }
    }

    val currentEntry by navController.currentBackStackEntryAsState()
    val isPlayingGame = currentEntry?.destination?.hasRoute<Playing>() == true

    LaunchedEffect(isPlayingGame) {
        if (isPlayingGame && gameAudio == null) {
            try {
                gameAudio = Res.readBytes("files/game_focus.wav")
            } catch (_: Exception) {
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, audioPlayer, isMuted) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> if (!isMuted) audioPlayer.pause()
                Lifecycle.Event.ON_RESUME -> if (!isMuted) audioPlayer.resume()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(isPlayingGame, isMuted, menuAudio, gameAudio) {
        if (isMuted) {
            audioPlayer.stop()
            return@LaunchedEffect
        }
        val data = if (isPlayingGame) gameAudio else menuAudio
        if (data != null) {
            audioPlayer.play(data, loop = true)
        }
    }

    BraincupTheme(colorScheme = resolvedColorScheme) {
        systemBarAppearance(isDarkTheme)
        Surface(modifier = Modifier.fillMaxSize()) {
            CompositionLocalProvider(
                LocalAccessiblePalette provides colorblindPaletteEnabled,
                LocalNumberPadAscending provides numberPadAscending,
            ) {
                AppNavHost(navController = navController, startDestination = MainMenu) {
                    composable<MainMenu> {
                        val onOpenSettings = remember(controller) { { controller.navigateToSettings() } }
                        MainMenuScreen(
                            controller = controller,
                            onOpenSettings = onOpenSettings,
                            useBuiltInSponsors = useBuiltInSponsors,
                        )
                    }

                    composable<Settings> {
                        val onBackSettings = remember(controller) { { controller.navigateToMainMenu() } }
                        SettingsScreen(
                            isMuted = isMuted,
                            onToggleMute = {
                                isMuted = !isMuted
                                controller.storage.setAudioMuted(isMuted)
                            },
                            isColorblindPaletteEnabled = colorblindPaletteEnabled,
                            onToggleColorblindPalette = {
                                colorblindPaletteEnabled = !colorblindPaletteEnabled
                                controller.storage.setColorblindPaletteEnabled(colorblindPaletteEnabled)
                            },
                            isHapticEnabled = hapticEnabled,
                            onToggleHaptic = {
                                hapticEnabled = !hapticEnabled
                                controller.storage.setHapticEnabled(hapticEnabled)
                            },
                            isNumberPadAscending = numberPadAscending,
                            onToggleNumberPadAscending = {
                                numberPadAscending = !numberPadAscending
                                controller.storage.setNumberPadAscending(numberPadAscending)
                            },
                            themeMode = themeMode,
                            onThemeSelected = { mode ->
                                themeMode = mode
                                controller.storage.setThemeMode(mode)
                            },
                            onBack = onBackSettings,
                        )
                    }

                    composable<Instructions> { backStackEntry ->
                        val route: Instructions = backStackEntry.toRoute()
                        val gameType = getGameTypeById(route.gameTypeId)
                        if (gameType != null) {
                            val onStart = remember(controller, gameType) { { controller.startGame(gameType) } }
                            val onBackInstructions = remember(controller) { { controller.navigateToMainMenu() } }
                            val onShowLeaderboard = remember(controller, gameType) {
                                if (
                                    gameType.hasLeaderboard &&
                                    PlayGamesBridge.onShowLeaderboard != null
                                ) {
                                    { controller.showLeaderboard(gameType) }
                                } else {
                                    null
                                }
                            }
                            InstructionsScreen(
                                gameType = gameType,
                                storage = controller.storage,
                                onStart = onStart,
                                onBack = onBackInstructions,
                                onShowLeaderboard = onShowLeaderboard,
                            )
                        }
                    }

                    composable<Playing> {
                        // Intentionally do NOT collect timeRemaining/elapsedTime here: the progress
                        // bar collects those flows so a 100ms tick cannot restart game content.
                        val gameState by controller.gameState.collectAsStateWithLifecycle()
                        val gameUiState by controller.gameUiState.collectAsStateWithLifecycle()
                        val hapticSuccess = rememberHapticSuccess()
                        val onAnswer = remember(controller) { { answer: String -> controller.submitAnswer(answer) } }
                        val onGiveUp = remember(controller) { { controller.giveUp() } }
                        val onWordleFinished = remember(controller) { { controller.wordleFinishedAction() } }
                        val navigateHome = remember(controller) { { controller.navigateToMainMenu() } }

                        LaunchedEffect(controller, hapticEnabled) {
                            controller.intermediateCorrectEvents.collect {
                                if (hapticEnabled) hapticSuccess()
                            }
                        }

                        when (val state = gameState) {
                            is GameState.Active -> {
                                val uiState = gameUiState ?: return@composable
                                var showQuitDialog by remember { mutableStateOf(false) }
                                val confirmBeforeQuit = shouldConfirmQuit(uiState)
                                val onBackFromGame = remember(confirmBeforeQuit, navigateHome) {
                                    {
                                        if (confirmBeforeQuit) {
                                            showQuitDialog = true
                                        } else {
                                            navigateHome()
                                        }
                                    }
                                }

                                BackHandler {
                                    if (showQuitDialog) {
                                        showQuitDialog = false
                                    } else {
                                        onBackFromGame()
                                    }
                                }

                                LaunchedEffect(showQuitDialog) {
                                    if (showQuitDialog) {
                                        controller.pauseTimers()
                                    } else {
                                        controller.resumeTimers()
                                    }
                                }

                                GameScreen(
                                    gameUiState = uiState,
                                    timeRemaining = controller.timeRemaining,
                                    elapsedTime = controller.elapsedTime,
                                    onAnswer = onAnswer,
                                    onGiveUp = onGiveUp,
                                    onBack = onBackFromGame,
                                    inSessionMode = controller.isInSessionMode,
                                    isTimerPaused = showQuitDialog,
                                    onWordleFinishedAction = onWordleFinished,
                                    orbitBallPositions = controller.orbitBallPositions,
                                    bubbleSumFrames = controller.bubbleSumFrames,
                                )

                                if (showQuitDialog) {
                                    QuitGameDialog(
                                        onDismiss = { showQuitDialog = false },
                                        onQuit = {
                                            showQuitDialog = false
                                            navigateHome()
                                        },
                                    )
                                }
                            }

                            is GameState.Feedback -> {
                                LaunchedEffect(state) {
                                    if (state.isCorrect && hapticEnabled) hapticSuccess()
                                }
                                AnswerFeedbackScreen(
                                    isCorrect = state.isCorrect,
                                    message = state.message,
                                )
                            }

                            is GameState.Idle -> {
                                // Game not active, navigating away
                            }
                        }
                    }

                    composable<Finish> { backStackEntry ->
                        val route: Finish = backStackEntry.toRoute()
                        val gameType = getGameTypeById(route.gameTypeId)
                        if (gameType != null) {
                            val onPlayRandom = remember(controller) { { controller.playRandomGame() } }
                            val onPlayAgain = remember(controller, gameType) { { controller.playAgain(gameType) } }
                            val onMenu = remember(controller) { { controller.navigateToMainMenu() } }
                            FinishScreen(
                                gameType = gameType,
                                score = route.score,
                                isNewHighscore = route.isNewHighscore,
                                answeredAllCorrect = route.answeredAllCorrect,
                                highscore = route.highscore,
                                xpGained = route.xpGained,
                                totalXpAfter = route.totalXpAfter,
                                difficultyBonus = route.difficultyBonus,
                                onPlayRandom = onPlayRandom,
                                onPlayAgain = onPlayAgain,
                                onMenu = onMenu,
                            )
                        }
                    }

                    composable<Scoreboard> { backStackEntry ->
                        val route: Scoreboard = backStackEntry.toRoute()
                        val gameType = getGameTypeById(route.gameTypeId)
                        if (gameType != null) {
                            val onBackScoreboard = remember(controller) { { controller.navigateToMainMenu() } }
                            ScoreboardScreen(
                                gameType = gameType,
                                storage = controller.storage,
                                onBack = onBackScoreboard,
                            )
                        }
                    }

                    composable<Achievements> {
                        val onBackAchievements = remember(controller) { { controller.navigateToMainMenu() } }
                        AchievementsScreen(
                            storage = controller.storage,
                            onBack = onBackAchievements,
                        )
                    }

                    composable<NormalSudokuMenu> {
                        val onPuzzleSelected = remember(controller) {
                            { id: String -> controller.navigateToNormalSudokuPlay(id) }
                        }
                        val onBackSudokuMenu = remember(controller) { { controller.navigateToMainMenu() } }
                        NormalSudokuMenuScreen(
                            storage = controller.storage,
                            onPuzzleSelected = onPuzzleSelected,
                            onBack = onBackSudokuMenu,
                        )
                    }

                    composable<NormalSudokuPlay> { backStackEntry ->
                        val route: NormalSudokuPlay = backStackEntry.toRoute()
                        val popSudokuMenu = remember(navController) {
                            {
                                navController.popBackStack(NormalSudokuMenu, inclusive = false)
                                Unit
                            }
                        }
                        NormalSudokuPlayScreen(
                            puzzleId = route.puzzleId,
                            storage = controller.storage,
                            onCompleted = popSudokuMenu,
                            onBack = popSudokuMenu,
                        )
                    }

                    composable<NormalChessMenu> {
                        val onStartChess = remember(controller) {
                            { mode: NormalChessMode, difficulty: NormalChessDifficulty ->
                                controller.navigateToNormalChessPlay(mode, difficulty)
                            }
                        }
                        val onBackChessMenu = remember(controller) { { controller.navigateToMainMenu() } }
                        NormalChessMenuScreen(
                            storage = controller.storage,
                            onStart = onStartChess,
                            onBack = onBackChessMenu,
                        )
                    }

                    composable<NormalChessPlay> { backStackEntry ->
                        val route: NormalChessPlay = backStackEntry.toRoute()
                        val mode = NormalChessMode.entries.firstOrNull { it.name == route.mode }
                            ?: NormalChessMode.VS_CPU
                        val difficulty = NormalChessDifficulty.entries.firstOrNull { it.name == route.difficulty }
                            ?: NormalChessDifficulty.MEDIUM
                        val onBackChessPlay = remember(navController) {
                            {
                                navController.popBackStack(NormalChessMenu, inclusive = false)
                                Unit
                            }
                        }
                        NormalChessPlayScreen(
                            mode = mode,
                            difficulty = difficulty,
                            storage = controller.storage,
                            onBack = onBackChessPlay,
                        )
                    }

                    composable<MatchstickRiddlesMenu> {
                        val onRiddleSelected = remember(controller) {
                            { id: String -> controller.navigateToMatchstickRiddlesPlay(id) }
                        }
                        val onBackMatchstickMenu = remember(controller) { { controller.navigateToMainMenu() } }
                        MatchstickRiddlesMenuScreen(
                            storage = controller.storage,
                            onRiddleSelected = onRiddleSelected,
                            onBack = onBackMatchstickMenu,
                        )
                    }

                    composable<MatchstickRiddlesPlay> { backStackEntry ->
                        val route: MatchstickRiddlesPlay = backStackEntry.toRoute()
                        val popMatchstickMenu = remember(navController) {
                            {
                                navController.popBackStack(MatchstickRiddlesMenu, inclusive = false)
                                Unit
                            }
                        }
                        MatchstickRiddlesPlayScreen(
                            riddleId = route.riddleId,
                            storage = controller.storage,
                            onCompleted = popMatchstickMenu,
                            onBack = popMatchstickMenu,
                        )
                    }

                    composable<PegSolitaire> {
                        val onBackPegSolitaire = remember(controller) { { controller.navigateToMainMenu() } }
                        PegSolitairePlayScreen(
                            storage = controller.storage,
                            onBack = onBackPegSolitaire,
                        )
                    }

                    composable<SessionInterstitial> {
                        val session by controller.sessionState.collectAsStateWithLifecycle()
                        val current = session
                        if (current != null) {
                            val nextGameId = current.gameIds.getOrNull(current.currentIndex)
                            val nextGame = nextGameId?.let { getGameTypeById(it) }
                            if (nextGame != null) {
                                val onContinue = remember(controller) { { controller.playNextSessionGame() } }
                                val onExit = remember(controller) { { controller.navigateToMainMenu() } }
                                SessionInterstitialScreen(
                                    nextGame = nextGame,
                                    nextGameIndex = current.currentIndex,
                                    totalGames = current.gameIds.size,
                                    runningTotal = current.gameIds.zip(current.scores).filter { (id, _) ->
                                        getGameTypeById(id)?.lowerScoreIsBetter != true
                                    }.sumOf { (_, score) -> score },
                                    onContinue = onContinue,
                                    onExit = onExit,
                                )
                            }
                        }
                    }

                    composable<SessionComplete> {
                        val result by controller.lastCompletedSession.collectAsStateWithLifecycle()
                        val current = result
                        if (current != null) {
                            val onDone = remember(controller) { { controller.navigateToMainMenu() } }
                            SessionCompleteScreen(
                                gameIds = current.gameIds.toImmutableList(),
                                scores = current.scores.toImmutableList(),
                                streakBefore = current.streakBefore,
                                streakAfter = current.streakAfter,
                                xpGained = current.xpGained,
                                levelChange = current.levelChange,
                                onDone = onDone,
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }
}

/**
 * Games that record the score as soon as they end can leave without a prompt once finished:
 * Wordle when the puzzle is over, MiniChess when the game has an outcome.
 */
private fun shouldConfirmQuit(gameUiState: GameUiState): Boolean = when (gameUiState) {
    is WordleUiState -> !gameUiState.finished
    is MiniChessUiState -> gameUiState.outcome == null
    else -> true
}
