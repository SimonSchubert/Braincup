package com.inspiredandroid.braincup.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.inspiredandroid.braincup.app.Achievements
import com.inspiredandroid.braincup.app.Finish
import com.inspiredandroid.braincup.app.Instructions
import com.inspiredandroid.braincup.app.MainMenu
import com.inspiredandroid.braincup.app.MatchstickRiddlesMenu
import com.inspiredandroid.braincup.app.MatchstickRiddlesPlay
import com.inspiredandroid.braincup.app.NormalChessMenu
import com.inspiredandroid.braincup.app.NormalChessPlay
import com.inspiredandroid.braincup.app.NormalSudokuMenu
import com.inspiredandroid.braincup.app.NormalSudokuPlay
import com.inspiredandroid.braincup.app.Playing
import com.inspiredandroid.braincup.app.Scoreboard
import com.inspiredandroid.braincup.app.SessionComplete
import com.inspiredandroid.braincup.app.SessionInterstitial
import com.inspiredandroid.braincup.app.Settings
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getGameTypeById

const val GITHUB_PAGES_BASE_PATH = "/Braincup"

fun detectWebBasePath(pathname: String): String =
    if (pathname.startsWith(GITHUB_PAGES_BASE_PATH)) GITHUB_PAGES_BASE_PATH else ""

fun navRouteToPathSuffix(route: Any): String = when (route) {
    is MainMenu -> ""
    is Settings -> "settings"
    is Achievements -> "achievements"
    is SessionInterstitial -> "session"
    is SessionComplete -> "session/complete"
    is NormalSudokuMenu -> "sudoku"
    is NormalSudokuPlay -> "sudoku/${route.puzzleId}"
    is NormalChessMenu -> "chess"
    is NormalChessPlay -> "chess/${route.mode}/${route.difficulty}"
    is MatchstickRiddlesMenu -> "matchstick"
    is MatchstickRiddlesPlay -> "matchstick/${route.riddleId}"
    is Instructions -> gamePathSuffix(route.gameTypeId)
    is Playing -> gamePathSuffix(route.gameTypeId)
    is Finish -> gamePathSuffix(route.gameTypeId)
    is Scoreboard -> gameScoreboardPathSuffix(route.gameTypeId)
    else -> ""
}

fun pathSuffixToNavRoute(suffix: String): Any? {
    if (suffix.isEmpty()) return null
    return when (suffix) {
        "settings" -> Settings
        "achievements" -> Achievements
        "session" -> SessionInterstitial
        "session/complete" -> SessionComplete
        "sudoku" -> NormalSudokuMenu
        "chess" -> NormalChessMenu
        "matchstick" -> MatchstickRiddlesMenu
        else -> parseParameterizedPath(suffix)
    }
}

fun NavBackStackEntry.toUrlPathSuffix(): String {
    val destination = destination
    return when {
        destination.hasRoute<MainMenu>() -> ""
        destination.hasRoute<Settings>() -> "settings"
        destination.hasRoute<Achievements>() -> "achievements"
        destination.hasRoute<SessionInterstitial>() -> "session"
        destination.hasRoute<SessionComplete>() -> "session/complete"
        destination.hasRoute<NormalSudokuMenu>() -> "sudoku"
        destination.hasRoute<NormalSudokuPlay>() -> navRouteToPathSuffix(toRoute<NormalSudokuPlay>())
        destination.hasRoute<NormalChessMenu>() -> "chess"
        destination.hasRoute<NormalChessPlay>() -> navRouteToPathSuffix(toRoute<NormalChessPlay>())
        destination.hasRoute<MatchstickRiddlesMenu>() -> "matchstick"
        destination.hasRoute<MatchstickRiddlesPlay>() -> navRouteToPathSuffix(toRoute<MatchstickRiddlesPlay>())
        destination.hasRoute<Instructions>() -> gamePathSuffix(toRoute<Instructions>().gameTypeId)
        destination.hasRoute<Playing>() -> gamePathSuffix(toRoute<Playing>().gameTypeId)
        destination.hasRoute<Finish>() -> gamePathSuffix(toRoute<Finish>().gameTypeId)
        destination.hasRoute<Scoreboard>() -> gameScoreboardPathSuffix(toRoute<Scoreboard>().gameTypeId)
        else -> ""
    }
}

private fun parseParameterizedPath(suffix: String): Any? {
    if (suffix.startsWith("sudoku/")) {
        val puzzleId = suffix.removePrefix("sudoku/")
        return puzzleId.takeIf { it.isNotEmpty() }?.let { NormalSudokuPlay(it) }
    }
    if (suffix.startsWith("chess/")) {
        val parts = suffix.removePrefix("chess/").split('/')
        if (parts.size == 2 && parts[0].isNotEmpty() && parts[1].isNotEmpty()) {
            return NormalChessPlay(mode = parts[0], difficulty = parts[1])
        }
        return null
    }
    if (suffix.startsWith("matchstick/")) {
        val riddleId = suffix.removePrefix("matchstick/")
        return riddleId.takeIf { it.isNotEmpty() }?.let { MatchstickRiddlesPlay(it) }
    }
    if (suffix.endsWith("/scores")) {
        val slug = suffix.removeSuffix("/scores")
        val gameType = GameType.fromUrlSlug(slug) ?: return null
        return Scoreboard(gameType.id)
    }
    val gameType = GameType.fromUrlSlug(suffix) ?: return null
    return Instructions(gameType.id)
}

private fun gamePathSuffix(gameTypeId: String): String =
    getGameTypeById(gameTypeId)?.urlSlug.orEmpty()

private fun gameScoreboardPathSuffix(gameTypeId: String): String {
    val slug = getGameTypeById(gameTypeId)?.urlSlug ?: return ""
    return "$slug/scores"
}