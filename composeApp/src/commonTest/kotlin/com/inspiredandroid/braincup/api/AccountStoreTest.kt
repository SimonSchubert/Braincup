package com.inspiredandroid.braincup.api

import com.russhwolf.settings.MapSettings
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccountStoreTest {

    @AfterTest
    fun resetBridge() {
        PlayGamesBridge.hasPlayStoreAccount = false
        PlayGamesBridge.isGameCenterAccount = false
        PlayGamesBridge.updateCurrentPlayer(null)
    }

    @Test
    fun fossStartsWithDefaultLocalAccount() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val store = testAccountStore()
        val accounts = store.list()
        assertEquals(1, accounts.size)
        assertEquals(AccountStore.DEFAULT_LOCAL_ID, accounts.single().id)
        assertEquals(AccountKind.LOCAL, accounts.single().kind)
        assertFalse(accounts.single().canDelete)
        assertEquals(AccountStore.DEFAULT_LOCAL_ID, store.activeId())
        assertEquals("", store.progressPrefix())
    }

    @Test
    fun playStoreListsPlaySlotWithoutSeedingDefault() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val store = testAccountStore()
        val accounts = store.list()
        assertEquals(listOf(AccountStore.PLAY_ID), accounts.map { it.id })
        assertEquals(AccountKind.PLAY, accounts.single().kind)
        assertEquals(AccountStore.PLAY_ID, store.activeId())
        assertEquals("", store.progressPrefix())
    }

    @Test
    fun createAndSwitchLocalUsesPrefixedProgress() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val store = testAccountStore()
        val created = store.createLocal("Sam", AccountIcon.CRAB)
        assertEquals("Sam", created?.name)
        assertEquals(AccountIcon.CRAB, created?.icon)
        assertEquals(2, store.list().size)
        assertEquals(created?.id, store.activeId())
        assertTrue(store.progressPrefix().startsWith("a_"))
        assertTrue(store.switchTo(AccountStore.DEFAULT_LOCAL_ID))
        assertEquals("", store.progressPrefix())
    }

    @Test
    fun cannotDeleteDefaultOrLastLocalOnFoss() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val store = testAccountStore()
        assertFalse(store.deleteLocal(AccountStore.DEFAULT_LOCAL_ID))
        val extra = store.createLocal("Other", AccountIcon.FISH)
        assertFalse(store.deleteLocal(AccountStore.DEFAULT_LOCAL_ID))
        assertTrue(store.deleteLocal(extra!!.id))
        assertEquals(1, store.list().size)
    }

    @Test
    fun cannotCreateBeyondCap() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val store = testAccountStore()
        repeat(AccountStore.MAX_LOCAL_ACCOUNTS - 1) { index ->
            assertTrue(store.createLocal("P$index", AccountIcon.TUNA) != null)
        }
        assertFalse(store.canCreate())
        assertNull(store.createLocal("Overflow", AccountIcon.TUNA))
    }

    @Test
    fun playAndLocalCanCoexist() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val store = testAccountStore()
        val local = store.createLocal("Offline", AccountIcon.OCTOPUS)
        assertEquals(2, store.list().size)
        assertEquals(local!!.id, store.activeId())
        assertNotEquals("", store.progressPrefix())
        assertTrue(store.switchTo(AccountStore.PLAY_ID))
        assertTrue(store.isPlayAccountActive())
        assertEquals("", store.progressPrefix())
    }

    @Test
    fun firstStorePlayerKeepsUnprefixedKeys() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val store = testAccountStore()
        assertFalse(store.bindStorePlayer("player-a"))
        assertEquals("", store.progressPrefix(AccountStore.PLAY_ID))
        assertFalse(store.bindStorePlayer("player-a"))
    }

    @Test
    fun laterStorePlayerUsesOwnPrefixAndCanSwitchBack() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val store = testAccountStore()
        store.bindStorePlayer("player-a")
        assertTrue(store.bindStorePlayer("player-b"))
        val secondPrefix = store.progressPrefix(AccountStore.PLAY_ID)
        assertEquals("a_play.${AccountStore.prefixToken("player-b")}.", secondPrefix)
        assertTrue(store.bindStorePlayer("player-a"))
        assertEquals("", store.progressPrefix(AccountStore.PLAY_ID))
    }

    @Test
    fun fossInsertsDefaultNextToExistingLocals() {
        PlayGamesBridge.hasPlayStoreAccount = true
        val settings = MapSettings()
        val play = testAccountStore(settings)
        val extra = play.createLocal("Kid", AccountIcon.SEAL)
        assertEquals(listOf(AccountStore.PLAY_ID, extra!!.id), play.list().map { it.id })

        PlayGamesBridge.hasPlayStoreAccount = false
        val foss = testAccountStore(settings)
        assertEquals(
            listOf(AccountStore.DEFAULT_LOCAL_ID, extra.id),
            foss.list().map { it.id },
        )
        assertEquals(extra.id, foss.activeId())
        assertTrue(foss.switchTo(AccountStore.DEFAULT_LOCAL_ID))
        assertEquals("", foss.progressPrefix())
    }

    @Test
    fun foldsDefaultLocalIntoPlayOnStoreBuild() {
        PlayGamesBridge.hasPlayStoreAccount = false
        val settings = MapSettings()
        testAccountStore(settings)
        PlayGamesBridge.hasPlayStoreAccount = true
        val store = testAccountStore(settings)
        assertEquals(listOf(AccountStore.PLAY_ID), store.list().map { it.id })
        assertEquals(AccountStore.PLAY_ID, store.activeId())
    }

    @Test
    fun gameCenterSlotUsesGameCenterKind() {
        PlayGamesBridge.hasPlayStoreAccount = true
        PlayGamesBridge.isGameCenterAccount = true
        val store = testAccountStore()
        assertEquals(AccountKind.GAME_CENTER, store.list().single().kind)
        assertTrue(store.list().single().isStoreAccount)
    }
}
