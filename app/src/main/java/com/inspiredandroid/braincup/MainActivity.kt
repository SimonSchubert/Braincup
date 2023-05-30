package com.inspiredandroid.braincup

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.app.NavigationInterface
import com.inspiredandroid.braincup.challenge.ChallengeData
import com.inspiredandroid.braincup.composables.screens.AchievementsScreen
import com.inspiredandroid.braincup.composables.screens.AnomalyPuzzleScreen
import com.inspiredandroid.braincup.composables.screens.ChainCalculationScreen
import com.inspiredandroid.braincup.composables.screens.ColorConfusionScreen
import com.inspiredandroid.braincup.composables.screens.CorrectAnswerScreen
import com.inspiredandroid.braincup.composables.screens.CorrectChallengeAnswerScreen
import com.inspiredandroid.braincup.composables.screens.CreateChallengeMenuScreen
import com.inspiredandroid.braincup.composables.screens.CreateRiddleChallenge
import com.inspiredandroid.braincup.composables.screens.CreateSherlockCalculationChallenge
import com.inspiredandroid.braincup.composables.screens.FinishScreen
import com.inspiredandroid.braincup.composables.screens.FractionCalculationScreen
import com.inspiredandroid.braincup.composables.screens.InstructionsScreen
import com.inspiredandroid.braincup.composables.screens.MainMenuScreen
import com.inspiredandroid.braincup.composables.screens.MentalCalculationScreen
import com.inspiredandroid.braincup.composables.screens.PathFinderScreen
import com.inspiredandroid.braincup.composables.screens.RiddleScreen
import com.inspiredandroid.braincup.composables.screens.ScoreboardScreen
import com.inspiredandroid.braincup.composables.screens.SherlockCalculationScreen
import com.inspiredandroid.braincup.composables.screens.ValueComparisonScreen
import com.inspiredandroid.braincup.composables.screens.WrongAnswerScreen
import com.inspiredandroid.braincup.composables.screens.WrongChallengeAnswerScreen
import com.inspiredandroid.braincup.games.AnomalyPuzzleGame
import com.inspiredandroid.braincup.games.ChainCalculationGame
import com.inspiredandroid.braincup.games.ColorConfusionGame
import com.inspiredandroid.braincup.games.FractionCalculationGame
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.GridSolverGame
import com.inspiredandroid.braincup.games.MentalCalculationGame
import com.inspiredandroid.braincup.games.PathFinderGame
import com.inspiredandroid.braincup.games.RiddleGame
import com.inspiredandroid.braincup.games.SherlockCalculationGame
import com.inspiredandroid.braincup.games.ValueComparisonGame
import com.russhwolf.settings.SharedPreferencesSettings

class MainActivity : AppCompatActivity(), NavigationInterface {

    private val gameMaster = NavigationController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        settings = SharedPreferencesSettings(sharedPrefs)

        val challengeData = intent.getStringExtra("challenge")
        if (challengeData != null) {
            val challengeUrl = intent.getStringExtra("challengeUrl") ?: ""
            val challenge = ChallengeData.parse(challengeUrl, challengeData)
            gameMaster.start(
                state = AppState.CHALLENGE,
                challengeData = challenge
            )
        } else {
            gameMaster.start()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (gameMaster.state == AppState.START) {
                    finish()
                } else {
                    gameMaster.start()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        gameMaster.storage.putAppOpen()
    }

    override fun showMainMenu(
        title: String,
        description: String,
        games: List<GameType>,
        showInstructions: (GameType) -> Unit,
        showScore: (GameType) -> Unit,
        showAchievements: () -> Unit,
        createChallenge: () -> Unit,
        storage: UserStorage,
        totalScore: Int,
        appOpenCount: Int
    ) {
        setContent {
            MainMenuScreen(
                title,
                description,
                games,
                showInstructions,
                showScore,
                showAchievements,
                createChallenge,
                storage,
                totalScore,
                appOpenCount
            )
        }
    }

    override fun showInstructions(
        gameType: GameType,
        title: String,
        description: String,
        showChallengeInfo: Boolean,
        hasSecret: Boolean,
        start: () -> Unit
    ) {
        setContent {
            InstructionsScreen(
                title,
                description,
                showChallengeInfo,
                hasSecret,
                start,
                gameMaster
            )
        }
    }

    override fun showMentalCalculation(
        game: MentalCalculationGame,
        answer: (String) -> Unit
    ) {
        setContent {
            MentalCalculationScreen(game, answer)
        }
    }

    override fun showPathFinder(game: PathFinderGame, answer: (String) -> Unit) {
        setContent {
            PathFinderScreen(game, answer)
        }
    }

    override fun showRiddle(
        game: RiddleGame,
        title: String,
        answer: (String) -> Unit,

        ) {
        setContent {
            RiddleScreen(game, answer)
        }
    }

    override fun showGridSolver(
        game: GridSolverGame,
        answer: (String) -> Unit,

        ) {
        TODO("Not yet implemented")
    }

    override fun showScoreboard(
        gameType: GameType,
        highscore: Int,
        scores: List<Pair<String, List<Int>>>
    ) {
        setContent {
            ScoreboardScreen(
                gameType,
                highscore,
                scores,
                gameMaster
            )
        }
    }

    override fun showAchievements(
        allAchievements: List<UserStorage.Achievements>,
        unlockedAchievements: List<UserStorage.Achievements>
    ) {
        setContent {
            AchievementsScreen(
                allAchievements,
                unlockedAchievements,
                gameMaster
            )
        }
    }

    override fun showAnomalyPuzzle(
        game: AnomalyPuzzleGame,
        answer: (String) -> Unit,

        ) {
        setContent {
            AnomalyPuzzleScreen(
                game,
                answer
            )
        }
    }

    override fun showColorConfusion(
        game: ColorConfusionGame,
        answer: (String) -> Unit,

        ) {
        setContent {
            ColorConfusionScreen(game, answer)
        }
    }

    override fun showSherlockCalculation(
        game: SherlockCalculationGame,
        title: String,
        answer: (String) -> Unit,

        ) {
        setContent {
            SherlockCalculationScreen(
                game,
                answer
            )
        }
    }

    override fun showChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,

        ) {
        setContent {
            ChainCalculationScreen(
                game,
                answer
            )
        }
    }

    override fun showValueComparison(
        game: ValueComparisonGame,
        answer: (String) -> Unit,

        ) {
        setContent {
            ValueComparisonScreen(game, answer)
        }
    }

    override fun showFractionCalculation(
        game: FractionCalculationGame,
        answer: (String) -> Unit,

        ) {
        setContent {
            FractionCalculationScreen(game, answer)
        }
    }

    override fun showCorrectAnswerFeedback(gameType: GameType, hint: String?) {
        setContent {
            CorrectAnswerScreen(hint)
        }
    }

    override fun showWrongAnswerFeedback(gameType: GameType, solution: String) {
        setContent {
            WrongAnswerScreen(solution)
        }
    }

    override fun showCorrectChallengeAnswerFeedback(solution: String, secret: String, url: String) {
        setContent {
            CorrectChallengeAnswerScreen(this, solution, secret, url, gameMaster)
        }
    }

    override fun showWrongChallengeAnswerFeedback(url: String) {
        setContent {
            WrongChallengeAnswerScreen(this, url, gameMaster)
        }
    }

    override fun showCreateChallengeMenu(games: List<GameType>, answer: (GameType) -> Unit) {
        setContent {
            CreateChallengeMenuScreen(games, answer, gameMaster)
        }
    }

    override fun showCreateRiddleChallenge(title: String) {
        setContent {
            CreateRiddleChallenge(this, title, gameMaster)
        }
    }

    override fun showCreateSherlockCalculationChallenge(title: String, description: String) {
        setContent {
            CreateSherlockCalculationChallenge(this, title, gameMaster)
        }
    }

    override fun showFinishFeedback(
        gameType: GameType,
        rank: String,
        newHighscore: Boolean,
        answeredAllCorrect: Boolean,
        plays: Int,
        random: () -> Unit,
        again: () -> Unit
    ) {
        setContent {
            FinishScreen(
                rank,
                newHighscore,
                answeredAllCorrect,
                plays,
                random,
                again,
                gameMaster
            )
        }
    }
}

