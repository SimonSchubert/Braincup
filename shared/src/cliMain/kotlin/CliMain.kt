import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.app.NavigationInterface
import com.inspiredandroid.braincup.app.Version
import com.inspiredandroid.braincup.bold
import com.inspiredandroid.braincup.challenge.*
import com.inspiredandroid.braincup.color
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.getFigure
import com.inspiredandroid.braincup.games.tools.getName
import com.inspiredandroid.braincup.getLines
import com.inspiredandroid.braincup.merge
import io.ktor.http.Url
import platform.posix.exit
import platform.posix.sleep

data class CliArgument(val id: ID, val options: List<String>, val description: String) {
    enum class ID {
        DEEPLINK,
        GAME,
        VERSION,
        HELP
    }
}

val arguments by lazy {
    listOf(
        CliArgument(
            CliArgument.ID.VERSION,
            listOf("--version", "-v"),
            "Version of the application"
        ),
        CliArgument(CliArgument.ID.HELP, listOf("--help", "-h"), "Print help"),
        CliArgument(CliArgument.ID.DEEPLINK, listOf("--deeplink", "-d"), "Open web deeplink"),
        CliArgument(
            CliArgument.ID.GAME,
            listOf("--game", "-g"),
            "Start game immediately\n${GameType.values()
                .joinToString(separator = "\n") { "\t${it.getId()} = ${it.getName()}" }}"
        )
    )
}

fun main(args: Array<String>) {
    args.forEachIndexed { index, s ->
        if (s.startsWith("-")) {
            when (arguments.firstOrNull { it.options.contains(s) }?.id) {
                CliArgument.ID.DEEPLINK -> {
                    val url = args.getOrNull(index + 1)?.trim()
                    if (url != null) {
                        val base64Data = Url(url).parameters["data"]
                        if (base64Data != null) {
                            val challengeData = ChallengeData.parse(url = url, data = base64Data)
                            if (challengeData !is ChallengeDataParseError) {
                                CliMain(
                                    appState = AppState.CHALLENGE,
                                    challengeData = challengeData
                                )
                                return
                            }
                        }
                    }
                }
                CliArgument.ID.GAME -> {
                    val gameId = args.getOrNull(index + 1)?.trim()
                    val gameType = GameType.values().firstOrNull { it.getId() == gameId }
                    if (gameType != null) {
                        CliMain(AppState.INSTRUCTIONS, gameType)
                        return
                    }
                }
                CliArgument.ID.VERSION -> {
                    println("Version ${Version.name}")
                    return
                }
                CliArgument.ID.HELP -> {
                    arguments.forEach {
                        println()
                        println(it.options.joinToString(separator = ",").bold())
                        println("\t${it.description}")
                    }
                    return
                }
            }
        }
    }
    CliMain(AppState.START)
}

class CliMain(
    appState: AppState,
    gameType: GameType? = null,
    challengeData: ChallengeData? = null
) : NavigationInterface {

    private val exitCommands by lazy { listOf("quit", "exit", ":q") }
    private val gameMaster = NavigationController(this)

    init {
        gameMaster.start(appState, gameType, challengeData)
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
        if (showChallengeInfo) {
            println("You got challenged")
            if (hasSecret) {
                println("The challenge will unveil a secret.")
            }
            println()
        }
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

    override fun showValueComparison(
        game: ValueComparisonGame,
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

    override fun showGridSolver(game: GridSolverGame, answer: (String) -> Unit, next: () -> Unit) {
        printDivider()
        repeat(game.entries.size) {
            println("  *" + " * *".repeat(game.entries.size))
            println("  *" + "   *".repeat(game.entries.size) + " " + game.resultsY[it])
        }
        println("  *" + " * *".repeat(game.entries.size))
        print("  ")
        repeat(game.resultsX.size) {
            print("  ${game.resultsX[it]} ".substring(0, 4))
        }
        println()
        println()

        println("Type the missing numbers from top left to bottom right separated by spaces.")
        readAndAnswer(answer, next)
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

    override fun showCorrectChallengeAnswerFeedback(solution: String, secret: String, url: String) {
        printDivider()
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
                rowLines.merge(getFigureIndexLines(id))
                rowLines.merge(figure.getLines())
                id++
            }
            rowLines.forEach { text ->
                println(text)
            }
            println()
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
        lines[startY * 2 + 2] = charArray.concatToString()

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

    /**
     * Index has to be < 100
     */
    private fun getFigureIndexLines(index: Int): List<String> {
        val space = " ".repeat(2 - index.toString().length)
        return listOf(
            " $index$space",
            "   ",
            "   ",
            "   ",
            "   "
        )
    }
}
