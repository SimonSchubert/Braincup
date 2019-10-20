package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppController
import com.inspiredandroid.braincup.app.AppInterface
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getHex
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

    override fun showHeightComparison(
        game: HeightComparisonGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {

    }

    override fun showFractionCalculation(
        game: FractionCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
    }

    private val appController = AppController(this)

    var gameTitle = ""

    init {
        appController.start()
    }

    override fun showMainMenu(
        title: String,
        description: String,
        games: List<GameType>,
        instructions: (GameType) -> Unit,
        score: (GameType) -> Unit
    ) {
        window.addEventListener("popstate", {
            showMainMenu(title, description, games, instructions, score)
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
            val storage = UserStorage()
            div {
                style = "margin: auto;"
                games.forEach { game ->
                    br { }
                    button {
                        style = "width: 300px; height: 50px; font-size: 16px; margin-top: 16px; margin-right: 6px"
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
                            instructions(game)
                        }
                    }
                    val highscore = storage.getHighScore(game.getId())
                    if (highscore > 0) {
                        button {
                            style = "width: 85px; height: 50px; font-size: 16px; margin-top: 16px; margin-left: 6px"
                            classes += "mdc-button mdc-button--raised"
                            img {
                                classes += "material-icons mdc-button__icon"
                                src = game.getMedalResource(highscore)
                                style = "height: 20px; width: 20px;"
                            }
                            span {
                                classes += "mdc-button__label"
                                text(highscore)
                            }
                            onClickFunction = {
                                score(game)
                            }
                        }
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
            br {}
            br {}
            a {
                href = "https://github.com/SimonSchubert/Braincup"
                target = "_blank"
                img {
                    src = "images/github.png"
                    width = "16px"
                }
            }
        }
    }

    override fun showInstructions(title: String, description: String, start: () -> Unit) {
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
                    start()
                }
            }
        }
    }

    override fun showMentalCalculation(
        game: MentalCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
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
                        if (game.getNumberLength() == input.value.length) {
                            answer(input.value)
                            window.setTimeout({
                                next()
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
        next: () -> Unit
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
                style = "font-size: 144px; color: ${game.displayedColor.getHex()};"
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
                    text("${game.shapePoints} = " + game.answerShape.getName())
                }
                br {}
                span {
                    style = "width: 200px"
                    classes += "mdc-typography--headline5"
                    text("${game.colorPoints} = ")
                    span {
                        style = "color: ${game.stringColor.getHex()};"
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
                                next()
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

    override fun showChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
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
                        if (game.isCorrect(input.value)) {
                            answer(input.value)
                            window.setTimeout({
                                next()
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
                        next()
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
        next: () -> Unit
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
                        if (game.isCorrect(input.value)) {
                            answer(input.value)
                            window.setTimeout({
                                next()
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
                        next()
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

    override fun showWrongAnswerFeedback(solution: String) {
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
            br {}
            br {}
            div {
                classes += "mdc-typography--headline5"
                text("the answer was $solution")
            }
        }
    }

    override fun showFinishFeedback(
        rank: String,
        newHighscore: Boolean,
        plays: Int,
        random: () -> Unit
    ) {
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
            if (newHighscore) {
                br { }
                div {
                    classes += "mdc-typography--headline3"
                    text("New highscore")
                }
            }
            br { }
            div {
                classes += "mdc-typography--headline5"
                text("Score: $rank")
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

    override fun showScoreboard(
        game: GameType,
        highscore: Int,
        scores: List<Pair<String, List<Int>>>
    ) {
        gameTitle = game.getName()
        window.history.pushState(
            null,
            "",
            "${gameTitle.toLowerCase().removeWhitespaces()}_score.html"
        )
        document.title = "$gameTitle score - Braincup"
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            div {
                classes += "mdc-typography--headline2"
                text("$gameTitle - Scores")
            }
            br {}
            br {}

            div {
                classes += "mdc-typography--headline4"
                text("Highscore: $highscore")
            }

            br {}

            div {
                style = "display: flex;margin: auto;justify-content: center;align-items: center;"
                div {
                    classes += "mdc-typography--headline6"
                    text("> 0")
                }
                img {
                    style = "height: 25px; width: 25px;"
                    classes += "material-icons"
                    src = MEDAL_THIRD_RESOURCE
                }
                div {
                    style = "margin-left:16px;"
                    classes += "mdc-typography--headline6"
                    text("> ${game.getScoreTable()[1] - 1} ")
                }
                img {
                    style = "height: 25px; width: 25px;"
                    classes += "material-icons"
                    src = MEDAL_SECOND_RESOURCE
                }
                div {
                    style = "margin-left:16px;"
                    classes += "mdc-typography--headline6"
                    text("    > ${game.getScoreTable()[0] - 1} ")
                }
                img {
                    style = "height: 25px; width: 25px;"
                    classes += "material-icons"
                    src = MEDAL_FIRST_RESOURCE
                }
            }

            br {}

            div {
                style = "width: 600px; margin: auto;"

                scores.forEach {
                    div {
                        classes += "mdc-typography--headline5"
                        text(it.first)
                    }
                    it.second.forEach { score ->
                        div {
                            style =
                                "width: ${score * 10}px; min-width: 50px; height: 30px; background: #ED7354;display: flex;align-items: center;"
                            div {
                                style = "color: var(--mdc-theme-on-primary, #fff);margin-left: 8px;"
                                classes += "mdc-typography--headline6"
                                text(score)
                            }
                            img {
                                style = "height: 25px; width: 25px;"
                                classes += "material-icons"
                                src = game.getMedalResource(score)
                            }
                        }
                        br { }
                    }
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