package com.inspiredandroid.braincup.reversi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun at(row: Int, col: Int) = row * REVERSI_SIZE + col

class ReversiBoardTest {
    @Test
    fun startingPositionHasFourCentreDiscsAndBlackToMove() {
        val b = ReversiBoard.startingPosition()
        assertEquals(Disc.BLACK, b.sideToMove)
        assertEquals(2, b.count(Disc.BLACK))
        assertEquals(2, b.count(Disc.WHITE))
        assertEquals(32, b.emptyCount())
        assertEquals(Disc.WHITE, b.discAt(at(2, 2)))
        assertEquals(Disc.BLACK, b.discAt(at(2, 3)))
        assertEquals(Disc.BLACK, b.discAt(at(3, 2)))
        assertEquals(Disc.WHITE, b.discAt(at(3, 3)))
    }

    @Test
    fun blackHasFourOpeningMoves() {
        val b = ReversiBoard.startingPosition()
        assertEquals(
            setOf(at(1, 2), at(2, 1), at(3, 4), at(4, 3)),
            b.legalMoves().toSet(),
        )
    }

    @Test
    fun aMoveThatFlanksNothingIsIllegal() {
        val b = ReversiBoard.startingPosition()
        // Diagonally off the centre block: it touches a white disc, but nothing closes the line.
        assertEquals(emptyList(), b.flipsFor(at(1, 1)))
        assertFalse(at(1, 1) in b.legalMoves())
        // An occupied square is never legal either.
        assertEquals(emptyList(), b.flipsFor(at(2, 2)))
    }

    @Test
    fun applyingAMoveFlipsTheFlankedDiscs() {
        val b = ReversiBoard.startingPosition()
        val after = b.apply(at(1, 2))
        assertEquals(Disc.BLACK, after.discAt(at(1, 2)))
        assertEquals(Disc.BLACK, after.discAt(at(2, 2)), "the flanked white disc should have flipped")
        assertEquals(4, after.count(Disc.BLACK))
        assertEquals(1, after.count(Disc.WHITE))
        assertEquals(Disc.WHITE, after.sideToMove)
    }

    @Test
    fun applyLeavesTheOriginalBoardUntouched() {
        val b = ReversiBoard.startingPosition()
        b.apply(at(1, 2))
        assertEquals(2, b.count(Disc.BLACK))
        assertEquals(Disc.WHITE, b.discAt(at(2, 2)))
        assertEquals(Disc.BLACK, b.sideToMove)
    }

    @Test
    fun flipsRunInAllEightDirections() {
        // Black rings a white square that rings the empty centre, so playing the centre closes a
        // line in every one of the eight directions at once.
        val b = ReversiBoard.fromRows(
            listOf(
                "BBBBB.",
                "BWWWB.",
                "BW.WB.",
                "BWWWB.",
                "BBBBB.",
                "......",
            ),
            sideToMove = Disc.BLACK,
        )
        assertEquals(8, b.flipsFor(at(2, 2)).size)
        assertEquals(0, b.apply(at(2, 2)).count(Disc.WHITE))
    }

    @Test
    fun flipsStopAtAnEmptySquare() {
        val b = ReversiBoard.fromRows(
            listOf(
                ".WW.B.",
                "......",
                "......",
                "......",
                "......",
                "......",
            ),
            sideToMove = Disc.BLACK,
        )
        // Walking right from (0,0) crosses two whites and then reaches an empty square, not a
        // black one, so the line never closes.
        assertEquals(emptyList(), b.flipsFor(at(0, 0)))
    }

    @Test
    fun flipsDoNotWrapAroundTheEdge() {
        // (0,4) empty, (0,5) white, (1,0) black. Those are consecutive flat indices, so walking
        // the flat index by a single delta would read them as a closed line across the edge.
        val b = ReversiBoard.fromRows(
            listOf(
                ".....W",
                "B.....",
                "......",
                "......",
                "......",
                "......",
            ),
            sideToMove = Disc.BLACK,
        )
        assertEquals(emptyList(), b.flipsFor(at(0, 4)))
        assertTrue(b.legalMoves().isEmpty(), "no move should exist, got ${b.legalMoves()}")
    }

    @Test
    fun aStuckSidePassesWhileTheGameCarriesOn() {
        // White's only disc is in the corner of a black block: nothing it can place closes a line
        // back onto it, while black can still take it from below.
        val b = ReversiBoard.fromRows(
            listOf(
                "BBBBBB",
                "BBBBBB",
                "BBBBBW",
                "......",
                "......",
                "......",
            ),
            sideToMove = Disc.WHITE,
        )
        assertTrue(b.legalMoves().isEmpty(), "white should be stuck, got ${b.legalMoves()}")
        assertFalse(b.isGameOver(), "black can still move, so the game continues")
        val passed = b.passTurn()
        assertEquals(Disc.BLACK, passed.sideToMove)
        assertEquals(listOf(at(3, 5)), passed.legalMoves())
    }

    @Test
    fun theGameEndsWhenNeitherSideCanMoveEvenWithEmptiesLeft() {
        val b = ReversiBoard.fromRows(
            listOf(
                "BB....",
                "......",
                "......",
                "......",
                "......",
                "......",
            ),
            sideToMove = Disc.WHITE,
        )
        assertTrue(b.isGameOver())
        assertTrue(b.emptyCount() > 0)
        assertEquals(ReversiResult.BLACK_WINS, b.result())
    }

    @Test
    fun resultReportsTheDiscMajorityAndDrawsOnAnEqualCount() {
        val blackAhead = ReversiBoard.fromRows(
            listOf("BBBBBB", "BBBBBB", "BBBBBB", "WWWWWW", "WWWWWW", "WWWWWB"),
            sideToMove = Disc.BLACK,
        )
        assertEquals(ReversiResult.BLACK_WINS, blackAhead.result())

        val whiteAhead = ReversiBoard.fromRows(
            listOf("WWWWWW", "WWWWWW", "WWWWWW", "BBBBBB", "BBBBBB", "BBBBBW"),
            sideToMove = Disc.BLACK,
        )
        assertEquals(ReversiResult.WHITE_WINS, whiteAhead.result())

        val level = ReversiBoard.fromRows(
            listOf("BBBBBB", "BBBBBB", "BBBBBB", "WWWWWW", "WWWWWW", "WWWWWW"),
            sideToMove = Disc.BLACK,
        )
        assertEquals(ReversiResult.DRAW, level.result())
    }

    @Test
    fun anOngoingGameHasNoResult() {
        assertEquals(ReversiResult.ONGOING, ReversiBoard.startingPosition().result())
    }
}
