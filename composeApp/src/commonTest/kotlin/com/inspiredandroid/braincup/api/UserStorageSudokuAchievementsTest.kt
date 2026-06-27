package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.normalsudoku.NormalSudokuPuzzles
import com.inspiredandroid.braincup.normalsudoku.SudokuDifficulty
import com.russhwolf.settings.MapSettings
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Covers the incremental Normal Sudoku tier achievements: progress is reported per solve,
 * the achievement unlocks at the tier target, and reinstall restore seeds the local
 * completed set from the store's step count.
 */
class UserStorageSudokuAchievementsTest {

    private fun tierIds(difficulty: SudokuDifficulty): List<String> = NormalSudokuPuzzles.byDifficulty(difficulty).map { it.id }

    private fun solvedInTier(storage: UserStorage, difficulty: SudokuDifficulty): Int {
        val ids = tierIds(difficulty).toSet()
        return storage.getCompletedNormalSudokuIds().count { it in ids }
    }

    @BeforeTest
    fun clearBridge() {
        PlayGamesBridge.onSudokuTierProgress = null
    }

    @AfterTest
    fun resetBridge() {
        PlayGamesBridge.onSudokuTierProgress = null
    }

    @Test
    fun reportsRisingProgressAndUnlocksAtTarget() {
        val storage = UserStorage(MapSettings())
        val reported = mutableListOf<Pair<SudokuDifficulty, Int>>()
        PlayGamesBridge.onSudokuTierProgress = { difficulty, count -> reported += difficulty to count }

        val ids = tierIds(SudokuDifficulty.BEGINNER)
        assertEquals(UserStorage.SUDOKU_TIER_TARGET, ids.size)

        ids.forEachIndexed { index, id ->
            storage.markNormalSudokuCompleted(id, SudokuDifficulty.BEGINNER)
            assertEquals(SudokuDifficulty.BEGINNER to (index + 1), reported.last())
        }

        // Only reaches the unlocked list once the whole tier is solved.
        assertTrue(storage.getUnlockedAchievements().contains(UserStorage.Achievements.SUDOKU_BEGINNER))
    }

    @Test
    fun doesNotUnlockBeforeTarget() {
        val storage = UserStorage(MapSettings())
        tierIds(SudokuDifficulty.EASY).take(UserStorage.SUDOKU_TIER_TARGET - 1).forEach {
            storage.markNormalSudokuCompleted(it, SudokuDifficulty.EASY)
        }
        assertFalse(storage.getUnlockedAchievements().contains(UserStorage.Achievements.SUDOKU_EASY))
    }

    @Test
    fun reMarkingSamePuzzleDoesNotDoubleCount() {
        val storage = UserStorage(MapSettings())
        val id = tierIds(SudokuDifficulty.MEDIUM).first()
        storage.markNormalSudokuCompleted(id, SudokuDifficulty.MEDIUM)
        storage.markNormalSudokuCompleted(id, SudokuDifficulty.MEDIUM)
        assertEquals(1, solvedInTier(storage, SudokuDifficulty.MEDIUM))
    }

    @Test
    fun restoreSeedsCompletedSetAndIsIdempotent() {
        val storage = UserStorage(MapSettings())

        assertTrue(storage.restoreSudokuTierProgressIfHigher(SudokuDifficulty.EXPERT, 4))
        assertEquals(4, solvedInTier(storage, SudokuDifficulty.EXPERT))
        // First 4 puzzles of the tier are the ones marked.
        assertEquals(
            tierIds(SudokuDifficulty.EXPERT).take(4).toSet(),
            storage.getCompletedNormalSudokuIds(),
        )
        // Partial restore must not unlock the achievement.
        assertFalse(storage.getUnlockedAchievements().contains(UserStorage.Achievements.SUDOKU_EXPERT))

        // Equal or lower remote counts are no-ops.
        assertFalse(storage.restoreSudokuTierProgressIfHigher(SudokuDifficulty.EXPERT, 4))
        assertFalse(storage.restoreSudokuTierProgressIfHigher(SudokuDifficulty.EXPERT, 3))
        assertEquals(4, solvedInTier(storage, SudokuDifficulty.EXPERT))
    }

    @Test
    fun restoreToFullTierUnlocksAchievement() {
        val storage = UserStorage(MapSettings())
        assertTrue(storage.restoreSudokuTierProgressIfHigher(SudokuDifficulty.HARD, UserStorage.SUDOKU_TIER_TARGET))
        assertTrue(storage.getUnlockedAchievements().contains(UserStorage.Achievements.SUDOKU_HARD))
    }
}
