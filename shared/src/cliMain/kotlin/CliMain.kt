import com.inspiredandroid.braincup.app.AppController
import com.inspiredandroid.braincup.app.AppInterface
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getName
import platform.posix.sleep
import kotlin.system.getTimeMillis

fun main() {
    CliMain()
}

class CliMain : AppInterface {

    private val gameMaster = AppController(this)

    init {
        gameMaster.start()
    }

    override fun showMainMenu(
        title: String,
        description: String,
        games: List<Game.Type>,
        callback: (Game.Type) -> Unit
    ) {
        printTitle(title)
        println(description)
        println()

        games.forEachIndexed { index, game ->
            println("${index + 1}. ${game.getName()}")
        }

        val index = (readLine()?.toIntOrNull() ?: 0) + -1
        val choice = games.getOrNull(index) ?: Game.Type.BORING_CHAIN_CALCULAITON
        callback(choice)
    }

    override fun showInstructions(title: String, description: String, start: (Long) -> Unit) {
        printTitle(title)
        println(description)
        println("You can type \"quit\" and press enter at anytime to go back to the menu.")
        println()
        println("Press enter to start.")
        readLine()
        start(getTimeMillis())
    }

    override fun showMentalCalculation(
        game: MentalCalculationGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        printDivider()
        println(game.calculation)
        println()

        answer(readLine() ?: "")
        sleep(1u)
        next(getTimeMillis())
    }

    override fun showBoringChainCalculation(
        game: BoringChainCalculation,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        printDivider()
        println(game.calculation)
        println()

        answer(readLine() ?: "")
        sleep(1u)
        next(getTimeMillis())
    }

    override fun showColorConfusion(
        game: ColorConfusionGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        printDivider()

        when (game.displayedShape) {
            Shape.SQUARE -> printSquare(game.displayedColor)
            Shape.TRIANGLE -> printTriangle(game.displayedColor)
            Shape.CIRCLE -> printCircle(game.displayedColor)
            Shape.HEART -> printHeart(game.displayedColor)
        }

        println()
        println("${game.shapePoints} = " + game.answerShape.getName())
        println("${game.colorPoints} = " + game.answerColor.getName().color(game.stringColor))
        println()

        answer(readLine() ?: "")
        sleep(1u)
        next(getTimeMillis())
    }

    override fun showSherlockCalculation(
        game: SherlockCalculationGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    ) {
        printDivider()
        println("Goal: ${game.result}")
        println("Numbers: ${game.numbers.joinToString()}")
        println()

        answer(readLine() ?: "")
        sleep(1u)
        next(getTimeMillis())
    }

    override fun showFinishFeedback(rank: String, plays: Int, random: () -> Unit) {
        printDivider()
        println("Score: $rank")
    }

    override fun showCorrectAnswerFeedback() {
        println("√ :)".color(Color.GREEN))
    }

    override fun showWrongAnswerFeedback() {
        println("x :(".color(Color.RED))
    }

    private fun printSquare(color: Color) {
        println(" _________".color(color))
        println(" |       |".color(color))
        println(" |       |".color(color))
        println(" |       |".color(color))
        println(" |_______|".color(color))
    }

    private fun printTriangle(color: Color) {
        println("    /\\".color(color))
        println("   /  \\".color(color))
        println("  /    \\".color(color))
        println(" /      \\".color(color))
        println(" --------".color(color))
    }

    private fun printCircle(color: Color) {
        println("    *  *".color(color))
        println("  *      *".color(color))
        println(" *        *".color(color))
        println("  *      *".color(color))
        println("    *  *".color(color))
    }

    private fun printHeart(color: Color) {
        println("   *     *".color(color))
        println(" *    *    *".color(color))
        println("  *       *".color(color))
        println("    *   *".color(color))
        println("      * ".color(color))
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
            Color.RED -> "$ESCAPE[31m$this$RESET"
            Color.GREEN -> "$ESCAPE[32m$this$RESET"
            Color.BLUE -> "$ESCAPE[34m$this$RESET"
            Color.PURPLE -> "$ESCAPE[35m$this$RESET"
        }
    }
}