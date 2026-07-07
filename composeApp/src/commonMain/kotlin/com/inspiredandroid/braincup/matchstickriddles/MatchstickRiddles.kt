package com.inspiredandroid.braincup.matchstickriddles

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.matchstick_riddle_prompt_move_one
import braincup.composeapp.generated.resources.matchstick_riddle_prompt_move_three
import braincup.composeapp.generated.resources.matchstick_riddle_prompt_move_two
import org.jetbrains.compose.resources.StringResource
import kotlin.math.sqrt

/**
 * A single matchstick position in normalized board coordinates (origin top-left), running from
 * endpoint A to endpoint B. A matchstick is one unit long, so digit segments and operator bars
 * all share the same length.
 */
data class Stick(val ax: Float, val ay: Float, val bx: Float, val by: Float) {
    val midX: Float get() = (ax + bx) / 2f
    val midY: Float get() = (ay + by) / 2f
}

/**
 * A curated matchstick puzzle. [slots] holds every possible stick position so that indices stay
 * stable, [initial] is the set of occupied slots shown at the start, and [solutions] is the
 * intended answer (kept for reference and tests). A board is checked with [isSolved], which decodes
 * the occupied sticks back into an equation and accepts ANY arrangement that reads as a true
 * equation, not just the intended one.
 */
data class MatchstickRiddle(
    val id: String,
    val promptRes: StringResource,
    val slots: List<Stick>,
    val initial: Set<Int>,
    val solutions: List<Set<Int>>,
    /** How many matchsticks the player may relocate from their starting positions. */
    val moves: Int,
    /** Slots that can never be moved (the equals-sign bars). */
    val fixedSlots: Set<Int>,
    internal val cells: List<CellLayout>,
) {
    val boardWidth: Float get() = slots.maxOf { maxOf(it.ax, it.bx) }
    val boardHeight: Float get() = slots.maxOf { maxOf(it.ay, it.by) }

    /**
     * The occupied sticks that are locked. Once the player has relocated [moves] sticks, the ones
     * still resting in their starting slots can no longer be picked up; only the moved sticks stay
     * adjustable. Returns empty while the player still has moves to spend.
     */
    fun lockedSticks(occupied: Set<Int>): Set<Int> {
        val displaced = occupied.count { it !in initial }
        if (displaced < moves) return emptySet()
        // Equals bars are handled by [fixedSlots] and stay visually normal, so exclude them here.
        return occupied.filterTo(mutableSetOf()) { it in initial && it !in fixedSlots }
    }

    /** True once the occupied sticks form a well-formed, arithmetically true equation. */
    fun isSolved(occupied: Set<Int>): Boolean {
        if (occupied == initial) return false
        val equation = decodeEquation(occupied) ?: return false
        return isTrueEquation(equation)
    }

    /** Reads the occupied sticks back into an equation string, or null if any cell is malformed. */
    private fun decodeEquation(occupied: Set<Int>): String? {
        val builder = StringBuilder()
        for (cell in cells) {
            builder.append(cell.decode(occupied) ?: return null)
        }
        return builder.toString()
    }
}

object MatchstickRiddles {
    /**
     * Curated equation riddles, ordered so each introduces a distinct trick and the challenge ramps
     * up: nudging one digit, then moving a stick across the equation, then flipping the operator,
     * then two-stick moves (including longer three-addend sums), and finally three-stick moves. The
     * matchstick count is always conserved because the only allowed action is moving a stick.
     */
    val all: List<MatchstickRiddle> = listOf(
        // Tier 1: relocate a single stick within one digit (result, then each operand).
        riddle("one_plus_one", initial = "1+1=3", solution = "1+1=2"),
        riddle("nine_minus_four", initial = "9-4=2", solution = "6-4=2"),
        riddle("nine_minus_three", initial = "9-3=0", solution = "9-3=6"),
        riddle("six_plus_three", initial = "6+3=6", solution = "6+3=9"),
        // Tier 2: carry a stick from one digit across to another.
        riddle("eight_plus_three", initial = "8+3=3", solution = "6+3=9"),
        // Tier 3: flip the operator between + and -.
        riddle("nine_minus_two", initial = "9-2=5", solution = "3+2=5"),
        riddle("nine_plus_zero", initial = "9+0=1", solution = "9-8=1"),
        riddle("eight_plus_one", initial = "8+1=1", solution = "8-7=1"),
        riddle("nine_minus_four_nine", initial = "9-4=9", solution = "5+4=9"),
        riddle("nine_plus_three", initial = "9+3=0", solution = "9-9=0"),
        // Tier 4: two-stick moves.
        riddle("two_plus_two_six", initial = "2+2=6", solution = "3+3=6"),
        riddle("two_plus_three_eight", initial = "2+3=8", solution = "3+5=8"),
        // Tier 5: two-stick moves on longer sums (three addends).
        riddle("three_addends_swap", initial = "1+1+2=7", solution = "1+1+5=7"),
        riddle("three_addends_carry", initial = "1+1+8=7", solution = "1+4+2=7"),
        riddle("three_addends_flip", initial = "1+1+3=6", solution = "1+7-2=6"),
        riddle("three_addends_big", initial = "1+1+1=18", solution = "1+7+7=15"),
        // Tier 6: three-stick moves.
        riddle("two_minus_two", initial = "2-2=2", solution = "2+2=4"),
        riddle("flip_both_signs", initial = "1+1+1=4", solution = "7-1-1=5"),
        riddle("triple_seven", initial = "1+1+1=23", solution = "7+7+7=21"),
    )

    fun byId(id: String): MatchstickRiddle? = all.firstOrNull { it.id == id }

    /**
     * Fixed ceiling for Play Games / Game Center progress (e.g. 19/50). Must match the incremental
     * step total set once in Play Console / App Store Connect; it cannot be changed later, so leave
     * headroom for future riddles. In-app completion uses [count] instead; the store achievement
     * never auto-unlocks.
     */
    const val storeProgressMax: Int = 50

    /** Live catalog size — the in-app "solve them all" target and menu progress denominator. */
    val count: Int get() = all.size
}

/** The seven segments of a digit cell, in a fixed order so slot indices are deterministic. */
private enum class Seg { TOP, TOP_LEFT, TOP_RIGHT, MID, BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM }

/**
 * The one canonical seven-segment shape for each digit glyph. Boards are built from these shapes and
 * decoded against them, so only an exact, well-formed digit counts: a player must land on the full
 * glyph, not an "open-loop" near-miss (e.g. a `4` plus a top bar does NOT read as a `9`).
 */
private val DIGIT_FORMS: Map<Char, Set<Seg>> = mapOf(
    '0' to setOf(Seg.TOP, Seg.TOP_LEFT, Seg.TOP_RIGHT, Seg.BOTTOM_LEFT, Seg.BOTTOM_RIGHT, Seg.BOTTOM),
    '1' to setOf(Seg.TOP_RIGHT, Seg.BOTTOM_RIGHT),
    '2' to setOf(Seg.TOP, Seg.TOP_RIGHT, Seg.MID, Seg.BOTTOM_LEFT, Seg.BOTTOM),
    '3' to setOf(Seg.TOP, Seg.TOP_RIGHT, Seg.MID, Seg.BOTTOM_RIGHT, Seg.BOTTOM),
    '4' to setOf(Seg.TOP_LEFT, Seg.TOP_RIGHT, Seg.MID, Seg.BOTTOM_RIGHT),
    '5' to setOf(Seg.TOP, Seg.TOP_LEFT, Seg.MID, Seg.BOTTOM_RIGHT, Seg.BOTTOM),
    '6' to setOf(Seg.TOP, Seg.TOP_LEFT, Seg.MID, Seg.BOTTOM_LEFT, Seg.BOTTOM_RIGHT, Seg.BOTTOM),
    '7' to setOf(Seg.TOP, Seg.TOP_RIGHT, Seg.BOTTOM_RIGHT),
    '8' to setOf(Seg.TOP, Seg.TOP_LEFT, Seg.TOP_RIGHT, Seg.MID, Seg.BOTTOM_LEFT, Seg.BOTTOM_RIGHT, Seg.BOTTOM),
    '9' to setOf(Seg.TOP, Seg.TOP_LEFT, Seg.TOP_RIGHT, Seg.MID, Seg.BOTTOM_RIGHT, Seg.BOTTOM),
)

/** Decodes the lit slots of one cell back to its character, or null when the cell is malformed. */
sealed interface CellLayout {
    fun decode(occupied: Set<Int>): Char?
}

private class DigitLayout(private val segToSlot: Map<Seg, Int>) : CellLayout {
    override fun decode(occupied: Set<Int>): Char? {
        val lit: Set<Seg> = segToSlot.filterValues { it in occupied }.keys
        return DIGIT_FORMS.entries.firstOrNull { (_, form) -> form == lit }?.key
    }
}

private class TermOpLayout(private val hmid: Int, private val vmid: Int) : CellLayout {
    override fun decode(occupied: Set<Int>): Char? = when {
        hmid in occupied && vmid in occupied -> '+'
        hmid in occupied && vmid !in occupied -> '-'
        else -> null
    }
}

private class EqualsLayout(private val top: Int, private val bottom: Int) : CellLayout {
    override fun decode(occupied: Set<Int>): Char? = if (top in occupied && bottom in occupied) '=' else null
}

/** A cell in an authored equation, used to lay out the board. */
private sealed interface Cell
private data class DigitCell(val glyph: Char) : Cell
private data class TermOpCell(val glyph: Char) : Cell // '+' or '-'
private object EqualsCell : Cell

private fun parseEquation(text: String): List<Cell> = text.map { ch ->
    when (ch) {
        '+', '-' -> TermOpCell(ch)
        '=' -> EqualsCell
        in '0'..'9' -> DigitCell(ch)
        else -> error("Unsupported matchstick equation character '$ch' in \"$text\"")
    }
}

private fun sameKind(a: Cell, b: Cell): Boolean = when (a) {
    is DigitCell -> b is DigitCell
    is TermOpCell -> b is TermOpCell
    EqualsCell -> b is EqualsCell
}

/**
 * Evaluates an additive equation such as "9-8=1" or "1+2+3=6": the left side is a chain of numbers
 * joined by + and - (folded left to right, no operator precedence), and the right side is a single
 * number. Only this grammar is supported; anything else returns false.
 */
private fun isTrueEquation(equation: String): Boolean {
    val eq = equation.indexOf('=')
    if (eq < 0) return false
    val left = evaluateAdditiveChain(equation.substring(0, eq)) ?: return false
    val right = equation.substring(eq + 1).toIntOrNull() ?: return false
    return left == right
}

/**
 * Folds a chain like "1+2-3" from left to right into its value, or null when the chain is malformed
 * (empty, a leading or trailing operator, or two operators in a row).
 */
private fun evaluateAdditiveChain(chain: String): Int? {
    if (chain.isEmpty()) return null
    var total = 0
    var sign = 1
    val number = StringBuilder()
    for (ch in chain) {
        when (ch) {
            in '0'..'9' -> number.append(ch)
            '+', '-' -> {
                if (number.isEmpty()) return null
                total += sign * number.toString().toInt()
                number.clear()
                sign = if (ch == '+') 1 else -1
            }
            else -> return null
        }
    }
    if (number.isEmpty()) return null
    return total + sign * number.toString().toInt()
}

/**
 * Builds a riddle from an [initial] (false) equation and the true [solution] it becomes. Both must
 * share the same cell layout so slot indices line up, and the difference must be a genuine move
 * (the matchstick count is conserved). The equations are checked at construction so a mis-authored
 * riddle fails fast.
 */
private fun riddle(id: String, initial: String, solution: String): MatchstickRiddle {
    val initialCells = parseEquation(initial)
    val solutionCells = parseEquation(solution)
    require(initialCells.size == solutionCells.size) {
        "$id: \"$initial\" and \"$solution\" have different layouts"
    }
    require(initialCells.indices.all { sameKind(initialCells[it], solutionCells[it]) }) {
        "$id: cell kinds must match between initial and solution"
    }
    require(!isTrueEquation(initial)) { "$id: initial \"$initial\" is already true" }
    require(isTrueEquation(solution)) { "$id: solution \"$solution\" is not a true equation" }

    val board = buildBoard(initialCells)
    val solutionOccupied = buildBoard(solutionCells).occupied
    require(board.occupied.size == solutionOccupied.size) {
        "$id: a move must conserve the matchstick count"
    }
    require((board.occupied - solutionOccupied).none { it in board.fixedSlots }) {
        "$id: a solution must not move the equals sign"
    }
    val moves = (board.occupied - solutionOccupied).size
    val promptRes = when (moves) {
        1 -> Res.string.matchstick_riddle_prompt_move_one
        2 -> Res.string.matchstick_riddle_prompt_move_two
        3 -> Res.string.matchstick_riddle_prompt_move_three
        else -> error("$id: unsupported move count $moves")
    }
    return MatchstickRiddle(
        id = id,
        promptRes = promptRes,
        slots = board.slots,
        initial = board.occupied,
        solutions = listOf(solutionOccupied),
        moves = moves,
        fixedSlots = board.fixedSlots,
        cells = board.cells,
    )
}

private class Board(
    val slots: List<Stick>,
    val occupied: Set<Int>,
    val cells: List<CellLayout>,
    val fixedSlots: Set<Int>,
)

/** Lays out cells left to right and collects the slots, occupied indices, cells, and fixed slots. */
private fun buildBoard(cells: List<Cell>): Board {
    val builder = BoardBuilder()
    val advance = 1f + 0.5f // cell width plus gap
    var x = 0f
    val layouts = cells.map { cell ->
        val layout = when (cell) {
            is DigitCell -> builder.digit(x, cell.glyph)
            is TermOpCell -> builder.termOp(x, cell.glyph)
            EqualsCell -> builder.equalsOp(x)
        }
        x += advance
        layout
    }
    return Board(builder.sticks.toList(), builder.occupied.toSet(), layouts, builder.fixedSlots.toSet())
}

/** Builds the occupied slots for an arbitrary equation on a fresh board (used by tests). */
internal fun occupiedForEquation(equation: String): Set<Int> = buildBoard(parseEquation(equation)).occupied

/**
 * Accumulates cell slots into a single flat list (so indices stay stable across the whole board)
 * and records which start occupied. Each cell always contributes its full slot superset, even the
 * unlit ones, so they remain valid snap destinations and keep indices aligned when an operator or
 * digit changes between the initial board and a solution.
 */
private class BoardBuilder {
    val sticks = mutableListOf<Stick>()
    val occupied = mutableSetOf<Int>()

    /** Slots that can never be moved (the equals-sign bars). */
    val fixedSlots = mutableSetOf<Int>()

    private fun add(stick: Stick, lit: Boolean): Int {
        val index = sticks.size
        if (lit) occupied.add(index)
        sticks.add(stick)
        return index
    }

    fun digit(ox: Float, glyph: Char): DigitLayout {
        val u = 1f
        val segments = listOf(
            Seg.TOP to Stick(ox, 0f, ox + u, 0f),
            Seg.TOP_LEFT to Stick(ox, 0f, ox, u),
            Seg.TOP_RIGHT to Stick(ox + u, 0f, ox + u, u),
            Seg.MID to Stick(ox, u, ox + u, u),
            Seg.BOTTOM_LEFT to Stick(ox, u, ox, 2 * u),
            Seg.BOTTOM_RIGHT to Stick(ox + u, u, ox + u, 2 * u),
            Seg.BOTTOM to Stick(ox, 2 * u, ox + u, 2 * u),
        )
        val lit = DIGIT_FORMS.getValue(glyph)
        val segToSlot = LinkedHashMap<Seg, Int>()
        for ((seg, stick) in segments) segToSlot[seg] = add(stick, seg in lit)
        return DigitLayout(segToSlot)
    }

    /** A '+' or '-' operator: a horizontal bar (always present) and a vertical bar (plus only). */
    fun termOp(ox: Float, glyph: Char): TermOpLayout {
        val u = 1f
        val hmid = add(Stick(ox, u, ox + u, u), lit = true)
        val vmid = add(Stick(ox + u / 2f, u / 2f, ox + u / 2f, 1.5f * u), lit = glyph == '+')
        return TermOpLayout(hmid, vmid)
    }

    /** An equals operator: two stacked horizontal bars. These bars are fixed and never move. */
    fun equalsOp(ox: Float): EqualsLayout {
        val u = 1f
        val top = add(Stick(ox, 0.7f, ox + u, 0.7f), lit = true)
        val bottom = add(Stick(ox, 1.3f, ox + u, 1.3f), lit = true)
        fixedSlots += top
        fixedSlots += bottom
        return EqualsLayout(top, bottom)
    }
}

/** Shortest distance from point ([px], [py]) to the line segment [stick], in board coordinates. */
fun pointToSegmentDistance(px: Float, py: Float, stick: Stick): Float {
    val dx = stick.bx - stick.ax
    val dy = stick.by - stick.ay
    val lengthSquared = dx * dx + dy * dy
    if (lengthSquared == 0f) {
        return sqrt((px - stick.ax) * (px - stick.ax) + (py - stick.ay) * (py - stick.ay))
    }
    val t = (((px - stick.ax) * dx + (py - stick.ay) * dy) / lengthSquared).coerceIn(0f, 1f)
    val projX = stick.ax + t * dx
    val projY = stick.ay + t * dy
    return sqrt((px - projX) * (px - projX) + (py - projY) * (py - projY))
}
