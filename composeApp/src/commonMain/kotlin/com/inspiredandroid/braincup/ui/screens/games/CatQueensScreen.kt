package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.icons.CatFace
import com.inspiredandroid.braincup.ui.theme.CatQueensBoardFrame
import com.inspiredandroid.braincup.ui.theme.CatRegionColors
import com.inspiredandroid.braincup.ui.theme.ErrorRed
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.PuzzleGridInk
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.CatQueensContent(
    uiState: CatQueensUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val n = uiState.size
    val compact = LocalIsCompactHeight.current

    val gridLineColor = Color(0xFF000000).copy(alpha = 0.15f)
    val borderColor = PuzzleGridInk
    val invalidColor = ErrorRed
    val validColor = SuccessGreen
    val catPainter = rememberVectorPainter(CatFace)

    val placed = uiState.cats.size
    val solvedLook = placed == n && uiState.invalidCats.isEmpty()
    val boardModifier = puzzleBoardModifier(n, n, compact)

    // The board sits on a beveled prism tray, matching the Mini Chess board's raised look.
    val board: @Composable () -> Unit = {
        PrismCard(face = CatQueensBoardFrame, facet = PrismFacet.Board, modifier = boardModifier) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(uiState) {
                        detectTapGestures { offset ->
                            val (r, c) = cellAt(offset, size.width, size.height, n, n)
                            onAnswer("${r * n + c}")
                        }
                    },
            ) {
                val cellW = size.width / n
                val cellH = size.height / n
                val cellSize = Size(cellW, cellH)
                fun topLeft(index: Int) = Offset((index % n) * cellW, (index / n) * cellH)

                for (index in 0 until n * n) {
                    val color = CatRegionColors[uiState.regionIdByCellIndex[index] % CatRegionColors.size]
                    drawRect(color = color, topLeft = topLeft(index), size = cellSize)
                }

                // Thin lines between every cell, then bold lines along region boundaries so the zones
                // read clearly even when two neighbouring hues are hard to tell apart.
                drawPuzzleGridLines(n, n, gridLineColor, 1.dp.toPx())

                val bold = 3.dp.toPx()
                for (r in 0 until n) {
                    for (c in 0 until n) {
                        val index = r * n + c
                        val region = uiState.regionIdByCellIndex[index]
                        val x0 = c * cellW
                        val y0 = r * cellH
                        val x1 = x0 + cellW
                        val y1 = y0 + cellH
                        if (r == 0 || uiState.regionIdByCellIndex[index - n] != region) {
                            drawLine(borderColor, Offset(x0, y0), Offset(x1, y0), strokeWidth = bold)
                        }
                        if (r == n - 1 || uiState.regionIdByCellIndex[index + n] != region) {
                            drawLine(borderColor, Offset(x0, y1), Offset(x1, y1), strokeWidth = bold)
                        }
                        if (c == 0 || uiState.regionIdByCellIndex[index - 1] != region) {
                            drawLine(borderColor, Offset(x0, y0), Offset(x0, y1), strokeWidth = bold)
                        }
                        if (c == n - 1 || uiState.regionIdByCellIndex[index + 1] != region) {
                            drawLine(borderColor, Offset(x1, y0), Offset(x1, y1), strokeWidth = bold)
                        }
                    }
                }

                // Each cat shows live correctness: a green ring while it breaks no rule, a red ring
                // and tint the moment it clashes with another cat (row, column, zone or touching).
                val pad = cellW * 0.12f
                val catSize = Size(cellW - 2 * pad, cellH - 2 * pad)
                val ring = 3.dp.toPx()
                val inset = cellW * 0.07f
                uiState.cats.forEach { index ->
                    val tl = topLeft(index)
                    val invalid = index in uiState.invalidCats
                    if (invalid) {
                        drawRect(color = invalidColor.copy(alpha = 0.18f), topLeft = tl, size = cellSize)
                    }
                    translate(left = tl.x + pad, top = tl.y + pad) {
                        with(catPainter) { draw(catSize) }
                    }
                    drawRect(
                        color = if (invalid) invalidColor else validColor,
                        topLeft = Offset(tl.x + inset, tl.y + inset),
                        size = Size(cellW - 2 * inset, cellH - 2 * inset),
                        style = Stroke(width = ring),
                    )
                }
            }
        }
    }

    val progress: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Image(imageVector = CatFace, contentDescription = null, modifier = Modifier.size(22.dp))
            Text(
                text = "$placed / $n",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (solvedLook) SuccessGreen else MaterialTheme.colorScheme.onSurface,
            )
        }
    }

    // While a rule is broken the how-to line is replaced by the specific error; it resets itself
    // the moment the conflict is cleared because `violation` goes back to null.
    val instruction = when (uiState.violation) {
        CatQueensUiState.Violation.ROW -> stringResource(Res.string.cat_queens_error_row)
        CatQueensUiState.Violation.COLUMN -> stringResource(Res.string.cat_queens_error_column)
        CatQueensUiState.Violation.ZONE -> stringResource(Res.string.cat_queens_error_zone)
        CatQueensUiState.Violation.TOUCHING -> stringResource(Res.string.cat_queens_error_touch)
        null -> stringResource(Res.string.game_cat_queens_howto)
    }
    val isError = uiState.violation != null

    LevelPuzzleLayout(
        level = uiState.level,
        headerGap = 6.dp,
        board = board,
        actions = { GiveUpButton(onGiveUp = onGiveUp) },
    ) { compactLayout ->
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) { progress() }
        Spacer(Modifier.height(6.dp))
        BoardInstructionLine(
            text = instruction,
            isError = isError,
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
private fun CatQueensContentPreview() {
    GamePreviewHost {
        CatQueensContent(
            uiState = CatQueensUiState(
                size = 4,
                regionIdByCellIndex = persistentListOf(
                    0, 0, 1, 1,
                    0, 2, 1, 3,
                    2, 2, 3, 3,
                    2, 2, 3, 3,
                ),
                cats = persistentSetOf(2, 4),
                invalidCats = persistentSetOf(),
                level = 1,
            ),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
