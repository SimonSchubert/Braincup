package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.path_finder_demo_caption
import braincup.composeapp.generated.resources.path_finder_demo_title
import com.inspiredandroid.braincup.games.tools.Direction
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import com.inspiredandroid.braincup.games.tools.Color as FigureColor

private const val DemoGridSize = 3

// A soft orange that paints the trail as the path is walked, distinct from the bold start tile.
private val TrailOrange = Color(0xFFFFCC99)

private data class DemoPoint(val col: Int, val row: Int)

private fun DemoPoint.move(direction: Direction): DemoPoint = when (direction) {
    Direction.UP -> copy(row = row - 1)
    Direction.DOWN -> copy(row = row + 1)
    Direction.LEFT -> copy(col = col - 1)
    Direction.RIGHT -> copy(col = col + 1)
}

private val DemoStart = DemoPoint(0, 0)

// A fixed, in-bounds route on the 3x3 board with no consecutive repeats.
private val DemoDirections = listOf(Direction.RIGHT, Direction.DOWN, Direction.RIGHT, Direction.DOWN)

// Every square the path visits, including start and destination: start -> ... -> end.
private val DemoPath: List<DemoPoint> = buildList {
    var point = DemoStart
    add(point)
    for (direction in DemoDirections) {
        point = point.move(direction)
        add(point)
    }
}

private val DemoDestination = DemoPath.last()

private const val StepMillis = 600L

/**
 * Animated tutorial for Path Finder: a 3x3 grid with a bold orange start tile and a row of arrows.
 * As each arrow lights up, the tile the path reaches is painted light orange, tracing the route one
 * step at a time. The destination tile flashes green when reached, then the whole thing loops.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PathFinderDemo(modifier: Modifier = Modifier) {
    // Index of the arrow currently (or last) executed; controls which arrows are lit. -1 means none.
    var activeArrow by remember { mutableStateOf(-1) }
    // How many path tiles beyond the start are painted with the trail color.
    var revealed by remember { mutableStateOf(0) }
    var destinationLit by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            activeArrow = -1
            revealed = 0
            destinationLit = false
            delay(700)

            for (index in DemoDirections.indices) {
                activeArrow = index
                revealed = index + 1
                delay(StepMillis)
            }

            destinationLit = true
            delay(1100)
        }
    }

    // Tiles 1..revealed of the path are lit as the trail; the start tile is always shown on its own.
    val trail = remember(revealed) { DemoPath.subList(1, revealed + 1).toSet() }
    val cellMax = if (LocalIsCompactHeight.current) 56.dp else 72.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.path_finder_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            DemoDirections.forEachIndexed { index, direction ->
                ShapeCanvas(
                    figure = direction.figure,
                    // Arrows light up as the path passes them, reinforcing the step-by-step mapping.
                    modifier = Modifier
                        .size(28.dp)
                        .alpha(if (index <= activeArrow) 1f else 0.3f),
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.widthIn(max = cellMax * DemoGridSize),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for (row in 0 until DemoGridSize) {
                Row {
                    for (col in 0 until DemoGridSize) {
                        val point = DemoPoint(col, row)
                        val face = when {
                            destinationLit && point == DemoDestination -> SuccessGreen
                            point == DemoStart -> FigureColor.ORANGE.composeColor()
                            point in trail -> TrailOrange
                            else -> FigureColor.GREY_LIGHT.composeColor()
                        }
                        PathFinderDemoTile(
                            face = face,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.path_finder_demo_caption),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun PathFinderDemoTile(
    face: Color,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = face,
        modifier = modifier,
        isClickable = false,
        onClick = {},
    ) {}
}
