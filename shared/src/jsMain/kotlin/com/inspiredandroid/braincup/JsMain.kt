package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.app.AppController
import com.inspiredandroid.braincup.app.AppInterface
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getName
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.body
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onInputFunction
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    JsMain()
}

class JsMain : AppInterface {

    private val appController = AppController(this)

    var gameTitle = ""

    init {
        appController.start()
    }

    override fun showMainMenu(
        title: String,
        description: String,
        games: List<GameType>,
        callback: (GameType) -> Unit
    ) {
        window.addEventListener("popstate", {
            showMainMenu(title, description, games, callback)
        })
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text(title)
            }
            br { }
            br { }
            div {
                classes += "mdc-typography--headline6"
                text(description)
            }
            games.forEach { game ->
                br { }
                br { }
                button {
                    style = "width: 300px; height: 50px; font-size: 16px;"
                    classes += "mdc-button mdc-button--raised"
                    img {
                        classes += "material-icons mdc-button__icon"
                        src = game.getImageResource()
                        style = "height: 20px; width: 20px;"
                    }
                    span {
                        classes += "mdc-button__label"
                        text(game.getName())
                    }
                    onClickFunction = {
                        callback(game)
                    }
                }
            }
            br {}
            img {
                src = "images/waiting.svg"
                width = "400px"
            }
            div {
                classes += "mdc-typography--headline4"
                text("Download")
            }
            div {
                classes += "mdc-typography--headline5"
                text("macOS homebrew:")
            }
            code {
                text("brew tap SimonSchubert/braincup && brew install SimonSchubert/braincup/braincup")
            }
        }
    }

    override fun showInstructions(title: String, description: String, start: (Long) -> Unit) {
        gameTitle = title
        window.history.pushState(null, "", "${gameTitle.toLowerCase().removeWhitespaces()}.html")
        document.title = "$gameTitle - Braincup"
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text(title)
            }
            br { }
            br { }
            div {
                classes += "mdc-typography--headline6"
                text(description)
            }
            br { }
            br { }
            button {
                classes += "mdc-button mdc-button--raised"
                text("Start")
                onClickFunction = {
                    start(currentTimeMillis())
                }
            }
        }
    }

    override fun showMentalCalculation(
        game: MentalCalculationGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text(gameTitle)
            }
            br { }
            br { }
            br {}
            br {}
            br {}
            div {
                classes += "mdc-typography--headline4"
                text(game.calculation)
            }
            br {}
            div {
                classes += "mdc-text-field mdc-text-field--outlined"
                input {
                    style = "text-align: center;font-size: 30px;width: 150px;"
                    classes = setOf("mdc-text-field__input")
                    id = "answerInput"
                    autoComplete = false
                    onInputFunction = {
                        val input = document.getElementById("answerInput") as HTMLInputElement
                        input.focus()
                        console.log(input.value)
                        if (game.number.toString().length == input.value.length) {
                            answer(input.value)
                            window.setTimeout({
                                next(currentTimeMillis())
                            }, 1000)
                        }
                    }
                }
                div {
                    classes += "mdc-notched-outline mdc-notched-outline--no-label"
                    div {
                        classes += "mdc-notched-outline__leading"
                    }
                    div {
                        classes += "mdc-notched-outline__trailing"
                    }
                }
            }
            br {}
        }
        val input = document.getElementById("answerInput") as HTMLInputElement
        input.focus()
    }

    override fun showColorConfusion(
        game: ColorConfusionGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text(gameTitle)
            }
            br { }
            br { }
            i {
                style = "font-size: 144px; color:${game.displayedColor.getName()};"
                classes += "material-icons"
                text(game.displayedShape.getIconResource())
            }
            br { }
            br { }

            div {
                style = "display: inline-block; text-align: left;"
                span {
                    style = "width: 200px"
                    classes += "mdc-typography--headline5"
                    text("${game.shapePoints}: is " + game.answerShape.getName())
                }
                br {}
                span {
                    style = "width: 200px"
                    classes += "mdc-typography--headline5"
                    text("${game.colorPoints}: is ")
                    span {
                        style = "color:${game.stringColor.getName()};"
                        text(game.answerColor.getName())
                    }
                }
            }
            br { }
            br { }

            div {
                classes += "mdc-text-field mdc-text-field--outlined"
                input {
                    style = "text-align: center;font-size: 30px;width: 150px;"
                    classes = setOf("mdc-text-field__input")
                    id = "answerInput"
                    autoComplete = false
                    onInputFunction = {
                        val input = document.getElementById("answerInput") as HTMLInputElement
                        input.focus()
                        if (game.points().length == input.value.length) {
                            answer(input.value)
                            window.setTimeout({
                                next(currentTimeMillis())
                            }, 1000)
                        }
                    }
                }
                div {
                    classes += "mdc-notched-outline mdc-notched-outline--no-label"
                    div {
                        classes += "mdc-notched-outline__leading"
                    }
                    div {
                        classes += "mdc-notched-outline__trailing"
                    }
                }
            }
        }
        val input = document.getElementById("answerInput") as HTMLInputElement
        input.focus()
    }

    override fun showBoringChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text(gameTitle)
            }
            br {}
            br {}
            br {}
            br {}
            div {
                classes += "mdc-typography--headline5"
                text(game.calculation)
            }
            br {}
            div {
                classes += "mdc-text-field mdc-text-field--outlined"
                input {
                    style = "text-align: center;font-size: 30px;width: 150px;"
                    classes = setOf("mdc-text-field__input")
                    id = "answerInput"
                    autoComplete = false
                    onInputFunction = {
                        val input = document.getElementById("answerInput") as HTMLInputElement
                        input.focus()
                        console.log(input.value)
                        if (game.isCorrect(input.value)) {
                            answer(input.value)
                            window.setTimeout({
                                next(currentTimeMillis())
                            }, 1000)
                        }
                    }
                }
                div {
                    classes += "mdc-notched-outline mdc-notched-outline--no-label"
                    div {
                        classes += "mdc-notched-outline__leading"
                    }
                    div {
                        classes += "mdc-notched-outline__trailing"
                    }
                }
            }
            br {}
            br {}
            button {
                style = "width: 150px"
                classes += "mdc-button mdc-button--raised"
                text("Give up")
                onClickFunction = {
                    answer("")
                    window.setTimeout({
                        next(currentTimeMillis())
                    }, 1000)
                }
            }
            br {}
        }
        val input = document.getElementById("answerInput") as HTMLInputElement
        input.focus()
    }

    override fun showSherlockCalculation(
        game: SherlockCalculationGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text(gameTitle)
            }
            br {}
            br {}
            br {}
            br {}
            div {
                classes += "mdc-typography--headline4"
                text("Goal: ${game.result}")
            }
            div {
                classes += "mdc-typography--headline5"
                text("Numbers: ${game.getNumbersString()}")
            }
            br {}
            div {
                classes += "mdc-text-field mdc-text-field--outlined"
                input {
                    style = "text-align: center;font-size: 30px;width: 150px;"
                    classes = setOf("mdc-text-field__input")
                    id = "answerInput"
                    autoComplete = false
                    onInputFunction = {
                        val input = document.getElementById("answerInput") as HTMLInputElement
                        input.focus()
                        console.log(input.value)
                        if (game.isCorrect(input.value)) {
                            answer(input.value)
                            window.setTimeout({
                                next(currentTimeMillis())
                            }, 1000)
                        }
                    }
                }
                div {
                    classes += "mdc-notched-outline mdc-notched-outline--no-label"
                    div {
                        classes += "mdc-notched-outline__leading"
                    }
                    div {
                        classes += "mdc-notched-outline__trailing"
                    }
                }
            }
            br {}
            br {}
            button {
                style = "width: 150px"
                classes += "mdc-button mdc-button--raised"
                text("Give up")
                onClickFunction = {
                    answer("")
                    window.setTimeout({
                        next(currentTimeMillis())
                    }, 1000)
                }
            }
            br {}
        }
        val input = document.getElementById("answerInput") as HTMLInputElement
        input.focus()
    }

    override fun showCorrectAnswerFeedback() {
        document.body = document.create.body {
            style = "text-align: center; margin: 0px; height: 100%"
            div {
                classes += "mdc-typography--headline2"
                style = "padding-top: 24px;"
                text(gameTitle)
            }
            br {}
            br {}
            img {
                src = "images/welcome.svg"
                width = "400px"
            }
        }
    }

    override fun showWrongAnswerFeedback() {
        document.body = document.create.body {
            style = "text-align: center; margin: 0px; height: 100%"
            div {
                classes += "mdc-typography--headline2"
                style = "padding-top: 24px;"
                text(gameTitle)
            }
            br {}
            br {}
            img {
                src = "images/searching.svg"
                width = "400px"
            }
        }
    }

    override fun showFinishFeedback(rank: String, plays: Int, random: () -> Unit) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text(gameTitle)
            }
            br { }
            br { }
            img {
                src = "images/success.svg"
                width = "400px"
            }
            br { }
            div {
                classes += "mdc-typography--headline5"
                text("You scored better than $rank% of the other players.")
            }
            br { }
            br { }
            button {
                style = "width: 250px"
                classes += "mdc-button mdc-button--raised"
                text("Random game")
                onClickFunction = {
                    random()
                }
            }
        }
    }

    private fun Shape.getIconResource(): String {
        return when (this) {
            Shape.SQUARE -> "crop_square"
            Shape.CIRCLE -> "brightness_1"
            Shape.TRIANGLE -> "change_history"
            Shape.HEART -> "favorite"
        }
    }
}