import com.inspiredandroid.braincup.*
import platform.posix.sleep
import kotlin.system.getTimeMillis

fun main() {
    MacMain()
}

class MacMain : Gamemaster.Interface {

    val gamemaster = Gamemaster(this)

    init {
        gamemaster.start()
    }

    override fun showMainMenu(title: String, description: String, games: List<Game>, callback: (Game) -> Unit) {
        println("-----------")
        println("- $title -")
        println("-----------")
        println(description)

        games.forEachIndexed { index, game ->
            println("${index + 1}. ${game.getName()}")
        }

        val index = readLine()?.toIntOrNull() ?: -1
        val choice = games.getOrNull(index) ?: Game.MENTAL_CALCULATION
        callback(choice)
    }

    override fun showInstructions(title: String, description: String, start: (Long) -> Unit) {
        println("-----------------------")
        println("- $title  -")
        println("-----------------------")
        println(description)
        println("")
        readLine()
        start(getTimeMillis())
    }

    override fun showMentalCalculation(
        game: MentalCalculation,
        title: String,
        showValue: Boolean,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        println(game.nextCalculation())
        answer(readLine() ?: "")
        sleep(1)
        next(getTimeMillis())
    }

    override fun showColorConfusion(
        round: ColorConfusion.Round,
        title: String,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        when (round.displayedShape) {
            Shape.SQUARE -> printSquare(round.displayedColor)
            Shape.TRIANGLE -> printTriangle(round.displayedColor)
            Shape.CIRCLE -> printCircle(round.displayedColor)
            Shape.HEART -> printHeart(round.displayedColor)
        }

        println("${round.shapePoints}. " + round.answerShape.getName())
        println("${round.colorPoints}. " + round.answerColor.getName().color(round.stringColor))

        answer(readLine().toString())
        next(getTimeMillis())
    }

    override fun showFinishFeedback(rank: String, title: String, plays: Int, random: () -> Unit) {
        println("")
        println("-----------------------")
        println("You scored better than $rank% of the other players.")
    }

    override fun showCorrectAnswerFeedback(title: String) {
        println("✓")
    }

    override fun showWrongAnswerFeedback(title: String) {
        println(":(")
    }

/*
    private fun colorConfusion() {
        val game = ColorConfusion()

        println("--------------------")
        println("- Color confusion  -")
        println("--------------------")
        println("You will see a colored figure. Under the figure will be 2 statements. Summarize the values of each correct statement. Time limit is 2 minutes.")
        readLine()
        println("")

        val startTime = getTimeMillis()
        var running = true
        while (running) {
            val round = game.nextRound()

            when (round.displayedShape) {
                Shape.SQUARE -> printSquare(round.displayedColor)
                Shape.TRIANGLE -> printTriangle(round.displayedColor)
                Shape.CIRCLE -> printCircle(round.displayedColor)
                Shape.HEART -> printHeart(round.displayedColor)
            }

            sleep(1)
            println("${round.shapePoints}. " + round.answerShape.getShapeName())
            println("${round.colorPoints}. " + round.answerColor.getColorName().color(round.stringColor))

            if (round.isCorrect(readLine().toString())) {
                println("✓")
                points++
            } else {
                println(":( ")
            }

            if (getTimeMillis() - startTime > 120 * 1000) {
                println("")
                println("-----------------------")
                println("You finished the game with a total of $points points.")
                running = false
            }
            sleep(1)
        }

        println("")
        println("")
        showMainScreen()
    }
*/

    private fun printSquare(color: Color) {
        println(" _________".color(color))
        println(" |       |".color(color))
        println(" |       |".color(color))
        println(" |       |".color(color))
        println(" |_______|".color(color))
    }

    private fun printTriangle(color: Color) {
        println("    /\\  ".color(color))
        println("   /  \\".color(color))
        println("  /    \\".color(color))
        println(" /      \\".color(color))
        println(" --------".color(color))
    }

    private fun printCircle(color: Color) {
        println("    *  *    ".color(color))
        println("  *      *  ".color(color))
        println(" *        * ".color(color))
        println("  *      *  ".color(color))
        println("    *  *    ".color(color))
    }

    private fun printHeart(color: Color) {
        println("   *     *    ".color(color))
        println(" *    *    * ".color(color))
        println("  *       *  ".color(color))
        println("    *   *    ".color(color))
        println("      *      ".color(color))
    }

    companion object {
        internal const val ESCAPE = '\u001B'
        internal const val RESET = "$ESCAPE[0m"
    }

    private fun String.color(color: Color): String {
        return when (color) {
            Color.RED -> "$ESCAPE[31m$this$RESET"
            Color.GREEN -> "$ESCAPE[32m$this$RESET"
            Color.BLUE -> "$ESCAPE[34m$this$RESET"
            Color.PURPLE -> "$ESCAPE[35m$this$RESET"
            else -> this
        }
    }

}