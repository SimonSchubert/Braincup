package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.SoloChessUiState
import com.inspiredandroid.braincup.games.minichess.PieceType
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlin.random.Random

/**
 * Solo Chess: chess.com's single-player capture puzzle. The board holds several same-colored pieces;
 * every move must capture another piece, no piece may capture more than twice, and the board is
 * solved when a single piece remains. There is always a king, and because the king can never be
 * captured it is necessarily the last piece standing (chess.com's "king must be last" rule).
 *
 * Level-based stateful puzzle modeled on [CatQueensGame] / [KnotGame] / [ShikakuGame]: one puzzle per
 * attempt, no timer, score = highest level reached. The player taps a piece to select it, then taps a
 * piece it can capture ([tap]); a wrong line can dead-end, so the board can be reset with [restart].
 *
 * Generation is a retrograde construction that is solvable by construction: start from the lone king
 * and repeatedly "un-capture" (slide an existing piece back along a legal capture line and drop a new
 * piece on the square it vacated). The reverse of that sequence is a guaranteed solution, so no
 * uniqueness solver is needed; solve detection accepts ANY sequence that reduces the board to one
 * piece. Pawns are intentionally excluded (their directional capture and promotion add rules without
 * adding puzzle value).
 */
class SoloChessGame(
    level: Int = 1,
    private val random: Random = Random.Default,
) : Game() {
    override val adaptiveDifficulty: Boolean = false

    var level: Int = level.coerceAtLeast(1)
        private set

    /** Side length of the square board; cells are indexed row * size + col, row 0 at the top. */
    var size: Int = 4
        private set

    /** cell index -> piece type currently on that cell. Exactly one entry is the king. */
    internal val pieces: MutableMap<Int, PieceType> = mutableMapOf()

    /** cell index -> captures the piece on that cell may still make (starts at [MAX_CAPTURES]). */
    internal val capturesLeft: MutableMap<Int, Int> = mutableMapOf()

    /** The cell holding the king; the king can never be captured, so it is always the final piece. */
    internal var kingCell: Int = -1
        private set

    /** The currently selected piece's cell, or null. Held here so [toUiState] can expose its targets. */
    internal var selected: Int? = null
        private set

    /** Snapshot of the generated board, used to reset the level after a dead-end ([restart]). */
    private var initialPieces: Map<Int, PieceType> = emptyMap()
    private var initialKingCell: Int = -1

    /** The generator's own forward solution as ordered (from, to) capture moves; kept for tests. */
    internal var generatedSolution: List<Pair<Int, Int>> = emptyList()
        private set

    private data class Difficulty(val size: Int, val pieces: Int)

    // The board grows slowly and gets denser before the next size up, so each tier adds a real step in
    // difficulty. Gold is level 10 (a packed 6x6), matching the sibling level puzzles.
    private fun difficultyFor(level: Int): Difficulty = when (level) {
        1 -> Difficulty(size = 4, pieces = 3)
        2 -> Difficulty(size = 4, pieces = 4)
        3 -> Difficulty(size = 4, pieces = 5)
        4 -> Difficulty(size = 5, pieces = 5)
        5 -> Difficulty(size = 5, pieces = 6)
        6 -> Difficulty(size = 5, pieces = 7)
        7 -> Difficulty(size = 5, pieces = 8)
        8 -> Difficulty(size = 6, pieces = 8)
        9 -> Difficulty(size = 6, pieces = 9)
        10 -> Difficulty(size = 6, pieces = 10)
        11 -> Difficulty(size = 6, pieces = 11)
        12 -> Difficulty(size = 6, pieces = 12)
        13 -> Difficulty(size = 6, pieces = 13)
        else -> Difficulty(size = 6, pieces = 14)
    }

    override fun generateRound() {
        val difficulty = difficultyFor(level)
        size = difficulty.size
        selected = null

        // Retrograde construction always yields a solvable board, but a crowded board can occasionally
        // stall before reaching the target count. Keep the densest attempt as a safe fallback.
        var best = buildPuzzle(difficulty)
        var attempts = 1
        while (best.pieces.size < difficulty.pieces && attempts < MAX_GENERATION_ATTEMPTS) {
            val candidate = buildPuzzle(difficulty)
            if (candidate.pieces.size > best.pieces.size) best = candidate
            attempts++
        }

        pieces.clear()
        capturesLeft.clear()
        best.pieces.forEach { (cell, type) ->
            pieces[cell] = type
            // Every piece starts fresh: it may still capture twice, whatever the generator did with it.
            capturesLeft[cell] = MAX_CAPTURES
        }
        kingCell = best.kingCell
        generatedSolution = best.solution
        initialPieces = pieces.toMap()
        initialKingCell = kingCell
    }

    private data class Built(
        val pieces: Map<Int, PieceType>,
        val kingCell: Int,
        /** Forward solution as ordered (from, to) capture moves. */
        val solution: List<Pair<Int, Int>>,
    )

    /**
     * Builds one puzzle by retrograde construction: place the king on a random cell, then repeatedly
     * un-capture until [Difficulty.pieces] pieces are on the board. Each un-capture slides an existing
     * piece (with captures to spare) back along a legal line and drops a fresh piece on the square it
     * left, so the forward replay of those moves (the un-captures in reverse) is a valid solution.
     */
    private fun buildPuzzle(difficulty: Difficulty): Built {
        val n = difficulty.size
        val board = HashMap<Int, PieceType>()
        // captures the piece on a cell makes in the forward solution so far; capped at MAX_CAPTURES.
        val used = HashMap<Int, Int>()
        // Forward moves in construction order; the solution is these reversed (last un-capture first).
        val forwardMoves = ArrayList<Pair<Int, Int>>()

        var king = random.nextInt(n * n)
        board[king] = PieceType.KING
        used[king] = 0

        while (board.size < difficulty.pieces) {
            // A mover is any piece that still has a capture to spare (un-capturing adds one forward
            // capture to it). Try movers in random order until one offers a legal retrograde step.
            val movers = board.keys.filter { (used[it] ?: 0) < MAX_CAPTURES }.shuffled(random)
            var placed = false
            for (from in movers) {
                val type = board.getValue(from)
                val sources = reverseSources(from, type, board.keys, n).shuffled(random)
                val target = sources.firstOrNull() ?: continue
                // Slide the mover back to `target`; a brand-new piece appears where it stood (`from`).
                // The matching forward move is the mover capturing that new piece: target -> from.
                val moverUsed = (used[from] ?: 0) + 1
                board[target] = type
                used[target] = moverUsed
                board.remove(from)
                used.remove(from)
                if (type == PieceType.KING) king = target
                board[from] = randomCapturablePiece()
                used[from] = 0
                forwardMoves.add(target to from)
                placed = true
                break
            }
            if (!placed) break
        }
        return Built(board, king, forwardMoves.asReversed().toList())
    }

    private fun randomCapturablePiece(): PieceType =
        CAPTURABLE_TYPES[random.nextInt(CAPTURABLE_TYPES.size)]

    /**
     * Empty cells from which a piece of [type] could legally capture onto [from] (i.e. squares the
     * piece could have come from in the move that landed it on [from]). Sliding pieces stop at the
     * first occupied square in each direction; steppers use their fixed offsets. [occupied] is the set
     * of currently occupied cells (which always includes [from] itself).
     */
    private fun reverseSources(from: Int, type: PieceType, occupied: Set<Int>, n: Int): List<Int> {
        val fr = from / n
        val fc = from % n
        val out = ArrayList<Int>()
        when (type) {
            PieceType.KNIGHT -> for (o in KNIGHT_OFFSETS) addStep(fr + o[0], fc + o[1], n, occupied, out)
            PieceType.KING -> for (o in KING_OFFSETS) addStep(fr + o[0], fc + o[1], n, occupied, out)
            PieceType.ROOK -> addRays(fr, fc, n, occupied, ROOK_DIRECTIONS, out)
            PieceType.BISHOP -> addRays(fr, fc, n, occupied, BISHOP_DIRECTIONS, out)
            PieceType.QUEEN -> {
                addRays(fr, fc, n, occupied, ROOK_DIRECTIONS, out)
                addRays(fr, fc, n, occupied, BISHOP_DIRECTIONS, out)
            }
            PieceType.PAWN -> Unit // pawns are not used
        }
        return out
    }

    private fun addStep(r: Int, c: Int, n: Int, occupied: Set<Int>, out: MutableList<Int>) {
        if (r in 0 until n && c in 0 until n) {
            val cell = r * n + c
            if (cell !in occupied) out.add(cell)
        }
    }

    private fun addRays(
        fr: Int,
        fc: Int,
        n: Int,
        occupied: Set<Int>,
        directions: Array<IntArray>,
        out: MutableList<Int>,
    ) {
        for (d in directions) {
            var r = fr + d[0]
            var c = fc + d[1]
            while (r in 0 until n && c in 0 until n) {
                val cell = r * n + c
                if (cell in occupied) break // can't slide past or onto another piece
                out.add(cell)
                r += d[0]
                c += d[1]
            }
        }
    }

    /**
     * Cells the piece on [from] can capture: occupied squares (never the king) reachable by the
     * piece's movement, with sliders blocked by intervening pieces. Empty if [from] holds no piece or
     * has no captures left.
     */
    internal fun captureTargets(from: Int): Set<Int> {
        val type = pieces[from] ?: return emptySet()
        if ((capturesLeft[from] ?: 0) <= 0) return emptySet()
        val fr = from / size
        val fc = from % size
        val out = HashSet<Int>()
        when (type) {
            PieceType.KNIGHT -> for (o in KNIGHT_OFFSETS) addCapture(fr + o[0], fc + o[1], out)
            PieceType.KING -> for (o in KING_OFFSETS) addCapture(fr + o[0], fc + o[1], out)
            PieceType.ROOK -> captureRays(fr, fc, ROOK_DIRECTIONS, out)
            PieceType.BISHOP -> captureRays(fr, fc, BISHOP_DIRECTIONS, out)
            PieceType.QUEEN -> {
                captureRays(fr, fc, ROOK_DIRECTIONS, out)
                captureRays(fr, fc, BISHOP_DIRECTIONS, out)
            }
            PieceType.PAWN -> Unit
        }
        return out
    }

    private fun addCapture(r: Int, c: Int, out: MutableSet<Int>) {
        if (r in 0 until size && c in 0 until size) {
            val cell = r * size + c
            if (cell in pieces && cell != kingCell) out.add(cell)
        }
    }

    private fun captureRays(fr: Int, fc: Int, directions: Array<IntArray>, out: MutableSet<Int>) {
        for (d in directions) {
            var r = fr + d[0]
            var c = fc + d[1]
            while (r in 0 until size && c in 0 until size) {
                val cell = r * size + c
                if (cell in pieces) {
                    if (cell != kingCell) out.add(cell) // first piece hit is the only capture on this ray
                    break
                }
                r += d[0]
                c += d[1]
            }
        }
    }

    /**
     * Handles a tap on [index]: selects a piece, deselects it, performs a capture when the tapped cell
     * is a legal target of the selected piece, or switches the selection. Returns whether the board is
     * now solved.
     */
    fun tap(index: Int): Boolean {
        if (index !in 0 until size * size) return false
        val current = selected
        if (current == null) {
            if (index in pieces && (capturesLeft[index] ?: 0) > 0) selected = index
            return false
        }
        if (index == current) {
            selected = null
            return false
        }
        if (index in captureTargets(current)) {
            capture(current, index)
            selected = null
            return isSolved()
        }
        selected = if (index in pieces && (capturesLeft[index] ?: 0) > 0) index else null
        return false
    }

    private fun capture(from: Int, to: Int) {
        val type = pieces.getValue(from)
        val remaining = (capturesLeft[from] ?: 0) - 1
        pieces.remove(from)
        capturesLeft.remove(from)
        // The captured piece on `to` is removed; the mover takes its square with one fewer capture.
        pieces[to] = type
        capturesLeft[to] = remaining
        if (from == kingCell) kingCell = to
    }

    /** Resets the board to the generated start so a dead-end line can be retried. */
    fun restart() {
        pieces.clear()
        capturesLeft.clear()
        initialPieces.forEach { (cell, type) ->
            pieces[cell] = type
            capturesLeft[cell] = MAX_CAPTURES
        }
        kingCell = initialKingCell
        selected = null
    }

    /** Solved when one piece remains; since the king can never be captured, that piece is the king. */
    private fun isSolved(): Boolean = pieces.size == 1

    /** True when the board is unsolved yet no piece has any legal capture, so only a restart helps. */
    private fun isStuck(): Boolean =
        pieces.size > 1 && pieces.keys.none { captureTargets(it).isNotEmpty() }

    override fun isCorrect(input: String): Boolean = isSolved()

    override fun solution(): String = ""

    override fun hint(): String? = null

    override fun toUiState(): SoloChessUiState {
        val sel = selected
        return SoloChessUiState(
            size = size,
            pieces = pieces.toImmutableMap(),
            capturesLeft = capturesLeft.toImmutableMap(),
            kingCell = kingCell,
            selected = sel,
            targets = (if (sel != null) captureTargets(sel) else emptySet()).toImmutableSet(),
            level = level,
            stuck = isStuck(),
        )
    }

    companion object {
        /** No piece may capture more than twice (chess.com's rule). */
        const val MAX_CAPTURES = 2

        /** Upper bound on whole-board build retries while reaching the target piece count. */
        private const val MAX_GENERATION_ATTEMPTS = 200

        /** Types a generated (capturable) piece may be. The king is placed once, separately; pawns are
         *  excluded so every piece moves the same in all directions. */
        private val CAPTURABLE_TYPES = arrayOf(
            PieceType.KNIGHT,
            PieceType.BISHOP,
            PieceType.ROOK,
            PieceType.QUEEN,
        )

        private val KNIGHT_OFFSETS = arrayOf(
            intArrayOf(1, 2), intArrayOf(2, 1), intArrayOf(2, -1), intArrayOf(1, -2),
            intArrayOf(-1, -2), intArrayOf(-2, -1), intArrayOf(-2, 1), intArrayOf(-1, 2),
        )
        private val KING_OFFSETS = arrayOf(
            intArrayOf(1, 0), intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(0, -1),
            intArrayOf(1, 1), intArrayOf(-1, 1), intArrayOf(1, -1), intArrayOf(-1, -1),
        )
        private val ROOK_DIRECTIONS = arrayOf(
            intArrayOf(1, 0), intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(0, -1),
        )
        private val BISHOP_DIRECTIONS = arrayOf(
            intArrayOf(1, 1), intArrayOf(-1, 1), intArrayOf(1, -1), intArrayOf(-1, -1),
        )
    }
}
