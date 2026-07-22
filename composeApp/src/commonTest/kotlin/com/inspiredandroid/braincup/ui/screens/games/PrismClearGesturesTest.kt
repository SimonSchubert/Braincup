package com.inspiredandroid.braincup.ui.screens.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PrismClearGesturesTest {

    private val step = 50f
    private val rows = 3
    private val cols = 4

    @Test
    fun cellIndexAtMapsCentersAndRejectsOutside() {
        // Top-left cell (0,0)
        assertEquals(0, prismClearCellIndexAt(1f, 1f, step, rows, cols))
        assertEquals(0, prismClearCellIndexAt(49f, 49f, step, rows, cols))
        // Col 1
        assertEquals(1, prismClearCellIndexAt(50f, 10f, step, rows, cols))
        // Row 1, col 2 → index 1*4+2 = 6
        assertEquals(6, prismClearCellIndexAt(100f, 50f, step, rows, cols))
        // Outside
        assertNull(prismClearCellIndexAt(-1f, 10f, step, rows, cols))
        assertNull(prismClearCellIndexAt(10f, -1f, step, rows, cols))
        assertNull(prismClearCellIndexAt(step * cols, 10f, step, rows, cols))
        assertNull(prismClearCellIndexAt(10f, step * rows, step, rows, cols))
        assertNull(prismClearCellIndexAt(10f, 10f, 0f, rows, cols))
    }

    @Test
    fun neighbourInDragDirectionRequiresThreshold() {
        val from = 5 // row 1, col 1 on 4-col grid
        assertNull(prismClearNeighbourInDragDirection(from, 10f, 0f, thresholdPx = 20f, rows, cols))
        assertNull(prismClearNeighbourInDragDirection(from, 0f, 10f, thresholdPx = 20f, rows, cols))
    }

    @Test
    fun neighbourInDragDirectionPicksOrthogonalAndDominantAxis() {
        val from = 5 // (1,1)
        // Right
        assertEquals(6, prismClearNeighbourInDragDirection(from, 25f, 5f, 20f, rows, cols))
        // Left
        assertEquals(4, prismClearNeighbourInDragDirection(from, -25f, 5f, 20f, rows, cols))
        // Down
        assertEquals(9, prismClearNeighbourInDragDirection(from, 5f, 25f, 20f, rows, cols))
        // Up
        assertEquals(1, prismClearNeighbourInDragDirection(from, 5f, -25f, 20f, rows, cols))
        // Diagonal: horizontal wins on equal magnitude
        assertEquals(6, prismClearNeighbourInDragDirection(from, 25f, 25f, 20f, rows, cols))
        assertEquals(4, prismClearNeighbourInDragDirection(from, -25f, 25f, 20f, rows, cols))
    }

    @Test
    fun neighbourInDragDirectionRejectsBoardEdge() {
        // Top-left: left and up leave the board
        assertNull(prismClearNeighbourInDragDirection(0, -30f, 0f, 20f, rows, cols))
        assertNull(prismClearNeighbourInDragDirection(0, 0f, -30f, 20f, rows, cols))
        // Bottom-right: right and down leave the board
        val br = rows * cols - 1
        assertNull(prismClearNeighbourInDragDirection(br, 30f, 0f, 20f, rows, cols))
        assertNull(prismClearNeighbourInDragDirection(br, 0f, 30f, 20f, rows, cols))
    }
}
