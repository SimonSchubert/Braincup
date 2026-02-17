package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Direction
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTile(
    gameType: GameType,
    highscore: Int,
    onPlay: () -> Unit,
    onViewScore: () -> Unit,
) {
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(onClick = onPlay),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            GamePreview(gameType)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 6.dp, bottom = 6.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(gameType.displayNameRes),
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (highscore > 0) {
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(onClick = onViewScore),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$highscore",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun GamePreview(gameType: GameType) {
    when (gameType) {
        GameType.ANOMALY_PUZZLE -> AnomalyPuzzlePreview()
        GameType.PATH_FINDER -> PathFinderPreview()
        GameType.COLORED_SHAPES -> ColoredShapesPreview()
        GameType.VISUAL_MEMORY -> VisualMemoryPreview()
        GameType.MENTAL_CALCULATION -> MentalCalculationPreview()
        GameType.SHERLOCK_CALCULATION -> SherlockCalculationPreview()
        GameType.CHAIN_CALCULATION -> ChainCalculationPreview()
        GameType.FRACTION_CALCULATION -> FractionCalculationPreview()
        GameType.VALUE_COMPARISON -> ValueComparisonPreview()
        GameType.GRID_SOLVER -> GridSolverPreview()
        GameType.PATTERN_SEQUENCE -> PatternSequencePreview()
        GameType.GHOST_GRID -> GhostGridPreview()
        GameType.COLOR_CONFUSION -> ColorConfusionPreview()
    }
}

// --- Preview Composables ---

@Composable
private fun AnomalyPuzzlePreview() {
    val figures = listOf(
        Figure(Shape.STAR, Color.RED),
        Figure(Shape.STAR, Color.RED),
        Figure(Shape.STAR, Color.RED),
        Figure(Shape.STAR, Color.BLUE),
    )
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        figures.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { figure ->
                    ShapeCanvas(
                        figure = figure,
                        modifier = Modifier.weight(1f).aspectRatio(1f).padding(3.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PathFinderPreview() {
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Arrow directions row
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            listOf(Direction.RIGHT, Direction.DOWN, Direction.RIGHT).forEach {
                ShapeCanvas(
                    figure = it.figure,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        // 4x4 mini grid
        val startRow = 1
        val startCol = 1
        Column(modifier = Modifier.weight(1f).aspectRatio(1f)) {
            for (row in 0 until 4) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 4) {
                        val isStart = row == startRow && col == startCol
                        ShapeCanvas(
                            figure = Figure(
                                Shape.SQUARE,
                                if (isStart) Color.ORANGE else Color.GREY_LIGHT,
                            ),
                            modifier = Modifier.weight(1f).aspectRatio(1f).padding(1.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColoredShapesPreview() {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ShapeCanvas(
            figure = Figure(Shape.HEART, Color.BLUE),
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "heart = 3",
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = "blue = 4",
            style = MaterialTheme.typography.labelSmall,
            color = Color.BLUE.composeColor,
        )
    }
}

@Composable
private fun VisualMemoryPreview() {
    val figures = listOf(
        Figure(Shape.TRIANGLE, Color.RED),
        null,
        Figure(Shape.CIRCLE, Color.GREEN),
        null,
    )
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        figures.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { figure ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                if (figure != null) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                },
                                MaterialTheme.shapes.extraSmall,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (figure != null) {
                            ShapeCanvas(
                                figure = figure,
                                modifier = Modifier.fillMaxSize().padding(4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MentalCalculationPreview() {
    Text(
        text = "+15",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun SherlockCalculationPreview() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp),
    ) {
        Text(
            text = "= 26",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(4, 9, 3, 7, 2).forEach { num ->
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$num",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChainCalculationPreview() {
    Text(
        text = "5 + 3 \u00D7 2",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun FractionCalculationPreview() {
    Text(
        text = "(2/3) \u00D7 (4/5)",
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ValueComparisonPreview() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp),
    ) {
        Text(
            text = "3 + 8",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "vs",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "5 + 4",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun GridSolverPreview() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp),
    ) {
        // 2x2 grid + sum cards
        val grid = listOf(listOf("?", "?"), listOf("?", "?"))
        val rowSums = listOf(8, 6)
        val colSums = listOf(7, 7)

        grid.forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                row.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.shapes.extraSmall,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = cell, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.shapes.extraSmall,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "=${rowSums[rowIndex]}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            colSums.forEach { sum ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.shapes.extraSmall,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "=$sum",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Spacer(Modifier.size(24.dp))
        }
    }
}

@Composable
private fun GhostGridPreview() {
    val highlighted = setOf(0, 4, 7) // diagonal cells highlighted
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        for (row in 0 until 3) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    val isHighlighted = index in highlighted
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                if (isHighlighted) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                },
                                MaterialTheme.shapes.extraSmall,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun PatternSequencePreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp),
    ) {
        listOf(
            Figure(Shape.TRIANGLE, Color.RED),
            Figure(Shape.TRIANGLE, Color.BLUE),
            Figure(Shape.TRIANGLE, Color.RED),
        ).forEach { figure ->
            ShapeCanvas(
                figure = figure,
                modifier = Modifier.size(28.dp),
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.shapes.small,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "?",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun ColorConfusionPreview() {
    val words = listOf(
        Triple("RED", Color.RED, true),
        Triple("BLUE", Color.GREEN, false),
        Triple("GREEN", Color.GREEN, true),
        Triple("PURPLE", Color.YELLOW, false),
    )
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        words.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { (word, color, matching) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.shapes.extraSmall,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = word,
                            style = MaterialTheme.typography.labelSmall,
                            color = color.composeColor,
                        )
                    }
                }
            }
        }
    }
}
