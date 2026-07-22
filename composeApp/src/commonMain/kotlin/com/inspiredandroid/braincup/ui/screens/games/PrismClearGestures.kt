package com.inspiredandroid.braincup.ui.screens.games

import kotlin.math.abs

/**
 * Maps a board-local pointer position to a cell index, or null if outside the grid.
 * Cells are laid out on a uniform [stepPx] grid (cell size + gap).
 */
internal fun prismClearCellIndexAt(
    x: Float,
    y: Float,
    stepPx: Float,
    rows: Int,
    cols: Int,
): Int? {
    if (stepPx <= 0f || rows <= 0 || cols <= 0) return null
    if (x < 0f || y < 0f) return null
    val col = (x / stepPx).toInt()
    val row = (y / stepPx).toInt()
    if (row !in 0 until rows || col !in 0 until cols) return null
    return row * cols + col
}

/**
 * After the pointer has moved by ([dx], [dy]) from the press cell [from], returns the
 * orthogonally adjacent neighbour in the dominant drag direction once either axis exceeds
 * [thresholdPx]. Returns null below the threshold or if that neighbour would leave the board.
 */
internal fun prismClearNeighbourInDragDirection(
    from: Int,
    dx: Float,
    dy: Float,
    thresholdPx: Float,
    rows: Int,
    cols: Int,
): Int? {
    if (from < 0 || rows <= 0 || cols <= 0) return null
    if (from >= rows * cols) return null
    if (thresholdPx <= 0f) return null
    val absDx = abs(dx)
    val absDy = abs(dy)
    if (absDx < thresholdPx && absDy < thresholdPx) return null

    val row = from / cols
    val col = from % cols
    return if (absDx >= absDy) {
        // Horizontal preference on ties so a mostly-horizontal flick still resolves.
        val targetCol = if (dx > 0f) col + 1 else col - 1
        if (targetCol !in 0 until cols) null else row * cols + targetCol
    } else {
        val targetRow = if (dy > 0f) row + 1 else row - 1
        if (targetRow !in 0 until rows) null else targetRow * cols + col
    }
}
