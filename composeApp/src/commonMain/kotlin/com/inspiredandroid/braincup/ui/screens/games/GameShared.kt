package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
