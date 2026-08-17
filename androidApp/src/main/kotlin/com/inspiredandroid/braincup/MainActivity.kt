package com.inspiredandroid.braincup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.inspiredandroid.braincup.api.ReviewBridge
import com.inspiredandroid.braincup.licenses.AttributionRegistry

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

        ReviewBridge.requestInAppReview = { requestInAppReview(this) }
        initPlayGames(this)
        storeAttributions?.let(AttributionRegistry::register)

        setContent {
            AndroidApp(
                useBuiltInSponsors = useBuiltInSponsors,
            )
        }
    }
}
