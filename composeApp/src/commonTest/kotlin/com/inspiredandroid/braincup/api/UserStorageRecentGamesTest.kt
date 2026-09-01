package com.inspiredandroid.braincup.api

import com.russhwolf.settings.MapSettings
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers the launcher-shortcut recents list in [UserStorage.putRecentGame]: newest first, no
 * duplicates, capped, and kept per player account.
 */
class UserStorageRecentGamesTest {

    @AfterTest
    fun resetBridge() {
        PlayGamesBridge.hasPlayStoreAccount = false
    }

    private fun storage() = UserStorage(MapSettings())

    @Test
    fun freshStorageHasNoRecents() {
        assertEquals(emptyList(), storage().getRecentGames())
    }

    @Test
    fun newestComesFirst() {
        val storage = storage()
        storage.putRecentGame("MiniSudoku")
        storage.putRecentGame("sudoku")
        assertEquals(listOf("sudoku", "MiniSudoku"), storage.getRecentGames())
    }

    @Test
    fun replayingMovesToTheFrontWithoutDuplicating() {
        val storage = storage()
        storage.putRecentGame("MiniSudoku")
        storage.putRecentGame("chess")
        storage.putRecentGame("MiniSudoku")
        assertEquals(listOf("MiniSudoku", "chess"), storage.getRecentGames())
    }

    @Test
    fun capDropsTheOldest() {
        val storage = storage()
        val played = (1..UserStorage.RECENT_GAMES_LIMIT + 2).map { "game$it" }
        played.forEach { storage.putRecentGame(it) }
        assertEquals(
            played.reversed().take(UserStorage.RECENT_GAMES_LIMIT),
            storage.getRecentGames(),
        )
    }

    @Test
    fun emptySuffixIsIgnored() {
        val storage = storage()
        storage.putRecentGame("")
        assertEquals(emptyList(), storage.getRecentGames())
    }

    @Test
    fun accountsKeepTheirOwnRecents() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val storage = storage()
        storage.putRecentGame("MiniSudoku")

        storage.accounts.createLocal("Kid", AccountIcon.SEAL)
        assertEquals(emptyList(), storage.getRecentGames())
        storage.putRecentGame("chess")

        storage.accounts.switchTo(AccountStore.DEFAULT_LOCAL_ID)
        assertEquals(listOf("MiniSudoku"), storage.getRecentGames())
    }
}
