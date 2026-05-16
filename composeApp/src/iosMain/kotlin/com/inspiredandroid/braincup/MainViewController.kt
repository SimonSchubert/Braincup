package com.inspiredandroid.braincup

import androidx.compose.ui.window.ComposeUIViewController
import com.inspiredandroid.braincup.api.ReviewBridge
import com.inspiredandroid.braincup.api.UserStorage

fun MainViewController(requestReview: () -> Unit = {}) = ComposeUIViewController {
    ReviewBridge.requestInAppReview = requestReview
    App()
}

/** Swift-callable factory for [UserStorage]; K/N does not expose the no-arg default constructor. */
fun createUserStorage(): UserStorage = UserStorage()
