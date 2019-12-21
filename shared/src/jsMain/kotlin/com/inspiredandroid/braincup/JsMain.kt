package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.app.NavigationInterface
import com.inspiredandroid.braincup.challenge.ChallengeData
import com.inspiredandroid.braincup.challenge.ChallengeUrl
import com.inspiredandroid.braincup.challenge.ChallengeUrlError
import com.inspiredandroid.braincup.challenge.UrlController
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.getHex
import com.inspiredandroid.braincup.games.tools.getName
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.body
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLParagraphElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.min

var code = 0

fun main() {
    // Workaround for dce
    if (code != 0) {
        referenceFunctions()
    }
}

class JsMain(state: AppState, gameType: GameType? = null, challengeData: ChallengeData? = null) :
    NavigationInterface {

    private val appController = NavigationController(this)

    init {
        appController.start(state, gameType, challengeData)
    }

    override fun showMainMenu(
        title: String,
        description: String,
        games: List<GameType>,
        showInstructions: (GameType) -> Unit,
        showScore: (GameType) -> Unit,
        showAchievements: () -> Unit,
        createChallenge: () -> Unit,
        storage: UserStorage,
        totalScore: Int,
        appOpenCount: Int
    ) {
        document.body = base {
            title(title)
            margin(32)
            headline6(description)
            div {
                style = "margin: auto;"
                games.forEach { game ->
                    br { }
                    textButton(
                        text = game.getName(),
                        width = 300,
                        imagePath = "images/${game.getImageResource()}"
                    ) {
                        game.openGameHtml()
                    }
                    val highscore = storage.getHighScore(game.getId())
                    if (highscore > 0) {
                        textButton(
                            text = highscore.toString(),
                            width = 85,
                            imagePath = "images/${game.getMedalResource(highscore)}"
                        ) {
                            game.openScoreboardHtml()
                        }
                    }
                }
            }

            div {
                if (appOpenCount > 1) {
                    pentagonText("Training days", appOpenCount.toString())
                }
                if (totalScore > 0) {
                    pentagonText("Total score", totalScore.toString())
                }
            }

            textButton(
                text = "Create challenge",
                width = 300,
                color = "#5c8e58",
                imagePath = "images/icons8-create_new3.svg"
            ) {
                openCreateChallengeHtml()
            }

            br {}
            illustration("waiting.svg")
            div(classes = "download-info border-box") {
                headline4("Download")
                div {
                    downloadButton(
                        url = "https://apps.apple.com/us/app/braincup/id1483376887#?platform=iphone",
                        imagePath = "images/app_store.png"
                    )
                    downloadButton(
                        url = "https://play.google.com/store/apps/details?id=com.inspiredandroid.braincup",
                        imagePath = "images/play_store.png"
                    )
                }
                headline5("macOS homebrew:")
                div {
                    code {
                        text("brew tap SimonSchubert/braincup && brew install SimonSchubert/braincup/braincup")
                    }
                }
                gitButton(
                    url = "https://github.com/SimonSchubert/Braincup",
                    width = 32,
                    bottomMargin = 8,
                    imagePath = "images/github.png"
                )
                gitButton(
                    url = "https://gitlab.com/Simon_Schubert/Braincup",
                    width = 48,
                    bottomMargin = 0,
                    imagePath = "images/gitlab.svg"
                )
            }
        }
    }

    override fun showInstructions(
        gameType: GameType,
        title: String,
        description: String,
        showChallengeInfo: Boolean,
        start: () -> Unit
    ) {
        document.title = "Braincup - $title"
        document.body = base {
            if (showChallengeInfo) {
                title("You got challenged")
                illustration("message-sent.svg")
                headline3(title)
            } else {
                title(title)
            }
            br { }
            br { }
            headline6(description)
            br { }
            br { }
            textButton(text = "Start") {
                start()
            }
        }
    }

    override fun showMentalCalculation(
        game: MentalCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = base {
            title(game.getGameType().getName())
            margin(128)
            headline4(game.calculation)
            br {}
            textInput {
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
        document.body = base {
            title(game.getGameType().getName())
            margin(32)
            div {
                id = "canvasWrapper"
            }
            margin(16)
            div {
                style = "display: inline-block; text-align: left;"
                headline5("${game.shapePoints} = " + game.answerShape.getName())
                span {
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
            textInput {
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
        canvas.drawFigure(Figure(game.displayedShape, game.displayedColor), 120, 120)
        document.getElementById("canvasWrapper")?.appendChild(canvas)
    }

    override fun showChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = base {
            title(game.getGameType().getName())
            margin(128)
            headline4(game.calculation)
            br {}
            textInput {
                if (game.isCorrect(it)) {
                    answer(it)
                    window.setTimeout({
                        next()
                    }, 1000)
                }
            }
            br {}
            br {}
            giveUpButton(answer, next)
        }
        focusAnswerInput()
    }

    override fun showSherlockCalculation(
        game: SherlockCalculationGame,
        title: String,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = base {
            title(title)
            margin(128)
            headline4("Goal: ${game.result}")
            headline5(game.getNumbersString())
            br {}
            textInput {
                if (game.isCorrect(it)) {
                    answer(it)
                    window.setTimeout({
                        next()
                    }, 1000)
                }
            }
            br {}
            br {}
            giveUpButton(answer, next)
            br {}
        }
        focusAnswerInput()
    }

    override fun showHeightComparison(
        game: HeightComparisonGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = base {
            title(game.getGameType().getName())
            br {}
            game.answers.forEachIndexed { index, s ->
                br {}
                br {}
                textButton(text = s, width = 150) {
                    answer("${index + 1}")
                    window.setTimeout({
                        next()
                    }, 1000)
                }
            }
        }
    }

    override fun showFractionCalculation(
        game: FractionCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = base {
            title(game.getGameType().getName())
            margin(128)
            headline4(game.calculation)
            br {}
            textInput {
                if (game.isCorrect(it)) {
                    answer(it)
                    window.setTimeout({
                        next()
                    }, 1000)
                }
            }
            br {}
            br {}
            giveUpButton(answer, next)
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

        document.body = base {
            title(game.getGameType().getName())
            margin(64)
            table {
                style = "margin: auto"
                var index = 0
                game.figures.chunked(chunkSize).forEach {
                    tr {
                        it.forEach {
                            td {
                                id = "canvasWrapper$index"
                                style = "padding: 8px; cursor: pointer;"
                            }
                            index++
                        }
                    }
                }
            }
        }

        val margin = 48 + chunkSize * 16
        val maxCanvasSize = document.body?.clientWidth?.minus(margin)?.div(chunkSize) ?: 120
        val canvasSize = min(120, maxCanvasSize)

        game.figures.forEachIndexed { index, figure ->
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            canvas.drawFigure(figure, canvasSize, canvasSize)
            canvas.onclick = {
                answer((index + 1).toString())
                window.setTimeout({
                    next()
                }, 1000)
            }
            document.getElementById("canvasWrapper$index")?.appendChild(canvas)
        }
    }

    override fun showRiddle(
        game: RiddleGame,
        title: String,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        document.body = base {
            title(title)
            margin(32)
            headline5(game.description)
            margin(24)
            textInput {
                if (game.isCorrect(it)) {
                    answer(it)
                    window.setTimeout({
                        next()
                    }, 1000)
                }
            }
        }
    }

    override fun showCorrectChallengeAnswerFeedback(solution: String, url: String) {
        document.body = base {
            title("Congratulation")
            margin(24)
            div {
                classes += "mdc-typography--headline5"
                text("You solution ")
                a {
                    style = "cursor: pointer;"
                    text(solution)
                    onClickFunction = {
                        document.copyToClipboard(solution)
                        showSuccessHint("Copied to clipboard")
                    }
                }
                text(" solved the challenge.")
            }
            illustration("delivery.svg")
            br {}
            textButton(
                text = "Share challenge",
                width = 250,
                imagePath = "images/icons8-copy_link.svg"
            ) {
                document.copyToClipboard(url)
                showSuccessHint("Copied to clipboard")
            }
            br {}
            textButton(text = "Menu", width = 250, imagePath = "images/icons8-menu.svg") {
                openIndexHtml()
            }
        }
    }

    override fun showWrongChallengeAnswerFeedback(url: String) {
        document.body = base {
            title("Unsolved")
            margin(24)
            headline5("The challenge will stay unsolved for now.")
            illustration("searching.svg")
            br {}
            textButton(
                text = "Share challenge",
                width = 250,
                imagePath = "images/icons8-copy_link.svg"
            ) {
                document.copyToClipboard(url)
                showSuccessHint("Copied to clipboard")
            }
            br {}
            textButton(text = "Menu", width = 250, imagePath = "images/icons8-menu.svg") {
                openIndexHtml()
            }
        }
    }

    override fun showCorrectAnswerFeedback(gameType: GameType, hint: String?) {
        document.body = base {
            title(gameType.getName())
            margin(64)
            illustration("welcome.svg")
            if (hint != null) {
                margin(64)
                headline5(hint)
            }
        }
    }

    override fun showWrongAnswerFeedback(gameType: GameType, solution: String) {
        document.body = base {
            title(gameType.getName())
            margin(64)
            illustration("searching.svg")
            margin(64)
            headline5("Correct was: $solution")
        }
    }

    override fun showFinishFeedback(
        gameType: GameType,
        rank: String,
        newHighscore: Boolean,
        answeredAllCorrect: Boolean,
        plays: Int,
        random: () -> Unit,
        again: () -> Unit
    ) {
        document.body = base {
            headline2(gameType.getName())
            br { }
            br { }
            illustration("success.svg")
            if (newHighscore) {
                br { }
                headline3("New highscore")
            }
            br { }
            headline5("Score: $rank")
            br { }
            br { }
            textButton(
                text = "Again",
                width = 250,
                imagePath = "images/icons8-recurring_appointment.svg"
            ) {
                gameType.openGameHtml()
            }
            br { }
            textButton(
                text = "Random game",
                width = 250,
                imagePath = "images/icons8-dice.svg"
            ) {
                GameType.values().random().openGameHtml()
            }
            br { }
            textButton(text = "Menu", width = 250, imagePath = "images/icons8-menu.svg") {
                openIndexHtml()
            }
        }
    }

    override fun showScoreboard(
        gameType: GameType,
        highscore: Int,
        scores: List<Pair<String, List<Int>>>
    ) {
        document.title = "Braincup - ${gameType.getName()} score"
        document.body = base {
            title("${gameType.getName()} - Scores")
            margin(64)
            headline4("Highscore: $highscore")
            br {}
            div(classes = "score-keys border-box") {
                headline6("> 0")
                icon(25, "images/$MEDAL_THIRD_RESOURCE")
                margin(left = 16)
                headline6("> ${gameType.getScoreTable()[1] - 1}")
                icon(25, "images/$MEDAL_SECOND_RESOURCE")
                margin(left = 16)
                headline6("> ${gameType.getScoreTable()[0] - 1}")
                icon(25, "images/$MEDAL_FIRST_RESOURCE")
            }
            br {}
            val goldMedalScore = gameType.getScoreTable()[0]
            div {
                style = "width: 100%; max-width: 500px; margin: auto;"
                scores.forEach {
                    headline5(it.first)
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
                            icon(25, "images/${gameType.getMedalResource(score)}")
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

    override fun showCreateChallengeMenu(games: List<GameType>, answer: (GameType) -> Unit) {
        document.title = "Braincup - Create challenge"
        document.body = base {
            title("Create challenge")
            margin(32)
            headline5("Create your own challenge and share it with your friends, family and co-workers.")
            games.forEach { game ->
                br { }
                textButton(
                    text = game.getName(),
                    width = 300,
                    imagePath = "images/${game.getImageResource()}"
                ) {
                    answer(game)
                }
            }
        }
    }

    override fun showCreateSherlockCalculationChallenge(title: String, description: String) {
        var challengeTitle = ""
        var goal = ""
        var numbers = ""

        document.title = "Braincup - $title"
        document.body = document.create.body {
            style = "text-align: center; margin: 24px"
            title(title)
            margin(32)

            div {
                classes += "mdc-typography--headline5"
                text("Challenge your friends with your own ")
                a {
                    text("Sherlock calculation")
                    href = "sherlockcalculation.html"
                    target = "_self"
                }
                text(" task. Challenges for other game types will follow.")
            }

            margin(48)
            headline4("Title")
            textInput(width = 350) {
                challengeTitle = it
            }
            helperText("Title of the challenge is optional")

            margin(48)
            headline4("Goal")
            textInput(width = 150) {
                goal = it
            }

            margin(32)
            headline4("Allowed numbers")
            textInput(width = 350) {
                numbers = it
            }
            helperText("Separated by comma or space")
            br {}

            margin(48)
            textButton(
                text = "Copy link to clipboard",
                imagePath = "images/icons8-copy_link.svg"
            ) {
                val result = UrlController.buildSherlockCalculationChallengeUrl(
                    challengeTitle,
                    goal,
                    numbers
                )
                when (result) {
                    is ChallengeUrl -> {
                        document.copyToClipboard(result.url)
                        showSuccessHint("Copied to clipboard")
                    }
                    is ChallengeUrlError -> {
                        showErrorHint(result.errorMessage)
                    }
                }
            }
        }
    }

    override fun showCreateRiddleChallenge(title: String) {
        var description = ""
        var answers = ""
        var challengeTitle = ""

        document.title = "Braincup - $title"
        document.body = base {
            title(title)
            margin(32)
            headline5("Challenge your friends with a Riddle. Challenges for other game types will follow.")

            margin(48)
            headline4("Title")
            textInput(width = 350) {
                challengeTitle = it
            }
            helperText("Title of the riddle is optional")

            margin(48)
            headline4("Description")
            multilineTextInput(width = 350) {
                description = it
            }

            margin(32)
            headline4("Answers")
            textInput(width = 350) {
                answers = it
            }
            helperText("Separated by comma or space")
            br {}

            margin(48)
            textButton(
                text = "Copy link to clipboard",
                imagePath = "images/icons8-copy_link.svg"
            ) {
                val result =
                    UrlController.buildRiddleChallengeUrl(challengeTitle, description, answers)
                when (result) {
                    is ChallengeUrl -> {
                        document.copyToClipboard(result.url)
                        showSuccessHint("Copied to clipboard")
                    }
                    is ChallengeUrlError -> {
                        showErrorHint(result.errorMessage)
                    }
                }
            }
        }
    }

    private fun openIndexHtml() {
        window.open("", target = "_self")
    }

    private fun openCreateChallengeHtml() {
        window.open("create_challenge.html", target = "_self")
    }

    private fun focusAnswerInput() {
        val input = document.getElementById("answerInput") as HTMLInputElement
        input.focus()
    }

    private fun showErrorHint(message: String) {
        showHint(message, "error")
    }

    private fun showSuccessHint(message: String) {
        showHint(message, "success")
    }

    private fun showHint(message: String, cssClass: String) {
        val te = document.createElement("p") as HTMLParagraphElement
        te.innerHTML = message
        te.classList.add("fade_in_and_out")
        te.classList.add("hint")
        te.classList.add(cssClass)
        document.body?.appendChild(te)
    }
}
