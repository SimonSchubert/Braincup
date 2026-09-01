package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.accounts_create
import braincup.composeapp.generated.resources.accounts_create_title
import braincup.composeapp.generated.resources.accounts_delete
import braincup.composeapp.generated.resources.accounts_delete_confirm
import braincup.composeapp.generated.resources.accounts_delete_title
import braincup.composeapp.generated.resources.accounts_edit
import braincup.composeapp.generated.resources.accounts_edit_title
import braincup.composeapp.generated.resources.accounts_game_center_desc
import braincup.composeapp.generated.resources.accounts_local_desc
import braincup.composeapp.generated.resources.accounts_name_label
import braincup.composeapp.generated.resources.accounts_play_desc
import braincup.composeapp.generated.resources.accounts_save
import braincup.composeapp.generated.resources.accounts_title
import braincup.composeapp.generated.resources.button_stay
import braincup.composeapp.generated.resources.settings_play_games_switch_cancel
import com.inspiredandroid.braincup.api.AccountIcon
import com.inspiredandroid.braincup.api.AccountKind
import com.inspiredandroid.braincup.api.PlayerAccount
import com.inspiredandroid.braincup.api.StorePlayerProfile
import com.inspiredandroid.braincup.ui.components.AccountAvatar
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ChunkyCheck
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismDialog
import com.inspiredandroid.braincup.ui.components.PrismDialogButtonRow
import com.inspiredandroid.braincup.ui.components.PrismDialogShell
import com.inspiredandroid.braincup.ui.components.PrismTextField
import com.inspiredandroid.braincup.ui.components.drawable
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.components.noRippleClickable
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismSlot
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AccountsScreen(
    accounts: List<PlayerAccount>,
    activeId: String,
    storeProfile: StorePlayerProfile?,
    canCreate: Boolean,
    onSelect: (String) -> Unit,
    onCreate: (String, AccountIcon) -> Unit,
    onEdit: (String, String, AccountIcon) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
) {
    var editor by remember { mutableStateOf<AccountEditor?>(null) }
    var pendingDelete by remember { mutableStateOf<PlayerAccount?>(null) }

    AppScaffold(
        title = stringResource(Res.string.accounts_title),
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = ContentMaxWidth)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            accounts.forEach { account ->
                val displayName = if (account.isStoreAccount) {
                    storeProfile?.displayName?.takeIf { it.isNotBlank() } ?: account.name
                } else {
                    account.name
                }
                AccountRow(
                    account = account.copy(name = displayName),
                    selected = account.id == activeId,
                    playAvatarBytes = storeProfile?.avatarBytes,
                    onSelect = { onSelect(account.id) },
                    onEdit = if (account.canEdit) {
                        { editor = AccountEditor.Edit(account) }
                    } else {
                        null
                    },
                    onDelete = if (account.canDelete) {
                        { pendingDelete = account }
                    } else {
                        null
                    },
                )
            }
            if (canCreate) {
                DefaultButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = stringResource(Res.string.accounts_create),
                    onClick = { editor = AccountEditor.Create },
                )
            }
        }
    }

    editor?.let { current ->
        AccountEditorDialog(
            editor = current,
            onDismiss = { editor = null },
            onConfirm = { name, icon ->
                when (current) {
                    AccountEditor.Create -> onCreate(name, icon)
                    is AccountEditor.Edit -> onEdit(current.account.id, name, icon)
                }
                editor = null
            },
        )
    }

    pendingDelete?.let { account ->
        PrismDialog(
            onDismissRequest = { pendingDelete = null },
            title = stringResource(Res.string.accounts_delete_title),
            message = stringResource(Res.string.accounts_delete_confirm, account.name),
            primaryLabel = stringResource(Res.string.button_stay),
            onPrimary = { pendingDelete = null },
            secondaryLabel = stringResource(Res.string.accounts_delete),
            onSecondary = {
                onDelete(account.id)
                pendingDelete = null
            },
        )
    }
}

@Composable
private fun AccountRow(
    account: PlayerAccount,
    selected: Boolean,
    playAvatarBytes: ByteArray?,
    onSelect: () -> Unit,
    onEdit: (() -> Unit)?,
    onDelete: (() -> Unit)?,
) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onSelect)
            .hoverHand(),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AccountAvatar(account = account, playAvatarBytes = playAvatarBytes)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(
                            when (account.kind) {
                                AccountKind.PLAY -> Res.string.accounts_play_desc
                                AccountKind.GAME_CENTER -> Res.string.accounts_game_center_desc
                                AccountKind.LOCAL -> Res.string.accounts_local_desc
                            },
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (selected) {
                    // Drawn rather than typed: the Unicode tick falls back to the platform face
                    // and reads as a thin system mark next to the brand type.
                    ChunkyCheck(color = Primary, modifier = Modifier.size(20.dp))
                }
            }
            if (onEdit != null || onDelete != null) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (onEdit != null) {
                        Text(
                            text = stringResource(Res.string.accounts_edit),
                            style = MaterialTheme.typography.labelLarge,
                            color = Primary,
                            modifier = Modifier.noRippleClickable(onClick = onEdit).hoverHand(),
                        )
                    }
                    if (onDelete != null) {
                        Text(
                            text = stringResource(Res.string.accounts_delete),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.noRippleClickable(onClick = onDelete).hoverHand(),
                        )
                    }
                }
            }
        }
    }
}

private sealed class AccountEditor {
    data object Create : AccountEditor()
    data class Edit(val account: PlayerAccount) : AccountEditor()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AccountEditorDialog(
    editor: AccountEditor,
    onDismiss: () -> Unit,
    onConfirm: (String, AccountIcon) -> Unit,
) {
    val initial = (editor as? AccountEditor.Edit)?.account
    var name by remember { mutableStateOf(initial?.name.orEmpty()) }
    var icon by remember { mutableStateOf(initial?.icon ?: AccountIcon.BLOWFISH) }
    val canSave = name.trim().isNotEmpty()
    PrismDialogShell(onDismissRequest = onDismiss) {
        Text(
            text = stringResource(
                if (editor is AccountEditor.Create) {
                    Res.string.accounts_create_title
                } else {
                    Res.string.accounts_edit_title
                },
            ),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(16.dp))
        PrismTextField(
            value = name,
            onValueChange = { if (it.length <= 24) name = it },
            placeholder = stringResource(Res.string.accounts_name_label),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            AccountIcon.entries.forEach { candidate ->
                val selected = candidate == icon
                Image(
                    painter = painterResource(candidate.drawable()),
                    contentDescription = candidate.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(PrismSlot)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .then(
                            if (selected) {
                                Modifier.border(2.dp, Primary, PrismSlot)
                            } else {
                                Modifier
                            },
                        )
                        .noRippleClickable { icon = candidate }
                        .hoverHand(),
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        PrismDialogButtonRow(
            primaryLabel = stringResource(Res.string.settings_play_games_switch_cancel),
            onPrimary = onDismiss,
            secondaryLabel = stringResource(Res.string.accounts_save),
            onSecondary = { if (canSave) onConfirm(name, icon) },
        )
    }
}
