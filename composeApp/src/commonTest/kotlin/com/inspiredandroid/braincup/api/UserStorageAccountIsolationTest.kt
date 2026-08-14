package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.games.GameType
import com.russhwolf.settings.MapSettings
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserStorageAccountIsolationTest {

    @AfterTest
    fun resetBridge() {
        PlayGamesBridge.hasPlayStoreAccount = false
        PlayGamesBridge.isGameCenterAccount = false
        PlayGamesBridge.onGoldMedal = null
        PlayGamesBridge.onSubmitTotalXp = null
    }

    @Test
    fun localAccountDoesNotShareXpOrHighScores() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val settings = MapSettings()
        val storage = UserStorage(settings)
        storage.putScore(GameType.MENTAL_CALCULATION.id, 12)
        val defaultXp = storage.getTotalXp()
        assertTrue(defaultXp > 0)

        val local = storage.accounts.createLocal("Kid", AccountIcon.SEAL)
        assertEquals(0, storage.getHighScore(GameType.MENTAL_CALCULATION.id))
        assertEquals(0, storage.getTotalXp())

        storage.putScore(GameType.MENTAL_CALCULATION.id, 3)
        assertEquals(3, storage.getHighScore(GameType.MENTAL_CALCULATION.id))

        storage.accounts.switchTo(AccountStore.DEFAULT_LOCAL_ID)
        assertEquals(12, storage.getHighScore(GameType.MENTAL_CALCULATION.id))
        assertEquals(defaultXp, storage.getTotalXp())
        assertTrue(settings.keys.any { it.startsWith("a_${local!!.id}.") })
    }

    @Test
    fun playRestoreDoesNotTouchLocalPrefix() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val settings = MapSettings()
        val live = UserStorage(settings)
        live.accounts.createLocal("Offline", AccountIcon.WHALE)
        live.putScore(GameType.FLAGS.id, 4)

        val restore = UserStorage(settings, playSlotProgress = true)
        restore.restoreTotalXpIfHigher(500)
        restore.restoreHighScoreIfHigher(GameType.FLAGS.id, 9)

        assertEquals(4, live.getHighScore(GameType.FLAGS.id))
        live.accounts.switchTo(AccountStore.PLAY_ID)
        assertEquals(500, live.getTotalXp())
        assertEquals(9, live.getHighScore(GameType.FLAGS.id))
    }

    @Test
    fun fossDefaultDoesNotNotifyPlayGames() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val storage = UserStorage(MapSettings())
        val medals = mutableListOf<GameType>()
        PlayGamesBridge.onGoldMedal = { medals += it }
        storage.putScore(GameType.MENTAL_CALCULATION.id, GameType.MENTAL_CALCULATION.goldScore)
        assertEquals(emptyList(), medals)
    }

    @Test
    fun localPlayDoesNotNotifyPlayGames() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val storage = UserStorage(MapSettings())
        val medals = mutableListOf<GameType>()
        PlayGamesBridge.onGoldMedal = { medals += it }
        storage.accounts.createLocal("Offline", AccountIcon.CRAB)
        storage.putScore(GameType.MENTAL_CALCULATION.id, GameType.MENTAL_CALCULATION.goldScore)
        assertEquals(emptyList(), medals)

        storage.accounts.switchTo(AccountStore.PLAY_ID)
        storage.putScore(GameType.MENTAL_CALCULATION.id, GameType.MENTAL_CALCULATION.goldScore)
        assertEquals(listOf(GameType.MENTAL_CALCULATION), medals)
    }

    @Test
    fun unlockedGoldSeedsHighScoreForTileMedal() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val storage = UserStorage(MapSettings())
        storage.restoreUnlockedAchievements(setOf(UserStorage.Achievements.GOLD_MENTAL_CALCULATION))
        assertTrue(GameType.MENTAL_CALCULATION.meetsScore(storage.getHighScore(GameType.MENTAL_CALCULATION.id), GameType.MENTAL_CALCULATION.goldScore))
        storage.accounts.createLocal("Kid", AccountIcon.SEAL)
        assertEquals(0, storage.getHighScore(GameType.MENTAL_CALCULATION.id))
        storage.accounts.switchTo(AccountStore.PLAY_ID)
        storage.seedHighScoresFromUnlockedGold()
        assertTrue(GameType.MENTAL_CALCULATION.meetsScore(storage.getHighScore(GameType.MENTAL_CALCULATION.id), GameType.MENTAL_CALCULATION.goldScore))
    }

    @Test
    fun deviceThemeIsSharedAcrossAccounts() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val storage = UserStorage(MapSettings())
        storage.setAudioMuted(true)
        storage.accounts.createLocal("B", AccountIcon.FISH)
        assertTrue(storage.isAudioMuted())
        assertNull(PlayGamesBridge.onGoldMedal)
    }

    @Test
    fun fossDefaultOwnsExistingUnprefixedProgressWhenLocalsAlreadyExist() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val settings = MapSettings()
        val play = UserStorage(settings)
        play.putScore(GameType.MENTAL_CALCULATION.id, 15)
        val playXp = play.getTotalXp()
        play.accounts.createLocal("Kid", AccountIcon.SEAL)

        PlayGamesBridge.hasPlayStoreAccount = false
        val foss = UserStorage(settings)
        assertEquals(0, foss.getHighScore(GameType.MENTAL_CALCULATION.id))
        foss.accounts.switchTo(AccountStore.DEFAULT_LOCAL_ID)
        assertEquals(15, foss.getHighScore(GameType.MENTAL_CALCULATION.id))
        assertEquals(playXp, foss.getTotalXp())
    }

    @Test
    fun deleteLocalRemovesPrefixedKeys() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val settings = MapSettings()
        val storage = UserStorage(settings)
        val extra = storage.accounts.createLocal("Kid", AccountIcon.SEAL)
        storage.putScore(GameType.MENTAL_CALCULATION.id, 8)
        val prefix = "a_${extra!!.id}."
        assertTrue(settings.keys.any { it.startsWith(prefix) })
        assertTrue(storage.accounts.deleteLocal(extra.id))
        assertTrue(settings.keys.none { it.startsWith(prefix) })
        assertEquals(AccountStore.DEFAULT_LOCAL_ID, storage.accounts.activeId())
    }

    @Test
    fun achievementsStayIsolatedAcrossAccounts() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val storage = UserStorage(MapSettings())
        storage.putScore(GameType.MENTAL_CALCULATION.id, GameType.MENTAL_CALCULATION.goldScore)
        assertTrue(UserStorage.Achievements.GOLD_MENTAL_CALCULATION in storage.getUnlockedAchievements())

        storage.accounts.createLocal("Kid", AccountIcon.CRAB)
        assertTrue(storage.getUnlockedAchievements().isEmpty())

        storage.accounts.switchTo(AccountStore.DEFAULT_LOCAL_ID)
        assertTrue(UserStorage.Achievements.GOLD_MENTAL_CALCULATION in storage.getUnlockedAchievements())
    }

    @Test
    fun storePlayerChangeDoesNotMergeXp() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val settings = MapSettings()
        val storage = UserStorage(settings)
        storage.accounts.bindStorePlayer("player-a")
        storage.putScore(GameType.MENTAL_CALCULATION.id, 12)
        val playerAXp = storage.getTotalXp()
        assertTrue(playerAXp > 0)

        assertTrue(storage.accounts.bindStorePlayer("player-b"))
        assertEquals(0, storage.getTotalXp())
        assertEquals(0, storage.getHighScore(GameType.MENTAL_CALCULATION.id))

        val restore = UserStorage(settings, playSlotProgress = true)
        restore.restoreTotalXpIfHigher(40)
        assertEquals(40, storage.getTotalXp())

        storage.accounts.bindStorePlayer("player-a")
        assertEquals(playerAXp, storage.getTotalXp())
        assertEquals(12, storage.getHighScore(GameType.MENTAL_CALCULATION.id))
    }

    @Test
    fun secondStorePlayerStillNotifiesPlayGames() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val storage = UserStorage(MapSettings())
        storage.accounts.bindStorePlayer("player-a")
        storage.accounts.bindStorePlayer("player-b")
        assertTrue(storage.accounts.isPlayAccountActive())
        assertNotEquals("", storage.accounts.progressPrefix())
        val medals = mutableListOf<GameType>()
        PlayGamesBridge.onGoldMedal = { medals += it }
        storage.putScore(GameType.MENTAL_CALCULATION.id, GameType.MENTAL_CALCULATION.goldScore)
        assertEquals(listOf(GameType.MENTAL_CALCULATION), medals)
    }
}
