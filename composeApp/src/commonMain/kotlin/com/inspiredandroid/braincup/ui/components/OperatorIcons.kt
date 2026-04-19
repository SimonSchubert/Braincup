package com.inspiredandroid.braincup.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private const val STROKE_WIDTH = 3f

private fun operator(
    strokeOnly: Boolean = true,
    pathBlock: PathBuilder.() -> Unit,
): ImageVector =
    ImageVector.Builder(
        name = "op",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).path(
        fill = if (strokeOnly) null else SolidColor(Color.White),
        stroke = if (strokeOnly) SolidColor(Color.White) else null,
        strokeLineWidth = if (strokeOnly) STROKE_WIDTH else 0f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        pathBuilder = pathBlock,
    ).build()

private val Plus = operator {
    moveTo(5f, 12f); lineTo(19f, 12f)
    moveTo(12f, 5f); lineTo(12f, 19f)
}

private val Minus = operator {
    moveTo(5f, 12f); lineTo(19f, 12f)
}

private val Multiply = operator {
    moveTo(6f, 6f); lineTo(18f, 18f)
    moveTo(18f, 6f); lineTo(6f, 18f)
}

private val Divide = operator(strokeOnly = false) {
    // Horizontal bar
    moveTo(5f, 10.5f); horizontalLineTo(19f); verticalLineTo(13.5f); horizontalLineTo(5f); close()
    // Upper dot
    moveTo(12f, 5.5f)
    arcTo(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 12f, 9.5f)
    arcTo(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 12f, 5.5f)
    close()
    // Lower dot
    moveTo(12f, 14.5f)
    arcTo(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 12f, 18.5f)
    arcTo(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 12f, 14.5f)
    close()
}

private val LeftParen = operator {
    moveTo(14.5f, 4f)
    curveTo(11f, 7f, 9.5f, 9.5f, 9.5f, 12f)
    curveTo(9.5f, 14.5f, 11f, 17f, 14.5f, 20f)
}

private val RightParen = operator {
    moveTo(9.5f, 4f)
    curveTo(13f, 7f, 14.5f, 9.5f, 14.5f, 12f)
    curveTo(14.5f, 14.5f, 13f, 17f, 9.5f, 20f)
}

internal val OperatorIcons: Map<String, ImageVector> = mapOf(
    "+" to Plus,
    "-" to Minus,
    "*" to Multiply,
    "×" to Multiply,
    "/" to Divide,
    "÷" to Divide,
    "(" to LeftParen,
    ")" to RightParen,
)
