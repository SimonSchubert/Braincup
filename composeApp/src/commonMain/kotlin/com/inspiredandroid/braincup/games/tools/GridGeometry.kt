package com.inspiredandroid.braincup.games.tools

/** Orthogonally adjacent indices on a row-major [rows] x [cols] grid, clipped at the edges. */
fun orthogonalNeighbors(index: Int, rows: Int, cols: Int): List<Int> {
    val row = index / cols
    val col = index % cols
    val out = ArrayList<Int>(4)
    if (row > 0) out.add(index - cols)
    if (row < rows - 1) out.add(index + cols)
    if (col > 0) out.add(index - 1)
    if (col < cols - 1) out.add(index + 1)
    return out
}

/**
 * Orthogonal-neighbour lookup backed by a table that is rebuilt only when the board is resized.
 *
 * Puzzle solvers walk neighbours in their innermost loop, where the fresh boxed list
 * [orthogonalNeighbors] returns per call costs more than the search itself. Callers hold one of
 * these and pass the current board size on every lookup, so a game that resizes between rounds (or
 * has its size set directly in a test) can never read a stale table.
 */
class OrthogonalNeighborTable {
    private var rows = 0
    private var cols = 0
    private var table: Array<IntArray> = emptyArray()

    operator fun get(index: Int, rows: Int, cols: Int): IntArray {
        if (rows != this.rows || cols != this.cols) {
            table = build(rows, cols)
            this.rows = rows
            this.cols = cols
        }
        return table[index]
    }

    private fun build(rows: Int, cols: Int): Array<IntArray> = Array(rows * cols) { index ->
        val row = index / cols
        val col = index % cols
        var count = 0
        val out = IntArray(4)
        if (row > 0) out[count++] = index - cols
        if (row < rows - 1) out[count++] = index + cols
        if (col > 0) out[count++] = index - 1
        if (col < cols - 1) out[count++] = index + 1
        out.copyOf(count)
    }
}
