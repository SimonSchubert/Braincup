package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.components.noRippleClickable
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.ThemeMode
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    isColorblindPaletteEnabled: Boolean,
    onToggleColorblindPalette: () -> Unit,
    isHapticEnabled: Boolean,
    onToggleHaptic: () -> Unit,
    isNumberPadAscending: Boolean,
    onToggleNumberPadAscending: () -> Unit,
    themeMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
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
            SettingsThemeSelector(
                themeMode = themeMode,
                onThemeSelected = onThemeSelected,
            )
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
            SettingsToggleRow(
                title = stringResource(Res.string.settings_keypad),
                description = stringResource(Res.string.settings_keypad_desc),
                checked = isNumberPadAscending,
                onToggle = onToggleNumberPadAscending,
            )
        }
    }
}

@Composable
private fun SettingsThemeSelector(
    themeMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
) {
    val labels: Map<ThemeMode, StringResource> = mapOf(
        ThemeMode.SYSTEM to Res.string.settings_theme_system,
        ThemeMode.LIGHT to Res.string.settings_theme_light,
        ThemeMode.DARK to Res.string.settings_theme_dark,
        ThemeMode.OLED to Res.string.settings_theme_oled,
    )
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.settings_theme),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(Res.string.settings_theme_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            ThemeMode.entries.toList().chunked(2).forEach { rowModes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowModes.forEach { mode ->
                        val isSelected = themeMode == mode
                        PrismTile(
                            face = if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 48.dp),
                            onClick = { onThemeSelected(mode) },
                        ) {
                            Text(
                                text = stringResource(labels.getValue(mode)),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            )
                        }
                    }
                }
            }
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
            .noRippleClickable { onToggle() }
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
