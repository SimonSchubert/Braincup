package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.CatRegionColors
import com.inspiredandroid.braincup.ui.theme.ErrorRed
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.PuzzleGridInk
import com.inspiredandroid.braincup.ui.theme.ShikakuBoardFrame
import com.inspiredandroid.braincup.ui.theme.ShikakuCellColor
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import com.inspiredandroid.braincup.ui.theme.puzzleGridLine
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.ShikakuContent(
    uiState: ShikakuUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val rows = uiState.rows
    val cols = uiState.cols
    val compact = LocalIsCompactHeight.current

    val gridLineColor = puzzleGridLine()
    val cellColor = ShikakuCellColor
    val regionBorderColor = PuzzleGridInk
    val invalidBorder = ErrorRed
    val invalidOverlay = ErrorRed.copy(alpha = 0.25f)
    val previewColor = Primary
    val clueColor = PuzzleGridInk
    val numberFont = numberFontFamily()
    val textMeasurer = rememberTextMeasurer()

    // Drag state in grid coordinates; only the committed rectangle is sent to the game.
    var dragStart by remember(uiState) { mutableStateOf<Pair<Int, Int>?>(null) }
    var dragCurrent by remember(uiState) { mutableStateOf<Pair<Int, Int>?>(null) }

    val boardModifier = Modifier.puzzleBoardModifier(rows, cols, compact)

    val board: @Composable () -> Unit = {
        PrismCard(face = ShikakuBoardFrame, facet = PrismFacet.Board, modifier = boardModifier) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(uiState) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val cell = cellAt(offset, size.width, size.height, rows, cols)
                                dragStart = cell
                                dragCurrent = cell
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                dragCurrent = cellAt(change.position, size.width, size.height, rows, cols)
                            },
                            onDragEnd = {
                                val start = dragStart
                                val end = dragCurrent
                                if (start != null && end != null) {
                                    onAnswer(BoardCommand.draw(start.first, start.second, end.first, end.second))
                                }
                                dragStart = null
                                dragCurrent = null
                            },
                            onDragCancel = {
                                dragStart = null
                                dragCurrent = null
                            },
                        )
                    }
                    .pointerInput(uiState) {
                        detectTapGestures { offset ->
                            val (row, col) = cellAt(offset, size.width, size.height, rows, cols)
                            onAnswer(BoardCommand.delete(row, col))
                        }
                    },
            ) {
                val cellW = size.width / cols
                val cellH = size.height / rows

                fun rectTopLeft(top: Int, left: Int) = Offset(left * cellW, top * cellH)
                fun rectSize(top: Int, left: Int, bottom: Int, right: Int) = Size((right - left + 1) * cellW, (bottom - top + 1) * cellH)

                drawRect(color = cellColor)

                // Committed rectangles: color fills first so grid lines and borders draw on top.
                uiState.rectangles.forEachIndexed { idx, rect ->
                    val regionColor = CatRegionColors[idx % CatRegionColors.size]
                    drawRect(
                        color = regionColor.copy(alpha = 0.65f),
                        topLeft = rectTopLeft(rect.top, rect.left),
                        size = rectSize(rect.top, rect.left, rect.bottom, rect.right),
                    )
                    if (!rect.isValid) {
                        drawRect(
                            color = invalidOverlay,
                            topLeft = rectTopLeft(rect.top, rect.left),
                            size = rectSize(rect.top, rect.left, rect.bottom, rect.right),
                        )
                    }
                }

                drawPuzzleGridLines(rows, cols, gridLineColor, strokeWidth = 1.5.dp.toPx())

                // Bold border around each rectangle: dark for valid (like Cat Queens), red for invalid.
                uiState.rectangles.forEach { rect ->
                    val borderColor = if (rect.isValid) regionBorderColor else invalidBorder
                    val x0 = rectTopLeft(rect.top, rect.left).x
                    val y0 = rectTopLeft(rect.top, rect.left).y
                    val x1 = x0 + rectSize(rect.top, rect.left, rect.bottom, rect.right).width
                    val y1 = y0 + rectSize(rect.top, rect.left, rect.bottom, rect.right).height
                    val bold = 3.dp.toPx()
                    drawLine(borderColor, Offset(x0, y0), Offset(x1, y0), strokeWidth = bold)
                    drawLine(borderColor, Offset(x0, y1), Offset(x1, y1), strokeWidth = bold)
                    drawLine(borderColor, Offset(x0, y0), Offset(x0, y1), strokeWidth = bold)
                    drawLine(borderColor, Offset(x1, y0), Offset(x1, y1), strokeWidth = bold)
                }

                val start = dragStart
                val end = dragCurrent
                if (start != null && end != null) {
                    val top = minOf(start.first, end.first)
                    val bottom = maxOf(start.first, end.first)
                    val left = minOf(start.second, end.second)
                    val right = maxOf(start.second, end.second)
                    drawRect(
                        color = previewColor.copy(alpha = 0.25f),
                        topLeft = rectTopLeft(top, left),
                        size = rectSize(top, left, bottom, right),
                    )
                    drawRect(
                        color = previewColor,
                        topLeft = rectTopLeft(top, left),
                        size = rectSize(top, left, bottom, right),
                        style = Stroke(width = 3.dp.toPx()),
                    )
                }

                val clueStyle = TextStyle(
                    color = clueColor,
                    fontSize = (cellH * 0.42f).toSp(),
                    fontFamily = numberFont,
                    fontWeight = FontWeight.Bold,
                )
                uiState.clueByCellIndex.forEach { (index, value) ->
                    val r = index / cols
                    val c = index % cols
                    val measured = textMeasurer.measure(AnnotatedString(value.toString()), style = clueStyle)
                    val centerX = c * cellW + cellW / 2f
                    val centerY = r * cellH + cellH / 2f
                    drawText(
                        measured,
                        topLeft = Offset(centerX - measured.size.width / 2f, centerY - measured.size.height / 2f),
                    )
                }
            }
        }
    }

    LevelPuzzleLayout(
        level = uiState.level,
        headerGap = 4.dp,
        board = board,
        actions = { GiveUpButton(onGiveUp = onGiveUp) },
    ) { compactLayout ->
        BoardInstructionLine(
            text = stringResource(Res.string.game_shikaku_howto),
            isError = false,
            style = if (compactLayout) {
                MaterialTheme.typography.labelMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            modifier = if (compactLayout) {
                Modifier
            } else {
                Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 24.dp)
            },
        )
    }
}

@DevicePreviews
@Composable
private fun ShikakuContentPreview() {
    GamePreviewHost {
        ShikakuContent(
            uiState = ShikakuUiState(
                rows = 3,
                cols = 3,
                clueByCellIndex = persistentMapOf(0 to 4, 2 to 2, 6 to 3),
                rectangles = persistentListOf(),
                level = 1,
            ),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
