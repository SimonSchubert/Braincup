package com.inspiredandroid.braincup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.inspiredandroid.braincup.api.ReviewBridge
import com.inspiredandroid.braincup.licenses.AttributionRegistry
import com.inspiredandroid.braincup.navigation.ExternalRouteRequests
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val externalRoutes = ExternalRouteRequests()
    private val shortcutScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

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

        // On a recreation the system re-delivers the launch intent, which would navigate again.
        if (savedInstanceState == null) openRequestedRoute(intent)

        setContent {
            AndroidApp(
                useBuiltInSponsors = useBuiltInSponsors,
                externalRoutes = externalRoutes,
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openRequestedRoute(intent)
    }

    // The launcher only shows the shortcuts once the player has left, so this is the last useful
    // moment to publish them, and it catches a new game, an account switch, a language change and
    // a palette toggle in one place.
    override fun onStop() {
        super.onStop()
        shortcutScope.launch { publishLauncherShortcuts(applicationContext) }
    }

    override fun onDestroy() {
        shortcutScope.cancel()
        super.onDestroy()
    }

    private fun openRequestedRoute(intent: Intent) {
        val pathSuffix = intent.routePathSuffix() ?: return
        ShortcutManagerCompat.reportShortcutUsed(this, pathSuffix)
        externalRoutes.request(pathSuffix)
    }
}
