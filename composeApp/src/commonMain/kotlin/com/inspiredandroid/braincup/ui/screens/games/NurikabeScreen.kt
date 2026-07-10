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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.NurikabeBoardFrame
import com.inspiredandroid.braincup.ui.theme.NurikabeIslandColor
import com.inspiredandroid.braincup.ui.theme.NurikabeSeaColor
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.PuzzleGridInk
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import com.inspiredandroid.braincup.ui.theme.puzzleGridLine
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.NurikabeContent(
    uiState: NurikabeUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val rows = uiState.rows
    val cols = uiState.cols
    val compact = LocalIsCompactHeight.current

    val gridLineColor = puzzleGridLine(alpha = 0.4f)
    val islandColor = NurikabeIslandColor
    val seaColor = NurikabeSeaColor
    val previewColor = Primary
    val clueColor = PuzzleGridInk
    val satisfiedFill = SuccessGreenSoft
    val satisfiedColor = SuccessGreen
    val invalidFill = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
    val invalidColor = MaterialTheme.colorScheme.error
    val poolColor = MaterialTheme.colorScheme.error.copy(alpha = 0.55f)
    val numberFont = numberFontFamily()
    val textMeasurer = rememberTextMeasurer()

    // Drag paints a whole stroke at once; only the committed cells are sent on release so the
    // gesture (keyed on uiState) is never cancelled mid-stroke. The first touched cell fixes the
    // mode: an empty cell fills sea, a sea cell erases it.
    var dragCells by remember(uiState) { mutableStateOf(emptySet<Int>()) }
    var dragFills by remember(uiState) { mutableStateOf(true) }

    val boardModifier = if (compact) {
        Modifier.heightIn(max = 260.dp).aspectRatio(cols.toFloat() / rows)
    } else {
        Modifier.widthIn(max = 340.dp).aspectRatio(cols.toFloat() / rows)
    }

    val board: @Composable () -> Unit = {
        PrismCard(face = NurikabeBoardFrame, facet = PrismFacet.Board, modifier = boardModifier) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(uiState) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val (r, c) = cellAt(offset, size.width, size.height, rows, cols)
                                val index = r * cols + c
                                if (index in uiState.clues) {
                                    dragFills = true
                                    dragCells = emptySet()
                                } else {
                                    dragFills = index !in uiState.walls
                                    dragCells = setOf(index)
                                }
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val (r, c) = cellAt(change.position, size.width, size.height, rows, cols)
                                val index = r * cols + c
                                if (index !in uiState.clues) dragCells = dragCells + index
                            },
                            onDragEnd = {
                                val cells = dragCells
                                if (cells.isNotEmpty()) {
                                    onAnswer("paint:${if (dragFills) 1 else 0}:${cells.joinToString(",")}")
                                }
                                dragCells = emptySet()
                            },
                            onDragCancel = { dragCells = emptySet() },
                        )
                    }
                    .pointerInput(uiState) {
                        detectTapGestures { offset ->
                            val (r, c) = cellAt(offset, size.width, size.height, rows, cols)
                            onAnswer("toggle:${r * cols + c}")
                        }
                    },
            ) {
                val cellW = size.width / cols
                val cellH = size.height / rows

                fun cellTopLeft(index: Int) = Offset((index % cols) * cellW, (index / cols) * cellH)
                val cellSize = Size(cellW, cellH)

                drawRect(color = islandColor)

                // Live island feedback: a completed island reads green, an over-filled one red.
                uiState.satisfiedCells.forEach { index ->
                    drawRect(color = satisfiedFill, topLeft = cellTopLeft(index), size = cellSize)
                }
                uiState.invalidCells.forEach { index ->
                    drawRect(color = invalidFill, topLeft = cellTopLeft(index), size = cellSize)
                }

                uiState.walls.forEach { index ->
                    drawRect(color = seaColor, topLeft = cellTopLeft(index), size = cellSize)
                }

                // Flag forbidden 2x2 sea pools in red over the painted sea.
                uiState.poolCells.forEach { index ->
                    drawRect(color = poolColor, topLeft = cellTopLeft(index), size = cellSize)
                }

                // All islands correct but the sea is in pieces: flag the stranded sea so the player
                // knows it still needs to be joined into one region.
                uiState.disconnectedSeaCells.forEach { index ->
                    drawRect(color = previewColor.copy(alpha = 0.4f), topLeft = cellTopLeft(index), size = cellSize)
                    drawRect(
                        color = previewColor,
                        topLeft = cellTopLeft(index),
                        size = cellSize,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }

                // Live stroke preview: filled cells turn into sea, erased cells back to island, each
                // outlined so the affected cells read clearly under either mode.
                dragCells.forEach { index ->
                    drawRect(
                        color = if (dragFills) seaColor.copy(alpha = 0.55f) else islandColor,
                        topLeft = cellTopLeft(index),
                        size = cellSize,
                    )
                    drawRect(
                        color = previewColor,
                        topLeft = cellTopLeft(index),
                        size = cellSize,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }

                for (c in 0..cols) {
                    val x = c * cellW
                    drawLine(gridLineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.5.dp.toPx())
                }
                for (r in 0..rows) {
                    val y = r * cellH
                    drawLine(gridLineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5.dp.toPx())
                }

                val clueFontSize = (cellH * 0.42f).toSp()
                uiState.clues.forEach { (index, value) ->
                    val color = when (index) {
                        in uiState.satisfiedCells -> satisfiedColor
                        in uiState.invalidCells -> invalidColor
                        else -> clueColor
                    }
                    val style = TextStyle(
                        color = color,
                        fontSize = clueFontSize,
                        fontFamily = numberFont,
                        fontWeight = FontWeight.Bold,
                    )
                    val measured = textMeasurer.measure(AnnotatedString(value.toString()), style = style)
                    val centerX = (index % cols) * cellW + cellW / 2f
                    val centerY = (index / cols) * cellH + cellH / 2f
                    drawText(
                        measured,
                        topLeft = Offset(centerX - measured.size.width / 2f, centerY - measured.size.height / 2f),
                    )
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
                    text = stringResource(Res.string.game_nurikabe_howto),
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
            text = stringResource(Res.string.game_nurikabe_howto),
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

@GameDevicePreviews
@Composable
private fun NurikabeContentPreview() {
    GamePreviewHost {
        NurikabeContent(
            uiState = NurikabeUiState(
                rows = 4,
                cols = 4,
                clues = persistentMapOf(0 to 3, 2 to 2, 15 to 2),
                walls = persistentSetOf(1, 3, 5),
                satisfiedCells = persistentSetOf(),
                invalidCells = persistentSetOf(),
                poolCells = persistentSetOf(),
                disconnectedSeaCells = persistentSetOf(),
                level = 1,
            ),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
