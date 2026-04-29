package com.inspiredandroid.braincup.games.minichess

import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ScenarioGeneratorTest {
    @Test
    fun generatesValidScenariosAcrossDifficulties() {
        val random = Random(20260429)
        // depth 1 = Easy, 3 = Medium, 5 = Hard (matches InstructionsScreen options).
        for (depth in listOf(1, 3, 5)) {
            repeat(200) { iteration ->
                val board = ScenarioGenerator.generate(depth, random)
                val msg = "depth=$depth iteration=$iteration"

                val whiteKing = board.findKing(Color.WHITE)
                val blackKing = board.findKing(Color.BLACK)
                assertNotNull(whiteKing, "$msg: white king missing")
                assertNotNull(blackKing, "$msg: black king missing")

                val dist = chebyshev(whiteKing, blackKing)
                assertTrue(dist >= 2, "$msg: kings adjacent at $whiteKing / $blackKing")

                assertFalse(board.isInCheck(Color.WHITE), "$msg: white in check")
                assertFalse(board.isInCheck(Color.BLACK), "$msg: black in check")
                assertFalse(board.isCheckmate(), "$msg: already checkmate")
                assertFalse(board.isStalemate(), "$msg: already stalemate")
                assertTrue(board.legalMoves().size >= 3, "$msg: too few legal moves")

                val white = board.pieceCount(Color.WHITE)
                val black = board.pieceCount(Color.BLACK)
                // White always K + 3..4 non-king = 4..5 total.
                assertTrue(white in 4..5, "$msg: unexpected white piece count $white")
                // Black: Easy (depth 1) = K + 2..3 non-king = 3..4; harder = K + 3..4 = 4..5.
                val expectedBlack = if (depth <= 1) 3..4 else 4..5
                assertTrue(black in expectedBlack, "$msg: unexpected black piece count $black")

                // Pawns never on home/promotion ranks.
                val snapshot = board.snapshot()
                for (i in snapshot.indices) {
                    val p = snapshot[i] ?: continue
                    if (p.type == PieceType.PAWN) {
                        val row = i / BOARD_SIZE
                        assertTrue(row in 1 until BOARD_SIZE - 1, "$msg: pawn on row $row")
                    }
                }
            }
        }
    }

    private fun chebyshev(a: Square, b: Square): Int = max(abs(a.file - b.file), abs(a.row - b.row))
}
