package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.FeedbackMessage
import com.inspiredandroid.braincup.app.FigureCell
import com.inspiredandroid.braincup.app.MentalFlexUiState
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

/**
 * Task switching: a cue names the dimension that counts this round, and the board always offers a
 * competing answer on the dimension that does not.
 *
 * Every board holds one candidate matching the target on shape only, one matching it on color
 * only, and distractors matching on neither. Which of the first two is correct depends entirely on
 * [rule], so the very same board has two different answers and a player still running the previous
 * rule lands on the other match rather than on a random tile.
 *
 * The cue is [cueExemplar], a pair of figures demonstrating the rule, rather than the word "shape"
 * or "color" (see `MentalFlexRuleCue`). A printed word is one lexical lookup, so a switch round
 * costs exactly what a repeat round costs and the exercise measures nothing; working out which
 * trait a pair holds constant is the same relational step the board then asks for. It also leaves
 * the board with no text on it at all, in an app shipping 44 locales.
 *
 * Difficulty is derived from [round] rather than stepped at exact values, so a run resumed at a
 * stored round comes back at the difficulty it left off at (see `AdaptiveDifficultyResumeTest`).
 */
class MentalFlexGame(
    private val random: Random = Random.Default,
) : Game() {
    enum class Rule { SHAPE, COLOR }

    var rule: Rule = Rule.SHAPE
        private set

    var target: Figure = Figure(Shape.SQUARE, GameColor.BLUE)
        private set

    var candidates: List<Figure> = emptyList()
        private set

    var correctIndex: Int = 0
        private set

    /** True when [rule] just changed. The switch trials are the ones the exercise is really about. */
    var isSwitch: Boolean = false
        private set

    /**
     * Two figures holding exactly the active trait constant: same shape in two colors for
     * [Rule.SHAPE], two shapes in one color for [Rule.COLOR]. This is the cue, and it states the
     * rule by demonstrating it rather than by naming it.
     *
     * Re-drawn every round. A fixed pair would be memorised as one picture after a few rounds and
     * decay into the same lookup a printed word is; a fresh pair has to be read as a relation each
     * time. Neither figure ever uses the target's own shape or color, so the cue cannot be mistaken
     * for a hint about which tile to tap.
     */
    var cueExemplar: List<Figure> = emptyList()
        private set

    /** Null until the first round is generated, so a resumed run does not report a phantom switch. */
    private var previousRule: Rule? = null

    /**
     * ABSTRACT_TRIANGLE is deliberately absent: it shares the displayName "triangle" with TRIANGLE,
     * so a board holding both could not be described unambiguously when the player gives up. T and
     * L are absent because they are blocky tetromino forms meant for the rotation puzzles, and
     * read as near-identical silhouettes at tile size.
     */
    private val shapes = listOf(
        Shape.SQUARE,
        Shape.TRIANGLE,
        Shape.CIRCLE,
        Shape.HEART,
        Shape.STAR,
        Shape.DIAMOND,
        Shape.HOUSE,
        Shape.ARROW,
    )

    /**
     * ROSA and GREY_LIGHT are left out. Matching by color has to stay a test of attention rather
     * than of eyesight, and those two are the pair most easily confused with RED and with a dimmed
     * tile respectively.
     */
    private val colors = listOf(
        GameColor.RED,
        GameColor.GREEN,
        GameColor.BLUE,
        GameColor.PURPLE,
        GameColor.YELLOW,
        GameColor.ORANGE,
        GameColor.TURQUOISE,
    )

    override fun generateRound() {
        val previous = previousRule
        rule = when {
            previous == null -> if (random.nextBoolean()) Rule.SHAPE else Rule.COLOR
            random.nextFloat() < switchProbability() -> previous.other()
            else -> previous
        }
        isSwitch = previous != null && rule != previous
        previousRule = rule

        val targetShape = shapes.random(random)
        val targetColor = colors.random(random)
        target = Figure(targetShape, targetColor)

        val otherShapes = shapes.filter { it != targetShape }
        val otherColors = colors.filter { it != targetColor }

        // Both matches sit on every board; only the cue decides which one is the answer.
        val shapeMatch = Figure(targetShape, otherColors.random(random))
        val colorMatch = Figure(otherShapes.random(random), targetColor)

        // Distractors share neither dimension with the target, so they are never a plausible answer
        // under either rule. Drawn from the full cross-product rather than by walking an index, so
        // the board can grow past the size of either pool without running off the end of one.
        val distractors = otherShapes
            .flatMap { shape -> otherColors.map { color -> Figure(shape, color) } }
            .shuffled(random)
            .take(candidateCount() - 2)

        val cueShapes = otherShapes.shuffled(random)
        val cueColors = otherColors.shuffled(random)
        cueExemplar = when (rule) {
            Rule.SHAPE -> listOf(
                Figure(cueShapes[0], cueColors[0]),
                Figure(cueShapes[0], cueColors[1]),
            )
            Rule.COLOR -> listOf(
                Figure(cueShapes[0], cueColors[0]),
                Figure(cueShapes[1], cueColors[0]),
            )
        }

        val correct = if (rule == Rule.SHAPE) shapeMatch else colorMatch
        candidates = (listOf(shapeMatch, colorMatch) + distractors).shuffled(random)
        correctIndex = candidates.indexOf(correct)
    }

    /** Answers are 1-based tile positions, matching the other tap-a-figure boards. */
    override fun isCorrect(input: String): Boolean = input.toIntOrNull()?.minus(1) == correctIndex

    override fun solution(): String {
        val figure = candidates[correctIndex]
        return "${figure.color.displayName} ${figure.shape.displayName}"
    }

    override fun solutionMessage(): FeedbackMessage {
        val figure = candidates[correctIndex]
        return FeedbackMessage.FigureDescription(figure.color, figure.shape, null)
    }

    override fun hint(): String? = null

    override fun toUiState(): MentalFlexUiState {
        val columns = columnsPerRow()
        return MentalFlexUiState(
            rule = rule,
            cueExemplar = cueExemplar.toImmutableList(),
            target = target,
            rows = candidates
                .map { FigureCell(it) }
                .chunked(columns)
                .map { it.toImmutableList() }
                .toImmutableList(),
            columnsPerRow = columns,
        )
    }

    /**
     * How many tiles have to be scanned. Capped at six: past that the round turns into a long
     * visual search, which is what Anomaly Puzzle already is. The difficulty here should come from
     * holding the right rule, not from the size of the haystack.
     */
    private fun candidateCount(): Int = if (round < 3) 4 else 6

    /** Square-ish grids: 2x2, then 3x2. */
    private fun columnsPerRow(): Int = if (candidateCount() == 4) 2 else 3

    /**
     * How often the rule flips. It never reaches 1.0: a rule that alternated every round would be
     * predictable, and predictable switches cost nothing to prepare for.
     */
    private fun switchProbability(): Float = when {
        round < 3 -> 0.25f
        round < 7 -> 0.4f
        else -> 0.5f
    }

    private fun Rule.other(): Rule = if (this == Rule.SHAPE) Rule.COLOR else Rule.SHAPE
}
