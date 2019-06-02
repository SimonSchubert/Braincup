package com.inspiredandroid.braincup

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

class JsMain : Gamemaster.Interface {

    val gamemaster = Gamemaster(this)

    init {
        gamemaster.start()
    }

    override fun showInstructions(title: String, description: String, start: (Long) -> Unit) {
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

    override fun showMainMenu(title: String, description: String, games: List<Game>, callback: (Game) -> Unit) {
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
        }
    }

    override fun showMentalCalculation(
        game: MentalCalculation,
        title: String,
        showValue: Boolean,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text(title)
            }
            br { }
            br { }
            br {}
            br {}
            br {}
            div {
                classes += "mdc-typography--headline4"
                if (showValue) {
                    text("${game.number} " + game.nextCalculation())
                } else {
                    text(game.nextCalculation())
                }
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
        round: ColorConfusion.Round,
        title: String,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text(title)
            }
            br { }
            br { }
            i {
                style = "font-size: 144px; color:${round.displayedColor.getName()};"
                classes += "material-icons"
                text(round.displayedShape.getIconResource())
            }
            br { }
            br { }

            div {
                style = "display: inline-block; text-align: left;"
                span {
                    style = "width: 200px"
                    classes += "mdc-typography--headline5"
                    text("${round.shapePoints}: is " + round.answerShape.getName())
                }
                br {}
                span {
                    style = "width: 200px"
                    classes += "mdc-typography--headline5"
                    text("${round.colorPoints}: is ")
                    span {
                        style = "color:${round.stringColor.getName()};"
                        text(round.answerColor.getName())
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
                        if (round.points().length == input.value.length) {
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

    override fun showCorrectAnswerFeedback(title: String) {
        document.body = document.create.body {
            style = "text-align: center; margin: 0px; height: 100%"
            div {
                classes += "mdc-typography--headline2"
                style = "padding-top: 24px;"
                text(title)
            }
            br {}
            br {}
            img {
                src = "images/welcome.svg"
                width = "400px"
            }
        }
    }

    override fun showWrongAnswerFeedback(title: String) {
        document.body = document.create.body {
            style = "text-align: center; margin: 0px; height: 100%"
            div {
                classes += "mdc-typography--headline2"
                style = "padding-top: 24px;"
                text(title)
            }
            br {}
            br {}
            img {
                src = "images/searching.svg"
                width = "400px"
            }
        }
    }

    override fun showFinishFeedback(rank: String, title: String, plays: Int, random: () -> Unit) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text(title)
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
            if (plays < 3) {
                div {
                    classes += "mdc-typography--headline6"
                    text("You have ${3 - plays} more games to go to achieve your daily goal.")
                }
            } else {
                div {
                    text("Awesome! You have achieved your daily goal.")
                }
            }
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