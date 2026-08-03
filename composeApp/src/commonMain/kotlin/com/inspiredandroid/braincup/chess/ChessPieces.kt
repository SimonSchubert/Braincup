package com.inspiredandroid.braincup.chess

/**
 * Board-size-independent chess vocabulary shared by the 5x5 Mini Chess / Solo Chess boards
 * (`games.minichess`) and the full 8x8 board (`normalchess`). Bounds checking stays with each
 * board, which knows its own size.
 */
enum class PieceType { KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN }

enum class PieceColor { WHITE, BLACK }

fun PieceColor.opposite(): PieceColor = if (this == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

data class Piece(val type: PieceType, val color: PieceColor)

data class Square(val file: Int, val row: Int)

data class Move(
    val from: Square,
    val to: Square,
    val promotion: PieceType? = null,
)
