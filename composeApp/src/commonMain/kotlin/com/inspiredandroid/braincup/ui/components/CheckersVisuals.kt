package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.inspiredandroid.braincup.checkers.CheckersPiece
import com.inspiredandroid.braincup.checkers.CheckersSide
import com.inspiredandroid.braincup.ui.theme.CheckersBlackPiece
import com.inspiredandroid.braincup.ui.theme.CheckersCrown
import com.inspiredandroid.braincup.ui.theme.CheckersWhitePiece
import com.inspiredandroid.braincup.ui.theme.PrismFacet

@Composable
fun CheckersDisc(piece: CheckersPiece, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        ColorPrismCell(
            face = if (piece.side == CheckersSide.BLACK) CheckersBlackPiece else CheckersWhitePiece,
            facet = PrismFacet.Dot,
            modifier = Modifier.fillMaxSize(),
        )
        if (piece.isKing) {
            Canvas(Modifier.fillMaxSize(0.5f)) { drawCrown(CheckersCrown) }
        }
    }
}

private fun DrawScope.drawCrown(color: Color) {
    val w = size.width
    val h = size.height
    val crown = Path().apply {
        moveTo(0.05f * w, 0.85f * h)
        lineTo(0f, 0.2f * h)
        lineTo(0.3f * w, 0.5f * h)
        lineTo(0.5f * w, 0.1f * h)
        lineTo(0.7f * w, 0.5f * h)
        lineTo(w, 0.2f * h)
        lineTo(0.95f * w, 0.85f * h)
        close()
    }
    drawPath(crown, color)
}
