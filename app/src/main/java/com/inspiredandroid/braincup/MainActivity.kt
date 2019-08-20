package androidx.ui.material.studies

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.widget.TableRow
import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.composer
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Duration
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.core.sp
import androidx.ui.graphics.Color
import androidx.ui.input.EditorModel
import androidx.ui.input.EditorStyle
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.MaxIntrinsicHeight
import androidx.ui.layout.MinIntrinsicWidth
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.Table
import androidx.ui.layout.TableColumnWidth
import androidx.ui.material.Button
import androidx.ui.material.studies.Scaffold
import androidx.ui.material.studies.rally.AppTheme
import androidx.ui.material.studies.rally.RallyAccountsCard
import androidx.ui.material.studies.rally.RallyAlertCard
import androidx.ui.material.studies.rally.RallyBillsCard
import androidx.ui.material.themeTextStyle
import androidx.ui.temputils.delay
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.text.style.TextDirection
import com.inspiredandroid.braincup.AppController
import com.inspiredandroid.braincup.Shape
import com.inspiredandroid.braincup.games.ColorConfusion
import com.inspiredandroid.braincup.games.Game
import com.inspiredandroid.braincup.games.MentalCalculation
import com.inspiredandroid.braincup.games.SherlockCalculation
import com.inspiredandroid.braincup.games.getName
import com.inspiredandroid.braincup.getComposeColor
import com.inspiredandroid.braincup.getName
import java.lang.System.currentTimeMillis
import android.os.AsyncTask
import androidx.ui.core.WithDensity
import androidx.ui.core.vectorgraphics.DrawVector
import androidx.ui.core.vectorgraphics.compat.vectorResource
import androidx.ui.foundation.SimpleImage
import androidx.ui.painting.Image
import androidx.ui.painting.imageFromResource

class MainActivity : Activity(), AppController.Interface {

    private val gameMaster = AppController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameMaster.start()
    }

    override fun onBackPressed() {
        gameMaster.start()
    }

    @Composable
    fun BaseApp(children: @Composable() () -> Unit) {
        AppTheme {
            Column(mainAxisAlignment = MainAxisAlignment.Center) {
                children()
            }
        }
    }

    override fun showMainMenu(title: String, description: String, games: List<Game>, callback: (Game) -> Unit) {
        setContent {
            BaseApp {
                Text(title, +themeTextStyle { h5 })
                HeightSpacer(8.dp)
                Text(description, +themeTextStyle { subtitle1 })
                HeightSpacer(16.dp)
                games.forEach {
                    HeightSpacer(16.dp)
                    Button(it.getName(), {
                        callback(it)
                    })
                }
                val vectorAsset = +vectorResource(R.drawable.ic_waiting)
                Container(width = 266.dp, height = 200.dp) {
                    DrawVector(vectorAsset)
                }
            }
        }
    }

    override fun showInstructions(title: String, description: String, start: (Long) -> Unit) {
        setContent {
            BaseApp {
                Text(title, +themeTextStyle { h6 })
                Padding(16.dp, 16.dp, 16.dp, 24.dp) {
                    Text(
                        description,
                        +themeTextStyle {
                            subtitle1
                        }, ParagraphStyle(textAlign = TextAlign.Center)
                    )
                }
                Button("Start", {
                    start(currentTimeMillis())
                })
            }
        }
    }

    override fun showMentalCalculation(game: MentalCalculation, answer: (String) -> Unit, next: (Long) -> Unit) {
        setContent {
            BaseApp {
                Text(game.calculation, +themeTextStyle { h3 })
                NumberPad(onInputChange = {
                    if(it.length == game.number.toString().length) {
                        answer(it)
                        DelayedTask().execute(next)
                    }
                })
            }
        }
    }

    override fun showColorConfusion(game: ColorConfusion, answer: (String) -> Unit, next: (Long) -> Unit) {
        setContent {
            BaseApp {
                Text("${game.shapePoints} = ${game.answerShape.getName()}", +themeTextStyle { h5 })
                Text("${game.colorPoints} = ${game.answerColor.getName()}", TextStyle(fontSize = 24.sp, color = game
                    .stringColor
                    .getComposeColor())
                )
                Text(game.displayedShape.getChar(), TextStyle(fontSize = 96.sp, color = game
                    .displayedColor
                    .getComposeColor())
                )
                NumberPad(onInputChange = {
                    if(game.points().length == it.length) {
                        answer(it)
                        DelayedTask().execute(next)
                    }
                })
            }
        }
    }

    override fun showSherlockCalculation(game: SherlockCalculation, answer: (String) -> Unit, next: (Long) -> Unit) {
        setContent {
            BaseApp {
                Text("Goal: ${game.result}", +themeTextStyle { h3 })
                Text("Numbers: ${game.numbers.joinToString()}", +themeTextStyle { h5 })
                NumberPad(true, onInputChange = {
                    if(game.isCorrect(it)) {
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
        setContent {
            BaseApp {
                val vectorAsset = +vectorResource(R.drawable.ic_success)
                Container(width = 266.dp, height = 200.dp) {
                    DrawVector(vectorAsset)
                }
            }
        }
    }

    override fun showWrongAnswerFeedback() {
        setContent {
            BaseApp {
                val vectorAsset = +vectorResource(R.drawable.ic_searching)
                Container(width = 266.dp, height = 200.dp) {
                    DrawVector(vectorAsset)
                }
            }
        }
    }

    override fun showFinishFeedback(rank: String, plays: Int, random: () -> Unit) {
        setContent {
            BaseApp {
                Text(rank, +themeTextStyle { h6 })
                Button("Next game", {
                    random()
                })
            }
        }
    }

    @Composable
    fun NumberPad(showOperators: Boolean = false, onInputChange: (String) -> Unit) {
        val input = +state { "" }
        Padding(16.dp, 16.dp, 16.dp, 16.dp) {
            Text(
                input.value, style = TextStyle(color = Color.Black, fontSize = 32.sp)
            )
        }
        Container(height = 50.dp) {
            if (input.value.isNotEmpty()) {
                Button("◄", {
                    input.value = input.value.substring(0, input.value.lastIndex)
                })
            }
        }
        Table(columnCount = if(showOperators) { 4 } else { 3 }, columnWidth = { TableColumnWidth.Inflexible.Wrap }) {
            tableRow {
                NumberPadButton("7", input, onInputChange)
                NumberPadButton("8", input, onInputChange)
                NumberPadButton("9", input, onInputChange)
                if(showOperators) {
                    NumberPadButton("/", input, onInputChange)
                }
            }
            tableRow {
                NumberPadButton("4", input, onInputChange)
                NumberPadButton("5", input, onInputChange)
                NumberPadButton("6", input, onInputChange)
                if(showOperators) {
                    NumberPadButton("*", input, onInputChange)
                }
            }
            tableRow {
                NumberPadButton("1", input, onInputChange)
                NumberPadButton("2", input, onInputChange)
                NumberPadButton("3", input, onInputChange)
                if(showOperators) {
                    NumberPadButton("-", input, onInputChange)
                }
            }
            tableRow {
                if(showOperators) {
                    NumberPadButton("(", input, onInputChange)
                } else {
                    Padding(2.dp) {}
                }
                NumberPadButton("0", input, onInputChange)
                if(showOperators) {
                    NumberPadButton(")", input, onInputChange)
                } else {
                    Padding(2.dp) {}
                }
                if(showOperators) {
                    NumberPadButton("+", input, onInputChange)
                }
            }
        }
    }

    @Composable
    fun NumberPadButton(value: String, input: State<String>, onInputChange: (String) -> Unit) {
        Padding(2.dp) {
            Button(value, {
                input.value += value
                onInputChange(input.value)
            })
        }
    }

    /**
     * Temporary solution
     */
    fun Shape.getChar(): String {
        return when(this) {
            Shape.SQUARE -> "■"
            Shape.TRIANGLE -> "▲"
            Shape.CIRCLE -> "◯"
            Shape.HEART -> "♥"
        }
    }

    /**
     * Temporary solution until coroutines will work
     */
    private inner class DelayedTask : AsyncTask<(Long) -> Unit, Int, (Long) -> Unit>() {
        override fun doInBackground(vararg next: (Long) -> Unit): (Long) -> Unit {
            Thread.sleep(1000)
            return next[0]
        }

        override fun onPostExecute(result: (Long) -> Unit) {
            result(currentTimeMillis())
        }
    }
}