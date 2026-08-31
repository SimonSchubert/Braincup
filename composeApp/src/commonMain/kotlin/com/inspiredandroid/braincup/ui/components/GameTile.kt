package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.WordleLetterState
import com.inspiredandroid.braincup.games.Cube
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.PrismTileType
import com.inspiredandroid.braincup.games.SimonSaysGame
import com.inspiredandroid.braincup.games.TrioFill
import com.inspiredandroid.braincup.games.TrioShape
import com.inspiredandroid.braincup.games.formattedScore
import com.inspiredandroid.braincup.games.mirror
import com.inspiredandroid.braincup.games.toProjection
import com.inspiredandroid.braincup.games.tools.Animal
import com.inspiredandroid.braincup.games.tools.Direction
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.games.wordle.WordlePreviewPuzzles
import com.inspiredandroid.braincup.ui.icons.CatFace
import com.inspiredandroid.braincup.ui.localizedName
import com.inspiredandroid.braincup.ui.screens.games.PuzzleClueCacheSize
import com.inspiredandroid.braincup.ui.screens.games.drawTextCentered
import com.inspiredandroid.braincup.ui.theme.BubbleSumBoardFrame
import com.inspiredandroid.braincup.ui.theme.CatQueensBoardFrame
import com.inspiredandroid.braincup.ui.theme.CatRegionColors
import com.inspiredandroid.braincup.ui.theme.FlashCrowdBlue
import com.inspiredandroid.braincup.ui.theme.FlashCrowdBlueBottom
import com.inspiredandroid.braincup.ui.theme.FlashCrowdBlueSide
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellow
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellowBottom
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellowSide
import com.inspiredandroid.braincup.ui.theme.HanoiBaseColor
import com.inspiredandroid.braincup.ui.theme.HanoiDiskColors
import com.inspiredandroid.braincup.ui.theme.HanoiPegColor
import com.inspiredandroid.braincup.ui.theme.KnotBoardFrame
import com.inspiredandroid.braincup.ui.theme.KnotCellColor
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.LightsOutOffColor
import com.inspiredandroid.braincup.ui.theme.LightsOutOnColor
import com.inspiredandroid.braincup.ui.theme.MatchstickColors
import com.inspiredandroid.braincup.ui.theme.NurikabeBoardFrame
import com.inspiredandroid.braincup.ui.theme.NurikabeIslandColor
import com.inspiredandroid.braincup.ui.theme.NurikabeSeaColor
import com.inspiredandroid.braincup.ui.theme.OrbitTrackerBoardFrame
import com.inspiredandroid.braincup.ui.theme.PegBoardFrame
import com.inspiredandroid.braincup.ui.theme.PegBoardSurface
import com.inspiredandroid.braincup.ui.theme.PegHole
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.PrismShade
import com.inspiredandroid.braincup.ui.theme.PrismSlot
import com.inspiredandroid.braincup.ui.theme.PuzzleGridInk
import com.inspiredandroid.braincup.ui.theme.ShikakuBoardFrame
import com.inspiredandroid.braincup.ui.theme.SpotTheNewColors
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.WordleAbsent
import com.inspiredandroid.braincup.ui.theme.WordlePresent
import com.inspiredandroid.braincup.ui.theme.medalTint
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import com.inspiredandroid.braincup.ui.theme.tileFace
import com.inspiredandroid.braincup.ui.theme.tileTextColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.exp
import androidx.compose.ui.text.intl.Locale as ComposeLocale

// Tile previews always render on light pastel backgrounds (gameType.accentColor),
// so set text colors explicitly rather than inheriting from the surrounding (possibly dark) theme.
private val PreviewTextColor = LightColorScheme.onSurface

private val AnomalyPuzzlePreviewFigures = listOf(
    Figure(Shape.STAR, GameColor.RED),
    Figure(Shape.STAR, GameColor.RED),
    Figure(Shape.STAR, GameColor.RED),
    Figure(Shape.STAR, GameColor.BLUE),
)

private val PathFinderPreviewDirections = listOf(Direction.RIGHT, Direction.DOWN, Direction.RIGHT)

private val PathFinderPreviewGrid: List<List<Figure>> = run {
    val startRow = 1
    val startCol = 1
    List(4) { row ->
        List(4) { col ->
            val isStart = row == startRow && col == startCol
            Figure(Shape.SQUARE, if (isStart) GameColor.ORANGE else GameColor.GREY_LIGHT)
        }
    }
}

/**
 * Mini scored guess for the menu tile: secret 1356, guess 1234 →
 * bull / miss / cow / miss, with matching count chips underneath.
 * Same green/amber/grey teaching colours as the instructions demo.
 */
private val BullsAndCowsPreviewTiles: List<Pair<Char, Color>> = listOf(
    '1' to SuccessGreen, // bull — right digit, right place
    '2' to WordleAbsent, // miss
    '3' to WordlePresent, // cow — right digit, wrong place
    '4' to WordleAbsent, // miss
)

/**
 * The measured extent of a preview's whole label set, ready to be scaled into any cell.
 *
 * Text measurement is the expensive half of the fit and depends only on the labels, the style and
 * the density — never on the cell. Doing it once per preview instead of once per cell is what
 * keeps a 5x5 grid like Schulte Table from paying 25 measurements per cell (see
 * [rememberPreviewTextFitter]); [fitTo] is then pure arithmetic.
 */
private class PreviewTextFitter(
    private val natural: TextStyle,
    private val baseFontSize: TextUnit,
    private val widestPx: Int,
    private val tallestPx: Int,
    private val density: Density,
) {
    /**
     * [natural] resized so that every measured label fits a [cellWidth] x [cellHeight] slot on a
     * single line, never growing past the original font size.
     */
    fun fitTo(cellWidth: Dp, cellHeight: Dp): TextStyle {
        val scale = with(density) {
            minOf(cellWidth.toPx() / widestPx, cellHeight.toPx() / tallestPx, 1f).coerceAtLeast(0f)
        }
        return natural.copy(fontSize = baseFontSize * scale)
    }
}

/**
 * Measures [texts] in [style] once, so each cell of a preview grid can scale that one result down
 * to its own box via [PreviewTextFitter.fitTo].
 *
 * Preview cells are derived from tile width, which the adaptive menu grid varies from its 150.dp
 * minimum (any 360.dp phone) up to ~300.dp, while the font sizes and line heights in
 * [MaterialTheme.typography] are absolute. On a narrow tile that leaves a 24.sp line box inside a
 * 14.dp cell, and TextOverflow.Clip shears the glyphs. Measuring the whole [texts] list at once
 * keeps every cell of a grid identical, and covers locales whose words run far longer than the
 * English ones.
 *
 * The fitted style also drops the absolute line height: without that the shrunken glyph would
 * still sit in the original over-tall line box and stay clipped.
 */
@Composable
private fun rememberPreviewTextFitter(texts: List<String>, style: TextStyle): PreviewTextFitter {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(texts, style, density, measurer) {
        val natural = style.copy(lineHeight = TextUnit.Unspecified)
        var widest = 1
        var tallest = 1
        texts.forEach { text ->
            val measured = measurer.measure(text, natural, softWrap = false).size
            widest = maxOf(widest, measured.width)
            tallest = maxOf(tallest, measured.height)
        }
        PreviewTextFitter(natural, style.fontSize, widest, tallest, density)
    }
}

@Composable
private fun BullsAndCowsPreview() {
    val digitLabels = remember { BullsAndCowsPreviewTiles.map { it.first.toString() } }
    val digitStyle = MaterialTheme.typography.titleMedium.copy(
        fontFamily = numberFontFamily(),
        fontWeight = FontWeight.Bold,
    )
    val digitFitter = rememberPreviewTextFitter(digitLabels, digitStyle)
    val chipStyle = MaterialTheme.typography.labelMedium.copy(
        fontFamily = numberFontFamily(),
        fontWeight = FontWeight.Bold,
    )
    val chipFitter = rememberPreviewTextFitter(BullsAndCowsChipLabels, chipStyle)
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            BullsAndCowsPreviewTiles.forEach { (digit, face) ->
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    ColorPrismCell(
                        face = face,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Text(
                        text = digit.toString(),
                        color = Color.White,
                        // Fit the flat prism face, not the whole cell, so the digit clears the bevel.
                        style = digitFitter.fitTo(
                            cellWidth = maxWidth - PrismFacet.Cell,
                            cellHeight = maxHeight - PrismFacet.Cell,
                        ),
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        ) {
            // fill = false keeps the chips snug around their labels while capping them at half the
            // row, so the pair can never spill past the preview on a narrow tile.
            BullsAndCowsPreviewChip(
                label = "1B",
                color = SuccessGreen,
                fitter = chipFitter,
                modifier = Modifier.weight(1f, fill = false),
            )
            BullsAndCowsPreviewChip(
                label = "1C",
                color = WordlePresent,
                fitter = chipFitter,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

private val BullsAndCowsChipLabels = listOf("1B", "1C")
private val BullsAndCowsChipPaddingH = 8.dp
private val BullsAndCowsChipPaddingV = 3.dp

@Composable
private fun BullsAndCowsPreviewChip(
    label: String,
    color: Color,
    fitter: PreviewTextFitter,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val labelStyle = fitter.fitTo(
            cellWidth = (maxWidth - BullsAndCowsChipPaddingH * 2).coerceAtLeast(0.dp),
            cellHeight = (maxHeight - BullsAndCowsChipPaddingV * 2).coerceAtLeast(0.dp),
        )
        Text(
            text = label,
            modifier = Modifier
                .clip(PrismSlot)
                .background(color.copy(alpha = 0.18f))
                .padding(
                    horizontal = BullsAndCowsChipPaddingH,
                    vertical = BullsAndCowsChipPaddingV,
                ),
            style = labelStyle,
            color = color,
            maxLines = 1,
            softWrap = false,
        )
    }
}

private val VisualMemoryPreviewFigures: List<Figure?> = listOf(
    Figure(Shape.TRIANGLE, GameColor.RED),
    null,
    Figure(Shape.CIRCLE, GameColor.GREEN),
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

// A 2x2 corner of the matrix: shape distributes across the rows, the missing cell is the
// one the player supplies. A full 3x3 is unreadable at tile size.
private val PatternSequencePreviewFigures = listOf(
    Figure(Shape.TRIANGLE, GameColor.RED),
    Figure(Shape.CIRCLE, GameColor.BLUE),
    Figure(Shape.CIRCLE, GameColor.BLUE),
)

private val OrbitTrackerPreviewBalls = listOf(
    Triple(0.3f, 0.25f, true),
    Triple(0.7f, 0.4f, false),
    Triple(0.5f, 0.7f, true),
    Triple(0.2f, 0.6f, false),
    Triple(0.8f, 0.75f, true),
)

/**
 * word -> ink. One matching pair and one mismatched pair is the whole rule (tap the words whose ink
 * matches their meaning), and two full-width rows leave each word four times the space a 2x2 grid
 * did — enough to stay readable on the grid's narrowest tile and in locales with long color names.
 */
private val ColorConfusionPreviewWords = listOf(
    GameColor.RED to GameColor.RED,
    GameColor.PURPLE to GameColor.YELLOW,
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

/** One move into a 4-disk Hanoi: the pyramid still on the left, the smallest disk parked right. */
private val TowerOfHanoiPreviewPegs: List<List<Int>> = listOf(
    listOf(4, 3, 2),
    emptyList(),
    listOf(1),
)

private const val TowerOfHanoiPreviewDisks = 4
private val PegGapPreview = 6.dp
private val PegPadHPreview = 3.dp
private val DiskGapPreview = 2.dp

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
    onPlay: (GameType) -> Unit,
    onViewScore: (GameType) -> Unit,
    modifier: Modifier = Modifier,
    /**
     * Wear the untimed section's taller tile, with the personal best under the name.
     *
     * A property of where the tile is being drawn rather than of the game: the same game is a
     * plain square in Continue, where it sits among mini games and has to line up with them.
     */
    untimedStyle: Boolean = false,
) {
    val medal: @Composable RowScope.() -> Unit = {
        val medalTint = gameType.medalTint(highscore)
        if (medalTint != null) {
            Spacer(Modifier.width(4.dp))
            PrismTrophy(
                tint = medalTint,
                modifier = Modifier
                    .size(28.dp)
                    .hoverHand()
                    .noRippleClickable(onClick = { onViewScore(gameType) }),
            )
        }
    }

    // Lines up with the entries beside it instead of ending the row half a tile short. It keeps
    // its medal either way.
    if (untimedStyle) {
        NormalGameTile(
            label = stringResource(gameType.displayNameRes),
            accentColor = gameType.accentColor,
            onClick = { onPlay(gameType) },
            modifier = modifier,
            caption = if (highscore > 0) {
                stringResource(Res.string.menu_best_score, gameType.formattedScore(highscore))
            } else {
                stringResource(Res.string.menu_not_played)
            },
            trailing = medal,
            preview = { GamePreview(gameType) },
        )
        return
    }

    GameTileShell(
        label = stringResource(gameType.displayNameRes),
        accentColor = gameType.accentColor,
        labelMaxLines = 1,
        modifier = modifier,
        onClick = { onPlay(gameType) },
        preview = { GamePreview(gameType) },
        trailing = medal,
    )
}

/** The full-size 9x9 Sudoku entry, with how much of the 50 puzzle book is done. */
@Composable
fun NormalSudokuTile(completedCount: Int, onClick: () -> Unit, total: Int = 50) {
    NormalGameTile(
        label = stringResource(Res.string.normal_sudoku_title),
        accentColor = GameType.MINI_SUDOKU.accentColor,
        onClick = onClick,
        caption = stringResource(Res.string.menu_progress_fraction, completedCount, total),
        progress = if (total > 0) completedCount.toFloat() / total else 0f,
    ) { NormalSudokuPreview() }
}

/** The full-size 8x8 Chess entry. Endless play, so it carries a description, not progress. */
@Composable
fun NormalChessTile(onClick: () -> Unit) {
    NormalGameTile(
        label = stringResource(Res.string.normal_chess_button),
        accentColor = GameType.MINI_CHESS.accentColor,
        onClick = onClick,
        caption = stringResource(Res.string.menu_chess_caption),
    ) { NormalChessPreview() }
}

/** The Matchstick Riddles entry, with how many of the riddles are solved. */
@Composable
fun MatchstickRiddlesTile(solvedCount: Int, total: Int, onClick: () -> Unit) {
    NormalGameTile(
        label = stringResource(Res.string.matchstick_riddles_title),
        accentColor = MatchstickColors.TileAccentArgb,
        onClick = onClick,
        caption = stringResource(Res.string.menu_progress_fraction, solvedCount, total),
        progress = if (total > 0) solvedCount.toFloat() / total else 0f,
    ) { MatchstickRiddlesPreview() }
}

/**
 * The standalone matrix-reasoning IQ test.
 *
 * Carries the personal best rather than a progress bar: it is one sitting that ends in a score,
 * not a collection to work through.
 */
@Composable
fun IqTestTile(bestIq: Int?, onClick: () -> Unit) {
    NormalGameTile(
        label = stringResource(Res.string.iq_test_button),
        accentColor = GameType.PATTERN_SEQUENCE.accentColor,
        onClick = onClick,
        caption = if (bestIq != null) {
            stringResource(Res.string.menu_best_score, bestIq.toString())
        } else {
            stringResource(Res.string.menu_iq_untaken)
        },
    ) { IqTestPreview() }
}

/** English peg solitaire. One board, so a description rather than progress. */
@Composable
fun PegSolitaireTile(onClick: () -> Unit) {
    NormalGameTile(
        label = stringResource(Res.string.peg_solitaire_button),
        accentColor = PegSolitaireTileAccentArgb,
        onClick = onClick,
        caption = stringResource(Res.string.menu_peg_caption),
    ) { PegSolitairePreview() }
}

private const val PegSolitaireTileAccentArgb = 0xFF8D6E63L

/**
 * A miniature of the result screen's bell curve rather than another matrix, so the tile reads as
 * "a measurement" next to the puzzle tiles rather than as a second Pattern Sequence.
 */
@Composable
private fun IqTestPreview() {
    val belowFace = MaterialTheme.colorScheme.primary
    val aboveFace = MaterialTheme.colorScheme.surfaceContainer
    Row(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        repeat(IqTestPreviewBars) { index ->
            // Column heights sample the normal curve at each bar's center, so the row reads as the
            // same distribution the result screen plots. The color break is the score marker: the
            // primary columns are the share of people the score sits above, which is what a
            // percentile means, so a separate marker line would only repeat the boundary.
            val z = ((index + 0.5f) / IqTestPreviewBars - 0.5f) * IqTestPreviewSpread
            ColorPrismCell(
                face = if (index < IqTestPreviewBarsBelow) belowFace else aboveFace,
                facet = PrismFacet.Dot,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(exp(-0.5f * z * z)),
            )
        }
    }
}

private const val IqTestPreviewBars = 9

/** Filled columns; doubles as the marker position, so it has to land on a bar boundary. */
private const val IqTestPreviewBarsBelow = 6

/** Half-width of the sampled z-range: wide enough to fall off, tight enough to keep edge bars visible. */
private const val IqTestPreviewSpread = 4.4f

/**
 * A tile for the untimed entries that are not real [GameType]s and have no per-game highscore
 * or medal: the IQ test, full-size Chess and Sudoku, Matchstick Riddles and Peg Solitaire.
 *
 * Unlike a mini game these have no clock, and what they do have is progress that accumulates over
 * many sittings. So the tile is not locked to a square: the preview keeps the
 * full square the mini games give theirs, and the label block below it grows to hold a bar and a
 * count. Cramming the count into the label instead ("Sudoku (12/50)") buried the one number that
 * matters and truncated it at tile width.
 */
@Composable
private fun NormalGameTile(
    label: String,
    accentColor: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    caption: String? = null,
    progress: Float? = null,
    trailing: @Composable RowScope.() -> Unit = {},
    preview: @Composable () -> Unit,
) {
    PrismTile(
        face = Primary,
        modifier = modifier.hoverHand(),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(accentColor)),
                contentAlignment = Alignment.Center,
            ) {
                MaterialTheme(colorScheme = LightColorScheme) {
                    preview()
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 6.dp, bottom = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        // Always two lines: the names in this section run from "Sudoku" to
                        // "Matchstick Riddles", and letting the block shrink to the short ones left
                        // the row visibly ragged, with the tall tile hanging below its neighbours.
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    trailing()
                }
                if (progress != null) {
                    // White on the tile's own face rather than the accent: the accent is a pale
                    // tile background, and at bar size on orange it read as an empty track.
                    PrismProgressBar(
                        progress = { progress },
                        trackColor = Color.Black.copy(alpha = 0.22f),
                        fillColor = Color.White,
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                    )
                }
                if (caption != null) {
                    Text(
                        text = caption,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/**
 * Square menu tile: accent-tinted preview above a label row. [trailing] holds anything that sits
 * after the label, such as the medal on a scored mini-game.
 */
@Composable
private fun GameTileShell(
    label: String,
    accentColor: Long,
    labelMaxLines: Int,
    onClick: () -> Unit,
    preview: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    PrismTile(
        face = Primary,
        modifier = modifier
            .aspectRatio(1f)
            .hoverHand(),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(accentColor)),
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
                    color = Color.White,
                    maxLines = labelMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                trailing()
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
        GameType.BUBBLE_SUM -> BubbleSumPreview()
        GameType.QUICK_SUM -> QuickSumPreview()
        GameType.SHERLOCK_CALCULATION -> SherlockCalculationPreview()
        GameType.CHAIN_CALCULATION -> ChainCalculationPreview()
        GameType.MISSING_OPERATORS -> MissingOperatorsPreview()
        GameType.FRACTION_CALCULATION -> FractionCalculationPreview()
        GameType.VALUE_COMPARISON -> ValueComparisonPreview()
        GameType.MINI_SUDOKU -> MiniSudokuPreview()
        GameType.SCHULTE_TABLE -> SchulteTablePreview()
        GameType.PATTERN_SEQUENCE -> PatternSequencePreview()
        GameType.GHOST_GRID -> GhostGridPreview()
        GameType.SIMON_SAYS -> SimonSaysPreview()
        GameType.COLOR_CONFUSION -> ColorConfusionPreview()
        GameType.ORBIT_TRACKER -> OrbitTrackerPreview()
        GameType.FLASH_CROWD -> FlashCrowdPreview()
        GameType.MINI_CHESS -> MiniChessPreview()
        GameType.LIGHTS_OUT -> LightsOutPreview()
        GameType.SLIDING_PUZZLE -> SlidingPuzzlePreview()
        GameType.TOWER_OF_HANOI -> TowerOfHanoiPreview()
        GameType.SHIKAKU -> ShikakuPreview()
        GameType.NURIKABE -> NurikabePreview()
        GameType.CAT_QUEENS -> CatQueensPreview()
        GameType.KNOT -> KnotPreview()
        GameType.SOLO_CHESS -> SoloChessPreview()
        GameType.PRISM_CLEAR -> PrismClearPreview()
        GameType.FLAGS -> FlagsPreview()
        GameType.DIGIT_MEMORY -> DigitMemoryPreview()
        GameType.N_BACK -> NBackPreview()
        GameType.SPOT_THE_NEW -> SpotTheNewPreview()
        GameType.WORDLE -> WordlePreview()
        GameType.BULLS_AND_COWS -> BullsAndCowsPreview()
        GameType.TRIO -> TrioPreview()
        GameType.MENTAL_ROTATIONS -> MentalRotationsPreview()
        GameType.MENTAL_FLEX -> MentalFlexPreview()
    }
}

// --- Preview Composables ---

private val MentalFlexPreviewTarget = Figure(Shape.STAR, GameColor.BLUE)

// One match on shape, one on color, one on neither: the choice the rule cue resolves.
private val MentalFlexPreviewCandidates = listOf(
    Figure(Shape.STAR, GameColor.RED),
    Figure(Shape.CIRCLE, GameColor.BLUE),
    Figure(Shape.HEART, GameColor.GREEN),
)

@Composable
private fun MentalFlexPreview() {
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ShapeCanvas(
            figure = MentalFlexPreviewTarget,
            modifier = Modifier.weight(1f).aspectRatio(1f),
        )
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            MentalFlexPreviewCandidates.forEach { figure ->
                ShapeCanvas(
                    figure = figure,
                    modifier = Modifier.weight(1f).aspectRatio(1f).padding(3.dp),
                )
            }
        }
    }
}

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
                            facet = PrismFacet.Dot,
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
            figure = Figure(Shape.HEART, GameColor.BLUE),
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
                            SpotTheNewColors.highlightFace()
                        } else {
                            SpotTheNewColors.normalFace()
                        },
                        facet = PrismFacet.Cell,
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
                        face = MaterialTheme.colorScheme.surfaceContainer,
                        facet = PrismFacet.Cell,
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
private fun MissingOperatorsPreview() {
    // Mini selected operator slot (same shape/colors as play), empty, so the tile reads
    // as "fill in the missing operator" without a plain "?".
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MathText(
            text = "12",
            style = MaterialTheme.typography.headlineSmall,
            color = PreviewTextColor,
        )
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(
                    // Selected empty slot — primary tint reads clearly on pastel tile faces.
                    color = LightColorScheme.primaryContainer.copy(alpha = 0.45f),
                    shape = PrismSlot,
                )
                .border(
                    border = BorderStroke(2.dp, LightColorScheme.primary),
                    shape = PrismSlot,
                ),
        ) {}
        MathText(
            text = "4 = 3",
            style = MaterialTheme.typography.headlineSmall,
            color = PreviewTextColor,
        )
    }
}

@Composable
private fun QuickSumPreview() {
    // Mirrors the arena: one term lit, the rest of the sequence still to come.
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "7",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = numberFontFamily(),
            textAlign = TextAlign.Center,
            color = PreviewTextColor,
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(PreviewTextColor.copy(alpha = if (i <= 1) 1f else 0.25f)),
                )
            }
        }
    }
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
private fun NBackPreview() {
    // Mirrors the arena: one shape lit, the rest of the stream still to come.
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ShapeCanvas(
            figure = Figure(Shape.STAR, GameColor.BLUE),
            modifier = Modifier.size(44.dp),
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(PreviewTextColor.copy(alpha = if (i <= 1) 1f else 0.25f)),
                )
            }
        }
    }
}

@Composable
private fun SherlockCalculationPreview() {
    Column(
        // Five cells in one row is the densest layout in the tile set, so it gets the tighter
        // inset the other cramped previews already use, and a 1.dp gutter instead of 2.dp.
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(20.dp),
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
        val numberLabels = remember { SherlockPreviewNumbers.map { it.toString() } }
        val numberStyle = MaterialTheme.typography.labelSmall.copy(fontFamily = numberFontFamily())
        val fitter = rememberPreviewTextFitter(numberLabels, numberStyle)
        Row(modifier = Modifier.fillMaxWidth()) {
            SherlockPreviewNumbers.forEach { num ->
                PrismCard(
                    face = MaterialTheme.colorScheme.surfaceContainer,
                    facet = PrismFacet.Dot,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(1.dp),
                ) {
                    BoxWithConstraints(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$num",
                            style = fitter.fitTo(maxWidth, maxHeight),
                            color = LightColorScheme.onSecondaryContainer,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
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

private val ValueComparisonPreviewTerms = listOf("3 + 8", "5 + 4")

@Composable
private fun ValueComparisonPreview() {
    val vsLabel = stringResource(Res.string.preview_vs)
    val termStyle = MaterialTheme.typography.titleMedium.copy(fontFamily = numberFontFamily())
    val termFitter = rememberPreviewTextFitter(ValueComparisonPreviewTerms, termStyle)
    val vsLabels = remember(vsLabel) { listOf(vsLabel) }
    val vsFitter = rememberPreviewTextFitter(vsLabels, MaterialTheme.typography.labelSmall)
    // Unlike the grid previews nothing here is clipped per cell: it is the stack of three lines
    // that outgrows the square, so give each line a third of the height and fit it to that.
    BoxWithConstraints(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        val lineHeight = maxHeight / 3
        val fittedTerm = termFitter.fitTo(maxWidth, lineHeight)
        val fittedVs = vsFitter.fitTo(maxWidth, lineHeight)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = ValueComparisonPreviewTerms[0],
                style = fittedTerm,
                color = PreviewTextColor,
                maxLines = 1,
                softWrap = false,
            )
            Text(
                text = vsLabel,
                style = fittedVs,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
            )
            Text(
                text = ValueComparisonPreviewTerms[1],
                style = fittedTerm,
                color = PreviewTextColor,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@Composable
private fun MiniSudokuPreview() {
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    PrismCard(
        face = PreviewTextColor,
        facet = PrismFacet.Preview,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        // The prism face is the outer border now; the gaps between cells stay as grid lines.
        Column(
            modifier = Modifier.fillMaxSize().background(gridLineColor),
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
                                .background(MaterialTheme.colorScheme.surfaceContainer),
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
    val textMeasurer = rememberTextMeasurer(cacheSize = PuzzleClueCacheSize)
    val cellColor = LightColorScheme.surface
    val thinLine = PreviewTextColor.copy(alpha = 0.2f)
    val boldLine = PreviewTextColor
    PrismCard(
        face = PreviewTextColor,
        facet = PrismFacet.Preview,
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
            // Twenty-one givens over nine distinct digits: measure each digit once.
            val digitLayouts = NormalSudokuGivens.values.distinct().associateWith { digit ->
                textMeasurer.measure(AnnotatedString(digit.toString()), style = style)
            }
            NormalSudokuGivens.forEach { (pos, digit) ->
                val (row, col) = pos
                val centerX = col * cell + cell / 2f
                val centerY = row * cell + cell / 2f
                drawTextCentered(digitLayouts.getValue(digit), centerX, centerY)
            }
        }
    }
}

@Composable
private fun MatchstickRiddlesPreview() {
    val body = MatchstickColors.WoodBody
    val head = MatchstickColors.WoodHead
    PrismCard(
        face = PreviewTextColor,
        facet = PrismFacet.Preview,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize().background(LightColorScheme.surface)) {
            val w = size.width
            val h = size.height
            val stroke = w * 0.07f
            val headR = w * 0.05f
            fun stick(ax: Float, ay: Float, bx: Float, by: Float) {
                val a = Offset(ax * w, ay * h)
                drawLine(body, a, Offset(bx * w, by * h), strokeWidth = stroke, cap = StrokeCap.Round)
                drawCircle(head, radius = headR, center = a)
            }
            // A small matchstick "+" and "=" so the tile reads as a matchstick equation.
            stick(0.16f, 0.50f, 0.42f, 0.50f)
            stick(0.29f, 0.35f, 0.29f, 0.65f)
            stick(0.58f, 0.40f, 0.84f, 0.40f)
            stick(0.58f, 0.60f, 0.84f, 0.60f)
        }
    }
}

@Composable
private fun PegSolitairePreview() {
    val frame = PegBoardFrame
    val surface = PegBoardSurface
    val hole = PegHole
    PrismCard(
        face = frame,
        facet = PrismFacet.Preview,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(surface)
                .padding(6.dp),
        ) {
            for (row in 0 until 7) {
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    for (col in 0 until 7) {
                        // English cross
                        if (!(row in 2..4 || col in 2..4)) {
                            Spacer(Modifier.weight(1f).fillMaxHeight())
                            continue
                        }
                        val isEmpty = row == 3 && col == 3
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            ColorPrismCell(
                                face = hole.darken(PrismShade.Side),
                                facet = PrismFacet.Dot,
                                modifier = Modifier.fillMaxSize(0.72f),
                            )
                            if (!isEmpty) {
                                ColorPrismCell(
                                    face = Primary,
                                    facet = PrismFacet.Dot,
                                    modifier = Modifier.fillMaxSize(0.62f),
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
private fun SchulteTablePreview() {
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val numberLabels = remember {
            SchulteTablePreviewGrid.flatten().map { it.number.toString() }
        }
        val numberStyle = MaterialTheme.typography.labelSmall.copy(
            fontFamily = numberFontFamily(),
            fontWeight = FontWeight.Bold,
        )
        val fitter = rememberPreviewTextFitter(numberLabels, numberStyle)
        SchulteTablePreviewGrid.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { cell ->
                    PrismCard(
                        face = if (cell.tapped) {
                            MaterialTheme.colorScheme.surfaceVariant
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer
                        },
                        facet = PrismFacet.Cell,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                    ) {
                        BoxWithConstraints(contentAlignment = Alignment.Center) {
                            Text(
                                text = cell.number.toString(),
                                style = fitter.fitTo(maxWidth, maxHeight),
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = if (cell.tapped) 0.4f else 1f,
                                ),
                                maxLines = 1,
                                softWrap = false,
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
                            MaterialTheme.colorScheme.surfaceContainer
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
private fun SimonSaysPreview() {
    // All four pads at full colour rather than one lit and three dark. A tile is an identity mark,
    // not a snapshot of play: the unlit face is near-black by design, and three near-black wedges
    // go muddy against the pale mint MEMORY accent. Four bright quadrants read as the Simon disc
    // at a glance, which is what the tile has to do.
    SimonDisc(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        // The hub reads as a hole punched through to the tile's accent background.
        hubColor = Color(GameType.SIMON_SAYS.accentColor),
    ) { index, quadrant, padModifier ->
        Box(
            modifier = padModifier
                .clip(simonQuadrantShape(quadrant))
                .background(SimonSaysGame.PADS[index].composeColor()),
        )
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
    val textMeasurer = rememberTextMeasurer(cacheSize = PuzzleClueCacheSize)
    val n = ShikakuPreviewSize
    PrismCard(
        face = ShikakuBoardFrame,
        facet = PrismFacet.Preview,
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
            val clueLayouts = ShikakuPreviewRects.map { it.clue }.distinct().associateWith { clue ->
                textMeasurer.measure(AnnotatedString(clue.toString()), style = clueStyle)
            }
            ShikakuPreviewRects.forEach { rect ->
                val centerX = rect.clueCol * cellW + cellW / 2f
                val centerY = rect.clueRow * cellH + cellH / 2f
                drawTextCentered(clueLayouts.getValue(rect.clue), centerX, centerY)
            }
        }
    }
}

@Composable
private fun NurikabePreview() {
    val gridLineColor = PreviewTextColor.copy(alpha = 0.5f)
    val numberFont = numberFontFamily()
    val textMeasurer = rememberTextMeasurer(cacheSize = PuzzleClueCacheSize)
    val n = NurikabePreviewSize
    PrismCard(
        face = NurikabeBoardFrame,
        facet = PrismFacet.Preview,
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
            val clueLayouts = NurikabePreviewClues.values.distinct().associateWith { value ->
                textMeasurer.measure(AnnotatedString(value.toString()), style = clueStyle)
            }
            NurikabePreviewClues.forEach { (index, value) ->
                val centerX = (index % n) * cellW + cellW / 2f
                val centerY = (index / n) * cellH + cellH / 2f
                drawTextCentered(clueLayouts.getValue(value), centerX, centerY)
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
        facet = PrismFacet.Preview,
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
        facet = PrismFacet.Preview,
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
        // Every letter of the puzzle drives one shared size: Bungee is proportional, so fitting each
        // cell on its own would render "I" larger than "W" in the same row.
        val letters = remember(puzzle) { puzzle.guesses.flatMap { it.map(Char::toString) } }
        val letterStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        val fitter = rememberPreviewTextFitter(letters, letterStyle)
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
                        fitter = fitter,
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
    fitter: PreviewTextFitter,
    modifier: Modifier = Modifier,
) {
    val face = state.tileFace()
    val textColor = state.tileTextColor()
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        ColorPrismCell(
            face = face,
            modifier = Modifier.fillMaxSize(),
        )
        Text(
            text = char.toString(),
            color = textColor,
            style = fitter.fitTo(
                cellWidth = maxWidth - PrismFacet.Cell,
                cellHeight = maxHeight - PrismFacet.Cell,
            ),
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
private fun SlidingPuzzlePreview() {
    val numberLabels = remember {
        SlidingPuzzlePreviewLabels.filter { it != 0 }.map { it.toString() }
    }
    val numberStyle = MaterialTheme.typography.labelSmall.copy(
        fontFamily = numberFontFamily(),
        fontWeight = FontWeight.Bold,
    )
    val fitter = rememberPreviewTextFitter(numberLabels, numberStyle)
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
                        facet = PrismFacet.Cell,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                    ) {
                        if (!isEmpty) {
                            BoxWithConstraints(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label.toString(),
                                    style = fitter.fitTo(maxWidth, maxHeight),
                                    color = LightColorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    softWrap = false,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Miniature of the in-game board: each peg sits on its own translucent chrome card with a
 * square pole and a full-width base, and disks are prism bars sized so the stack fills the pole
 * the way it does while playing.
 */
@Composable
private fun TowerOfHanoiPreview() {
    val pegFace = PreviewTextColor.copy(alpha = 0.07f)
    BoxWithConstraints(
        // Unlike the grid previews this one is not square: the board wants every bit of tile width
        // it can get, so the three pegs stay wide enough for readable disks.
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        val baseHeight = 7.dp
        val pegPadV = 5.dp
        // Disks are sized off the peg width so they stay wide pills like in game, and the board is
        // only as tall as the stack needs: a full-height column would leave bare, spindly poles.
        val pegInnerWidth = (maxWidth - PegGapPreview * 2) / TowerOfHanoiPreviewPegs.size - PegPadHPreview * 2
        val diskHeight = pegInnerWidth * 0.3f
        // The pole holds the whole stack plus one spare slot, matching the in-game proportion.
        val boardHeight = (diskHeight + DiskGapPreview) * (TowerOfHanoiPreviewDisks + 1) +
            baseHeight + pegPadV * 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(boardHeight.coerceAtMost(maxHeight)),
            horizontalArrangement = Arrangement.spacedBy(PegGapPreview),
            verticalAlignment = Alignment.Bottom,
        ) {
            TowerOfHanoiPreviewPegs.forEach { disks ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(PrismSlot)
                        .background(pegFace)
                        .padding(horizontal = PegPadHPreview, vertical = pegPadV),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight(0.92f)
                                .align(Alignment.BottomCenter)
                                .background(HanoiPegColor),
                        )
                        Column(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(DiskGapPreview),
                        ) {
                            disks.asReversed().forEach { size ->
                                TowerOfHanoiPreviewDisk(size = size, height = diskHeight)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(baseHeight)
                            .background(HanoiBaseColor),
                    )
                }
            }
        }
    }
}

@Composable
private fun TowerOfHanoiPreviewDisk(size: Int, height: Dp) {
    val fraction = (size - 1).toFloat() / (TowerOfHanoiPreviewDisks - 1).toFloat()
    ColorPrismCell(
        face = HanoiDiskColors[(size - 1) % HanoiDiskColors.size],
        facet = PrismFacet.Dot,
        modifier = Modifier
            // The smallest disk still needs to read as a bar rather than a dot, so the width
            // range starts at half the peg instead of scaling all the way down.
            .fillMaxWidth(0.5f + 0.5f * fraction)
            .height(height),
    )
}

@Composable
private fun PatternSequencePreview() {
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(2) { row ->
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                repeat(2) { column ->
                    val index = row * 2 + column
                    if (index == PatternSequencePreviewFigures.size) {
                        PrismCard(
                            face = MaterialTheme.colorScheme.surfaceContainer,
                            facet = PrismFacet.Cell,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        ) {
                            Text(
                                text = "?",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    } else {
                        ShapeCanvas(
                            figure = PatternSequencePreviewFigures[index],
                            modifier = Modifier.weight(1f).fillMaxHeight().padding(3.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrbitTrackerPreview() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val primarySide = remember(primaryColor) { primaryColor.darken(0.7f) }
    val primaryBottom = remember(primaryColor) { primaryColor.darken(0.5f) }
    val variantSide = remember(onSurfaceVariantColor) { onSurfaceVariantColor.darken(0.7f) }
    val variantBottom = remember(onSurfaceVariantColor) { onSurfaceVariantColor.darken(0.5f) }
    PrismCard(
        face = OrbitTrackerBoardFrame,
        facet = PrismFacet.Preview,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer),
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

private val BubbleSumPreviewBubbles = listOf(
    Triple(0.30f, 0.32f, 3),
    Triple(0.70f, 0.30f, 7),
    Triple(0.50f, 0.68f, 2),
)

/** Bubble shown mid-warning, so the tile carries the mechanic the game is built around. */
private const val BubbleSumPreviewWarningBubble = 0

@Composable
private fun BubbleSumPreview() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val primarySide = remember(primaryColor) { primaryColor.darken(0.7f) }
    val primaryBottom = remember(primaryColor) { primaryColor.darken(0.5f) }
    val textMeasurer = rememberTextMeasurer(cacheSize = PuzzleClueCacheSize)
    val digitStyle = MaterialTheme.typography.labelSmall.copy(
        color = androidx.compose.ui.graphics.Color.White,
        fontWeight = FontWeight.Bold,
    )
    val warningDigitStyle = digitStyle.copy(color = PuzzleGridInk)
    PrismCard(
        face = BubbleSumBoardFrame,
        facet = PrismFacet.Preview,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(24.dp),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            val ballRadius = size.width * 0.12f
            BubbleSumPreviewBubbles.forEachIndexed { index, (x, y, value) ->
                val warning = index == BubbleSumPreviewWarningBubble
                val center = Offset(x * size.width, y * size.height)
                if (warning) {
                    // Let drawPrismCircle shade the yellow itself, the way the arena draws it.
                    drawPrismCircle(
                        center = center,
                        radius = ballRadius,
                        face = FlashCrowdYellow,
                    )
                } else {
                    drawPrismCircle(
                        center = center,
                        radius = ballRadius,
                        face = primaryColor,
                        side = primarySide,
                        bottom = primaryBottom,
                    )
                }
                val measured = textMeasurer.measure(
                    text = AnnotatedString(value.toString()),
                    style = if (warning) warningDigitStyle else digitStyle,
                )
                drawText(
                    textLayoutResult = measured,
                    topLeft = Offset(
                        center.x - measured.size.width / 2f,
                        center.y - measured.size.height / 2f,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ColorConfusionPreview() {
    val labels = ColorConfusionPreviewWords.map { it.first.localizedName() }
    // Both words share one size (the longest one drives it) so the two cards match.
    val fitter = rememberPreviewTextFitter(labels, MaterialTheme.typography.labelSmall)
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ColorConfusionPreviewWords.forEach { (wordColor, fontColor) ->
            PrismCard(
                face = MaterialTheme.colorScheme.surface,
                facet = PrismFacet.Cell,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(2.dp),
            ) {
                BoxWithConstraints(contentAlignment = Alignment.Center) {
                    val style = fitter.fitTo(
                        cellWidth = (maxWidth - 4.dp).coerceAtLeast(0.dp),
                        cellHeight = maxHeight,
                    )
                    Text(
                        text = wordColor.localizedName(),
                        style = style,
                        color = fontColor.composeColor(),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false,
                    )
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
        facet = PrismFacet.Preview,
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
    val fill = ColorFilter.tint(if (isWhite) Color.White else Color.Black)
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
        facet = PrismFacet.Preview,
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
        facet = PrismFacet.Preview,
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

@Composable
private fun PrismClearPreview() {
    val pattern = listOf(
        0, 2, 2, -1, -1, -1,
        2, 0, 1, 0, 1, 1,
    )
    Column(
        modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        for (row in 0 until 2) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 6) {
                    val ordinal = pattern[row * 6 + col]
                    val face = if (ordinal < 0) {
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    } else {
                        PrismTileType.entries[ordinal].color.composeColor()
                    }
                    ColorPrismCell(
                        face = face,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(1.dp),
                    )
                }
            }
        }
    }
}

private val FlagsPreviewDrawables: List<DrawableResource> = listOf(
    Res.drawable.flag_japan,
    Res.drawable.flag_brazil,
    Res.drawable.flag_france,
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

private val TrioPreviewGlyphs = listOf(1, 2, 3)

@Composable
private fun TrioPreview() {
    val ink = GameColor.RED.composeColor()
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TrioPreviewGlyphs.forEach { count ->
            TrioCardGlyphs(
                shape = TrioShape.CIRCLE,
                count = count,
                fill = TrioFill.SOLID,
                color = ink,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
            )
        }
    }
}

/** A chiral 3-arm staircase and its mirror, the shape of one round in miniature. */
private val MentalRotationsPreviewFigure = listOf(
    Cube(0, 0, 0),
    Cube(1, 0, 0),
    Cube(2, 0, 0),
    Cube(2, 1, 0),
    Cube(2, 2, 0),
    Cube(2, 2, 1),
)

@Composable
private fun MentalRotationsPreview() {
    MentalRotationsPair(
        reference = MentalRotationsPreviewFigure.toProjection(),
        candidate = mirror(MentalRotationsPreviewFigure).toProjection(),
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(8.dp),
        spacing = 6.dp,
    )
}
