package com.inspiredandroid.braincup

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.preference.PreferenceManager
import androidx.ui.core.setContent
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppController
import com.inspiredandroid.braincup.app.AppInterface
import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.composables.*
import com.inspiredandroid.braincup.games.*
import com.russhwolf.settings.AndroidSettings

class MainActivity : Activity(), AppInterface {

    private val gameMaster = AppController(this)
    lateinit var frameLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        settings = AndroidSettings(sharedPrefs)

        frameLayout = FrameLayout(this)
        setContentView(frameLayout)
        gameMaster.start()
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
        instructions: (GameType) -> Unit,
        score: (GameType) -> Unit,
        achievements: () -> Unit,
        storage: UserStorage,
        totalScore: Int,
        appOpenCount: Int
    ) {
        setContent {
            MainMenuScreen(
                title,
                description,
                games,
                instructions,
                score,
                achievements,
                storage,
                totalScore,
                appOpenCount
            )
        }
    }

    override fun showInstructions(title: String, description: String, start: () -> Unit) {
        setContent {
            InstructionsScreen(
                title,
                description,
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

    override fun showScoreboard(
        game: GameType,
        highscore: Int,
        scores: List<Pair<String, List<Int>>>
    ) {
        setContent {
            ScoreboardScreen(
                game,
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

    override fun showHeightComparison(
        game: HeightComparisonGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            HeightComparisonScreen(game, answer, next)
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

    override fun showCorrectAnswerFeedback() {
        setContent {
            CorrectAnswerScreen()
        }
    }

    override fun showWrongAnswerFeedback(solution: String) {
        setContent {
            WrongAnswerScreen(solution)
        }
    }

    override fun showFinishFeedback(
        rank: String,
        newHighscore: Boolean,
        answeredAllCorrect: Boolean,
        plays: Int,
        random: () -> Unit
    ) {
        setContent {
            FinishScreen(
                rank,
                newHighscore,
                answeredAllCorrect,
                plays,
                random,
                gameMaster
            )
        }
    }
}

