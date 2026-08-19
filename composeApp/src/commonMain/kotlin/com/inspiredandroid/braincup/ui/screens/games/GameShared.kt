package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PathFinderCell(
    cell: FigureCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val face = when (cell.state) {
        FigureCellState.WRONG -> MaterialTheme.colorScheme.errorContainer
        FigureCellState.CORRECT -> SuccessGreenSoft
        else -> cell.figure.color.composeColor()
    }
    val isClickable = cell.state == FigureCellState.NORMAL
    val cellModifier = if (cell.state == FigureCellState.DIMMED) modifier.alpha(0.3f) else modifier
    PrismTile(
        face = face,
        modifier = cellModifier,
        isClickable = isClickable,
        isSelected = cell.state == FigureCellState.DIMMED,
        onClick = onClick,
    ) {}
}

@Composable
internal fun FigureCellContent(
    cell: FigureCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (cell.state) {
        FigureCellState.NORMAL -> PrismTile(
            face = MaterialTheme.colorScheme.surfaceContainer,
            modifier = modifier,
            onClick = onClick,
        ) {
            ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
        }
        FigureCellState.WRONG -> PrismTile(
            face = MaterialTheme.colorScheme.errorContainer,
            modifier = modifier,
            isClickable = false,
            onClick = {},
        ) {
            ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
        }
        FigureCellState.CORRECT -> PrismTile(
            face = SuccessGreenSoft,
            modifier = modifier,
            isClickable = false,
            onClick = {},
        ) {
            ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
        }
        FigureCellState.DIMMED -> PrismTile(
            face = MaterialTheme.colorScheme.surfaceContainer,
            modifier = modifier.alpha(0.3f),
            isClickable = false,
            isSelected = true,
            onClick = {},
        ) {
            ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
        }
    }
}

@Composable
internal fun TimeProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    PrismProgressBar(
        progress = progress,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        fillColor = Primary,
        modifier = modifier.height(12.dp),
    )
}

@Composable
internal fun MemorizeTimeProgressBar(
    totalMillis: Float,
    isTimerPaused: Boolean,
    modifier: Modifier = Modifier,
    restartKey: Any? = Unit,
) {
    var progress by remember(restartKey) { mutableFloatStateOf(1f) }
    val paused by rememberUpdatedState(isTimerPaused)
    LaunchedEffect(restartKey) {
        val startNanos = withFrameNanos { it }
        var pausedAccumulationNanos = 0L
        while (progress > 0f) {
            if (paused) {
                val pauseStart = withFrameNanos { it }
                while (paused) {
                    withFrameNanos { it }
                }
                pausedAccumulationNanos += withFrameNanos { it } - pauseStart
                continue
            }
            val nowNanos = withFrameNanos { it }
            val elapsedMillis = (nowNanos - startNanos - pausedAccumulationNanos) / 1_000_000f
            progress = (1f - elapsedMillis / totalMillis).coerceAtLeast(0f)
            withFrameNanos { it }
        }
    }
    TimeProgressIndicator(progress = progress, modifier = modifier)
}

@Composable
internal fun StopwatchDisplay(elapsedMillis: Long, modifier: Modifier = Modifier) {
    val seconds = elapsedMillis / 1000
    val tenths = (elapsedMillis % 1000) / 100
    Text(
        text = stringResource(Res.string.format_seconds, "$seconds.$tenths"),
        style = MaterialTheme.typography.titleLarge,
        fontFamily = numberFontFamily(),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally),
    )
}

internal fun cellAt(offset: Offset, width: Int, height: Int, rows: Int, cols: Int): Pair<Int, Int> {
    val col = (offset.x / width * cols).toInt().coerceIn(0, cols - 1)
    val row = (offset.y / height * rows).toInt().coerceIn(0, rows - 1)
    return row to col
}

/**
 * "Level N" heading shown by every level-based puzzle. In the tall layout callers pass
 * `Modifier.align(Alignment.CenterHorizontally)`; in the compact sidebar the surrounding
 * column already centers it.
 */
@Composable
internal fun LevelHeader(level: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.level_label, level),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier,
    )
}

/** "Moves: N" counter the level puzzles show under their [LevelHeader]. */
@Composable
internal fun MovesLabel(moves: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.moves_label, moves),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

/**
 * The how-to line of a puzzle board, which doubles as its error line: while [isError] it turns
 * bold and red, and it goes back to normal on its own once the board is legal again. The compact
 * sidebar passes the smaller label style.
 */
@Composable
internal fun BoardInstructionLine(
    text: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Text(
        text = text,
        style = style,
        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = if (isError) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

/**
 * Compact-height layout shell: the board and its side panel sit next to each other on one
 * centered row, because there is no vertical room to stack them.
 */
@Composable
internal fun CompactGameRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** Tile size for a square n x n board of [PrismTile]s, shrunk when vertical room is tight. */
internal fun squareTileSize(gridSize: Int, compact: Boolean): Dp = when {
    compact -> if (gridSize <= 4) 48.dp else 40.dp
    gridSize == 3 -> 72.dp
    gridSize == 4 -> 60.dp
    else -> 52.dp
}

/** Draws the cell separators of a [rows] x [cols] board filling the whole canvas. */
internal fun DrawScope.drawPuzzleGridLines(rows: Int, cols: Int, color: Color, strokeWidth: Float) {
    val cellW = size.width / cols
    val cellH = size.height / rows
    for (c in 0..cols) {
        val x = c * cellW
        drawLine(color, Offset(x, 0f), Offset(x, size.height), strokeWidth = strokeWidth)
    }
    for (r in 0..rows) {
        val y = r * cellH
        drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = strokeWidth)
    }
}
