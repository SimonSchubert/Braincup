package com.inspiredandroid.braincup

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory

fun requestInAppReview(activity: Activity) {
    val manager = ReviewManagerFactory.create(activity)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            manager.launchReviewFlow(activity, reviewInfo)
        }
    }
}
