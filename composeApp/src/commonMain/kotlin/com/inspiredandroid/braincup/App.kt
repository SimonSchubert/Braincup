package com.inspiredandroid.braincup

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_quit_game
import braincup.composeapp.generated.resources.button_stay
import braincup.composeapp.generated.resources.iq_test_quit_message
import braincup.composeapp.generated.resources.iq_test_quit_title
import com.inspiredandroid.braincup.api.PlayGamesBridge
import com.inspiredandroid.braincup.api.ReviewBridge
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.audio.SimonPadSounds
import com.inspiredandroid.braincup.audio.rememberAudioPlayer
import com.inspiredandroid.braincup.games.getGameTypeById
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.haptic.rememberHapticSuccess
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.locale.AppLocale
import com.inspiredandroid.braincup.locale.LocalAppLanguage
import com.inspiredandroid.braincup.locale.appLanguageForTag
import com.inspiredandroid.braincup.locale.isRightToLeftLanguage
import com.inspiredandroid.braincup.navigation.AppNavHost
import com.inspiredandroid.braincup.navigation.ExternalRouteRequests
import com.inspiredandroid.braincup.normalchess.NormalChessDifficulty
import com.inspiredandroid.braincup.normalchess.NormalChessMode
import com.inspiredandroid.braincup.ui.components.LocalNumberPadAscending
import com.inspiredandroid.braincup.ui.components.PrismDialog
import com.inspiredandroid.braincup.ui.components.QuitGameDialog
import com.inspiredandroid.braincup.ui.screens.*
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestIntroScreen
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestPlayScreen
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestResultScreen
import com.inspiredandroid.braincup.ui.screens.iqtest.IqTestReviewScreen
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.LocalAccessiblePalette
import com.inspiredandroid.braincup.ui.theme.OledColorScheme
import com.inspiredandroid.braincup.ui.theme.ThemeMode
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.intl.Locale as ComposeLocale

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(
    systemColorSchemeProvider: ((dark: Boolean) -> ColorScheme)? = null,
    systemBarAppearance: @Composable (darkTheme: Boolean) -> Unit = {},
    useBuiltInSponsors: Boolean = false,
    externalRoutes: ExternalRouteRequests = ExternalRouteRequests.None,
    onNavHostReady: suspend (NavController) -> Unit = {},
) {
    val navController = rememberNavController()
    val controller = remember(navController) { GameController(navController) }
    DisposableEffect(controller) {
        onDispose { controller.dispose() }
    }
    val iqTestController = remember(controller) { IqTestController(navController, controller.storage) }
    DisposableEffect(iqTestController) {
        onDispose { iqTestController.dispose() }
    }
    val audioPlayer = rememberAudioPlayer()
    // Separate player so pad one-shots never stop ambient/game music.
    val soundEffectPlayer = rememberAudioPlayer()

    var isMuted by remember { mutableStateOf(controller.storage.isAudioMuted()) }
    var colorblindPaletteEnabled by remember {
        mutableStateOf(controller.storage.isColorblindPaletteEnabled())
    }
    var hapticEnabled by remember { mutableStateOf(controller.storage.isHapticEnabled()) }
    var numberPadAscending by remember {
        mutableStateOf(controller.storage.isNumberPadAscending())
    }
    var themeMode by remember { mutableStateOf(controller.storage.getThemeMode()) }
    var appLanguage by remember(controller) {
        // Applied while the state is created rather than from an effect: the language has to be in
        // place before any child reads a string resource, and effects only run once the first
        // composition is done. Later changes apply from the picker's callback instead.
        val stored = controller.storage.getAppLanguageTag()
        AppLocale.apply(stored)
        mutableStateOf(stored)
    }

    val systemDark = isSystemInDarkTheme()
    val resolvedColorScheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemColorSchemeProvider?.invoke(systemDark)
            ?: if (systemDark) DarkColorScheme else LightColorScheme
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.OLED -> OledColorScheme
    }

    // Drive the system bar icon brightness from the resolved theme rather than the OS setting, so an
    // explicit Light/Dark/OLED choice that differs from the device's dark-mode state still gets
    // legible status/navigation bar icons.
    val isDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.OLED -> true
    }

    var menuAudio by remember { mutableStateOf<ByteArray?>(null) }
    var gameAudio by remember { mutableStateOf<ByteArray?>(null) }
    var simonPadAudio by remember { mutableStateOf<Map<GameColor, ByteArray>>(emptyMap()) }

    LaunchedEffect(Unit) {
        val opens = controller.storage.incrementAndGetTotalAppOpens()
        if (opens % 5 == 0) {
            ReviewBridge.requestInAppReview?.invoke()
        }
    }

    // Audio is only fetched once it can actually be heard. On web these are plain WAV files served
    // uncompressed, so a muted visitor would otherwise pay 1.1 MB for ambient audio on every load
    // and another 3.5 MB the moment they start a game.
    LaunchedEffect(isMuted) {
        if (isMuted) return@LaunchedEffect
        if (menuAudio == null) {
            try {
                menuAudio = Res.readBytes("files/menu_ambient.wav")
            } catch (_: Exception) {
            }
        }
        // Small one-shots (~18 KB each); load with menu audio so the first Simon flash is never silent.
        if (simonPadAudio.isEmpty()) {
            val loaded = mutableMapOf<GameColor, ByteArray>()
            for ((color, path) in SimonPadSounds.paths) {
                try {
                    loaded[color] = Res.readBytes(path)
                } catch (_: Exception) {
                }
            }
            simonPadAudio = loaded
        }
    }

    val currentEntry by navController.currentBackStackEntryAsState()
    val isPlayingGame = currentEntry?.destination?.hasRoute<Playing>() == true

    LaunchedEffect(isPlayingGame, isMuted) {
        if (isPlayingGame && !isMuted && gameAudio == null) {
            try {
                gameAudio = Res.readBytes("files/game_focus.wav")
            } catch (_: Exception) {
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val onRefreshStoreProfile = PlayGamesBridge.onRefreshStoreProfile
    DisposableEffect(lifecycleOwner, onRefreshStoreProfile) {
        if (onRefreshStoreProfile == null) {
            return@DisposableEffect onDispose { }
        }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                PlayGamesBridge.onRefreshStoreProfile?.invoke()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            onRefreshStoreProfile()
        }
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    DisposableEffect(lifecycleOwner, audioPlayer, isMuted) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> if (!isMuted) audioPlayer.pause()
                Lifecycle.Event.ON_RESUME -> if (!isMuted) audioPlayer.resume()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(isPlayingGame, isMuted, menuAudio, gameAudio) {
        if (isMuted) {
            audioPlayer.stop()
            soundEffectPlayer.stop()
            return@LaunchedEffect
        }
        val data = if (isPlayingGame) gameAudio else menuAudio
        if (data != null) {
            audioPlayer.play(data, loop = true)
        }
    }

    val latestMuted by rememberUpdatedState(isMuted)
    val latestSimonPadAudio by rememberUpdatedState(simonPadAudio)
    LaunchedEffect(controller, soundEffectPlayer) {
        controller.simonPadSoundEvents.collect { color ->
            if (latestMuted) return@collect
            val data = latestSimonPadAudio[color] ?: return@collect
            soundEffectPlayer.play(data, loop = false)
        }
    }

    // Forces the whole subtree to recompose against the new locale; see LocalAppLanguage for why
    // this is a static CompositionLocal and not key(). Nothing below reads it.
    CompositionLocalProvider(LocalAppLanguage provides appLanguage) {
        BraincupTheme(colorScheme = resolvedColorScheme) {
            systemBarAppearance(isDarkTheme)
            Surface(modifier = Modifier.fillMaxSize()) {
                CompositionLocalProvider(
                    LocalAccessiblePalette provides colorblindPaletteEnabled,
                    LocalNumberPadAscending provides numberPadAscending,
                    // Compose takes the layout direction from the platform configuration, which an
                    // in-app language change does not touch, so picking German on an Arabic device
                    // would otherwise keep a mirrored layout. Derive it from the language the app
                    // is actually rendering in.
                    LocalLayoutDirection provides if (isRightToLeftLanguage(ComposeLocale.current.language)) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
                ) {
                    AppNavHost(navController = navController, startDestination = MainMenu) {
                        composable<MainMenu> {
                            val onOpenSettings = remember(controller) { { controller.navigateToSettings() } }
                            val onIqTest = remember(iqTestController) { { iqTestController.navigateToIntro() } }
                            MainMenuScreen(
                                controller = controller,
                                onOpenSettings = onOpenSettings,
                                onIqTest = onIqTest,
                                useBuiltInSponsors = useBuiltInSponsors,
                            )
                        }

                        composable<Settings> {
                            val onBackSettings = remember(controller) { { controller.navigateToMainMenu() } }
                            val accountSnapshot by controller.storage.accounts.snapshot.collectAsStateWithLifecycle()
                            val storeProfile by PlayGamesBridge.currentPlayer.collectAsStateWithLifecycle()
                            SettingsScreen(
                                isMuted = isMuted,
                                onToggleMute = {
                                    isMuted = !isMuted
                                    controller.storage.setAudioMuted(isMuted)
                                },
                                isColorblindPaletteEnabled = colorblindPaletteEnabled,
                                onToggleColorblindPalette = {
                                    colorblindPaletteEnabled = !colorblindPaletteEnabled
                                    controller.storage.setColorblindPaletteEnabled(colorblindPaletteEnabled)
                                },
                                isHapticEnabled = hapticEnabled,
                                onToggleHaptic = {
                                    hapticEnabled = !hapticEnabled
                                    controller.storage.setHapticEnabled(hapticEnabled)
                                },
                                isNumberPadAscending = numberPadAscending,
                                onToggleNumberPadAscending = {
                                    numberPadAscending = !numberPadAscending
                                    controller.storage.setNumberPadAscending(numberPadAscending)
                                },
                                themeMode = themeMode,
                                onThemeSelected = { mode ->
                                    themeMode = mode
                                    controller.storage.setThemeMode(mode)
                                },
                                onBack = onBackSettings,
                                activeAccount = accountSnapshot.accounts.firstOrNull {
                                    it.id == accountSnapshot.activeId
                                },
                                storeProfile = storeProfile,
                                onOpenAccounts = { controller.navigateToAccounts() },
                                onOpenLanguage = { controller.navigateToLanguage() },
                                languageName = appLanguageForTag(appLanguage)?.nativeName,
                                onOpenLicenses = { controller.navigateToLicenses() },
                            )
                        }

                        composable<Licenses> {
                            val onBackLicenses = remember(navController, controller) {
                                {
                                    if (!navController.popBackStack()) {
                                        controller.navigateToSettings()
                                    }
                                }
                            }
                            LicensesScreen(onBack = onBackLicenses)
                        }

                        composable<Language> {
                            val onBackLanguage = remember(navController, controller) {
                                {
                                    if (!navController.popBackStack()) {
                                        controller.navigateToSettings()
                                    }
                                }
                            }
                            LanguageScreen(
                                selectedTag = appLanguage,
                                onSelect = { tag ->
                                    // Before the state flip, so the recomposition it triggers
                                    // reads the new locale rather than the old one.
                                    AppLocale.apply(tag)
                                    appLanguage = tag
                                    controller.storage.setAppLanguageTag(tag)
                                },
                                onBack = onBackLanguage,
                            )
                        }

                        composable<Accounts> {
                            val accountSnapshot by controller.storage.accounts.snapshot.collectAsStateWithLifecycle()
                            val storeProfile by PlayGamesBridge.currentPlayer.collectAsStateWithLifecycle()
                            AccountsScreen(
                                accounts = accountSnapshot.accounts,
                                activeId = accountSnapshot.activeId,
                                storeProfile = storeProfile,
                                canCreate = controller.storage.accounts.canCreate(),
                                onSelect = { id ->
                                    if (controller.storage.accounts.switchTo(id)) {
                                        controller.reloadAfterAccountSwitch()
                                    }
                                },
                                onCreate = { name, icon ->
                                    if (controller.storage.accounts.createLocal(name, icon) != null) {
                                        controller.reloadAfterAccountSwitch()
                                    }
                                },
                                onEdit = { id, name, icon ->
                                    controller.storage.accounts.updateLocal(id, name, icon)
                                },
                                onDelete = { id ->
                                    if (controller.storage.accounts.deleteLocal(id)) {
                                        controller.reloadAfterAccountSwitch()
                                    }
                                },
                                onBack = {
                                    if (!navController.popBackStack()) {
                                        controller.navigateToSettings()
                                    }
                                },
                            )
                        }

                        composable<Instructions> { backStackEntry ->
                            val route: Instructions = backStackEntry.toRoute()
                            val gameType = getGameTypeById(route.gameTypeId)
                            if (gameType != null) {
                                val onStart = remember(controller, gameType) { { controller.startGame(gameType) } }
                                val onBackInstructions = remember(controller) { { controller.navigateToMainMenu() } }
                                val onShowLeaderboard = remember(controller, gameType) {
                                    if (
                                        gameType.hasLeaderboard &&
                                        PlayGamesBridge.onShowLeaderboard != null
                                    ) {
                                        { controller.showLeaderboard(gameType) }
                                    } else {
                                        null
                                    }
                                }
                                InstructionsScreen(
                                    gameType = gameType,
                                    storage = controller.storage,
                                    onStart = onStart,
                                    onBack = onBackInstructions,
                                    onShowLeaderboard = onShowLeaderboard,
                                )
                            }
                        }

                        composable<Playing> {
                            // Intentionally do NOT collect timeRemaining/elapsedTime here: the progress
                            // bar collects those flows so a 100ms tick cannot restart game content.
                            val gameState by controller.gameState.collectAsStateWithLifecycle()
                            val gameUiState by controller.gameUiState.collectAsStateWithLifecycle()
                            val hapticSuccess = rememberHapticSuccess()
                            val onAnswer = remember(controller) { { answer: String -> controller.submitAnswer(answer) } }
                            val onGiveUp = remember(controller) { { controller.giveUp() } }
                            val onWordleFinished = remember(controller) { { controller.wordleFinishedAction() } }
                            val onBullsAndCowsFinished = remember(controller) {
                                { controller.bullsAndCowsFinishedAction() }
                            }
                            val navigateHome = remember(controller) { { controller.navigateToMainMenu() } }

                            LaunchedEffect(controller, hapticEnabled) {
                                controller.intermediateCorrectEvents.collect {
                                    if (hapticEnabled) hapticSuccess()
                                }
                            }

                            when (val state = gameState) {
                                is GameState.Active -> {
                                    val uiState = gameUiState ?: return@composable
                                    var showQuitDialog by remember { mutableStateOf(false) }
                                    val confirmBeforeQuit = shouldConfirmQuit(uiState)
                                    val onBackFromGame = remember(confirmBeforeQuit, navigateHome) {
                                        {
                                            if (confirmBeforeQuit) {
                                                showQuitDialog = true
                                            } else {
                                                navigateHome()
                                            }
                                        }
                                    }

                                    val backState = rememberNavigationEventState(NavigationEventInfo.None)
                                    NavigationBackHandler(
                                        state = backState,
                                        onBackCompleted = {
                                            if (showQuitDialog) {
                                                showQuitDialog = false
                                            } else {
                                                onBackFromGame()
                                            }
                                        },
                                    )

                                    LaunchedEffect(showQuitDialog) {
                                        if (showQuitDialog) {
                                            controller.pauseTimers()
                                        } else {
                                            controller.resumeTimers()
                                        }
                                    }

                                    GameScreen(
                                        gameUiState = uiState,
                                        timeRemaining = controller.timeRemaining,
                                        elapsedTime = controller.elapsedTime,
                                        onAnswer = onAnswer,
                                        onGiveUp = onGiveUp,
                                        onBack = onBackFromGame,
                                        inSessionMode = controller.isInSessionMode,
                                        isTimerPaused = showQuitDialog,
                                        onWordleFinishedAction = onWordleFinished,
                                        onBullsAndCowsFinishedAction = onBullsAndCowsFinished,
                                        orbitBallPositions = controller.orbitBallPositions,
                                        bubbleSumFrames = controller.bubbleSumFrames,
                                        onBubbleSumArenaSize = controller::setBubbleSumArenaSize,
                                    )

                                    if (showQuitDialog) {
                                        QuitGameDialog(
                                            onDismiss = { showQuitDialog = false },
                                            onQuit = {
                                                showQuitDialog = false
                                                navigateHome()
                                            },
                                        )
                                    }
                                }

                                is GameState.Feedback -> {
                                    LaunchedEffect(state) {
                                        if (state.isCorrect && hapticEnabled) hapticSuccess()
                                    }
                                    AnswerFeedbackScreen(
                                        isCorrect = state.isCorrect,
                                        message = state.message,
                                    )
                                }

                                is GameState.Idle -> {
                                    // Game not active, navigating away
                                }
                            }
                        }

                        composable<Finish> { backStackEntry ->
                            val route: Finish = backStackEntry.toRoute()
                            val gameType = getGameTypeById(route.gameTypeId)
                            if (gameType != null) {
                                val onPlayRandom = remember(controller) { { controller.playRandomGame() } }
                                val onPlayAgain = remember(controller, gameType) { { controller.playAgain(gameType) } }
                                val onMenu = remember(controller) { { controller.navigateToMainMenu() } }
                                FinishScreen(
                                    gameType = gameType,
                                    score = route.score,
                                    isNewHighscore = route.isNewHighscore,
                                    answeredAllCorrect = route.answeredAllCorrect,
                                    highscore = route.highscore,
                                    xpGained = route.xpGained,
                                    totalXpAfter = route.totalXpAfter,
                                    adaptiveStartRoundCredit = route.adaptiveStartRoundCredit,
                                    isFinalCatalogLevel = route.isFinalCatalogLevel,
                                    onPlayRandom = onPlayRandom,
                                    onPlayAgain = onPlayAgain,
                                    onMenu = onMenu,
                                )
                            }
                        }

                        composable<Scoreboard> { backStackEntry ->
                            val route: Scoreboard = backStackEntry.toRoute()
                            val gameType = getGameTypeById(route.gameTypeId)
                            if (gameType != null) {
                                val onBackScoreboard = remember(controller) { { controller.navigateToMainMenu() } }
                                ScoreboardScreen(
                                    gameType = gameType,
                                    storage = controller.storage,
                                    onBack = onBackScoreboard,
                                )
                            }
                        }

                        composable<Achievements> {
                            val onBackAchievements = remember(controller) { { controller.navigateToMainMenu() } }
                            AchievementsScreen(
                                storage = controller.storage,
                                onBack = onBackAchievements,
                            )
                        }

                        composable<NormalSudokuMenu> {
                            val onPuzzleSelected = remember(controller) {
                                { id: String -> controller.navigateToNormalSudokuPlay(id) }
                            }
                            val onBackSudokuMenu = remember(controller) { { controller.navigateToMainMenu() } }
                            NormalSudokuMenuScreen(
                                storage = controller.storage,
                                onPuzzleSelected = onPuzzleSelected,
                                onBack = onBackSudokuMenu,
                            )
                        }

                        composable<NormalSudokuPlay> { backStackEntry ->
                            val route: NormalSudokuPlay = backStackEntry.toRoute()
                            val popSudokuMenu = remember(navController) {
                                {
                                    navController.popBackStack(NormalSudokuMenu, inclusive = false)
                                    Unit
                                }
                            }
                            NormalSudokuPlayScreen(
                                puzzleId = route.puzzleId,
                                storage = controller.storage,
                                onCompleted = popSudokuMenu,
                                onBack = popSudokuMenu,
                            )
                        }

                        composable<NormalChessMenu> {
                            val onStartChess = remember(controller) {
                                { mode: NormalChessMode, difficulty: NormalChessDifficulty ->
                                    controller.navigateToNormalChessPlay(mode, difficulty)
                                }
                            }
                            val onBackChessMenu = remember(controller) { { controller.navigateToMainMenu() } }
                            NormalChessMenuScreen(
                                storage = controller.storage,
                                onStart = onStartChess,
                                onBack = onBackChessMenu,
                            )
                        }

                        composable<NormalChessPlay> { backStackEntry ->
                            val route: NormalChessPlay = backStackEntry.toRoute()
                            val mode = NormalChessMode.entries.firstOrNull { it.name == route.mode }
                                ?: NormalChessMode.VS_CPU
                            val difficulty = NormalChessDifficulty.entries.firstOrNull { it.name == route.difficulty }
                                ?: NormalChessDifficulty.MEDIUM
                            val onBackChessPlay = remember(navController) {
                                {
                                    navController.popBackStack(NormalChessMenu, inclusive = false)
                                    Unit
                                }
                            }
                            NormalChessPlayScreen(
                                mode = mode,
                                difficulty = difficulty,
                                storage = controller.storage,
                                onBack = onBackChessPlay,
                            )
                        }

                        composable<MatchstickRiddlesMenu> {
                            val onRiddleSelected = remember(controller) {
                                { id: String -> controller.navigateToMatchstickRiddlesPlay(id) }
                            }
                            val onBackMatchstickMenu = remember(controller) { { controller.navigateToMainMenu() } }
                            MatchstickRiddlesMenuScreen(
                                storage = controller.storage,
                                onRiddleSelected = onRiddleSelected,
                                onBack = onBackMatchstickMenu,
                            )
                        }

                        composable<MatchstickRiddlesPlay> { backStackEntry ->
                            val route: MatchstickRiddlesPlay = backStackEntry.toRoute()
                            val popMatchstickMenu = remember(navController) {
                                {
                                    navController.popBackStack(MatchstickRiddlesMenu, inclusive = false)
                                    Unit
                                }
                            }
                            MatchstickRiddlesPlayScreen(
                                riddleId = route.riddleId,
                                storage = controller.storage,
                                onCompleted = popMatchstickMenu,
                                onBack = popMatchstickMenu,
                            )
                        }

                        composable<PegSolitaire> {
                            val onBackPegSolitaire = remember(controller) { { controller.navigateToMainMenu() } }
                            PegSolitairePlayScreen(
                                storage = controller.storage,
                                onBack = onBackPegSolitaire,
                            )
                        }

                        composable<LearnMenu> {
                            val onTopicSelected = remember(controller) {
                                { topic: MathTopic -> controller.navigateToLearnTopic(topic) }
                            }
                            val onBackLearnMenu = remember(controller) { { controller.navigateToMainMenu() } }
                            LearnMenuScreen(
                                storage = controller.storage,
                                onTopicSelected = onTopicSelected,
                                onBack = onBackLearnMenu,
                            )
                        }

                        composable<LearnTopicDetail> { backStackEntry ->
                            val route: LearnTopicDetail = backStackEntry.toRoute()
                            val topic = MathTopic.byId(route.topicId)
                            if (topic != null) {
                                val onUnitSelected = remember(controller) {
                                    { unit: LearnUnit -> controller.navigateToLearnUnit(unit) }
                                }
                                val onGuide = remember(controller, topic) { { controller.navigateToLearnGuide(topic) } }
                                val onBackTopic = remember(navController) {
                                    {
                                        navController.popBackStack()
                                        Unit
                                    }
                                }
                                LearnTopicScreen(
                                    topic = topic,
                                    storage = controller.storage,
                                    onUnitSelected = onUnitSelected,
                                    onGuide = onGuide,
                                    onBack = onBackTopic,
                                )
                            }
                        }

                        composable<LearnUnitDetail> { backStackEntry ->
                            val route: LearnUnitDetail = backStackEntry.toRoute()
                            val unit = LearnCatalog.unitById(route.unitId)
                            if (unit != null) {
                                val onLessonSelected = remember(controller) {
                                    { lessonId: String -> controller.navigateToLearnLesson(lessonId) }
                                }
                                val onTakeTest = remember(controller, unit) { { controller.navigateToLearnTest(unit) } }
                                val onViewCertificate = remember(controller, unit) {
                                    { controller.navigateToLearnCertificate(unit) }
                                }
                                val onBackUnit = remember(navController) {
                                    {
                                        navController.popBackStack()
                                        Unit
                                    }
                                }
                                LearnUnitScreen(
                                    unit = unit,
                                    storage = controller.storage,
                                    onLessonSelected = onLessonSelected,
                                    onTakeTest = onTakeTest,
                                    onViewCertificate = onViewCertificate,
                                    onBack = onBackUnit,
                                )
                            }
                        }

                        composable<LearnShapeGuide> {
                            val onBackShapeGuide = remember(navController) {
                                {
                                    navController.popBackStack()
                                    Unit
                                }
                            }
                            LearnShapeGuideScreen(onBack = onBackShapeGuide)
                        }

                        composable<LearnRulesGuide> {
                            val onBackRulesGuide = remember(navController) {
                                {
                                    navController.popBackStack()
                                    Unit
                                }
                            }
                            LearnRulesGuideScreen(onBack = onBackRulesGuide)
                        }

                        composable<LearnLessonPlay> { backStackEntry ->
                            val route: LearnLessonPlay = backStackEntry.toRoute()
                            val lesson = LearnCatalog.lessonById(route.lessonId)
                            val unit = lesson?.let { LearnCatalog.unitOfLesson(it) }
                            if (lesson != null && unit != null) {
                                val popUnit = remember(controller, unit) {
                                    { controller.popToLearnUnit(unit) }
                                }
                                val onNextLesson = remember(controller) {
                                    { lessonId: String -> controller.navigateToLearnLesson(lessonId, replaceCurrent = true) }
                                }
                                val onTakeTest = remember(controller, unit) {
                                    { controller.navigateToLearnTest(unit) }
                                }
                                val hapticSuccess = rememberHapticSuccess()
                                val onCorrectAnswer = remember(hapticSuccess, hapticEnabled) {
                                    { if (hapticEnabled) hapticSuccess() }
                                }
                                LearnLessonScreen(
                                    lessonId = lesson.id,
                                    storage = controller.storage,
                                    onNextLesson = onNextLesson,
                                    onTakeTest = onTakeTest,
                                    onCorrectAnswer = onCorrectAnswer,
                                    onBack = popUnit,
                                )
                            }
                        }

                        composable<LearnTest> { backStackEntry ->
                            val route: LearnTest = backStackEntry.toRoute()
                            val unit = LearnCatalog.unitById(route.unitId)
                            if (unit != null) {
                                val popUnit = remember(controller, unit) { { controller.popToLearnUnit(unit) } }
                                val onViewCertificate = remember(controller, unit) {
                                    { controller.navigateToLearnCertificate(unit, fromTest = true) }
                                }
                                val hapticSuccess = rememberHapticSuccess()
                                val onPassed = remember(hapticSuccess, hapticEnabled) {
                                    { if (hapticEnabled) hapticSuccess() }
                                }
                                LearnQuizScreen(
                                    unit = unit,
                                    storage = controller.storage,
                                    onViewCertificate = onViewCertificate,
                                    onPassed = onPassed,
                                    onDone = popUnit,
                                    onBack = popUnit,
                                )
                            }
                        }

                        composable<LearnCertificate> { backStackEntry ->
                            val route: LearnCertificate = backStackEntry.toRoute()
                            val unit = LearnCatalog.unitById(route.unitId)
                            if (unit != null) {
                                val popUnit = remember(controller, unit) { { controller.popToLearnUnit(unit) } }
                                LearnCertificateScreen(
                                    unit = unit,
                                    storage = controller.storage,
                                    onDone = popUnit,
                                    onBack = popUnit,
                                )
                            }
                        }

                        composable<SessionInterstitial> {
                            val session by controller.sessionState.collectAsStateWithLifecycle()
                            val current = session
                            if (current != null) {
                                val nextGameId = current.gameIds.getOrNull(current.currentIndex)
                                val nextGame = nextGameId?.let { getGameTypeById(it) }
                                if (nextGame != null) {
                                    val onContinue = remember(controller) { { controller.playNextSessionGame() } }
                                    val onExit = remember(controller) { { controller.navigateToMainMenu() } }
                                    SessionInterstitialScreen(
                                        nextGame = nextGame,
                                        nextGameIndex = current.currentIndex,
                                        totalGames = current.gameIds.size,
                                        runningTotal = current.gameIds.zip(current.scores).filter { (id, _) ->
                                            getGameTypeById(id)?.lowerScoreIsBetter != true
                                        }.sumOf { (_, score) -> score },
                                        onContinue = onContinue,
                                        onExit = onExit,
                                    )
                                }
                            }
                        }

                        composable<SessionComplete> {
                            val result by controller.lastCompletedSession.collectAsStateWithLifecycle()
                            val current = result
                            if (current != null) {
                                val onDone = remember(controller) { { controller.navigateToMainMenu() } }
                                SessionCompleteScreen(
                                    gameIds = current.gameIds.toImmutableList(),
                                    scores = current.scores.toImmutableList(),
                                    streakBefore = current.streakBefore,
                                    streakAfter = current.streakAfter,
                                    xpGained = current.xpGained,
                                    levelChange = current.levelChange,
                                    onDone = onDone,
                                )
                            }
                        }

                        composable<IqTestIntro> {
                            val history by iqTestController.history.collectAsStateWithLifecycle()
                            val onStart = remember(iqTestController) { { iqTestController.start() } }
                            val onBackIntro = remember(controller) { { controller.navigateToMainMenu() } }
                            IqTestIntroScreen(
                                history = history,
                                onStart = onStart,
                                onBack = onBackIntro,
                            )
                        }

                        composable<IqTestPlay> {
                            val playState by iqTestController.playState.collectAsStateWithLifecycle()
                            val current = playState
                            if (current != null) {
                                var showIqQuitDialog by remember { mutableStateOf(false) }

                                // System back must land on the same prompt as the toolbar arrow. Popping
                                // the route outright would strand the run: the controller keeps counting
                                // down and would later score it and open the result over whatever the
                                // player had moved on to.
                                val iqBackState = rememberNavigationEventState(NavigationEventInfo.None)
                                NavigationBackHandler(
                                    state = iqBackState,
                                    onBackCompleted = { showIqQuitDialog = !showIqQuitDialog },
                                )

                                IqTestPlayScreen(
                                    uiState = current,
                                    timeRemaining = iqTestController.timeRemaining,
                                    onSelect = remember(iqTestController) { { index: Int -> iqTestController.select(index) } },
                                    onPrevious = remember(iqTestController) { { iqTestController.goToPrevious() } },
                                    onNext = remember(iqTestController) { { iqTestController.goToNext() } },
                                    onFinish = remember(iqTestController) { { iqTestController.finish() } },
                                    onRequestQuit = { showIqQuitDialog = true },
                                )

                                if (showIqQuitDialog) {
                                    PrismDialog(
                                        onDismissRequest = { showIqQuitDialog = false },
                                        title = stringResource(Res.string.iq_test_quit_title),
                                        message = stringResource(Res.string.iq_test_quit_message),
                                        primaryLabel = stringResource(Res.string.button_stay),
                                        onPrimary = { showIqQuitDialog = false },
                                        secondaryLabel = stringResource(Res.string.button_quit_game),
                                        onSecondary = {
                                            showIqQuitDialog = false
                                            iqTestController.abandon()
                                        },
                                    )
                                }
                            }
                        }

                        composable<IqTestResult> {
                            val result by iqTestController.resultState.collectAsStateWithLifecycle()
                            val current = result
                            if (current != null) {
                                IqTestResultScreen(
                                    uiState = current,
                                    onReview = remember(iqTestController) { { iqTestController.openReview() } },
                                    onDone = remember(iqTestController) { { iqTestController.leaveResult() } },
                                )
                            }
                        }

                        composable<IqTestReview> {
                            val item by iqTestController.reviewItem.collectAsStateWithLifecycle()
                            val current = item
                            if (current != null) {
                                IqTestReviewScreen(
                                    uiState = current,
                                    onPrevious = remember(iqTestController) { { iqTestController.reviewPrevious() } },
                                    onNext = remember(iqTestController) { { iqTestController.reviewNext() } },
                                    onBack = remember(navController) {
                                        {
                                            navController.popBackStack()
                                            Unit
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }

    val pendingExternalRoute by externalRoutes.pending.collectAsStateWithLifecycle()
    LaunchedEffect(pendingExternalRoute) {
        val pathSuffix = pendingExternalRoute ?: return@LaunchedEffect
        externalRoutes.consume()
        // The IQ test runs on its own controller, whose timer stops nowhere else.
        if (currentEntry?.destination?.hasRoute<IqTestPlay>() == true) {
            iqTestController.abandon()
        }
        controller.openExternalRoute(pathSuffix)
    }
}

/**
 * Games that record the score as soon as they end can leave without a prompt once finished:
 * Wordle when the puzzle is over, MiniChess when the game has an outcome.
 */
private fun shouldConfirmQuit(gameUiState: GameUiState): Boolean = when (gameUiState) {
    is WordleUiState -> !gameUiState.finished
    is BullsAndCowsUiState -> !gameUiState.finished
    is MiniChessUiState -> gameUiState.outcome == null
    else -> true
}
