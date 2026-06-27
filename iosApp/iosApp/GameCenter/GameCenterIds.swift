import Foundation
import ComposeApp

enum GameCenterIds {
    // Per-game gold-medal achievements. Keyed by Kotlin enum `name` (e.g. "MINI_SUDOKU")
    // so we don't depend on Kotlin/Native's lowerCamelCase conversion of enum cases.
    private static let achievementsByGameName: [String: String] = [
        "MINI_SUDOKU":           "achievement.sudoku_sage",
        "MINI_CHESS":            "achievement.endgame_virtuoso",
        "SOLO_CHESS":            "achievement.last_piece_standing",
        "LIGHTS_OUT":            "achievement.total_blackout",
        "SLIDING_PUZZLE":        "achievement.smooth_operator",
        "SHIKAKU":               "achievement.rectangle_master",
        "NURIKABE":              "achievement.wall_builder",
        "CAT_QUEENS":            "achievement.cat_herder",
        "KNOT":                  "achievement.untangler",
        "PATH_FINDER":           "achievement.trailblazer",
        "ANOMALY_PUZZLE":        "achievement.odd_one_spotted",
        "COLORED_SHAPES":        "achievement.shape_shifter",
        "SHERLOCK_CALCULATION":  "achievement.elementary_my_dear",
        "MENTAL_CALCULATION":    "achievement.human_calculator",
        "CHAIN_CALCULATION":     "achievement.unbroken_chain",
        "FRACTION_CALCULATION":  "achievement.fraction_boss",
        "VALUE_COMPARISON":      "achievement.greater_than_the_rest",
        "GHOST_GRID":            "achievement.ghost_whisperer",
        "VISUAL_MEMORY":         "achievement.photographic_mind",
        "ORBIT_TRACKER":         "achievement.astronomer",
        "PATTERN_SEQUENCE":      "achievement.pattern_prophet",
        "COLOR_CONFUSION":       "achievement.true_colors",
        "FLASH_CROWD":           "achievement.crowd_counter",
        "SCHULTE_TABLE":         "achievement.lightning_gaze",
        "DIGIT_MEMORY":          "achievement.digit_savant",
        "SPOT_THE_NEW":          "achievement.fresh_eyes",
        "WORDLE":                "achievement.wordsmith",
        "FLAGS":                 "achievement.flag_bearer",
    ]

    private static let leaderboardsByGameName: [String: String] = [
        "FLAGS": "leaderboard.flag_master",
    ]

    // Normal Sudoku per-difficulty incremental tiers. Keyed by SudokuDifficulty `name`
    // (e.g. "BEGINNER") for the same reason as the per-game map above.
    private static let sudokuTiersByName: [String: String] = [
        // NB: the Beginner tier was registered on Game Center as "sudoku_beginner"
        // (not "sudoku_sprout"); IDs are immutable, so the code matches the store.
        "BEGINNER": "achievement.sudoku_beginner",
        "EASY":     "achievement.sudoku_scholar",
        "MEDIUM":   "achievement.sudoku_strategist",
        "HARD":     "achievement.sudoku_slayer",
        "EXPERT":   "achievement.sudoku_sovereign",
    ]

    static let achievementMindMarathoner = "achievement.mind_marathoner"
    static let achievementIronStreak     = "achievement.iron_streak"
    static let leaderboardBrainCup       = "leaderboard.brain_cup"

    static let mindMarathonerTarget = 10_000
    static let sudokuTierTarget = 10

    static func achievement(for game: GameType) -> String? {
        achievementsByGameName[game.name]
    }

    static func leaderboard(for game: GameType) -> String? {
        leaderboardsByGameName[game.name]
    }

    static func sudokuTierAchievement(forName name: String) -> String? {
        sudokuTiersByName[name]
    }

    /// Maps a Game Center sudoku-tier ID back to its Kotlin `SudokuDifficulty` (for restore).
    static func sudokuTier(forGameCenterId id: String) -> SudokuDifficulty? {
        switch id {
        case "achievement.sudoku_beginner":   return SudokuDifficulty.beginner
        case "achievement.sudoku_scholar":    return SudokuDifficulty.easy
        case "achievement.sudoku_strategist": return SudokuDifficulty.medium
        case "achievement.sudoku_slayer":     return SudokuDifficulty.hard
        case "achievement.sudoku_sovereign":  return SudokuDifficulty.expert
        default: return nil
        }
    }

    /// Inverse of `achievement(for:)` — maps a Game Center ID back to the Kotlin
    /// `UserStorage.Achievements` enum case used during restore.
    static func userStorageAchievement(forGameCenterId id: String) -> UserStorage.Achievements? {
        switch id {
        case achievementMindMarathoner: return UserStorage.Achievements.totalScore10k
        case achievementIronStreak:     return UserStorage.Achievements.streak30
        case "achievement.sudoku_sage":           return UserStorage.Achievements.goldMiniSudoku
        case "achievement.endgame_virtuoso":      return UserStorage.Achievements.goldMiniChess
        case "achievement.last_piece_standing":   return UserStorage.Achievements.goldSoloChess
        case "achievement.total_blackout":        return UserStorage.Achievements.goldLightsOut
        case "achievement.smooth_operator":       return UserStorage.Achievements.goldSlidingPuzzle
        case "achievement.rectangle_master":      return UserStorage.Achievements.goldShikaku
        case "achievement.wall_builder":          return UserStorage.Achievements.goldNurikabe
        case "achievement.cat_herder":            return UserStorage.Achievements.goldCatQueens
        case "achievement.untangler":             return UserStorage.Achievements.goldKnot
        case "achievement.trailblazer":           return UserStorage.Achievements.goldPathFinder
        case "achievement.odd_one_spotted":       return UserStorage.Achievements.goldAnomalyPuzzle
        case "achievement.shape_shifter":         return UserStorage.Achievements.goldColoredShapes
        case "achievement.elementary_my_dear":    return UserStorage.Achievements.goldSherlockCalculation
        case "achievement.human_calculator":      return UserStorage.Achievements.goldMentalCalculation
        case "achievement.unbroken_chain":        return UserStorage.Achievements.goldChainCalculation
        case "achievement.fraction_boss":         return UserStorage.Achievements.goldFractionCalculation
        case "achievement.greater_than_the_rest": return UserStorage.Achievements.goldValueComparison
        case "achievement.ghost_whisperer":       return UserStorage.Achievements.goldGhostGrid
        case "achievement.photographic_mind":     return UserStorage.Achievements.goldVisualMemory
        case "achievement.astronomer":            return UserStorage.Achievements.goldOrbitTracker
        case "achievement.pattern_prophet":       return UserStorage.Achievements.goldPatternSequence
        case "achievement.true_colors":           return UserStorage.Achievements.goldColorConfusion
        case "achievement.crowd_counter":         return UserStorage.Achievements.goldFlashCrowd
        case "achievement.lightning_gaze":        return UserStorage.Achievements.goldSchulteTable
        case "achievement.digit_savant":          return UserStorage.Achievements.goldDigitMemory
        case "achievement.fresh_eyes":            return UserStorage.Achievements.goldSpotTheNew
        case "achievement.wordsmith":             return UserStorage.Achievements.goldWordle
        case "achievement.flag_bearer":           return UserStorage.Achievements.goldFlags
        default: return nil
        }
    }
}
