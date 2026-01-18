@file:OptIn(ExperimentalComposeUiApi::class)

package com.inspiredandroid.braincup

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.screens.*
import com.inspiredandroid.braincup.ui.theme.BraincupTheme

@Composable
fun App() {
    val navController = rememberNavController()
    val controller = remember(navController) { GameController(navController) }

    BraincupTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = MainMenu) {
                composable<MainMenu> {
                    MainMenuScreen(controller = controller)
                }

                composable<Instructions> { backStackEntry ->
                    val route: Instructions = backStackEntry.toRoute()
                    val gameType = GameController.getGameTypeById(route.gameTypeId)
                    if (gameType != null) {
                        InstructionsScreen(
                            gameType = gameType,
                            onStart = { controller.startGame(gameType) },
                            onBack = { controller.navigateToMainMenu() },
                        )
                    }
                }

                composable<Playing> { backStackEntry ->
                    val route: Playing = backStackEntry.toRoute()
                    val gameType = GameController.getGameTypeById(route.gameTypeId)
                    val gameState by controller.gameState.collectAsState()
                    val timeRemaining by controller.timeRemaining.collectAsState()

                    when (val state = gameState) {
                        is GameState.Active -> {
                            GameScreen(
                                game = state.game,
                                timeRemaining = timeRemaining,
                                onAnswer = {
                                    if (gameType == GameType.VISUAL_MEMORY) {
                                        controller.submitVisualMemoryAnswer(it)
                                    } else {
                                        controller.submitAnswer(it)
                                    }
                                },
                                onGiveUp = { controller.giveUp() },
                                onBack = { controller.navigateToMainMenu() },
                            )
                        }

                        is GameState.Feedback -> {
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
                    val gameType = GameController.getGameTypeById(route.gameTypeId)
                    if (gameType != null) {
                        FinishScreen(
                            gameType = gameType,
                            score = route.score,
                            isNewHighscore = route.isNewHighscore,
                            answeredAllCorrect = route.answeredAllCorrect,
                            onPlayRandom = { controller.playRandomGame() },
                            onPlayAgain = { controller.playAgain(gameType) },
                            onMenu = { controller.navigateToMainMenu() },
                        )
                    }
                }

                composable<Scoreboard> { backStackEntry ->
                    val route: Scoreboard = backStackEntry.toRoute()
                    val gameType = GameController.getGameTypeById(route.gameTypeId)
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
            }
        }
    }
}
