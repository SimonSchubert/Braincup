import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppController
import com.inspiredandroid.braincup.app.AppInterface
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getName
import platform.posix.exit
import platform.posix.sleep

fun main() {
    CliMain()
}

class CliMain : AppInterface {

    private val exitCommands = listOf("quit", "exit", ":q")
    private val gameMaster = AppController(this)

    init {
        gameMaster.start()
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
        printTitle(title)
        println(description)
        println()

        games.forEachIndexed { index, game ->
            println("${index + 1}. ${game.getName()}")
        }
        println()

        val input = readLine()
        if (exitCommands.contains(input)) {
            exit(0)
        } else {
            val index = (input?.toIntOrNull() ?: 0) + -1
            val choice = games.getOrNull(index) ?: GameType.FRACTION_CALCULATION
            instructions(choice)
        }
    }

    override fun showInstructions(title: String, description: String, start: () -> Unit) {
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

    override fun showFinishFeedback(
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

    override fun showCorrectAnswerFeedback(hint: String?) {
        println("√ :)".color(Color.GREEN))
        if (hint != null) {
            println(hint)
        }
    }

    override fun showWrongAnswerFeedback(solution: String) {
        println("x :( - correct was: $solution".color(Color.RED))
    }

    override fun showScoreboard(
        game: GameType,
        highscore: Int,
        scores: List<Pair<String, List<Int>>>
    ) {

    }

    override fun showAchievements(
        allAchievements: List<UserStorage.Achievements>,
        unlockedAchievements: List<UserStorage.Achievements>
    ) {

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
        }
    }

    private fun printAbstractDiamond(color: Color): List<String> {
        return listOf(
            "      * * * ".color(color),
            "    *     * ".color(color),
            "  *       * ".color(color),
            "  *     *   ".color(color),
            "  * * *     ".color(color)
        )
    }

    private fun printAbstractHouse(color: Color): List<String> {
        return listOf(
            "      *      ".color(color),
            "    *   *    ".color(color),
            "  *       *  ".color(color),
            "  *       *  ".color(color),
            "  * * * * *  ".color(color)
        )
    }

    private fun printAbstractTriangle(color: Color): List<String> {
        return listOf(
            "        * *  ".color(color),
            "      *   *  ".color(color),
            "    *     *  ".color(color),
            "  *       *  ".color(color),
            "  * * * * *  ".color(color)
        )
    }

    private fun printAbstractL(color: Color): List<String> {
        return listOf(
            "  * * *      ".color(color),
            "  *   *      ".color(color),
            "  *   * * *  ".color(color),
            "  *       *  ".color(color),
            "  * * * * *  ".color(color)
        )
    }

    private fun printAbstractT(color: Color): List<String> {
        return listOf(
            "    * * *    ".color(color),
            "    *   *    ".color(color),
            "  * *   * *  ".color(color),
            "  *       *  ".color(color),
            "  * * * * *  ".color(color)
        )
    }

    private fun printSquare(color: Color): List<String> {
        return listOf(
            "  * * * * *  ".color(color),
            "  *       *  ".color(color),
            "  *       *  ".color(color),
            "  *       *  ".color(color),
            "  * * * * *  ".color(color)
        )
    }

    private fun printTriangle(color: Color): List<String> {
        return listOf(
            "      *      ".color(color),
            "     * *     ".color(color),
            "    *   *    ".color(color),
            "   *     *   ".color(color),
            "  * * * * *  ".color(color)
        )
    }

    private fun printCircle(color: Color): List<String> {
        return listOf(
            "    *  *     ".color(color),
            "  *      *   ".color(color),
            " *        *  ".color(color),
            "  *      *   ".color(color),
            "    *  *     ".color(color)
        )
    }

    private fun printStar(color: Color): List<String> {
        return listOf(
            "  *   *    * ".color(color),
            "    * * *    ".color(color),
            "  * *   * *  ".color(color),
            "    * * *    ".color(color),
            "  *   *    * ".color(color)
        )
    }

    private fun printHeart(color: Color): List<String> {
        return listOf(
            "   *     *   ".color(color),
            " *    *    * ".color(color),
            "  *       *  ".color(color),
            "    *   *    ".color(color),
            "      *      ".color(color)
        )
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
        }
    }

    private fun getColoredText(code: Int, text: String): String {
        return "$ESCAPE[${code}m$text$RESET"
    }
}