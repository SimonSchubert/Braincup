package com.inspiredandroid.braincup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.inspiredandroid.braincup.api.UserStorage

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
        )
        super.onCreate(savedInstanceState)
        setContent {
            AndroidApp()
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
