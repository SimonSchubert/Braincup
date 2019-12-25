import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.app.NavigationInterface
import com.inspiredandroid.braincup.challenge.ChallengeUrl
import com.inspiredandroid.braincup.challenge.ChallengeUrlError
import com.inspiredandroid.braincup.challenge.ChallengeUrlResult
import com.inspiredandroid.braincup.challenge.UrlBuilder
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.*
import platform.posix.exit
import platform.posix.sleep

fun main() {
    CliMain()
}

class CliMain : NavigationInterface {

    private val exitCommands = listOf("quit", "exit", ":q")
    private val gameMaster = NavigationController(this)

    init {
        gameMaster.start()
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
        printTitle(title)
        println(description)
        println()

        games.forEachIndexed { index, game ->
            println("${index + 1}. ${game.getName()}")
        }
        println()
        println("${games.size + 1}. Create challenge")
        println()

        while (true) {
            val input = readLine() ?: ""
            if (exitCommands.contains(input)) {
                exit(0)
            } else {
                val index = (input.toIntOrNull() ?: 0) - 1
                if (index == games.size) {
                    createChallenge()
                    return
                } else {
                    val choice = games.getOrNull(index)
                    if (choice != null) {
                        showInstructions(choice)
                        return
                    }
                }
            }
        }
    }

    override fun showInstructions(
        gameType: GameType,
        title: String,
        description: String,
        showChallengeInfo: Boolean,
        hasSecret: Boolean,
        start: () -> Unit
    ) {
        printTitle(title)
        println(description)
        println("You can type \"quit\" and press enter at anytime to go back to the menu.")
        println()
        println("Press enter to start.")

        if (exitCommands.contains(readLine())) {
            gameMaster.start()
        } else {
            start()
        }
    }

    override fun showMentalCalculation(
        game: MentalCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        printDivider()
        println(game.calculation)
        println()

        readAndAnswer(answer, next)
    }

    override fun showChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        printDivider()
        println(game.calculation)
        println()

        readAndAnswer(answer, next)
    }

    override fun showColorConfusion(
        game: ColorConfusionGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        printDivider()

        printFigure(
            Figure(
                game.displayedShape,
                game.displayedColor
            )
        )
        println()
        println("${game.shapePoints} = " + game.answerShape.getName())
        println("${game.colorPoints} = " + game.answerColor.getName().color(game.stringColor))
        println()

        readAndAnswer(answer, next)
    }

    override fun showSherlockCalculation(
        game: SherlockCalculationGame,
        title: String,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        printDivider()
        println("Goal: ${game.result}")
        println("Numbers: ${game.getNumbersString()}")
        println()

        readAndAnswer(answer, next)
    }

    override fun showHeightComparison(
        game: HeightComparisonGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        printDivider()
        game.answers.forEachIndexed { index, s ->
            println("${index + 1}: $s")
        }
        println()

        readAndAnswer(answer, next)
    }

    override fun showFractionCalculation(
        game: FractionCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        printDivider()
        print(game.calculation)
        println()

        readAndAnswer(answer, next)
    }

    override fun showAnomalyPuzzle(
        game: AnomalyPuzzleGame,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        printDivider()

        val chunkSize = when (game.figures.size) {
            6 -> 3
            9 -> 3
            else -> 4
        }

        printFiguresRow(game.figures, chunkSize)
        println()
        readAndAnswer(answer, next)
    }

    override fun showRiddle(
        game: RiddleGame,
        title: String,
        answer: (String) -> Unit,
        next: () -> Unit
    ) {
        printDivider()

        println(game.quest)

        while (true) {
            val input = readLine() ?: ""
            if (exitCommands.contains(input)) {
                gameMaster.start()
                return
            } else {
                if (game.isCorrect(input)) {
                    answer(input)
                    next()
                } else {
                    println("Not quite. Try again.")
                }
            }
        }
    }

    override fun showPathFinder(game: PathFinderGame, answer: (String) -> Unit, next: () -> Unit) {
        printDivider()
        val arrows = mutableListOf<String>()
        game.directions.forEach {
            arrows.merge(it.getFigure().getLines())
        }
        arrows.forEach {
            println("Instructions: $it")
        }
        println()
        printGrid(game.gridSize, game.startX, game.startY)
        println()
        println("At which x,z coordinate does the journey end?")
        readAndAnswer(answer, next)
    }

    private fun printGrid(size: Int, startX: Int, startY: Int) {
        val lines = mutableListOf<String>()

        // Add Y coordinates to the left
        repeat(size) {
            lines.add("  ")
            lines.add("${it + 1} ")
        }

        // Add 'half grid' parts
        repeat(size) {
            lines.merge(getGridPart(size))
        }

        // Add missing bottom line
        val bottom = "* * ".repeat(size)
        lines.add("  $bottom")

        // Add missing tailing line
        val tail = mutableListOf<String>()
        repeat(size) {
            tail.add("*")
            tail.add("*")
        }
        tail.add("*")
        lines.merge(tail)

        // Add X coordinates on top
        var xCoordinates = "  "
        repeat(size) {
            xCoordinates += "  ${it + 1} "
        }
        lines.add(0, xCoordinates)

        val charArray = lines[startY * 2 + 2].toCharArray()
        charArray[startX * 4 + 4] = '*'
        lines[startY * 2 + 2] = String(charArray)

        lines.forEach {
            println(it)
        }
    }

    private fun getGridPart(count: Int): List<String> {
        val parts = mutableListOf<String>()
        repeat(count) {
            parts.add("* * ")
            parts.add("*   ")
        }
        return parts
    }

    override fun showCorrectChallengeAnswerFeedback(solution: String, secret: String, url: String) {
        println("Your solution '$solution' solved the challenge.")
        println("Secret unveiled: $secret")
        waitForEnterAndContinue()
    }

    override fun showWrongChallengeAnswerFeedback(url: String) {
        println("Unsolved")
        println("The challenge will stay unsolved for now.")
        waitForEnterAndContinue()
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
        printDivider()
        if (answeredAllCorrect) {
            println("You got 1 extra point for making zero mistakes.")
        }
        println("Score: $rank")
        println()
        println("1. Play again")
        println("2. Play random game")
        println("3. Menu")
        println()

        val input = readLine() ?: ""
        if (exitCommands.contains(input)) {
            gameMaster.start()
        } else {
            when (input) {
                "1" -> again()
                "2" -> random()
                "3" -> gameMaster.start()
                else -> gameMaster.start()
            }
        }
    }

    override fun showCorrectAnswerFeedback(gameType: GameType, hint: String?) {
        println("√ :)".color(Color.GREEN))
        if (hint != null) {
            println(hint)
        }
    }

    override fun showWrongAnswerFeedback(gameType: GameType, solution: String) {
        println("x :( - correct was: $solution".color(Color.RED))
    }

    override fun showScoreboard(
        gameType: GameType,
        highscore: Int,
        scores: List<Pair<String, List<Int>>>
    ) {

    }

    override fun showAchievements(
        allAchievements: List<UserStorage.Achievements>,
        unlockedAchievements: List<UserStorage.Achievements>
    ) {

    }

    override fun showCreateChallengeMenu(games: List<GameType>, answer: (GameType) -> Unit) {
        println("Create your own challenge and share it with your friends, family and co-workers. You can also hide a secret message which will get unveiled after solving the challenge.")
        println("You can type \"quit\" and press enter at anytime to go back to the menu.")
        println()
        games.forEachIndexed { index, game ->
            println("${index + 1}. ${game.getName()}")
        }
        while (true) {
            val input = readLine() ?: ""
            if (exitCommands.contains(input)) {
                gameMaster.start()
                return
            } else {
                val index = (input.toIntOrNull() ?: 0) - 1
                val choice = games.getOrNull(index)
                if (choice != null) {
                    answer(choice)
                    return
                }
            }
        }
    }

    override fun showCreateSherlockCalculationChallenge(title: String, description: String) {
        println("Create your own Sherlock calculation challenge.")
        println("Title of the challenge(optional, press enter to skip):")
        val challengeTitle = readLine() ?: ""
        println("The secret will be revealed after solving the challenge(optional, press enter to skip):")
        val secret = readLine() ?: ""
        println("The goal that has to be found:")
        val goal = readLine() ?: ""
        println("The allowed numbers to find the goal(separated by comma or space):")
        val answers = readLine() ?: ""

        val result = UrlBuilder.buildSherlockCalculationChallengeUrl(
            challengeTitle,
            secret,
            goal,
            answers
        )
        showCreateChallengeResult(result)
    }

    override fun showCreateRiddleChallenge(title: String) {
        println("Create your own Riddle challenge.")
        println("Title of the challenge(optional, press enter to skip):")
        val challengeTitle = readLine() ?: ""
        println("The secret will be revealed after solving the challenge(optional, press enter to skip):")
        val secret = readLine() ?: ""
        println("Riddle:")
        val riddle = readLine() ?: ""
        println("Answers (separated by comma):")
        val answers = readLine() ?: ""

        val result = UrlBuilder.buildRiddleChallengeUrl(
            challengeTitle,
            secret,
            riddle,
            answers
        )
        showCreateChallengeResult(result)
    }

    private fun showCreateChallengeResult(result: ChallengeUrlResult) {
        when (result) {
            is ChallengeUrl -> {
                println()
                println("Mobile and web url: ${result.url}")
                println()
                println("Command line: braincup --deeplink ${result.url}")
            }
            is ChallengeUrlError -> {
                println(result.errorMessage)
            }
        }
        waitForEnterAndContinue()
    }

    private fun waitForEnterAndContinue() {
        println()
        println("Press enter to continue")
        readLine()
        gameMaster.start()
    }

    private fun readAndAnswer(answer: (String) -> Unit, next: () -> Unit) {
        val input = readLine() ?: ""
        if (exitCommands.contains(input)) {
            gameMaster.start()
        } else {
            answer(input)
            sleep(1u)
            next()
        }
    }

    private fun printFigure(figure: Figure) {
        figure.getLines().forEach {
            println(it)
        }
    }

    private fun printFiguresRow(figures: List<Figure>, chunkSize: Int) {
        var id = 1
        figures.chunked(chunkSize).forEach { chunked ->
            val rowLines = mutableListOf<String>()
            chunked.forEach { figure ->
                rowLines.merge(getIndexLines(id))
                rowLines.merge(figure.getLines())
                id++
            }
            rowLines.forEach { text ->
                println(text)
            }
            println()
        }
    }

    private fun MutableList<String>.merge(data: List<String>) {
        data.forEachIndexed { index, line ->
            if (index >= this.size) {
                this.add("")
            }
            this[index] += line
        }
    }

    /**
     * Index has to be < 100
     */
    private fun getIndexLines(index: Int): List<String> {
        val space = " ".repeat(2 - index.toString().length)
        return listOf(
            " $index$space",
            "   ",
            "   ",
            "   ",
            "   "
        )
    }

    private fun Figure.getLines(): List<String> {
        return this.shape.getLines(this.color, this.rotation)
    }

    private fun Shape.getLines(color: Color, rotation: Int = 0): List<String> {
        return when (this) {
            Shape.SQUARE -> listOf(
                "  * * * * *  ".color(color),
                "  *       *  ".color(color),
                "  *       *  ".color(color),
                "  *       *  ".color(color),
                "  * * * * *  ".color(color)
            )
            Shape.CIRCLE -> listOf(
                "    *  *     ".color(color),
                "  *      *   ".color(color),
                " *        *  ".color(color),
                "  *      *   ".color(color),
                "    *  *     ".color(color)
            )
            Shape.HEART -> listOf(
                "   *     *   ".color(color),
                " *    *    * ".color(color),
                "  *       *  ".color(color),
                "    *   *    ".color(color),
                "      *      ".color(color)
            )
            Shape.STAR -> listOf(
                "  *   *    * ".color(color),
                "    * * *    ".color(color),
                "  * *   * *  ".color(color),
                "    * * *    ".color(color),
                "  *   *    * ".color(color)
            )
            Shape.T -> listOf(
                "    * * *    ".color(color),
                "    *   *    ".color(color),
                "  * *   * *  ".color(color),
                "  *       *  ".color(color),
                "  * * * * *  ".color(color)
            )
            Shape.DIAMOND -> listOf(
                "      * * *  ".color(color),
                "    *     *  ".color(color),
                "  *       *  ".color(color),
                "  *     *    ".color(color),
                "  * * *      ".color(color)
            )
            Shape.HOUSE -> listOf(
                "      *      ".color(color),
                "    *   *    ".color(color),
                "  *       *  ".color(color),
                "  *       *  ".color(color),
                "  * * * * *  ".color(color)
            )
            Shape.ABSTRACT_TRIANGLE -> listOf(
                "        * *  ".color(color),
                "      *   *  ".color(color),
                "    *     *  ".color(color),
                "  *       *  ".color(color),
                "  * * * * *  ".color(color)
            )
            Shape.TRIANGLE -> when (rotation) {
                0 -> listOf(
                    "      *      ".color(color),
                    "     * *     ".color(color),
                    "    *   *    ".color(color),
                    "   *     *   ".color(color),
                    "  * * * * *  ".color(color)
                )
                90 -> listOf(
                    "  *          ".color(color),
                    "  *   *      ".color(color),
                    "  *       *  ".color(color),
                    "  *   *      ".color(color),
                    "  *          ".color(color)
                )
                180 -> listOf(
                    "  * * * * *  ".color(color),
                    "   *     *   ".color(color),
                    "    *   *    ".color(color),
                    "     * *     ".color(color),
                    "      *      ".color(color)
                )
                else -> listOf(
                    "          *  ".color(color),
                    "      *   *  ".color(color),
                    "  *       *  ".color(color),
                    "      *   *  ".color(color),
                    "          *  ".color(color)
                )
            }
            Shape.L -> when (rotation) {
                90 -> listOf(
                    "  * * * * *  ".color(color),
                    "  *       *  ".color(color),
                    "  *   * * *  ".color(color),
                    "  *   *      ".color(color),
                    "  * * *      ".color(color)
                )
                180 -> listOf(
                    "  * * * * *  ".color(color),
                    "  *       *  ".color(color),
                    "  * * *   *  ".color(color),
                    "      *   *  ".color(color),
                    "      * * *  ".color(color)
                )
                270 -> listOf(
                    "      * * *  ".color(color),
                    "      *   *  ".color(color),
                    "  * * *   *  ".color(color),
                    "  *       *  ".color(color),
                    "  * * * * *  ".color(color)
                )
                else -> listOf(
                    "  * * *      ".color(color),
                    "  *   *      ".color(color),
                    "  *   * * *  ".color(color),
                    "  *       *  ".color(color),
                    "  * * * * *  ".color(color)
                )
            }
            Shape.ARROW -> when (rotation) {
                90 -> listOf(
                    " → ".color(color)
                )
                180 -> listOf(
                    " ↓ ".color(color)
                )
                270 -> listOf(
                    " ← ".color(color)
                )
                else -> listOf(
                    " ↑ ".color(color)
                )
            }
        }
    }

    private fun printDivider() {
        println()
        println("-------------------------")
        println()
    }

    private fun printTitle(title: String) {
        val titleDashes = "-".repeat(title.length)
        println("--$titleDashes--")
        println("- $title -")
        println("--$titleDashes--")
    }

    companion object {
        internal const val ESCAPE = '\u001B'
        internal const val RESET = "$ESCAPE[0m"
    }

    private fun String.color(color: Color): String {
        return when (color) {
            Color.RED -> getColoredText(31, this)
            Color.GREEN -> getColoredText(32, this)
            Color.BLUE -> getColoredText(34, this)
            Color.PURPLE -> getColoredText(35, this)
            Color.YELLOW -> getColoredText(93, this)
            Color.ORANGE -> getColoredText(33, this)
            Color.TURKIES -> getColoredText(36, this)
            Color.ROSA -> getColoredText(95, this)
            Color.GREY_DARK -> this
            Color.GREY_LIGHT -> this
        }
    }

    private fun getColoredText(code: Int, text: String): String {
        return "$ESCAPE[${code}m$text$RESET"
    }
}