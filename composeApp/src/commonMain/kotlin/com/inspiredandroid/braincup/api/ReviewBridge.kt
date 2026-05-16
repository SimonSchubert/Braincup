package com.inspiredandroid.braincup.api

/**
 * Platform-agnostic hook to trigger the OS-provided in-app review prompt.
 * Android (playStore flavor) wires this to Play Core's ReviewManager.
 * iOS wires this to SKStoreReviewController.requestReview.
 * Other platforms/flavors leave it null (no-op).
 *
 * Both underlying APIs rate-limit themselves, so callers can fire freely.
 */
object ReviewBridge {
    var requestInAppReview: (() -> Unit)? = null
}
