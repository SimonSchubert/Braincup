package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.compositeOver
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
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.app.RailYardUiState
import com.inspiredandroid.braincup.app.WordleLetterState
import com.inspiredandroid.braincup.checkers.CheckersPiece
import com.inspiredandroid.braincup.checkers.CheckersSide
import com.inspiredandroid.braincup.games.ColorConfusionGame
import com.inspiredandroid.braincup.games.Cube
import com.inspiredandroid.braincup.games.FlockGame
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.PrismTileType
import com.inspiredandroid.braincup.games.RailYardGame
import com.inspiredandroid.braincup.games.RuleShiftGame
import com.inspiredandroid.braincup.games.SimonSaysGame
import com.inspiredandroid.braincup.games.TrioCard
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
import com.inspiredandroid.braincup.ui.screens.games.drawPuzzleGridLines
import com.inspiredandroid.braincup.ui.screens.games.drawRegionBorders
import com.inspiredandroid.braincup.ui.screens.games.drawTextCentered
import com.inspiredandroid.braincup.ui.theme.CatRegionColors
import com.inspiredandroid.braincup.ui.theme.CheckersDarkSquare
import com.inspiredandroid.braincup.ui.theme.CheckersLightSquare
import com.inspiredandroid.braincup.ui.theme.FlashCrowdBlue
import com.inspiredandroid.braincup.ui.theme.FlashCrowdBlueBottom
import com.inspiredandroid.braincup.ui.theme.FlashCrowdBlueSide
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellow
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellowBottom
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellowSide
import com.inspiredandroid.braincup.ui.theme.HanoiBaseColor
import com.inspiredandroid.braincup.ui.theme.HanoiDiskColors
import com.inspiredandroid.braincup.ui.theme.HanoiPegColor
import com.inspiredandroid.braincup.ui.theme.KnotCellColor
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.LightsOutOffColor
import com.inspiredandroid.braincup.ui.theme.LightsOutOnColor
import com.inspiredandroid.braincup.ui.theme.MatchstickColors
import com.inspiredandroid.braincup.ui.theme.NurikabeIslandColor
import com.inspiredandroid.braincup.ui.theme.NurikabeSeaColor
import com.inspiredandroid.braincup.ui.theme.PegBoardSurface
import com.inspiredandroid.braincup.ui.theme.PegHole
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismChamferShape
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.PrismShade
import com.inspiredandroid.braincup.ui.theme.PrismSlot
import com.inspiredandroid.braincup.ui.theme.PuzzleGridInk
import com.inspiredandroid.braincup.ui.theme.RailYardGround
import com.inspiredandroid.braincup.ui.theme.ReversiBlackDisc
import com.inspiredandroid.braincup.ui.theme.ReversiFelt
import com.inspiredandroid.braincup.ui.theme.ReversiGridLine
import com.inspiredandroid.braincup.ui.theme.ReversiWhiteDisc
import com.inspiredandroid.braincup.ui.theme.SpotTheNewColors
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.UntimedSectionAccent
import com.inspiredandroid.braincup.ui.theme.WordleAbsent
import com.inspiredandroid.braincup.ui.theme.WordlePresent
import com.inspiredandroid.braincup.ui.theme.medalTint
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import com.inspiredandroid.braincup.ui.theme.tileFace
import com.inspiredandroid.braincup.ui.theme.tileTextColor
import kotlinx.collections.immutable.persistentListOf
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

private val PreviewInset = 20.dp

/** The square every preview stands in, so tiles share one footprint whatever they draw. */
private fun Modifier.previewSquare(inset: Dp = PreviewInset): Modifier = fillMaxHeight().aspectRatio(1f).padding(inset)

/**
 * For a row of four or more: a square leaves each cell under 20dp on a phone, so these take the
 * tile's full width the way [NBackPreview] does.
 */
private fun Modifier.previewWide(): Modifier = fillMaxSize().padding(horizontal = 16.dp, vertical = PreviewInset)

private val PreviewCardFace = LightColorScheme.surfaceContainer
private val PreviewCardInk = LightColorScheme.onSecondaryContainer
private val PreviewKeyGap = 3.dp

/**
 * A raised key or card with its label fitted to the flat face, the way the number pad and the
 * number cards look in play. The label is centred on the face, not the whole cell, so it clears
 * the bevel.
 */
@Composable
private fun PreviewKey(
    label: String,
    fitter: PreviewTextFitter,
    modifier: Modifier = Modifier,
    face: Color = Primary,
    ink: Color = Color.White,
) {
    BoxWithConstraints(modifier = modifier) {
        val labelStyle = fitter.fitTo(
            cellWidth = (maxWidth - PrismFacet.Cell) * PreviewLabelFill,
            cellHeight = (maxHeight - PrismFacet.Cell) * PreviewLabelFill,
        )
        ColorPrismCell(face = face, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = PrismFacet.Cell, bottom = PrismFacet.Cell),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = ink,
                style = labelStyle,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

/**
 * Share of a face a fitted label may take. A fraction rather than a fixed margin, so a label on a
 * 300dp tablet tile keeps the same breathing room it has on a 150dp phone tile.
 */
private const val PreviewLabelFill = 0.72f

// The board-sized chamfer of PrismSlot swallows a slot this small.
private val PreviewSlotShape = PrismChamferShape(PrismFacet.Cell)

/** The empty answer slot of the number games, sunk into the tile rather than raised from it. */
@Composable
private fun PreviewSlot(modifier: Modifier = Modifier, selected: Boolean = false) {
    Box(
        modifier = modifier
            .padding(end = PrismFacet.Cell, bottom = PrismFacet.Cell)
            .background(LightColorScheme.surface, PreviewSlotShape)
            .border(
                width = 1.5.dp,
                color = if (selected) Primary else LightColorScheme.outlineVariant,
                shape = PreviewSlotShape,
            ),
    )
}

/**
 * The board every framed preview sits on. One light face and one grey shading for all of them, so
 * no tile's board reads darker or deeper than its neighbour's whatever the game's own frame is.
 */
@Composable
private fun PreviewBoard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    PrismCard(
        face = PreviewCardFace,
        side = PreviewBoardSide,
        bottom = PreviewBoardBottom,
        facet = PrismFacet.Preview,
        modifier = modifier,
        content = content,
    )
}

private val PreviewBoardSide = PreviewCardFace.darken(PrismShade.Side)
private val PreviewBoardBottom = PreviewCardFace.darken(PrismShade.Bottom)

/** A shape on a raised card, the way the shape games deal their figures. Null is an empty card. */
@Composable
private fun PreviewShapeCard(figure: Figure?, modifier: Modifier = Modifier) {
    PrismCard(face = PreviewCardFace, facet = PrismFacet.Cell, modifier = modifier) {
        if (figure != null) {
            ShapeCanvas(figure = figure, modifier = Modifier.fillMaxSize().padding(4.dp))
        }
    }
}

/**
 * Bold number-font labels. Display size because [PreviewTextFitter] only ever scales down: with a
 * smaller base the labels stop growing while their cards keep growing on wide tiles.
 */
@Composable
private fun previewNumberStyle(): TextStyle = MaterialTheme.typography.displayLarge.copy(
    fontFamily = numberFontFamily(),
    fontWeight = FontWeight.Bold,
)

@Composable
private fun BullsAndCowsPreview() {
    val numberStyle = previewNumberStyle()
    val digitLabels = remember { BullsAndCowsPreviewTiles.map { it.first.toString() } }
    val digitFitter = rememberPreviewTextFitter(digitLabels, numberStyle)
    val chipFitter = rememberPreviewTextFitter(BullsAndCowsChipLabels.map { it.first }, numberStyle)
    Column(
        modifier = Modifier.previewWide(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            BullsAndCowsPreviewTiles.forEach { (digit, face) ->
                PreviewKey(
                    label = digit.toString(),
                    fitter = digitFitter,
                    face = face,
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(0.62f).height(22.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            BullsAndCowsChipLabels.forEach { (label, face) ->
                PreviewKey(
                    label = label,
                    fitter = chipFitter,
                    face = face,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}

private val BullsAndCowsChipLabels = listOf("1B" to SuccessGreen, "1C" to WordlePresent)

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
private const val SherlockPreviewGoal = "26"

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
 * word -> ink, printed in a colour it does not name, over the swatch row it is answered from. The
 * mismatch is the whole rule in one picture, and putting the word's own ink on the row below says
 * where the answer is without a word of explanation.
 */
private val ColorConfusionPreviewWord = GameColor.GREEN to GameColor.RED

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
            accentColor = UntimedSectionAccent,
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
        // The one conversion: GameCategory keeps its accent as an ARGB Long so the enum does not
        // drag a Compose dependency into the games package.
        accentColor = Color(gameType.accentColor),
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
        accentColor = UntimedSectionAccent,
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
        accentColor = UntimedSectionAccent,
        onClick = onClick,
        caption = stringResource(Res.string.menu_chess_caption),
    ) { NormalChessPreview() }
}

/** The Matchstick Riddles entry, with how many of the riddles are solved. */
@Composable
fun MatchstickRiddlesTile(solvedCount: Int, total: Int, onClick: () -> Unit) {
    NormalGameTile(
        label = stringResource(Res.string.matchstick_riddles_title),
        accentColor = UntimedSectionAccent,
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
        accentColor = UntimedSectionAccent,
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
        accentColor = UntimedSectionAccent,
        onClick = onClick,
        caption = stringResource(Res.string.menu_peg_caption),
    ) { PegSolitairePreview() }
}

/** The 6x6 Reversi entry. Endless play against the CPU, so a description rather than progress. */
@Composable
fun ReversiTile(onClick: () -> Unit) {
    NormalGameTile(
        label = stringResource(Res.string.reversi_button),
        accentColor = UntimedSectionAccent,
        onClick = onClick,
        caption = stringResource(Res.string.menu_reversi_caption),
    ) { ReversiPreview() }
}

/** English draughts on 8x8. Endless play against the CPU or a friend, so a description rather than progress. */
@Composable
fun CheckersTile(onClick: () -> Unit) {
    NormalGameTile(
        label = stringResource(Res.string.checkers_button),
        accentColor = UntimedSectionAccent,
        onClick = onClick,
        caption = stringResource(Res.string.menu_checkers_caption),
    ) { CheckersPreview() }
}

/**
 * A miniature of the result screen's bell curve rather than another matrix, so the tile reads as
 * "a measurement" next to the puzzle tiles rather than as a second Pattern Sequence.
 */
@Composable
private fun IqTestPreview() {
    val belowFace = MaterialTheme.colorScheme.primary
    val aboveFace = MaterialTheme.colorScheme.surfaceContainer
    Row(
        modifier = Modifier.previewSquare(),
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
    accentColor: Color,
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
                    .background(accentColor),
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
    accentColor: Color,
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
                    .background(accentColor),
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
        GameType.HEAD_COUNT -> HeadCountPreview()
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
        GameType.FLOCK -> FlockPreview()
        GameType.RAIL_YARD -> RailYardPreview()
        GameType.ORBIT_TRACKER -> OrbitTrackerPreview()
        GameType.FLASH_CROWD -> FlashCrowdPreview()
        GameType.MINI_CHESS -> MiniChessPreview()
        GameType.MINI_CHECKERS -> MiniCheckersPreview()
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
        GameType.RULE_SHIFT -> RuleShiftPreview()
    }
}

// --- Preview Composables ---

private val MentalFlexPreviewTarget = Figure(Shape.STAR, GameColor.BLUE)

// The four key cards, one symbol each: the tile has to read as a row of sorting targets, and the
// real counts would be unreadable at this size.
private val RuleShiftPreviewKeys = RuleShiftGame.keyCards.map { Figure(it.shape, it.color) }

@Composable
private fun RuleShiftPreview() {
    Column(
        modifier = Modifier.previewWide(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            RuleShiftPreviewKeys.forEach { figure ->
                PreviewShapeCard(figure = figure, modifier = Modifier.weight(1f).aspectRatio(1f))
            }
        }
        ShapeCanvas(
            figure = Figure(Shape.CROSS, GameColor.BLUE),
            modifier = Modifier.weight(1f).aspectRatio(1f),
        )
    }
}

// One match on shape, one on color, one on neither: the choice the rule cue resolves.
private val MentalFlexPreviewCandidates = listOf(
    Figure(Shape.STAR, GameColor.RED),
    Figure(Shape.CIRCLE, GameColor.BLUE),
    Figure(Shape.HEART, GameColor.GREEN),
)

@Composable
private fun MentalFlexPreview() {
    Column(
        modifier = Modifier.previewWide(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
    ) {
        ShapeCanvas(
            figure = MentalFlexPreviewTarget,
            modifier = Modifier.weight(1f).aspectRatio(1f),
        )
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            MentalFlexPreviewCandidates.forEach { figure ->
                PreviewShapeCard(figure = figure, modifier = Modifier.weight(1f).aspectRatio(1f))
            }
        }
    }
}

@Composable
private fun AnomalyPuzzlePreview() {
    PreviewShapeGrid(AnomalyPuzzlePreviewFigures)
}

/** Two rows of two shape cards, the board of the shape-memory and odd-one-out games. */
@Composable
private fun PreviewShapeGrid(figures: List<Figure?>) {
    Column(
        modifier = Modifier.previewSquare(),
        verticalArrangement = Arrangement.spacedBy(PreviewKeyGap, Alignment.CenterVertically),
    ) {
        figures.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
            ) {
                row.forEach { figure ->
                    PreviewShapeCard(figure = figure, modifier = Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
private fun PathFinderPreview() {
    Column(
        modifier = Modifier.previewSquare(inset = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            PathFinderPreviewDirections.forEach {
                ShapeCanvas(figure = it.figure, modifier = Modifier.size(20.dp))
            }
        }
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

private val ColoredShapesPreviewKeys = listOf("3", "4", "7")

/** The figure to describe over the number keys it is answered on. */
@Composable
private fun ColoredShapesPreview() {
    val fitter = rememberPreviewTextFitter(ColoredShapesPreviewKeys, previewNumberStyle())
    Column(
        modifier = Modifier.previewSquare(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
    ) {
        ShapeCanvas(
            figure = Figure(Shape.HEART, GameColor.BLUE),
            modifier = Modifier.weight(1f).aspectRatio(1f),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            ColoredShapesPreviewKeys.forEach { key ->
                PreviewKey(label = key, fitter = fitter, modifier = Modifier.weight(1f).aspectRatio(1f))
            }
        }
    }
}

@Composable
private fun SpotTheNewPreview() {
    val newAnimal = SpotTheNewPreviewAnimals.last()
    Column(
        modifier = Modifier.previewSquare(),
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
    PreviewShapeGrid(VisualMemoryPreviewFigures)
}

private const val MentalCalculationPreviewProblem = "8 + 15"
private val MentalCalculationPreviewKeys = listOf("7", "8", "9", "0")

/** The problem on its card over a row of the number keys it is answered on. */
@Composable
private fun MentalCalculationPreview() {
    val numberStyle = previewNumberStyle()
    val problemFitter = rememberPreviewTextFitter(listOf(MentalCalculationPreviewProblem), numberStyle)
    val keyFitter = rememberPreviewTextFitter(MentalCalculationPreviewKeys, numberStyle)
    Column(
        modifier = Modifier.previewWide(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        PreviewKey(
            label = MentalCalculationPreviewProblem,
            fitter = problemFitter,
            face = PreviewCardFace,
            ink = PreviewCardInk,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            MentalCalculationPreviewKeys.forEach { key ->
                PreviewKey(label = key, fitter = keyFitter, modifier = Modifier.weight(1f).aspectRatio(1f))
            }
        }
    }
}

private val MissingOperatorsPreviewTerms = listOf("12", null, "4", "=3")
private val MissingOperatorsPreviewKeys = listOf("+", "\u2212", "\u00D7", "\u00F7")

/** The equation as tiles with the operator still to place, above the operator keys. */
@Composable
private fun MissingOperatorsPreview() {
    val numberStyle = previewNumberStyle()
    val termFitter = rememberPreviewTextFitter(MissingOperatorsPreviewTerms.filterNotNull(), numberStyle)
    val keyFitter = rememberPreviewTextFitter(MissingOperatorsPreviewKeys, numberStyle)
    Column(
        modifier = Modifier.previewWide(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            MissingOperatorsPreviewTerms.forEach { term ->
                val cell = Modifier.weight(1f).aspectRatio(1f)
                if (term == null) {
                    PreviewSlot(modifier = cell, selected = true)
                } else {
                    PreviewKey(label = term, fitter = termFitter, face = PreviewCardFace, ink = PreviewCardInk, modifier = cell)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            MissingOperatorsPreviewKeys.forEach { key ->
                PreviewKey(label = key, fitter = keyFitter, modifier = Modifier.weight(1f).aspectRatio(1f))
            }
        }
    }
}

private const val QuickSumPreviewTerms = 4
private const val QuickSumPreviewShown = 2

/** Mirrors the arena: one term on its card, the run of terms below it, two seen and two to come. */
@Composable
private fun QuickSumPreview() {
    val fitter = rememberPreviewTextFitter(listOf("7"), previewNumberStyle())
    Column(
        modifier = Modifier.previewSquare(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        PreviewKey(
            label = "7",
            fitter = fitter,
            face = PreviewCardFace,
            ink = PreviewCardInk,
            modifier = Modifier.weight(1f).aspectRatio(1f),
        )
        Row(
            modifier = Modifier.fillMaxWidth(0.42f),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            repeat(QuickSumPreviewTerms) { index ->
                ColorPrismCell(
                    face = if (index < QuickSumPreviewShown) Primary else PreviewCardFace,
                    facet = PrismFacet.Dot,
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                )
            }
        }
    }
}

@Composable
private fun HeadCountPreview() {
    HeadCountHouse(
        entering = 2,
        leaving = 0,
        entersFromLeft = true,
        walkProgress = 0.45f,
        modifier = Modifier.fillMaxWidth().aspectRatio(HeadCountSceneAspect),
    )
}

private val DigitMemoryPreviewSequence = listOf("4", "9", "2", "8")

/** Recall is half done: the shown sequence above, the first digits keyed back below it. */
private const val DigitMemoryPreviewRecalled = 2

@Composable
private fun DigitMemoryPreview() {
    val fitter = rememberPreviewTextFitter(DigitMemoryPreviewSequence, previewNumberStyle())
    Column(
        modifier = Modifier.previewWide(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            DigitMemoryPreviewSequence.forEach { digit ->
                PreviewKey(
                    label = digit,
                    fitter = fitter,
                    face = PreviewCardFace,
                    ink = PreviewCardInk,
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            DigitMemoryPreviewSequence.forEachIndexed { index, digit ->
                val cell = Modifier.weight(1f).aspectRatio(1f)
                if (index < DigitMemoryPreviewRecalled) {
                    PreviewKey(label = digit, fitter = fitter, modifier = cell)
                } else {
                    PreviewSlot(modifier = cell, selected = index == DigitMemoryPreviewRecalled)
                }
            }
        }
    }
}

/**
 * A 2-back stream, and the whole task in one row: the newest item, on the right, repeats the one
 * two back. Both ends of that pair wear the green a caught match lights up in, so the tile states
 * the rule without needing a caption.
 */
private val NBackPreviewStream = listOf(
    Shape.STAR to true,
    Shape.CIRCLE to false,
    Shape.STAR to true,
)

@Composable
private fun NBackPreview() {
    // Slots rather than loose shapes: a stream is a run of positions, and the slot row is the
    // idiom the rest of the tile set already reads in. The shapes take the game's own colours -
    // near-black at 0.3 alpha went muddy against the pale MEMORY accent.
    Row(
        // Full tile width rather than the square box the grid previews use: three slots across a
        // square would leave the shapes too small to name at a glance.
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NBackPreviewStream.forEach { (shape, isMatch) ->
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
                    PrismPolygon(
                        points = shape.paths,
                        face = if (isMatch) SuccessGreen else Primary,
                        modifier = Modifier.fillMaxSize().padding(5.dp),
                    )
                }
            }
        }
    }
}

/** The goal on its plaque over the number cards the expression is built from. */
@Composable
private fun SherlockCalculationPreview() {
    val numberStyle = previewNumberStyle()
    val numberLabels = remember { SherlockPreviewNumbers.map { it.toString() } }
    val numberFitter = rememberPreviewTextFitter(numberLabels, numberStyle)
    val goalFitter = rememberPreviewTextFitter(listOf(SherlockPreviewGoal), numberStyle)
    Column(
        modifier = Modifier.previewWide(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PreviewKeyGap, Alignment.CenterVertically),
    ) {
        PreviewKey(
            label = SherlockPreviewGoal,
            fitter = goalFitter,
            face = PreviewCardFace,
            ink = Primary,
            modifier = Modifier.fillMaxWidth(0.5f).weight(1f),
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            SherlockPreviewNumbers.forEach { number ->
                PreviewKey(
                    label = number.toString(),
                    fitter = numberFitter,
                    face = PreviewCardFace,
                    ink = PreviewCardInk,
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                )
            }
        }
    }
}

/** `5 + 3 × 2 = ?` laid out as the tiles of an expression, two rows of three. */
private val ChainCalculationPreviewTokens = listOf(listOf("5", "+", "3"), listOf("\u00D7", "2", null))

@Composable
private fun ChainCalculationPreview() {
    val fitter = rememberPreviewTextFitter(
        ChainCalculationPreviewTokens.flatten().filterNotNull(),
        previewNumberStyle(),
    )
    Column(
        modifier = Modifier.previewWide(),
        verticalArrangement = Arrangement.spacedBy(PreviewKeyGap, Alignment.CenterVertically),
    ) {
        ChainCalculationPreviewTokens.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap, Alignment.CenterHorizontally),
            ) {
                row.forEach { token ->
                    val cell = Modifier.fillMaxHeight().aspectRatio(1f)
                    when {
                        token == null -> PreviewSlot(modifier = cell, selected = true)
                        token.first().isDigit() -> PreviewKey(
                            label = token,
                            fitter = fitter,
                            face = PreviewCardFace,
                            ink = PreviewCardInk,
                            modifier = cell,
                        )
                        else -> PreviewKey(label = token, fitter = fitter, modifier = cell)
                    }
                }
            }
        }
    }
}

@Composable
private fun FractionCalculationPreview() {
    Row(
        modifier = Modifier.previewSquare(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FractionCalculationPreviewCard("2", "3", Modifier.weight(1f))
        Text(
            "\u00D7",
            style = MaterialTheme.typography.titleLarge,
            fontFamily = numberFontFamily(),
            fontWeight = FontWeight.Bold,
            color = Primary,
        )
        FractionCalculationPreviewCard("4", "5", Modifier.weight(1f))
    }
}

@Composable
private fun FractionCalculationPreviewCard(numerator: String, denominator: String, modifier: Modifier = Modifier) {
    PrismCard(
        face = PreviewCardFace,
        facet = PrismFacet.Cell,
        modifier = modifier.fillMaxHeight(0.8f),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            FractionText(
                numerator = numerator,
                denominator = denominator,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = PreviewCardInk,
            )
        }
    }
}

private val ValueComparisonPreviewTerms = listOf("3 + 8", "5 + 4")

@Composable
private fun ValueComparisonPreview() {
    val vsLabel = stringResource(Res.string.preview_vs)
    val termFitter = rememberPreviewTextFitter(ValueComparisonPreviewTerms, previewNumberStyle())
    val vsLabels = remember(vsLabel) { listOf(vsLabel) }
    val vsFitter = rememberPreviewTextFitter(vsLabels, MaterialTheme.typography.labelMedium)
    // The two answer buttons of the game, stacked, with the versus between them.
    Column(
        modifier = Modifier.previewWide(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val term = Modifier.fillMaxWidth().weight(1f)
        PreviewKey(label = ValueComparisonPreviewTerms[0], fitter = termFitter, modifier = term)
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().weight(0.55f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = vsLabel,
                style = vsFitter.fitTo(maxWidth, maxHeight),
                color = PreviewTextColor,
                maxLines = 1,
                softWrap = false,
            )
        }
        PreviewKey(label = ValueComparisonPreviewTerms[1], fitter = termFitter, modifier = term)
    }
}

@Composable
private fun MiniSudokuPreview() {
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val digits = remember { MiniSudokuPreviewGrid.flatten().filter { it.isNotEmpty() } }
    val fitter = rememberPreviewTextFitter(digits, previewNumberStyle())
    PreviewBoard(
        modifier = Modifier.previewSquare(),
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
                        BoxWithConstraints(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = cell,
                                style = fitter.fitTo(maxWidth * 0.5f, maxHeight * 0.5f),
                                color = MaterialTheme.colorScheme.onSurface,
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
    PreviewBoard(
        modifier = Modifier.previewSquare(),
    ) {
        Canvas(modifier = Modifier.fillMaxSize().background(cellColor)) {
            val n = 9
            val cell = size.width / n
            drawPuzzleGridLines(rows = n, cols = n, color = thinLine, strokeWidth = 1.dp.toPx())
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
    PreviewBoard(
        modifier = Modifier.previewSquare(),
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

// A mid-game 6x6 position rather than the opening four discs: the tile has to read as Reversi at
// thumbnail size, and two discs on an empty board read as nothing at all.
private val ReversiPreviewBlack: Set<Int> = setOf(8, 9, 14, 15, 20, 21, 26, 27, 28)
private val ReversiPreviewWhite: Set<Int> = setOf(7, 13, 16, 19, 22, 25)

@Composable
private fun ReversiPreview() {
    PreviewBoard(
        modifier = Modifier.previewSquare(),
    ) {
        Column(modifier = Modifier.fillMaxSize().background(ReversiGridLine)) {
            for (row in 0 until 6) {
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    for (col in 0 until 6) {
                        val index = row * 6 + col
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(Modifier.matchParentSize().padding(0.5.dp).background(ReversiFelt))
                            val face = when (index) {
                                in ReversiPreviewBlack -> ReversiBlackDisc
                                in ReversiPreviewWhite -> ReversiWhiteDisc
                                else -> null
                            }
                            if (face != null) {
                                ColorPrismCell(
                                    face = face,
                                    facet = PrismFacet.Dot,
                                    modifier = Modifier.fillMaxSize(0.78f),
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
private fun CheckersPreview() {
    CheckersPreviewBoard(size = 8) { row, _ ->
        when {
            row <= 2 -> CheckersPiece(CheckersSide.WHITE, isKing = false)
            row >= 5 -> CheckersPiece(CheckersSide.BLACK, isKing = false)
            else -> null
        }
    }
}

private val MiniCheckersPreviewPieces: Map<Pair<Int, Int>, CheckersPiece> = mapOf(
    (0 to 3) to CheckersPiece(CheckersSide.WHITE, isKing = false),
    (2 to 1) to CheckersPiece(CheckersSide.WHITE, isKing = false),
    (1 to 2) to CheckersPiece(CheckersSide.BLACK, isKing = true),
    (3 to 0) to CheckersPiece(CheckersSide.BLACK, isKing = false),
)

@Composable
private fun MiniCheckersPreview() {
    CheckersPreviewBoard(size = 4) { row, col -> MiniCheckersPreviewPieces[row to col] }
}

@Composable
private fun CheckersPreviewBoard(size: Int, pieceOnDarkSquare: (row: Int, col: Int) -> CheckersPiece?) {
    PreviewBoard(
        modifier = Modifier.previewSquare(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0 until size) {
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    for (col in 0 until size) {
                        val isDark = (row + col) % 2 == 1
                        val piece = if (isDark) pieceOnDarkSquare(row, col) else null
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (isDark) CheckersDarkSquare else CheckersLightSquare),
                            contentAlignment = Alignment.Center,
                        ) {
                            piece?.let { CheckersDisc(piece = it, modifier = Modifier.fillMaxSize(0.8f)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PegSolitairePreview() {
    val surface = PegBoardSurface
    val hole = PegHole
    PreviewBoard(
        modifier = Modifier.previewSquare(),
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
        modifier = Modifier.previewSquare(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val numberLabels = remember {
            SchulteTablePreviewGrid.flatten().map { it.number.toString() }
        }
        val numberStyle = previewNumberStyle()
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
                                style = fitter.fitTo(maxWidth * PreviewLabelFill, maxHeight * PreviewLabelFill),
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
        modifier = Modifier.previewSquare(),
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

// The pad that is mid-flash. Every pad stays at full colour rather than three dark and one lit:
// the unlit face is near-black by design, and three of those go muddy on the pale MEMORY accent.
private const val SimonSaysPreviewLitPad = 1

@Composable
private fun SimonSaysPreview() {
    // The light card face the other boards sit on rather than the game's slate body.
    SimonDisc(
        modifier = Modifier.previewSquare(),
        bodyColor = PreviewCardFace,
    ) { index, quadrant, padModifier ->
        val base = SimonSaysGame.PADS[index].composeColor()
        Box(
            modifier = padModifier.simonPadSurface(
                quadrant = quadrant,
                face = if (index == SimonSaysPreviewLitPad) simonPadColor(base, lit = true) else base,
            ),
        )
    }
}

@Composable
private fun LightsOutPreview() {
    Column(
        modifier = Modifier.previewSquare(),
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
    PreviewBoard(
        modifier = Modifier.previewSquare(),
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
            drawPuzzleGridLines(rows = n, cols = n, color = gridLineColor, strokeWidth = 1.dp.toPx())

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
    PreviewBoard(
        modifier = Modifier.previewSquare(),
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
            drawPuzzleGridLines(rows = n, cols = n, color = gridLineColor, strokeWidth = 1.5.dp.toPx())

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
    PreviewBoard(
        modifier = Modifier.previewSquare(),
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

            drawPuzzleGridLines(rows = n, cols = n, color = gridLineColor, strokeWidth = 1.dp.toPx())

            drawRegionBorders(
                regionIdByCellIndex = CatQueensPreviewRegions,
                rows = n,
                cols = n,
                color = borderColor,
                strokeWidth = 2.5f.dp.toPx(),
            )

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
    PreviewBoard(
        modifier = Modifier.previewSquare(),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / n
            val cellH = size.height / n
            fun center(cell: Int) = Offset((cell % n + 0.5f) * cellW, (cell / n + 0.5f) * cellH)

            drawRect(color = KnotCellColor)
            drawPuzzleGridLines(rows = n, cols = n, color = gridLineColor, strokeWidth = 1.dp.toPx())

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
        modifier = Modifier.previewSquare(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Every letter of the puzzle drives one shared size: Bungee is proportional, so fitting each
        // cell on its own would render "I" larger than "W" in the same row.
        val letters = remember(puzzle) { puzzle.guesses.flatMap { it.map(Char::toString) } }
        val letterStyle = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold)
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
    PreviewKey(
        label = char.toString(),
        fitter = fitter,
        face = state.tileFace(),
        ink = state.tileTextColor(),
        modifier = modifier,
    )
}

@Composable
private fun SlidingPuzzlePreview() {
    val numberLabels = remember {
        SlidingPuzzlePreviewLabels.filter { it != 0 }.map { it.toString() }
    }
    val numberStyle = previewNumberStyle()
    val fitter = rememberPreviewTextFitter(numberLabels, numberStyle)
    Column(
        modifier = Modifier.previewSquare(),
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
                                    style = fitter.fitTo(maxWidth * PreviewLabelFill, maxHeight * PreviewLabelFill),
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
    val fitter = rememberPreviewTextFitter(listOf("?"), previewNumberStyle())
    Column(
        modifier = Modifier.previewSquare(),
        verticalArrangement = Arrangement.spacedBy(PreviewKeyGap, Alignment.CenterVertically),
    ) {
        repeat(2) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
            ) {
                repeat(2) { column ->
                    val index = row * 2 + column
                    val cell = Modifier.weight(1f).aspectRatio(1f)
                    if (index == PatternSequencePreviewFigures.size) {
                        // The cell the player supplies, in the game's highlighted face.
                        PreviewKey(
                            label = "?",
                            fitter = fitter,
                            face = LightColorScheme.secondaryContainer,
                            ink = PreviewCardInk,
                            modifier = cell,
                        )
                    } else {
                        PreviewShapeCard(figure = PatternSequencePreviewFigures[index], modifier = cell)
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
    PreviewBoard(
        modifier = Modifier.previewSquare(),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            val ballRadius = size.width * 0.08f
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
    PreviewBoard(
        modifier = Modifier.previewSquare(),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            val ballRadius = size.width * 0.15f
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
                    style = (if (warning) warningDigitStyle else digitStyle).copy(fontSize = ballRadius.toSp()),
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
    val (word, ink) = ColorConfusionPreviewWord
    val label = word.localizedName()
    val fitter = rememberPreviewTextFitter(listOf(label), MaterialTheme.typography.headlineMedium)
    Column(
        modifier = Modifier.previewWide(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            Text(
                text = label,
                style = fitter.fitTo(maxWidth, maxHeight),
                color = ink.composeColor(),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
        ) {
            ColorConfusionGame.RESPONSE_COLORS.forEach { swatch ->
                ColorPrismCell(
                    face = swatch.composeColor(),
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                )
            }
        }
    }
}

/** The flock on a strip of sky, so the birds sit on something rather than float on the tile. */
@Composable
private fun FlockPreview() {
    PreviewBoard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
    ) {
        FlockRow(
            target = FlockGame.Direction.LEFT,
            flankers = FlockGame.Direction.RIGHT,
            feedback = AnswerFeedbackState.NORMAL,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun RailYardPreview() {
    // The light card every other board preview sits on, so the rails flip to the yard's dark slate.
    RailYardBoard(
        uiState = RailYardPreviewState,
        trains = { RailYardPreviewState.trains },
        modifier = Modifier.previewSquare(inset = 14.dp),
        ground = PreviewCardFace,
        rail = RailYardGround,
        facet = PrismFacet.Preview,
    )
}

// Drawn by hand rather than dealt: a generated map is too dense to read at tile size, so the tile
// keeps the smallest yard there is, one switch and a train already heading the right way.
private val RailYardPreviewState = RailYardUiState(
    layoutKey = 0,
    tunnel = RailYardGame.Point(0.5f, 0.04f),
    tracks = persistentListOf(
        RailYardUiState.Track(persistentListOf(RailYardGame.Point(0.5f, 0.04f), RailYardGame.Point(0.5f, 0.33f)), isOpen = true),
        RailYardUiState.Track(
            persistentListOf(RailYardGame.Point(0.5f, 0.33f), RailYardGame.Point(0.18f, 0.33f), RailYardGame.Point(0.18f, 0.92f)),
            isOpen = true,
        ),
        RailYardUiState.Track(
            persistentListOf(RailYardGame.Point(0.5f, 0.33f), RailYardGame.Point(0.82f, 0.33f), RailYardGame.Point(0.82f, 0.92f)),
            isOpen = false,
        ),
    ),
    switches = persistentListOf(RailYardUiState.Switch(nodeIndex = 1, position = RailYardGame.Point(0.5f, 0.33f), turnsRight = false)),
    stations = persistentListOf(
        RailYardUiState.Station(RailYardGame.Point(0.18f, 0.92f), GameColor.RED, RailYardGame.StationFlash.NONE),
        RailYardUiState.Station(RailYardGame.Point(0.82f, 0.92f), GameColor.BLUE, RailYardGame.StationFlash.NONE),
    ),
    trains = persistentListOf(RailYardGame.TrainFrame(x = 0.18f, y = 0.52f, isHorizontal = false, color = GameColor.RED)),
)

/** Two side-by-side panels, one per crowd, so the tile reads as a comparison of two fields. */
@Composable
private fun FlashCrowdPreview() {
    Row(
        modifier = Modifier.previewWide(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FlashCrowdPreviewPanel(FlashCrowdPreviewLeftDots, FlashCrowdBlue, FlashCrowdBlueSide, FlashCrowdBlueBottom)
        FlashCrowdPreviewPanel(FlashCrowdPreviewRightDots, FlashCrowdYellow, FlashCrowdYellowSide, FlashCrowdYellowBottom)
    }
}

@Composable
private fun RowScope.FlashCrowdPreviewPanel(
    dots: List<Triple<Float, Float, Float>>,
    face: Color,
    side: Color,
    bottom: Color,
) {
    PreviewBoard(
        modifier = Modifier.weight(1f).fillMaxHeight(),
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            dots.forEach { (x, y, r) ->
                drawPrismCircle(
                    center = Offset(x * size.width, y * size.height),
                    radius = r * size.width,
                    face = face,
                    side = side,
                    bottom = bottom,
                )
            }
        }
    }
}

@Composable
private fun MiniChessPreview() {
    PreviewBoard(
        modifier = Modifier.previewSquare(),
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
    PreviewBoard(
        modifier = Modifier.previewSquare(),
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
    PreviewBoard(
        modifier = Modifier.previewSquare(),
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

/** A well with blocks settled to the bottom, the way a Prism Clear board fills up. */
@Composable
private fun PrismClearPreview() {
    PreviewBoard(
        modifier = Modifier.previewWide(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(PreviewCardFace).padding(4.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            PrismClearPreviewPattern.chunked(PrismClearPreviewColumns).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { ordinal ->
                        val cell = Modifier.weight(1f).aspectRatio(1f).padding(1.dp)
                        if (ordinal == null) {
                            Spacer(cell)
                        } else {
                            ColorPrismCell(face = PrismTileType.entries[ordinal].color.composeColor(), modifier = cell)
                        }
                    }
                }
            }
        }
    }
}

private const val PrismClearPreviewColumns = 6
private val PrismClearPreviewPattern: List<Int?> = listOf(
    null, null, null, 1, null, null,
    0, null, 2, 1, null, 0,
    2, 0, 2, 0, 1, 1,
)

private val FlagsPreviewDrawables: List<DrawableResource> = listOf(
    Res.drawable.flag_japan,
    Res.drawable.flag_brazil,
    Res.drawable.flag_france,
    Res.drawable.flag_canada,
)

/** A hand of flag cards, each on its own white card. */
@Composable
private fun FlagsPreview() {
    Column(
        modifier = Modifier.previewSquare(),
        verticalArrangement = Arrangement.spacedBy(PreviewKeyGap, Alignment.CenterVertically),
    ) {
        FlagsPreviewDrawables.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PreviewKeyGap),
            ) {
                row.forEach { drawable ->
                    PrismCard(
                        face = Color.White,
                        facet = PrismFacet.Cell,
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                    ) {
                        Image(
                            painter = painterResource(drawable),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(2.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * A board in miniature with the trio already found: three cards lit green, one shape each and a
 * single mark so the shapes stay readable at tile size, beside a decoy. Raised and opaque rather
 * than the game's pressed, translucent correct face, which reads as mud on the tile's background.
 */
private val TrioPreviewCards = listOf(
    TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID) to true,
    TrioCard(TrioShape.SQUARE, 2, TrioFill.OUTLINE) to false,
    TrioCard(TrioShape.TRIANGLE, 1, TrioFill.OUTLINE) to true,
    TrioCard(TrioShape.SQUARE, 1, TrioFill.STRIPED) to true,
)

private val TrioPreviewFoundFace = SuccessGreen.copy(alpha = 0.4f).compositeOver(Color.White)

@Composable
private fun TrioPreview() {
    Column(
        modifier = Modifier.previewSquare(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TrioPreviewCards.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { (card, found) ->
                    PrismCard(
                        face = if (found) TrioPreviewFoundFace else MaterialTheme.colorScheme.surfaceContainer,
                        facet = PrismFacet.Cell,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                    ) {
                        TrioCardGlyphs(
                            shape = card.shape,
                            count = card.count,
                            fill = card.fill,
                            color = Color.Black,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
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
    PreviewBoard(
        modifier = Modifier.previewWide(),
    ) {
        MentalRotationsPair(
            reference = MentalRotationsPreviewFigure.toProjection(),
            candidate = mirror(MentalRotationsPreviewFigure).toProjection(),
            modifier = Modifier.fillMaxSize().padding(6.dp),
            spacing = 6.dp,
        )
    }
}
