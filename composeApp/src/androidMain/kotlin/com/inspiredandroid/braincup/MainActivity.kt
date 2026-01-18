package com.inspiredandroid.braincup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.play.core.review.ReviewManagerFactory
import com.inspiredandroid.braincup.api.UserStorage

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }

        checkAndRequestReview()
    }

    private fun checkAndRequestReview() {
        val userStorage = UserStorage()
        val appStartCount = userStorage.incrementAndGetTotalAppOpens()

        if (appStartCount % 5 == 0) {
            requestInAppReview()
        }
    }

    private fun requestInAppReview() {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(this, reviewInfo)
            }
        }
    }
}
