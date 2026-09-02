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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PathFinderCell(
    cell: FigureCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val face = when (cell.state) {
        AnswerFeedbackState.WRONG -> MaterialTheme.colorScheme.errorContainer
        AnswerFeedbackState.CORRECT -> SuccessGreenSoft
        else -> cell.figure.color.composeColor()
    }
    val isClickable = cell.state == AnswerFeedbackState.NORMAL
    val cellModifier = if (cell.state == AnswerFeedbackState.DIMMED) modifier.alpha(0.3f) else modifier
    PrismTile(
        face = face,
        modifier = cellModifier,
        isClickable = isClickable,
        isSelected = cell.state == AnswerFeedbackState.DIMMED,
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
        AnswerFeedbackState.WRONG -> MaterialTheme.colorScheme.errorContainer
        AnswerFeedbackState.CORRECT -> SuccessGreenSoft
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    val isNormal = cell.state == AnswerFeedbackState.NORMAL
    val isDimmed = cell.state == AnswerFeedbackState.DIMMED
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
    progress: () -> Float,
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
    // Held as state rather than read into a local: the bar reads it in its draw phase, so the
    // countdown costs one repaint a frame instead of recomposing this and everything under it.
    val progress = remember(restartKey) { mutableFloatStateOf(1f) }
    val paused by rememberUpdatedState(isTimerPaused)
    LaunchedEffect(restartKey) {
        val startNanos = withFrameNanos { it }
        var pausedAccumulationNanos = 0L
        while (progress.floatValue > 0f) {
            if (paused) {
                val pauseStart = withFrameNanos { it }
                // Suspend until the pause lifts rather than asking for a frame per vsync: the
                // pause lasts as long as the quit dialog is open, and spinning on withFrameNanos
                // kept the choreographer awake - and this composable animating - the whole time.
                snapshotFlow { paused }.first { !it }
                pausedAccumulationNanos += withFrameNanos { it } - pauseStart
                continue
            }
            val nowNanos = withFrameNanos { it }
            val elapsedMillis = (nowNanos - startNanos - pausedAccumulationNanos) / 1_000_000f
            progress.floatValue = (1f - elapsedMillis / totalMillis).coerceAtLeast(0f)
        }
    }
    TimeProgressIndicator(progress = { progress.floatValue }, modifier = modifier)
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

/**
 * How many laid-out clue glyphs a puzzle board's measurer keeps.
 *
 * The default is eight and a board carries more clues than that, so they evicted each other and
 * every redraw - which for these boards means every pointer move of a drag - re-laid all of them.
 */
internal const val PuzzleClueCacheSize = 32

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
 * [BoardInstructionLine] as the level puzzles show it, styled for the layout it lands in.
 *
 * All five level boards made the same two choices by hand: the compact sidebar has no room for body
 * text and no column to centre in, the tall layout has both. Taking the layout rather than a style
 * and a modifier keeps that pairing in one place.
 */
@Composable
internal fun ColumnScope.LevelBoardInstructionLine(
    text: String,
    isError: Boolean,
    compactLayout: Boolean,
) {
    BoardInstructionLine(
        text = text,
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

/**
 * The status block of a level board that shows a progress counter above its how-to line.
 *
 * Cat Queens and Solo Chess both count something down while you play (regions filled, pieces left)
 * and both put it directly above the instruction, with the same gap.
 */
@Composable
internal fun ColumnScope.LevelBoardStatus(
    compactLayout: Boolean,
    instruction: String,
    isError: Boolean,
    progress: @Composable () -> Unit,
) {
    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) { progress() }
    Spacer(Modifier.height(6.dp))
    LevelBoardInstructionLine(text = instruction, isError = isError, compactLayout = compactLayout)
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

/**
 * Face and content colours for an answer tile, plus the modifier that dims and un-clicks it.
 *
 * Value Comparison and Mental Rotations both show the same kind of answer tile: correct turns
 * green, wrong turns to the error container, and the options that are no longer live fade back.
 * The tiles themselves differ (one holds a fraction, the other a word) so only the colouring is
 * shared. Flags deliberately keeps its own, which fades further and tints its dimmed state.
 */
internal data class AnswerTileColors(val face: Color, val content: Color, val isInteractive: Boolean)

@Composable
internal fun answerTileColors(state: AnswerFeedbackState): AnswerTileColors = AnswerTileColors(
    face = when (state) {
        AnswerFeedbackState.WRONG -> MaterialTheme.colorScheme.errorContainer
        AnswerFeedbackState.CORRECT -> SuccessGreen
        else -> Primary
    },
    content = when (state) {
        AnswerFeedbackState.WRONG -> MaterialTheme.colorScheme.onErrorContainer
        else -> Color.White
    },
    isInteractive = state == AnswerFeedbackState.NORMAL,
)

/** Dims an answer tile that is no longer live, and gives a live one the pointer cursor. */
internal fun Modifier.answerTileState(state: AnswerFeedbackState): Modifier = this
    .then(if (state == AnswerFeedbackState.DIMMED) Modifier.alpha(0.3f) else Modifier)
    .then(if (state == AnswerFeedbackState.NORMAL) Modifier.hoverHand() else Modifier)

/**
 * A square [gridSize] x [gridSize] board of equally sized cells, laid out row by row.
 *
 * Lights Out and Sliding Puzzle are the same board with a different cell drawn in it, so the loop,
 * the 4.dp gaps and the [squareTileSize] call live here. [cell] is handed the flat index and the
 * size its tile has to fit.
 */
@Composable
internal fun SquareTileBoard(
    gridSize: Int,
    compact: Boolean,
    cell: @Composable (index: Int, size: Dp) -> Unit,
) {
    val cellSize = squareTileSize(gridSize, compact)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (row in 0 until gridSize) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (col in 0 until gridSize) {
                    cell(row * gridSize + col, cellSize)
                }
            }
        }
    }
}

/** Tile size for a square n x n board of [PrismTile]s, shrunk when vertical room is tight. */
internal fun squareTileSize(gridSize: Int, compact: Boolean): Dp = when {
    compact -> if (gridSize <= 4) 48.dp else 40.dp
    gridSize == 3 -> 72.dp
    gridSize == 4 -> 60.dp
    else -> 52.dp
}

/** Draws [measured] centred on ([centerX], [centerY]). */
internal fun DrawScope.drawTextCentered(
    measured: TextLayoutResult,
    centerX: Float,
    centerY: Float,
    color: Color = Color.Unspecified,
) {
    drawText(
        measured,
        color = color,
        topLeft = Offset(centerX - measured.size.width / 2f, centerY - measured.size.height / 2f),
    )
}

/**
 * The bold outline around each region of a board whose cells are grouped by [regionIdByCellIndex].
 *
 * An edge is drawn only where the neighbouring cell belongs to a different region, or where the
 * board ends. Cat Queens draws this three times over (on the board, in its tutorial and on its menu
 * tile) and the three have to agree, because the regions are the rule the puzzle is played by.
 */
internal fun DrawScope.drawRegionBorders(
    regionIdByCellIndex: List<Int>,
    rows: Int,
    cols: Int,
    color: Color,
    strokeWidth: Float,
) {
    val cellW = size.width / cols
    val cellH = size.height / rows
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            val index = r * cols + c
            val region = regionIdByCellIndex[index]
            val x0 = c * cellW
            val y0 = r * cellH
            val x1 = x0 + cellW
            val y1 = y0 + cellH
            if (r == 0 || regionIdByCellIndex[index - cols] != region) {
                drawLine(color, Offset(x0, y0), Offset(x1, y0), strokeWidth = strokeWidth)
            }
            if (r == rows - 1 || regionIdByCellIndex[index + cols] != region) {
                drawLine(color, Offset(x0, y1), Offset(x1, y1), strokeWidth = strokeWidth)
            }
            if (c == 0 || regionIdByCellIndex[index - 1] != region) {
                drawLine(color, Offset(x0, y0), Offset(x0, y1), strokeWidth = strokeWidth)
            }
            if (c == cols - 1 || regionIdByCellIndex[index + 1] != region) {
                drawLine(color, Offset(x1, y0), Offset(x1, y1), strokeWidth = strokeWidth)
            }
        }
    }
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

/** The small spaced caption naming the phase a timed round is in. */
@Composable
internal fun PhaseLabel(text: String, accent: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = accent,
        letterSpacing = 3.sp,
    )
}
