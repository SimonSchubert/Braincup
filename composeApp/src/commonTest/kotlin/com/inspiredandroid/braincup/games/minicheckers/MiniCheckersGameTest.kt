package com.inspiredandroid.braincup.games.minicheckers

import com.inspiredandroid.braincup.app.CpuRoundOutcome
import com.inspiredandroid.braincup.checkers.CheckersAi
import com.inspiredandroid.braincup.checkers.CheckersBoard
import com.inspiredandroid.braincup.checkers.CheckersResult
import com.inspiredandroid.braincup.checkers.CheckersSide
import com.inspiredandroid.braincup.games.MiniCheckersGame
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class MiniCheckersGameTest {
    private fun newGame(difficulty: MiniCheckersDifficulty = MiniCheckersDifficulty.NORMAL) = MiniCheckersGame(difficulty, Random(7)).apply { nextRound() }

    @Test
    fun findingTheWinEndsTheRoundWithWinPoints() {
        val game = newGame()
        val player = CheckersAi(depth = 9, random = Random(1))
        val cpu = CheckersAi(MiniCheckersDifficulty.NORMAL.cpu, random = Random(2))
        while (game.phase != MiniCheckersGame.Phase.ROUND_OVER) {
            val move = game.parseMove(MiniCheckersGame.encodeMove(player.bestMove(game.board)!!.path))!!
            game.applyPlayerMove(move)
            if (game.phase == MiniCheckersGame.Phase.AI_THINKING) game.applyAiMove(cpu.bestMove(game.board)!!)
        }
        assertEquals(CpuRoundOutcome.PLAYER_WIN, game.outcome)
        assertEquals(10, game.winPoints())
    }

    @Test
    fun resetRestoresTheSameScenario() {
        val game = newGame()
        val start = game.board
        game.applyPlayerMove(game.board.legalMoves().first())
        game.resetScenario()
        assertSame(start, game.board)
        assertEquals(MiniCheckersGame.Phase.PLAYER_TURN, game.phase)
        assertNull(game.lastMove)
        assertNull(game.outcome)
    }

    @Test
    fun givingUpIsALoss() {
        val game = newGame()
        game.markGiveUp()
        assertEquals(CpuRoundOutcome.PLAYER_LOSS, game.outcome)
        assertEquals(MiniCheckersGame.Phase.ROUND_OVER, game.phase)
    }

    @Test
    fun aMultiJumpIsParsedFromItsFullPath() {
        val board = CheckersBoard.fromRows(
            listOf(
                "......",
                "......",
                "...w..",
                "......",
                ".w....",
                "b.....",
            ),
            CheckersSide.BLACK,
        )
        val jump = board.legalMoves().single()
        assertEquals(listOf(30, 20, 10), jump.path)
        assertEquals(CheckersResult.BLACK_WINS, board.apply(jump).result())
    }

    @Test
    fun malformedOrIllegalInputIsRejected() {
        val game = newGame()
        assertNull(game.parseMove("not a move"))
        assertNull(game.parseMove("0>1"))
    }

    @Test
    fun menCrownOnTheFarRowOfASmallBoard() {
        val board = CheckersBoard.fromRows(
            listOf(
                "......",
                "b.....",
                "......",
                "......",
                "...w..",
                "......",
            ),
            CheckersSide.BLACK,
        )
        val blackMove = board.legalMoves().single()
        val afterBlack = board.apply(blackMove)
        assertEquals(true, afterBlack.pieceAt(blackMove.to)?.isKing)
        val whiteMove = afterBlack.legalMoves().first()
        assertEquals(true, afterBlack.apply(whiteMove).pieceAt(whiteMove.to)?.isKing)
    }

    @Test
    fun theSmallBoardDrawsAfterItsOwnQuietLimit() {
        val board = CheckersBoard.fromRows(
            listOf(
                ".W....",
                "......",
                "......",
                "......",
                "......",
                "....B.",
            ),
            CheckersSide.BLACK,
            quietPlies = MINI_CHECKERS_DRAW_PLIES - 1,
            drawPlies = MINI_CHECKERS_DRAW_PLIES,
        )
        assertEquals(CheckersResult.ONGOING, board.result())
        assertEquals(CheckersResult.DRAW, board.apply(board.legalMoves().first()).result())
    }
}
