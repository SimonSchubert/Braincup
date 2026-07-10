package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.CatRegionColors
import com.inspiredandroid.braincup.ui.theme.KnotBoardFrame
import com.inspiredandroid.braincup.ui.theme.KnotCellColor
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.puzzleGridLine
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.KnotContent(
    uiState: KnotUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val rows = uiState.rows
    val cols = uiState.cols
    val compact = LocalIsCompactHeight.current

    val gridLineColor = puzzleGridLine()
    val cellColor = KnotCellColor

    // Endpoint and committed-path lookups, so a drag can tell which color it should draw.
    val endpointColorByCell = remember(uiState) {
        HashMap<Int, Int>().apply {
            uiState.endpoints.forEach {
                put(it.a, it.color)
                put(it.b, it.color)
            }
        }
    }
    val pathColorByCell = remember(uiState) {
        HashMap<Int, Int>().apply {
            uiState.paths.forEach { (color, cells) -> cells.forEach { put(it, color) } }
        }
    }

    // Drag state in grid coordinates; only the committed path is sent to the game on release.
    var dragColor by remember(uiState) { mutableStateOf<Int?>(null) }
    var dragCells by remember(uiState) { mutableStateOf<List<Int>>(emptyList()) }

    val boardModifier = if (compact) {
        Modifier.heightIn(max = 260.dp).aspectRatio(cols.toFloat() / rows)
    } else {
        Modifier.widthIn(max = 340.dp).aspectRatio(cols.toFloat() / rows)
    }

    val board: @Composable () -> Unit = {
        PrismCard(face = KnotBoardFrame, facet = PrismFacet.Board, modifier = boardModifier) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(uiState) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val (r, c) = cellAt(offset, size.width, size.height, rows, cols)
                                val cell = r * cols + c
                                val color = endpointColorByCell[cell] ?: pathColorByCell[cell]
                                if (color != null) {
                                    dragColor = color
                                    val existing = uiState.paths[color]
                                    // Grabbing a dot draws fresh; grabbing a path cell continues from
                                    // the endpoint up to that cell.
                                    dragCells = when {
                                        cell in endpointColorByCell -> listOf(cell)
                                        existing != null -> {
                                            val idx = existing.indexOf(cell)
                                            if (idx >= 0) existing.subList(0, idx + 1).toList() else listOf(cell)
                                        }
                                        else -> listOf(cell)
                                    }
                                } else {
                                    dragColor = null
                                    dragCells = emptyList()
                                }
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                if (dragColor != null) {
                                    val (r, c) = cellAt(change.position, size.width, size.height, rows, cols)
                                    val cell = r * cols + c
                                    if (dragCells.isEmpty() || dragCells.last() != cell) {
                                        dragCells = dragCells + cell
                                    }
                                }
                            },
                            onDragEnd = {
                                val color = dragColor
                                val cells = dragCells
                                if (color != null && cells.isNotEmpty()) {
                                    onAnswer("path:$color:${cells.joinToString(",")}")
                                }
                                dragColor = null
                                dragCells = emptyList()
                            },
                            onDragCancel = {
                                dragColor = null
                                dragCells = emptyList()
                            },
                        )
                    }
                    .pointerInput(uiState) {
                        detectTapGestures { offset ->
                            val (r, c) = cellAt(offset, size.width, size.height, rows, cols)
                            val cell = r * cols + c
                            val color = endpointColorByCell[cell] ?: pathColorByCell[cell]
                            if (color != null) onAnswer("clear:$color")
                        }
                    },
            ) {
                val cellW = size.width / cols
                val cellH = size.height / rows
                fun center(cell: Int) = Offset((cell % cols + 0.5f) * cellW, (cell / cols + 0.5f) * cellH)

                drawRect(color = cellColor)

                for (c in 0..cols) {
                    val x = c * cellW
                    drawLine(gridLineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.5.dp.toPx())
                }
                for (r in 0..rows) {
                    val y = r * cellH
                    drawLine(gridLineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5.dp.toPx())
                }

                val pathStroke = minOf(cellW, cellH) * 0.34f
                val dotRadius = minOf(cellW, cellH) * 0.30f

                // Committed paths: thick rounded lines through cell centers.
                uiState.paths.forEach { (color, cells) ->
                    val display = CatRegionColors[color % CatRegionColors.size]
                    for (i in 1 until cells.size) {
                        drawLine(
                            display,
                            center(cells[i - 1]),
                            center(cells[i]),
                            strokeWidth = pathStroke,
                            cap = StrokeCap.Round,
                        )
                    }
                }

                // Live drag preview on top, slightly translucent.
                val previewColor = dragColor
                if (previewColor != null && dragCells.size >= 2) {
                    val display = CatRegionColors[previewColor % CatRegionColors.size].copy(alpha = 0.55f)
                    for (i in 1 until dragCells.size) {
                        drawLine(
                            display,
                            center(dragCells[i - 1]),
                            center(dragCells[i]),
                            strokeWidth = pathStroke,
                            cap = StrokeCap.Round,
                        )
                    }
                }

                // Endpoint dots last so they sit above the paths that meet them.
                uiState.endpoints.forEach { endpoint ->
                    val display = CatRegionColors[endpoint.color % CatRegionColors.size]
                    drawCircle(color = display, radius = dotRadius, center = center(endpoint.a))
                    drawCircle(color = display, radius = dotRadius, center = center(endpoint.b))
                }
            }
        }
    }

    if (compact) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            board()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.level_label, uiState.level),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.game_knot_howto),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                GiveUpButton(onGiveUp = onGiveUp)
            }
        }
    } else {
        Text(
            text = stringResource(Res.string.level_label, uiState.level),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.game_knot_howto),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            board()
        }
        Spacer(Modifier.height(16.dp))
        GiveUpButton(
            onGiveUp = onGiveUp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@DevicePreviews
@Composable
private fun KnotContentPreview() {
    GamePreviewHost {
        KnotContent(
            uiState = KnotUiState(
                rows = 4,
                cols = 4,
                endpoints = persistentListOf(
                    KnotUiState.Endpoint(0, 0, 11),
                    KnotUiState.Endpoint(1, 1, 10),
                ),
                paths = persistentMapOf(),
                level = 1,
            ),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
