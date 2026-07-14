package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.level_label
import braincup.composeapp.generated.resources.moves_label
import braincup.composeapp.generated.resources.tower_of_hanoi_invalid_move
import com.inspiredandroid.braincup.app.TowerOfHanoiUiState
import com.inspiredandroid.braincup.games.TowerOfHanoiGame
import com.inspiredandroid.braincup.ui.components.GiveUpButton
import com.inspiredandroid.braincup.ui.components.LocalIsCompactHeight
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.HanoiBaseColor
import com.inspiredandroid.braincup.ui.theme.HanoiDiskColors
import com.inspiredandroid.braincup.ui.theme.HanoiPegColor
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.math.roundToInt

private val PegGap = 8.dp
private val BaseHeight = 10.dp
private val DiskGap = 2.dp
private val PegPadH = 4.dp
private val PegPadV = 6.dp
private val PoleWidth = 8.dp

/** How high a flying disk rises above the board top during a cross-peg move. */
private val LiftClearance = 4.dp

private const val LiftMillis = 90
private const val SlideMillis = 120
private const val SelectMillis = 100
private const val RejectFlashMillis = 420L

@Composable
internal fun ColumnScope.TowerOfHanoiContent(
    uiState: TowerOfHanoiUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val boardHeight = if (compact) 160.dp else 240.dp
    val pegWidth = if (compact) 88.dp else 108.dp

    var showInvalidMessage by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.rejectNonce) {
        if (uiState.rejectNonce <= 0) {
            showInvalidMessage = false
            return@LaunchedEffect
        }
        showInvalidMessage = true
        delay(RejectFlashMillis)
        showInvalidMessage = false
    }

    val invalidMessage: @Composable () -> Unit = {
        AnimatedVisibility(
            visible = showInvalidMessage,
            enter = fadeIn(tween(120)),
            exit = fadeOut(tween(220)),
        ) {
            Text(
                text = stringResource(Res.string.tower_of_hanoi_invalid_move),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(max = 220.dp)
                    .padding(horizontal = 4.dp),
            )
        }
    }

    val board: @Composable () -> Unit = {
        HanoiBoard(
            pegs = uiState.pegs,
            diskCount = uiState.diskCount,
            selectedPeg = uiState.selectedPeg,
            rejectedPeg = uiState.rejectedPeg,
            rejectFromPeg = uiState.rejectFromPeg,
            rejectNonce = uiState.rejectNonce,
            pegWidth = pegWidth,
            boardHeight = boardHeight,
            onPegClick = { peg -> onAnswer(peg.toString()) },
        )
    }

    if (compact) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            board()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.level_label, uiState.level),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.moves_label, uiState.moves),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                invalidMessage()
                Spacer(Modifier.height(8.dp))
                GiveUpButton(onGiveUp = onGiveUp)
            }
        }
    } else {
        Text(
            text = stringResource(Res.string.level_label, uiState.level),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.moves_label, uiState.moves),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            board()
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            invalidMessage()
        }
        Spacer(Modifier.height(8.dp))
        GiveUpButton(
            onGiveUp = onGiveUp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

/**
 * Absolute-layout board: peg chrome underneath, disks drawn on top with per-disk X/Y
 * [Animatable]s. Cross-peg moves use a lift → slide → drop path; selection gently lifts the
 * top disk and tints its peg. Illegal drops flash the target peg, nudge+shake the held disk
 * back, and highlight the blocking disk.
 */
@Composable
private fun HanoiBoard(
    pegs: List<List<Int>>,
    diskCount: Int,
    selectedPeg: Int?,
    rejectedPeg: Int?,
    rejectFromPeg: Int?,
    rejectNonce: Int,
    pegWidth: Dp,
    boardHeight: Dp,
    onPegClick: (Int) -> Unit,
) {
    val density = LocalDensity.current
    val selectionColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val idlePegFace = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.35f)

    val totalWidth = pegWidth * TowerOfHanoiGame.PEG_COUNT + PegGap * (TowerOfHanoiGame.PEG_COUNT - 1)
    val diskHeight = ((boardHeight - 20.dp) / (diskCount + 1).coerceAtLeast(1)).coerceIn(12.dp, 28.dp)
    val maxDiskWidth = pegWidth - 8.dp
    val minDiskWidth = pegWidth * 0.35f
    val selectLift = diskHeight * 0.55f

    val targets = remember(pegs) {
        buildMap {
            pegs.forEachIndexed { peg, stack ->
                stack.forEachIndexed { index, size ->
                    put(size, peg to index)
                }
            }
        }
    }

    val xAnim = remember { mutableStateMapOf<Int, Animatable<Float, AnimationVector1D>>() }
    val yAnim = remember { mutableStateMapOf<Int, Animatable<Float, AnimationVector1D>>() }
    val initialized = remember { mutableStateMapOf<Int, Boolean>() }

    // Which peg/disk should show reject styling right now (UI-local so it can auto-clear).
    var flashPeg by remember { mutableIntStateOf(-1) }
    var flashBlockDisk by remember { mutableIntStateOf(-1) }
    var rejectPulse by remember { mutableStateOf(false) }
    // Lets the reject bounce own the disk's motion for one nonce (avoids deselect settle racing it).
    var handledRejectNonce by remember { mutableIntStateOf(0) }

    LaunchedEffect(diskCount) {
        val live = (1..diskCount).toSet()
        xAnim.keys.filter { it !in live }.forEach { xAnim.remove(it) }
        yAnim.keys.filter { it !in live }.forEach { yAnim.remove(it) }
        initialized.keys.filter { it !in live }.forEach { initialized.remove(it) }
    }

    fun diskWidth(size: Int): Dp {
        val fraction = if (diskCount <= 1) {
            1f
        } else {
            (size - 1).toFloat() / (diskCount - 1).toFloat()
        }
        return minDiskWidth + (maxDiskWidth - minDiskWidth) * fraction
    }

    fun restOffsetPx(peg: Int, stackIndex: Int, size: Int, lifted: Boolean): Pair<Float, Float> {
        val width = diskWidth(size)
        val pegLeft = (pegWidth + PegGap) * peg
        val x = with(density) { (pegLeft + (pegWidth - width) / 2f).toPx() }
        val stackBottom = boardHeight - BaseHeight - PegPadV
        val yBase = stackBottom - diskHeight * (stackIndex + 1) - DiskGap * stackIndex
        val y = with(density) { (if (lifted) yBase - selectLift else yBase).toPx() }
        return x to y
    }

    val liftYPx = with(density) { LiftClearance.toPx() }
    val pegStepPx = with(density) { (pegWidth + PegGap).toPx() }
    val shakeAmpPx = with(density) { 11.dp.toPx() }

    // Reject feedback: nudge toward the illegal target, shake, spring home; flash peg + blocker.
    // Selection is already cleared; animate from [rejectFromPeg]'s top disk (unlifted rest).
    LaunchedEffect(rejectNonce) {
        if (rejectNonce <= 0 || rejectedPeg == null || rejectFromPeg == null) return@LaunchedEffect
        val sourcePeg = rejectFromPeg
        val heldSize = pegs.getOrNull(sourcePeg)?.lastOrNull() ?: return@LaunchedEffect
        val blockDisk = pegs.getOrNull(rejectedPeg)?.lastOrNull() ?: -1
        val x = xAnim[heldSize] ?: return@LaunchedEffect
        val y = yAnim[heldSize] ?: return@LaunchedEffect
        val stackIndex = pegs[sourcePeg].lastIndex
        // Home is the resting (unselected) slot; start from current lifted pose if still mid-lift.
        val (homeX, homeY) = restOffsetPx(sourcePeg, stackIndex, heldSize, lifted = false)
        val (targetX, _) = restOffsetPx(
            peg = rejectedPeg,
            stackIndex = pegs[rejectedPeg].size,
            size = heldSize,
            lifted = false,
        )
        val midX = homeX + (targetX - homeX) * 0.55f

        flashPeg = rejectedPeg
        flashBlockDisk = blockDisk
        rejectPulse = true

        // Partial attempt toward the illegal peg, then shake + retreat.
        y.animateTo(liftYPx, animationSpec = tween(50, easing = FastOutSlowInEasing))
        x.animateTo(midX, animationSpec = tween(70, easing = FastOutSlowInEasing))
        x.animateTo(midX + shakeAmpPx, animationSpec = tween(28))
        x.animateTo(midX - shakeAmpPx, animationSpec = tween(30))
        x.animateTo(midX + shakeAmpPx * 0.55f, animationSpec = tween(28))
        coroutineScope {
            launch {
                x.animateTo(
                    homeX,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                )
            }
            launch {
                y.animateTo(
                    homeY,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                )
            }
        }

        delay(RejectFlashMillis - 200)
        rejectPulse = false
        flashPeg = -1
        flashBlockDisk = -1
        handledRejectNonce = rejectNonce
    }

    Box(
        modifier = Modifier
            .width(totalWidth)
            .height(boardHeight),
    ) {
        Row(
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.spacedBy(PegGap),
            verticalAlignment = Alignment.Bottom,
        ) {
            for (pegIndex in 0 until TowerOfHanoiGame.PEG_COUNT) {
                HanoiPegChrome(
                    selected = selectedPeg == pegIndex,
                    rejected = flashPeg == pegIndex,
                    selectionColor = selectionColor,
                    errorColor = errorColor,
                    idleFace = idlePegFace,
                    width = pegWidth,
                    height = boardHeight,
                    onClick = { onPegClick(pegIndex) },
                )
            }
        }

        for (size in 1..diskCount) {
            val target = targets[size] ?: continue
            val (peg, stackIndex) = target
            val isTopOfSelected = selectedPeg == peg &&
                pegs.getOrNull(peg)?.lastOrNull() == size
            val isBlocking = flashBlockDisk == size
            val width = diskWidth(size)

            key(size) {
                val x = xAnim.getOrPut(size) {
                    val (ix, _) = restOffsetPx(peg, stackIndex, size, lifted = false)
                    Animatable(ix)
                }
                val y = yAnim.getOrPut(size) {
                    val (_, iy) = restOffsetPx(peg, stackIndex, size, lifted = false)
                    Animatable(iy)
                }

                LaunchedEffect(peg, stackIndex, isTopOfSelected, diskCount, pegWidth, boardHeight, rejectNonce) {
                    // Reject bounce owns this disk until handledRejectNonce catches up.
                    if (rejectNonce > handledRejectNonce &&
                        rejectFromPeg == peg &&
                        pegs.getOrNull(peg)?.lastOrNull() == size
                    ) {
                        return@LaunchedEffect
                    }

                    val (tx, ty) = restOffsetPx(peg, stackIndex, size, lifted = isTopOfSelected)
                    if (initialized[size] != true) {
                        x.snapTo(tx)
                        y.snapTo(ty)
                        initialized[size] = true
                        return@LaunchedEffect
                    }

                    val dx = tx - x.value
                    val dy = ty - y.value
                    if (abs(dx) < 0.5f && abs(dy) < 0.5f) return@LaunchedEffect

                    val crossedPeg = abs(dx) > pegStepPx * 0.45f
                    if (crossedPeg) {
                        y.animateTo(
                            liftYPx,
                            animationSpec = tween(LiftMillis, easing = FastOutSlowInEasing),
                        )
                        x.animateTo(
                            tx,
                            animationSpec = tween(SlideMillis, easing = FastOutSlowInEasing),
                        )
                        y.animateTo(
                            ty,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium,
                            ),
                        )
                    } else {
                        coroutineScope {
                            launch {
                                x.animateTo(
                                    tx,
                                    animationSpec = tween(SelectMillis, easing = FastOutSlowInEasing),
                                )
                            }
                            launch {
                                y.animateTo(
                                    ty,
                                    animationSpec = tween(SelectMillis, easing = FastOutSlowInEasing),
                                )
                            }
                        }
                    }
                }

                val elev by animateFloatAsState(
                    targetValue = when {
                        isTopOfSelected -> 6f
                        isBlocking -> 5f
                        else -> 2f
                    },
                    animationSpec = tween(SelectMillis),
                    label = "diskElev$size",
                )
                val scale by animateFloatAsState(
                    targetValue = when {
                        isTopOfSelected -> 1.04f
                        isBlocking -> 1.06f
                        else -> 1f
                    },
                    animationSpec = tween(SelectMillis, easing = FastOutSlowInEasing),
                    label = "diskScale$size",
                )
                val diskFace by animateColorAsState(
                    targetValue = if (isBlocking) {
                        // Blend disk color toward error so the "too small" blocker is obvious.
                        lerpColor(hanoiDiskColor(size), errorColor, 0.55f)
                    } else {
                        hanoiDiskColor(size)
                    },
                    animationSpec = tween(80),
                    label = "diskFace$size",
                )

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(x.value.roundToInt(), y.value.roundToInt())
                        }
                        .size(width = width, height = diskHeight)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .shadow(
                            elevation = elev.dp,
                            shape = RoundedCornerShape(diskHeight / 2),
                            clip = false,
                        )
                        .then(
                            if (isBlocking) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = errorColor,
                                    shape = RoundedCornerShape(diskHeight / 2),
                                )
                            } else {
                                Modifier
                            },
                        )
                        .clip(RoundedCornerShape(diskHeight / 2))
                        .background(diskFace),
                )
            }
        }
    }
}

@Composable
private fun HanoiPegChrome(
    selected: Boolean,
    rejected: Boolean,
    selectionColor: Color,
    errorColor: Color,
    idleFace: Color,
    width: Dp,
    height: Dp,
    onClick: () -> Unit,
) {
    val face by animateColorAsState(
        targetValue = when {
            rejected -> errorColor.copy(alpha = 0.28f)
            selected -> selectionColor.copy(alpha = 0.18f)
            else -> idleFace
        },
        animationSpec = tween(SelectMillis),
        label = "pegFace",
    )
    val poleColor by animateColorAsState(
        targetValue = when {
            rejected -> errorColor
            selected -> selectionColor
            else -> HanoiPegColor
        },
        animationSpec = tween(SelectMillis),
        label = "pegPole",
    )
    val baseColor by animateColorAsState(
        targetValue = when {
            rejected -> errorColor
            selected -> selectionColor
            else -> HanoiBaseColor
        },
        animationSpec = tween(SelectMillis),
        label = "pegBase",
    )

    Column(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(face)
            .then(
                if (rejected) {
                    Modifier.border(2.dp, errorColor, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                },
            )
            .hoverHand()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = PegPadH, vertical = PegPadV),
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
                    .width(PoleWidth)
                    .fillMaxHeight(0.92f)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(PoleWidth / 2))
                    .background(poleColor),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(BaseHeight)
                .clip(RoundedCornerShape(4.dp))
                .background(baseColor),
        )
    }
}

private fun hanoiDiskColor(size: Int): Color {
    val index = (size - 1).coerceIn(0, HanoiDiskColors.lastIndex)
    return HanoiDiskColors[index]
}

private fun lerpColor(a: Color, b: Color, t: Float): Color {
    val u = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * u,
        green = a.green + (b.green - a.green) * u,
        blue = a.blue + (b.blue - a.blue) * u,
        alpha = a.alpha + (b.alpha - a.alpha) * u,
    )
}

@DevicePreviews
@Composable
private fun TowerOfHanoiContentPreview() {
    GamePreviewHost {
        TowerOfHanoiContent(
            uiState = TowerOfHanoiUiState(
                diskCount = 3,
                pegs = persistentListOf(
                    persistentListOf(3, 2),
                    persistentListOf(1),
                    persistentListOf(),
                ),
                selectedPeg = null,
                rejectedPeg = 1,
                rejectFromPeg = 0,
                rejectNonce = 1,
                moves = 1,
                level = 1,
            ),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
