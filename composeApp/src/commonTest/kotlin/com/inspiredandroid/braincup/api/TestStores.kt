package com.inspiredandroid.braincup.api

import com.russhwolf.settings.MapSettings

/**
 * A [UserStorage] backed by settings that live only for the test.
 *
 * Always constructed with explicit [MapSettings] rather than the no-argument constructor, which
 * reaches for the platform's real settings and has no implementation to reach for under a test
 * runner or Paparazzi.
 */
internal fun testStorage(settings: MapSettings = MapSettings()): UserStorage = UserStorage(settings)

/** An [AccountStore] over throwaway settings, for the same reason as [testStorage]. */
internal fun testAccountStore(settings: MapSettings = MapSettings()): AccountStore = AccountStore(settings)
