package com.inspiredandroid.braincup.navigation

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.daily_challenge_title
import braincup.composeapp.generated.resources.iq_test_button
import braincup.composeapp.generated.resources.matchstick_riddles_title
import braincup.composeapp.generated.resources.normal_chess_button
import braincup.composeapp.generated.resources.normal_sudoku_title
import braincup.composeapp.generated.resources.peg_solitaire_button
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.IqTestIntro
import com.inspiredandroid.braincup.app.MatchstickRiddlesMenu
import com.inspiredandroid.braincup.app.NormalChessMenu
import com.inspiredandroid.braincup.app.NormalSudokuMenu
import com.inspiredandroid.braincup.app.PegSolitaire
import com.inspiredandroid.braincup.app.SessionInterstitial
import com.inspiredandroid.braincup.games.GameCategory
import com.inspiredandroid.braincup.games.GameType
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

const val MAX_RECENT_SHORTCUTS = 3

/**
 * One launcher entry, resolved down to plain data: the generated `Res` class is internal to this
 * module, so the Android side cannot read a [StringResource] itself.
 */
data class LauncherShortcut(
    val pathSuffix: String,
    val label: String,
    val category: GameCategory?,
)

/** The daily challenge, then the games played most recently. */
suspend fun launcherShortcuts(storage: UserStorage): List<LauncherShortcut> = buildList {
    add(
        LauncherShortcut(
            pathSuffix = navRouteToPathSuffix(SessionInterstitial),
            label = getString(Res.string.daily_challenge_title),
            category = null,
        ),
    )
    val colorblind = storage.isColorblindPaletteEnabled()
    storage.getRecentGames()
        .filterNot { colorblind && GameType.fromUrlSlug(it)?.requiresColorVision == true }
        .take(MAX_RECENT_SHORTCUTS)
        .forEach { pathSuffix ->
            val labelRes = recentGameLabelRes(pathSuffix) ?: return@forEach
            add(
                LauncherShortcut(
                    pathSuffix = pathSuffix,
                    label = getString(labelRes),
                    category = recentGameCategory(pathSuffix),
                ),
            )
        }
}

/** Null for a suffix no longer in the catalog, which drops the entry rather than the whole list. */
private fun recentGameLabelRes(pathSuffix: String): StringResource? = when (pathSuffix) {
    navRouteToPathSuffix(NormalSudokuMenu) -> Res.string.normal_sudoku_title
    navRouteToPathSuffix(NormalChessMenu) -> Res.string.normal_chess_button
    navRouteToPathSuffix(MatchstickRiddlesMenu) -> Res.string.matchstick_riddles_title
    navRouteToPathSuffix(PegSolitaire) -> Res.string.peg_solitaire_button
    navRouteToPathSuffix(IqTestIntro) -> Res.string.iq_test_button
    else -> GameType.fromUrlSlug(pathSuffix)?.displayNameRes
}

// The five games outside GameType are all logic puzzles; the label carries which one it is.
private fun recentGameCategory(pathSuffix: String): GameCategory = GameType.fromUrlSlug(pathSuffix)?.category ?: GameCategory.LOGIC
