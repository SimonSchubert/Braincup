package androidx.ui.material.studies

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import androidx.compose.Composable
import androidx.compose.composer
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.core.sp
import androidx.ui.graphics.Color
import androidx.ui.input.EditorModel
import androidx.ui.input.EditorStyle
import androidx.ui.layout.Column
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.material.Button
import androidx.ui.material.studies.Scaffold
import androidx.ui.material.studies.rally.AppTheme
import androidx.ui.material.studies.rally.RallyAccountsCard
import androidx.ui.material.studies.rally.RallyAlertCard
import androidx.ui.material.studies.rally.RallyBillsCard
import androidx.ui.material.themeTextStyle
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextAlign
import com.inspiredandroid.braincup.AppController
import com.inspiredandroid.braincup.games.ColorConfusion
import com.inspiredandroid.braincup.games.Game
import com.inspiredandroid.braincup.games.MentalCalculation
import com.inspiredandroid.braincup.games.SherlockCalculation
import com.inspiredandroid.braincup.games.getName
import com.inspiredandroid.braincup.getComposeColor
import com.inspiredandroid.braincup.getName
import java.lang.System.currentTimeMillis

class MainActivity : Activity(), AppController.Interface {

    private val gameMaster = AppController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameMaster.start()
    }

    override fun showMainMenu(title: String, description: String, games: List<Game>, callback: (Game) -> Unit) {
        setContent {
            AppTheme {
                Column(mainAxisAlignment = MainAxisAlignment.Center) {
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
                    HeightSpacer(16.dp)
                }
            }
        }
    }

    override fun showInstructions(title: String, description: String, start: (Long) -> Unit) {
        setContent {
            AppTheme {
                Column(mainAxisAlignment = MainAxisAlignment.Center) {
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
                        start(System.currentTimeMillis())
                    })
                }
            }
        }
    }

    override fun showMentalCalculation(game: MentalCalculation, answer: (String) -> Unit, next: (Long) -> Unit) {
        setContent {
            AppTheme {
                Column(mainAxisAlignment = MainAxisAlignment.Center) {
                    Text(game.calculation, +themeTextStyle { h3 })
                    Padding(16.dp, 16.dp, 16.dp, 96.dp) {
                        val state = +state { EditorModel() }
                        TextField(value = state.value, onValueChange = {
                            state.value = it
                            if(it.text.length == game.number.toString().length) {
                                answer(it.text)
                                next(currentTimeMillis())
                            }
                        }, editorStyle = EditorStyle(+themeTextStyle { h6 }))
                    }
                }
            }
        }
    }

    override fun showColorConfusion(game: ColorConfusion, answer: (String) -> Unit, next: (Long) -> Unit) {
        setContent {
            AppTheme {
                Column(mainAxisAlignment = MainAxisAlignment.Center) {
                    Text("${game.shapePoints} = ${game.answerShape.getName()}", +themeTextStyle { h5 })
                    Text("${game.colorPoints} = ${game.answerColor.getName()}", TextStyle(color = game.stringColor
                        .getComposeColor())
                    )
                    Padding(16.dp, 16.dp, 16.dp, 96.dp) {
                        val state = +state { EditorModel() }
                        TextField(value = state.value, onValueChange = {
                            state.value = it
                            if(game.points().length == it.text.length) {
                                answer(it.text)
                                next(currentTimeMillis())
                            }
                        }, editorStyle = EditorStyle(+themeTextStyle { h6 }))
                    }
                }
            }
        }
    }

    override fun showSherlockCalculation(game: SherlockCalculation, answer: (String) -> Unit, next: (Long) -> Unit) {
        setContent {
            AppTheme {
                Column(mainAxisAlignment = MainAxisAlignment.Center) {
                    Text("Goal: ${game.result}", +themeTextStyle { h3 })
                    Text("Numbers: ${game.numbers.joinToString()}", +themeTextStyle { h5 })
                    Padding(16.dp, 16.dp, 16.dp, 96.dp) {
                        val state = +state { EditorModel() }
                        TextField(value = state.value , onValueChange = {
                            state.value = it
                            if(game.isCorrect(it.text)) {
                                answer(it.text)
                                next(currentTimeMillis())
                            }
                        }, editorStyle = EditorStyle(+themeTextStyle { h6 }))
                    }
                }
            }
        }
    }

    override fun showCorrectAnswerFeedback() {
        setContent {
            AppTheme {
                Column(mainAxisAlignment = MainAxisAlignment.Center) {
                    Text("√ :)", +themeTextStyle { h6 })
                }
            }
        }
    }

    override fun showWrongAnswerFeedback() {
        setContent {
            AppTheme {
                Column(mainAxisAlignment = MainAxisAlignment.Center) {
                    Text("x :(", +themeTextStyle { h6 })
                }
            }
        }
    }

    override fun showFinishFeedback(rank: String, plays: Int, random: () -> Unit) {
        setContent {
            AppTheme {
                Column(mainAxisAlignment = MainAxisAlignment.Center) {
                    Text("Your rank: $rank", +themeTextStyle { h6 })
                    Button("Next game", {
                        random()
                    })
                }
            }
        }
    }
}