package com.inspiredandroid.braincup.ui.theme

import androidx.compose.ui.graphics.Color
import com.inspiredandroid.braincup.ui.components.darken

/** Dark ink for puzzle grid lines, region borders, and clue glyphs. */
val PuzzleGridInk = Color(0xFF1A1A1A)

fun puzzleGridLine(alpha: Float = 0.2f): Color = PuzzleGridInk.copy(alpha = alpha)

/** Shared slate tray used by Shikaku and Knot boards. */
val PuzzleSlateFrame = Color(0xFF3E4450)

/** Shared light cell background used by Shikaku and Knot boards. */
val PuzzleLightCell = Color(0xFFDDE3EA)

val FlashCrowdBlue = Color(0xFF4285F4)
val FlashCrowdYellow = Color(0xFFFBBC04)

// Neutral graphite tray for the Cat Queens board, so the pastel zones pop off the prism frame.
val CatQueensBoardFrame = Color(0xFF4A4754)

val ShikakuBoardFrame = PuzzleSlateFrame
val ShikakuCellColor = PuzzleLightCell

// Dark ocean-slate tray for the Nurikabe grid — subtly blue-tinted to echo the sea theme.
val NurikabeBoardFrame = Color(0xFF3A4858)
val NurikabeIslandColor = Color(0xFFE8E8E8)
val NurikabeSeaColor = Color(0xFF546E7A)

val KnotBoardFrame = PuzzleSlateFrame
val KnotCellColor = PuzzleLightCell

/** Peg pole / base for Tower of Hanoi. */
val HanoiPegColor = Color(0xFF6B7280)
val HanoiBaseColor = Color(0xFF4B5563)

/**
 * Disk face colors by size rank (1 = smallest … 7 = largest). Warm spectrum so stacks read
 * clearly in light and dark themes.
 */
val HanoiDiskColors: List<Color> = listOf(
    Color(0xFF60A5FA), // 1 blue
    Color(0xFF34D399), // 2 green
    Color(0xFFFBBF24), // 3 amber
    Color(0xFFF97316), // 4 orange
    Color(0xFFF43F5E), // 5 rose
    Color(0xFFA78BFA), // 6 violet
    Color(0xFF2DD4BF), // 7 teal
)

val FlashCrowdBlueSide = FlashCrowdBlue.darken(0.7f)
val FlashCrowdBlueBottom = FlashCrowdBlue.darken(0.5f)

// Yellow needs a lighter darken than the prism default (0.7/0.5) so the facets read on a light hue.
val FlashCrowdYellowSide = FlashCrowdYellow.darken(0.85f)
val FlashCrowdYellowBottom = FlashCrowdYellow.darken(0.7f)
