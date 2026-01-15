package com.inspiredandroid.braincup

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.inspiredandroid.braincup.app.GameController
import com.inspiredandroid.braincup.app.Screen
import com.inspiredandroid.braincup.ui.screens.*
import com.inspiredandroid.braincup.ui.theme.BraincupTheme

@Composable
fun App() {
    val controller = remember { GameController() }
    val currentScreen by controller.currentScreen.collectAsState()
    val timeRemaining by controller.timeRemaining.collectAsState()

    BraincupTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (val screen = currentScreen) {
                is Screen.MainMenu -> {
                    MainMenuScreen(controller = controller)
                }

                is Screen.Instructions -> {
                    InstructionsScreen(
                        gameType = screen.gameType,
                        onStart = { controller.startGame(screen.gameType) },
                        onBack = { controller.navigateToMainMenu() }
                    )
                }

                is Screen.Playing -> {
                    GameScreen(
                        game = screen.game,
                        timeRemaining = timeRemaining,
                        onAnswer = { controller.submitAnswer(it) },
                        onBack = { controller.navigateToMainMenu() }
                    )
                }

                is Screen.AnswerFeedback -> {
                    AnswerFeedbackScreen(
                        isCorrect = screen.isCorrect,
                        message = screen.message
                    )
                }

                is Screen.Finish -> {
                    FinishScreen(
                        gameType = screen.gameType,
                        score = screen.score,
                        isNewHighscore = screen.isNewHighscore,
                        answeredAllCorrect = screen.answeredAllCorrect,
                        onPlayRandom = { controller.playRandomGame() },
                        onPlayAgain = { controller.playAgain(screen.gameType) },
                        onMenu = { controller.navigateToMainMenu() }
                    )
                }

                is Screen.Scoreboard -> {
                    ScoreboardScreen(
                        gameType = screen.gameType,
                        storage = controller.storage,
                        onBack = { controller.navigateToMainMenu() }
                    )
                }

                is Screen.Achievements -> {
                    AchievementsScreen(
                        storage = controller.storage,
                        onBack = { controller.navigateToMainMenu() }
                    )
                }
            }
        }
    }
}
