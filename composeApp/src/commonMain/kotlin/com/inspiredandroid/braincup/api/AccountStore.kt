package com.inspiredandroid.braincup.api

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.random.Random

enum class AccountKind {
    PLAY,
    GAME_CENTER,
    LOCAL,
}

enum class AccountIcon {
    BLOWFISH,
    CLAM,
    CRAB,
    DOLPHIN,
    FISH,
    JELLYFISH,
    LOBSTER,
    MANTA_RAY,
    OCTOPUS,
    SEAGULL,
    SEAHORSE,
    SEAL,
    SEASHELL,
    SQUID,
    STARFISH,
    SWORDFISH,
    TUNA,
    TURTLE,
    WHALE,
    WINKLE,
    ;

    companion object {
        fun fromStored(raw: String): AccountIcon = entries.firstOrNull { it.name == raw } ?: BLOWFISH
    }
}

data class PlayerAccount(
    val id: String,
    val name: String,
    val icon: AccountIcon,
    val kind: AccountKind,
    val canDelete: Boolean,
    val canEdit: Boolean,
) {
    val isStoreAccount: Boolean
        get() = kind == AccountKind.PLAY || kind == AccountKind.GAME_CENTER
}

class AccountStore(
    private val settings: Settings = Settings(),
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val _snapshot = MutableStateFlow(readSnapshot())
    val snapshot: StateFlow<AccountSnapshot> = _snapshot.asStateFlow()

    fun progressPrefix(accountId: String = activeId()): String = when (accountId) {
        DEFAULT_LOCAL_ID -> ""
        PLAY_ID -> storePlayerPrefix()
        else -> "$PREFIX$accountId."
    }

    fun activeId(): String = _snapshot.value.activeId

    fun isPlayAccountActive(): Boolean = PlayGamesBridge.hasPlayStoreAccount && activeId() == PLAY_ID

    fun bindStorePlayer(playerId: String): Boolean {
        val trimmed = playerId.trim()
        if (trimmed.isEmpty()) return false
        val previous = settings.getStringOrNull(KEY_STORE_PLAYER)
        if (settings.getStringOrNull(KEY_STORE_LEGACY_PLAYER) == null) {
            settings.putString(KEY_STORE_LEGACY_PLAYER, trimmed)
        }
        if (previous == trimmed) return false
        settings.putString(KEY_STORE_PLAYER, trimmed)
        publish()
        return previous != null
    }

    fun list(): List<PlayerAccount> = _snapshot.value.accounts

    fun active(): PlayerAccount = list().firstOrNull { it.id == activeId() } ?: list().first()

    fun canCreate(): Boolean = localRecords().size < MAX_LOCAL_ACCOUNTS

    fun switchTo(id: String): Boolean {
        val allowed = list().any { it.id == id }
        if (!allowed) return false
        settings.putString(KEY_ACTIVE_ACCOUNT, id)
        publish()
        return true
    }

    fun createLocal(name: String, icon: AccountIcon): PlayerAccount? {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || !canCreate()) return null
        val id = newLocalId()
        val records = localRecords() + StoredAccount(id, trimmed, icon.name)
        saveLocals(records)
        settings.putString(KEY_ACTIVE_ACCOUNT, id)
        publish()
        return list().first { it.id == id }
    }

    fun updateLocal(id: String, name: String, icon: AccountIcon): Boolean {
        if (id == PLAY_ID) return false
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return false
        val records = localRecords()
        val index = records.indexOfFirst { it.id == id }
        if (index < 0) return false
        saveLocals(records.toMutableList().also { it[index] = StoredAccount(id, trimmed, icon.name) })
        publish()
        return true
    }

    fun deleteLocal(id: String): Boolean {
        if (id == PLAY_ID || id == DEFAULT_LOCAL_ID) return false
        val records = localRecords()
        if (records.none { it.id == id }) return false
        if (records.size <= 1 && !PlayGamesBridge.hasPlayStoreAccount) return false
        val prefix = progressPrefix(id)
        if (prefix.isNotEmpty()) {
            settings.keys.filter { it.startsWith(prefix) }.forEach { settings.remove(it) }
        }
        val remaining = records.filterNot { it.id == id }
        saveLocals(remaining)
        if (settings.getStringOrNull(KEY_ACTIVE_ACCOUNT) == id) {
            val next = when {
                PlayGamesBridge.hasPlayStoreAccount -> PLAY_ID
                remaining.isNotEmpty() -> remaining.first().id
                else -> DEFAULT_LOCAL_ID
            }
            settings.putString(KEY_ACTIVE_ACCOUNT, next)
        }
        publish()
        return true
    }

    private fun readSnapshot(): AccountSnapshot {
        val locals = resolvedLocals()
        val storeKind = if (PlayGamesBridge.isGameCenterAccount) AccountKind.GAME_CENTER else AccountKind.PLAY
        val play = if (PlayGamesBridge.hasPlayStoreAccount) {
            listOf(
                PlayerAccount(
                    id = PLAY_ID,
                    name = PlayGamesBridge.currentPlayer.value?.displayName
                        ?: if (storeKind == AccountKind.GAME_CENTER) GAME_CENTER_FALLBACK_NAME else PLAY_FALLBACK_NAME,
                    icon = AccountIcon.STARFISH,
                    kind = storeKind,
                    canDelete = false,
                    canEdit = false,
                ),
            )
        } else {
            emptyList()
        }
        val localAccounts = locals.map { record ->
            val isOnlyLocal = locals.size <= 1 && play.isEmpty()
            PlayerAccount(
                id = record.id,
                name = record.name,
                icon = AccountIcon.fromStored(record.icon),
                kind = AccountKind.LOCAL,
                canDelete = record.id != DEFAULT_LOCAL_ID && !isOnlyLocal,
                canEdit = true,
            )
        }
        val accounts = play + localAccounts
        val storedActive = settings.getStringOrNull(KEY_ACTIVE_ACCOUNT)
        val active = when {
            storedActive != null && accounts.any { it.id == storedActive } -> storedActive
            PlayGamesBridge.hasPlayStoreAccount -> PLAY_ID
            else -> DEFAULT_LOCAL_ID
        }
        if (storedActive != active) {
            settings.putString(KEY_ACTIVE_ACCOUNT, active)
        }
        return AccountSnapshot(accounts, active)
    }

    private fun publish() {
        _snapshot.value = readSnapshot()
    }

    fun refresh() {
        publish()
    }

    private fun resolvedLocals(): List<StoredAccount> {
        val loaded = localRecords()
        if (PlayGamesBridge.hasPlayStoreAccount) {
            return foldDefaultIntoPlay(loaded)
        }
        if (loaded.any { it.id == DEFAULT_LOCAL_ID }) return loaded
        val seed = StoredAccount(DEFAULT_LOCAL_ID, DEFAULT_LOCAL_NAME, AccountIcon.BLOWFISH.name)
        val withDefault = listOf(seed) + loaded
        saveLocals(withDefault)
        return withDefault
    }

    private fun foldDefaultIntoPlay(locals: List<StoredAccount>): List<StoredAccount> {
        if (locals.none { it.id == DEFAULT_LOCAL_ID }) return locals
        val remaining = locals.filterNot { it.id == DEFAULT_LOCAL_ID }
        saveLocals(remaining)
        if (settings.getStringOrNull(KEY_ACTIVE_ACCOUNT) == DEFAULT_LOCAL_ID) {
            settings.putString(KEY_ACTIVE_ACCOUNT, PLAY_ID)
        }
        return remaining
    }

    private fun storePlayerPrefix(): String {
        val current = settings.getStringOrNull(KEY_STORE_PLAYER) ?: return ""
        val legacy = settings.getStringOrNull(KEY_STORE_LEGACY_PLAYER) ?: return ""
        // First bound store identity keeps pre-accounts keys; later identities get their own prefix.
        if (current == legacy) return ""
        return "$PREFIX$PLAY_ID.${prefixToken(current)}."
    }

    private fun localRecords(): List<StoredAccount> {
        val raw = settings.getStringOrNull(KEY_ACCOUNTS) ?: return emptyList()
        return runCatching { json.decodeFromString<List<StoredAccount>>(raw) }.getOrDefault(emptyList())
    }

    private fun saveLocals(records: List<StoredAccount>) {
        settings.putString(KEY_ACCOUNTS, json.encodeToString(records))
    }

    private fun newLocalId(): String {
        val alphabet = "abcdefghijklmnopqrstuvwxyz0123456789"
        while (true) {
            val id = buildString { repeat(10) { append(alphabet[Random.nextInt(alphabet.length)]) } }
            if (id != PLAY_ID && id != DEFAULT_LOCAL_ID && localRecords().none { it.id == id }) return id
        }
    }

    companion object {
        const val PLAY_ID = "play"
        const val DEFAULT_LOCAL_ID = "default"
        const val MAX_LOCAL_ACCOUNTS = 8
        const val PREFIX = "a_"
        private const val DEFAULT_LOCAL_NAME = "Player"
        private const val PLAY_FALLBACK_NAME = "Play Games"
        private const val GAME_CENTER_FALLBACK_NAME = "Game Center"
        private const val KEY_ACCOUNTS = "player_accounts"
        private const val KEY_ACTIVE_ACCOUNT = "active_player_account"
        private const val KEY_STORE_PLAYER = "store_player_id"
        private const val KEY_STORE_LEGACY_PLAYER = "store_legacy_player_id"

        fun prefixToken(playerId: String): String {
            val safe = buildString {
                for (character in playerId) {
                    if (character.isLetterOrDigit() || character == '_' || character == '-') append(character)
                }
            }.take(48)
            return safe.ifEmpty { playerId.hashCode().toUInt().toString(16) }
        }
    }
}

data class AccountSnapshot(
    val accounts: List<PlayerAccount>,
    val activeId: String,
)

@Serializable
private data class StoredAccount(
    val id: String,
    val name: String,
    val icon: String,
)
