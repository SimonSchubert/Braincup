package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.WordleLetterState
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.tools.Animal
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Direction
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.games.wordle.WordlePreviewPuzzles
import com.inspiredandroid.braincup.ui.icons.CatFace
import com.inspiredandroid.braincup.ui.localizedName
import com.inspiredandroid.braincup.ui.screens.CatQueensBoardFrame
import com.inspiredandroid.braincup.ui.screens.FlashCrowdBlue
import com.inspiredandroid.braincup.ui.screens.FlashCrowdBlueBottom
import com.inspiredandroid.braincup.ui.screens.FlashCrowdBlueSide
import com.inspiredandroid.braincup.ui.screens.FlashCrowdYellow
import com.inspiredandroid.braincup.ui.screens.FlashCrowdYellowBottom
import com.inspiredandroid.braincup.ui.screens.FlashCrowdYellowSide
import com.inspiredandroid.braincup.ui.screens.KnotBoardFrame
import com.inspiredandroid.braincup.ui.screens.KnotCellColor
import com.inspiredandroid.braincup.ui.screens.NurikabeBoardFrame
import com.inspiredandroid.braincup.ui.screens.NurikabeIslandColor
import com.inspiredandroid.braincup.ui.screens.NurikabeSeaColor
import com.inspiredandroid.braincup.ui.screens.ShikakuBoardFrame
import com.inspiredandroid.braincup.ui.theme.CatRegionColors
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.LightsOutOffColor
import com.inspiredandroid.braincup.ui.theme.LightsOutOnColor
import com.inspiredandroid.braincup.ui.theme.MedalBronze
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.MedalSilver
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.WordleAbsent
import com.inspiredandroid.braincup.ui.theme.WordleCorrect
import com.inspiredandroid.braincup.ui.theme.WordlePresent
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.intl.Locale as ComposeLocale

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

// The last animal is the "new" one and is highlighted in the preview.
private val SpotTheNewPreviewAnimals: List<Animal> = listOf(
    Animal.CRAB,
    Animal.FISH,
    Animal.TURTLE,
    Animal.OCTOPUS,
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

private const val ShikakuPreviewSize = 3

/** A solved 3x3 Shikaku: a 2x2 (4), a 2x1 (2), and a 1x3 (3). clueRow/clueCol mark the number. */
private data class ShikakuPreviewRect(
    val top: Int,
    val left: Int,
    val bottom: Int,
    val right: Int,
    val clue: Int,
    val clueRow: Int,
    val clueCol: Int,
)

private val ShikakuPreviewRects: List<ShikakuPreviewRect> = listOf(
    ShikakuPreviewRect(top = 0, left = 0, bottom = 1, right = 1, clue = 4, clueRow = 0, clueCol = 0),
    ShikakuPreviewRect(top = 0, left = 2, bottom = 1, right = 2, clue = 2, clueRow = 0, clueCol = 2),
    ShikakuPreviewRect(top = 2, left = 0, bottom = 2, right = 2, clue = 3, clueRow = 2, clueCol = 0),
)

private const val NurikabePreviewSize = 4

/** Sea (wall) cells in the 4x4 preview grid. All other cells are island (white). */
private val NurikabePreviewSea: Set<Int> = setOf(1, 3, 5, 7, 9, 10, 11, 12, 13)

/** cellIndex -> island clue size. Clues sit in island cells. */
private val NurikabePreviewClues: Map<Int, Int> = mapOf(0 to 3, 2 to 2, 15 to 2)

private const val CatQueensPreviewSize = 4
private val CatQueensPreviewRegions: List<Int> = listOf(
    0, 0, 1, 1,
    0, 1, 1, 2,
    3, 3, 1, 2,
    3, 3, 2, 2,
)
private val CatQueensPreviewCats: Set<Int> = setOf(2, 4, 11, 13)

private const val KnotPreviewSize = 4
private data class KnotPreviewPath(val color: Int, val cells: List<Int>)

/** A solved 4x4 Knot: three colored paths that together cover every cell without crossing. */
private val KnotPreviewPaths: List<KnotPreviewPath> = listOf(
    KnotPreviewPath(color = 0, cells = listOf(0, 4, 8, 12, 13, 14, 15, 11)),
    KnotPreviewPath(color = 1, cells = listOf(1, 5, 9, 10)),
    KnotPreviewPath(color = 2, cells = listOf(2, 3, 7, 6)),
)

private data class MiniChessPreviewPlacement(val drawable: DrawableResource, val isWhite: Boolean)

private val MiniChessPreviewPieces: Map<Int, MiniChessPreviewPlacement> = mapOf(
    0 to MiniChessPreviewPlacement(Res.drawable.ic_chess_king, isWhite = true),
    4 to MiniChessPreviewPlacement(Res.drawable.ic_chess_pawn, isWhite = false),
    8 to MiniChessPreviewPlacement(Res.drawable.ic_chess_queen, isWhite = false),
)

@Composable
fun GameTile(
    gameType: GameType,
    highscore: Int,
    onPlay: () -> Unit,
    onViewScore: () -> Unit,
) {
    PrismTile(
        face = Primary,
        modifier = Modifier
            .aspectRatio(1f)
            .hoverHand(),
        onClick = onPlay,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    color = ComposeColor.White,
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
                    PrismTrophy(
                        tint = medalTint,
                        modifier = Modifier
                            .size(28.dp)
                            .hoverHand()
                            .noRippleClickable(onClick = onViewScore),
                    )
                }
            }
        }
    }
}

/**
 * A square tile matching [GameTile]'s look, but for the "normal" (full-size) game entries that are
 * not real [GameType]s and have no per-game highscore/medal. Caller supplies the label, accent color
 * and a [preview] drawn the same way as the mini-game previews.
 */
/** The full-size 9x9 Sudoku entry, shown as a square tile alongside the mini games. */
@Composable
fun NormalSudokuTile(completedCount: Int, onClick: () -> Unit) {
    NormalGameTile(
        label = stringResource(Res.string.normal_sudoku_button, completedCount, 50),
        accentColor = GameType.MINI_SUDOKU.accentColor,
        onClick = onClick,
    ) { NormalSudokuPreview() }
}

/** The full-size 8x8 Chess entry, shown as a square tile alongside the mini games. */
@Composable
fun NormalChessTile(onClick: () -> Unit) {
    NormalGameTile(
        label = stringResource(Res.string.normal_chess_button),
        accentColor = GameType.MINI_CHESS.accentColor,
        onClick = onClick,
    ) { NormalChessPreview() }
}

@Composable
private fun NormalGameTile(
    label: String,
    accentColor: Long,
    onClick: () -> Unit,
    preview: @Composable () -> Unit,
) {
    PrismTile(
        face = Primary,
        modifier = Modifier
            .aspectRatio(1f)
            .hoverHand(),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(ComposeColor(accentColor)),
                contentAlignment = Alignment.Center,
            ) {
                MaterialTheme(colorScheme = LightColorScheme) {
                    preview()
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
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = ComposeColor.White,
                    // Allow two lines so a longer label with a progress count (e.g. "Normal
                    // Sudoku (0/50)") stays readable at tile width instead of truncating.
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
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
        GameType.SHIKAKU -> ShikakuPreview()
        GameType.NURIKABE -> NurikabePreview()
        GameType.CAT_QUEENS -> CatQueensPreview()
        GameType.KNOT -> KnotPreview()
        GameType.SOLO_CHESS -> SoloChessPreview()
        GameType.FLAGS -> FlagsPreview()
        GameType.DIGIT_MEMORY -> DigitMemoryPreview()
        GameType.SPOT_THE_NEW -> SpotTheNewPreview()
        GameType.WORDLE -> WordlePreview()
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
                        ColorPrismCell(
                            face = figure.color.composeColor(),
                            facet = 1.5.dp,
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
    Box(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        ShapeCanvas(
            figure = Figure(Shape.HEART, Color.BLUE),
            modifier = Modifier.size(48.dp),
        )
    }
}

@Composable
private fun SpotTheNewPreview() {
    val newAnimal = SpotTheNewPreviewAnimals.last()
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SpotTheNewPreviewAnimals.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { animal ->
                    PrismCard(
                        face = if (animal == newAnimal) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        facet = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(animal.resource),
                                contentDescription = null,
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
private fun VisualMemoryPreview() {
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        VisualMemoryPreviewFigures.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { figure ->
                    PrismCard(
                        face = if (figure != null) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        },
                        facet = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
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
}

@Composable
private fun MentalCalculationPreview() {
    Text(
        text = "+15",
        style = MaterialTheme.typography.headlineSmall,
        fontFamily = numberFontFamily(),
        textAlign = TextAlign.Center,
        color = PreviewTextColor,
    )
}

@Composable
private fun DigitMemoryPreview() {
    Text(
        text = "4 9 2 8",
        style = MaterialTheme.typography.headlineSmall,
        fontFamily = numberFontFamily(),
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
            fontFamily = numberFontFamily(),
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            SherlockPreviewNumbers.forEach { num ->
                PrismCard(
                    face = MaterialTheme.colorScheme.secondaryContainer,
                    facet = 1.5.dp,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(2.dp),
                ) {
                    Text(
                        text = "$num",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = numberFontFamily(),
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
        Text(
            "\u00D7",
            style = MaterialTheme.typography.titleLarge,
            fontFamily = numberFontFamily(),
            color = PreviewTextColor,
        )
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
            fontFamily = numberFontFamily(),
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
            fontFamily = numberFontFamily(),
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
                                fontFamily = numberFontFamily(),
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

// A sparse set of givens for the full 9x9 sudoku preview (row to col -> digit).
private val NormalSudokuGivens: Map<Pair<Int, Int>, Int> = mapOf(
    (0 to 0) to 5, (0 to 3) to 3, (0 to 7) to 9,
    (1 to 1) to 8, (1 to 5) to 1,
    (2 to 4) to 6, (2 to 8) to 2,
    (3 to 2) to 7, (3 to 6) to 4,
    (4 to 0) to 9, (4 to 4) to 5, (4 to 8) to 1,
    (5 to 2) to 4, (5 to 6) to 8,
    (6 to 0) to 2, (6 to 4) to 7,
    (7 to 3) to 6, (7 to 7) to 3,
    (8 to 1) to 1, (8 to 5) to 9, (8 to 8) to 4,
)

// The full 9x9 board (with bold 3x3 box dividers) reads as "normal" sudoku, distinct from the
// 2x2 [MiniSudokuPreview].
@Composable
private fun NormalSudokuPreview() {
    val numberFont = numberFontFamily()
    val textMeasurer = rememberTextMeasurer()
    val cellColor = LightColorScheme.surface
    val thinLine = PreviewTextColor.copy(alpha = 0.2f)
    val boldLine = PreviewTextColor
    PrismCard(
        face = PreviewTextColor,
        facet = 4.dp,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize().background(cellColor)) {
            val n = 9
            val cell = size.width / n
            for (i in 0..n) {
                drawLine(thinLine, Offset(i * cell, 0f), Offset(i * cell, size.height), strokeWidth = 1.dp.toPx())
                drawLine(thinLine, Offset(0f, i * cell), Offset(size.width, i * cell), strokeWidth = 1.dp.toPx())
            }
            val bold = 2.dp.toPx()
            for (i in 0..n step 3) {
                drawLine(boldLine, Offset(i * cell, 0f), Offset(i * cell, size.height), strokeWidth = bold)
                drawLine(boldLine, Offset(0f, i * cell), Offset(size.width, i * cell), strokeWidth = bold)
            }
            val style = TextStyle(
                color = PreviewTextColor,
                fontSize = (cell * 0.62f).toSp(),
                fontFamily = numberFont,
                fontWeight = FontWeight.Bold,
            )
            NormalSudokuGivens.forEach { (pos, digit) ->
                val (row, col) = pos
                val measured = textMeasurer.measure(AnnotatedString(digit.toString()), style = style)
                val centerX = col * cell + cell / 2f
                val centerY = row * cell + cell / 2f
                drawText(
                    measured,
                    topLeft = Offset(centerX - measured.size.width / 2f, centerY - measured.size.height / 2f),
                )
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
                    PrismCard(
                        face = if (cell.tapped) {
                            MaterialTheme.colorScheme.surfaceVariant
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        facet = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                    ) {
                        Text(
                            text = cell.number.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = numberFontFamily(),
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
                    ColorPrismCell(
                        face = if (isHighlighted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
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
                    ColorPrismCell(
                        face = if (isOn) LightsOutOnColor else LightsOutOffColor,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ShikakuPreview() {
    val gridLineColor = PreviewTextColor.copy(alpha = 0.15f)
    val borderColor = PreviewTextColor
    val numberFont = numberFontFamily()
    val textMeasurer = rememberTextMeasurer()
    val n = ShikakuPreviewSize
    PrismCard(
        face = ShikakuBoardFrame,
        facet = 4.dp,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
        ) {
            val cellW = size.width / n
            val cellH = size.height / n

            // Each rectangle gets a distinct region color (like Cat Queens regions).
            ShikakuPreviewRects.forEachIndexed { idx, rect ->
                drawRect(
                    color = CatRegionColors[idx % CatRegionColors.size],
                    topLeft = Offset(rect.left * cellW, rect.top * cellH),
                    size = Size((rect.right - rect.left + 1) * cellW, (rect.bottom - rect.top + 1) * cellH),
                )
            }

            // Thin grid lines over the fills.
            for (i in 0..n) {
                drawLine(gridLineColor, Offset(i * cellW, 0f), Offset(i * cellW, size.height), strokeWidth = 1.dp.toPx())
                drawLine(gridLineColor, Offset(0f, i * cellH), Offset(size.width, i * cellH), strokeWidth = 1.dp.toPx())
            }

            // Bold dark border around each rectangle (like Cat Queens region borders).
            val bold = 3.dp.toPx()
            ShikakuPreviewRects.forEach { rect ->
                val x0 = rect.left * cellW
                val y0 = rect.top * cellH
                val x1 = (rect.right + 1) * cellW
                val y1 = (rect.bottom + 1) * cellH
                drawLine(borderColor, Offset(x0, y0), Offset(x1, y0), strokeWidth = bold)
                drawLine(borderColor, Offset(x0, y1), Offset(x1, y1), strokeWidth = bold)
                drawLine(borderColor, Offset(x0, y0), Offset(x0, y1), strokeWidth = bold)
                drawLine(borderColor, Offset(x1, y0), Offset(x1, y1), strokeWidth = bold)
            }

            val clueStyle = TextStyle(
                color = PreviewTextColor,
                fontSize = (cellH * 0.4f).toSp(),
                fontFamily = numberFont,
                fontWeight = FontWeight.Bold,
            )
            ShikakuPreviewRects.forEach { rect ->
                val measured = textMeasurer.measure(AnnotatedString(rect.clue.toString()), style = clueStyle)
                val centerX = rect.clueCol * cellW + cellW / 2f
                val centerY = rect.clueRow * cellH + cellH / 2f
                drawText(
                    measured,
                    topLeft = Offset(centerX - measured.size.width / 2f, centerY - measured.size.height / 2f),
                )
            }
        }
    }
}

@Composable
private fun NurikabePreview() {
    val gridLineColor = PreviewTextColor.copy(alpha = 0.5f)
    val numberFont = numberFontFamily()
    val textMeasurer = rememberTextMeasurer()
    val n = NurikabePreviewSize
    PrismCard(
        face = NurikabeBoardFrame,
        facet = 4.dp,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
        ) {
            val cellW = size.width / n
            val cellH = size.height / n
            fun topLeft(index: Int) = Offset((index % n) * cellW, (index / n) * cellH)
            val cellSize = Size(cellW, cellH)

            // Island cells are light; sea cells are dark — the classic Nurikabe look.
            drawRect(color = NurikabeIslandColor)
            NurikabePreviewSea.forEach { index ->
                drawRect(color = NurikabeSeaColor, topLeft = topLeft(index), size = cellSize)
            }

            // Dark grid lines, slightly thicker than the sea preview to match Shikaku / Cat Queens.
            for (i in 0..n) {
                drawLine(gridLineColor, Offset(i * cellW, 0f), Offset(i * cellW, size.height), strokeWidth = 1.5.dp.toPx())
                drawLine(gridLineColor, Offset(0f, i * cellH), Offset(size.width, i * cellH), strokeWidth = 1.5.dp.toPx())
            }

            val clueStyle = TextStyle(
                color = PreviewTextColor,
                fontSize = (cellH * 0.4f).toSp(),
                fontFamily = numberFont,
                fontWeight = FontWeight.Bold,
            )
            NurikabePreviewClues.forEach { (index, value) ->
                val measured = textMeasurer.measure(AnnotatedString(value.toString()), style = clueStyle)
                val centerX = (index % n) * cellW + cellW / 2f
                val centerY = (index / n) * cellH + cellH / 2f
                drawText(
                    measured,
                    topLeft = Offset(centerX - measured.size.width / 2f, centerY - measured.size.height / 2f),
                )
            }
        }
    }
}

@Composable
private fun CatQueensPreview() {
    val gridLineColor = PreviewTextColor.copy(alpha = 0.25f)
    val borderColor = PreviewTextColor
    val catPainter = rememberVectorPainter(CatFace)
    val n = CatQueensPreviewSize
    PrismCard(
        face = CatQueensBoardFrame,
        facet = 4.dp,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(20.dp),
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
        ) {
            val cellW = size.width / n
            val cellH = size.height / n
            val cellSize = Size(cellW, cellH)
            fun topLeft(index: Int) = Offset((index % n) * cellW, (index / n) * cellH)

            for (index in 0 until n * n) {
                val color = CatRegionColors[CatQueensPreviewRegions[index] % CatRegionColors.size]
                drawRect(color = color, topLeft = topLeft(index), size = cellSize)
            }

            for (i in 0..n) {
                drawLine(gridLineColor, Offset(i * cellW, 0f), Offset(i * cellW, size.height), strokeWidth = 1.dp.toPx())
                drawLine(gridLineColor, Offset(0f, i * cellH), Offset(size.width, i * cellH), strokeWidth = 1.dp.toPx())
            }

            val bold = 2.5f.dp.toPx()
            for (r in 0 until n) {
                for (c in 0 until n) {
                    val index = r * n + c
                    val region = CatQueensPreviewRegions[index]
                    val x0 = c * cellW
                    val y0 = r * cellH
                    val x1 = x0 + cellW
                    val y1 = y0 + cellH
                    if (r == 0 || CatQueensPreviewRegions[index - n] != region) {
                        drawLine(borderColor, Offset(x0, y0), Offset(x1, y0), strokeWidth = bold)
                    }
                    if (r == n - 1 || CatQueensPreviewRegions[index + n] != region) {
                        drawLine(borderColor, Offset(x0, y1), Offset(x1, y1), strokeWidth = bold)
                    }
                    if (c == 0 || CatQueensPreviewRegions[index - 1] != region) {
                        drawLine(borderColor, Offset(x0, y0), Offset(x0, y1), strokeWidth = bold)
                    }
                    if (c == n - 1 || CatQueensPreviewRegions[index + 1] != region) {
                        drawLine(borderColor, Offset(x1, y0), Offset(x1, y1), strokeWidth = bold)
                    }
                }
            }

            val pad = cellW * 0.14f
            val catSize = Size(cellW - 2 * pad, cellH - 2 * pad)
            CatQueensPreviewCats.forEach { index ->
                val tl = topLeft(index)
                translate(left = tl.x + pad, top = tl.y + pad) {
                    with(catPainter) { draw(catSize) }
                }
            }
        }
    }
}

@Composable
private fun KnotPreview() {
    val gridLineColor = PreviewTextColor.copy(alpha = 0.15f)
    val n = KnotPreviewSize
    PrismCard(
        face = KnotBoardFrame,
        facet = 4.dp,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / n
            val cellH = size.height / n
            fun center(cell: Int) = Offset((cell % n + 0.5f) * cellW, (cell / n + 0.5f) * cellH)

            drawRect(color = KnotCellColor)
            for (i in 0..n) {
                drawLine(gridLineColor, Offset(i * cellW, 0f), Offset(i * cellW, size.height), strokeWidth = 1.dp.toPx())
                drawLine(gridLineColor, Offset(0f, i * cellH), Offset(size.width, i * cellH), strokeWidth = 1.dp.toPx())
            }

            val stroke = minOf(cellW, cellH) * 0.34f
            val dotRadius = minOf(cellW, cellH) * 0.30f
            KnotPreviewPaths.forEach { path ->
                val color = CatRegionColors[path.color % CatRegionColors.size]
                for (i in 1 until path.cells.size) {
                    drawLine(color, center(path.cells[i - 1]), center(path.cells[i]), strokeWidth = stroke, cap = StrokeCap.Round)
                }
                drawCircle(color, radius = dotRadius, center = center(path.cells.first()))
                drawCircle(color, radius = dotRadius, center = center(path.cells.last()))
            }
        }
    }
}

@Composable
private fun WordlePreview() {
    val puzzle = remember(ComposeLocale.current.language) {
        WordlePreviewPuzzles.forTag(ComposeLocale.current.language)
            ?: WordlePreviewPuzzles.forTag("en")
    } ?: return
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        puzzle.guesses.forEach { guess ->
            val states = puzzle.statesFor(guess)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                guess.forEachIndexed { index, char ->
                    WordlePreviewCell(
                        char = char,
                        state = states[index],
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
        }
    }
}

@Composable
private fun WordlePreviewCell(
    char: Char,
    state: WordleLetterState,
    modifier: Modifier = Modifier,
) {
    val face = when (state) {
        WordleLetterState.CORRECT -> WordleCorrect
        WordleLetterState.PRESENT -> WordlePresent
        WordleLetterState.ABSENT -> WordleAbsent
        WordleLetterState.EMPTY, WordleLetterState.PENDING -> ComposeColor(0xFFD3D6DA)
    }
    val textColor = when (state) {
        WordleLetterState.CORRECT, WordleLetterState.PRESENT -> ComposeColor.White
        else -> ComposeColor(0xFF1A1A1B)
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        ColorPrismCell(
            face = face,
            modifier = Modifier.fillMaxSize(),
        )
        Text(
            text = char.toString(),
            color = textColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
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
                    PrismCard(
                        face = if (isEmpty) {
                            MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                        facet = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                    ) {
                        if (!isEmpty) {
                            Text(
                                text = label.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = numberFontFamily(),
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
            PrismCard(
                face = MaterialTheme.colorScheme.primaryContainer,
                facet = 2.dp,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(2.dp),
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
    val primarySide = remember(primaryColor) { primaryColor.darken(0.7f) }
    val primaryBottom = remember(primaryColor) { primaryColor.darken(0.5f) }
    val variantSide = remember(onSurfaceVariantColor) { onSurfaceVariantColor.darken(0.7f) }
    val variantBottom = remember(onSurfaceVariantColor) { onSurfaceVariantColor.darken(0.5f) }
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
                drawPrismCircle(
                    center = Offset(x * size.width, y * size.height),
                    radius = ballRadius,
                    face = if (isTarget) primaryColor else onSurfaceVariantColor,
                    side = if (isTarget) primarySide else variantSide,
                    bottom = if (isTarget) primaryBottom else variantBottom,
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
                    PrismCard(
                        face = MaterialTheme.colorScheme.surface,
                        facet = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                    ) {
                        Text(
                            text = wordColor.localizedName(),
                            style = MaterialTheme.typography.labelSmall,
                            color = fontColor.composeColor(),
                            textAlign = TextAlign.Center,
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
                drawPrismCircle(
                    center = Offset(x * size.width, y * size.height),
                    radius = r * size.width,
                    face = FlashCrowdBlue,
                    side = FlashCrowdBlueSide,
                    bottom = FlashCrowdBlueBottom,
                )
            }
        }
        Canvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
            FlashCrowdPreviewRightDots.forEach { (x, y, r) ->
                drawPrismCircle(
                    center = Offset(x * size.width, y * size.height),
                    radius = r * size.width,
                    face = FlashCrowdYellow,
                    side = FlashCrowdYellowSide,
                    bottom = FlashCrowdYellowBottom,
                )
            }
        }
    }
}

@Composable
private fun MiniChessPreview() {
    PrismCard(
        face = ChessBoardFrame,
        facet = 4.dp,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 2 downTo 0) {
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    for (col in 0..2) {
                        val isLight = (row + col) % 2 == 0
                        val flatIndex = row * 3 + col
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (isLight) ChessLightSquare else ChessDarkSquare),
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

private val ChessBackRank: List<DrawableResource> = listOf(
    Res.drawable.ic_chess_rook,
    Res.drawable.ic_chess_knight,
    Res.drawable.ic_chess_bishop,
    Res.drawable.ic_chess_queen,
    Res.drawable.ic_chess_king,
    Res.drawable.ic_chess_bishop,
    Res.drawable.ic_chess_knight,
    Res.drawable.ic_chess_rook,
)

// A full 8x8 board in the starting position reads as "normal" chess, distinct from the 3x3
// [MiniChessPreview]. Reuses [MiniChessPreviewPiece] for the haloed pieces.
@Composable
private fun NormalChessPreview() {
    PrismCard(
        face = ChessBoardFrame,
        facet = 4.dp,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0..7) {
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    for (col in 0..7) {
                        val isLight = (row + col) % 2 == 0
                        val placement: Pair<DrawableResource, Boolean>? = when (row) {
                            0 -> ChessBackRank[col] to false
                            1 -> Res.drawable.ic_chess_pawn to false
                            6 -> Res.drawable.ic_chess_pawn to true
                            7 -> ChessBackRank[col] to true
                            else -> null
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (isLight) ChessLightSquare else ChessDarkSquare),
                            contentAlignment = Alignment.Center,
                        ) {
                            placement?.let { (drawable, isWhite) ->
                                MiniChessPreviewPiece(
                                    drawable = drawable,
                                    isWhite = isWhite,
                                    modifier = Modifier.fillMaxSize().padding(1.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// A small 3x3 Solo Chess board: all one color (the hallmark of the puzzle), the king plus two pieces
// it will whittle down to itself.
private val SoloChessPreviewPieces: Map<Int, DrawableResource> = mapOf(
    0 to Res.drawable.ic_chess_queen,
    2 to Res.drawable.ic_chess_knight,
    4 to Res.drawable.ic_chess_king,
)

@Composable
private fun SoloChessPreview() {
    PrismCard(
        face = ChessBoardFrame,
        facet = 4.dp,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0..2) {
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    for (col in 0..2) {
                        val isLight = (row + col) % 2 == 0
                        val flatIndex = row * 3 + col
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (isLight) ChessLightSquare else ChessDarkSquare),
                            contentAlignment = Alignment.Center,
                        ) {
                            SoloChessPreviewPieces[flatIndex]?.let { drawable ->
                                MiniChessPreviewPiece(
                                    drawable = drawable,
                                    isWhite = true,
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

private val FlagsPreviewDrawables: List<DrawableResource> = listOf(
    Res.drawable.flag_japan_50,
    Res.drawable.flag_brazil_50,
    Res.drawable.flag_france_50,
)

@Composable
private fun FlagsPreview() {
    val borderColor = LightColorScheme.outlineVariant
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FlagsPreviewDrawables.forEach { drawable ->
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
            )
        }
    }
}
