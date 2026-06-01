package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
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

@Composable
fun ChessPieceIcon(resource: DrawableResource, isWhite: Boolean) {
    val painter = painterResource(resource)
    val fill = ColorFilter.tint(if (isWhite) Color.White else Color.Black)
    Canvas(modifier = Modifier.size(44.dp)) {
        if (isWhite) {
            for ((dx, dy) in ChessHaloDeltas) {
                translate(left = dx, top = dy) {
                    with(painter) { draw(size = this@Canvas.size, colorFilter = ChessOutlineFilter) }
                }
            }
        }
        with(painter) { draw(size = size, colorFilter = fill) }
    }
}
