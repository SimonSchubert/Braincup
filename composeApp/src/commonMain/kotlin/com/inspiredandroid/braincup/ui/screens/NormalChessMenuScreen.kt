package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.normal_chess_difficulty
import braincup.composeapp.generated.resources.normal_chess_difficulty_easy
import braincup.composeapp.generated.resources.normal_chess_difficulty_hard
import braincup.composeapp.generated.resources.normal_chess_difficulty_medium
import braincup.composeapp.generated.resources.normal_chess_mode_cpu
import braincup.composeapp.generated.resources.normal_chess_mode_human
import braincup.composeapp.generated.resources.normal_chess_mode_human_subtitle
import braincup.composeapp.generated.resources.normal_chess_start
import braincup.composeapp.generated.resources.normal_chess_title
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.normalchess.NormalChessDifficulty
import com.inspiredandroid.braincup.normalchess.NormalChessMode
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.Primary
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NormalChessMenuScreen(
    storage: UserStorage,
    onStart: (NormalChessMode, NormalChessDifficulty) -> Unit,
    onBack: () -> Unit,
) {
    var mode by remember { mutableStateOf(storage.getNormalChessMode()) }
    var difficulty by remember { mutableStateOf(storage.getNormalChessDifficulty()) }

    AppScaffold(
        title = stringResource(Res.string.normal_chess_title),
        onBack = onBack,
        scrollable = true,
    ) {
        Spacer(Modifier.height(16.dp))

        ModeTile(
            label = stringResource(Res.string.normal_chess_mode_cpu),
            subtitle = null,
            selected = mode == NormalChessMode.VS_CPU,
            onClick = { mode = NormalChessMode.VS_CPU },
        )

        Spacer(Modifier.height(12.dp))

        ModeTile(
            label = stringResource(Res.string.normal_chess_mode_human),
            subtitle = stringResource(Res.string.normal_chess_mode_human_subtitle),
            selected = mode == NormalChessMode.VS_HUMAN,
            onClick = { mode = NormalChessMode.VS_HUMAN },
        )

        if (mode == NormalChessMode.VS_CPU) {
            Spacer(Modifier.height(24.dp))
            DifficultyRow(
                selected = difficulty,
                onSelected = { difficulty = it },
            )
        }

        Spacer(Modifier.height(32.dp))

        DefaultButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                storage.setNormalChessMode(mode)
                storage.setNormalChessDifficulty(difficulty)
                onStart(mode, difficulty)
            },
            value = stringResource(Res.string.normal_chess_start),
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ModeTile(
    label: String,
    subtitle: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    PrismTile(
        face = if (selected) Primary else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .hoverHand()
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DifficultyRow(
    selected: NormalChessDifficulty,
    onSelected: (NormalChessDifficulty) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.normal_chess_difficulty),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NormalChessDifficulty.entries.forEach { d ->
                val isSelected = d == selected
                PrismTile(
                    face = if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .hoverHand()
                        .defaultMinSize(minHeight = 48.dp),
                    onClick = { onSelected(d) },
                ) {
                    Text(
                        text = stringResource(d.labelRes()),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun NormalChessDifficulty.labelRes(): StringResource = when (this) {
    NormalChessDifficulty.EASY -> Res.string.normal_chess_difficulty_easy
    NormalChessDifficulty.MEDIUM -> Res.string.normal_chess_difficulty_medium
    NormalChessDifficulty.HARD -> Res.string.normal_chess_difficulty_hard
}

@DevicePreviews
@Composable
private fun NormalChessMenuScreenPreview() {
    ScreenPreviewHost {
        val storage = remember { UserStorage.forPreview() }
        NormalChessMenuScreen(
            storage = storage,
            onStart = { _, _ -> },
            onBack = {},
        )
    }
}
