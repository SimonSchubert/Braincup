package com.inspiredandroid.braincup

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.R
import com.inspiredandroid.braincup.games.GameCategory
import com.inspiredandroid.braincup.navigation.MAX_RECENT_SHORTCUTS
import com.inspiredandroid.braincup.navigation.launcherShortcuts

const val ACTION_OPEN_ROUTE = "com.inspiredandroid.braincup.action.OPEN_ROUTE"

private const val ROUTE_SCHEME = "braincup"
private const val ROUTE_HOST = "open"

/**
 * The route travels as a URI rather than an extra so a shortcut the player pinned keeps working:
 * it names the destination, not a position in a list that moves.
 */
fun routeIntent(context: Context, pathSuffix: String): Intent = Intent(ACTION_OPEN_ROUTE)
    .setClass(context, MainActivity::class.java)
    .setData(Uri.parse("$ROUTE_SCHEME://$ROUTE_HOST/$pathSuffix"))
    .addFlags(
        // CLEAR_TOP with SINGLE_TOP reuses the running task and delivers onNewIntent instead of
        // stacking a second MainActivity; NEW_TASK alone does not, because this intent does not
        // filterEquals the task's MAIN/LAUNCHER base intent.
        Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_SINGLE_TOP,
    )

fun Intent.routePathSuffix(): String? {
    if (action != ACTION_OPEN_ROUTE) return null
    val uri = data ?: return null
    if (uri.scheme != ROUTE_SCHEME || uri.host != ROUTE_HOST) return null
    return uri.path?.trim('/')?.takeIf { it.isNotEmpty() }
}

suspend fun publishLauncherShortcuts(context: Context) {
    val budget = minOf(
        1 + MAX_RECENT_SHORTCUTS,
        ShortcutManagerCompat.getMaxShortcutCountPerActivity(context),
    )
    val shortcuts = launcherShortcuts(UserStorage()).take(budget).mapIndexed { rank, entry ->
        ShortcutInfoCompat.Builder(context, entry.pathSuffix)
            .setShortLabel(entry.label)
            .setLongLabel(entry.label)
            .setRank(rank)
            .setIcon(IconCompat.createWithResource(context, iconResFor(entry.category)))
            .setActivity(ComponentName(context, MainActivity::class.java))
            .setIntent(routeIntent(context, entry.pathSuffix))
            .build()
    }
    // Throttled for background callers, and unavailable before the user unlocks the device.
    runCatching { ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts) }
}

private fun iconResFor(category: GameCategory?): Int = when (category) {
    null -> R.drawable.ic_shortcut_daily
    GameCategory.MEMORY -> R.drawable.ic_shortcut_memory
    GameCategory.LOGIC -> R.drawable.ic_shortcut_logic
    GameCategory.PERCEPTION -> R.drawable.ic_shortcut_perception
    GameCategory.MATH -> R.drawable.ic_shortcut_math
}
