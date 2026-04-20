package com.inspiredandroid.braincup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.inspiredandroid.braincup.api.UserStorage

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidApp(this)
        }

        checkAndRequestReview()
    }

    private fun checkAndRequestReview() {
        val userStorage = UserStorage()
        val appStartCount = userStorage.incrementAndGetTotalAppOpens()

        if (appStartCount % 5 == 0) {
            requestInAppReview(this)
        }
    }
}
