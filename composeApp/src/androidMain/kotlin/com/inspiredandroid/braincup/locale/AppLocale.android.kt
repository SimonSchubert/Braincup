package com.inspiredandroid.braincup.locale

import android.os.LocaleList

// Captured before the first override so "System default" is restorable without a relaunch.
private val systemLocales: LocaleList = LocaleList.getDefault()

// Compose's Android locale delegate reads LocaleList.getDefault() (AndroidLocaleDelegateAPI24),
// so this is the value stringResource resolves against. LocaleList.setDefault also updates
// Locale.getDefault(), which the resources library's non-composable getString path uses.
internal actual fun setPlatformLanguage(tag: String?) {
    LocaleList.setDefault(
        if (tag == null) systemLocales else LocaleList.forLanguageTags(tag),
    )
}
