package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.hoverHand
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    isColorblindPaletteEnabled: Boolean,
    onToggleColorblindPalette: () -> Unit,
    isHapticEnabled: Boolean,
    onToggleHaptic: () -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(Res.string.settings_title),
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SettingsToggleRow(
                title = stringResource(Res.string.settings_sound),
                description = stringResource(Res.string.settings_sound_desc),
                checked = !isMuted,
                onToggle = onToggleMute,
            )
            SettingsToggleRow(
                title = stringResource(Res.string.settings_haptic),
                description = stringResource(Res.string.settings_haptic_desc),
                checked = isHapticEnabled,
                onToggle = onToggleHaptic,
            )
            SettingsToggleRow(
                title = stringResource(Res.string.settings_colorblind),
                description = stringResource(Res.string.settings_colorblind_desc),
                checked = isColorblindPaletteEnabled,
                onToggle = onToggleColorblindPalette,
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .hoverHand(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(16.dp))
            Switch(
                checked = checked,
                onCheckedChange = { onToggle() },
            )
        }
    }
}
