package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Direction
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.localizedName
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.MedalBronze
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.MedalSilver
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

private val FlashCrowdBlue = ComposeColor(0xFF4285F4)
private val FlashCrowdYellow = ComposeColor(0xFFFBBC04)

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
            .hoverHand()
            .clickable(onClick = onPlay),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(ComposeColor(gameType.accentColor)),
            contentAlignment = Alignment.Center,
        ) {
            MaterialTheme(colorScheme = LightColorScheme) {
                CompositionLocalProvider(LocalContentColor provides LightColorScheme.onSurface) {
                    GamePreview(gameType)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp)
                .padding(start = 8.dp, top = 6.dp, bottom = 6.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(gameType.displayNameRes),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            val medalTint = when {
                highscore >= gameType.goldScore -> MedalGold
                highscore >= gameType.silverScore -> MedalSilver
                highscore > 0 -> MedalBronze
                else -> null
            }
            if (medalTint != null) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    painterResource(Res.drawable.ic_icons8_counter_gold),
                    contentDescription = null,
                    tint = medalTint,
                    modifier = Modifier
                        .size(28.dp)
                        .hoverHand()
                        .clickable(onClick = onViewScore),
                )
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
        GameType.MINI_SUDOKU -> MiniSudokuPreview()
        GameType.PATTERN_SEQUENCE -> PatternSequencePreview()
        GameType.GHOST_GRID -> GhostGridPreview()
        GameType.COLOR_CONFUSION -> ColorConfusionPreview()
        GameType.ORBIT_TRACKER -> OrbitTrackerPreview()
        GameType.FLASH_CROWD -> FlashCrowdPreview()
    }
}

// --- Preview Composables ---

@Composable
private fun AnomalyPuzzlePreview() {
    val figures = remember {
        listOf(
            Figure(Shape.STAR, Color.RED),
            Figure(Shape.STAR, Color.RED),
            Figure(Shape.STAR, Color.RED),
            Figure(Shape.STAR, Color.BLUE),
        )
    }
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
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
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Arrow directions row
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            remember { listOf(Direction.RIGHT, Direction.DOWN, Direction.RIGHT) }.forEach {
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
        val gridFigures = remember {
            List(4) { row ->
                List(4) { col ->
                    val isStart = row == startRow && col == startCol
                    Figure(Shape.SQUARE, if (isStart) Color.ORANGE else Color.GREY_LIGHT)
                }
            }
        }
        Column(modifier = Modifier.weight(1f).aspectRatio(1f)) {
            gridFigures.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { figure ->
                        ShapeCanvas(
                            figure = figure,
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
            text = "${Shape.HEART.localizedName()} = 3",
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = "${Color.BLUE.localizedName()} = 4",
            style = MaterialTheme.typography.labelSmall,
            color = Color.BLUE.composeColor,
        )
    }
}

@Composable
private fun VisualMemoryPreview() {
    val figures = remember {
        listOf(
            Figure(Shape.TRIANGLE, Color.RED),
            null,
            Figure(Shape.CIRCLE, Color.GREEN),
            null,
        )
    }
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
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
            remember { listOf(4, 9, 3, 7, 2) }.forEach { num ->
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
    MathText(
        text = "5 + 3 * 2",
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
private fun FractionCalculationPreview() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FractionText(
            numerator = "2",
            denominator = "3",
            style = MaterialTheme.typography.titleLarge,
        )
        Text("\u00D7", style = MaterialTheme.typography.titleLarge)
        FractionText(
            numerator = "4",
            denominator = "5",
            style = MaterialTheme.typography.titleLarge,
        )
    }
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
            text = stringResource(Res.string.preview_vs),
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
private fun MiniSudokuPreview() {
    val grid = listOf(
        listOf("1", "2"),
        listOf("", "4"),
    )
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val cellSize = 36.dp
    val outerFrame = 3.dp
    val blockSeparator = 3.dp

    Box(modifier = Modifier.background(gridLineColor)) {
        Column(modifier = Modifier.padding(outerFrame)) {
            grid.forEachIndexed { rowIndex, row ->
                Row {
                    row.forEachIndexed { colIndex, cell ->
                        Box(
                            modifier = Modifier
                                .padding(
                                    end = if (colIndex == 0) blockSeparator else 0.dp,
                                    bottom = if (rowIndex == 0) blockSeparator else 0.dp,
                                )
                                .size(cellSize)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = cell,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GhostGridPreview() {
    val highlighted = setOf(0, 4, 7) // diagonal cells highlighted
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
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
        remember {
            listOf(
                Figure(Shape.TRIANGLE, Color.RED),
                Figure(Shape.TRIANGLE, Color.BLUE),
                Figure(Shape.TRIANGLE, Color.RED),
            )
        }.forEach { figure ->
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
private fun OrbitTrackerPreview() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val balls = remember {
        listOf(
            Triple(0.3f, 0.25f, true),
            Triple(0.7f, 0.4f, false),
            Triple(0.5f, 0.7f, true),
            Triple(0.2f, 0.6f, false),
            Triple(0.8f, 0.75f, true),
        )
    }
    Canvas(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(8.dp),
    ) {
        val ballRadius = size.width * 0.06f
        balls.forEach { (x, y, isTarget) ->
            drawCircle(
                color = if (isTarget) primaryColor else onSurfaceVariantColor,
                radius = ballRadius,
                center = Offset(x * size.width, y * size.height),
            )
        }
    }
}

@Composable
private fun ColorConfusionPreview() {
    val words = remember {
        listOf(
            Pair(Color.RED, Color.RED),
            Pair(Color.BLUE, Color.GREEN),
            Pair(Color.GREEN, Color.GREEN),
            Pair(Color.PURPLE, Color.YELLOW),
        )
    }
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        words.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { (wordColor, fontColor) ->
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
                            text = wordColor.localizedName(),
                            style = MaterialTheme.typography.labelSmall,
                            color = fontColor.composeColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlashCrowdPreview() {
    val leftDots = remember {
        listOf(
            Triple(0.2f, 0.2f, 0.06f),
            Triple(0.5f, 0.15f, 0.05f),
            Triple(0.8f, 0.3f, 0.055f),
            Triple(0.3f, 0.5f, 0.05f),
            Triple(0.7f, 0.55f, 0.06f),
            Triple(0.15f, 0.75f, 0.055f),
            Triple(0.5f, 0.8f, 0.05f),
            Triple(0.85f, 0.78f, 0.06f),
        )
    }
    val rightDots = remember {
        listOf(
            Triple(0.3f, 0.25f, 0.09f),
            Triple(0.7f, 0.3f, 0.085f),
            Triple(0.5f, 0.6f, 0.09f),
            Triple(0.25f, 0.8f, 0.08f),
            Triple(0.75f, 0.78f, 0.085f),
        )
    }
    Row(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Canvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
            leftDots.forEach { (x, y, r) ->
                drawCircle(
                    color = FlashCrowdBlue,
                    radius = r * size.width,
                    center = Offset(x * size.width, y * size.height),
                )
            }
        }
        Canvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
            rightDots.forEach { (x, y, r) ->
                drawCircle(
                    color = FlashCrowdYellow,
                    radius = r * size.width,
                    center = Offset(x * size.width, y * size.height),
                )
            }
        }
    }
}
