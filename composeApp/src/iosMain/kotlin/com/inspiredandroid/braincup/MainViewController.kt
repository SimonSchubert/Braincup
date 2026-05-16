package com.inspiredandroid.braincup

import androidx.compose.ui.window.ComposeUIViewController
import com.inspiredandroid.braincup.api.ReviewBridge

fun MainViewController(requestReview: () -> Unit = {}) = ComposeUIViewController {
    ReviewBridge.requestInAppReview = requestReview
    App()
}
