package com.inspiredandroid.braincup.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.matchstick_riddles_reset
import braincup.composeapp.generated.resources.matchstick_riddles_solved
import braincup.composeapp.generated.resources.matchstick_riddles_title
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.matchstickriddles.MatchstickRiddle
import com.inspiredandroid.braincup.matchstickriddles.MatchstickRiddles
import com.inspiredandroid.braincup.matchstickriddles.pointToSegmentDistance
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.TextPrismButton
import com.inspiredandroid.braincup.ui.components.boardTransform
import com.inspiredandroid.braincup.ui.components.drawStick
import com.inspiredandroid.braincup.ui.components.drawStickOutline
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.ErrorRed
import com.inspiredandroid.braincup.ui.theme.MatchstickColors
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

// Distances are in board units, where one matchstick is one unit long.
private const val HIT_THRESHOLD = 0.7f
private const val SNAP_RADIUS = 0.7f

@Composable
fun MatchstickRiddlesPlayScreen(
    riddleId: String,
    storage: UserStorage,
    onCompleted: () -> Unit,
    onBack: () -> Unit,
) {
    val riddle = remember(riddleId) { MatchstickRiddles.byId(riddleId) }
    if (riddle == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val occupied = remember(riddle) { mutableStateListOf<Int>().apply { addAll(riddle.initial) } }
    var solved by remember(riddle) { mutableStateOf(false) }

    fun move(from: Int, to: Int) {
        if (solved || from == to) return
        if (from !in occupied || to in occupied) return
        occupied.remove(from)
        occupied.add(to)
        if (riddle.isSolved(occupied.toSet())) {
            solved = true
            storage.markMatchstickRiddleSolved(riddle.id)
        }
    }

    LaunchedEffect(solved) {
        if (solved) {
            delay(1200)
            onCompleted()
        }
    }

    fun resetBoard() {
        occupied.clear()
        occupied.addAll(riddle.initial)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val landscape = maxWidth > maxHeight

        AppScaffold(
            title = stringResource(Res.string.matchstick_riddles_title),
            onBack = onBack,
            scrollable = false,
            actions = if (landscape && !solved) {
                {
                    MatchstickResetAction(onClick = ::resetBoard)
                }
            } else {
                null
            },
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!solved) {
                    val movesLeft =
                        (riddle.moves - occupied.count { it !in riddle.initial }).coerceAtLeast(0)
                    if (landscape) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = stringResource(riddle.promptRes),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                            )
                            MovesIndicator(budget = riddle.moves, movesLeft = movesLeft)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(riddle.promptRes),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            MovesIndicator(budget = riddle.moves, movesLeft = movesLeft)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    MatchstickBoard(
                        riddle = riddle,
                        occupied = occupied,
                        solved = solved,
                        onMove = ::move,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(riddle.boardWidth / riddle.boardHeight),
                    )
                }

                if (!landscape || solved) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (solved) {
                            Text(
                                text = stringResource(Res.string.matchstick_riddles_solved),
                                style = MaterialTheme.typography.titleMedium,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold,
                            )
                        } else {
                            MatchstickResetButton(onClick = ::resetBoard)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchstickResetAction(onClick: () -> Unit) {
    TextPrismButton(
        onClick = onClick,
        value = stringResource(Res.string.matchstick_riddles_reset),
        modifier = Modifier.padding(end = 8.dp),
    )
}

@Composable
private fun MatchstickResetButton(onClick: () -> Unit) {
    PrismTile(
        face = Primary,
        onClick = onClick,
        modifier = Modifier.widthIn(min = 120.dp),
    ) {
        Text(
            text = stringResource(Res.string.matchstick_riddles_reset),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun MatchstickBoard(
    riddle: MatchstickRiddle,
    occupied: List<Int>,
    solved: Boolean,
    onMove: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dragging by remember(riddle) { mutableStateOf<Int?>(null) }
    var grab by remember(riddle) { mutableStateOf(Offset.Zero) }
    var pointer by remember(riddle) { mutableStateOf(Offset.Zero) }
    var snapTarget by remember(riddle) { mutableStateOf<Int?>(null) }

    val woodBody = MatchstickColors.WoodBody
    val woodSide = MatchstickColors.WoodSide
    val headColor = MatchstickColors.woodHead(solved)
    // Sticks that can no longer be picked up (the move budget is spent) stay readable but lose the
    // vivid green head and are muted, so the still-movable stick stands out.
    val lockedBody = MatchstickColors.LockedBody
    val lockedSide = MatchstickColors.LockedSide
    val lockedHead = MatchstickColors.LockedHead
    val emptyColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
    val snapColor = Primary.copy(alpha = 0.45f)

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(riddle, solved) {
                    if (solved) return@pointerInput
                    detectDragGestures(
                        onDragStart = { offset ->
                            val t = boardTransform(size.width.toFloat(), size.height.toFloat(), riddle)
                            val (nx, ny) = t.toNorm(offset)
                            val occ = occupied.toList()
                            // The equals sign is fixed. Once the move budget is spent, sticks still in
                            // their start slots also lock; only already-moved sticks stay grabbable.
                            val locked = riddle.lockedSticks(occ.toSet())
                            val hit = occ.minByOrNull { pointToSegmentDistance(nx, ny, riddle.slots[it]) }
                            if (hit != null &&
                                hit !in locked &&
                                hit !in riddle.fixedSlots &&
                                pointToSegmentDistance(nx, ny, riddle.slots[hit]) <= HIT_THRESHOLD
                            ) {
                                dragging = hit
                                grab = offset
                                pointer = offset
                                snapTarget = hit
                            } else {
                                dragging = null
                                snapTarget = null
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val origin = dragging
                            if (origin != null) {
                                pointer = change.position
                                val t = boardTransform(size.width.toFloat(), size.height.toFloat(), riddle)
                                val (nx, ny) = t.toNorm(pointer)
                                val occ = occupied.toSet()
                                val nearest = riddle.slots.indices
                                    .filter { it == origin || it !in occ }
                                    .minByOrNull { pointToSegmentDistance(nx, ny, riddle.slots[it]) }
                                snapTarget = nearest?.takeIf {
                                    pointToSegmentDistance(nx, ny, riddle.slots[it]) <= SNAP_RADIUS
                                }
                            }
                        },
                        onDragEnd = {
                            val origin = dragging
                            val target = snapTarget
                            if (origin != null && target != null && target != origin) {
                                onMove(origin, target)
                            }
                            dragging = null
                            snapTarget = null
                        },
                        onDragCancel = {
                            dragging = null
                            snapTarget = null
                        },
                    )
                },
        ) {
            val t = boardTransform(size.width, size.height, riddle)
            val occ = occupied.toSet()
            val active = dragging
            val target = snapTarget

            // Faint outlines mark where a matchstick can be placed.
            riddle.slots.indices.forEach { i ->
                if (i !in occ && i != target) {
                    drawStickOutline(riddle.slots[i], t, emptyColor, strong = false)
                }
            }

            // Highlight the slot the dragged stick will snap into.
            if (target != null && target != active) {
                drawStickOutline(riddle.slots[target], t, snapColor, strong = true)
            }

            // Resting matchsticks (the dragged one is drawn separately on top). Sticks still in a
            // start slot are dimmed and unmovable once the move budget is spent.
            val lockedSticks = if (solved) emptySet() else riddle.lockedSticks(occ)
            occupied.forEach { i ->
                if (i != active) {
                    val locked = i in lockedSticks
                    val s = riddle.slots[i]
                    if (locked) {
                        drawStick(t.toPx(s.ax, s.ay), t.toPx(s.bx, s.by), t.scale, lockedBody, lockedSide, lockedHead)
                    } else {
                        drawStick(t.toPx(s.ax, s.ay), t.toPx(s.bx, s.by), t.scale, woodBody, woodSide, headColor)
                    }
                }
            }

            // The dragged matchstick: snapped onto the target, otherwise following the finger.
            if (active != null) {
                val s = riddle.slots[active]
                if (target != null) {
                    val tgt = riddle.slots[target]
                    drawStick(t.toPx(tgt.ax, tgt.ay), t.toPx(tgt.bx, tgt.by), t.scale, woodBody, woodSide, headColor)
                } else {
                    val delta = pointer - grab
                    drawStick(
                        t.toPx(s.ax, s.ay) + delta,
                        t.toPx(s.bx, s.by) + delta,
                        t.scale,
                        woodBody,
                        woodSide,
                        headColor,
                    )
                }
            }
        }
    }
}

/**
 * A prominent, glanceable budget readout sitting under the prompt: a big number for how many fresh
 * moves remain, paired with a row of matchstick pips that visually deplete (a vivid green-headed
 * stick turns into a spent grey one) as the player relocates sticks. The pips reuse the board's
 * movable/locked colors so "how many sticks can I move" reads at a glance, and the number flips to
 * red once the budget is spent.
 */
@Composable
private fun MovesIndicator(
    budget: Int,
    movesLeft: Int,
    modifier: Modifier = Modifier,
) {
    val numberColor by animateColorAsState(
        targetValue = if (movesLeft == 0) ErrorRed else Primary,
        label = "movesNumberColor",
    )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(start = 20.dp, end = 24.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = movesLeft.toString(),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = numberColor,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(budget) { i ->
                MatchstickPip(spent = i >= movesLeft)
            }
        }
    }
}

/** A single matchstick token used by [MovesIndicator]; greyed out once the move it stands for is spent. */
@Composable
private fun MatchstickPip(spent: Boolean) {
    val body by animateColorAsState(if (spent) MatchstickColors.PipSpentBody else MatchstickColors.WoodBody, label = "pipBody")
    val side by animateColorAsState(if (spent) MatchstickColors.PipSpentSide else MatchstickColors.WoodSide, label = "pipSide")
    val head by animateColorAsState(if (spent) MatchstickColors.PipSpentHead else MatchstickColors.WoodHead, label = "pipHead")
    Canvas(modifier = Modifier.size(width = 13.dp, height = 34.dp)) {
        val cx = size.width / 2f
        val top = Offset(cx, size.height * 0.16f)
        val bottom = Offset(cx, size.height * 0.95f)
        val w = size.width * 0.5f
        drawLine(side, top, bottom, strokeWidth = w * 1.3f, cap = StrokeCap.Round)
        drawLine(body, top, bottom, strokeWidth = w, cap = StrokeCap.Round)
        drawCircle(head, radius = w * 1.05f, center = top)
    }
}

@DevicePreviews
@Composable
private fun MatchstickRiddlesPlayScreenPreview() {
    ScreenPreviewHost {
        val storage = remember { UserStorage.forPreview() }
        MatchstickRiddlesPlayScreen(
            riddleId = MatchstickRiddles.all.first().id,
            storage = storage,
            onCompleted = {},
            onBack = {},
        )
    }
}
