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
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextAlign
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppController
import com.inspiredandroid.braincup.app.AppInterface
import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.getHex
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
    fun BaseScrollApp(children: @Composable() ColumnScope.() -> Unit) {
        AppTheme {
            VerticalScroller {
                Center {
                    Column {
                        children()
                    }
                }
            }
        }
    }

    @Composable
    fun BaseApp(children: @Composable() ColumnScope.() -> Unit) {
        AppTheme {
            Center {
                Column {
                    children()
                }
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
        setContent {
            BaseScrollApp {
                HeightSpacer(8.dp)
                Text(
                    title,
                    style = (+MaterialTheme.typography()).h5,
                    modifier = Gravity.Center
                )
                HeightSpacer(8.dp)
                Text(
                    description,
                    style = (+MaterialTheme.typography()).subtitle1,
                    modifier = Gravity.Center
                )
                HeightSpacer(16.dp)
                games.forEach {
                    HeightSpacer(16.dp)
                    Row(modifier = Gravity.Center) {
                        Button(onClick = { instructions(it) }) {
                            Row {
                                val vectorAsset =
                                    +vectorResource(it.getAndroidDrawable())
                                Container(width = 24.dp, height = 24.dp) {
                                    DrawVector(vectorAsset)
                                }
                                WidthSpacer(16.dp)
                                Text(text = it.getName())
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
                    Text(
                        "Consecutive training",
                        style = (+MaterialTheme.typography()).subtitle1,
                        modifier = Gravity.Center
                    )
                    Text(
                        appOpenCount.toString(),
                        style = (+MaterialTheme.typography()).h6,
                        modifier = Gravity.Center
                    )
                }
                if (totalScore > 0) {
                    HeightSpacer(16.dp)
                    Text(
                        "Total score",
                        style = (+MaterialTheme.typography()).subtitle1,
                        modifier = Gravity.Center
                    )
                    Text(
                        totalScore.toString(),
                        style = (+MaterialTheme.typography()).h6,
                        modifier = Gravity.Center
                    )
                }
                HeightSpacer(24.dp)
                Button(onClick = { achievements() }, modifier = Gravity.Center) {
                    Row(
                        arrangement = Arrangement.Center
                    ) {
                        val vectorAsset =
                            +vectorResource(R.drawable.ic_icons8_test_passed)
                        Container(
                            width = 24.dp, height = 24.dp,
                            modifier = Gravity.Center
                        ) {
                            DrawVector(vectorAsset)
                        }
                        WidthSpacer(16.dp)
                        val unlockedAchievements =
                            storage.getUnlockedAchievements()
                        Text(
                            text = "Achievements (${unlockedAchievements.size}/${UserStorage.Achievements.values().size})"
                        )
                    }
                }
                val vectorAsset = +vectorResource(R.drawable.ic_waiting)
                Container(
                    width = 266.dp, height = 200.dp,
                    modifier = Gravity.Center
                ) {
                    DrawVector(vectorAsset)
                }
            }
        }
    }

    override fun showInstructions(title: String, description: String, start: () -> Unit) {
        setContent {
            BaseApp {
                Text(
                    title,
                    style = (+MaterialTheme.typography()).h6,
                    modifier = Gravity.Center
                )
                Padding(16.dp, 16.dp, 16.dp, 24.dp) {
                    Text(
                        description,
                        style = (+MaterialTheme.typography()).subtitle1
                        , paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center),
                        modifier = Gravity.Center
                    )
                }
                Button("Start", onClick = {
                    start()
                }, modifier = Gravity.Center)
            }
        }
    }

    override fun showMentalCalculation(
        game: MentalCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            BaseApp {
                Text(
                    game.calculation, style = (+MaterialTheme.typography()).h3,
                    modifier = Gravity.Center
                )
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
            style = (+MaterialTheme.typography()).subtitle1
            , paragraphStyle = ParagraphStyle(textAlign = TextAlign.Left)
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
        setContent {
            BaseScrollApp {
                Text(
                    "${game.getName()} - Scores",
                    style = (+MaterialTheme.typography()).h5
                )

                HeightSpacer(16.dp)
                Text(
                    "Hightscore: $highscore",
                    style = (+MaterialTheme.typography()).h6
                )

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
                        style = (+MaterialTheme.typography()).h6
                    )
                    HeightSpacer(8.dp)
                    val pointSize = 15
                    it.second.forEach { score ->
                        var width = (score * pointSize).dp
                        if (width < 36.dp) {
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
                                    style = (+MaterialTheme.typography()).subtitle1
                                    , paragraphStyle = ParagraphStyle(textAlign = TextAlign.Left)
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
        setContent {
            BaseScrollApp {
                Text(
                    "Achievements (${unlockedAchievements.size}/${allAchievements.size})",
                    style = (+MaterialTheme.typography()).h5
                )
                HeightSpacer(16.dp)
                allAchievements.forEach {
                    HeightSpacer(16.dp)
                    Row(arrangement = Arrangement.Center) {
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
                            style = (+MaterialTheme.typography()).subtitle1
                            , paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                        )
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
        setContent {
            BaseApp {
                Text(
                    "${game.shapePoints} = ${game.answerShape.getName()}", style =
                    (+MaterialTheme.typography()).h5,
                    modifier = Gravity.Center
                )
                Text(
                    "${game.colorPoints} = ${game.answerColor.getName()}", style = TextStyle
                        (
                        fontSize = 24.sp,
                        color = game.stringColor.getComposeColor()
                    ),
                    modifier = Gravity.Center
                )
                HeightSpacer(32.dp)
                Container(
                    width = 96.dp, height = 96.dp,
                    modifier = Gravity.Center
                ) {
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
        setContent {
            BaseApp {
                Text(
                    "Goal: ${game.result}",
                    style = (+MaterialTheme.typography()).h3,
                    modifier = Gravity.Center
                )
                Text(
                    "Numbers: ${game.getNumbersString()}",
                    style = (+MaterialTheme.typography()).h5, modifier = Gravity.Center
                )
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
                }, modifier = Gravity.Center)
            }
        }
    }

    override fun showChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            BaseApp {
                Text(
                    game.calculation, style = (+MaterialTheme.typography()).h4,
                    modifier = Gravity.Center
                )
                NumberPad(false, onInputChange = {
                    if (game.isCorrect(it)) {
                        answer(it)
                        DelayedTask().execute(next)
                    }
                })
                HeightSpacer(32.dp)
                Button(
                    "Give up", onClick = {
                        answer("")
                        DelayedTask().execute(next)
                    },
                    modifier = Gravity.Center
                )
            }
        }
    }

    override fun showHeightComparison(
        game: HeightComparisonGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            BaseApp {
                game.answers.forEachIndexed { index, s ->
                    HeightSpacer(16.dp)
                    Button(text = s, onClick = {
                        answer("${index + 1}")
                        DelayedTask().execute(next)
                    }, modifier = Gravity.Center)
                }
            }
        }
    }

    override fun showFractionCalculation(
        game: FractionCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        setContent {
            BaseApp {
                Text(
                    game.calculation, style = (+MaterialTheme.typography()).h4,
                    modifier = Gravity.Center
                )
                NumberPad(false, onInputChange = {
                    if (game.isCorrect(it)) {
                        answer(it)
                        DelayedTask().execute(next)
                    }
                })
                HeightSpacer(32.dp)
                Button(
                    "Give up", onClick = {
                        answer("")
                        DelayedTask().execute(next)
                    },
                    modifier = Gravity.Center
                )
            }
        }
    }

    override fun showCorrectAnswerFeedback() {
        setContent {
            BaseApp {
                val vectorAsset = +vectorResource(R.drawable.ic_success)
                Container(width = 266.dp, height = 200.dp) {
                    DrawVector(vectorAsset)
                }
            }
        }
    }

    override fun showWrongAnswerFeedback(solution: String) {
        setContent {
            BaseApp {
                val vectorAsset = +vectorResource(R.drawable.ic_searching)
                Container(width = 266.dp, height = 200.dp) {
                    DrawVector(vectorAsset)
                }
                HeightSpacer(16.dp)
                Text(
                    "Correct was: $solution",
                    modifier = Gravity.Center
                )
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
        setContent {
            BaseApp {
                Text("Score: $rank", style = (+MaterialTheme.typography()).h4)
                HeightSpacer(16.dp)
                if (answeredAllCorrect) {
                    Text(
                        "You got 1 extra point for making zero mistakes.",
                        style = (+MaterialTheme.typography()).h6,
                        paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                    )
                }
                HeightSpacer(8.dp)
                if (newHighscore) {
                    Text("New highscore", style = (+MaterialTheme.typography()).h6)
                }
                HeightSpacer(16.dp)
                Button("Next game", onClick = {
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
        Column {
            Row(arrangement = Arrangement.End, modifier = Spacing(16.dp) wraps Gravity.Center) {
                Text(
                    input.value,
                    style = TextStyle(color = Color.Black, fontSize = 32.sp),
                    modifier = Gravity.Center
                )
                if (input.value.isNotEmpty()) {
                    WidthSpacer(width = 8.dp)
                    Button(
                        "◄", onClick = {
                            input.value = input.value.substring(0, input.value.lastIndex)
                        }, modifier = Gravity.Center
                    )
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
    }

    @Composable
    fun NumberPadButton(value: String, input: State<String>, onInputChange: (String) -> Unit) {
        Button(value, onClick = {
            input.value += value
            onInputChange(input.value)
        }, modifier = Spacing(4.dp))
    }

    private fun com.inspiredandroid.braincup.games.tools.Color.getComposeColor(): Color {
        return Color(android.graphics.Color.parseColor(this.getHex()))
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

    private fun GameType.getAndroidMedalResource(score: Int): Int {
        val scoreTable = this.getScoreTable()
        return when {
            score >= scoreTable[0] -> R.drawable.ic_icons8_medal_first_place
            score >= scoreTable[1] -> R.drawable.ic_icons8_medal_second_place
            else -> R.drawable.ic_icons8_medal_third_place
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
