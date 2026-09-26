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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.checkers_difficulty
import braincup.composeapp.generated.resources.checkers_difficulty_easy
import braincup.composeapp.generated.resources.checkers_difficulty_hard
import braincup.composeapp.generated.resources.checkers_difficulty_medium
import braincup.composeapp.generated.resources.checkers_howto
import braincup.composeapp.generated.resources.checkers_mode_cpu
import braincup.composeapp.generated.resources.checkers_mode_human
import braincup.composeapp.generated.resources.checkers_mode_human_subtitle
import braincup.composeapp.generated.resources.checkers_start
import braincup.composeapp.generated.resources.checkers_title
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.checkers.CheckersDifficulty
import com.inspiredandroid.braincup.checkers.CheckersMode
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.Primary
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckersMenuScreen(
    storage: UserStorage,
    onStart: (CheckersMode, CheckersDifficulty) -> Unit,
    onBack: () -> Unit,
) {
    var mode by remember { mutableStateOf(storage.getCheckersMode()) }
    var difficulty by remember { mutableStateOf(storage.getCheckersDifficulty()) }

    AppScaffold(
        title = stringResource(Res.string.checkers_title),
        onBack = onBack,
        scrollable = true,
    ) {
        Spacer(Modifier.height(16.dp))

        // There is no instructions screen in front of this one, so the rule lives here.
        Text(
            text = stringResource(Res.string.checkers_howto),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .widthIn(max = ContentMaxWidth)
                .padding(horizontal = 24.dp),
        )

        Spacer(Modifier.height(24.dp))

        ModeTile(
            label = stringResource(Res.string.checkers_mode_cpu),
            subtitle = null,
            selected = mode == CheckersMode.VS_CPU,
            onClick = { mode = CheckersMode.VS_CPU },
        )

        Spacer(Modifier.height(12.dp))

        ModeTile(
            label = stringResource(Res.string.checkers_mode_human),
            subtitle = stringResource(Res.string.checkers_mode_human_subtitle),
            selected = mode == CheckersMode.VS_HUMAN,
            onClick = { mode = CheckersMode.VS_HUMAN },
        )

        // Nothing to set a difficulty on when both players are human.
        if (mode == CheckersMode.VS_CPU) {
            Spacer(Modifier.height(24.dp))
            DifficultyRow(selected = difficulty, onSelected = { difficulty = it })
        }

        Spacer(Modifier.height(32.dp))

        DefaultButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                storage.setCheckersMode(mode)
                storage.setCheckersDifficulty(difficulty)
                onStart(mode, difficulty)
            },
            value = stringResource(Res.string.checkers_start),
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ColumnScope.ModeTile(
    label: String,
    subtitle: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    PrismTile(
        face = if (selected) Primary else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .hoverHand()
            .widthIn(max = ContentMaxWidth)
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
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
                    color = if (selected) {
                        Color.White.copy(alpha = 0.85f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

@Composable
private fun DifficultyRow(
    selected: CheckersDifficulty,
    onSelected: (CheckersDifficulty) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.checkers_difficulty),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.widthIn(max = ContentMaxWidth).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CheckersDifficulty.entries.forEach { entry ->
                val isSelected = entry == selected
                PrismTile(
                    face = if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .hoverHand()
                        .defaultMinSize(minHeight = 48.dp),
                    onClick = { onSelected(entry) },
                ) {
                    Text(
                        text = stringResource(entry.labelRes()),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun CheckersDifficulty.labelRes(): StringResource = when (this) {
    CheckersDifficulty.EASY -> Res.string.checkers_difficulty_easy
    CheckersDifficulty.MEDIUM -> Res.string.checkers_difficulty_medium
    CheckersDifficulty.HARD -> Res.string.checkers_difficulty_hard
}

@DevicePreviews
@Composable
private fun CheckersMenuScreenPreview() {
    ScreenPreviewHost {
        val storage = remember { UserStorage.forPreview() }
        CheckersMenuScreen(
            storage = storage,
            onStart = { _, _ -> },
            onBack = {},
        )
    }
}
