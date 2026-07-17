package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_start
import braincup.composeapp.generated.resources.instructions_best_score
import braincup.composeapp.generated.resources.instructions_leaderboard
import braincup.composeapp.generated.resources.mini_chess_difficulty
import braincup.composeapp.generated.resources.mini_chess_difficulty_easy
import braincup.composeapp.generated.resources.mini_chess_difficulty_hard
import braincup.composeapp.generated.resources.mini_chess_difficulty_medium
import braincup.composeapp.generated.resources.wordle_legend_absent
import braincup.composeapp.generated.resources.wordle_legend_correct
import braincup.composeapp.generated.resources.wordle_legend_present
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.formattedScore
import com.inspiredandroid.braincup.ui.components.AnomalyPuzzleDemo
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.BubbleSumDemo
import com.inspiredandroid.braincup.ui.components.CatQueensDemo
import com.inspiredandroid.braincup.ui.components.ChainCalculationDemo
import com.inspiredandroid.braincup.ui.components.ChessMoveDemo
import com.inspiredandroid.braincup.ui.components.ColorConfusionDemo
import com.inspiredandroid.braincup.ui.components.ColoredShapesDemo
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.DigitMemoryDemo
import com.inspiredandroid.braincup.ui.components.FlagsDemo
import com.inspiredandroid.braincup.ui.components.FlashCrowdDemo
import com.inspiredandroid.braincup.ui.components.FractionCalculationDemo
import com.inspiredandroid.braincup.ui.components.GhostGridDemo
import com.inspiredandroid.braincup.ui.components.KnotDemo
import com.inspiredandroid.braincup.ui.components.LightsOutDemo
import com.inspiredandroid.braincup.ui.components.MentalCalculationDemo
import com.inspiredandroid.braincup.ui.components.MiniSudokuDemo
import com.inspiredandroid.braincup.ui.components.NBackDemo
import com.inspiredandroid.braincup.ui.components.NurikabeDemo
import com.inspiredandroid.braincup.ui.components.OrbitTrackerDemo
import com.inspiredandroid.braincup.ui.components.PathFinderDemo
import com.inspiredandroid.braincup.ui.components.PatternSequenceDemo
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.QuickSumDemo
import com.inspiredandroid.braincup.ui.components.SchulteTableDemo
import com.inspiredandroid.braincup.ui.components.SherlockCalculationDemo
import com.inspiredandroid.braincup.ui.components.ShikakuDemo
import com.inspiredandroid.braincup.ui.components.SlidingPuzzleDemo
import com.inspiredandroid.braincup.ui.components.SoloChessDemo
import com.inspiredandroid.braincup.ui.components.SpotTheNewDemo
import com.inspiredandroid.braincup.ui.components.TextPrismButton
import com.inspiredandroid.braincup.ui.components.TowerOfHanoiDemo
import com.inspiredandroid.braincup.ui.components.ValueComparisonDemo
import com.inspiredandroid.braincup.ui.components.VisualMemoryDemo
import com.inspiredandroid.braincup.ui.components.WordleDemo
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.StartAccent
import com.inspiredandroid.braincup.ui.theme.WordleAbsent
import com.inspiredandroid.braincup.ui.theme.WordleCorrect
import com.inspiredandroid.braincup.ui.theme.WordlePresent
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun InstructionsScreen(
    gameType: GameType,
    storage: UserStorage,
    onStart: () -> Unit,
    onBack: () -> Unit,
    onShowLeaderboard: (() -> Unit)? = null,
) {
    AppScaffold(
        title = stringResource(gameType.displayNameRes),
        onBack = onBack,
        // Body scrolls under a pinned footer so the Start button stays on screen, and
        // provideCompactHeight lets the demos shrink to their compact sizing on short screens.
        provideCompactHeight = true,
        bottomBar = {
            InstructionsFooter(
                gameType = gameType,
                onStart = onStart,
                onShowLeaderboard = onShowLeaderboard,
            )
        },
    ) {
        // Cap the column on wide layouts (tablet/desktop) so the instructions read as a tidy
        // centered block instead of stretching edge to edge.
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))

            val demoModifier = Modifier.padding(horizontal = 16.dp)
            when (gameType) {
                GameType.MINI_CHESS -> ChessMoveDemo(modifier = demoModifier)
                GameType.GHOST_GRID -> GhostGridDemo(modifier = demoModifier)
                GameType.LIGHTS_OUT -> LightsOutDemo(modifier = demoModifier)
                GameType.PATH_FINDER -> PathFinderDemo(modifier = demoModifier)
                GameType.SHIKAKU -> ShikakuDemo(modifier = demoModifier)
                GameType.NURIKABE -> NurikabeDemo(modifier = demoModifier)
                GameType.SCHULTE_TABLE -> SchulteTableDemo(modifier = demoModifier)
                GameType.SPOT_THE_NEW -> SpotTheNewDemo(modifier = demoModifier)
                GameType.ORBIT_TRACKER -> OrbitTrackerDemo(modifier = demoModifier)
                GameType.BUBBLE_SUM -> BubbleSumDemo(modifier = demoModifier)
                GameType.QUICK_SUM -> QuickSumDemo(modifier = demoModifier)
                GameType.CAT_QUEENS -> CatQueensDemo(modifier = demoModifier)
                GameType.KNOT -> KnotDemo(modifier = demoModifier)
                GameType.SOLO_CHESS -> SoloChessDemo(modifier = demoModifier)
                GameType.FLASH_CROWD -> FlashCrowdDemo(modifier = demoModifier)
                GameType.ANOMALY_PUZZLE -> AnomalyPuzzleDemo(modifier = demoModifier)
                GameType.SLIDING_PUZZLE -> SlidingPuzzleDemo(modifier = demoModifier)
                GameType.TOWER_OF_HANOI -> TowerOfHanoiDemo(modifier = demoModifier)
                GameType.MINI_SUDOKU -> MiniSudokuDemo(modifier = demoModifier)
                GameType.COLORED_SHAPES -> ColoredShapesDemo(modifier = demoModifier)
                GameType.WORDLE -> WordleDemo(modifier = demoModifier)
                GameType.VISUAL_MEMORY -> VisualMemoryDemo(modifier = demoModifier)
                GameType.DIGIT_MEMORY -> DigitMemoryDemo(modifier = demoModifier)
                GameType.N_BACK -> NBackDemo(modifier = demoModifier)
                GameType.PATTERN_SEQUENCE -> PatternSequenceDemo(modifier = demoModifier)
                GameType.COLOR_CONFUSION -> ColorConfusionDemo(modifier = demoModifier)
                GameType.FLAGS -> FlagsDemo(modifier = demoModifier)
                GameType.MENTAL_CALCULATION -> MentalCalculationDemo(modifier = demoModifier)
                GameType.CHAIN_CALCULATION -> ChainCalculationDemo(modifier = demoModifier)
                GameType.FRACTION_CALCULATION -> FractionCalculationDemo(modifier = demoModifier)
                GameType.SHERLOCK_CALCULATION -> SherlockCalculationDemo(modifier = demoModifier)
                GameType.VALUE_COMPARISON -> ValueComparisonDemo(modifier = demoModifier)
            }

            if (gameType == GameType.WORDLE) {
                Spacer(Modifier.height(16.dp))
                WordleColorLegend(modifier = Modifier.padding(horizontal = 24.dp))
            }

            if (gameType == GameType.MINI_CHESS) {
                Spacer(Modifier.height(24.dp))
                MiniChessDifficultySelector(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    initial = storage.getMiniChessDifficulty(),
                    onSelected = { storage.setMiniChessDifficulty(it) },
                )
            }

            if (gameType.hasLeaderboard) {
                val highscore = storage.getHighScore(gameType.id)
                if (highscore > 0) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(
                            Res.string.instructions_best_score,
                            gameType.formattedScore(highscore),
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// Pinned bottom bar so the Start action is always visible while the demo scrolls. Start uses the
// teal [StartAccent] so it never matches the orange tiles inside the demos; the optional Leaderboard
// sits below it as a secondary action.
@Composable
private fun InstructionsFooter(
    gameType: GameType,
    onStart: () -> Unit,
    onShowLeaderboard: (() -> Unit)?,
) {
    // No separator: the footer shares the page background so it reads as part of the screen, with
    // only whitespace and the teal Start button setting it apart. The opaque surface keeps any
    // scrolled content from bleeding through behind the buttons.
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DefaultButton(
                onClick = onStart,
                value = stringResource(Res.string.button_start),
                face = StartAccent,
            )
            if (gameType.hasLeaderboard && onShowLeaderboard != null) {
                Spacer(Modifier.height(8.dp))
                TextPrismButton(
                    onClick = onShowLeaderboard,
                    value = stringResource(Res.string.instructions_leaderboard),
                )
            }
        }
    }
}

@Composable
private fun WordleColorLegend(modifier: Modifier = Modifier) {
    // One swatch per line, matching the board tiles, so the green/yellow/gray meaning is shown
    // visually instead of spelled out in the description.
    val entries = listOf(
        Triple(WordleCorrect, 'A', Res.string.wordle_legend_correct),
        Triple(WordlePresent, 'B', Res.string.wordle_legend_present),
        Triple(WordleAbsent, 'C', Res.string.wordle_legend_absent),
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        entries.forEach { (face, letter, labelRes) ->
            WordleLegendRow(face = face, letter = letter, labelRes = labelRes)
        }
    }
}

@Composable
private fun WordleLegendRow(face: Color, letter: Char, labelRes: StringResource) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        PrismCard(
            face = face,
            modifier = Modifier.size(44.dp),
        ) {
            Text(
                text = letter.toString(),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun MiniChessDifficultySelector(
    initial: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val supported = listOf(1, 3, 5)
    val resolvedInitial = if (initial in supported) initial else 3
    var selected by remember { mutableIntStateOf(resolvedInitial) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.mini_chess_difficulty),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        // Spread depth widely so users feel a real strength jump:
        //   Easy=1 (no opponent-response prediction → easy to trap)
        //   Medium=3 (sees player + own follow-up)
        //   Hard=5 (deep tactical calculation; slower thinks)
        val options = listOf(
            1 to stringResource(Res.string.mini_chess_difficulty_easy),
            3 to stringResource(Res.string.mini_chess_difficulty_medium),
            5 to stringResource(Res.string.mini_chess_difficulty_hard),
        )
        Row(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            options.forEach { (depth, label) ->
                val isSelected = selected == depth
                PrismTile(
                    face = if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .hoverHand()
                        .defaultMinSize(minHeight = 48.dp),
                    onClick = {
                        selected = depth
                        onSelected(depth)
                    },
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun InstructionsScreenPreview() {
    ScreenPreviewHost {
        val storage = remember { UserStorage.forPreview() }
        InstructionsScreen(
            gameType = GameType.MENTAL_CALCULATION,
            storage = storage,
            onStart = {},
            onBack = {},
        )
    }
}
