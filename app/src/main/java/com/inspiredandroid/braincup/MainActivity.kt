package com.inspiredandroid.braincup

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.app.NavigationInterface
import com.inspiredandroid.braincup.challenge.ChallengeData
import com.inspiredandroid.braincup.composables.*
import com.inspiredandroid.braincup.games.*
import com.russhwolf.settings.AndroidSettings

class MainActivity : AppCompatActivity(), NavigationInterface {

    private val gameMaster = NavigationController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        settings = AndroidSettings(sharedPrefs)

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
    }

    override fun onBackPressed() {
        if (gameMaster.state == AppState.START) {
            super.onBackPressed()
        } else {
            gameMaster.start()
        }
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
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            MentalCalculationScreen(game, answer, next)
        }
    }

    override fun showPathFinder(game: PathFinderGame, answer: (String) -> Unit, next: () -> Unit) {
        setContent {
            PathFinderScreen(game, answer, next)
        }
    }

    override fun showRiddle(
        game: RiddleGame,
        title: String,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            RiddleScreen(game, answer, next)
        }
    }

    override fun showGridSolver(
        game: GridSolverGame,
        answer: (String) -> Unit,
        next: () -> Unit
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
        next: () -> Unit
    ) {
        setContent {
            AnomalyPuzzleScreen(
                game,
                answer,
                next
            )
        }
    }

    override fun showColorConfusion(
        game: ColorConfusionGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            ColorConfusionScreen(game, answer, next)
        }
    }

    override fun showSherlockCalculation(
        game: SherlockCalculationGame,
        title: String,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            SherlockCalculationScreen(
                game,
                answer,
                next
            )
        }
    }

    override fun showChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            ChainCalculationScreen(
                game,
                answer,
                next
            )
        }
    }

    override fun showValueComparison(
        game: ValueComparisonGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            ValueComparisonScreen(game, answer, next)
        }
    }

    override fun showFractionCalculation(
        game: FractionCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            FractionCalculationScreen(game, answer, next)
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

