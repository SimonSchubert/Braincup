package com.inspiredandroid.braincup.locale

import platform.Foundation.NSUserDefaults

private const val APPLE_LANGUAGES = "AppleLanguages"

// Both Compose's darwin locale delegate and the resources library's iOS getSystemEnvironment read
// NSLocale.preferredLanguages, which is derived from AppleLanguages. The system value lives in the
// global domain, so writing the key shadows it and removing the key unshadows it again; nothing
// about the device's own language setting is touched.
internal actual fun setPlatformLanguage(tag: String?) {
    val defaults = NSUserDefaults.standardUserDefaults
    if (tag == null) {
        defaults.removeObjectForKey(APPLE_LANGUAGES)
    } else {
        defaults.setObject(listOf(tag), forKey = APPLE_LANGUAGES)
    }
    defaults.synchronize()
}
