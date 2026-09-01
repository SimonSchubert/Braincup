package com.inspiredandroid.braincup.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A route asked for from outside the composition, such as an Android launcher shortcut.
 *
 * State rather than a plain value because the same `App()` composition also has to receive the
 * later requests that arrive while the app is already running.
 */
class ExternalRouteRequests {
    private val _pending = MutableStateFlow<String?>(null)
    val pending: StateFlow<String?> = _pending.asStateFlow()

    fun request(pathSuffix: String) {
        _pending.value = pathSuffix
    }

    fun consume() {
        _pending.value = null
    }

    companion object {
        /** For the targets with no external entry point: nothing ever requests on it. */
        val None = ExternalRouteRequests()
    }
}
