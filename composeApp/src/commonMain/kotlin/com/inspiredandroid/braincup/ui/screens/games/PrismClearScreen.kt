package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.level_label
import braincup.composeapp.generated.resources.prism_clear_restart
import braincup.composeapp.generated.resources.prism_clear_stuck
import braincup.composeapp.generated.resources.prism_clear_undo
import com.inspiredandroid.braincup.app.PrismClearClearWave
import com.inspiredandroid.braincup.app.PrismClearUiState
import com.inspiredandroid.braincup.games.PrismTileType
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.LocalIsCompactHeight
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.Primary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private val CellGap = 3.dp
private const val SwapMillis = 200
private const val PopMillis = 240
private const val FallMillis = 280

/** Fraction of cell step required before a drag commits to an orthogonal neighbour. */
private const val DragThresholdFraction = 0.4f

/** Stable visual tile for absolute-positioned board animation. */
private data class VisualTile(
    val id: Long,
    val typeOrdinal: Int,
    val col: Int,
    val row: Int,
    val popping: Boolean = false,
)

@Composable
internal fun ColumnScope.PrismClearContent(
    uiState: PrismClearUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val rows = uiState.rows
    val cols = uiState.cols
    val compact = LocalIsCompactHeight.current
    val cellSize = cellSizeFor(rows = rows, cols = cols, compact = compact)
    val step = cellSize + CellGap
    val density = LocalDensity.current
    val stepPx = with(density) { step.toPx() }

    var tiles by remember { mutableStateOf<List<VisualTile>>(emptyList()) }
    var nextId by remember { mutableLongStateOf(1L) }
    var inputLocked by remember { mutableStateOf(false) }
    // Taps during swap/clear anim are queued and applied when the board settles.
    var pendingTapIndex by remember { mutableStateOf<Int?>(null) }

    fun handleCellTap(index: Int) {
        if (inputLocked) {
            pendingTapIndex = index
        } else {
            pendingTapIndex = null
            onAnswer("tap:$index")
        }
    }

    fun flushPendingTap() {
        val pending = pendingTapIndex ?: return
        pendingTapIndex = null
        onAnswer("tap:$pending")
    }

    // Drop a stale pending selection when the level changes.
    LaunchedEffect(uiState.level) {
        pendingTapIndex = null
    }

    // Deal / undo / restart: snap. Successful swap: slide tiles, then clear/fall waves.
    LaunchedEffect(uiState.boardEpoch, uiState.level, rows, cols) {
        val waves = uiState.clearWaves
        val hasSwap = uiState.swapFromIndex >= 0 &&
            uiState.swapToIndex >= 0 &&
            uiState.cellsBeforeSwap.isNotEmpty()

        if (!hasSwap && waves.isEmpty()) {
            var id = nextId
            tiles = buildTilesFromCells(uiState.cells, rows, cols) { id++ }
            nextId = id
            inputLocked = false
            flushPendingTap()
            return@LaunchedEffect
        }

        inputLocked = true

        if (hasSwap) {
            // Start from pre-swap board, then slide the two tiles into each other's cells.
            tiles = mergeIntoBoard(tiles, uiState.cellsBeforeSwap, rows, cols) { nextId++ }
            delay(16) // let composition pick up pre-swap positions
            tiles = applyVisualSwap(
                tiles = tiles,
                fromIndex = uiState.swapFromIndex,
                toIndex = uiState.swapToIndex,
                cols = cols,
            )
            delay(SwapMillis.toLong())
        }

        for (wave in waves) {
            playClearWave(
                wave = wave,
                rows = rows,
                cols = cols,
                currentTiles = { tiles },
                setTiles = { tiles = it },
                allocId = { nextId++ },
            )
        }
        // Ensure we end exactly on the settled engine board.
        var id = nextId
        tiles = buildTilesFromCells(uiState.cells, rows, cols) { id++ }
        nextId = id
        inputLocked = false
        // Apply any cell the player tapped mid-animation (selection survives the anim).
        flushPendingTap()
    }

    val boardWidth = step * cols - CellGap
    val boardHeight = step * rows - CellGap
    // Prefer a mid-animation queued selection over the engine selection (cleared on swap).
    val highlightedIndex = pendingTapIndex ?: uiState.selectedIndex

    // Fresh values for the board gesture loop without restarting pointerInput every frame.
    val inputLockedState = rememberUpdatedState(inputLocked)
    val cellsState = rememberUpdatedState(uiState.cells)
    val onAnswerState = rememberUpdatedState(onAnswer)
    val onTapState = rememberUpdatedState { index: Int -> handleCellTap(index) }
    val dragThresholdPx = stepPx * DragThresholdFraction

    val board: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(boardWidth, boardHeight)
                .hoverHand(),
        ) {
            // Slot chrome under tiles.
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val index = row * cols + col
                    val occupied = tiles.any { !it.popping && it.row == row && it.col == col }
                    PrismTile(
                        face = if (occupied) {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        } else {
                            // Near-invisible empty slots still show selection chrome when needed.
                            MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                                alpha = if (highlightedIndex == index) 1f else 0.01f,
                            )
                        },
                        modifier = Modifier
                            .offset(x = step * col, y = step * row)
                            .size(cellSize),
                        isSelected = !occupied && highlightedIndex == index,
                        isClickable = false,
                        onClick = {},
                    ) {}
                }
            }
            for (tile in tiles) {
                key(tile.id) {
                    AnimatedPrismClearTile(
                        tile = tile,
                        cellSize = cellSize,
                        stepPx = stepPx,
                        isSelected = !tile.popping &&
                            highlightedIndex == tile.row * cols + tile.col,
                    )
                }
            }
            // Full-board input layer on top so tap + drag are not stolen by tile clickables.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(rows, cols, stepPx, dragThresholdPx) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            val from = prismClearCellIndexAt(
                                x = down.position.x,
                                y = down.position.y,
                                stepPx = stepPx,
                                rows = rows,
                                cols = cols,
                            ) ?: return@awaitEachGesture

                            var totalDx = 0f
                            var totalDy = 0f
                            var swapped = false

                            // Accumulate drag; commit once to an orthogonal neighbour past threshold.
                            drag(down.id) { change ->
                                val delta = change.positionChange()
                                change.consume()
                                totalDx += delta.x
                                totalDy += delta.y
                                if (swapped || inputLockedState.value) return@drag

                                val to = prismClearNeighbourInDragDirection(
                                    from = from,
                                    dx = totalDx,
                                    dy = totalDy,
                                    thresholdPx = dragThresholdPx,
                                    rows = rows,
                                    cols = cols,
                                ) ?: return@drag

                                val cells = cellsState.value
                                if (from in cells.indices &&
                                    to in cells.indices &&
                                    cells[from] != null &&
                                    cells[to] != null &&
                                    !inputLockedState.value
                                ) {
                                    // Drag commits go straight to trySwap; skip while animating.
                                    onAnswerState.value("swap:$from,$to")
                                    swapped = true
                                }
                            }

                            // No committed drag-swap → treat as tap (select / deselect / swap).
                            if (!swapped) {
                                onTapState.value(from)
                            }
                        }
                    },
            )
        }
    }

    val actions: @Composable () -> Unit = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(
                // Stay visually enabled for the whole turn once a move exists; only the first
                // pre-move state is dimmed. Clicks during clear/swap anim still wait on inputLock.
                onClick = { if (!inputLocked && uiState.canUndo) onAnswer("undo") },
                value = stringResource(Res.string.prism_clear_undo),
                face = if (uiState.canUndo) {
                    Primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                },
            )
            DefaultButton(
                onClick = { if (!inputLocked) onAnswer("restart") },
                value = stringResource(Res.string.prism_clear_restart),
            )
        }
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.level_label, uiState.level),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (uiState.stuck) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(Res.string.prism_clear_stuck),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(8.dp))
                actions()
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
        if (uiState.stuck) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.prism_clear_stuck),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            board()
        }
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            actions()
        }
    }
}

/** Exchange row/col of the two tiles sitting on [fromIndex] and [toIndex]. */
private fun applyVisualSwap(
    tiles: List<VisualTile>,
    fromIndex: Int,
    toIndex: Int,
    cols: Int,
): List<VisualTile> {
    val fr = fromIndex / cols
    val fc = fromIndex % cols
    val tr = toIndex / cols
    val tc = toIndex % cols
    return tiles.map { tile ->
        when {
            tile.row == fr && tile.col == fc -> tile.copy(row = tr, col = tc)
            tile.row == tr && tile.col == tc -> tile.copy(row = fr, col = fc)
            else -> tile
        }
    }
}

/**
 * Play one cascade wave: show board with matches → pop cleared tiles → gravity settle.
 */
private suspend fun playClearWave(
    wave: PrismClearClearWave,
    rows: Int,
    cols: Int,
    currentTiles: () -> List<VisualTile>,
    setTiles: (List<VisualTile>) -> Unit,
    allocId: () -> Long,
) {
    // Align display to the pre-clear board, preserving tile ids where types match by cell.
    val before = mergeIntoBoard(currentTiles(), wave.cellsBeforeClear, rows, cols, allocId)
    val clearedSet = wave.clearedIndices.toSet()
    setTiles(
        before.map { tile ->
            val index = tile.row * cols + tile.col
            if (index in clearedSet) tile.copy(popping = true) else tile
        },
    )
    delay(PopMillis.toLong())

    // Survivors after pop, then reassign rows from the post-gravity board (column match).
    val survivors = before.filter { (it.row * cols + it.col) !in clearedSet }
    val fallen = settleAfterGravity(survivors, wave.cellsAfterGravity, rows, cols, allocId)
    setTiles(fallen)
    delay(FallMillis.toLong())
}

/**
 * Rebuild visual tiles for [targetCells], reusing ids from [previous] when the same type
 * occupies the same cell (swap / non-moved). Other tiles get new ids.
 */
private fun mergeIntoBoard(
    previous: List<VisualTile>,
    targetCells: ImmutableList<Int?>,
    rows: Int,
    cols: Int,
    allocId: () -> Long,
): List<VisualTile> {
    val byCell = previous.filter { !it.popping }.associateBy { it.row * cols + it.col }
    val out = ArrayList<VisualTile>()
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val type = targetCells[row * cols + col] ?: continue
            val index = row * cols + col
            val prev = byCell[index]
            val id = if (prev != null && prev.typeOrdinal == type) prev.id else allocId()
            out.add(VisualTile(id = id, typeOrdinal = type, col = col, row = row))
        }
    }
    return out
}

/**
 * After clears, rematch survivors within each column (bottom-up) onto the post-gravity stack
 * so tiles fall smoothly rather than teleporting.
 */
private fun settleAfterGravity(
    survivors: List<VisualTile>,
    afterCells: ImmutableList<Int?>,
    rows: Int,
    cols: Int,
    allocId: () -> Long,
): List<VisualTile> {
    val result = ArrayList<VisualTile>()
    for (col in 0 until cols) {
        val oldStack = survivors
            .filter { it.col == col }
            .sortedByDescending { it.row } // bottom → top
        val newStack = (rows - 1 downTo 0).mapNotNull { r ->
            val t = afterCells[r * cols + col] ?: return@mapNotNull null
            r to t
        }
        var oi = 0
        var ni = 0
        while (oi < oldStack.size && ni < newStack.size) {
            val old = oldStack[oi]
            val (newRow, newType) = newStack[ni]
            if (old.typeOrdinal == newType) {
                result.add(old.copy(row = newRow, popping = false))
                oi++
                ni++
            } else {
                // Shouldn't remain after a proper clear; drop orphan.
                oi++
            }
        }
        while (ni < newStack.size) {
            val (newRow, newType) = newStack[ni]
            result.add(
                VisualTile(
                    id = allocId(),
                    typeOrdinal = newType,
                    col = col,
                    row = newRow,
                ),
            )
            ni++
        }
    }
    return result
}

@Composable
private fun AnimatedPrismClearTile(
    tile: VisualTile,
    cellSize: Dp,
    stepPx: Float,
    isSelected: Boolean,
) {
    val targetX = tile.col * stepPx
    val targetY = tile.row * stepPx
    val x = remember(tile.id) { Animatable(targetX) }
    val y = remember(tile.id) { Animatable(targetY) }

    LaunchedEffect(tile.col, tile.row, tile.popping) {
        if (tile.popping) return@LaunchedEffect
        // Animate both axes so adjacent swaps slide horizontally/vertically, and gravity falls.
        val movingBoth = x.value != targetX && y.value != targetY
        val duration = if (x.value != targetX || y.value != targetY) {
            // Swaps are short; pure vertical falls use FallMillis.
            if (x.value != targetX) SwapMillis else FallMillis
        } else {
            FallMillis
        }
        val spec = tween<Float>(duration, easing = FastOutSlowInEasing)
        if (movingBoth) {
            launch { x.animateTo(targetX, spec) }
            y.animateTo(targetY, spec)
        } else {
            if (x.value != targetX) x.animateTo(targetX, spec) else x.snapTo(targetX)
            if (y.value != targetY) y.animateTo(targetY, spec) else y.snapTo(targetY)
        }
    }

    val popScale = remember(tile.id) { Animatable(1f) }
    val popAlpha = remember(tile.id) { Animatable(1f) }
    LaunchedEffect(tile.popping) {
        if (!tile.popping) {
            popScale.snapTo(1f)
            popAlpha.snapTo(1f)
            return@LaunchedEffect
        }
        // Punch outward, then shrink + fade (candy-crush style pop).
        launch {
            popScale.animateTo(1.28f, tween(80, easing = FastOutSlowInEasing))
            popScale.animateTo(0.1f, tween(PopMillis - 80, easing = FastOutSlowInEasing))
        }
        launch {
            delay(50)
            popAlpha.animateTo(0f, tween(PopMillis - 50))
        }
    }

    val selectScale by animateFloatAsState(
        targetValue = if (isSelected) 1.06f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "selectScale",
    )

    val face = PrismTileType.entries[tile.typeOrdinal].color.composeColor()
    // Brighten slightly while popping for a flash.
    val flashBoost by animateFloatAsState(
        targetValue = if (tile.popping) 0.35f else 0f,
        animationSpec = tween(80),
        label = "flash",
    )
    val displayFace = face.copy(
        red = (face.red + (1f - face.red) * flashBoost).coerceIn(0f, 1f),
        green = (face.green + (1f - face.green) * flashBoost).coerceIn(0f, 1f),
        blue = (face.blue + (1f - face.blue) * flashBoost).coerceIn(0f, 1f),
    )

    Box(
        modifier = Modifier
            .offset { IntOffset(x.value.roundToInt(), y.value.roundToInt()) }
            .size(cellSize)
            .graphicsLayer {
                val s = popScale.value * selectScale
                scaleX = s
                scaleY = s
                alpha = popAlpha.value
            },
    ) {
        PrismTile(
            face = displayFace,
            modifier = Modifier.size(cellSize),
            isSelected = isSelected,
            // Board-level pointerInput owns tap + drag so clickable does not steal gestures.
            isClickable = false,
            onClick = {},
        ) {}
    }
}

private fun cellSizeFor(rows: Int, cols: Int, compact: Boolean): Dp {
    val longest = maxOf(rows, cols)
    return when {
        compact && longest <= 4 -> 52.dp
        compact && longest <= 5 -> 44.dp
        compact && longest <= 6 -> 38.dp
        compact -> 32.dp
        longest <= 4 -> 64.dp
        longest <= 5 -> 56.dp
        longest <= 6 -> 48.dp
        else -> 40.dp
    }
}

private fun buildTilesFromCells(
    cells: ImmutableList<Int?>,
    rows: Int,
    cols: Int,
    nextId: () -> Long,
): List<VisualTile> {
    val out = ArrayList<VisualTile>()
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val t = cells[row * cols + col] ?: continue
            out.add(
                VisualTile(
                    id = nextId(),
                    typeOrdinal = t,
                    col = col,
                    row = row,
                ),
            )
        }
    }
    return out
}

@DevicePreviews
@Composable
private fun PrismClearContentPreview() {
    val rows = 3
    val cols = 6
    val cells = MutableList<Int?>(rows * cols) { null }
    cells[1 * cols + 0] = 0
    cells[1 * cols + 1] = 2
    cells[1 * cols + 2] = 2
    cells[2 * cols + 0] = 2
    cells[2 * cols + 1] = 0
    cells[2 * cols + 2] = 1
    cells[2 * cols + 3] = 0
    cells[2 * cols + 4] = 1
    cells[2 * cols + 5] = 1
    GamePreviewHost {
        PrismClearContent(
            uiState = PrismClearUiState(
                rows = rows,
                cols = cols,
                cells = persistentListOf(*cells.toTypedArray()),
                selectedIndex = 1 * cols,
                movesUsed = 0,
                level = 1,
                stuck = false,
                canUndo = true,
            ),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
