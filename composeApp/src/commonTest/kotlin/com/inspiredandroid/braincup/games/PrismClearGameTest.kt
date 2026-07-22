package com.inspiredandroid.braincup.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PrismClearGameTest {

    @Test
    fun adaptiveDifficultyIsDisabled() {
        assertFalse(PrismClearGame().adaptiveDifficulty)
    }

    @Test
    fun levelIsClampedToCatalog() {
        assertEquals(1, PrismClearGame(level = 0).level)
        assertEquals(1, PrismClearGame(level = -2).level)
        assertEquals(PrismClearLevels.COUNT, PrismClearGame(level = 99).level)
        assertEquals(7, PrismClearGame(level = 7).level)
    }

    @Test
    fun catalogHasFifteenLevels() {
        assertEquals(15, PrismClearLevels.COUNT)
        assertEquals(15, PrismClearLevels.all.size)
    }

    @Test
    fun steepCurveOccupancyAndSolutionLength() {
        val l1 = PrismClearLevels.forLevel(1)
        val l5 = PrismClearLevels.forLevel(5)
        val l10 = PrismClearLevels.forLevel(10)
        val l15 = PrismClearLevels.forLevel(15)
        assertEquals(2, l1.solution.size)
        assertTrue(l1.occupiedCells() < l5.occupiedCells())
        assertTrue(l5.occupiedCells() < l10.occupiedCells())
        assertTrue(l10.occupiedCells() <= l15.occupiedCells())
        assertTrue(l10.solution.size >= 6)
        assertTrue(l15.solution.size >= 8)
    }

    @Test
    fun everyPredefinedLevelIsSolvable() {
        for (def in PrismClearLevels.all) {
            val cells = def.parseCells()
            assertTrue(
                hasAnyValidSwap(cells, def.rows, def.cols),
                "stuck on deal L${def.id}",
            )
            assertTrue(
                typeCountsAreMultiplesOfThree(cells),
                "type counts L${def.id}",
            )
            assertTrue(
                verifySolves(cells, def.rows, def.cols, def.solution),
                "solution fails L${def.id}",
            )

            val game = PrismClearGame(level = def.id).apply { nextRound() }
            assertEquals(def.rows, game.rows)
            assertEquals(def.cols, game.cols)
            assertEquals(def.solution, game.generatedSolution)
            for ((a, b) in game.generatedSolution) {
                if (game.boardIsEmpty()) break
                val result = game.trySwap(a, b)
                assertTrue(
                    result == PrismClearGame.PrismClearResult.Updated ||
                        result == PrismClearGame.PrismClearResult.Solved,
                    "illegal step L${def.id} result=$result",
                )
                if (result == PrismClearGame.PrismClearResult.Solved) break
            }
            assertTrue(game.boardIsEmpty(), "not empty after solution L${def.id}")
        }
    }

    @Test
    fun topazIsOrangeNotYellow() {
        assertEquals(
            com.inspiredandroid.braincup.games.tools.Color.ORANGE,
            PrismTileType.TOPAZ.color,
        )
    }

    @Test
    fun restartRestoresBoard() {
        val game = PrismClearGame(level = 2).apply { nextRound() }
        val initial = game.cells.copyOf()
        val (a, b) = game.generatedSolution.first()
        game.trySwap(a, b)
        assertTrue(game.canUndo)
        game.restart()
        assertTrue(initial.contentEquals(game.cells))
        assertEquals(0, game.movesUsed)
        assertFalse(game.canUndo)
        assertEquals(null, game.selectedIndex)
    }

    @Test
    fun undoRevertsLastSuccessfulSwap() {
        val game = PrismClearGame(level = 1).apply { nextRound() }
        val before = game.cells.copyOf()
        assertFalse(game.canUndo)
        assertFalse(game.undo())

        val (a, b) = game.generatedSolution.first()
        game.trySwap(a, b)
        assertEquals(1, game.movesUsed)
        assertTrue(game.canUndo)
        assertFalse(before.contentEquals(game.cells))

        assertTrue(game.undo())
        assertTrue(before.contentEquals(game.cells))
        assertEquals(0, game.movesUsed)
        assertFalse(game.canUndo)
    }

    @Test
    fun undoStacksMultipleMoves() {
        val game = PrismClearGame(level = 1).apply { nextRound() }
        val start = game.cells.copyOf()
        val (a1, b1) = game.generatedSolution[0]
        game.trySwap(a1, b1)
        val afterFirst = game.cells.copyOf()
        if (!game.boardIsEmpty()) {
            val (a2, b2) = game.generatedSolution[1]
            game.trySwap(a2, b2)
        }
        assertTrue(game.canUndo)
        game.undo()
        assertTrue(afterFirst.contentEquals(game.cells) || start.contentEquals(game.cells))
        if (game.canUndo) {
            game.undo()
            assertTrue(start.contentEquals(game.cells))
        }
    }

    @Test
    fun rejectedSwapDoesNotCreateUndoStep() {
        val game = PrismClearGame(level = 4).apply { nextRound() }
        assertFalse(game.canUndo)
        outer@ for (i in game.cells.indices) {
            if (game.cells[i] == null) continue
            for (n in orthogonalNeighbors(i, game.rows, game.cols)) {
                if (n <= i || game.cells[n] == null) continue
                if (!isLegalMatchingSwap(game.cells, game.rows, game.cols, i, n)) {
                    assertEquals(
                        PrismClearGame.PrismClearResult.Rejected,
                        game.trySwap(i, n),
                    )
                    assertFalse(game.canUndo)
                    assertEquals(0, game.movesUsed)
                    break@outer
                }
            }
        }
    }

    @Test
    fun clearEntireRunIncludingLengthFour() {
        val rows = 1
        val cols = 6
        val cells = arrayOfNulls<PrismTileType?>(rows * cols)
        cells[0] = PrismTileType.RUBY
        cells[1] = PrismTileType.RUBY
        cells[2] = PrismTileType.RUBY
        cells[3] = PrismTileType.RUBY
        assertEquals(setOf(0, 1, 2, 3), findMatches(cells, rows, cols))
    }

    @Test
    fun gravityPacksToBottom() {
        val cells = arrayOf(
            PrismTileType.RUBY,
            null,
            PrismTileType.EMERALD,
        )
        applyGravity(cells, 3, 1)
        assertEquals(null, cells[0])
        assertEquals(PrismTileType.RUBY, cells[1])
        assertEquals(PrismTileType.EMERALD, cells[2])
    }

    @Test
    fun uiStateForLevelMatchesCatalog() {
        val ui = PrismClearGame.uiStateForLevel(1)
        assertEquals(1, ui.level)
        assertEquals(3, ui.rows)
        assertEquals(6, ui.cols)
        assertFalse(ui.canUndo)
    }

    private fun PrismClearLevel.occupiedCells(): Int = parseCells().count { it != null }
}
