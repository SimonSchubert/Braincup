package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.inspiredandroid.braincup.games.minichess.PieceType
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

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

fun chessPieceResource(type: PieceType): DrawableResource = when (type) {
    PieceType.KING -> Res.drawable.ic_chess_king
    PieceType.QUEEN -> Res.drawable.ic_chess_queen
    PieceType.ROOK -> Res.drawable.ic_chess_rook
    PieceType.BISHOP -> Res.drawable.ic_chess_bishop
    PieceType.KNIGHT -> Res.drawable.ic_chess_knight
    PieceType.PAWN -> Res.drawable.ic_chess_pawn
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
