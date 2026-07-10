package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.ic_pencil_notes
import braincup.composeapp.generated.resources.normal_sudoku_erase
import braincup.composeapp.generated.resources.normal_sudoku_not_solved
import braincup.composeapp.generated.resources.normal_sudoku_notes_mode
import braincup.composeapp.generated.resources.normal_sudoku_notes_toggle
import braincup.composeapp.generated.resources.normal_sudoku_pen_mode
import braincup.composeapp.generated.resources.normal_sudoku_solved
import braincup.composeapp.generated.resources.normal_sudoku_title
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.normalsudoku.NormalSudokuPuzzles
import com.inspiredandroid.braincup.normalsudoku.NoteMask
import com.inspiredandroid.braincup.normalsudoku.autoEliminateNote
import com.inspiredandroid.braincup.normalsudoku.decodeSudokuNotes
import com.inspiredandroid.braincup.normalsudoku.emptySudokuNotes
import com.inspiredandroid.braincup.normalsudoku.encodeSudokuNotes
import com.inspiredandroid.braincup.normalsudoku.noteMaskHas
import com.inspiredandroid.braincup.normalsudoku.noteMaskToText
import com.inspiredandroid.braincup.normalsudoku.noteMaskToggle
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val GRID = 9
private const val BLOCK = 3
private val CellSeparator = 1.dp
private val PadGap = 6.dp

@Composable
fun NormalSudokuPlayScreen(
    puzzleId: String,
    storage: UserStorage,
    onCompleted: () -> Unit,
    onBack: () -> Unit,
) {
    val puzzle = remember(puzzleId) { NormalSudokuPuzzles.byId(puzzleId) }
    if (puzzle == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val clueDigits = remember(puzzle) { puzzle.clues.map { it.digitToInt() } }
    val initial = remember(puzzle) {
        val saved = storage.getNormalSudokuProgress(puzzle.id)
        val source = saved ?: puzzle.clues
        MutableList(81) { source[it].digitToInt() }
    }
    val board = remember(puzzle) { mutableStateListOf<Int>().apply { addAll(initial) } }
    val notes = remember(puzzle) {
        mutableStateListOf<NoteMask>().apply {
            val savedNotes = storage.getNormalSudokuNotes(puzzle.id)
            addAll(decodeSudokuNotes(savedNotes ?: "") ?: emptySudokuNotes())
        }
    }
    var selectedIndex by remember(puzzle) {
        val firstEmpty = clueDigits.indexOfFirst { it == 0 }
        mutableStateOf(if (firstEmpty == -1) 0 else firstEmpty)
    }
    var notesMode by remember(puzzle) { mutableStateOf(false) }
    var lastWrongFlash by remember(puzzle) { mutableStateOf(0L) }
    var solved by remember(puzzle) { mutableStateOf(false) }
    var xpGained by remember(puzzle) { mutableStateOf(0) }

    fun persist() {
        if (solved) return
        storage.saveNormalSudokuProgress(puzzle.id, board.joinToString("") { it.toString() })
        storage.saveNormalSudokuNotes(puzzle.id, encodeSudokuNotes(notes))
    }

    fun toggleNote(pos: Int, digit: Int) {
        if (solved || digit !in 1..9) return
        if (pos !in board.indices || clueDigits[pos] != 0 || board[pos] != 0) return
        notes[pos] = noteMaskToggle(notes[pos], digit)
        persist()
    }

    fun placeDigit(pos: Int, digit: Int) {
        if (solved) return
        if (pos !in board.indices) return
        if (clueDigits[pos] != 0) return
        board[pos] = digit
        notes[pos] = 0
        if (digit in 1..9) autoEliminateNote(notes, digit, pos)
        persist()
        if (board.none { it == 0 }) {
            val asString = board.joinToString("") { it.toString() }
            if (asString == puzzle.solution) {
                xpGained = storage.awardNormalSudokuCompletionXp(puzzle.id, puzzle.difficulty).xpGained
                solved = true
                storage.markNormalSudokuCompleted(puzzle.id, puzzle.difficulty)
            } else {
                lastWrongFlash = currentTimeMillis()
            }
        } else {
            selectedIndex = nextEmpty(pos, board, clueDigits)
        }
    }

    fun applyDigit(digit: Int) {
        if (notesMode) toggleNote(selectedIndex, digit) else placeDigit(selectedIndex, digit)
    }

    fun applyErase() {
        if (solved) return
        val pos = selectedIndex
        if (pos !in board.indices || clueDigits[pos] != 0) return
        board[pos] = 0
        notes[pos] = 0
        persist()
    }

    LaunchedEffect(solved) {
        if (solved) {
            delay(if (xpGained > 0) 2200 else 1200)
            onCompleted()
        }
    }

    AppScaffold(
        title = stringResource(Res.string.normal_sudoku_title),
        onBack = onBack,
        scrollable = false,
        actions = {
            NotesModeToggle(
                notesMode = notesMode,
                enabled = !solved,
                onToggle = { notesMode = !notesMode },
            )
        },
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val landscape = maxWidth > maxHeight && maxHeight < 600.dp
            val compact = maxWidth < 400.dp || maxHeight < 600.dp
            val blockSep = if (compact) 8.dp else 12.dp
            val outerFrame = if (compact) 6.dp else 8.dp
            val screenPadding = if (compact) 6.dp else 8.dp

            val onCellClick = remember(solved, clueDigits) {
                { idx: Int ->
                    if (!solved && clueDigits[idx] == 0) selectedIndex = idx
                }
            }
            val onDigit = remember { { digit: Int -> applyDigit(digit) } }
            val onErase = remember { { applyErase() } }

            if (landscape) {
                val boardSize = minOf(
                    maxHeight - screenPadding * 2,
                    maxWidth * 0.55f - screenPadding * 2,
                )
                val padWidth = minOf(
                    maxWidth - boardSize - screenPadding * 2 - 16.dp,
                    boardSize * 0.55f,
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenPadding),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SudokuBoard9x9(
                        board = board,
                        notes = notes,
                        clueDigits = clueDigits,
                        selectedIndex = selectedIndex,
                        solved = solved,
                        outerFrame = outerFrame,
                        blockSep = blockSep,
                        modifier = Modifier.size(boardSize),
                        onCellClick = onCellClick,
                    )
                    DigitPad(
                        columns = 3,
                        board = board,
                        notesMode = notesMode,
                        selectedNotes = notes.getOrElse(selectedIndex) { 0 },
                        enabled = !solved,
                        modifier = Modifier.width(padWidth),
                        onDigit = onDigit,
                        onErase = onErase,
                    )
                }
            } else {
                val boardSize = minOf(
                    maxWidth - screenPadding * 2,
                    maxHeight * 0.62f,
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                ) {
                    SudokuBoard9x9(
                        board = board,
                        notes = notes,
                        clueDigits = clueDigits,
                        selectedIndex = selectedIndex,
                        solved = solved,
                        outerFrame = outerFrame,
                        blockSep = blockSep,
                        modifier = Modifier.size(boardSize),
                        onCellClick = onCellClick,
                    )
                    DigitPad(
                        columns = 9,
                        board = board,
                        notesMode = notesMode,
                        selectedNotes = notes.getOrElse(selectedIndex) { 0 },
                        enabled = !solved,
                        modifier = Modifier.width(boardSize),
                        onDigit = onDigit,
                        onErase = onErase,
                    )
                }
            }
        }

        StatusBanner(solved = solved, wrongFlashKey = lastWrongFlash, xpGained = xpGained)
    }
}

@Composable
private fun NotesModeToggle(
    notesMode: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit,
) {
    val label = if (notesMode) {
        stringResource(Res.string.normal_sudoku_notes_mode)
    } else {
        stringResource(Res.string.normal_sudoku_pen_mode)
    }
    val face = if (notesMode) PrimaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (notesMode) OnPrimaryContainer else MaterialTheme.colorScheme.onSurface
    PrismTile(
        face = face,
        modifier = Modifier
            .padding(end = 8.dp)
            .hoverHand(enabled)
            .alpha(if (enabled) 1f else 0.6f),
        isClickable = enabled,
        isSelected = notesMode,
        onClick = onToggle,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_pencil_notes),
                contentDescription = stringResource(Res.string.normal_sudoku_notes_toggle),
                colorFilter = ColorFilter.tint(contentColor),
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
            )
        }
    }
}

@Composable
private fun SudokuBoard9x9(
    board: List<Int>,
    notes: List<NoteMask>,
    clueDigits: List<Int>,
    selectedIndex: Int,
    solved: Boolean,
    outerFrame: Dp,
    blockSep: Dp,
    modifier: Modifier = Modifier,
    onCellClick: (Int) -> Unit,
) {
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    // Measure the board once and derive a uniform cell size so 81 cells do not each run
    // BoxWithConstraints. Block separators stay thicker than cell separators.
    BoxWithConstraints(modifier = modifier.background(gridLineColor)) {
        val innerW = maxWidth - outerFrame * 2
        val innerH = maxHeight - outerFrame * 2
        val hSepTotal = blockSep * 2 + CellSeparator * 6
        val vSepTotal = blockSep * 2 + CellSeparator * 6
        val cellSize = minOf(
            (innerW - hSepTotal) / GRID,
            (innerH - vSepTotal) / GRID,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerFrame),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for (row in 0 until GRID) {
                Row {
                    for (col in 0 until GRID) {
                        val index = row * GRID + col
                        val isClue = clueDigits[index] != 0
                        val committed = board[index]
                        SudokuCell(
                            index = index,
                            committedValue = committed,
                            noteMask = if (committed == 0) notes[index] else 0,
                            isClue = isClue,
                            isSelected = !solved && index == selectedIndex,
                            isSolved = solved,
                            cellSize = cellSize,
                            onCellClick = onCellClick,
                        )
                        if (col < GRID - 1) {
                            Spacer(
                                Modifier.width(
                                    if ((col + 1) % BLOCK == 0) blockSep else CellSeparator,
                                ),
                            )
                        }
                    }
                }
                if (row < GRID - 1) {
                    Spacer(
                        Modifier.height(
                            if ((row + 1) % BLOCK == 0) blockSep else CellSeparator,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SudokuCell(
    index: Int,
    committedValue: Int,
    noteMask: NoteMask,
    isClue: Boolean,
    isSelected: Boolean,
    isSolved: Boolean,
    cellSize: Dp,
    onCellClick: (Int) -> Unit,
) {
    val containerColor = when {
        isSolved -> SuccessGreen.copy(alpha = 0.22f)
        // Brand-pinned lavender so notes use OnPrimaryContainer with proper contrast in every theme.
        isSelected -> PrimaryContainer
        isClue -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isSolved -> SuccessGreen
        else -> MaterialTheme.colorScheme.onSurface
    }
    val showingNotes = committedValue == 0 && noteMask != 0
    val interactive = !isClue && !isSolved
    PrismTile(
        face = containerColor,
        isClickable = interactive,
        // Keep notes legible: sunken prism inset shrinks the content slot too much.
        isSelected = isSelected && !showingNotes,
        modifier = Modifier
            .size(cellSize)
            .hoverHand(interactive),
        onClick = { onCellClick(index) },
    ) {
        when {
            committedValue != 0 -> {
                Text(
                    text = committedValue.toString(),
                    fontSize = (cellSize.value * 0.5f).sp,
                    fontFamily = numberFontFamily(),
                    fontWeight = if (isClue) FontWeight.Bold else FontWeight.SemiBold,
                    color = textColor,
                    textAlign = TextAlign.Center,
                )
            }
            noteMask != 0 -> {
                CellNotesText(
                    noteMask = noteMask,
                    cellSize = cellSize,
                    isSelected = isSelected,
                )
            }
        }
    }
}

@Composable
private fun CellNotesText(
    noteMask: NoteMask,
    cellSize: Dp,
    isSelected: Boolean,
) {
    val noteColor = if (isSelected) OnPrimaryContainer else Primary
    val noteCount = noteMaskToText(noteMask).length
    val scale = if (noteCount > 6) 0.22f else 0.3f
    val fontSize = (cellSize.value * scale).sp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 3.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = noteMaskToText(noteMask),
            fontSize = fontSize,
            lineHeight = fontSize,
            fontFamily = numberFontFamily(),
            fontWeight = FontWeight.Medium,
            color = noteColor,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DigitPad(
    columns: Int,
    board: List<Int>,
    notesMode: Boolean,
    selectedNotes: NoteMask,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onDigit: (Int) -> Unit,
    onErase: () -> Unit,
) {
    val digitCounts = IntArray(10)
    board.forEach { if (it in 1..9) digitCounts[it]++ }
    val digits = (1..9).toList()
    val modeLabel = if (notesMode) {
        stringResource(Res.string.normal_sudoku_notes_mode)
    } else {
        stringResource(Res.string.normal_sudoku_pen_mode)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PadGap),
    ) {
        Text(
            text = modeLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
        digits.chunked(columns).forEach { rowDigits ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PadGap),
            ) {
                rowDigits.forEach { digit ->
                    val digitEnabled = enabled && (notesMode || digitCounts[digit] < GRID)
                    DigitTile(
                        label = digit.toString(),
                        enabled = digitEnabled,
                        highlighted = notesMode && noteMaskHas(selectedNotes, digit),
                        onClick = { onDigit(digit) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    )
                }
                repeat(columns - rowDigits.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        EraseTile(
            enabled = enabled,
            onClick = onErase,
            modifier = Modifier.fillMaxWidth(0.7f),
        )
    }
}

@Composable
private fun DigitTile(
    label: String,
    enabled: Boolean,
    highlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val size = minOf(maxWidth, maxHeight)
        PrismTile(
            face = if (highlighted) Primary.copy(alpha = 0.18f) else PrimaryContainer,
            modifier = Modifier
                .size(size)
                .hoverHand(enabled)
                .alpha(if (enabled) 1f else 0.6f),
            isClickable = enabled,
            isSelected = highlighted,
            onClick = onClick,
        ) {
            Text(
                text = label,
                fontSize = (size.value * 0.5f).sp,
                fontFamily = numberFontFamily(),
                fontWeight = FontWeight.Bold,
                color = OnPrimaryContainer,
            )
        }
    }
}

@Composable
private fun EraseTile(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .heightIn(min = 40.dp)
            .hoverHand(enabled)
            .alpha(if (enabled) 1f else 0.6f),
        isClickable = enabled,
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = stringResource(Res.string.normal_sudoku_erase),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun StatusBanner(solved: Boolean, wrongFlashKey: Long, xpGained: Int) {
    var showWrong by remember { mutableStateOf(false) }
    LaunchedEffect(wrongFlashKey) {
        if (wrongFlashKey > 0L) {
            showWrong = true
            delay(1400)
            showWrong = false
        }
    }
    val message: String? = when {
        solved -> stringResource(Res.string.normal_sudoku_solved)
        showWrong -> stringResource(Res.string.normal_sudoku_not_solved)
        else -> null
    }
    val color: Color = if (solved) SuccessGreen else Primary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        }
        if (solved && xpGained > 0) {
            Spacer(Modifier.height(8.dp))
            XpGainedChip(xpGained = xpGained)
        }
    }
}

private fun nextEmpty(from: Int, board: List<Int>, clueDigits: List<Int>): Int {
    val total = board.size
    for (step in 1..total) {
        val idx = (from + step) % total
        if (clueDigits[idx] == 0 && board[idx] == 0) return idx
    }
    return from
}

private fun currentTimeMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()

@DevicePreviews
@Composable
private fun NormalSudokuPlayScreenPreview() {
    ScreenPreviewHost {
        val storage = remember { UserStorage.forPreview() }
        NormalSudokuPlayScreen(
            puzzleId = NormalSudokuPuzzles.all.first().id,
            storage = storage,
            onCompleted = {},
            onBack = {},
        )
    }
}
