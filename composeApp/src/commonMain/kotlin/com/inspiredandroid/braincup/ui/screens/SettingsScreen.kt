package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.AccountIcon
import com.inspiredandroid.braincup.api.AccountKind
import com.inspiredandroid.braincup.api.PlayerAccount
import com.inspiredandroid.braincup.api.StorePlayerProfile
import com.inspiredandroid.braincup.ui.components.AccountAvatar
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.PrismToggle
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.components.noRippleClickable
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
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
    activeAccount: PlayerAccount? = null,
    storeProfile: StorePlayerProfile? = null,
    onOpenAccounts: (() -> Unit)? = null,
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
            if (activeAccount != null && onOpenAccounts != null) {
                SettingsAccountRow(
                    account = activeAccount,
                    storeProfile = storeProfile,
                    onClick = onOpenAccounts,
                )
            }
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
private fun SettingsAccountRow(
    account: PlayerAccount,
    storeProfile: StorePlayerProfile?,
    onClick: () -> Unit,
) {
    val title = if (account.isStoreAccount) {
        storeProfile?.displayName?.takeIf { it.isNotBlank() } ?: account.name
    } else {
        account.name
    }
    val description = stringResource(
        when (account.kind) {
            AccountKind.PLAY -> Res.string.accounts_play_desc
            AccountKind.GAME_CENTER -> Res.string.accounts_game_center_desc
            AccountKind.LOCAL -> Res.string.accounts_local_desc
        },
    )
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClickLabel = title, onClick = onClick)
            .hoverHand(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AccountAvatar(
                account = account.copy(name = title),
                playAvatarBytes = storeProfile?.avatarBytes,
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(Res.string.settings_play_games_profile_switch),
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
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
            PrismToggle(checked = checked)
        }
    }
}

@DevicePreviews
@Composable
private fun SettingsScreenPreview() {
    ScreenPreviewHost {
        SettingsScreen(
            isMuted = false,
            onToggleMute = {},
            isColorblindPaletteEnabled = false,
            onToggleColorblindPalette = {},
            isHapticEnabled = true,
            onToggleHaptic = {},
            isNumberPadAscending = true,
            onToggleNumberPadAscending = {},
            themeMode = ThemeMode.SYSTEM,
            onThemeSelected = {},
            onBack = {},
        )
    }
}

@DevicePreviews
@Composable
private fun SettingsScreenPlayGamesPreview() {
    ScreenPreviewHost {
        SettingsScreen(
            isMuted = false,
            onToggleMute = {},
            isColorblindPaletteEnabled = false,
            onToggleColorblindPalette = {},
            isHapticEnabled = true,
            onToggleHaptic = {},
            isNumberPadAscending = true,
            onToggleNumberPadAscending = {},
            themeMode = ThemeMode.SYSTEM,
            onThemeSelected = {},
            onBack = {},
            activeAccount = PlayerAccount(
                id = "default",
                name = "Alex",
                icon = AccountIcon.DOLPHIN,
                kind = AccountKind.LOCAL,
                canDelete = false,
                canEdit = true,
            ),
            onOpenAccounts = {},
        )
    }
}
