package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

private val FlashCrowdBlue = ComposeColor(0xFF4285F4)
private val FlashCrowdYellow = ComposeColor(0xFFFBBC04)

// Tile previews always render on light pastel backgrounds (gameType.accentColor),
// so set text colors explicitly rather than inheriting from the surrounding (possibly dark) theme.
private val PreviewTextColor = LightColorScheme.onSurface

private val AnomalyPuzzlePreviewFigures = listOf(
    Figure(Shape.STAR, Color.RED),
    Figure(Shape.STAR, Color.RED),
    Figure(Shape.STAR, Color.RED),
    Figure(Shape.STAR, Color.BLUE),
)

private val PathFinderPreviewDirections = listOf(Direction.RIGHT, Direction.DOWN, Direction.RIGHT)

private val PathFinderPreviewGrid: List<List<Figure>> = run {
    val startRow = 1
    val startCol = 1
    List(4) { row ->
        List(4) { col ->
            val isStart = row == startRow && col == startCol
            Figure(Shape.SQUARE, if (isStart) Color.ORANGE else Color.GREY_LIGHT)
        }
    }
}

private val VisualMemoryPreviewFigures: List<Figure?> = listOf(
    Figure(Shape.TRIANGLE, Color.RED),
    null,
    Figure(Shape.CIRCLE, Color.GREEN),
    null,
)

private val SherlockPreviewNumbers = listOf(4, 9, 3, 7, 2)

private val PatternSequencePreviewFigures = listOf(
    Figure(Shape.TRIANGLE, Color.RED),
    Figure(Shape.TRIANGLE, Color.BLUE),
    Figure(Shape.TRIANGLE, Color.RED),
)

private val OrbitTrackerPreviewBalls = listOf(
    Triple(0.3f, 0.25f, true),
    Triple(0.7f, 0.4f, false),
    Triple(0.5f, 0.7f, true),
    Triple(0.2f, 0.6f, false),
    Triple(0.8f, 0.75f, true),
)

private val ColorConfusionPreviewWords = listOf(
    Color.RED to Color.RED,
    Color.BLUE to Color.GREEN,
    Color.GREEN to Color.GREEN,
    Color.PURPLE to Color.YELLOW,
)

private val FlashCrowdPreviewLeftDots = listOf(
    Triple(0.2f, 0.2f, 0.06f),
    Triple(0.5f, 0.15f, 0.05f),
    Triple(0.8f, 0.3f, 0.055f),
    Triple(0.3f, 0.5f, 0.05f),
    Triple(0.7f, 0.55f, 0.06f),
    Triple(0.15f, 0.75f, 0.055f),
    Triple(0.5f, 0.8f, 0.05f),
    Triple(0.85f, 0.78f, 0.06f),
)

private val FlashCrowdPreviewRightDots = listOf(
    Triple(0.3f, 0.25f, 0.09f),
    Triple(0.7f, 0.3f, 0.085f),
    Triple(0.5f, 0.6f, 0.09f),
    Triple(0.25f, 0.8f, 0.08f),
    Triple(0.75f, 0.78f, 0.085f),
)

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
                GamePreview(gameType)
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
                gameType.meetsScore(highscore, gameType.goldScore) -> MedalGold
                gameType.meetsScore(highscore, gameType.silverScore) -> MedalSilver
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
        GameType.SCHULTE_TABLE -> SchulteTablePreview()
        GameType.PATTERN_SEQUENCE -> PatternSequencePreview()
        GameType.GHOST_GRID -> GhostGridPreview()
        GameType.COLOR_CONFUSION -> ColorConfusionPreview()
        GameType.ORBIT_TRACKER -> OrbitTrackerPreview()
        GameType.FLASH_CROWD -> FlashCrowdPreview()
        GameType.MINI_CHESS -> MiniChessPreview()
    }
}

// --- Preview Composables ---

@Composable
private fun AnomalyPuzzlePreview() {
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnomalyPuzzlePreviewFigures.chunked(2).forEach { row ->
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
            PathFinderPreviewDirections.forEach {
                ShapeCanvas(
                    figure = it.figure,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        // 4x4 mini grid
        Column(modifier = Modifier.weight(1f).aspectRatio(1f)) {
            PathFinderPreviewGrid.forEach { row ->
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
            color = PreviewTextColor,
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
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        VisualMemoryPreviewFigures.chunked(2).forEach { row ->
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
        color = PreviewTextColor,
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
            SherlockPreviewNumbers.forEach { num ->
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
                        color = LightColorScheme.onSecondaryContainer,
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
        color = PreviewTextColor,
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
            color = PreviewTextColor,
        )
        Text("\u00D7", style = MaterialTheme.typography.titleLarge, color = PreviewTextColor)
        FractionText(
            numerator = "4",
            denominator = "5",
            style = MaterialTheme.typography.titleLarge,
            color = PreviewTextColor,
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
            color = PreviewTextColor,
        )
        Text(
            text = stringResource(Res.string.preview_vs),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "5 + 4",
            style = MaterialTheme.typography.titleMedium,
            color = PreviewTextColor,
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
private fun SchulteTablePreview() {
    // 3×3 grid: jumbled numbers with a couple already tapped (dimmed) to show the mechanic.
    data class Cell(val number: Int, val tapped: Boolean)
    val grid = listOf(
        listOf(Cell(3, false), Cell(1, true), Cell(6, false)),
        listOf(Cell(2, true), Cell(9, false), Cell(7, false)),
        listOf(Cell(5, false), Cell(8, false), Cell(4, false)),
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(8.dp),
    ) {
        grid.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                row.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (cell.tapped) {
                                    MaterialTheme.colorScheme.surfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = cell.number.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = if (cell.tapped) 0.4f else 1f,
                            ),
                        )
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
        PatternSequencePreviewFigures.forEach { figure ->
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
    Canvas(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(8.dp),
    ) {
        val ballRadius = size.width * 0.06f
        OrbitTrackerPreviewBalls.forEach { (x, y, isTarget) ->
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
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ColorConfusionPreviewWords.chunked(2).forEach { row ->
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
    Row(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Canvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
            FlashCrowdPreviewLeftDots.forEach { (x, y, r) ->
                drawCircle(
                    color = FlashCrowdBlue,
                    radius = r * size.width,
                    center = Offset(x * size.width, y * size.height),
                )
            }
        }
        Canvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
            FlashCrowdPreviewRightDots.forEach { (x, y, r) ->
                drawCircle(
                    color = FlashCrowdYellow,
                    radius = r * size.width,
                    center = Offset(x * size.width, y * size.height),
                )
            }
        }
    }
}

@Composable
private fun MiniChessPreview() {
    val light = ComposeColor(0xFFEEEED2)
    val dark = ComposeColor(0xFF769656)
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val cellSize = 26.dp
    val outerFrame = 3.dp

    data class PreviewPiece(val drawable: DrawableResource, val isWhite: Boolean)
    val pieces = mapOf(
        0 to PreviewPiece(Res.drawable.ic_chess_king, isWhite = true), // a1
        4 to PreviewPiece(Res.drawable.ic_chess_pawn, isWhite = false), // b2
        8 to PreviewPiece(Res.drawable.ic_chess_queen, isWhite = false), // c3
    )

    // Frame the board with a thin border, mirroring the Mini Sudoku tile preview.
    Box(modifier = Modifier.background(borderColor)) {
        Column(modifier = Modifier.padding(outerFrame)) {
            for (row in 2 downTo 0) {
                Row {
                    for (col in 0..2) {
                        val isLight = (row + col) % 2 == 0
                        val flatIndex = row * 3 + col
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .background(if (isLight) light else dark),
                            contentAlignment = Alignment.Center,
                        ) {
                            pieces[flatIndex]?.let { piece ->
                                MiniChessPreviewPiece(piece.drawable, piece.isWhite)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniChessPreviewPiece(drawable: DrawableResource, isWhite: Boolean) {
    val painter = painterResource(drawable)
    val outline = ColorFilter.tint(ComposeColor.Black)
    val fill = ColorFilter.tint(if (isWhite) ComposeColor.White else ComposeColor.Black)
    Canvas(modifier = Modifier.size(20.dp)) {
        if (isWhite) {
            // 8-direction halo for a clean black outline so the white silhouette stands
            // out on the light tile.
            val deltas = listOf(
                -1f to -1f,
                0f to -1f,
                1f to -1f,
                -1f to 0f,
                1f to 0f,
                -1f to 1f,
                0f to 1f,
                1f to 1f,
            )
            for ((dx, dy) in deltas) {
                translate(left = dx, top = dy) {
                    with(painter) { draw(size = this@Canvas.size, colorFilter = outline) }
                }
            }
        }
        with(painter) { draw(size = size, colorFilter = fill) }
    }
}
