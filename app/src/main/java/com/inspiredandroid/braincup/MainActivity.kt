package com.inspiredandroid.braincup

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.widget.FrameLayout
import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.preference.PreferenceManager
import androidx.ui.core.*
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.Path
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.themeTextStyle
import androidx.ui.res.vectorResource
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextAlign
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppController
import com.inspiredandroid.braincup.app.AppInterface
import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getName
import com.inspiredandroid.braincup.games.tools.getPaths
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

    @Composable
    fun BaseApp(children: @Composable() () -> Unit) {
        AppTheme {
            Column(
                mainAxisAlignment = MainAxisAlignment.Center, crossAxisAlignment =
                CrossAxisAlignment.Center
            ) {
                children()
            }
        }
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
        frameLayout.setContent {
            VerticalScroller {
                BaseApp {
                    HeightSpacer(8.dp)
                    Text(title, style = +themeTextStyle { h5 })
                    HeightSpacer(8.dp)
                    Text(description, style = +themeTextStyle { subtitle1 })
                    HeightSpacer(16.dp)
                    games.forEach {
                        HeightSpacer(16.dp)
                        Row(crossAxisAlignment = CrossAxisAlignment.Center) {
                            Container(width = 200.dp) {
                                Button(onClick = { instructions(it) }) {
                                    Row(
                                        crossAxisAlignment = CrossAxisAlignment.Center,
                                        mainAxisSize = LayoutSize.Expand
                                    ) {
                                        val vectorAsset = +vectorResource(it.getAndroidDrawable())
                                        Container(width = 24.dp, height = 24.dp) {
                                            DrawVector(vectorAsset)
                                        }
                                        WidthSpacer(16.dp)
                                        Text(text = it.getName())
                                    }
                                }
                            }
                            val highscore = storage.getHighScore(it.getId())
                            if (highscore > 0) {
                                WidthSpacer(8.dp)
                                Button(onClick = { score(it) }) {
                                    val vectorAsset = +vectorResource(
                                        it.getAndroidMedalResource
                                            (highscore)
                                    )
                                    Container(width = 24.dp, height = 24.dp) {
                                        DrawVector(vectorAsset)
                                    }
                                }
                            }
                        }
                    }
                    if (appOpenCount > 0) {
                        HeightSpacer(32.dp)
                        Text("Consecutive training", style = +themeTextStyle { subtitle1 })
                        Text(appOpenCount.toString(), style = +themeTextStyle { h4 })
                    }
                    if (totalScore > 0) {
                        HeightSpacer(16.dp)
                        Text("Total score", style = +themeTextStyle { subtitle1 })
                        Text(totalScore.toString(), style = +themeTextStyle { h4 })
                    }
                    HeightSpacer(24.dp)
                    Container(width = 200.dp) {
                        Button(onClick = { achievements() }) {
                            Row(
                                crossAxisAlignment = CrossAxisAlignment.Center,
                                mainAxisSize = LayoutSize.Expand
                            ) {
                                val vectorAsset = +vectorResource(R.drawable.ic_icons8_test_passed)
                                Container(width = 24.dp, height = 24.dp) {
                                    DrawVector(vectorAsset)
                                }
                                WidthSpacer(16.dp)
                                val unlockedAchievements = storage.getUnlockedAchievements()
                                Text(text = "Achievements (${unlockedAchievements.size}/${UserStorage.Achievements.values().size})")
                            }
                        }
                    }
                    val vectorAsset = +vectorResource(R.drawable.ic_waiting)
                    Container(width = 266.dp, height = 200.dp) {
                        DrawVector(vectorAsset)
                    }
                }
            }
        }
    }

    private fun GameType.getAndroidMedalResource(score: Int): Int {
        val scoreTable = this.getScoreTable()
        return when {
            score >= scoreTable[0] -> R.drawable.ic_icons8_medal_first_place
            score >= scoreTable[1] -> R.drawable.ic_icons8_medal_second_place
            else -> R.drawable.ic_icons8_medal_third_place
        }
    }

    private fun GameType.getAndroidDrawable(): Int {
        return when (this) {
            GameType.MENTAL_CALCULATION -> R.drawable.ic_icons8_math
            GameType.COLOR_CONFUSION -> R.drawable.ic_icons8_fill_color
            GameType.SHERLOCK_CALCULATION -> R.drawable.ic_icons8_search
            GameType.CHAIN_CALCULATION -> R.drawable.ic_icons8_chain
            GameType.FRACTION_CALCULATION -> R.drawable.ic_icons8_divide
            GameType.HEIGHT_COMPARISON -> R.drawable.ic_icons8_height
        }
    }

    private fun UserStorage.Achievements.getAndroidResource(): Int {
        return when (this) {
            UserStorage.Achievements.MEDAL_BRONZE -> R.drawable.ic_icons8_medal_third_place
            UserStorage.Achievements.MEDAL_SILVER -> R.drawable.ic_icons8_medal_second_place
            UserStorage.Achievements.MEDAL_GOLD -> R.drawable.ic_icons8_medal_first_place
            UserStorage.Achievements.SCORES_10 -> R.drawable.ic_icons8_counter
            UserStorage.Achievements.SCORES_100 -> R.drawable.ic_icons8_counter_bronze
            UserStorage.Achievements.SCORES_1000 -> R.drawable.ic_icons8_counter_silver
            UserStorage.Achievements.SCORES_10000 -> R.drawable.ic_icons8_counter_gold
            UserStorage.Achievements.APP_OPEN_7 -> R.drawable.ic_icons8_counter_bronze
            UserStorage.Achievements.APP_OPEN_30 -> R.drawable.ic_icons8_counter_bronze
            UserStorage.Achievements.APP_OPEN_356 -> R.drawable.ic_icons8_counter_bronze
        }
    }

    override fun showInstructions(title: String, description: String, start: () -> Unit) {
        frameLayout.setContent {
            BaseApp {
                Text(title, style = +themeTextStyle { h6 })
                Padding(16.dp, 16.dp, 16.dp, 24.dp) {
                    Text(
                        description,
                        style = +themeTextStyle {
                            subtitle1
                        }, paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                    )
                }
                Button("Start", {
                    start()
                })
            }
        }
    }

    override fun showMentalCalculation(
        game: MentalCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        frameLayout.setContent {
            BaseApp {
                Text(game.calculation, style = +themeTextStyle { h3 })
                NumberPad(onInputChange = {
                    if (game.getNumberLength() == it.length) {
                        answer(it)
                        DelayedTask().execute(next)
                    }
                })
            }
        }
    }

    @Composable
    fun ScoreboardLegend(text: String, vector: Int) {
        Text(
            text,
            style = +themeTextStyle {
                subtitle1
            }, paragraphStyle = ParagraphStyle(textAlign = TextAlign.Left)
        )
        val vectorAsset = +vectorResource(
            vector
        )
        Container(width = 24.dp, height = 24.dp) {
            DrawVector(vectorAsset)
        }
    }

    override fun showScoreboard(
        game: GameType,
        highscore: Int,
        scores: List<Pair<String, List<Int>>>
    ) {
        frameLayout.setContent {
            BaseApp {
                Text("${game.getName()} - Scores",
                    style = +themeTextStyle { h5 })

                HeightSpacer(16.dp)
                Text("Hightscore: $highscore",
                    style = +themeTextStyle { h6 })

                HeightSpacer(8.dp)
                val table = game.getScoreTable()
                Row {
                    ScoreboardLegend("> 0", R.drawable.ic_icons8_medal_third_place)
                    WidthSpacer(8.dp)
                    ScoreboardLegend(">= ${table[1]}", R.drawable.ic_icons8_medal_second_place)
                    WidthSpacer(8.dp)
                    ScoreboardLegend(">= ${table[0]}", R.drawable.ic_icons8_medal_first_place)
                }

                game.getScoreTable()
                scores.forEach {
                    HeightSpacer(16.dp)
                    Text(
                        it.first,
                        style = +themeTextStyle { h6 })
                    HeightSpacer(8.dp)
                    val pointSize = 15
                    it.second.forEach { score ->
                        var width = (score * pointSize).dp
                        if(width < 36.dp) {
                            width = 36.dp
                        }
                        Container(
                            width = width,
                            height = 24.dp,
                            alignment = Alignment.Center
                        ) {
                            DrawShape(
                                RectangleShape,
                                color = Color(0xFFED7354)
                            )
                            Row {
                                Text(
                                    score.toString(),
                                    style = +themeTextStyle {
                                        subtitle1
                                    }, paragraphStyle = ParagraphStyle(textAlign = TextAlign.Left)
                                )
                                val vectorAsset = +vectorResource(
                                    game.getAndroidMedalResource(score)
                                )
                                Container(width = 24.dp, height = 24.dp) {
                                    DrawVector(vectorAsset)
                                }
                            }
                        }
                        HeightSpacer(16.dp)
                    }
                }
            }
        }
    }

    override fun showAchievements(
        allAchievements: List<UserStorage.Achievements>,
        unlockedAchievements: List<UserStorage.Achievements>
    ) {
        frameLayout.setContent {
            VerticalScroller {
                BaseApp {
                    Text(
                        "Achievements (${unlockedAchievements.size}/${allAchievements.size})",
                        style = +themeTextStyle { h5 })
                    HeightSpacer(16.dp)
                    allAchievements.forEach {
                        HeightSpacer(16.dp)
                        Row(
                            crossAxisAlignment = CrossAxisAlignment.Center,
                            mainAxisAlignment = MainAxisAlignment.Center
                        ) {
                            Container(width = 64.dp, height = 64.dp, alignment = Alignment.Center) {
                                DrawShape(
                                    RectangleShape,
                                    color = if (unlockedAchievements.contains(it)) {
                                        Color(0xFF65AA69)
                                    } else {
                                        Color.LightGray
                                    }
                                )
                                Padding(left = 16.dp, top = 16.dp, bottom = 16.dp, right = 16.dp) {
                                    val vectorAsset = +vectorResource(it.getAndroidResource())
                                    DrawVector(vectorAsset)
                                }
                            }
                            WidthSpacer(16.dp)
                            Text(
                                it.getDescription(),
                                style = +themeTextStyle {
                                    subtitle1
                                }, paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun showColorConfusion(
        game: ColorConfusionGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        frameLayout.setContent {
            BaseApp {
                Text("${game.shapePoints} = ${game.answerShape.getName()}", style =
                +themeTextStyle { h5 })
                Text(
                    "${game.colorPoints} = ${game.answerColor.getName()}", style = TextStyle
                        (
                        fontSize =
                        24.sp, color = game
                        .stringColor
                        .getComposeColor()
                    )
                )
                HeightSpacer(32.dp)
                Container(width = 96.dp, height = 96.dp) {
                    Draw { canvas, parentSize ->
                        val paint = Paint()
                        paint.color = game.displayedColor.getComposeColor()
                        paint.isAntiAlias = true
                        val path = Path()
                        game.displayedShape.getPaths().forEachIndexed { index, pair ->
                            val x = parentSize.width.value * pair.first
                            val y = parentSize.width.value * pair.second
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }
                        path.close()
                        canvas.drawPath(path, paint)
                    }
                }
                HeightSpacer(32.dp)
                val numbers = listOf(
                    0,
                    game.shapePoints,
                    game.colorPoints,
                    game.shapePoints + game.colorPoints
                ).sorted().map { it.toString() }
                NumberRow(numbers) {
                    answer(it)
                    DelayedTask().execute(next)
                }
            }
        }
    }

    override fun showSherlockCalculation(
        game: SherlockCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        frameLayout.setContent {
            BaseApp {
                Text("Goal: ${game.result}", style = +themeTextStyle { h3 })
                Text("Numbers: ${game.getNumbersString()}", style = +themeTextStyle { h5 })
                NumberPad(true, onInputChange = {
                    if (game.isCorrect(it)) {
                        answer(it)
                        DelayedTask().execute(next)
                    }
                })
                HeightSpacer(32.dp)
                Button("Give up", onClick = {
                    answer("")
                    DelayedTask().execute(next)
                })
            }
        }
    }

    override fun showChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        frameLayout.setContent {
            BaseApp {
                Text(game.calculation, style = +themeTextStyle { h4 })
                NumberPad(false, onInputChange = {
                    if (game.isCorrect(it)) {
                        answer(it)
                        DelayedTask().execute(next)
                    }
                })
                HeightSpacer(32.dp)
                Button("Give up", onClick = {
                    answer("")
                    DelayedTask().execute(next)
                })
            }
        }
    }

    override fun showHeightComparison(
        game: HeightComparisonGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        frameLayout.setContent {
            BaseApp {
                game.answers.forEachIndexed { index, s ->
                    HeightSpacer(16.dp)
                    Button(text = s, onClick = {
                        answer("${index + 1}")
                        DelayedTask().execute(next)
                    })
                }
            }
        }
    }

    override fun showFractionCalculation(
        game: FractionCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        frameLayout.setContent {
            BaseApp {
                Text(game.calculation, style = +themeTextStyle { h4 })
                NumberPad(false, onInputChange = {
                    if (game.isCorrect(it)) {
                        answer(it)
                        DelayedTask().execute(next)
                    }
                })
                HeightSpacer(32.dp)
                Button("Give up", onClick = {
                    answer("")
                    DelayedTask().execute(next)
                })
            }
        }
    }

    override fun showCorrectAnswerFeedback() {
        frameLayout.setContent {
            BaseApp {
                val vectorAsset = +vectorResource(R.drawable.ic_success)
                Container(width = 266.dp, height = 200.dp) {
                    DrawVector(vectorAsset)
                }
            }
        }
    }

    override fun showWrongAnswerFeedback(solution: String) {
        frameLayout.setContent {
            BaseApp {
                val vectorAsset = +vectorResource(R.drawable.ic_searching)
                Container(width = 266.dp, height = 200.dp) {
                    DrawVector(vectorAsset)
                }
                HeightSpacer(16.dp)
                Text("Correct was: $solution")
            }
        }
    }

    override fun showFinishFeedback(
        rank: String,
        newHighscore: Boolean,
        answeredAllCorrect: Boolean,
        plays: Int,
        random: () -> Unit
    ) {
        frameLayout.setContent {
            BaseApp {
                Text("Score: $rank", style = +themeTextStyle { h4 })
                HeightSpacer(16.dp)
                if (answeredAllCorrect) {
                    Text(
                        "You got 1 extra point for making zero mistakes.",
                        style = +themeTextStyle { h6 },
                        paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                    )
                }
                HeightSpacer(8.dp)
                if (newHighscore) {
                    Text("New highscore", style = +themeTextStyle { h6 })
                }
                HeightSpacer(16.dp)
                Button("Next game", {
                    random()
                })
            }
        }
    }


    @Composable
    fun NumberRow(numbers: List<String>, onInputChange: (String) -> Unit) {
        val input = +state { "" }
        Row {
            numbers.forEach {
                NumberPadButton(it, input, onInputChange)
            }
        }
    }

    @Composable
    fun NumberPad(showOperators: Boolean = false, onInputChange: (String) -> Unit) {
        val input = +state { "" }
        Row(crossAxisAlignment = CrossAxisAlignment.Center) {
            Padding(16.dp, 16.dp, 16.dp, 16.dp) {
                Text(
                    input.value, style = TextStyle(color = Color.Black, fontSize = 32.sp)
                )
            }
            if (input.value.isNotEmpty()) {
                Button("◄", {
                    input.value = input.value.substring(0, input.value.lastIndex)
                })
            }
        }
        Table(
            columns = if (showOperators) {
                4
            } else {
                3
            }, columnWidth = {
                TableColumnWidth.Wrap
            }) {
            tableRow {
                NumberPadButton("7", input, onInputChange)
                NumberPadButton("8", input, onInputChange)
                NumberPadButton("9", input, onInputChange)
                if (showOperators) {
                    NumberPadButton("/", input, onInputChange)
                }
            }
            tableRow {
                NumberPadButton("4", input, onInputChange)
                NumberPadButton("5", input, onInputChange)
                NumberPadButton("6", input, onInputChange)
                if (showOperators) {
                    NumberPadButton("*", input, onInputChange)
                }
            }
            tableRow {
                NumberPadButton("1", input, onInputChange)
                NumberPadButton("2", input, onInputChange)
                NumberPadButton("3", input, onInputChange)
                if (showOperators) {
                    NumberPadButton("-", input, onInputChange)
                }
            }
            tableRow {
                if (showOperators) {
                    NumberPadButton("(", input, onInputChange)
                } else {
                    Padding(3.dp) {}
                }
                NumberPadButton("0", input, onInputChange)
                if (showOperators) {
                    NumberPadButton(")", input, onInputChange)
                } else {
                    Padding(3.dp) {}
                }
                if (showOperators) {
                    NumberPadButton("+", input, onInputChange)
                }
            }
        }
    }

    @Composable
    fun NumberPadButton(value: String, input: State<String>, onInputChange: (String) -> Unit) {
        Padding(3.dp) {
            Button(value, {
                input.value += value
                onInputChange(input.value)
            })
        }
    }

    /**
     * Temporary solution
     */
    private fun Shape.getChar(): String {
        return when (this) {
            Shape.SQUARE -> "■"
            Shape.TRIANGLE -> "▲"
            Shape.CIRCLE -> "◯"
            Shape.HEART -> "♥"
        }
    }

    private fun com.inspiredandroid.braincup.games.tools.Color.getComposeColor(): Color {
        return when (this) {
            com.inspiredandroid.braincup.games.tools.Color.RED -> Color.Red
            com.inspiredandroid.braincup.games.tools.Color.GREEN -> Color.Green
            com.inspiredandroid.braincup.games.tools.Color.BLUE -> Color.Blue
            com.inspiredandroid.braincup.games.tools.Color.PURPLE -> Color.Magenta
        }
    }

    /**
     * Temporary solution until coroutines will work
     */
    private inner class DelayedTask : AsyncTask<() -> Unit, Int, () -> Unit>() {
        override fun doInBackground(vararg next: () -> Unit): () -> Unit {
            Thread.sleep(1000)
            return next[0]
        }

        override fun onPostExecute(result: () -> Unit) {
            result()
        }
    }
}
