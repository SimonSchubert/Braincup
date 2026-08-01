package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.ic_chess_bishop
import braincup.composeapp.generated.resources.ic_chess_king
import braincup.composeapp.generated.resources.ic_chess_knight
import braincup.composeapp.generated.resources.ic_chess_pawn
import braincup.composeapp.generated.resources.ic_chess_queen
import braincup.composeapp.generated.resources.ic_chess_rook
import com.inspiredandroid.braincup.games.SoloChessGame
import com.inspiredandroid.braincup.games.minichess.PieceType
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import com.inspiredandroid.braincup.normalchess.PieceType as NormalPieceType

val ChessLightSquare = Color(0xFFEEEED2)
val ChessDarkSquare = Color(0xFF6FA055)
val ChessBoardFrame = Color(0xFF3F5E2F)
val ChessSelected = Color(0xFFB9CAFF)
val ChessLastMove = Color(0x66FFD54F)
val ChessLegalDot = Color(0x66000000)
val ChessCaptureTint = Color(0x55E53935)
val ChessDrawDot = Color(0xCCFBC02D)
val ChessDrawTint = Color(0x66FBC02D)
val ChessCheckTint = Color(0x88E53935)
val ChessWarning = Color(0xFFE65100)

// Solo Chess capture-charge pips and the "spent piece" grey, shared by the board and its tutorial.
val SoloChessSpentTint = Color(0xFF9AA0A6) // muted grey for a piece with no captures left
val SoloChessPipTray = Color(0x66000000)
val SoloChessPipFilled = Color(0xFFFFCA28) // an amber "charge" = one capture remaining
val SoloChessPipEmpty = Color(0x55FFFFFF) // a used-up capture slot

val ChessHaloDeltas: List<Pair<Float, Float>> = listOf(
    -1f to -1f,
    0f to -1f,
    1f to -1f,
    -1f to 0f,
    1f to 0f,
    -1f to 1f,
    0f to 1f,
    1f to 1f,
)

val ChessOutlineFilter = ColorFilter.tint(Color.Black)

/** Legal-move / outcome overlay style for a single square. */
enum class ChessSquareTarget {
    None,

    /** Empty square that is a legal destination. */
    LegalEmpty,

    /** Occupied square that can be captured. */
    Capture,

    /** Move that would stalemate / draw-oriented highlight. */
    Stalemate,
}

/**
 * Shared chess square chrome used by Mini Chess, Normal Chess, and Solo Chess.
 *
 * Callers own piece rendering (and Solo capture pips) via [content].
 */
@Composable
fun ChessSquare(
    size: Dp,
    isLight: Boolean,
    isSelected: Boolean = false,
    isLastMove: Boolean = false,
    showCheckRing: Boolean = false,
    target: ChessSquareTarget = ChessSquareTarget.None,
    /** Solo Chess king square gold underlay (skipped when selected). */
    showKingHighlight: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val baseColor = if (isLight) ChessLightSquare else ChessDarkSquare
    val markerSize = size * 0.3f
    Box(
        modifier = modifier
            .size(size)
            .background(if (isSelected) ChessSelected else baseColor)
            .clickable(enabled = enabled, onClick = onClick)
            .hoverHand(enabled),
        contentAlignment = Alignment.Center,
    ) {
        if (isLastMove && !isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(ChessLastMove),
            )
        }
        if (showKingHighlight && !isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(ChessDrawTint),
            )
        }
        if (showCheckRing) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(ChessCheckTint),
            )
        }
        when (target) {
            ChessSquareTarget.Capture -> Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(ChessCaptureTint),
            )
            ChessSquareTarget.Stalemate -> Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(ChessDrawTint),
            )
            ChessSquareTarget.None, ChessSquareTarget.LegalEmpty -> Unit
        }
        content()
        when (target) {
            ChessSquareTarget.LegalEmpty -> Box(
                modifier = Modifier
                    .size(markerSize)
                    .background(ChessLegalDot),
            )
            ChessSquareTarget.Stalemate -> Box(
                modifier = Modifier
                    .size(markerSize)
                    .background(ChessDrawDot),
            )
            ChessSquareTarget.None, ChessSquareTarget.Capture -> Unit
        }
    }
}

fun chessPieceResource(type: PieceType): DrawableResource = when (type) {
    PieceType.KING -> Res.drawable.ic_chess_king
    PieceType.QUEEN -> Res.drawable.ic_chess_queen
    PieceType.ROOK -> Res.drawable.ic_chess_rook
    PieceType.BISHOP -> Res.drawable.ic_chess_bishop
    PieceType.KNIGHT -> Res.drawable.ic_chess_knight
    PieceType.PAWN -> Res.drawable.ic_chess_pawn
}

fun chessPieceResource(type: NormalPieceType): DrawableResource = when (type) {
    NormalPieceType.KING -> Res.drawable.ic_chess_king
    NormalPieceType.QUEEN -> Res.drawable.ic_chess_queen
    NormalPieceType.ROOK -> Res.drawable.ic_chess_rook
    NormalPieceType.BISHOP -> Res.drawable.ic_chess_bishop
    NormalPieceType.KNIGHT -> Res.drawable.ic_chess_knight
    NormalPieceType.PAWN -> Res.drawable.ic_chess_pawn
}

@Composable
fun ChessPieceIcon(
    resource: DrawableResource,
    isWhite: Boolean,
    figureSize: Dp = 44.dp,
    /** Overrides the fill color (e.g. a gray for a "spent" piece). A black outline is still drawn. */
    tint: Color? = null,
) {
    val painter = painterResource(resource)
    val fill = ColorFilter.tint(tint ?: if (isWhite) Color.White else Color.Black)
    Canvas(modifier = Modifier.size(figureSize)) {
        if (isWhite || tint != null) {
            for ((dx, dy) in ChessHaloDeltas) {
                translate(left = dx, top = dy) {
                    with(painter) { draw(size = this@Canvas.size, colorFilter = ChessOutlineFilter) }
                }
            }
        }
        with(painter) { draw(size = size, colorFilter = fill) }
    }
}

/** Capture "charges": one amber pip per remaining capture (max two). */
@Composable
fun SoloChessCapturePips(remaining: Int, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRoundRect(color = SoloChessPipTray, cornerRadius = CornerRadius(size.height / 2f))
        val radius = size.height * 0.3f
        val slots = SoloChessGame.MAX_CAPTURES
        val step = size.width / (slots + 1)
        for (i in 0 until slots) {
            drawCircle(
                color = if (i < remaining) SoloChessPipFilled else SoloChessPipEmpty,
                radius = radius,
                center = Offset(step * (i + 1), size.height / 2f),
            )
        }
    }
}
