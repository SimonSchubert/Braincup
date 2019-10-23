//package com.inspiredandroid.braincup
//
//import android.app.Activity
//import android.os.Bundle
//import android.widget.TableRow
//import androidx.compose.Composable
//import androidx.compose.State
//import androidx.compose.composer
//import androidx.compose.state
//import androidx.compose.unaryPlus
//import androidx.ui.core.Text
//import androidx.ui.core.dp
//import androidx.ui.material.Button
//import androidx.ui.core.setContent
//import androidx.ui.core.sp
//import androidx.ui.graphics.Color
//import androidx.ui.layout.Column
//import androidx.ui.layout.Container
//import androidx.ui.layout.HeightSpacer
//import androidx.ui.layout.MainAxisAlignment
//import androidx.ui.layout.Padding
//import androidx.ui.layout.Table
//import androidx.ui.layout.TableColumnWidth
//import androidx.ui.material.Button
//import androidx.ui.material.studies.Scaffold
//import androidx.ui.material.themeTextStyle
//import androidx.ui.text.ParagraphStyle
//import androidx.ui.text.TextStyle
//import androidx.ui.text.style.TextAlign
//import androidx.ui.text.style.TextDirection
//import android.os.AsyncTask
//import android.widget.FrameLayout
//import androidx.ui.core.WithDensity
//import androidx.ui.foundation.SimpleImage
//import androidx.ui.foundation.VerticalScroller
//import androidx.ui.graphics.vector.DrawVector
//import androidx.ui.layout.CrossAxisAlignment
//import androidx.ui.layout.Row
//import androidx.ui.layout.WidthSpacer
//import androidx.ui.material.TextButtonStyle
//import androidx.ui.material.studies.R
//import androidx.ui.res.vectorResource
//import com.inspiredandroid.braincup.api.UserStorage
//import com.inspiredandroid.braincup.app.AppController
//import com.inspiredandroid.braincup.app.AppInterface
//import com.inspiredandroid.braincup.app.AppState
//import com.inspiredandroid.braincup.games.ChainCalculationGame
//import com.inspiredandroid.braincup.games.ColorConfusionGame
//import com.inspiredandroid.braincup.games.FractionCalculationGame
//import com.inspiredandroid.braincup.games.GameType
//import com.inspiredandroid.braincup.games.HeightComparisonGame
//import com.inspiredandroid.braincup.games.MEDAL_FIRST_RESOURCE
//import com.inspiredandroid.braincup.games.MEDAL_SECOND_RESOURCE
//import com.inspiredandroid.braincup.games.MEDAL_THIRD_RESOURCE
//import com.inspiredandroid.braincup.games.MentalCalculationGame
//import com.inspiredandroid.braincup.games.SherlockCalculationGame
//import com.inspiredandroid.braincup.games.getMedalResource
//import com.inspiredandroid.braincup.games.getName
//import com.inspiredandroid.braincup.games.getScoreTable
//import com.inspiredandroid.braincup.games.tools.Shape
//import com.inspiredandroid.braincup.games.tools.getName
//
//class MainActivity : Activity(), AppInterface {
//
//    private val gameMaster = AppController(this)
//    lateinit var frameLayout: FrameLayout
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        frameLayout = FrameLayout(this)
//        setContentView(frameLayout)
//        gameMaster.start(this)
//    }
//
//    override fun onBackPressed() {
//        if(gameMaster.state == AppState.START) {
//            super.onBackPressed()
//        } else {
//            gameMaster.start(this)
//        }
//    }
//
//    @Composable
//    fun BaseApp(children: @Composable() () -> Unit) {
//        AppTheme {
//            Column(
//                mainAxisAlignment = MainAxisAlignment.Center, crossAxisAlignment =
//                CrossAxisAlignment.Center
//            ) {
//                children()
//            }
//        }
//    }
//
//    override fun showMainMenu(
//        title: String,
//        description: String,
//        games: List<GameType>,
//        instructions: (GameType) -> Unit,
//        score: (GameType) -> Unit
//    ) {
//        val storage = UserStorage(this)
//        frameLayout.setContent {
//            VerticalScroller {
//                BaseApp {
//                    Text(title, style = +themeTextStyle { h5 })
//                    HeightSpacer(8.dp)
//                    Text(description, style = +themeTextStyle { subtitle1 })
//                    HeightSpacer(16.dp)
//                    games.forEach {
//                        HeightSpacer(16.dp)
//                        Row(crossAxisAlignment = CrossAxisAlignment.Center) {
//                            Button(onClick = { instructions(it) }) {
//                                Row(crossAxisAlignment = CrossAxisAlignment.Center) {
//                                    val vectorAsset = +vectorResource(it.getAndroidDrawable())
//                                    Container(width = 24.dp, height = 24.dp) {
//                                        DrawVector(vectorAsset)
//                                    }
//                                    WidthSpacer(16.dp)
//                                    Text(text = it.getName())
//                                }
//                            }
//                            val highscore = storage.getHighScore(it.getId())
//                            if(highscore > 0) {
//                                WidthSpacer(8.dp)
//                                Button(onClick = { instructions(it) }) {
//                                    val vectorAsset = +vectorResource(it.getAndroidMedalResource
//                                        (highscore))
//                                    Container(width = 24.dp, height = 24.dp) {
//                                        DrawVector(vectorAsset)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    val vectorAsset = +vectorResource(R.drawable.ic_waiting)
//                    Container(width = 266.dp, height = 200.dp) {
//                        DrawVector(vectorAsset)
//                    }
//                }
//            }
//        }
//    }
//
//    fun GameType.getAndroidMedalResource(score: Int): Int {
//        val scoreTable = this.getScoreTable()
//        return when {
//            score >= scoreTable[0] -> R.drawable.ic_icons8_medal_first_place
//            score >= scoreTable[1] -> R.drawable.ic_icons8_medal_second_place
//            else -> R.drawable.ic_icons8_medal_third_place
//        }
//    }
//
//    fun GameType.getId(): String {
//        return when (this) {
//            GameType.MENTAL_CALCULATION -> "0"
//            GameType.COLOR_CONFUSION -> "1"
//            GameType.SHERLOCK_CALCULATION -> "2"
//            GameType.CHAIN_CALCULATION -> "3"
//            GameType.FRACTION_CALCULATION -> "4"
//            GameType.HEIGHT_COMPARISON -> "5"
//        }
//    }
//
//    fun GameType.getAndroidDrawable() : Int {
//        return when(this) {
//            GameType.MENTAL_CALCULATION -> R.drawable.ic_icons8_math
//            GameType.COLOR_CONFUSION -> R.drawable.ic_icons8_fill_color
//            GameType.SHERLOCK_CALCULATION -> R.drawable.ic_icons8_search
//            GameType.CHAIN_CALCULATION -> R.drawable.ic_icons8_chain
//            GameType.FRACTION_CALCULATION -> R.drawable.ic_icons8_divide
//            GameType.HEIGHT_COMPARISON -> R.drawable.ic_icons8_height
//        }
//    }
//
//    override fun showInstructions(title: String, description: String, start: () -> Unit) {
//        frameLayout.setContent {
//            BaseApp {
//                Text(title, style = +themeTextStyle { h6 })
//                Padding(16.dp, 16.dp, 16.dp, 24.dp) {
//                    Text(description,
//                        style = +themeTextStyle {
//                            subtitle1
//                        }, paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
//                    )
//                }
//                Button("Start", {
//                    start()
//                })
//            }
//        }
//    }
//
//    override fun showMentalCalculation(
//        game: MentalCalculationGame,
//        answer: (String) -> Unit,
//        next: () -> Unit
//    ) {
//        frameLayout.setContent {
//            BaseApp {
//                Text(game.calculation, style = +themeTextStyle { h3 })
//                NumberPad(onInputChange = {
//                    if(game.getNumberLength() == it.length) {
//                        answer(it)
//                        DelayedTask().execute(next)
//                    }
//                })
//            }
//        }
//    }
//
//    override fun showColorConfusion(
//        game: ColorConfusionGame,
//        answer: (String) -> Unit,
//        next: () -> Unit
//    ) {
//        frameLayout.setContent {
//            BaseApp {
//                Text("${game.shapePoints} = ${game.answerShape.getName()}", style=
//                +themeTextStyle { h5 })
//                Text("${game.colorPoints} = ${game.answerColor.getName()}", style = TextStyle
//                    (fontSize =
//                24.sp, color = game
//                    .stringColor
//                    .getComposeColor())
//                )
//                Text(game.displayedShape.getChar(), style = TextStyle(fontSize = 96.sp, color = game
//                    .displayedColor
//                    .getComposeColor())
//                )
//                NumberPad(onInputChange = {
//                    if(game.points().length == it.length) {
//                        answer(it)
//                        DelayedTask().execute(next)
//                    }
//                })
//            }
//        }
//    }
//
//    override fun showSherlockCalculation(
//        game: SherlockCalculationGame,
//        answer: (String) -> Unit,
//        next: () -> Unit
//    ) {
//        frameLayout.setContent {
//            BaseApp {
//                Text("Goal: ${game.result}", style = +themeTextStyle { h3 })
//                Text("Numbers: ${game.getNumbersString()}", style = +themeTextStyle { h5 })
//                NumberPad(true, onInputChange = {
//                    if(game.isCorrect(it)) {
//                        answer(it)
//                        DelayedTask().execute(next)
//                    }
//                })
//                HeightSpacer(32.dp)
//                Button("Give up", onClick = {
//                    answer("")
//                    DelayedTask().execute(next)
//                })
//            }
//        }
//    }
//
//    override fun showChainCalculation(
//        game: ChainCalculationGame,
//        answer: (String) -> Unit,
//        next: () -> Unit
//    ) {
//        frameLayout.setContent {
//            BaseApp {
//                Text(game.calculation, style = +themeTextStyle { h4 })
//                NumberPad(false, onInputChange = {
//                    if(game.isCorrect(it)) {
//                        answer(it)
//                        DelayedTask().execute(next)
//                    }
//                })
//                HeightSpacer(32.dp)
//                Button("Give up", onClick = {
//                    answer("")
//                    DelayedTask().execute(next)
//                })
//            }
//        }
//    }
//
//    override fun showHeightComparison(
//        game: HeightComparisonGame,
//        answer: (String) -> Unit,
//        next: () -> Unit
//    ) {
//        frameLayout.setContent {
//            BaseApp {
//                game.answers.forEachIndexed { index, s ->
//                    HeightSpacer(16.dp)
//                    Button(text = s, onClick = {
//                        answer("${index+1}")
//                        DelayedTask().execute(next)
//                    })
//                }
//            }
//        }
//    }
//
//    override fun showFractionCalculation(
//        game: FractionCalculationGame,
//        answer: (String) -> Unit,
//        next: () -> Unit
//    ) {
//        frameLayout.setContent {
//            BaseApp {
//                Text(game.calculation, style = +themeTextStyle { h4 })
//                NumberPad(false, onInputChange = {
//                    if(game.isCorrect(it)) {
//                        answer(it)
//                        DelayedTask().execute(next)
//                    }
//                })
//                HeightSpacer(32.dp)
//                Button("Give up", onClick = {
//                    answer("")
//                    DelayedTask().execute(next)
//                })
//            }
//        }
//    }
//
//    override fun showScoreboard(
//        game: GameType,
//        highscore: Int,
//        scores: List<Pair<String, List<Int>>>
//    ) {
//
//    }
//
//    override fun showCorrectAnswerFeedback() {
//        frameLayout.setContent {
//            BaseApp {
//                val vectorAsset = +vectorResource(R.drawable.ic_success)
//                Container(width = 266.dp, height = 200.dp) {
//                    DrawVector(vectorAsset)
//                }
//            }
//        }
//    }
//
//    override fun showWrongAnswerFeedback(solution: String) {
//        frameLayout.setContent {
//            BaseApp {
//                val vectorAsset = +vectorResource(R.drawable.ic_searching)
//                Container(width = 266.dp, height = 200.dp) {
//                    DrawVector(vectorAsset)
//                }
//            }
//        }
//    }
//
//    override fun showFinishFeedback(
//        rank: String,
//        newHighscore: Boolean,
//        plays: Int,
//        random: () -> Unit
//    ) {
//        frameLayout.setContent {
//            BaseApp {
//                Text("Score: $rank", style = +themeTextStyle { h3 })
//                HeightSpacer(16.dp)
//                if(newHighscore) {
//                    Text("New highscore", style = +themeTextStyle { h6 })
//                }
//                HeightSpacer(16.dp)
//                Button("Next game", {
//                    random()
//                })
//            }
//        }
//    }
//
//    @Composable
//    fun NumberPad(showOperators: Boolean = false, onInputChange: (String) -> Unit) {
//        val input = +state { "" }
//        Row(crossAxisAlignment = CrossAxisAlignment.Center) {
//            Padding(16.dp, 16.dp, 16.dp, 16.dp) {
//                Text(
//                    input.value, style = TextStyle(color = Color.Black, fontSize = 32.sp)
//                )
//            }
//            if (input.value.isNotEmpty()) {
//                Button("◄", {
//                    input.value = input.value.substring(0, input.value.lastIndex)
//                })
//            }
//        }
//        Table(columns = if(showOperators) { 4 } else { 3 }, columnWidth = {
//            TableColumnWidth.Wrap }) {
//            tableRow {
//                NumberPadButton("7", input, onInputChange)
//                NumberPadButton("8", input, onInputChange)
//                NumberPadButton("9", input, onInputChange)
//                if(showOperators) {
//                    NumberPadButton("/", input, onInputChange)
//                }
//            }
//            tableRow {
//                NumberPadButton("4", input, onInputChange)
//                NumberPadButton("5", input, onInputChange)
//                NumberPadButton("6", input, onInputChange)
//                if(showOperators) {
//                    NumberPadButton("*", input, onInputChange)
//                }
//            }
//            tableRow {
//                NumberPadButton("1", input, onInputChange)
//                NumberPadButton("2", input, onInputChange)
//                NumberPadButton("3", input, onInputChange)
//                if(showOperators) {
//                    NumberPadButton("-", input, onInputChange)
//                }
//            }
//            tableRow {
//                if(showOperators) {
//                    NumberPadButton("(", input, onInputChange)
//                } else {
//                    Padding(2.dp) {}
//                }
//                NumberPadButton("0", input, onInputChange)
//                if(showOperators) {
//                    NumberPadButton(")", input, onInputChange)
//                } else {
//                    Padding(2.dp) {}
//                }
//                if(showOperators) {
//                    NumberPadButton("+", input, onInputChange)
//                }
//            }
//        }
//    }
//
//    @Composable
//    fun NumberPadButton(value: String, input: State<String>, onInputChange: (String) -> Unit) {
//        Padding(2.dp) {
//            Button(value, {
//                input.value += value
//                onInputChange(input.value)
//            })
//        }
//    }
//
//    /**
//     * Temporary solution
//     */
//
//    private fun Shape.getChar(): String {
//        return when(this) {
//            Shape.SQUARE -> "■"
//            Shape.TRIANGLE -> "▲"
//            Shape.CIRCLE -> "◯"
//            Shape.HEART -> "♥"
//        }
//    }
//
//    private fun com.inspiredandroid.braincup.games.tools.Color.getComposeColor(): androidx.ui.graphics.Color {
//        return when(this) {
//            com.inspiredandroid.braincup.games.tools.Color.RED -> Color.Red
//            com.inspiredandroid.braincup.games.tools.Color.GREEN -> Color.Green
//            com.inspiredandroid.braincup.games.tools.Color.BLUE -> Color.Blue
//            com.inspiredandroid.braincup.games.tools.Color.PURPLE -> Color.Magenta
//        }
//    }
//
//
//    /**
//     * Temporary solution until coroutines will work
//     */
//
//    private inner class DelayedTask : AsyncTask<() -> Unit, Int, () -> Unit>() {
//        override fun doInBackground(vararg next: () -> Unit): () -> Unit {
//            Thread.sleep(1000)
//            return next[0]
//        }
//
//        override fun onPostExecute(result: () -> Unit) {
//            result()
//        }
//    }
//}
