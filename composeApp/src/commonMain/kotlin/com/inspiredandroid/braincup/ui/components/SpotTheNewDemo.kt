package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.spot_the_new_demo_correct
import braincup.composeapp.generated.resources.spot_the_new_demo_new
import braincup.composeapp.generated.resources.spot_the_new_demo_seen
import braincup.composeapp.generated.resources.spot_the_new_demo_seen_label
import braincup.composeapp.generated.resources.spot_the_new_demo_title
import com.inspiredandroid.braincup.games.tools.Animal
import com.inspiredandroid.braincup.ui.theme.SpotTheNewColors
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

// Choreography timings, tuned to read like the real game while staying watchable on a loop.
private const val ResetPauseMillis = 600L
private const val HighlightMillis = 1000L
private const val PressMillis = 240L
private const val AcceptFlashMillis = 360L
private const val BeforeWrongPressMillis = 750L
private const val WrongHoldMillis = 1800L
private const val RoundRestMillis = 650L
private const val LoopEndHoldMillis = 1700L
private const val GridFadeMillis = 320
private const val FlyMillis = 520

// Fixed cast (visually distinct sea creatures) so the script is deterministic. The run opens with
// two animals already on the shelf, then plays three correct rounds. OCTOPUS is shown in round 1,
// sits out round 2, and returns in round 3, where it is spotlighted as the "seen but absent" case.
private val InitialSeen = listOf(Animal.CRAB, Animal.OCTOPUS)
private val Round1Grid = listOf(Animal.CRAB, Animal.OCTOPUS, Animal.TURTLE)
private val Round1New = Animal.TURTLE
private val Round2Grid = listOf(Animal.CRAB, Animal.TURTLE, Animal.WHALE)
private val Round2New = Animal.WHALE
private val Round3Grid = listOf(Animal.OCTOPUS, Animal.TURTLE, Animal.JELLYFISH)
private val Round3New = Animal.JELLYFISH
private val Round3Recalled = Animal.OCTOPUS

// Every caption the demo cycles through, so the caption line can reserve the tallest one's height.
private val DemoCaptions = persistentListOf(
    Res.string.spot_the_new_demo_new,
    Res.string.spot_the_new_demo_seen,
    Res.string.spot_the_new_demo_correct,
)

/** A tile in flight from its grid slot up into the "Seen so far" shelf. */
private data class Flyer(val animal: Animal, val fromIndex: Int, val destSlot: Int, val face: Color)

/**
 * Animated tutorial board for Spot the New. It loops through a short scripted run that teaches the
 * one rule the real game never shows visually: an animal counts as "seen" the moment it has ever
 * appeared, even when it sat out the previous round.
 *
 * The run opens with two animals already on the "Seen so far" shelf, then plays three rounds: in
 * each one the new animal is highlighted, pressed, and flies up out of the grid to shrink into the
 * shelf, so the cumulative memory stays visible (the real game has no shelf; this is a teaching aid,
 * like Ghost Grid's numbered tiles). The grid cross-fades between rounds to make each round boundary
 * clear. The final round demonstrates the trap: an animal that sat out the previous round returns,
 * gets tapped, and is marked red (its shelf slot lights up as proof it was already seen) so the
 * viewer learns that tapping a seen animal is wrong; only then is the genuinely new one tapped.
 * Plays on its own like [GhostGridDemo].
 */
@Composable
fun SpotTheNewDemo(modifier: Modifier = Modifier) {
    // Every animal recognised so far, in the order learned (grows across the run).
    var shelf by remember { mutableStateOf(emptyList<Animal>()) }
    // The current round's full set of tiles, in display order.
    var gridAnimals by remember { mutableStateOf(emptyList<Animal>()) }
    // Tiles that have already flown out of the current grid into the shelf.
    var removedFromGrid by remember { mutableStateOf(emptySet<Animal>()) }
    // The round's answer: highlighted, then pressed, then it flies to the shelf.
    var newAnimal by remember { mutableStateOf<Animal?>(null) }
    // The old animal spotlighted as "already seen" during the key round (highlights its shelf slot).
    var recalled by remember { mutableStateOf<Animal?>(null) }
    // The tile currently sunk by a (simulated) tap.
    var pressed by remember { mutableStateOf<Animal?>(null) }
    // A grid tile marked red because tapping it would be wrong (it was already seen).
    var wrongMarked by remember { mutableStateOf<Animal?>(null) }
    var accepted by remember { mutableStateOf(false) }
    // The single tile currently mid-flight, plus the reserved empty shelf slot it is heading for.
    var flyer by remember { mutableStateOf<Flyer?>(null) }
    var reservedShelf by remember { mutableStateOf(false) }
    var captionRes by remember { mutableStateOf(Res.string.spot_the_new_demo_new) }

    val gridAlpha = remember { Animatable(0f) }
    val flyProgress = remember { Animatable(0f) }

    // Exception: shelf tiles use surfaceVariant — demo-only chrome not present in the real game.
    val shelfFace = MaterialTheme.colorScheme.surfaceVariant
    val highlightFace = SpotTheNewColors.highlightFace()
    val normalFace = SpotTheNewColors.normalFace()
    val wrongFace = SpotTheNewColors.wrongFace()

    val compact = LocalIsCompactHeight.current
    val gridCell = if (compact) 56.dp else 64.dp
    val shelfCell = if (compact) 34.dp else 40.dp
    val gridGap = 8.dp
    val shelfGap = 6.dp
    val midGap = 20.dp
    val maxShelf = InitialSeen.size + 3 // two seen at the start, plus one new animal per round

    val shelfRowWidth = shelfCell * maxShelf + shelfGap * (maxShelf - 1)
    val gridRowWidth = gridCell * 3 + gridGap * 2
    val stageWidth = maxOf(shelfRowWidth, gridRowWidth)
    val stageHeight = shelfCell + midGap + gridCell

    val density = LocalDensity.current
    val shelfCellPx = with(density) { shelfCell.toPx() }
    val shelfGapPx = with(density) { shelfGap.toPx() }
    val gridCellPx = with(density) { gridCell.toPx() }
    val gridGapPx = with(density) { gridGap.toPx() }
    val stageWidthPx = with(density) { stageWidth.toPx() }
    val gridTopPx = with(density) { (shelfCell + midGap).toPx() }

    // Center of shelf slot [k] when the row holds [n] slots (the row is centered in the stage).
    fun shelfSlotCenter(k: Int, n: Int): Offset {
        val rowWidth = shelfCellPx * n + shelfGapPx * (n - 1)
        val left = (stageWidthPx - rowWidth) / 2f
        return Offset(left + k * (shelfCellPx + shelfGapPx) + shelfCellPx / 2f, shelfCellPx / 2f)
    }

    // Center of grid tile [i] (the answering grid always holds three tiles).
    fun gridTileCenter(i: Int): Offset {
        val rowWidth = gridCellPx * 3 + gridGapPx * 2
        val left = (stageWidthPx - rowWidth) / 2f
        return Offset(left + i * (gridCellPx + gridGapPx) + gridCellPx / 2f, gridTopPx + gridCellPx / 2f)
    }

    LaunchedEffect(Unit) {
        // Lift [animal] out of its grid slot and glide it into the next free shelf slot.
        suspend fun flyToShelf(animal: Animal, face: Color) {
            val index = gridAnimals.indexOf(animal)
            if (index < 0) return
            flyer = Flyer(animal, index, shelf.size, face)
            reservedShelf = true // hold the destination slot open so nothing reflows on landing
            flyProgress.snapTo(0f)
            flyProgress.animateTo(1f, tween(FlyMillis))
            shelf = shelf + animal
            reservedShelf = false
            removedFromGrid = removedFromGrid + animal
            flyer = null
        }

        // Cross-fade the grid out, swap in a new round, and fade it back.
        suspend fun showRound(animals: List<Animal>) {
            if (gridAnimals.isNotEmpty()) gridAlpha.animateTo(0f, tween(GridFadeMillis))
            gridAnimals = animals
            removedFromGrid = emptySet()
            newAnimal = null
            recalled = null
            pressed = null
            wrongMarked = null
            accepted = false
            gridAlpha.snapTo(0f)
            gridAlpha.animateTo(1f, tween(GridFadeMillis))
        }

        // Show a round. If [recalledAnimal] is set, first demonstrate the trap: tap that already-seen
        // animal and mark it red (wrong), while its shelf slot lights up as proof it was seen. Then
        // tap the genuinely new one and send it up to the shelf.
        suspend fun playRound(grid: List<Animal>, newOne: Animal, recalledAnimal: Animal?) {
            showRound(grid)
            if (recalledAnimal != null) {
                recalled = recalledAnimal // light up its shelf slot: "it is already up here"
                captionRes = Res.string.spot_the_new_demo_seen
                delay(BeforeWrongPressMillis)
                pressed = recalledAnimal // the tempting (wrong) tap
                delay(PressMillis)
                pressed = null
                wrongMarked = recalledAnimal // turn it red: tapping a seen animal is wrong
                delay(WrongHoldMillis)
                wrongMarked = null
                recalled = null
            }
            newAnimal = newOne
            captionRes = Res.string.spot_the_new_demo_new
            delay(HighlightMillis)
            pressed = newOne
            delay(PressMillis)
            pressed = null
            accepted = true
            captionRes = Res.string.spot_the_new_demo_correct
            delay(AcceptFlashMillis)
            flyToShelf(newOne, highlightFace)
        }

        while (true) {
            // Start with two animals already seen and an empty grid.
            shelf = InitialSeen
            gridAnimals = emptyList()
            removedFromGrid = emptySet()
            newAnimal = null
            recalled = null
            pressed = null
            wrongMarked = null
            accepted = false
            flyer = null
            reservedShelf = false
            gridAlpha.snapTo(0f)
            captionRes = Res.string.spot_the_new_demo_new

            delay(ResetPauseMillis)

            // Three correct rounds in a row. OCTOPUS sits out round 2 and returns in round 3, where
            // it is spotlighted to show that "seen" persists even when an animal leaves the grid.
            playRound(Round1Grid, Round1New, recalledAnimal = null)
            delay(RoundRestMillis)
            playRound(Round2Grid, Round2New, recalledAnimal = null)
            delay(RoundRestMillis)
            playRound(Round3Grid, Round3New, recalledAnimal = Round3Recalled)
            delay(LoopEndHoldMillis)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.spot_the_new_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.spot_the_new_demo_seen_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))

        Box(Modifier.size(stageWidth, stageHeight)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Shelf row: filled slots, plus one invisible reserved slot while a tile is in flight.
                val slots: List<Animal?> = shelf + if (reservedShelf) listOf(null) else emptyList()
                Row(horizontalArrangement = Arrangement.spacedBy(shelfGap)) {
                    slots.forEach { animal ->
                        if (animal == null) {
                            Spacer(Modifier.size(shelfCell))
                        } else {
                            AnimalTile(
                                animal = animal,
                                face = if (animal == recalled) highlightFace else shelfFace,
                                contentPadding = 4.dp,
                                modifier = Modifier.size(shelfCell),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(midGap))

                // Round grid: three fixed-size tiles; a tile that has flown out leaves an empty slot.
                Row(
                    horizontalArrangement = Arrangement.spacedBy(gridGap),
                    modifier = Modifier.alpha(gridAlpha.value),
                ) {
                    gridAnimals.forEach { animal ->
                        if (animal in removedFromGrid || flyer?.animal == animal) {
                            Spacer(Modifier.size(gridCell))
                        } else {
                            val face = when {
                                animal == wrongMarked -> wrongFace
                                animal == newAnimal -> highlightFace
                                else -> normalFace
                            }
                            AnimalTile(
                                animal = animal,
                                face = face,
                                contentPadding = 8.dp,
                                // Reuse PrismTile's sink so the tap reads as a real press.
                                isSelected = animal == pressed,
                                modifier = Modifier.size(gridCell),
                            )
                        }
                    }
                }
            }

            // The flying tile: glides from its grid slot to the shelf, shrinking as it rises.
            flyer?.let { f ->
                val p = flyProgress.value
                val from = gridTileCenter(f.fromIndex)
                val to = shelfSlotCenter(f.destSlot, shelf.size + 1)
                val cx = from.x + (to.x - from.x) * p
                val cy = from.y + (to.y - from.y) * p
                val sizePx = gridCellPx + (shelfCellPx - gridCellPx) * p
                AnimalTile(
                    animal = f.animal,
                    face = f.face,
                    contentPadding = (8f - 4f * p).dp,
                    modifier = Modifier
                        .offset {
                            IntOffset((cx - sizePx / 2f).roundToInt(), (cy - sizePx / 2f).roundToInt())
                        }
                        .size(with(density) { sizePx.toDp() }),
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        DemoCaption(current = captionRes, all = DemoCaptions)
    }
}

@Composable
private fun AnimalTile(
    animal: Animal,
    face: Color,
    contentPadding: Dp,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    PrismTile(
        face = face,
        modifier = modifier,
        isClickable = false,
        isSelected = isSelected,
        onClick = {},
    ) {
        Image(
            painter = painterResource(animal.resource),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        )
    }
}
