package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    // Every state draws the same figure on the same tile; only the tile's face and whether it still
    // responds to touch change, so they are picked here rather than repeated per branch.
    val face = when (cell.state) {
        FigureCellState.WRONG -> MaterialTheme.colorScheme.errorContainer
        FigureCellState.CORRECT -> SuccessGreenSoft
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    val isNormal = cell.state == FigureCellState.NORMAL
    val isDimmed = cell.state == FigureCellState.DIMMED
    PrismTile(
        face = face,
        modifier = if (isDimmed) modifier.alpha(0.3f) else modifier,
        isClickable = isNormal,
        isSelected = isDimmed,
        onClick = if (isNormal) onClick else ({}),
    ) {
        ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
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

/**
 * Board sizing for the canvas puzzles: square boards cap on the short edge so the whole grid stays
 * on screen, and the aspect ratio comes from the grid itself so non-square boards are not stretched.
 * Compact height trades width for height because the side panel is beside the board, not under it.
 */
internal fun Modifier.puzzleBoardModifier(rows: Int, cols: Int, compact: Boolean): Modifier = if (compact) {
    heightIn(max = 260.dp).aspectRatio(cols.toFloat() / rows)
} else {
    widthIn(max = 340.dp).aspectRatio(cols.toFloat() / rows)
}

/**
 * The shape every level-based puzzle screen has: a level heading, some status under it, the board,
 * and the actions that end the round.
 *
 * The two layouts are different enough to be worth branching rather than reflowing. On a tall screen
 * the parts stack; on a short one there is no vertical room, so the board moves beside its panel
 * ([CompactGameRow]) and the status text drops to a smaller style. [status] is handed which layout it
 * is in so callers can pick that style, and is a slot rather than a string because some puzzles put
 * two things there (a progress counter and an instruction line) and some put nothing.
 *
 * [headerGap] exists because the puzzles carrying a progress counter breathe a little wider under the
 * heading than the ones carrying a single line.
 *
 * Tower of Hanoi and Prism Clear deliberately do not use this: Hanoi puts its message *below* the
 * board in a fixed-height box to stop the board jumping, and Prism Clear shows its line only while
 * the board is stuck. Bending either into this signature would cost more parameters than it saves.
 */
@Composable
internal fun ColumnScope.LevelPuzzleLayout(
    level: Int,
    headerGap: Dp,
    board: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    status: @Composable ColumnScope.(compact: Boolean) -> Unit,
) {
    if (LocalIsCompactHeight.current) {
        CompactGameRow {
            board()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LevelHeader(level)
                Spacer(Modifier.height(headerGap))
                status(true)
                Spacer(Modifier.height(8.dp))
                actions()
            }
        }
    } else {
        // Centred while it fits, scrolling once it does not. The heading, the status line and the
        // buttons all grow with the font scale while the boards are built from fixed cell sizes,
        // so on a narrow screen at a large one the parts no longer share the height between them.
        // Without the scroll it was whatever came last that went - the give-up button, cut off at
        // the bottom edge of a column that had no more room to give.
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val viewport = maxHeight
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = viewport),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                LevelHeader(level)
                Spacer(Modifier.height(headerGap))
                status(false)
                Spacer(Modifier.height(16.dp))
                // The padding keeps a board's raised edge on screen on a narrow window.
                Box(modifier = Modifier.padding(horizontal = 16.dp)) { board() }
                Spacer(Modifier.height(16.dp))
                actions()
            }
        }
    }
}
