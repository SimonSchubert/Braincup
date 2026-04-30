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
import com.inspiredandroid.braincup.ui.theme.LightsOutOffColor
import com.inspiredandroid.braincup.ui.theme.LightsOutOnColor
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

private val MiniSudokuPreviewGrid: List<List<String>> = listOf(
    listOf("1", "2"),
    listOf("", "4"),
)

private data class SchulteCell(val number: Int, val tapped: Boolean)

private val SchulteTablePreviewGrid: List<List<SchulteCell>> = listOf(
    listOf(SchulteCell(3, false), SchulteCell(1, true), SchulteCell(6, false)),
    listOf(SchulteCell(2, true), SchulteCell(9, false), SchulteCell(7, false)),
    listOf(SchulteCell(5, false), SchulteCell(8, false), SchulteCell(4, false)),
)

private val GhostGridPreviewHighlighted: Set<Int> = setOf(0, 4, 7)

private val LightsOutPreviewOn: Set<Int> = setOf(1, 3, 4, 5, 7)

private val SlidingPuzzlePreviewLabels: List<Int> = listOf(1, 2, 3, 4, 0, 5, 7, 8, 6)

private val MiniChessLightSquare = ComposeColor(0xFFEEEED2)
private val MiniChessDarkSquare = ComposeColor(0xFF769656)

private data class MiniChessPreviewPlacement(val drawable: DrawableResource, val isWhite: Boolean)

private val MiniChessPreviewPieces: Map<Int, MiniChessPreviewPlacement> = mapOf(
    0 to MiniChessPreviewPlacement(Res.drawable.ic_chess_king, isWhite = true),
    4 to MiniChessPreviewPlacement(Res.drawable.ic_chess_pawn, isWhite = false),
    8 to MiniChessPreviewPlacement(Res.drawable.ic_chess_queen, isWhite = false),
)

private val ChessHaloDeltas: List<Pair<Float, Float>> = listOf(
    -1f to -1f,
    0f to -1f,
    1f to -1f,
    -1f to 0f,
    1f to 0f,
    -1f to 1f,
    0f to 1f,
    1f to 1f,
)

private val ChessOutlineFilter = ColorFilter.tint(ComposeColor.Black)

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
        GameType.LIGHTS_OUT -> LightsOutPreview()
        GameType.SLIDING_PUZZLE -> SlidingPuzzlePreview()
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
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
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
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "= 26",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            SherlockPreviewNumbers.forEach { num ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(2.dp)
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
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
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
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp)
            .background(gridLineColor),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            MiniSudokuPreviewGrid.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    row.forEach { cell ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
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
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SchulteTablePreviewGrid.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                if (cell.tapped) {
                                    MaterialTheme.colorScheme.surfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                MaterialTheme.shapes.extraSmall,
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
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        for (row in 0 until 3) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    val isHighlighted = index in GhostGridPreviewHighlighted
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
private fun LightsOutPreview() {
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        for (row in 0 until 3) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    val isOn = index in LightsOutPreviewOn
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(
                                if (isOn) LightsOutOnColor else LightsOutOffColor,
                                CircleShape,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SlidingPuzzlePreview() {
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        for (row in 0 until 3) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    val label = SlidingPuzzlePreviewLabels[index]
                    val isEmpty = label == 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                if (isEmpty) {
                                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.primaryContainer
                                },
                                MaterialTheme.shapes.extraSmall,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (!isEmpty) {
                            Text(
                                text = label.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = LightColorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PatternSequencePreview() {
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PatternSequencePreviewFigures.forEach { figure ->
                ShapeCanvas(
                    figure = figure,
                    modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp),
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(2.dp)
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
}

@Composable
private fun OrbitTrackerPreview() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp)
            .background(borderColor),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .background(MaterialTheme.colorScheme.surface),
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
}

@Composable
private fun ColorConfusionPreview() {
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
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
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
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
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp)
            .background(borderColor),
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(2.dp)) {
            for (row in 2 downTo 0) {
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    for (col in 0..2) {
                        val isLight = (row + col) % 2 == 0
                        val flatIndex = row * 3 + col
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (isLight) MiniChessLightSquare else MiniChessDarkSquare),
                            contentAlignment = Alignment.Center,
                        ) {
                            MiniChessPreviewPieces[flatIndex]?.let { piece ->
                                MiniChessPreviewPiece(
                                    drawable = piece.drawable,
                                    isWhite = piece.isWhite,
                                    modifier = Modifier.fillMaxSize().padding(2.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniChessPreviewPiece(
    drawable: DrawableResource,
    isWhite: Boolean,
    modifier: Modifier = Modifier,
) {
    val painter = painterResource(drawable)
    val fill = ColorFilter.tint(if (isWhite) ComposeColor.White else ComposeColor.Black)
    Canvas(modifier = modifier) {
        if (isWhite) {
            // Halo offset scales with canvas size so the outline stays proportional.
            val haloOffset = size.minDimension * 0.02f
            for ((dx, dy) in ChessHaloDeltas) {
                translate(left = dx * haloOffset, top = dy * haloOffset) {
                    with(painter) { draw(size = this@Canvas.size, colorFilter = ChessOutlineFilter) }
                }
            }
        }
        with(painter) { draw(size = size, colorFilter = fill) }
    }
}
