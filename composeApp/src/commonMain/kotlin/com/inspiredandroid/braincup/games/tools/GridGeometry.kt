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
