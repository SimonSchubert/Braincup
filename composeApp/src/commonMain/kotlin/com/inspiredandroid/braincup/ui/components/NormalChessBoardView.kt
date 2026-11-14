package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.chess.PieceColor
import com.inspiredandroid.braincup.chess.PieceType
import com.inspiredandroid.braincup.chess.Square
import com.inspiredandroid.braincup.normalchess.NORMAL_CHESS_SIZE
import com.inspiredandroid.braincup.normalchess.NormalChessBoard
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

/** The full 8x8 board shared by Normal Chess and Checkmate. [flipped] puts black at the bottom. */
@Composable
fun NormalChessBoardView(
    board: NormalChessBoard,
    selected: Square?,
    highlightedTargets: ImmutableSet<Square>,
    lastMoveFrom: Square?,
    lastMoveTo: Square?,
    interactive: Boolean,
    onSquareTapped: (Square) -> Unit,
    stalematingTargets: ImmutableSet<Square> = persistentSetOf(),
    flipped: Boolean = false,
    // Header, spacers and action buttons around the board take roughly this much height.
    verticalChrome: Dp = 140.dp,
) {
    val whiteInCheck = remember(board) { board.isInCheck(PieceColor.WHITE) }
    val blackInCheck = remember(board) { board.isInCheck(PieceColor.BLACK) }
    // Scrollable scaffolds measure children with unbounded height; the real viewport height
    // comes from LocalScaffoldBodyHeight (provideCompactHeight on AppScaffold).
    val scaffoldBodyHeight = LocalScaffoldBodyHeight.current
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        val heightBudget = ((scaffoldBodyHeight ?: maxHeight) - verticalChrome)
            .coerceAtLeast(160.dp)
        // Grow with the smaller of width / remaining height; cap huge desktops.
        val boardSide = minOf(maxWidth, heightBudget, 480.dp)
        val cellSize = boardSide / NORMAL_CHESS_SIZE
        val pieceSize = cellSize * 0.9f

        PrismCard(
            face = ChessBoardFrame,
            facet = PrismFacet.Board,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Column {
                for (screenRow in 0 until NORMAL_CHESS_SIZE) {
                    Row {
                        for (screenCol in 0 until NORMAL_CHESS_SIZE) {
                            val row = if (flipped) screenRow else NORMAL_CHESS_SIZE - 1 - screenRow
                            val col = if (flipped) NORMAL_CHESS_SIZE - 1 - screenCol else screenCol
                            val square = Square(col, row)
                            val piece = board.pieceAt(square)
                            // a1 (file 0, row 0) is dark in standard chess; queen on d1 is light.
                            val isLight = (row + col) % 2 == 1
                            val isTarget = square in highlightedTargets
                            val showCheckRing = piece?.type == PieceType.KING &&
                                (
                                    (piece.color == PieceColor.WHITE && whiteInCheck) ||
                                        (piece.color == PieceColor.BLACK && blackInCheck)
                                    )
                            val target = when {
                                square in stalematingTargets -> ChessSquareTarget.Stalemate
                                isTarget && piece != null -> ChessSquareTarget.Capture
                                isTarget -> ChessSquareTarget.LegalEmpty
                                else -> ChessSquareTarget.None
                            }
                            ChessSquare(
                                size = cellSize,
                                isLight = isLight,
                                isSelected = selected == square,
                                isLastMove = square == lastMoveFrom || square == lastMoveTo,
                                showCheckRing = showCheckRing,
                                target = target,
                                enabled = interactive,
                                onClick = { onSquareTapped(square) },
                            ) {
                                piece?.let {
                                    Box(
                                        modifier = Modifier.size(pieceSize),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        ChessPieceIcon(
                                            resource = chessPieceResource(it.type),
                                            isWhite = it.color == PieceColor.WHITE,
                                            figureSize = pieceSize,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
