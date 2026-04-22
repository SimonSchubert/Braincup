@file:OptIn(ExperimentalComposeUiApi::class)

package com.inspiredandroid.braincup

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import braincup.composeapp.generated.resources.Res
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.audio.rememberAudioPlayer
import com.inspiredandroid.braincup.games.getGameTypeById
import com.inspiredandroid.braincup.haptic.rememberHapticSuccess
import com.inspiredandroid.braincup.navigation.AppNavHost
import com.inspiredandroid.braincup.ui.screens.*
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(colorScheme: ColorScheme? = null) {
    val resolvedColorScheme = colorScheme
        ?: if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    val navController = rememberNavController()
    val controller = remember(navController) { GameController(navController) }
    DisposableEffect(controller) {
        onDispose { controller.dispose() }
    }
    val audioPlayer = rememberAudioPlayer()

    var isMuted by remember { mutableStateOf(controller.storage.isAudioMuted()) }

    var menuAudio by remember { mutableStateOf<ByteArray?>(null) }
    var gameAudio by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(Unit) {
        try {
            menuAudio = Res.readBytes("files/menu_ambient.wav")
        } catch (_: Exception) {
        }
        try {
            gameAudio = Res.readBytes("files/game_focus.wav")
        } catch (_: Exception) {
        }
    }

    val currentEntry by navController.currentBackStackEntryAsState()
    val isPlayingGame = currentEntry?.destination?.route?.contains("Playing") == true

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
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavHost(navController = navController, startDestination = MainMenu) {
                composable<MainMenu> {
                    MainMenuScreen(
                        controller = controller,
                        isMuted = isMuted,
                        onToggleMute = {
                            isMuted = !isMuted
                            controller.storage.setAudioMuted(isMuted)
                        },
                    )
                }

                composable<Instructions> { backStackEntry ->
                    val route: Instructions = backStackEntry.toRoute()
                    val gameType = getGameTypeById(route.gameTypeId)
                    if (gameType != null) {
                        InstructionsScreen(
                            gameType = gameType,
                            onStart = { controller.startGame(gameType) },
                            onBack = { controller.navigateToMainMenu() },
                        )
                    }
                }

                composable<Playing> {
                    val gameState by controller.gameState.collectAsState()
                    val timeRemaining by controller.timeRemaining.collectAsState()
                    val gameUiState by controller.gameUiState.collectAsState()
                    val hapticSuccess = rememberHapticSuccess()

                    LaunchedEffect(controller) {
                        controller.intermediateCorrectEvents.collect { hapticSuccess() }
                    }

                    when (val state = gameState) {
                        is GameState.Active -> {
                            val uiState = gameUiState ?: return@composable
                            GameScreen(
                                gameUiState = uiState,
                                timeRemaining = timeRemaining,
                                onAnswer = { controller.submitAnswer(it) },
                                onGiveUp = { controller.giveUp() },
                                onBack = { controller.navigateToMainMenu() },
                            )
                        }

                        is GameState.Feedback -> {
                            LaunchedEffect(state) {
                                if (state.isCorrect) hapticSuccess()
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
                        FinishScreen(
                            gameType = gameType,
                            score = route.score,
                            isNewHighscore = route.isNewHighscore,
                            answeredAllCorrect = route.answeredAllCorrect,
                            highscore = route.highscore,
                            xpGained = route.xpGained,
                            totalXpAfter = route.totalXpAfter,
                            onPlayRandom = { controller.playRandomGame() },
                            onPlayAgain = { controller.playAgain(gameType) },
                            onMenu = { controller.navigateToMainMenu() },
                        )
                    }
                }

                composable<Scoreboard> { backStackEntry ->
                    val route: Scoreboard = backStackEntry.toRoute()
                    val gameType = getGameTypeById(route.gameTypeId)
                    if (gameType != null) {
                        ScoreboardScreen(
                            gameType = gameType,
                            storage = controller.storage,
                            onBack = { controller.navigateToMainMenu() },
                        )
                    }
                }

                composable<Achievements> {
                    AchievementsScreen(
                        storage = controller.storage,
                        onBack = { controller.navigateToMainMenu() },
                    )
                }

                composable<SessionInterstitial> {
                    val session by controller.sessionState.collectAsState()
                    val current = session
                    if (current != null) {
                        val nextGameId = current.gameIds.getOrNull(current.currentIndex)
                        val nextGame = nextGameId?.let { getGameTypeById(it) }
                        if (nextGame != null) {
                            SessionInterstitialScreen(
                                nextGame = nextGame,
                                nextGameIndex = current.currentIndex,
                                totalGames = current.gameIds.size,
                                runningTotal = current.scores.sum(),
                                onContinue = { controller.playNextSessionGame() },
                                onExit = { controller.navigateToMainMenu() },
                            )
                        }
                    }
                }

                composable<SessionComplete> {
                    val result by controller.lastCompletedSession.collectAsState()
                    val current = result
                    if (current != null) {
                        SessionCompleteScreen(
                            gameIds = current.gameIds,
                            scores = current.scores,
                            streakBefore = current.streakBefore,
                            streakAfter = current.streakAfter,
                            xpGained = current.xpGained,
                            levelChange = current.levelChange,
                            onDone = { controller.navigateToMainMenu() },
                        )
                    }
                }
            }
        }
    }
}
