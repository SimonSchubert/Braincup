package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppController
import com.inspiredandroid.braincup.app.AppInterface
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.getHex
import com.inspiredandroid.braincup.games.tools.getName
import com.inspiredandroid.braincup.games.tools.getPaths
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.body
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onInputFunction
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Path2D
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.min

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
        instructions: (GameType) -> Unit,
        score: (GameType) -> Unit,
        achievements: () -> Unit,
        storage: UserStorage,
        totalScore: Int,
        appOpenCount: Int
    ) {
        window.addEventListener("popstate", {
            showMainMenu(
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
        })
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            title(this, title)
            div {
                style = "margin-top: 32px"
                classes += "mdc-typography--headline6"
                text(description)
            }
            div {
                style = "margin: auto;"
                games.forEach { game ->
                    br { }
                    button {
                        style =
                            "width: 300px; max-width: 70%; height: 50px; font-size: 16px; margin-top: 16px; margin-right: 6px"
                        classes += "mdc-button mdc-button--raised"
                        img {
                            classes += "material-icons mdc-button__icon"
                            src = "images/${game.getImageResource()}"
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
                            style =
                                "width: 85px; height: 50px; font-size: 16px; margin-top: 16px; margin-left: 6px"
                            classes += "mdc-button mdc-button--raised"
                            img {
                                classes += "material-icons mdc-button__icon"
                                src = "images/${game.getMedalResource(highscore)}"
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

            /*
            div {
                val unlockedAchievements = storage.getUnlockedAchievements()
                button {
                    style =
                        "width: 300px; max-width: 70%; height: 50px; font-size: 16px; margin-top: 16px; margin-right: 6px"
                    classes += "mdc-button mdc-button--raised"
                    span {
                        classes += "mdc-button__label"
                        text("Achievements (${unlockedAchievements.size}/${UserStorage.Achievements.values().size})")
                    }
                    onClickFunction = {
                        achievements()
                    }
                }
            }
            */

            if (appOpenCount > 0) {
                div {
                    classes += "border_box"
                    style = "margin-top: 32px"
                    span {
                        classes += "mdc-typography--headline6"
                        text("Consecutive training")
                    }
                    br {}
                    span {
                        classes += "mdc-typography--headline4"
                        text(appOpenCount)
                    }
                }
            }

            if (totalScore > 0) {
                div {
                    classes += "border_box"
                    style = "margin-top: 32px"
                    span {
                        classes += "mdc-typography--headline6"
                        text("Total score")
                    }
                    br {}
                    span {
                        classes += "mdc-typography--headline4"
                        text(totalScore)
                    }
                }
            }

            img {
                classes += "illustration"
                src = "images/waiting.svg"
            }

            div {
                classes += "border_box"
                style = "width: calc(100% - 32px); max-width: 500px;"
                div {
                    classes += "mdc-typography--headline4"
                    text("Download")
                }
                div {
                    a {
                        href =
                            "https://apps.apple.com/us/app/braincup/id1483376887#?platform=iphone"
                        target = "_blank"
                        img {
                            style = "margin-top: 16px; margin-right: 4px;"
                            src = "images/app_store.png"
                            height = "48px"
                        }
                    }
                    a {
                        href =
                            "https://play.google.com/store/apps/details?id=com.inspiredandroid.braincup"
                        target = "_blank"
                        img {
                            style = "margin-top: 16px; margin-left: 4px;"
                            src = "images/play_store.png"
                            height = "48px"
                        }
                    }
                }
                div {
                    classes += "mdc-typography--headline5"
                    text("macOS homebrew:")
                }
                div {
                    code {
                        text("brew tap SimonSchubert/braincup && brew install SimonSchubert/braincup/braincup")
                    }
                }
                a {
                    href = "https://github.com/SimonSchubert/Braincup"
                    target = "_blank"
                    img {
                        style = "margin-top: 16px; margin-bottom: 8px;"
                        src = "images/github.png"
                        width = "32px"
                    }
                }
                a {
                    href = "https://gitlab.com/Simon_Schubert/Braincup"
                    target = "_blank"
                    img {
                        style = "margin-top: 16px"
                        src = "images/gitlab.svg"
                        width = "48px"
                    }
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
            title(this)
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
            title(this)
            div {
                classes += "mdc-typography--headline4"
                style = "margin-top: 128px"
                text(game.calculation)
            }
            br {}
            answerInput(this) {
                if (game.getNumberLength() == it.length) {
                    answer(it)
                    window.setTimeout({
                        next()
                    }, 1000)
                }
            }
        }
        focusAnswerInput()
    }

    override fun showColorConfusion(
        game: ColorConfusionGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            title(this)
            div {
                style = "margin-top: 32px"
                id = "canvas"
            }

            div {
                style = "display: inline-block; text-align: left; margin-top: 16px"
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
            answerInput(this) {
                if (game.points().length == it.length) {
                    answer(it)
                    window.setTimeout({
                        next()
                    }, 1000)
                }
            }
        }
        focusAnswerInput()

        // TODO: there must be a better way to do that
        val canvas = document.createElement("canvas") as HTMLCanvasElement
        canvas.drawFigure(Figure(game.displayedShape, game.displayedColor))
        document.getElementById("canvas")?.appendChild(canvas)
    }

    override fun showChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            title(this)
            div {
                style = "margin-top: 128px"
                classes += "mdc-typography--headline4"
                text(game.calculation)
            }
            br {}
            answerInput(this) {
                if (game.isCorrect(it)) {
                    answer(it)
                    window.setTimeout({
                        next()
                    }, 1000)
                }
            }
            br {}
            br {}
            giveUpButton(this, answer, next)
            br {}
        }
        focusAnswerInput()
    }

    override fun showSherlockCalculation(
        game: SherlockCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            title(this)
            div {
                style = "margin-top: 128px"
                classes += "mdc-typography--headline4"
                text("Goal: ${game.result}")
            }
            div {
                classes += "mdc-typography--headline5"
                text("Numbers: ${game.getNumbersString()}")
            }
            br {}
            answerInput(this) {
                if (game.isCorrect(it)) {
                    answer(it)
                    window.setTimeout({
                        next()
                    }, 1000)
                }
            }
            br {}
            br {}
            giveUpButton(this, answer, next)
            br {}
        }
        focusAnswerInput()
    }

    private fun focusAnswerInput() {
        val input = document.getElementById("answerInput") as HTMLInputElement
        input.focus()
    }

    // TODO: replace with DSL
    private fun title(body: BODY, title: String = gameTitle) {
        body.div {
            classes += "mdc-typography--headline2"
            text(title)
        }
    }

    // TODO: replace with DSL
    private fun giveUpButton(
        body: BODY, answer: (String) -> Unit,
        next: () -> Unit
    ) {
        body.button {
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
    }

    // TODO: replace with DSL
    private fun answerInput(body: BODY, action: (String) -> Unit) {
        body.div {
            classes += "mdc-text-field mdc-text-field--outlined"
            input {
                style = "text-align: center;font-size: 30px;width: 150px;"
                classes = setOf("mdc-text-field__input")
                id = "answerInput"
                autoComplete = false
                onInputFunction = {
                    val input = document.getElementById("answerInput") as HTMLInputElement
                    input.focus()
                    action(input.value)
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

    override fun showHeightComparison(
        game: HeightComparisonGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            title(this)
            br {}
            br {}
            br {}
            game.answers.forEachIndexed { index, s ->
                button {
                    style = "width: 150px; margin-top: 32px"
                    classes += "mdc-button mdc-button--raised"
                    text(s)
                    onClickFunction = {
                        answer("${index + 1}")
                        window.setTimeout({
                            next()
                        }, 1000)
                    }
                }
                br {}
            }
        }
    }

    override fun showFractionCalculation(
        game: FractionCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            title(this)
            div {
                style = "margin-top: 128px"
                classes += "mdc-typography--headline4"
                text(game.calculation)
            }
            br {}
            answerInput(this) {
                if (game.isCorrect(it)) {
                    answer(it)
                    window.setTimeout({
                        next()
                    }, 1000)
                }
            }
            br {}
            br {}
            giveUpButton(this, answer, next)
            br {}
        }
        focusAnswerInput()
    }

    override fun showAnomalyPuzzle(
        game: AnomalyPuzzleGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        val chunkSize = when (game.figures.size) {
            6 -> 3
            9 -> 3
            else -> 4
        }

        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            title(this)

            table {
                style = "margin: auto; margin-top: 64px"
                var index = 0
                game.figures.chunked(chunkSize).forEach {
                    tr {
                        it.forEach {
                            td {
                                id = "canvas$index"
                                style = "padding: 8px; cursor: pointer;"
                            }
                            index++
                        }
                    }
                }
            }
        }

        game.figures.forEachIndexed { index, figure ->
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            canvas.drawFigure(figure)
            canvas.onclick = {
                answer((index + 1).toString())
                window.setTimeout({
                    next()
                }, 1000)
            }
            document.getElementById("canvas$index")?.appendChild(canvas)
        }
    }

    override fun showCorrectAnswerFeedback(hint: String?) {
        document.body = document.create.body {
            style = "text-align: center; margin: 0px; height: 100%"
            div {
                classes += "mdc-typography--headline2"
                style = "padding-top: 24px;"
                text(gameTitle)
            }
            img {
                classes += "illustration"
                style = "margin-top: 64px"
                src = "images/welcome.svg"
            }
            if (hint != null) {
                div {
                    style = "margin-top: 64px"
                    classes += "mdc-typography--headline5"
                    text(hint)
                }
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
            img {
                classes += "illustration"
                style = "margin-top: 64px"
                src = "images/searching.svg"
            }
            div {
                style = "margin-top: 64px"
                classes += "mdc-typography--headline5"
                text("Correct was: $solution")
            }
        }
    }

    override fun showFinishFeedback(
        rank: String,
        newHighscore: Boolean,
        answeredAllCorrect: Boolean,
        plays: Int,
        random: () -> Unit,
        again: () -> Unit
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
                classes += "illustration"
                src = "images/success.svg"
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
                text("Again")
                onClickFunction = {
                    again()
                }
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
            br { }
            br { }
            button {
                style = "width: 250px"
                classes += "mdc-button mdc-button--raised"
                text("Menu")
                onClickFunction = {
                    appController.start()
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
            title(this, "$gameTitle - Scores")
            div {
                style = "margin-top: 64px"
                classes += "mdc-typography--headline4"
                text("Highscore: $highscore")
            }

            br {}

            div {
                style = "display: flex;margin: auto;justify-content: center;align-items: center;"
                classes += "border_box"
                div {
                    classes += "mdc-typography--headline6"
                    text("> 0")
                }
                img {
                    style = "height: 25px; width: 25px;"
                    classes += "material-icons"
                    src = "images/$MEDAL_THIRD_RESOURCE"
                }
                div {
                    style = "margin-left:16px;"
                    classes += "mdc-typography--headline6"
                    text("> ${game.getScoreTable()[1] - 1} ")
                }
                img {
                    style = "height: 25px; width: 25px;"
                    classes += "material-icons"
                    src = "images/$MEDAL_SECOND_RESOURCE"
                }
                div {
                    style = "margin-left:16px;"
                    classes += "mdc-typography--headline6"
                    text("    > ${game.getScoreTable()[0] - 1} ")
                }
                img {
                    style = "height: 25px; width: 25px;"
                    classes += "material-icons"
                    src = "images/$MEDAL_FIRST_RESOURCE"
                }
            }

            br {}
            val goldMedalScore = game.getScoreTable()[0]
            div {
                style = "width: 100%; max-width: 500px; margin: auto;"

                scores.forEach {
                    div {
                        classes += "mdc-typography--headline5"
                        text(it.first)
                    }
                    it.second.forEach { score ->
                        div {
                            val width = min(1f, score.toFloat() / goldMedalScore) * 100f

                            style =
                                "width: ${width}%; min-width: 50px; height: 30px; background: #ED7354;display: flex;align-items: center;"
                            div {
                                style = "color: var(--mdc-theme-on-primary, #fff);margin-left: 8px;"
                                classes += "mdc-typography--headline6"
                                text(score)
                            }
                            img {
                                style = "height: 25px; width: 25px;"
                                classes += "material-icons"
                                src = "images/${game.getMedalResource(score)}"
                            }
                        }
                        br { }
                    }
                }
            }
        }
    }

    override fun showAchievements(
        allAchievements: List<UserStorage.Achievements>,
        unlockedAchievements: List<UserStorage.Achievements>
    ) {

    }

    private fun HTMLCanvasElement.drawFigure(figure: Figure) {
        val context = this.getContext("2d") as CanvasRenderingContext2D
        context.canvas.width = 120
        context.canvas.height = 120

        val path2D = Path2D()
        context.fillStyle = figure.color.getHex()
        figure.shape.getPaths().forEachIndexed { index, pair ->
            val x = (context.canvas.width * pair.first).toDouble()
            val y = (context.canvas.height * pair.second).toDouble()
            if (index == 0) {
                path2D.moveTo(x, y)
            } else {
                path2D.lineTo(x, y)
            }
        }

        if (figure.rotation != 0) {
            context.translate(
                (context.canvas.width / 2f).toDouble(),
                (context.canvas.height / 2f).toDouble()
            )
            context.rotate(figure.rotation * kotlin.math.PI / 180f)
            context.translate(
                (-context.canvas.width / 2f).toDouble(),
                (-context.canvas.height / 2f).toDouble()
            )
        }
        context.fill(path2D)
    }
}