package com.inspiredandroid.braincup.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.inspiredandroid.braincup.app.Accounts
import com.inspiredandroid.braincup.app.Achievements
import com.inspiredandroid.braincup.app.Finish
import com.inspiredandroid.braincup.app.Instructions
import com.inspiredandroid.braincup.app.IqTestIntro
import com.inspiredandroid.braincup.app.IqTestPlay
import com.inspiredandroid.braincup.app.IqTestResult
import com.inspiredandroid.braincup.app.IqTestReview
import com.inspiredandroid.braincup.app.Language
import com.inspiredandroid.braincup.app.LearnCertificate
import com.inspiredandroid.braincup.app.LearnLessonPlay
import com.inspiredandroid.braincup.app.LearnMenu
import com.inspiredandroid.braincup.app.LearnRulesGuide
import com.inspiredandroid.braincup.app.LearnShapeGuide
import com.inspiredandroid.braincup.app.LearnTest
import com.inspiredandroid.braincup.app.LearnTopicDetail
import com.inspiredandroid.braincup.app.LearnUnitDetail
import com.inspiredandroid.braincup.app.Licenses
import com.inspiredandroid.braincup.app.MainMenu
import com.inspiredandroid.braincup.app.MatchstickRiddlesMenu
import com.inspiredandroid.braincup.app.MatchstickRiddlesPlay
import com.inspiredandroid.braincup.app.NormalChessMenu
import com.inspiredandroid.braincup.app.NormalChessPlay
import com.inspiredandroid.braincup.app.NormalSudokuMenu
import com.inspiredandroid.braincup.app.NormalSudokuPlay
import com.inspiredandroid.braincup.app.PegSolitaire
import com.inspiredandroid.braincup.app.Playing
import com.inspiredandroid.braincup.app.Scoreboard
import com.inspiredandroid.braincup.app.SessionComplete
import com.inspiredandroid.braincup.app.SessionInterstitial
import com.inspiredandroid.braincup.app.Settings
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getGameTypeById
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.MathTopic

const val GITHUB_PAGES_BASE_PATH = "/Braincup"

private const val LEARN_BASE_PATH = "learn"

/** The guides hang off the section root, not off a sub-topic: they belong to none of them. */
private const val LEARN_SHAPES_PATH = "$LEARN_BASE_PATH/shapes"
private const val LEARN_RULES_PATH = "$LEARN_BASE_PATH/rules"

fun detectWebBasePath(pathname: String): String = if (pathname.startsWith(GITHUB_PAGES_BASE_PATH)) GITHUB_PAGES_BASE_PATH else ""

fun navRouteToPathSuffix(route: Any): String = when (route) {
    is MainMenu -> ""
    is Settings -> "settings"
    is Accounts -> "accounts"
    is Licenses -> "licenses"
    is Language -> "language"
    is Achievements -> "achievements"
    is SessionInterstitial -> "session"
    is SessionComplete -> "session/complete"
    is NormalSudokuMenu -> "sudoku"
    is NormalSudokuPlay -> "sudoku/${route.puzzleId}"
    is NormalChessMenu -> "chess"
    is NormalChessPlay -> "chess/${route.mode}/${route.difficulty}"
    is MatchstickRiddlesMenu -> "matchstick"
    is MatchstickRiddlesPlay -> "matchstick/${route.riddleId}"
    is PegSolitaire -> "peg-solitaire"
    is IqTestIntro -> "iq-test"
    is IqTestPlay -> "iq-test/play"
    is IqTestResult -> "iq-test/result"
    is IqTestReview -> "iq-test/review"
    is LearnMenu -> LEARN_BASE_PATH
    is LearnShapeGuide -> LEARN_SHAPES_PATH
    is LearnRulesGuide -> LEARN_RULES_PATH
    is LearnTopicDetail -> learnTopicPathSuffix(route.topicId)
    is LearnUnitDetail -> learnUnitPathSuffix(route.unitId)
    is LearnLessonPlay -> "$LEARN_BASE_PATH/lesson/${route.lessonId}"
    is LearnTest -> learnUnitPathSuffix(route.unitId, "test")
    is LearnCertificate -> learnUnitPathSuffix(route.unitId, "certificate")
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
        "accounts" -> Accounts
        "licenses" -> Licenses
        "language" -> Language
        "achievements" -> Achievements
        "session" -> SessionInterstitial
        "session/complete" -> SessionComplete
        "sudoku" -> NormalSudokuMenu
        "chess" -> NormalChessMenu
        "matchstick" -> MatchstickRiddlesMenu
        "peg-solitaire" -> PegSolitaire
        "iq-test" -> IqTestIntro
        "iq-test/play" -> IqTestPlay
        "iq-test/result" -> IqTestResult
        "iq-test/review" -> IqTestReview
        LEARN_BASE_PATH -> LearnMenu
        LEARN_SHAPES_PATH -> LearnShapeGuide
        LEARN_RULES_PATH -> LearnRulesGuide
        else -> parseParameterizedPath(suffix)
    }
}

fun NavBackStackEntry.toUrlPathSuffix(): String {
    val destination = destination
    return when {
        destination.hasRoute<MainMenu>() -> navRouteToPathSuffix(MainMenu)
        destination.hasRoute<Settings>() -> navRouteToPathSuffix(Settings)
        destination.hasRoute<Accounts>() -> navRouteToPathSuffix(Accounts)
        destination.hasRoute<Licenses>() -> navRouteToPathSuffix(Licenses)
        destination.hasRoute<Language>() -> navRouteToPathSuffix(Language)
        destination.hasRoute<Achievements>() -> navRouteToPathSuffix(Achievements)
        destination.hasRoute<SessionInterstitial>() -> navRouteToPathSuffix(SessionInterstitial)
        destination.hasRoute<SessionComplete>() -> navRouteToPathSuffix(SessionComplete)
        destination.hasRoute<NormalSudokuMenu>() -> navRouteToPathSuffix(NormalSudokuMenu)
        destination.hasRoute<NormalSudokuPlay>() -> navRouteToPathSuffix(toRoute<NormalSudokuPlay>())
        destination.hasRoute<NormalChessMenu>() -> navRouteToPathSuffix(NormalChessMenu)
        destination.hasRoute<NormalChessPlay>() -> navRouteToPathSuffix(toRoute<NormalChessPlay>())
        destination.hasRoute<MatchstickRiddlesMenu>() -> navRouteToPathSuffix(MatchstickRiddlesMenu)
        destination.hasRoute<MatchstickRiddlesPlay>() -> navRouteToPathSuffix(toRoute<MatchstickRiddlesPlay>())
        destination.hasRoute<PegSolitaire>() -> navRouteToPathSuffix(PegSolitaire)
        destination.hasRoute<IqTestIntro>() -> navRouteToPathSuffix(IqTestIntro)
        destination.hasRoute<IqTestPlay>() -> navRouteToPathSuffix(IqTestPlay)
        destination.hasRoute<IqTestResult>() -> navRouteToPathSuffix(IqTestResult)
        destination.hasRoute<IqTestReview>() -> navRouteToPathSuffix(IqTestReview)
        destination.hasRoute<LearnMenu>() -> navRouteToPathSuffix(LearnMenu)
        destination.hasRoute<LearnShapeGuide>() -> navRouteToPathSuffix(LearnShapeGuide)
        destination.hasRoute<LearnRulesGuide>() -> navRouteToPathSuffix(LearnRulesGuide)
        destination.hasRoute<LearnTopicDetail>() -> navRouteToPathSuffix(toRoute<LearnTopicDetail>())
        destination.hasRoute<LearnUnitDetail>() -> navRouteToPathSuffix(toRoute<LearnUnitDetail>())
        destination.hasRoute<LearnLessonPlay>() -> navRouteToPathSuffix(toRoute<LearnLessonPlay>())
        destination.hasRoute<LearnTest>() -> navRouteToPathSuffix(toRoute<LearnTest>())
        destination.hasRoute<LearnCertificate>() -> navRouteToPathSuffix(toRoute<LearnCertificate>())
        destination.hasRoute<Instructions>() -> navRouteToPathSuffix(toRoute<Instructions>())
        destination.hasRoute<Playing>() -> navRouteToPathSuffix(toRoute<Playing>())
        destination.hasRoute<Finish>() -> navRouteToPathSuffix(toRoute<Finish>())
        destination.hasRoute<Scoreboard>() -> navRouteToPathSuffix(toRoute<Scoreboard>())
        else -> ""
    }
}

private fun parseParameterizedPath(suffix: String): Any? {
    if (suffix.startsWith("$LEARN_BASE_PATH/")) {
        return parseLearnPath(suffix.removePrefix("$LEARN_BASE_PATH/"))
    }
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

private fun gamePathSuffix(gameTypeId: String): String = getGameTypeById(gameTypeId)?.urlSlug.orEmpty()

private fun gameScoreboardPathSuffix(gameTypeId: String): String {
    val slug = getGameTypeById(gameTypeId)?.urlSlug ?: return ""
    return "$slug/scores"
}

/**
 * Learn paths below the section root: `lesson/<lessonId>` for a lesson, `<topicSlug>` for a topic,
 * and `<topicSlug>/<unitSlug>` optionally followed by `test` or `certificate` for one sub-topic.
 */
private fun parseLearnPath(rest: String): Any? {
    if (rest.startsWith("lesson/")) {
        val lessonId = rest.removePrefix("lesson/")
        return LearnCatalog.lessonById(lessonId)?.let { LearnLessonPlay(it.id) }
    }
    val parts = rest.split('/')
    if (parts.size > 3) return null
    val topic = MathTopic.bySlug(parts.firstOrNull().orEmpty()) ?: return null
    val unitSlug = parts.getOrNull(1) ?: return LearnTopicDetail(topic.id)
    val unit = LearnCatalog.unitBySlug(topic, unitSlug) ?: return null
    return when (parts.getOrNull(2)) {
        null -> LearnUnitDetail(unit.id)
        "test" -> LearnTest(unit.id)
        "certificate" -> LearnCertificate(unit.id)
        else -> null
    }
}

private fun learnTopicPathSuffix(topicId: String): String {
    val slug = MathTopic.byId(topicId)?.urlSlug ?: return LEARN_BASE_PATH
    return "$LEARN_BASE_PATH/$slug"
}

private fun learnUnitPathSuffix(unitId: String, leaf: String? = null): String {
    val unit = LearnCatalog.unitById(unitId) ?: return LEARN_BASE_PATH
    val base = "$LEARN_BASE_PATH/${unit.topic.urlSlug}/${unit.urlSlug}"
    return if (leaf == null) base else "$base/$leaf"
}
