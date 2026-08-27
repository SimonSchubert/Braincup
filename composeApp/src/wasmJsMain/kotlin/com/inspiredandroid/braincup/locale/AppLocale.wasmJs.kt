@file:OptIn(ExperimentalWasmJsInterop::class)

package com.inspiredandroid.braincup.locale

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsString
import kotlin.js.toJsString

// navigator.languages is an accessor on Navigator.prototype and the browser exposes no way to
// change it, so shadow it with a configurable own property on the navigator instance. Compose's
// web locale delegate calls userPreferredLanguages() fresh on every read, so it sees the shadow.
@JsFun("(tag) => { Object.defineProperty(navigator, 'languages', { value: [tag], configurable: true }); Object.defineProperty(navigator, 'language', { value: tag, configurable: true }); }")
private external fun shadowNavigatorLanguage(tag: JsString)

// Deleting the own property uncovers the prototype accessor again.
@JsFun("() => { delete navigator.languages; delete navigator.language; }")
private external fun clearNavigatorLanguageShadow()

internal actual fun setPlatformLanguage(tag: String?) {
    if (tag == null) {
        clearNavigatorLanguageShadow()
    } else {
        shadowNavigatorLanguage(tag.toJsString())
    }
}
